package com.example.demo.crawler.ch01_2;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/7/31 17:30
 */
public interface Frontier {

    CrawlUrl getNext() throws Exception;

    boolean putUrl(CrawlUrl url) throws Exception;
}
