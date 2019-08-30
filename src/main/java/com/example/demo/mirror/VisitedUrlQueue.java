package com.example.demo.mirror;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Description：记录已访问过后的URL队列
 * Author；JinHuatao
 * Date: 2019/7/31 10:26
 */
public class VisitedUrlQueue {
    //已访问的url集合
    private static Set<URL> visitedUrl = new HashSet<>();
    //待访问的url集合
    private static UrlQueue<URL> unVisitedUrl = new UrlQueue();

    //带优先级的待访问的url集合
    private static Queue unVisitedUrlWithPriory = new PriorityQueue<>();

    //获取url队列
    public static UrlQueue getUnVisitedUrl(){
        return unVisitedUrl;
    }
    //添加到访问过的URL队列
    public static void addVisitedUrl(URL url){
        visitedUrl.add(url);
    }
    //移除访问过的URL
    public static void removeVisitedUrl(String url){
        visitedUrl.remove(url);
    }
    //未访问的URL出队列
    public static Object unVisitedUrlDeQueue(){
        return unVisitedUrl.deQueue();
    }
    //保证每个url只被访问一次
    public static void addUnvisitedUrl(URL url){
        if(url != null && !url.getUrl().trim().equals("")
                && !visitedUrl.contains(url)
                && !unVisitedUrl.contains(url)){
            unVisitedUrl.enQueue(url);
        }
    }
    //获得已经访问的URL数目
    public static int getVisitedUrlNum(){
        return visitedUrl.size();
    }
    //判断未访问的URL队列中是否为空
    public static boolean unVisitedUrlIsEmpty(){
        return unVisitedUrl.isEmpty();
    }

}
