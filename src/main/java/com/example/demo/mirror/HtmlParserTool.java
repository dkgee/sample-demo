package com.example.demo.mirror;

import com.example.demo.util.DomainUtil;
import com.example.demo.util.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description：提取html页面链接
 * Author；JinHuatao
 * Date: 2019/8/29 14:12
 */
public class HtmlParserTool {


    public static Set<String> extractLinks(String url, LinkFilter filter){
        Set<String> links = new HashSet<>();
        try {
            WebDriver driver = new RemoteWebDriver(new java.net.URL("http://172.30.154.245:8910"), DesiredCapabilities.phantomjs());
            driver.get(url);
            String html = driver.getPageSource();

//            System.out.println("============================【Html页面】====================================");
//            System.out.println(html);
//            System.out.println("================================================================");
            Document document = Jsoup.parse(html);
//            System.out.println("============================【页面文本链接】====================================");
            String textBody = document.body().text();
//            System.out.println("============================【页面文本】====================================");
//            System.out.println(textBody);
//            System.out.println("================================================================");
            List<String> textLinks = TextUtil.getCommonUrl(textBody);
            for(String href: textLinks){
                if(StringUtils.isNotBlank(href)){
                    if(filter.accept(href)){
                        links.add(href);
                    }
                }
            }
//            System.out.println("============================【页面a标签链接】====================================");
            Elements elements = document.select("a");
            String host = DomainUtil.getSite(url);
            for(Element element: elements){
                String href = element.attr("href");
                if(StringUtils.isNotBlank(href)){
                    String tmpHref = "";
                    if(href.startsWith("http://")||(href.startsWith("https://"))){
                        tmpHref = href;
                    }
                  /*  if(href.startsWith("/")){
                        tmpHref = "http://" + host + href;
                    }*/
                    if(StringUtils.isBlank(tmpHref)){
                        continue;
                    }
                    if(filter.accept(tmpHref)){
                        links.add(tmpHref);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return links;
    }
}
