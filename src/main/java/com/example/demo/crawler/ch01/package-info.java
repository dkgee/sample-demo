/**
 * Description：
 *      演示了宽度爬虫的示例，这里面主要运用了爬虫队列、过滤器、网页下载、网页链接提取四部分
 *          爬虫队列：（1）存放待爬取的链接、（2）存放已爬取过的链接
 *          过滤器：限定指定范围内的链接，如只爬网站的内链
 *          网页下载：发起http请求，获取网页内容，或下载内容
 *          网页链接提取：从网页中提取出新的链接地址，加入到爬虫队列里。
 *      通过这四部分可以初步了解，单线程爬虫的工作的原理。
 *      其中用到的数据结构或工具：LinkedList、HashSet、HttpClient、HtmlParser
 * Author；JinHuatao
 * Date: 2019/7/31 15:23
 */
package com.example.demo.crawler.ch01;