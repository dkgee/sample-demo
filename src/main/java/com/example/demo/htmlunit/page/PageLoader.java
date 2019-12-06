package com.example.demo.htmlunit.page;

import com.example.demo.htmlunit.page.config.LoaderConfig;
import com.example.demo.htmlunit.page.core.ResourceLoader;
import com.example.demo.htmlunit.page.entity.*;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/12/6 11:22
 */
public class PageLoader extends Thread{

    private Map<String, Future<LoaderResult>> checkFinish = new ConcurrentHashMap<>();

    private volatile boolean shutdown;

    private String url;

    private String savePath;

    public PageLoader(String url, String savePath) {
        this.url = url;
        this.savePath = savePath;
    }

    public ThreadPoolTaskExecutor getThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("page-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public void run() {
        //step01: 创建页面下载线程池
        ThreadPoolTaskExecutor threadPoolTaskExecutor = getThreadPoolExecutor();

        //step02: 构建下载上线文
        LoaderContext loaderContext = new LoaderContext();

        Seed seed = new Seed(url, savePath);
        loaderContext.setReferer(url);

        getHtmlByHtmlUnit(seed, loaderContext);

        if(loaderContext.getHtml() != null){
           printIndexHtml(seed, loaderContext);
        }else {
            return;
        }

        List<Curi> curiList = new ArrayList<>();

        List<Curi> linkList = getHtmlLinkTag(seed, loaderContext);
        List<Curi> scriptList = getHtmlScriptTag(seed, loaderContext);
        List<Curi> imgList = getHtmlImgTag(seed, loaderContext);
        curiList.addAll(linkList);
        curiList.addAll(scriptList);
        curiList.addAll(imgList);

        //step03: 提交下载资源
        //多线程下载，需要控制下载速度
        for(Curi curi: curiList){
            Future<LoaderResult> future = threadPoolTaskExecutor.submit(new ResourceLoader(curi, loaderContext));
            checkFinish.put(curi.getUrl(), future);
        }

        while (true) {
            //检查资源是否下载完成
            checkFinish.entrySet().forEach((entry)->{
                if(entry.getValue().isDone()){
                    String tmpUrl = entry.getKey();
                    if(checkFinish.containsKey(tmpUrl)){
                        if(!checkFinish.get(tmpUrl).isDone()){
                            checkFinish.get(tmpUrl).cancel(true);
                        }
                        checkFinish.remove(tmpUrl);
                    }
                }
            });
            //检查资源是否下载完成
            if(checkFinish.isEmpty()){
                System.out.println("===================Link============================");
                System.out.println("link:" + seed.getCssCount() + ",js:" + seed.getJsCount() + ",img:" + seed.getImgCount());
                System.out.println("===================Link============================");
                printExtractLink(linkList);
                System.out.println("===================JS============================");
                printExtractLink(scriptList);
                System.out.println("===================IMG============================");
                printExtractLink(imgList);
                System.out.println("===============================================");
                System.out.println(seed.getUrl() + ",资源下载完毕");
                break;
            }else {
                try {
                    TimeUnit.SECONDS.sleep(2);//等待2s
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(seed.getUrl() + ",开始处理index.html。");
        Map<String, String> indexHtmlRegexReplace = loaderContext.getIndexHtmlRegexReplace();
        String html = loaderContext.getHtml();
        for(Map.Entry<String, String> entry: indexHtmlRegexReplace.entrySet()){
            String regexHref = entry.getKey();
            String newHref = entry.getValue();
            html = html.replaceAll(regexHref, newHref); //TODO 此处需要优化
        }
        loaderContext.setHtml(html);
        printIndexHtml(seed, loaderContext);
        System.out.println(seed.getUrl() + ",下载完成,关闭线程池");
        threadPoolTaskExecutor.shutdown();
    }

    public static void main(String[] args) throws FileNotFoundException {
        String url =  "https://waiguo99a.herokuapp.com/?ZkpKcvCL=mfKc&_5wbwQXmWs=F8EnvpETnxz&zFRh=Rm9zXuy&yLHO9BmFCQO=uVNR1uq&nyML_=Hq";
        String jxwzDir = "/jxwz/";
        PageLoader pageLoader = new PageLoader(url,jxwzDir);
        pageLoader.start();
    }

    public void printExtractLink(List<Curi> list){
        for(Curi curi:list){
            System.out.println(curi);
        }
    }

    public void printIndexHtml(Seed seed, LoaderContext loaderContext){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(seed.getSeedIndexPath()));
            pw.write(loaderContext.getHtml());
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(pw != null){
                pw.close();
            }
        }
    }

    public static List<Curi> getHtmlLinkTag(Seed seed, LoaderContext loaderContext){
        List<String> linkTag = HtmlParser.commonReg(loaderContext.getHtml(), LoaderConfig.TAG_LINK_REG, 0);

        StringBuilder linkText = new StringBuilder();
        for(String link:linkTag){
            linkText.append(link);
            linkText.append("\n\r");
        }
        String link = linkText.toString();
        List<String> linkOuterHref = HtmlParser.commonReg(link, LoaderConfig.ATTR_HREF_OUTER_REG, 2);
        Set<String> linkHref = new HashSet<>();
        linkHref.addAll(linkOuterHref);
        if(!linkTag.isEmpty()){
            seed.setCssCount(linkHref.size());
        }

        List<Curi> curiList = new ArrayList<>();
        for(String href:linkHref){
            String tmpUrl = href;
            if(href == null){
                System.out.println("CSS链接为空");
                continue;
            }
            Curi curi = new Curi();
            curi.setRawUrl(href);
            if(href.startsWith("//")){
                tmpUrl = seed.getProtocolHead() + href;
            }else if(href.startsWith("/")){
                tmpUrl = seed.getHostUrl() + href;
            }
            if(tmpUrl.contains("&amp;")){
                tmpUrl = tmpUrl.replaceAll("&amp;", "&");
            }
            curi.setUrl(tmpUrl);
            curi.setSaveDir(seed.getCssSaveDir());
            curi.setContextType(ContextType.css);
            curiList.add(curi);
        }
        return curiList;
    }

    public static List<Curi> getHtmlScriptTag(Seed seed, LoaderContext loaderContext){
        List<String> scriptTag = HtmlParser.commonReg(loaderContext.getHtml(), LoaderConfig.TAG_SCRIPT_REG, 0);
        StringBuilder scriptText = new StringBuilder();
        for(String jsLink:scriptTag){
            scriptText.append(jsLink);
            scriptText.append("\n\r");
        }
        String script = scriptText.toString();
        List<String> scriptSrc = HtmlParser.commonReg(script, LoaderConfig.ATTR_SRC_REG, 2);
        List<String> scriptDataSrc = HtmlParser.commonReg(script, LoaderConfig.ATTR_DATA_SRC_REG, 2);
        Set<String> scriptHref = new HashSet<>();
        scriptHref.addAll(scriptSrc);
        scriptHref.addAll(scriptDataSrc);
        if(!scriptHref.isEmpty()){
            seed.setJsCount(scriptHref.size());
        }
        List<Curi> curiList = new ArrayList<>();
        for(String jsLink:scriptHref){
            String tmpUrl = jsLink;
            if(jsLink == null){
                System.out.println("JS链接为空");
                continue;
            }
            Curi curi = new Curi();
            curi.setRawUrl(jsLink);
            if(jsLink.startsWith("//")){
                tmpUrl = seed.getProtocolHead() + jsLink;
            }else if(jsLink.startsWith("/")){
                tmpUrl = seed.getHostUrl() + jsLink;
            }
            if(tmpUrl.contains("&amp;")){
                tmpUrl = tmpUrl.replaceAll("&amp;", "&");
            }
            curi.setUrl(tmpUrl);
            curi.setSaveDir(seed.getJsSaveDir());
            curi.setContextType(ContextType.js);
            curiList.add(curi);
        }
        return curiList;
    }

    public static List<Curi> getHtmlImgTag(Seed seed, LoaderContext loaderContext){
        List<String> imgTag = HtmlParser.commonReg(loaderContext.getHtml(), LoaderConfig.TAG_IMG_REG, 0);
        StringBuilder imgText = new StringBuilder();
        for(String imgLink:imgTag){
            imgText.append(imgLink);
            imgText.append("\n\r");
        }
        String img = imgText.toString();
        List<String> imgSrc = HtmlParser.commonReg(img, LoaderConfig.ATTR_SRC_REG, 2);
        List<String> imgDataSrc = HtmlParser.commonReg(img, LoaderConfig.ATTR_DATA_SRC_REG, 2);
        Set<String> imgHref = new HashSet<>();
        imgHref.addAll(imgSrc);
        imgHref.addAll(imgDataSrc);
        if(!imgHref.isEmpty()){
            seed.setJsCount(imgHref.size());
        }
        List<Curi> curiList = new ArrayList<>();
        for(String imgLink:imgHref){
            String tmpUrl = imgLink;
            if(imgLink == null){
                System.out.println("图片链接为空");
                continue;
            }
            Curi curi = new Curi();
            curi.setRawUrl(imgLink);
            if(imgLink.startsWith("//")){
                tmpUrl = seed.getProtocolHead() + imgLink;
            }else if(imgLink.startsWith("/")){
                tmpUrl = seed.getHostUrl() + imgLink;
            }
            if(tmpUrl.contains("&amp;")){
                tmpUrl = tmpUrl.replaceAll("&amp;", "&");
            }

            curi.setUrl(tmpUrl);
            curi.setSaveDir(seed.getImgSaveDir());
            curi.setContextType(ContextType.img);
            curiList.add(curi);
        }
        return curiList;
    }

    public static void getHtmlByHtmlUnit(Seed seed, LoaderContext loaderContext) {
        String proxyhost = "127.0.0.1";
        int port = 1080;
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME, proxyhost, port)) {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setTimeout(20000);//设置“浏览器”的请求超时时间
            webClient.setJavaScriptTimeout(20000);//设置JS执行的超时时间
            webClient.getOptions().setThrowExceptionOnScriptError(false);//屏蔽日志
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//屏蔽日志

            URL myUrl = new URL(seed.getUrl());
            WebRequest wr = new WebRequest(myUrl);
            wr.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            wr.setAdditionalHeader("Accept-Encoding", "gzip, deflate, sdch");
            wr.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
            wr.setAdditionalHeader("Cache-Control", "no-cache");
            wr.setAdditionalHeader("Connection", "keep-alive");
            wr.setAdditionalHeader("Pragma", "no-cache");
            wr.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
            wr.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");

//            webClient.waitForBackgroundJavaScript(20000);//该方法阻塞线程

            //记录上下文Cookie
            WebResponse webResponse = webClient.loadWebResponse(wr);
            Page page = webClient.getPageCreator().createPage(webResponse, webClient.getCurrentWindow());
            HtmlPage htmlPage = (HtmlPage) page;

            Set<Cookie> htmlUnitCookie = webClient.getCookies(myUrl);
            for(Cookie cookie:htmlUnitCookie){
                loaderContext.addCookie(cookie.getName(), cookie.getValue(),
                        cookie.getDomain(), cookie.getPath(), cookie.getExpires());
            }

            loaderContext.setHtml(htmlPage.asXml());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
