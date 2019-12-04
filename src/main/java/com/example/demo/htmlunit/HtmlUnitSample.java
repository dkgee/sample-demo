package com.example.demo.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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
        try(final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            final HtmlPage page = webClient.getPage("https://github.com/hao369/a/wiki/jyg");
//            Assert.assertEquals("Google", page.getTitleText());
            System.out.println("==================================================>" + page.asText());//获取网页字符串文本
//            System.out.println("==================================================>" + page.asXml());//获取网页内容XML文本
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] homePage_sample(String url){
        // httpclient只能拿到第一个页面，htmlunit可以拿到跳转的页面。
        try(final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setUseInsecureSSL(true);//忽略ssl认证
            webClient.getOptions().setCssEnabled(false);//是否启用CSS
            webClient.getOptions().setJavaScriptEnabled(true);//很重要，启用JS
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

            webClient.getOptions().setAppletEnabled(false);
            webClient.getOptions().setActiveXNative(false);//是否执行ActiveX
//            webClient.getOptions().setGeolocationEnabled(false);
            webClient.getOptions().setPopupBlockerEnabled(true);//阻止弹框
//            webClient.getOptions().setRedirectEnabled(false);//是否从定向
            webClient.getOptions().setRedirectEnabled(true);//是否从定向
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);//失败打印内容

            webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常

            webClient.getOptions().setTimeout(20000);//设置“浏览器”的请求超时时间
            webClient.setJavaScriptTimeout(20000);//设置JS执行的超时时间

            final HtmlPage page = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(20000);//该方法阻塞线程

            System.out.println(page.asXml());
           /* String contentType = page.getContentType();
            if(contentType.contains("image")){
                InputStream is = page.getWebResponse().getContentAsStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int current = 0;
                int size = 0;
                while((current = bis.read(data,0,data.length)) != -1){
                    buffer.write(data,0,current);
                    size += current;
                }
                return buffer.toByteArray();
            }*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void homePage_sample03(String url) {
        String proxyhost = "127.0.0.1";
        int port = 1080;
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME, proxyhost, port)) {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setTimeout(20000);//设置“浏览器”的请求超时时间
            webClient.setJavaScriptTimeout(20000);//设置JS执行的超时时间

            URL myUrl = new URL(url);
            WebRequest wr = new WebRequest(myUrl);
            wr.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            wr.setAdditionalHeader("Accept-Encoding", "gzip, deflate, sdch");
            wr.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
            wr.setAdditionalHeader("Cache-Control", "no-cache");
            wr.setAdditionalHeader("Connection", "keep-alive");
            wr.setAdditionalHeader("Pragma", "no-cache");
            wr.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
            wr.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");

            webClient.waitForBackgroundJavaScript(20000);//该方法阻塞线程

            WebResponse webResponse = webClient.loadWebResponse(wr);
            Page page = webClient.getPageCreator().createPage(webResponse, webClient.getCurrentWindow());
            HtmlPage htmlPage = (HtmlPage) page;

            System.out.println(htmlPage.asXml());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] homePage_sample02(String url){
        try(final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setUseInsecureSSL(true);
            HttpWebConnection httpWebConnection = new HttpWebConnection(webClient);
            webClient.setWebConnection(httpWebConnection);

            HtmlPage page = webClient.getPage("http://tongji.baidu.com");
            HtmlElement username = (HtmlElement) page.getElementById("UserName");
            HtmlElement password = (HtmlElement) page.getElementById("Password");
            HtmlElement valiCode = (HtmlElement) page.getElementById("Valicode");
            HtmlImage valiCodeImg = (HtmlImage) page.getElementById("cas_code");
            ImageReader imageReader = valiCodeImg.getImageReader();
            BufferedImage bufferedImage = imageReader.read(0);

            JFrame f2 = new JFrame();
            JLabel l = new JLabel();
            l.setIcon(new ImageIcon(bufferedImage));
            f2.getContentPane().add(l);
            f2.setSize(100, 100);
            f2.setTitle("验证码");
            f2.setVisible(true);

            String valicodeStr = JOptionPane.showInputDialog("请输入验证码：");
            f2.setVisible(false);
            HtmlElement submit = (HtmlImage)page.getElementById("Submit");
            HtmlSubmitInput submit2 = (HtmlSubmitInput) submit;
            username.click();
            username.type("gabazi");
            password.click();
            password.type("******");
            valiCode.click();
            valiCode.type(valicodeStr);

            HtmlPage resultPage = submit2.click();
            System.out.println(resultPage.asText());
            System.exit(0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  csdn https://blog.csdn.net/yanghaolong/article/details/86680282
     *  获取网页标题、
     *  获取文章title、date、writer、reader、分类专栏类型
     *  评论：icon、commenter、comment、time、number
     *
     * */
    public static void sample03(){
        String url = "https://blog.csdn.net/yanghaolong/article/details/86680282";
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setTimeout(30000);

            long st = System.currentTimeMillis();
            final HtmlPage page = webClient.getPage(url);

            //step01：获取html页面
            String title = page.getTitleText();
            HtmlHeading1 heading1 =  (HtmlHeading1) page.getByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[1]/h1").get(0);
            String articleTitle = heading1.asText();
            HtmlSpan span = page.getFirstByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/span[1]");
            String postTime = span.asText();
            HtmlAnchor anchor = page.getFirstByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/a");
            String writer = anchor.asText();
            HtmlSpan readSpan = page.getFirstByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/span[2]");
            String readNum = readSpan.asText();
            HtmlAnchor classfiAnchor = page.getFirstByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/div/a");
            String classfi = classfiAnchor.asText();

            HtmlImage image = page.getFirstByXPath("//*[@id=\"mainBox\"]/main/div[3]/div[2]/div[1]/ul[1]/li/a/img");
            String imageSrc = image.getSrcAttribute();
//            DomNode domNode = page.querySelector("#mainBox > main > div.comment-box > div.comment-list-container > div.comment-list-box > ul:nth-child(1) > li > a > img");
//            String imageSrc = domNode.getFirstChild().getAttributes().getNamedItem("src").getTextContent();
//            String imageSrc = domNode.getFirstChild().getNodeName().toString();

            System.out.println("#Title:" + title);
            System.out.println("#ArticleTitle:" + articleTitle);
            System.out.println("#PostTime:" + postTime);
            System.out.println("#Writer:" + writer);
            System.out.println("#ReadNum:" + readNum);
            System.out.println("#ReadNum:" + readNum);
            System.out.println("#ImageSrc:" + imageSrc);
            long et = System.currentTimeMillis();
            System.out.println("采集耗时：" + (et - st) + "ms");
            //默认Options：    报错、未获取到采集结果
            //显示禁用css:  报错、未获取到采集结果
            //显示禁用css、js:   采集成功、耗时：2993ms
            //显示禁用css、开启js: 报错、未获取到采集结果
            //显示禁用css、开启js、ajax:    报错、未获取到采集结果
            //显示禁用js报错、statuscode、禁用axtivex、禁用css、开启js、ajax:  采集成功、耗时：26822ms
            //显示禁用js报错、statuscode、禁用css、开启js、ajax:    采集成功、耗时：30552ms
            //显示开启css、js:   采集成功、耗时：37894ms
            //显示禁用css、js:   采集成功、耗时：3076ms
            //显示禁用js报错、statuscode、禁用axtivex、禁用css、禁用js:  采集成功、耗时：3100ms\3047ms

            //HtmlUnit优先使用XPath选择器，能禁用js就禁用（不然页面加载时间长），
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sample0301(){
        String url = "https://blog.csdn.net/yanghaolong/article/details/86680282";
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setTimeout(30000);

            String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" >\n" +
                    "<html>\n" +
                    "\t<head>\n" +
                    "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" >\n" +
                    "\t\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "\t\t<title>&#21160;&#24577;&#32593;</title>\n" +
                    "\t</head>\n" +
                    "<body>\n" +
                    "</body></html>";

            //step01: 获取网页字节码及请求状态码
            byte[] body = html.getBytes();
            int statusCode = 200;
            String statusMessage = "success";
            List<NameValuePair> responseHeaders = new ArrayList<>();
            WebResponseData  webResponseData = new WebResponseData(body, statusCode,
                    statusMessage, responseHeaders);

            WebRequest webRequest = new WebRequest(new URL("https://we5.fdasre.tk/"));
            WebResponse webResponse = new WebResponse(webResponseData, webRequest, 2000);

            //step02: 加载至窗口执行
            WebWindow webWindow = webClient.getCurrentWindow();
            HtmlPage htmlPage = (HtmlPage)webClient.getPageCreator().createPage(webResponse, webWindow);

            //step03: 获取网页标题
            String title = htmlPage.getTitleText();

            System.out.println(title);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sample0302(){
        String url = "https://blog.csdn.net/yanghaolong/article/details/86680282";
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setTimeout(30000);

            HtmlPage page = webClient.getPage(url);
            webClient.download(webClient.getCurrentWindow(), "blog",
                    page.getWebResponse().getWebRequest(), false, false, null);
            page.save(new File("D://load/ttt.txt"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sample04() {
//        String url = "https://blog.csdn.net/yanghaolong/article/details/86680282";
//        String url = "https://github.com/hao369/a/wiki/jyg";
//        String url = "http://1vxh93p.i78.integreat.com.mx/";
        String url = "http://j7u.vtee.gq/t-22-12";
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setTimeout(30000);
            //添加请求头方式一
//            webClient.addRequestHeader("", "");
            //添加请求头方式二
            WebRequest wr = new WebRequest(new URL(url));

//            =========================================
            //默认浏览器为Chrome，以下为默认请求头信息
//            Accept:*/*
//            Upgrade-Insecure-Requests:1
//            Accept-Encoding:gzip, deflate
//            Accept-Language:en-US
//            =========================================

//            wr.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            wr.setAdditionalHeader("Accept-Encoding", "gzip, deflate, sdch");
//            wr.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
//            wr.setAdditionalHeader("Cache-Control", "no-cache");
//            wr.setAdditionalHeader("Connection", "keep-alive");
//            wr.setAdditionalHeader("Pragma", "no-cache");
//            wr.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
//            wr.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");

            WebResponse webResponse = webClient.loadWebResponse(wr);
            System.out.println("============================【默认设置】====================================");
            System.out.println(webClient.getBrowserVersion());

            WebRequest webRequest = webResponse.getWebRequest();
            System.out.println("============================【请求配置】====================================");
            System.out.println(webRequest.getUrl());
            System.out.println(webRequest.getHttpMethod().name());
            System.out.println(webRequest.getCharset());
            System.out.println(webRequest.getCredentials());
            System.out.println(webRequest.getProxyHost());
            System.out.println(webRequest.getProxyPort());
            System.out.println("============================【请求头】====================================");
            Map<String, String> map = webRequest.getAdditionalHeaders();
            for(Map.Entry<String, String> entry: map.entrySet()){
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
            System.out.println("============================【请求参数】====================================");
            List<NameValuePair> parameters = webRequest.getRequestParameters();
            for(NameValuePair nameValuePair: parameters){
                System.out.println(nameValuePair.getName() + ":" + nameValuePair.getValue());
            }
            System.out.println("===========================【响应设置】=====================================");
            System.out.println(webResponse.getStatusCode() + " " + webResponse.getStatusMessage());
            System.out.println(webResponse.getContentCharset());
            System.out.println(webResponse.getContentCharsetOrNull());
            System.out.println(webResponse.getContentLength());
            System.out.println(webResponse.getContentType());
            System.out.println(webResponse.getLoadTime());
            System.out.println("===========================【响应头】=====================================");
            List<NameValuePair> headers = webResponse.getResponseHeaders();
            for(NameValuePair nameValuePair: headers){
                System.out.println(nameValuePair.getName() + ":" + nameValuePair.getValue());
            }
            System.out.println("============================【加载页面】====================================");
            Page page = webClient.getPageCreator().createPage(webResponse, webClient.getCurrentWindow());
            HtmlPage htmlPage = (HtmlPage) page;
//            String title = htmlPage.getTitleText();
//            HtmlHeading1 heading1 =  (HtmlHeading1) htmlPage.getByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[1]/h1").get(0);
//            String articleTitle = heading1.asText();
//            HtmlSpan span = htmlPage.getFirstByXPath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/span[1]");
//            String postTime = span.asText();
//            System.out.println("#Title:" + title);
//            System.out.println("#ArticleTitle:" + articleTitle);
//            System.out.println("#PostTime:" + postTime);
            System.out.println("============================【页面css链接】====================================");
            Set<String> cssHref = new HashSet<>();
            DomNodeList<DomElement> pageAllCsslinks = htmlPage.getElementsByTagName("link");
            System.out.println(">>>>>>>>>>>>>>>>>>CSS href:" + pageAllCsslinks.size());
            for(DomElement domElement: pageAllCsslinks){
                String href = domElement.getAttribute("href");
                if(StringUtils.isNotBlank(href) && href.endsWith(".css")){
                    cssHref.add(href);
                }
            }
            System.out.println(">>>>>>>>>>>>>>>>>>CSS Href:" + cssHref.size());
            System.out.println(cssHref);
            System.out.println("============================【页面js链接】====================================");
            Set<String> jsSrc = new HashSet<>();
            DomNodeList<DomElement> pageAllJslinks = htmlPage.getElementsByTagName("script");
            System.out.println(">>>>>>>>>>>>>>>>>>JS src:" + pageAllJslinks.size());
//            System.out.println("BaseUrl:" + htmlPage.getUrl());
            for(DomElement domElement: pageAllJslinks){
                String text = domElement.getTextContent();
                if(StringUtils.isBlank(text)){
                    String href = domElement.getAttribute("src");
                    if(StringUtils.isNotBlank(href)){
                        if(href.startsWith("//")){
                            href = "https:" + href;
                        }
                        jsSrc.add(href);
                    }
                }
            }
            System.out.println(">>>>>>>>>>>>>>>>>>JS Src:" + jsSrc.size());
            System.out.println(jsSrc);
            System.out.println("============================【页面a链接】====================================");
            Set<String> aHref = new HashSet<>();
            DomNodeList<DomElement> pageAllAnherlinks = htmlPage.getElementsByTagName("a");
            System.out.println(">>>>>>>>>>>>>>>>>>a href:" + pageAllAnherlinks.size());
            for(DomElement domElement: pageAllAnherlinks){
                String href = domElement.getAttribute("href");
                if(StringUtils.isNotBlank(href) && (href.startsWith("http://")||(href.startsWith("https://")))){
                    aHref.add(href);
                }
            }
            System.out.println(">>>>>>>>>>>>>>>>>>A Link:" + aHref.size());
            System.out.println(aHref);
            System.out.println("============================【页面img链接】====================================");
            Set<String> imgSrc = new HashSet<>();
            DomNodeList<DomElement> pageAllImagelinks = htmlPage.getElementsByTagName("img");
            System.out.println(">>>>>>>>>>>>>>>>>>Raw Img link:" + pageAllImagelinks.size());
            for(DomElement domElement: pageAllImagelinks){
                String href = domElement.getAttribute("src");
                if(StringUtils.isNotBlank(href)){
                    if(href.startsWith("//")){
                        href = "https:" + href;
                    }
                    imgSrc.add(href);
                }
            }
            System.out.println(">>>>>>>>>>>>>>>>>>Img Src:" + imgSrc.size());
            System.out.println(imgSrc);
            System.out.println("================================================================");

            /*InputStream in = webResponse.getContentAsStream();
            BufferedInputStream bis = new BufferedInputStream(in);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int current = 0;
            int size = 0;
            while((current = bis.read(data,0,data.length)) != -1){
                buffer.write(data,0,current);
                size += current;
            }
            byte htmlByte[] =  buffer.toByteArray();
            String contentEncoding = TextUtil.getPageEncoding(htmlByte);
            String html = new String(htmlByte, contentEncoding);
            System.out.println("=============================【HTML】===================================");
            System.out.println(html);
            System.out.println("================================================================");
            */
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        homePage();
//        homePage_Firefox();
//        homePage_proxy();

//        String imageUrl = "https://dc.geye8.gq/";
//        byte[] bytes = homePage_sample(imageUrl);
        /*try {
            System.out.println(MD5Util.getFileMD5(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
//        homePage_sample03("http://1ak2dji.k8u7.ko-kut.net/jt1/?id=2");
//        sample04();
        sample0302();
    }
}
