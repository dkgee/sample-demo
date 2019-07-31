package com.example.demo.crawler.ch01;

/**
 * Description：链接过滤接口
 * Author；JinHuatao
 * Date: 2019/7/31 13:11
 */
public interface LinkFilter {
    public boolean accept(String url);
}
