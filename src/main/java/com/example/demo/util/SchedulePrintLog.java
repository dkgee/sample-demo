package com.example.demo.util;

import java.util.concurrent.TimeUnit;

/**
 * Description：定时打印日志设计
 * Author；JinHuatao
 * Date: 2019/7/12 10:55
 */
public class SchedulePrintLog {

    public static void main(String[] args) {
        //每5s打印一次日志
        while (true){
            try{
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("===============================");
        }
    }
}
