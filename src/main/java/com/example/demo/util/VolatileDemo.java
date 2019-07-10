package com.example.demo.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/7/9 10:17
 */
public class VolatileDemo {

//    private volatile int number = 0;

//    private Lock lock = new ReentrantLock();

//    private int number = 0;

    private static AtomicInteger a = new AtomicInteger();

    public int getNumber() {
//        return number;
        return a.get();
    }

    // synchronized     写在方法上，造成程序更低效
    public void increase(){
        /*try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

       /* synchronized (this){
            this.number++;
        }*/
//        lock.lock();
//        try{
//            this.number++;
//        }finally {
//            lock.unlock();
//        }

//        this.number++;

        a.getAndIncrement();
    }

    public static void main(String[] args) {
        final  VolatileDemo volDemo = new VolatileDemo();
        for(int i = 0; i < 500; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    volDemo.increase();
                }
            }).start();
        }

        while (Thread.activeCount() > 1){
            Thread.yield();
        }
        System.out.println("number:" + volDemo.getNumber());
    }
}
