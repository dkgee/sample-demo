package com.example.demo.stream;

import org.apache.commons.io.FileUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/7/3 13:22
 */
public class Stream_demo01 {

    public static void fun01(){
        // 创建一个空的货物集合数组
        // 使用for循环遍历数组，如果货物类型等于某一类型，将其添加至货物集合数组
        // 使用Collections的工具类对货物集合进行排序
        // 创建新的货物ID数组集合
        // 遍历货物集合数组，将其ID添加至新的货物ID数组集合中
    }

    public static void fun02(){
        // 创建一个并行的集合流处理
        // 过滤集合中货物类型是某一个类型的货物集合
        // 对集合进行排序
        // 创建一个货物ID集合的map
        // 将其转换为数组

        List<Goods> goodsList = new ArrayList<>();
        Goods g01 = new Goods(1, 11, Goods.Type.fruit );
        Goods g02 = new Goods(2, 12, Goods.Type.fruit );
        Goods g03 = new Goods(3, 13, Goods.Type.milk );
        Goods g04 = new Goods(4, 14, Goods.Type.milk );
        goodsList.add(g01);
        goodsList.add(g02);
        goodsList.add(g03);
        goodsList.add(g04);

        List<Integer> goodsId = goodsList.parallelStream()
                .filter(t -> t.getType() == Goods.Type.milk)
                .sorted(Comparator.comparing(Goods::getValue))
                .map(Goods::getId)
                .collect(Collectors.toList());

        System.out.println(goodsId);
    }

    public static void fun03(){
        // 1，单个值构建流
        Stream stream = Stream.of("a", "b", "c");
        // 2，数组
        String [] strArray = new String[]{"a", "b", "c"};
        stream = Stream.of(strArray);
        stream = Arrays.stream(strArray);
        // 3, 使用集合创建流
        List<String> list = Arrays.asList(strArray);
        stream = list.stream();

        //数值流的构造
        IntStream.of(new int[]{1, 2, 3}).forEach(System.out::println);
        IntStream.range(1, 3).forEach(System.out::println); // [1, 3) 区间内整型
        IntStream.rangeClosed(1,3).forEach(System.out::println); //[1, 3] 区间内整型


        //sample01：使用其他工具类内部方法，处理映射关系
        List<String> wordList = new ArrayList<>();
        wordList.add("hello");
        wordList.add("tank");
        wordList.add("easy");

        List<String> output = wordList.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(output);

        //sample02: 使用自定函数处理映射关系
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> squareNums = nums.stream().map(n -> n * n).collect(Collectors.toList());
        System.out.println(squareNums);


        //sample03: 将多个数组合并后，转成一个数组输出
        Stream<List<Integer>> inputStream = Stream.of(Arrays.asList(1), Arrays.asList(2, 3), Arrays.asList(4, 5, 6));
        // 将几个数组拍平成一个数组
        Stream<Integer> outputStream = inputStream.flatMap(childStream -> childStream.stream());
        System.out.println(outputStream.collect(Collectors.toList()));

        //sample04: 输出数组中的偶数
        Integer[] sixNums = {1, 2, 3, 4, 5, 6};
        Integer[] evens = Stream.of(sixNums).filter(t -> t % 2 == 0).toArray(Integer[]::new);
        System.out.println(evens[0]);

        // sample04: 读取一个文本中的所有单词，并转换为集合，在转换的过程中，对单词进行特殊字符处理处理
        try {
            String path = "D:/test.txt";
            List<String> wordLine = FileUtils.readLines(new File(path));
            List<String> word = wordLine.stream()
                    .flatMap(line ->{
                        return Stream.of(line.split(" ")).map(s -> {
                            if(s.contains("."))
                                s = s.replace(".", "");
                            else if(s.contains(","))
                                s = s.replace(",", "");
                            else if(s.contains("/"))
                                s = s.replace("/", "");
                            else if(s.contains("'"))
                                s = s.replace("'", "");
                            else if(s.contains(";"))
                                s = s.replace(";", "");
                            return s;
                        }).parallel();
                    })
                    .filter(w -> w.length() > 0)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            System.out.println(word);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // sample05: 创建一个词流，输出满足特定条件的词后，对词进行再处理，再输出。
        Stream.of("one", "two", "three")
                .filter(s -> s.length() == 3)
                .peek(System.out::println)
                .map(String::toUpperCase)
                .peek(System.out::println)
                .collect(Collectors.toList());

        // sample06: 并行处理输出的是无序的数据
        List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        list.parallelStream().forEach(System.out::println);         //无序输出
        list.parallelStream().forEachOrdered(System.out::println);  //按照原来的顺序输出

        //sample07: 空值变量控制
        String strA = " abcd", strB = null;
        print(strA);
        print("");
        print(strB);
        System.out.println(getLen(strA));
        System.out.println(getLen(""));
        System.out.println(getLen(strB));


        //sample07: reduce ，在给定起始值的基础上，筛选值；字符串拼接、min、max、average、sum都是特殊的reduce
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4);
//        Integer num = integerStream.reduce(0, (a, b) -> a + b);
        Integer num2 = integerStream.reduce(0, Integer::sum);
        System.out.println(num2);

        //sample08:
        String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
        System.out.println(concat);
        double minValue = Stream.of(-1.5, 1.0, -3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);
        System.out.println(minValue);
        //有起始值
        int sumNum = Stream.of(1, 2, 3, 4).reduce(0, Integer::sum);
        System.out.println(sumNum);
        //无起始值
        int sumNum2 = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();


        //sample09: 执行js脚本
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
        String name = "tank";
        Integer result = null;
        try {
            nashorn.eval("print('" + name + "')");
            result = (Integer) nashorn.eval("10 + 2");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        System.out.println(result.toString());

        //sample10: 递归
        Stream.iterate(0, n -> n + 3).limit(10).forEach(x -> System.out.println(x + " "));
    }

    public static int getLen(String text){
        // if (text != null) ? text.length() : -1
        return Optional.ofNullable(text).map(String::length).orElse(-1);
    }

    public static void print(String text){
        // if(text != null) 输出text
        Optional.ofNullable(text).ifPresent(System.out::println);
    }

    public static void main(String[] args) {



    }
}

class Goods{

    private int id;

    private int value;

    private Type type;

    public Goods(int id, int value, Type type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    enum Type{
        milk,fruit
    }
}
