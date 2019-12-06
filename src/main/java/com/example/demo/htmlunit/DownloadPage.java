package com.example.demo.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description：下载网站主页
 *     使用HttpClient下载网站主页mainHtml，解析mainHtml中依赖的CSS、JS、image下载
 *     目录结构：
 *          XXXXdomain:
 *              index.html : 主页文件内容
 *              resource ： 链接目录
 *                  js
 *                  css
 *                  img
 *                  file
 *     例如：
 *          baidu.com
 *              index.html
 *              resources/js
 *              resources/css
 *              resources/img
 *
 *     链接路径：
 *          相对路径：  /image/xx.png   /css/css
 *          绝对路径： https://xxx/xx  http://xxx/xx
 *          复用协议： //www.sss.com/xxx
 *
 * Author；JinHuatao
 * Date: 2019/12/4 11:06
 */
public class DownloadPage {

    private static Logger LOG = LoggerFactory.getLogger(DownloadPage.class);

    /**
     * 通用正则匹配
     *
     * @param text 正则匹配文本
     * @param regex 正则表达式
     * @param index 索引
     * */
    public static List<String> commonReg(final String text, final String regex, final int index){
        Pattern regPattern = Pattern.compile(regex);
        Matcher m = regPattern.matcher(text);
        List<String> list = new ArrayList<>();
        while (m.find()){
            list.add(m.group(index));
        }
        return list;
    }

    /**
     * 创建目录，并要进行删除以前的文件
     *
     * @param path
     * @return
     */
    public static boolean mkDir(String path) {
        if ("\\".equals(File.separator)) {
            path = path.replace("/", "\\");
        }
        if ("/".equals(File.separator)) {
            path = path.replace("\\", "/");
        }
        boolean falg = false;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            falg = true;
        }
        return falg;
    }

    /**
     * 将网页中的URL中包含在正则表达式中的字符处理成正常字符
     * */
    public static String replaceUrlRegex(final String url){
        StringBuilder sb = new StringBuilder(url);
        if(url.contains("?")){
            String tmpUrl = url;
            sb  = new StringBuilder();
            String[] tt = tmpUrl.split("\\?");
            for(int i = 0; i < tt.length; i++){
                sb.append(tt[i]);
                if(i != tt.length - 1){
                    sb.append("\\?");
                }
            }
        }
        if(url.contains(".")){
            String tmpUrl = sb.toString();
            sb  = new StringBuilder();
            String[] tt = tmpUrl.split("\\.");
            for(int i = 0; i < tt.length; i++){
                sb.append(tt[i]);
                if(i != tt.length - 1){
                    sb.append("\\.");
                }
            }
        }
        return sb.toString();
    }

    public static HttpGet initGetRequest(PageContext pageContext, String url) throws URISyntaxException {
        String tmpUrl = url.trim();
        URI uri = new URI(tmpUrl);
        HttpGet get = new HttpGet(uri);
        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        get.setHeader("Cache-Control", "no-cache");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Pragma", "no-cache");
        get.setHeader("Upgrade-Insecure-Requests", "1");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
        get.setHeader("Referer", pageContext.getReferer());
        return get;
    }

    private static void getFileStream(InputStream inputStream, File file, String filepath) throws IOException {
        boolean newFile = file.createNewFile();
        if(newFile){
            FileUtils.copyInputStreamToFile(inputStream, file);
        }
    }

    /**
     * 根据url路径获取文件保存路径, 只获取文件，不下载文件
     * */
    public static String getFileSavePath(PageContext pageContext, String url, String saveDir) {
        String fileName = null;
        try {
            HttpHost httpHost = new HttpHost("127.0.0.1", 1080);
            HttpGet get = initGetRequest(pageContext, url);
            RequestConfig requestConfig;
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(100000)
                    .setConnectTimeout(100000)
                    .setConnectionRequestTimeout(100000).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                    .setProxy(httpHost)
                    .build();
            get.setConfig(requestConfig);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            long stTime = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(get);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                String fileContentType = httpResponse.getEntity().getContentType().getValue();
                LOG.info("==========================" + fileContentType);
                String tmpLastSuffix = "." + fileContentType.substring(fileContentType.indexOf("/") + 1);
                if(tmpLastSuffix.contains(";")){
                    tmpLastSuffix = tmpLastSuffix.substring(0, tmpLastSuffix.indexOf(";"));
                }
                //此处需要根据具体类型做判断
                if(tmpLastSuffix.equals(".x-javascript") || tmpLastSuffix.equals(".javascript")){
                    tmpLastSuffix = ".js";
                }else if(tmpLastSuffix.equals(".plain")){
                    tmpLastSuffix = ".txt";
                }else if(tmpLastSuffix.equals(".octet-stream")){
                    LOG.error("暂时不支持该请求内容类型");
                    return null;
                }

                fileName = url.substring(url.lastIndexOf("/") + 1);
                if(!fileName.contains(".")){
                    fileName += tmpLastSuffix;
                }
                if(fileName.length() > 50 || fileName.contains("?") || fileName.contains("!") || fileName.contains("&")){
                    String tmpFileName = fileName;
                    fileName = System.currentTimeMillis() + tmpLastSuffix;
                    LOG.warn(tmpFileName + "包含特殊字符或长度太长，采用系统自命名:" + fileName);
                }
                String fileSavePath = saveDir + fileName;
                File file = new File(fileSavePath);
                InputStream inputStream = httpResponse.getEntity().getContent();
                long fileSize = httpResponse.getEntity().getContentLength();
                try {
                    if (file.exists()) {
                        long tmpSize = file.length();
                        if (fileSize != tmpSize) {
                            getFileStream(inputStream, file, fileSavePath);
                        }
                    } else {
                        getFileStream(inputStream, file, fileSavePath);
                    }
                } catch (Exception e) {
                    LOG.info("文件写入异常" + statusLine.getStatusCode());
                } finally {
                    inputStream.close();
                }
                long edTime = System.currentTimeMillis();
                LOG.info("下载耗时：" + (edTime - stTime) + "ms");
            } else {
                LOG.info("下载失败！下载结果码:" + statusLine.getStatusCode());
            }
        } catch (Exception e) {
            LOG.error("下载失败," + e);
        }
        return fileName;
    }

    public static void getHtmlByHtmlUnit(PageContext pageContext) {
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

            URL myUrl = new URL(pageContext.getRequestUrl());
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

            Set<Cookie> ss = webClient.getCookies(myUrl);
            pageContext.setCookieSet(webClient.getCookies(myUrl));
            pageContext.setHtml(htmlPage.asXml());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test01() throws IOException {
//        String file = "D:\\snapshot\\baidu.com\\index.html";
//        String mainUrl = "https://www.baidu.com/";
//        String mainProtocolHead = "https:";
//        Document document = Jsoup.parse(new File(file), "utf-8");
//        String html = document.html();

//        String mainUrl =  "https://www.csdn.net/";
//        String mainUrl_Sep =  "https://www.csdn.net";
//        String mainProtocolHead = "https:";
//        String Domain = "csdn.net";

//        String mainUrl =  "https://cn.bing.com/";
//        String mainUrl_Sep =  "https://cn.bing.com";
//        String mainProtocolHead = "https:";
//        String Domain = "cn.bing.com";

        //TODO 置一个上下文对象，记录响应的Cookie、Referer等信息

//        String mainUrl =  "https://www.baidu.com/";
//        String mainUrl_Sep =  "https://www.baidu.com";
//        String mainProtocolHead = "https:";
//        String Domain = "baidu.com";

        String mainUrl =  "https://waiguo99a.herokuapp.com/?ZkpKcvCL=mfKc&_5wbwQXmWs=F8EnvpETnxz&zFRh=Rm9zXuy&yLHO9BmFCQO=uVNR1uq&nyML_=Hq";
        String mainUrl_Sep =  "https://waiguo99a.herokuapp.com";
        String mainProtocolHead = "https:";
        String domain = "waiguo99a.herokuapp.com";
        PageContext pageContext = new PageContext();
        pageContext.setRequestUrl(mainUrl);
        pageContext.setDomain(domain);
        pageContext.setRequestHost(mainUrl_Sep);
        pageContext.setProtocolHead(mainProtocolHead);
        pageContext.setReferer(mainUrl);

        //TODO 使用HtmlUnit拿主网页(复杂网页还是下载不了)
//        Document document = Jsoup.connect(mainUrl).get();
//        String html = document.html();

        getHtmlByHtmlUnit(pageContext);

        System.out.println("======================================");
        System.out.println("创建保存目录");
        //根据域名创建目录
        String mainDir = PageConfig.MAIN_DIR + pageContext.getDomain() + "/";
        String cssDir = mainDir + PageConfig.RESOURCE_CSS_DIR;
        String jsDir = mainDir + PageConfig.RESOURCE_JS_DIR;
        String imgDir = mainDir + PageConfig.RESOURCE_IMG_DIR;
        pageContext.setMainDir(mainDir);
        pageContext.setCssDir(cssDir);
        pageContext.setJsDir(jsDir);
        pageContext.setImgDir(imgDir);

        mkDir(mainDir);
        System.out.println("主目录:" + mainDir);
        //下载html文件，缓存本地
        String mif = mainDir + PageConfig.INDEX_HTML;
        if(pageContext.getHtml() != null){
            PrintWriter pw = new PrintWriter(new File(mif));
            pw.write(pageContext.getHtml());
            pw.flush();
            pw.close();
        }else {
            return;
        }


        //TODO 提取的链接有问题，不是CSS的文件太多（必须以.css结尾）
        mkDir(cssDir);
        System.out.println("css目录:" + cssDir);
        //下载css文件，写入该目录
        System.out.println("======================================");
        System.out.println("===============提取所有的其他(css等)下载链接===============");
        List<String> linkTag = commonReg(pageContext.getHtml(), PageConfig.TAG_LINK_REG, 0);
        StringBuilder linkText = new StringBuilder();
        for(String link:linkTag){
            linkText.append(link);
            linkText.append("\n\r");
        }
        String link = linkText.toString();
        List<String> linkOuterHref = commonReg(link, PageConfig.ATTR_HREF_OUTER_REG, 2);
        Set<String> linkHref = new HashSet<>();
        linkHref.addAll(linkOuterHref);
        System.out.println("需要下载其他文件数量【包含//开头的链接】：" + linkHref.size());
        for(String href:linkHref){
            String tmpUrl = href;
//            System.out.println(tmpUrl);
            if(href == null){
                System.out.println("CSS链接为空");
                continue;
            }
            if(href.startsWith("//")){
                tmpUrl = mainProtocolHead + href;
            }else if(href.startsWith("/")){
                tmpUrl = mainUrl_Sep + href;
            }
            if(tmpUrl.contains("&amp;")){
                tmpUrl = tmpUrl.replaceAll("&amp;", "&");
            }
            //检查css中是否有url，如果有，需要提取下载，更换url。
            String fileName = getFileSavePath(pageContext, tmpUrl, cssDir);

            if(StringUtils.isNotBlank(fileName) && fileName.endsWith(".css")){
                String fileSavePath = cssDir + fileName;
                replaceCssFile(pageContext, fileSavePath);
            }

            if(StringUtils.isNotBlank(fileName)){
                String newHref = PageConfig.RESOURCE_CSS_DIR + fileName;
                String regexHref = replaceUrlRegex(href);
                String html = pageContext.getHtml().replaceAll(regexHref, newHref);
                pageContext.setHtml(html);
            }else {
                LOG.warn("{} 未下载成功", href);
            }
        }


        mkDir(jsDir);
        System.out.println("js目录:" + jsDir);
        //下载js文件，写入该目录
        List<String> scriptTag = commonReg(pageContext.getHtml(), PageConfig.TAG_SCRIPT_REG, 0);
        StringBuilder scriptText = new StringBuilder();
        for(String jsLink:scriptTag){
            scriptText.append(jsLink);
            scriptText.append("\n\r");
        }
        String script = scriptText.toString();
        List<String> scriptSrc = commonReg(script, PageConfig.ATTR_SRC_REG, 2);
        List<String> scriptDataSrc = commonReg(script, PageConfig.ATTR_DATA_SRC_REG, 2);
        Set<String> scriptHref = new HashSet<>();
        scriptHref.addAll(scriptSrc);
        scriptHref.addAll(scriptDataSrc);
        System.out.println("需要下载JS文件数量：" + scriptHref.size());
        for(String jsLink:scriptHref){
            String tmpUrl = jsLink;
//            System.out.println(tmpUrl);
            if(jsLink == null){
                System.out.println("JS链接为空");
                continue;
            }
            if(jsLink.startsWith("//")){
                tmpUrl = mainProtocolHead + jsLink;
            }else if(jsLink.startsWith("/")){
                tmpUrl = mainUrl_Sep + jsLink;
            }
            if(tmpUrl.contains("&amp;")){
                tmpUrl = tmpUrl.replaceAll("&amp;", "&");
            }
            String fileName = getFileSavePath(pageContext, tmpUrl, jsDir);
            if(StringUtils.isNotBlank(fileName)){
                String newHref = PageConfig.RESOURCE_JS_DIR + fileName;
                String regexHref = replaceUrlRegex(jsLink);
                String html = pageContext.getHtml().replaceAll(regexHref, newHref);
                pageContext.setHtml(html);
            }else {
                LOG.warn("{} 未下载成功", jsLink);
            }
        }

        mkDir(imgDir);
        System.out.println("img目录:" + imgDir);
        //下载img文件，写入该目录
        System.out.println("===============提取所有的Img下载链接===============");
        List<String> imgTag = commonReg(pageContext.getHtml(), PageConfig.TAG_IMG_REG, 0);
        StringBuilder imgText = new StringBuilder();
        for(String imgLink:imgTag){
            imgText.append(imgLink);
            imgText.append("\n\r");
        }
        String img = imgText.toString();
        List<String> imgSrc = commonReg(img, PageConfig.ATTR_SRC_REG, 2);
        List<String> imgDataSrc = commonReg(img, PageConfig.ATTR_DATA_SRC_REG, 2);
        Set<String> imgHref = new HashSet<>();
        imgHref.addAll(imgSrc);
        imgHref.addAll(imgDataSrc);
        System.out.println("需要下载Img文件数量【包含//开头的链接】：" + imgHref.size());
        for(String imgLink:imgHref){
            String tmpUrl = imgLink;
//            System.out.println(tmpUrl);
            if(imgLink == null){
                System.out.println("图片链接为空");
                continue;
            }
            if(imgLink.startsWith("//")){
                tmpUrl = mainProtocolHead + imgLink;
            }else if(imgLink.startsWith("/")){
                tmpUrl = mainUrl_Sep + imgLink;
            }
            if(tmpUrl.contains("&amp;")){
                tmpUrl = tmpUrl.replaceAll("&amp;", "&");
            }
            String fileName = getFileSavePath(pageContext, tmpUrl, imgDir);
            if(StringUtils.isNotBlank(fileName)){
                String newHref = PageConfig.RESOURCE_IMG_DIR + fileName;
                String regexHref = replaceUrlRegex(imgLink);
                String html = pageContext.getHtml().replaceAll(regexHref, newHref);
                pageContext.setHtml(html);
            }else {
                LOG.warn("{} 未下载成功", imgLink);
            }
        }

        //将本地缓存的html写入主目录 index.html
        String mainIndexFile = mainDir + "index.html";
        PrintWriter printWriter = new PrintWriter(new File(mainIndexFile));
        printWriter.write(pageContext.getHtml());
        printWriter.flush();
        printWriter.close();
        System.out.println("======================================");
    }

    public static void replaceCssFile(PageContext pageContext, String cssFilePath){
        try {
            String css = FileUtils.readFileToString(new File(cssFilePath), "utf-8");
            List<String> cssUrlList = commonReg(css, PageConfig.CSS_FILE_URL_REG, 1);
            System.out.println("CSS 文件中URL数量：" + cssUrlList.size());
            if(!cssUrlList.isEmpty()){
                for(String cssUrl:cssUrlList){
                    String tmpUrl = cssUrl;
                    if(cssUrl == null){
                        System.out.println("css中URL链接为空");
                        continue;
                    }
                    if(cssUrl.startsWith("/")){
                        tmpUrl = pageContext.getRequestHost() + tmpUrl;
                    }
                    if(tmpUrl.contains("&amp;")){
                        tmpUrl = tmpUrl.replaceAll("&amp;", "&");
                    }
                    String fileName = getFileSavePath(pageContext, tmpUrl, pageContext.getImgDir());
                    if(StringUtils.isNotBlank(fileName)){
                        String newHref = PageConfig.RESOURCE_IMG_DIR + fileName;
                        newHref = newHref.replaceAll(PageConfig.RESOURCE, PageConfig.DOUBLE_DOT);//相对路径
                        String regexHref = replaceUrlRegex(cssUrl);
                        css = css.replaceAll(regexHref, newHref);
                        PrintWriter printWriter = new PrintWriter(new File(cssFilePath));
                        printWriter.write(css);
                        printWriter.flush();
                        printWriter.close();
                    }else {
                        LOG.warn("{} 未下载成功", cssUrl);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        try {
            test01();
        } catch (IOException e) {
            e.printStackTrace();
        }

      /*  File file = new File("E:\\snapshot\\waiguo99a.herokuapp.com\\resources\\css\\1575538308176.css");
        try {
            String css = FileUtils.readFileToString(file, "utf-8");
            List<String> ll = commonReg(css, PageConfig.CSS_FILE_URL_REG, 1);
            for(String st: ll){
                System.out.println(st);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        /*String fileContentType = "application/x-javascript";
        String tmpLastSuffix = "." + fileContentType.substring(fileContentType.indexOf("/") + 1);
        if(tmpLastSuffix.contains(";")){
            tmpLastSuffix = tmpLastSuffix.substring(0, tmpLastSuffix.indexOf(";"));
        }
        System.out.println(tmpLastSuffix);*/

//        String cssDir = "D:/snapshot/";
//        String tmpUrl = "https://cn.bing.com/th?id=OHR.CanadaTreeFarm_ZH-CN6478268657_1920x1080.jpg&amp;rf=LaDigue_1920x1080.jpg&amp;pid=hp";
//        String fileName = getFileSavePath(tmpUrl, cssDir);
//        System.out.println(fileName);
        //下载没问题
        //TODO 替换
        // /rs/2T/kv/cj,nj/b2a824f7/b8c6c09d.js

    }
}
