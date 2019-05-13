package com.example.demo.beam;

import com.example.demo.util.GsonUtil;
import com.example.demo.util.HLDateTimeUtils;
import org.apache.beam.repackaged.beam_sdks_java_core.com.google.common.collect.ImmutableMap;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.io.clickhouse.ClickHouseIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;

import java.sql.Statement;

/**
 * Description：测试Beam同步Kafka数据至CK库
 * Author；JinHuatao
 * Date: 2019/5/10 10:31
 */
public class CKTest {

    public static void main(String[] args) {
        testBeam();
//        System.out.println(System.currentTimeMillis());
//        testConnection();
    }

    public static void testBeam() {
        //使用管道类型工厂，创建管道类型
        PipelineOptions options = PipelineOptionsFactory.create();
        //制定管道运行器类型
        options.setRunner(FlinkRunner.class);
        //配置完管道类型后，使用该管道类型创建一个管道
        Pipeline pipeline = Pipeline.create(options);

        //=========================================
        //step01-从Kafka中消费数据
        PCollection<KafkaRecord<String, String>> lines = pipeline.apply(KafkaIO.<String, String>read()
                .withBootstrapServers("172.30.154.241:9092")
                .withTopic("TopicAlarm3")
                .withKeyDeserializer(StringDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                .updateConsumerProperties(ImmutableMap.<String, Object>of("auto.offset.reset", "latest")));

        //auto.offset.reset设置kafka每次从头开始消费，参考地址：https://blog.csdn.net/lishuangzhe7047/article/details/74530417

        //step02-处理kafka中消费的数据，将其序列化为实例对象集合; 此处需要自定义业务处理逻辑
        PCollection<AlarmTable> alarmTablePCollection = lines.apply("ReverseToEntityObject", ParDo.of(new DoFn<KafkaRecord<String, String>, AlarmTable>() {
            private static final long serialVersionUID = 1L;
            //自定义处理lines集合中每条数据的方法
            @ProcessElement
            public void parseLines(ProcessContext pc) {
                String jsonStr = pc.element().getKV().getValue();
                System.out.println("-----------------1-kafka原始值：" + jsonStr);
                AlarmTable modelTable = null;
                try {
                    modelTable = GsonUtil.getObjectByJsonString(jsonStr, AlarmTable.class);
                    System.out.println("------------------2-json解析后对象值：" + modelTable);
                } catch (Exception e) {
                    System.out.print("解析Kafka中JSON对象异常：" + modelTable);
                    e.printStackTrace();
                }
                //将解析正常的数据返回放入alarmTablePCollection集合中
                if(modelTable != null){
                    pc.output(modelTable);
                }
            }
        }));

        //TODO 为实体类指明序列化编码器，此序列化编码器有问题，当实体类中有DateTime类型时，无法序列化，会报错。
//        alarmTablePCollection.setCoder(AvroCoder.of(AlarmTable.class));

        //创建alarmTable在数据库中的Schema
        final Schema alarmTableSchema = Schema.builder()
                .addStringField("alarmid")
                .addNullableField("alarmTitle", Schema.FieldType.STRING)
                .addNullableField("deviceModel", Schema.FieldType.STRING)
                .addNullableField("alarmSource", Schema.FieldType.INT32)
                .addNullableField("alarmMsg", Schema.FieldType.STRING)
                .addNullableField("alarmTime", Schema.FieldType.DATETIME)
                .addDateTimeField("alarmDate")
                .build();

        //step03-创建一个Model
        PCollection<Row> alarmTableModelPCollection = alarmTablePCollection.setCoder(AvroCoder.of(AlarmTable.class)).apply("AlarmTableModel", ParDo.of(new DoFn<AlarmTable, Row>() {
            private static final long serialVersionUID = 1L;

            // 将ReverseToEntityObject实体对象 转换成 Row
            @ProcessElement
            public void mapEntityToRow(ProcessContext pc){
                AlarmTable at = pc.element();
                System.out.println("---------需要序列化插入CK库数据：" + at);
                //注意：此处在接收到kafka值时才队形row与schema进行映射做类型校验，如果类型不正确，则直接抛出异常
                // Beam版本 2.11.0、 2.12.0中的DateTime必须为org.joda.time.base.AbstractInstant的子类
                DateTime alarmTime = DateTime.now();
                DateTime dateTime = DateTime.now();
                String strTime = at.getAlarmTime();
                if(strTime != null){
                    System.out.println("----------ROW解析方法中接收的alarmTime时间格式" + at.getAlarmTime());
                    alarmTime = HLDateTimeUtils.parseDateTimeDefault(strTime);
                    System.out.println("----------转换alarmTime时间格式完成【1】" + alarmTime);

                    dateTime = HLDateTimeUtils.parseDateDefault(strTime.split(" ")[0]);
                    System.out.println("----------转换dateTimee时间格式完成【2】" + dateTime);
                }

                //TODO 添加DateTime在序列化的地方有问题，解决方法是将DateTime在实体对象中以String类型保存，在ROW中入库时将
                //TODO 其解析为实际时间！！！
                Row row = Row.withSchema(alarmTableSchema)
                        .addValues(at.getAlarmid(), at.getAlarmTitle(), at.getDeviceModel(), at.getAlarmSource(),
                                at.getAlarmMsg(), alarmTime, dateTime).build();
                pc.output(row);
            }
        }));

        String jdbcUrl = "jdbc:clickhouse://172.30.154.241:8123/ciot";
        String tableName = "alarm";

        //指明序列化Schema
        alarmTableModelPCollection.setRowSchema(alarmTableSchema);

        //step04-配置数据出口
        alarmTableModelPCollection.apply(ClickHouseIO.<Row>write(jdbcUrl, tableName)
                .withMaxRetries(2)
                .withMaxInsertBlockSize(5)  //每次插入5条
                .withInitialBackoff(Duration.standardSeconds(5))   //设置过去时间段为5s
                .withInsertDeduplicate(false)      //不允许插入重复数据
                .withInsertDistributedSync(true));  //不需要分布式插入完成

//        ClickHouseConnection connection =
        //=========================================

        //运行管道，直到任务流完成前一直等待
        pipeline.run().waitUntilFinish();

    }


    public static void testConnection(){
        String jdbcUrl = "jdbc:clickhouse://172.30.154.241:8123/alarm";
        try(ClickHouseConnection connection =  new ClickHouseDataSource(jdbcUrl).getConnection();
            Statement statement = connection.createStatement()){
            String sql = "INSERT INTO ct_alarm_info VALUES ('15', 'test2', 'indoor-002', 569, 'test2', '2019-05-10 15:09:11');";
//            statement.executeQuery(sql);
            statement.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
