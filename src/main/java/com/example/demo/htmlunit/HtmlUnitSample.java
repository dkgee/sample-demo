package com.example.demo.htmlunit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import junit.framework.Assert;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/8 9:06
 */
public class HtmlUnitSample {


    /**
     * 不能直接用，会遇到证书错误，获取不到页面
     *  https://195.160.231.158/
     *  http://ssr.bingly.cn/
     * */
    public static void homePage(){
        try(final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://ssr.bingly.cn/");
            System.out.println("===========>" + page.getTitleText());//标题
//            Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

            final String pageAsXml = page.asXml();//html文档
//            Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));
            System.out.println("===========>" + pageAsXml);

            final String pageAsText = page.asText();//html 的内容文本
//            Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
            System.out.println("===========>" + pageAsText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟浏览器
     * */
    public static void homePage_Firefox(){
        try(final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            final HtmlPage page = webClient.getPage("https://www.baidu.com/");
            Assert.assertEquals("百度一下，你就知道", page.getTitleText());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找一个指定的DOM元素
     * */
    public static void getSpecificElements(){
        try(final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("https://www.xxx.com/");
            final HtmlDivision div = page.getHtmlElementById("some_div_id");
            final HtmlAnchor anchor = page.getAnchorByName("anchor_name");
//            NodeList inputs = page.getElementsByTagName("input");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void homePage_proxy(){
        String proxyhost = "127.0.0.1";
        int port = 1080;
        try(final WebClient webClient = new WebClient(BrowserVersion.CHROME, proxyhost, port)) {
            final HtmlPage page = webClient.getPage("https://www.google.com.hk/?gws_rd=cr,ssl");
            Assert.assertEquals("Google", page.getTitleText());
            System.out.println("==================================================>" + page.getTitleText());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        homePage();
//        homePage_Firefox();
        homePage_proxy();
    }
}
