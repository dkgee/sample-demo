package com.example.demo.crawler.ch01_2;

/**
 * Description：访问前置器
 * Author；JinHuatao
 * Date: 2019/7/31 18:29
 */
public interface VisitedFrontier{

    boolean contains(String value);

    void add(String value);

    void add(CrawlUrl url);
}
