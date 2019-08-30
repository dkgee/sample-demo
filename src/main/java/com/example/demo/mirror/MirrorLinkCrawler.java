package com.example.demo.mirror;

import java.util.HashSet;
import java.util.Set;

/**
 * Description：镜像地址爬取程序
 * Author；JinHuatao
 * Date: 2019/8/29 14:21
 */
public class MirrorLinkCrawler {

    /**
     *  使用种子初始化URL队列
     * */
    private void initCrawlerWithSeeds(String[] seeds, int maxDepth){
        for(int i = 0; i < seeds.length; i++){
            URL url = new URL(seeds[i], maxDepth);
            VisitedUrlQueue.addUnvisitedUrl(url);
        }
    }

    public void crawling(String[] seeds, int maxDepth){
        TopDomainFilter filter = new TopDomainFilter();
        filter.init(new String[]{"github.com","githubstatus.com","github.blog","github.community","git.io",
                "google.com","mozilla.org","opensource.guide", "macromedia.com"});
        initCrawlerWithSeeds(seeds, maxDepth);
        Set<String> targetLinks = new HashSet<>();
        while (!VisitedUrlQueue.unVisitedUrlIsEmpty() && VisitedUrlQueue.getVisitedUrlNum() <= 1000){
            URL visitUrl = (URL)VisitedUrlQueue.unVisitedUrlDeQueue();
            if(visitUrl == null)
                continue;
            Set<String> links = HtmlParserTool.extractLinks(visitUrl.getUrl(), filter);
            targetLinks.addAll(links);
            for(String link: links){
                int depth = visitUrl.getDepth() + 1;
                if(depth <= maxDepth){
                    URL url = new URL(link, depth, maxDepth);
                    VisitedUrlQueue.addUnvisitedUrl(url);
                }
            }
        }
        //System.out.println("==========> 入口地址：" + seeds);
        System.out.println("==========> 爬取深度：" + maxDepth);
        System.out.println("==========> 发现可疑链接数： " + targetLinks.size() );
        for(String url: targetLinks){
            System.out.println(url);
        }
    }

    public static void main(String[] args) {
        MirrorLinkCrawler mirrorLinkCrawler = new MirrorLinkCrawler();
        //"https://github.com/hao369/a/wiki/jyg",
        String[] enryUrl = { "https://dc.geye8.gq/"};
        int maxDepth = 3;
        mirrorLinkCrawler.crawling(enryUrl, maxDepth);
    }
}
