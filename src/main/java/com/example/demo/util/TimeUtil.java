package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述：
 * 作者: JinHuaTao
 * 时间：2017/6/23 9:11
 */
public class TimeUtil {

    private static final String start_prefix = " 00:00:00";

    private static final String end_prefix = " 23:59:59";

    private static Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * 获取时间转换成串
     *
     * @param time
     * @return
     */
    public static String formatDateTime(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }

    /**
     * 解析美国时间格式，[月 日,年]  例如："July 27, 2017"
     */
    public static Date getMDYDate(String value) {
        Date retVal = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd,yyyy", Locale.US);
        String dateTxt = value.toString();
        try {
            retVal = simpleDateFormat.parse(dateTxt);
        } catch (ParseException e) {
            simpleDateFormat.applyPattern("MMM dd,yyyy");
            try {
                retVal = simpleDateFormat.parse(dateTxt);
            } catch (ParseException e1) {
                e1.printStackTrace();
                retVal = null;
            }
        }
        return retVal;
    }

    public static Map<String, String> getLastTwoMinute() {
        Map<String, String> rsMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.roll(Calendar.MINUTE, -2);
        Date startDate = calendar.getTime();

        String startTime = sdf.format(startDate);
        String endTime = sdf.format(endDate);

        rsMap.put("st", startTime);
        rsMap.put("et", endTime);
        return rsMap;
    }

    /**
     * 解析常见的时间格式[年-月-日 时:分:秒]，例如
     */
    public static Date getYMDDate(String value) {
        Date retVal = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateTxt = value.toString();
        try {
            retVal = simpleDateFormat.parse(dateTxt);
        } catch (ParseException e) {
            simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            try {
                retVal = simpleDateFormat.parse(dateTxt);
            } catch (ParseException e1) {
                logger.error("时间解析异常：" + e1);
                retVal = null;
            }
        }
        return retVal;
    }

    public static String getDay(Date d, int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + day);
        return formatYMD(cal.getTime());
    }

    /**
     * String转化Date(yyyy-MM-dd HH:mm:ss)
     * @param dateString
     * @return
     */
    public static  Date getDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String getTodayStr() {
        Calendar cal = Calendar.getInstance();
        return formatYMD(cal.getTime());
    }

    public static Date getNow() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    public static String formatYMD(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(time);
    }

    public static String formatYMDHMS(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }

    public static String formatYMDHM(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return format.format(time);
    }

    public static String getHour() {
        SimpleDateFormat format = new SimpleDateFormat("HH");
        return format.format(Calendar.getInstance().getTime());
    }

    public static String getYesterdayStr() {
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.DATE, -1);
        Date day = cal.getTime();
        return formatYMD(day);
    }

    public static String getPassThreedayStr() {
        return getDay(new Date(), -3);
    }

    public static String getYesterdayStartStr() {
        return getYesterdayStr() + start_prefix;
    }

    public static String getPassThreeStartStr() {
        return getPassThreedayStr() + start_prefix;
    }

    /**
     * 获取当前时间点过去12小时的时间点
     * */
    public static String getPassTwelveStr() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        //如果是早上8点，则向后拨一天
        if( hour <= Calendar.HOUR_OF_DAY){
            cal.roll(Calendar.DATE, -1);
        }

        cal.roll(Calendar.HOUR_OF_DAY, -12);
        Date date = cal.getTime();
        return formatYMDHMS(date);
    }

    /**
     * 获取系统当前时间
     * */
    public static String getNowYMDHMS(){
        Calendar cal = Calendar.getInstance();
        return formatYMDHMS(cal.getTime());
    }

    /**
     * 解析常见的时间格式 [2018年01月18日 --等等]
     *
     * @param value 字符串的时间
     * @return Date
     */
    public static Date getChinaDate(String value) {
        Date retVal;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dateTxt = value.toString();
        try {
            retVal = simpleDateFormat.parse(dateTxt);
        } catch (ParseException e) {
            simpleDateFormat.applyPattern("yyyy年MM月dd日 HH时mm分ss秒");
            try {
                retVal = simpleDateFormat.parse(dateTxt);
            } catch (ParseException e1) {
                logger.error("时间解析异常：" + e1);
                retVal = null;
            }
        }
        return retVal;
    }

    /**
     * 解析墨西哥时间
     * @param value
     * @return date
     */
    public static Date  analyzeMexicoTime(String value){
        String[] splitResult = value.split(" ");
        //校验墨西哥时间格式
        if(splitResult.length != 5){
            logger.error("墨西哥时间格式异常!");
            return null;
        }
        String year = splitResult[4];
        String date = splitResult[0];
        String month = null;
        if(value.contains("dic")){
            month = "12";
        }
        if(value.contains("nov")){
            month = "11";
        }
        if(value.contains("oct")){
            month = "10";
        }
        if(value.contains("sep")){
            month = "9";
        }
        if(value.contains("ago")){
            month = "8";
        }
        if(value.contains("jul")){
            month = "7";
        }
        if(value.contains("jun")){
            month = "6";
        }
        if(value.contains("may")){
            month = "5";
        }
        if(value.contains("abr")){
            month = "4";
        }
        if(value.contains("mar")){
            month = "3";
        }
        if(value.contains("feb")){
            month = "2";
        }
        if(value.contains("ene")){
            month = "1";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(year + "-" + month + "-" + date);
        String time = sb.toString();
        Date reVal = getYMDDate(time);
        return reVal;
    }

    public static Date  analyzeUKTime(String value){
        String[] splitResult = value.split(" ");
        if(splitResult.length != 3){
            logger.error("英国时间格式异常!");
            return null;
        }
        String year = splitResult[2];
        String day = splitResult[0];
        String month = null;
        String valueLowerCase = value.toLowerCase();
        if(valueLowerCase.contains("dec")){
            month = "12";
        } else if(valueLowerCase.contains("nov")){
            month = "11";
        } else if(valueLowerCase.contains("oct")){
            month = "10";
        } else if(valueLowerCase.contains("sep")){
            month = "9";
        } else if(valueLowerCase.contains("agu")){
            month = "8";
        } else if(valueLowerCase.contains("aug")){
            month = "8";
        } else if(valueLowerCase.contains("jul")){
            month = "7";
        } else if(valueLowerCase.contains("jun")){
            month = "6";
        } else if(valueLowerCase.contains("may")){
            month = "5";
        } else if(valueLowerCase.contains("apr")){
            month = "4";
        } else if(valueLowerCase.contains("mar")){
            month = "3";
        } else if(valueLowerCase.contains("feb")){
            month = "2";
        } else if(valueLowerCase.contains("jan")){
            month = "1";
        }else {
            logger.error("UK时间类型解析找不到月份" + value);
            return TimeUtil.getYMDDate("1971-1-1");
        }

        StringBuffer sb = new StringBuffer();
        //如果时间为1970的处理
        if ("1970".equals(year)){
            year = "1971";
        }
        sb.append(year + "-" + month + "-" + day);
        Date rstVal = TimeUtil.getYMDDate(sb.toString());
        return rstVal;
    }

    public static void main(String[] args) {
         System.out.println(formatYMDHM(new Date()));
    }
}
