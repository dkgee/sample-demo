package com.example.demo.crawler.ch01;

import java.util.LinkedList;

/**
 * Description：保存将要访问的URL队列
 * Author；JinHuatao
 * Date: 2019/7/31 10:20
 */
public class UrlQueue {

    private LinkedList queue = new LinkedList();

    //入队列
    public void enQueue(Object t){
        queue.add(t);
    }
    //出队列
    public Object deQueue(){
        return queue.removeFirst();
    }
    //判断队列是否为空
    public boolean isEmpty(){
        return queue.isEmpty();
    }
    //判断队列是否包含t
    public boolean contains(Object t){
        return queue.contains(t);
    }
}
