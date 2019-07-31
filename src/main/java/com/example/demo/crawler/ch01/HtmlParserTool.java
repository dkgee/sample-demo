package com.example.demo.crawler.ch01;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.HashSet;
import java.util.Set;

/**
 * Description：Html页面解析工具类
 * Author；JinHuatao
 * Date: 2019/7/31 11:57
 */
public class HtmlParserTool {
    //获取一个网页上的链接，filter用来过滤链接
    public static Set<String> extractLinks(String url, LinkFilter filter){
        Set<String> links = new HashSet<>();
        try {
            Parser parser = new Parser(url);
            parser.setEncoding("gb2312");
            //过滤<frame>标签的filter，用来提取frame杯签里的 sre 属性
            NodeFilter frameFilter = new NodeFilter() {
                @Override
                public boolean accept(Node node) {
                    if(node.getText().startsWith("frame src=")){
                        return true;
                    }else {
                        return false;
                    }
                }
            };
            //OrFilter 来设置过滤<a>标签和<frame>标签
            OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
            //得到所有经过过滤的标签
            NodeList list = parser.extractAllNodesThatMatch(linkFilter);
            for(int i = 0; i < list.size(); i++){
                Node tag = list.elementAt(i);
                if( tag instanceof LinkTag){// <a> 标签
                    LinkTag link = (LinkTag) tag;
                    String linkUrl = link.getLink();//URL
                    if(filter.accept(linkUrl)){
                        links.add(linkUrl);
                    }
                }else {//<frame>标签
                    //提取frame里src属性的链接，如<frame src="test.html"/>
                    String frame = tag.getText();
                    int start = frame.indexOf("src=");
                    frame = frame.substring(start);
                    int end = frame.indexOf(" ");
                    if(end == -1)
                        end = frame.indexOf(">");
                    String frameUrl = frame.substring(5, end - 1);
                    if(filter.accept(frameUrl))
                        links.add(frameUrl);
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return links;
    }
}
