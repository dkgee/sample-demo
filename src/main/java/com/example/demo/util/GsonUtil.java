package com.example.demo.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述：JSON工具类
 * 作者: JinHuaTao
 * 时间：2017/6/14 9:33
 */
public class GsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);

    private static Gson gson = new Gson();

    /**
     * 将一个对象解析成json字符串
     * */
    public static String getJsonStringByObject(Object obj){
        return gson.toJson(obj);
    }

    /**
     * 解析json数据对象
     * */
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        T result = null;
        try{
            result = gson.fromJson(jsonData, type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按类型json字符串成一个对象
     * */
    public static Object getObjectByJsonString(String jsonStr, Type type){
        Object obj = null;
        try{
            obj = gson.fromJson(jsonStr, type);
        }catch(Exception e){
            e.printStackTrace();
        }
        return obj;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getObjectByJsonString(String jsonStr, Class<T> clazz){
        Map<String, String> objectList = (Map<String, String>)getObjectByJsonString(jsonStr , new TypeToken<Map<String, String>>() {}.getType());
        System.out.println("=============>>>>>>>>>>>>>>>======" + objectList);
        T obj = (T) mapToBean(objectList, clazz);
        return obj;
    }


    /**
     * 将一个Map对象数组转换为实际数组集合返回
     */
    public static Object mapToList(List<Object> list, Class<?> clazz) {
        List<Object> objectList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                if (obj instanceof Map) {
                    try {
                        Object object = mapToBean((Map<String, Object>) obj, clazz);
                        objectList.add(object);
                    } catch (Exception e) {
                        logger.error("转换成Bean异常：" + e);
                    }
                }
            }
        }
        return objectList;
    }

    public static Object mapToBean(Object obj,  Class<?> clazz){
        if (obj instanceof Map) {
            Map tmpObj = (Map) obj;
            try {
                obj = mapToBean(tmpObj, clazz);
            } catch (Exception e) {
                logger.error("转换成Bean异常：" + e);
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 将Map对象通过反射机制转换成Bean对象
     *
     * @param map   存放数据的map对象
     * @param clazz 待转换的class
     * @return 转换后的Bean对象
     * @throws Exception 异常
     */
    public static Object mapToBean(Map<String, Object> map, Class<?> clazz) throws Exception {
        Object obj = clazz.newInstance();
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String propertyName = entry.getKey();       //属性名
                Object value = entry.getValue();
                String setMethodName = "set"
                        + propertyName.substring(0, 1).toUpperCase()
                        + propertyName.substring(1);
                Field field = getClassField(clazz, propertyName);
                if (field == null)
                    continue;
                Class<?> fieldTypeClass = field.getType();
                value = convertValType(value, fieldTypeClass);
                try {
                    clazz.getMethod(setMethodName, field.getType()).invoke(obj, value);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 将Object类型的值，转换成bean对象属性里对应的类型值
     *
     * @param value          Object对象值
     * @param fieldTypeClass 属性的类型
     * @return 转换后的值
     */
    private static Object convertValType(Object value, Class<?> fieldTypeClass) {
        Object retVal = null;
        if (Long.class.getName().equals(fieldTypeClass.getName())
                || long.class.getName().equals(fieldTypeClass.getName())) {
            retVal = Long.parseLong(value.toString());
        } else if (Integer.class.getName().equals(fieldTypeClass.getName())
                || int.class.getName().equals(fieldTypeClass.getName())) {
            retVal = Integer.parseInt(value.toString());
        } else if (Float.class.getName().equals(fieldTypeClass.getName())
                || float.class.getName().equals(fieldTypeClass.getName())) {
            retVal = Float.parseFloat(value.toString());
        } else if (Double.class.getName().equals(fieldTypeClass.getName())
                || double.class.getName().equals(fieldTypeClass.getName())) {
            retVal = Double.parseDouble(value.toString());
        } else if (Boolean.class.getName().equals(fieldTypeClass.getName())
                || boolean.class.getName().equals(fieldTypeClass.getName())) {
            retVal = Boolean.parseBoolean(value.toString());
        } else if (Date.class.getName().equals(fieldTypeClass.getName())) {
            String dateTxt = value.toString();
            retVal = parserDate(dateTxt);
        } else if(DateTime.class.getName().equals(fieldTypeClass.getName())){
            String dateTxt = value.toString();
            retVal = HLDateTimeUtils.parseDateTimeDefault(dateTxt);
        }else if(MutableDateTime.class.getName().equals(fieldTypeClass.getName())){
            String dateTxt = value.toString();
//            retVal = HLDateTimeUtils.parseDateTimeDefault(dateTxt);
            retVal = MutableDateTime.now();
        }else {
            retVal = value;
        }
        return retVal;
    }

    /**
     * 同步时间转换格式
     * 间转换需要再次处理   例如 July 27, 2017; 2017-08-07 ....
     * */
    public static Object parserDate(final String dateTxt){
        Object retVal = dateTxt;
        if (dateTxt.contains("-")) {
            if(dateTxt.equals("-")){
                retVal = TimeUtil.getYMDDate("1971-01-01");
            }else {
                //2018-02-08
                retVal = TimeUtil.getYMDDate(dateTxt);
            }
        } else if (dateTxt.contains(",")) {
            //July 27, 2017
            retVal = TimeUtil.getMDYDate(dateTxt);
        }else if(dateTxt.contains("年")){
            //2018年01月18日
            retVal = TimeUtil.getChinaDate(dateTxt);
        }else if(dateTxt.contains(".")){
            //22 de oct. de 2016
            retVal = TimeUtil.analyzeMexicoTime(dateTxt);
        }else if(dateTxt.split(" ").length==3){
            //3 October 2015
            retVal = TimeUtil.analyzeUKTime(dateTxt);
        }else {
            //未发现该时间格式
            logger.error("》》》》》》》》》》》》没有此类时间格式:【" + dateTxt + "】");
            retVal = TimeUtil.getYMDDate("1971-01-01");
        }

        return retVal;
    }

    /**
     * 获取指定字段名称查找在class中的对应的Field对象(包括查找父类)
     *
     * @param clazz     指定的class
     * @param fieldName 字段名称
     * @return Field对象
     */
    private static Field getClassField(Class<?> clazz, String fieldName) {
        if (Object.class.getName().equals(clazz.getName())) {
            return null;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {// 简单的递归一下
            return getClassField(superClass, fieldName);
        }
        return null;
    }


    public static void main(String[] args) {
        /*Map<String, String> result = new HashMap<>();
        result.put("num", "123");
        result.put("index", "456");
        System.out.println(GsonUtil.getJsonStringByObject(result));*/
        String txt="[{\"entryUrl\":\"https://1lwuh3x.ilcs8.cf/7512366\",\"pageTitle\":\"动态网\",\"pageSavePath\":\"/fileRoot/xx/动态网.html\",\"snapshotSavePath\":\"/fileRoot/xx/动态网.png\"}]";
       /* List<WebPageInfo> ww = ( List<WebPageInfo>)GsonUtil.getObjectByJsonString(txt, new TypeToken<List<WebPageInfo>>(){}.getType());

        System.out.println(ww);*/

//        String dateTxt = "2019-05-09 16:55:00";
//        DateTime retVal =  HLDateTimeUtils.parseDateTimeDefault(dateTxt);
//        retVal.toInstant();
//        System.out.println(retVal.toInstant());
        MutableDateTime mutableDateTime = MutableDateTime.now();
        System.out.println(mutableDateTime);

    }

}
