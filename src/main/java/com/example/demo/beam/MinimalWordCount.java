package com.example.demo.beam;

import org.apache.beam.repackaged.beam_sdks_java_core.com.google.common.base.Joiner;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/5/14 11:41
 */
public class MinimalWordCount {

    public static void main(String[] args) {
//        testDemo01();
//        distinctDemo();

        joinExample();
    }

    /**
     * 统计文本中每个词的数量
     * */
    public static void testDemo01(){
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);

        Pipeline pipeline = Pipeline.create(options);

        String textPath = "D:\\tmp.txt";
        pipeline.apply(TextIO.read().from(textPath))
                .apply("ExtractWords", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        //该PTransform从文本中抽取单词，输出的是一个PCollection<String>集合
                        String[] text = c.element().toLowerCase().split("[\\s\\,\\.]+");
                        for(String word: text){
                            if(!word.isEmpty()){
                                c.output(word);
                            }
                        }
                    }}))
                .apply(Count.<String>perElement())  //统计每个单词数量，返回的是PCollection<KV<T, Long>>集合
                .apply("ConcatResultKVs", MapElements.via(new SimpleFunction<KV<String,Long>, String>() {
                    //该PTransform负责对Map类型PCollection进行处理，此处定义一个简单的函数，对KV<String,Long>参数格式化
                    // 输出是一个PCollection<String>集合
                    @Override
                    public String apply(KV<String, Long> input) {
                        return input.getKey() + ":" + input.getValue();
                    }}))
                .apply(TextIO.write().to("wordcount"));//将统计结果输出到前缀名为wordcount的文件中

        pipeline.run().waitUntilFinish();
    }

    public static void distinctDemo(){
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);

        Pipeline pipeline = Pipeline.create(options);

        String textPath = "D:\\tmp2.txt";
        pipeline.apply(TextIO.read().from(textPath))
                .apply(Distinct.<String>create())//此处添加去重逻辑
                .apply(TextIO.write().to("deduped"));

        pipeline.run().waitUntilFinish();
    }

    /**
     *
     * */
    public static void joinExample(){
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);

        Pipeline pipeline = Pipeline.create(options);

        String idInfoPath = "D:\\tmp4-1.txt";
        final PCollection<KV<String, String>> idInfoCollection = pipeline
                .apply(TextIO.read().from(idInfoPath))
                .apply("CreateUserIdInfoPairs", MapElements.via(new SimpleFunction<String, KV<String, String>>() {
                    @Override
                    public KV<String, String> apply(String input) {
                        String[] values = input.split("\\t");
                        return KV.of(values[0], values[1]);
                    }
                }));

        String opInfoPath = "D:\\tmp4-2.txt";
        final PCollection<KV<String, String>> opCollection = pipeline
                .apply(TextIO.read().from(opInfoPath))
                .apply("CreateIdOperationPairs", MapElements.via(new SimpleFunction<String, KV<String, String>>() {
                    @Override
                    public KV<String, String> apply(String input) {
                        String[] values = input.split("\\t");
                        System.out.println("-------value:" + values[0]);
                        System.out.println("-------length:" + values.length);
                        return KV.of(values[0], values[1]);
                    }
                }));

        final TupleTag<String> idInfoTag = new TupleTag<>();
        final TupleTag<String> opInfoTag = new TupleTag<>();

        //TODO 没看懂
        //将两个 PCollection结果进行合并，
        final PCollection<KV<String, CoGbkResult>> cogrouppedCollection = KeyedPCollectionTuple
                .of(idInfoTag, idInfoCollection)
                .and(opInfoTag, opCollection)
                .apply(CoGroupByKey.<String>create());

        final PCollection<KV<String,String>> finalResultCollection = cogrouppedCollection
                .apply("CreateJoinedIdInfoPairs", ParDo.of(new DoFn<KV<String, CoGbkResult>, KV<String, String>>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        KV<String, CoGbkResult> e = c.element();
                        String id = e.getKey();
                        String name = e.getValue().getOnly(idInfoTag);
                        for(String opInfo: c.element().getValue().getAll(opInfoTag)){
                            c.output(KV.of(id, "\t" + name + "\t" + opInfo));
                        }
                    }
                }));

        PCollection<String> formattedResults = finalResultCollection
                .apply("FormatFinalResults", ParDo.of(new DoFn<KV<String, String>, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        c.output(c.element().getKey() + "\t" + c.element().getValue());
                    }
                }));

        formattedResults.apply(TextIO.write().to("joinedResults"));
        pipeline.run().waitUntilFinish();
    }

    public static void testDemo(){
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);
        Pipeline pipeline = Pipeline.create(options);

        String textPath = "D:\\tmp3.txt";
        pipeline.apply("ReadMyFile", TextIO.read().from(textPath))
               .apply("ExtractFields", ParDo.of(new DoFn<String, KV<String, String>>() {//输入是一个String字符，输出是一个 KV<String, String>
                   //按行读取文件，对文件按制表符进行分割，转换成一个
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        String[] values = c.element().split("\t");
                        if(values.length == 3){
                            c.output(KV.of(values[1], values[0]));
                        }
                    }}))
               .apply("GroupByKey", GroupByKey.<String, String>create()) //按Key进行分组 PCollection<KV<String, Iterable<String>>>
               .apply("ConcatResults", MapElements.via(new SimpleFunction<KV<String,Iterable<String>>, String>() {
                   @Override
                   public String apply(KV<String, Iterable<String>> input) {
                       return new StringBuffer()
                               .append(input.getKey())
                               .append("\t")
                               .append(Joiner.on(",").join(input.getValue())).toString();// 35451605324179  3G,2G,4G
                   }}))
                .apply(TextIO.write().to("grouppedResults"));
        pipeline.run().waitUntilFinish();
    }


}
