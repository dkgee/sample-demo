package com.example.demo.beam;

import com.example.demo.util.GsonUtil;
import org.apache.beam.repackaged.beam_sdks_java_core.com.google.common.collect.ImmutableMap;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Distinct;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.gson.Gson;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/5/13 12:22
 */
public class ESTest {

    public static void main(String[] args) {
        testBeam3();
//        String val = "29.05.2019 00:25:11";
//        System.out.println(getFmtDate(val));
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

        // 备份写入 Elasticsearch
        String[] addresses = { "http://172.30.154.244:9200/" };
        PCollection<String> jsonCollection = alarmTablePCollection.setCoder(AvroCoder.of(AlarmTable.class))
                .apply("CovertJson", ParDo.of(new DoFn<AlarmTable, String>() {
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
        }));

        // 所有的Beam 数据写入ES的数据统一转换成json 才可以正常插入
        jsonCollection.apply(ElasticsearchIO.write()
                .withConnectionConfiguration(ElasticsearchIO.ConnectionConfiguration.create(addresses, "alarm", "TopicAlarm")));

        //运行管道，直到任务流完成前一直等待
        pipeline.run().waitUntilFinish();

    }

    public static void testBeam2() {
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
                .withTopic("qmgz2019")
                .withKeyDeserializer(StringDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                .updateConsumerProperties(ImmutableMap.<String, Object>of("auto.offset.reset", "latest")));

        //auto.offset.reset设置kafka每次从头开始消费，参考地址：https://blog.csdn.net/lishuangzhe7047/article/details/74530417

        //step02-处理kafka中消费的数据，将其序列化为实例对象集合; 此处需要自定义业务处理逻辑
        PCollection<Qmgz> qmgzTablePCollection = lines.apply("ReverseToEntityObject", ParDo.of(new DoFn<KafkaRecord<String, String>, Qmgz>() {
            private static final long serialVersionUID = 1L;
            //自定义处理lines集合中每条数据的方法
            @ProcessElement
            public void parseLines(ProcessContext pc) {
                String jsonStr = pc.element().getKV().getValue();
                System.out.println("-----------------1-kafka原始值：" + jsonStr);
                Qmgz qmgz = null;
                try {
                    qmgz = GsonUtil.getObjectByJsonString(jsonStr, Qmgz.class);
                    System.out.println("------------------2-json解析后对象值：" + qmgz);
                } catch (Exception e) {
                    System.out.print("解析Kafka中JSON对象异常：" + qmgz);
                    e.printStackTrace();
                }
                //将解析正常的数据返回放入alarmTablePCollection集合中
                if(qmgz != null){
                    pc.output(qmgz);
                }
            }
        }));

        // 备份写入 Elasticsearch
        String[] addresses = { "http://172.30.154.244:9200/" };
        PCollection<String> jsonCollection = qmgzTablePCollection.setCoder(AvroCoder.of(Qmgz.class))
                .apply("CovertJson", ParDo.of(new DoFn<Qmgz, String>() {
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
                }));

        // 所有的Beam 数据写入ES的数据统一转换成json 才可以正常插入
        jsonCollection.apply(ElasticsearchIO.write()
                .withConnectionConfiguration(ElasticsearchIO.ConnectionConfiguration.create(addresses, "telegram", "qmgz2019")));

        //运行管道，直到任务流完成前一直等待
        pipeline.run().waitUntilFinish();

    }

    public static void testBeam3() {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);

        Pipeline pipeline = Pipeline.create(options);

//        String textPath = "F:\\qmgz2019.txt";
        String textPath = "F:\\ttt\\qmgz2019_0610\\qmgz2019_0610.txt";
//        String textPath = "F:\\qmgz2019_0610.txt";
        PCollection<Qmgz> qmgzTablePCollection =pipeline.apply(TextIO.read().from(textPath))
                .apply("ReverseToEntityObject", ParDo.of(new DoFn<String, Qmgz>() {
                    private static final long serialVersionUID = 1L;
                    @ProcessElement
                    public void parseLines(ProcessContext pc) {
                        String jsonStr = pc.element();
                        Qmgz qmgz = null;
                        try {
                            qmgz = GsonUtil.getObjectByJsonString(jsonStr, Qmgz.class);
                            String date = qmgz.getDate();
                            qmgz.setDate(getFmtDate(date));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(qmgz != null){
                            pc.output(qmgz);
                        }
                    }}));

        // 备份写入 Elasticsearch
        String[] addresses = { "http://172.30.154.244:9200/" };
        PCollection<String> jsonCollection = qmgzTablePCollection.setCoder(AvroCoder.of(Qmgz.class))
                .apply("CovertJson", ParDo.of(new DoFn<Qmgz, String>() {
                    private static final long serialVersionUID = 1L;
                    @ProcessElement
                    public void processElement(ProcessContext ctx) {
                        Gson gon = new Gson();
                        String jString = "";
                        try {
                            jString = gon.toJson(ctx.element()); // 进行序列号代码
                        } catch (Exception e) {
                        }
                        ctx.output(jString);   // 回传实体
                    }
                }));

        // 所有的Beam 数据写入ES的数据统一转换成json 才可以正常插入
        jsonCollection.apply(ElasticsearchIO.write()
                .withConnectionConfiguration(ElasticsearchIO.ConnectionConfiguration.create(addresses, "telegram", "qmgz2019")));

        //运行管道，直到任务流完成前一直等待
        pipeline.run().waitUntilFinish();
    }

    /**
     * 解析常见的时间格式[年-月-日 时:分:秒]，例如
     */
    public static String getFmtDate(String value) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date retVal = simpleDateFormat.parse(value);
            date = simpleDateFormat2.format(retVal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
