package com.example.demo.htmlunit;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private static String TAG_LINK_REG = "(<link[\\s]+\\w+[^>]+>)";

//    private static String TAG_SCRIPT_REG = "(<script[\\s]+\\w+[^>]+\\.js[\\s]{0,1}\"[\\s]{0,1}>)";

    private static String TAG_SCRIPT_REG = "(<script[\\s]+\\w+[^>]+>)";

    private static String TAG_IMG_REG = "(<img[\\s]+\\w+[^>]+>)";

//    private static String ATTR_HREF_INNER_REG = "(href=([\\w|.|/]+)[\\s]{0,1})";//？

    private static String ATTR_HREF_OUTER_REG = "(href=[\\s]{0,1}\"(.+?)\")";

    private static String ATTR_SRC_REG = "(src=[\\s]{0,1}\"(.+?)\")";

    private static String ATTR_DATA_SRC_REG = "(data-src=[\\s]{0,1}\"(.+?)\")";

//    private static String MAIN_DIR = "D:/snapshot/pptang.com/";
    private static String MAIN_DIR = "D:/snapshot/";
    private static String RESOURCE_CSS_DIR = "resources/css/";
    private static String RESOURCE_JS_DIR = "resources/js/";
    private static String RESOURCE_IMG_DIR = "resources/img/";

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

    public static HttpGet initGetRequest(String url) throws URISyntaxException {
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
    public static String getFileSavePath(String url, String saveDir) {
        String fileName = null;
        try {
//            HttpHost httpHost = new HttpHost("127.0.0.1", 1080);
            HttpGet get = initGetRequest(url);
            RequestConfig requestConfig;
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(100000)
                    .setConnectTimeout(100000)
                    .setConnectionRequestTimeout(100000).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
//                    .setProxy(httpHost)
                    .build();
            get.setConfig(requestConfig);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            long stTime = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(get);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                String fileContentType = httpResponse.getEntity().getContentType().getValue();
                LOG.info("==========================" + fileContentType);
                String tmpLastSuffix = "";
                if(fileContentType.contains("image")){
                    tmpLastSuffix = ".png";
                    if(fileContentType.contains("/")){
                        tmpLastSuffix = "." + fileContentType.substring(fileContentType.indexOf("/") + 1);
                    }
                }else if(fileContentType.contains("android")){
                    tmpLastSuffix = ".apk";
                }

                fileName = url.substring(url.lastIndexOf("/") + 1);
                if(!fileName.contains(".")){
                    fileName += tmpLastSuffix;
                }
                if(fileName.length() > 50 || fileName.contains("?") || fileName.contains("!") || fileName.contains("&")){
                    String tmpFileName = fileName;
                    fileName = System.currentTimeMillis() + "";
                    if(tmpFileName.contains(".")){
                        int index = tmpFileName.lastIndexOf(".");
                        if(tmpFileName.contains("?")){
                            int lastindex = tmpFileName.indexOf("?");
                            if(index < lastindex){
                                tmpLastSuffix = tmpFileName.substring(index,lastindex);
                            }else{
                                tmpLastSuffix = tmpFileName.substring(index);
                            }
                        }else{
                            tmpLastSuffix = tmpFileName.substring(index);
                        }
                    }
                    fileName += tmpLastSuffix;
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

    public static void test01() throws IOException {
//        String file = "D:\\snapshot\\baidu.com\\index.html";
//        String mainUrl = "https://www.baidu.com/";
//        String mainProtocolHead = "https:";
//        Document document = Jsoup.parse(new File(file), "utf-8");
//        String html = document.html();

        String mainUrl =  "https://www.csdn.net/";
        String mainUrl_Sep =  "https://www.csdn.net";
        String mainProtocolHead = "https:";
        String Domain = "csdn.net";

     /*   String mainUrl =  "https://www.baidu.com/";
        String mainProtocolHead = "https:";
        String Domain = "baidu.com";*/

        Document document = Jsoup.connect(mainUrl).get();
        String html = document.html();

        System.out.println("======================================");
        System.out.println("创建保存目录");
        //根据域名创建目录
//        String mainDir = MAIN_DIR;
        String mainDir = MAIN_DIR + Domain + "/";
        String cssDir = mainDir + RESOURCE_CSS_DIR;
        String jsDir = mainDir + RESOURCE_JS_DIR;
        String imgDir = mainDir + RESOURCE_IMG_DIR;
        mkDir(mainDir);
        System.out.println("主目录:" + mainDir);
        //下载html文件，缓存本地

        mkDir(cssDir);
        System.out.println("css目录:" + cssDir);
        //下载css文件，写入该目录
        System.out.println("======================================");
        System.out.println("===============提取所有的其他(css等)下载链接===============");
        List<String> linkTag = commonReg(html, TAG_LINK_REG, 0);
        StringBuilder linkText = new StringBuilder();
        for(String link:linkTag){
            linkText.append(link);
            linkText.append("\n\r");
        }
        String link = linkText.toString();
        List<String> linkOuterHref = commonReg(link, ATTR_HREF_OUTER_REG, 2);
        Set<String> linkHref = new HashSet<>();
        linkHref.addAll(linkOuterHref);
        System.out.println("需要下载其他文件数量【包含//开头的链接】：" + linkHref.size());
        for(String href:linkHref){
            String tmpUrl = href;
//            System.out.println(tmpUrl);
            if(href.startsWith("//")){
                tmpUrl = mainProtocolHead + href;
            }else if(href.startsWith("/")){
                tmpUrl = mainUrl_Sep + href;
            }
            String fileName = getFileSavePath(tmpUrl, cssDir);
            String newHref = RESOURCE_CSS_DIR + fileName;
            html = html.replaceAll(href, newHref);
        }


        mkDir(jsDir);
        System.out.println("js目录:" + jsDir);
        //下载js文件，写入该目录
        List<String> scriptTag = commonReg(html, TAG_SCRIPT_REG, 0);
        StringBuilder scriptText = new StringBuilder();
        for(String jsLink:scriptTag){
            scriptText.append(jsLink);
            scriptText.append("\n\r");
        }
        String script = scriptText.toString();
        List<String> scriptSrc = commonReg(script, ATTR_SRC_REG, 2);
        List<String> scriptDataSrc = commonReg(script, ATTR_DATA_SRC_REG, 2);
        Set<String> scriptHref = new HashSet<>();
        scriptHref.addAll(scriptSrc);
        scriptHref.addAll(scriptDataSrc);
        System.out.println("需要下载JS文件数量：" + scriptHref.size());
        for(String jsLink:scriptHref){
            String tmpUrl = jsLink;
            System.out.println(tmpUrl);
            if(jsLink.startsWith("//")){
                tmpUrl = mainProtocolHead + jsLink;
            }else if(jsLink.startsWith("/")){
                tmpUrl = mainUrl_Sep + jsLink;
            }
            String fileName = getFileSavePath(tmpUrl, jsDir);
            String newHref = RESOURCE_JS_DIR + fileName;
            //TODO 此处路径替换有问题(每个都应该被替换掉)
            html = html.replaceAll(jsLink, newHref);
        }

        mkDir(imgDir);
        System.out.println("img目录:" + imgDir);
        //下载img文件，写入该目录
        System.out.println("===============提取所有的Img下载链接===============");
        List<String> imgTag = commonReg(html, TAG_IMG_REG, 0);
        StringBuilder imgText = new StringBuilder();
        for(String imgLink:imgTag){
            imgText.append(imgLink);
            imgText.append("\n\r");
        }
        String img = imgText.toString();
        List<String> imgSrc = commonReg(img, ATTR_SRC_REG, 2);
        List<String> imgDataSrc = commonReg(img, ATTR_DATA_SRC_REG, 2);
        Set<String> imgHref = new HashSet<>();
        imgHref.addAll(imgSrc);
        imgHref.addAll(imgDataSrc);
        System.out.println("需要下载Img文件数量【包含//开头的链接】：" + imgHref.size());
        for(String imgLink:imgHref){
            String tmpUrl = imgLink;
//            System.out.println(tmpUrl);
            if(imgLink.startsWith("//")){
                tmpUrl = mainProtocolHead + imgLink;
            }else if(imgLink.startsWith("/")){
                tmpUrl = mainUrl_Sep + imgLink;
            }
            String fileName = getFileSavePath(tmpUrl, imgDir);
            String newHref = RESOURCE_IMG_DIR + fileName;
            html = html.replaceAll(imgLink, newHref);
        }

        //将本地缓存的html写入主目录 index.html
        String mainIndexFile = mainDir + "index.html";
        PrintWriter printWriter = new PrintWriter(new File(mainIndexFile));
        printWriter.write(html);
        printWriter.flush();
        printWriter.close();
        System.out.println("======================================");
    }





    public static void main(String[] args) {
        try {
            test01();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String cssDir = "D:/snapshot/pptang.com/resources/js/";
//        String tmpUrl = "https://dss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/js/lib/jquery-1.10.2_1c4228b8.js";
//        String fileName = getFileSavePath(tmpUrl, cssDir);
//        System.out.println(fileName);
    }









}
