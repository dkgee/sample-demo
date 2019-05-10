package com.example.demo.beam;

import com.example.demo.util.GsonUtil;
import org.apache.beam.repackaged.beam_sdks_java_core.com.google.common.collect.ImmutableMap;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.RowCoder;
import org.apache.beam.sdk.io.clickhouse.ClickHouseIO;
import org.apache.beam.sdk.io.clickhouse.TableSchema;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.sdk.schemas.SchemaCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/5/9 9:49
 */
public class KafkaToClickhouseIO {

    public static void main(String[] args) {
         PipelineOptions options = PipelineOptionsFactory.create();// 创建管道工厂
         options.setRunner(FlinkRunner.class);  // 显式指定PipelineRunner：FlinkRunner必须指定如果不制定则为本地
         Pipeline pipeline = Pipeline.create(options); // 设置相关管道

        // 这里kV后说明kafka中的key和value均为String类型
         PCollection<KafkaRecord<String, String>> lines = pipeline.apply(KafkaIO.<String,String>read()
                 .withBootstrapServers("172.30.154.241:9092")   // 必需设置kafka的服务器地址和端口
                 .withTopic("TopicAlarm")                    // 必需，设置要读取的kafka的topic名称
                 .withKeyDeserializer(StringDeserializer.class) // 必需序列化key
                 .withValueDeserializer(StringDeserializer.class)    // 必需序列化value
                 .updateConsumerProperties(ImmutableMap.<String, Object>of("auto.offset.reset", "earliest")));// 这个属性kafka最常见的

        // 设置Schema 的的字段名称和类型
        final Schema alarmType = Schema.of(
                Schema.Field.of("alarmid", FieldType.STRING),
                Schema.Field.of("alarmTitle", FieldType.STRING),
                Schema.Field.of("deviceModel", FieldType.STRING),
                Schema.Field.of("alarmSource", FieldType.INT32),
                Schema.Field.of("alarmMsg", FieldType.STRING),
                Schema.Field.of("alarmTime", FieldType.DATETIME));//类型与字符串之间转换关系

        final Schema alarmSchema = Schema.builder()
                .addStringField("alarmid")
                .addStringField("alarmTitle")
                .addStringField("deviceModel")
                .addInt32Field("alarmSource")
                .addStringField("alarmMsg")
                .addDateTimeField("alarmTime")
                .build();

        // 从kafka中读出的数据转换成AlarmTable实体对象
        PCollection<AlarmTable> kafkadata = lines.apply("Remove Kafka Metadata", ParDo.of(new DoFn<KafkaRecord<String, String>, AlarmTable>() {
                    private static final long serialVersionUID = 1L;
                    @ProcessElement
                    public void processElement(ProcessContext ctx) {
//                        Gson gon = new Gson();
                        AlarmTable modelTable = null;
                        try {
                            //进行序列号代码
                            //获取jsonstr中各个字段
                            modelTable = GsonUtil.getObjectByJsonString(ctx.element().getKV().getValue(), AlarmTable.class);
                        } catch (Exception e) {
                            System.out.print("json序列化出现问题：");
                            e.printStackTrace();
                        }
                        if(modelTable != null){
                            try{
                                ctx.output(modelTable); // 回传实体
                            }catch (Exception e){
                                System.out.println("==========回传实体异常");
                                e.printStackTrace();
                            }
                        }else {
                            System.out.println("异常消息........");
                        }

                }
        }));

        // 备份写入 Elasticsearch
        /*String[] addresses = { "http://172.30.154.245:9200/" };
        PCollection<String> jsonCollection = kafkadata.setCoder(AvroCoder.of(AlarmTable.class))
                .apply("covert json", ParDo.of(new DoFn<AlarmTable, String>() {
                    private static final long serialVersionUID = 1L;
                    @ProcessElement
                    public void processElement(ProcessContext ctx) {
                         Gson gon = new Gson();
                         String jString = "";
                         try {
                             jString = gon.toJson(ctx.element()); // 进行序列号代码
                             System.out.print("序列化后的数据：" + jString);
                         } catch (Exception e) {
                             System.out.print("json序列化出现问题：" + e);
                         }
                         ctx.output(jString);   // 回传实体
                    }
        }));*/


        // 所有的Beam 数据写入ES的数据统一转换成json 才可以正常插入
        //jsonCollection.apply(ElasticsearchIO.write().withConnectionConfiguration(ElasticsearchIO.ConnectionConfiguration.create(addresses, "alarm", "TopicAlarm")));

        // 如果上面设置下面就不用设置
//        PCollection<Row> modelPCollection = kafkadata.setCoder(AvroCoder.of(AlarmTable.class)).apply(ParDo.of(new DoFn<AlarmTable, Row>() {
        PCollection<Row> modelPCollection = kafkadata.setCoder(AvroCoder.of(AlarmTable.class)).apply(ParDo.of(new DoFn<AlarmTable, Row>() {
                        // 实体转换成Row
                         private static final long serialVersionUID = 1L;
                         @ProcessElement
                         public void processElement(ProcessContext c) {
                             AlarmTable modelTable = c.element();

                             //TableSchema.ColumnType.DATETIME = TableSchema.ColumnType.parse("DateTime");
                             Row alarmRow = Row.withSchema(alarmSchema)
                                      .addValues(modelTable.getAlarmid(),
                                              modelTable.getAlarmTitle(),
                                              modelTable.getDeviceModel(),
                                              modelTable.getAlarmSource(),
                                              modelTable.getAlarmMsg(),
                                              modelTable.getAlarmTime()).build();
                              //实体赋值Row类型
                             c.output(alarmRow);
                         }
          }));

        try{
            // 写入ClickHouse
            modelPCollection.setRowSchema(alarmSchema).apply(ClickHouseIO.<Row>write("jdbc:clickhouse://172.30.154.241:8123/alarm", "ct_alarm_info3")
                    .withMaxRetries(3) // 重试次数
                    .withMaxInsertBlockSize(5) // 添加最大块的大小
                    .withInitialBackoff(Duration.standardSeconds(5)) //每5秒一个窗口
                    .withInsertDeduplicate(false) // 重复数据是否删除
                    .withInsertDistributedSync(false));

            pipeline.run().waitUntilFinish();
        }catch (Exception e){
            System.out.println("Clickhouse数据入库异常");
            e.printStackTrace();
        }

    }
}
