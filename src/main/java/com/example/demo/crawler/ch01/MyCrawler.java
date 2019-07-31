package com.example.demo.crawler.ch01;

import java.util.Set;

/**
 * Description：宽度爬虫主程序
 * Author；JinHuatao
 * Date: 2019/7/31 13:16
 */
public class MyCrawler {

    /**
     *  使用种子初始化URL队列
     * */
    private void initCrawlerWithSeeds(String[] seeds){
        for(int i = 0; i < seeds.length; i++)
            VisitedUrlQueue.addUnvisitedUrl(seeds[i]);
    }

    public void crawling(String[] seeds){
        //定义过滤器，提取以http://www.lietu.com开头的链接
        LinkFilter filter = new LinkFilter() {
            @Override
            public boolean accept(String url) {
                if(url.startsWith("http://www.lietu.com"))
                    return true;
                else
                    return false;
            }
        };
        //初始化URL队列
        initCrawlerWithSeeds(seeds);
        //循环条件：待抓取的链接不空且抓取的网页不多于1000
        while (!VisitedUrlQueue.unVisitedUrlIsEmpty() && VisitedUrlQueue.getVisitedUrlNum() <= 1000){
            //队头URL出队列
            String visitUrl = (String)VisitedUrlQueue.unVisitedUrlDeQueue();
            if(visitUrl == null)
                continue;
            DownLoadFile downLoadFile = new DownLoadFile();
            //下载网页
            downLoadFile.downloadFile(visitUrl);
            //提取出下载中的网页URL
            Set<String> links = HtmlParserTool.extractLinks(visitUrl, filter);
            //新的未访问的URL入队
            for(String link: links){
                VisitedUrlQueue.addUnvisitedUrl(link);
            }
        }
    }

    public static void main(String[] args) {
        MyCrawler crawler = new MyCrawler();
        crawler.crawling(new String[]{"http://www.lietu.com"});
    }
}
