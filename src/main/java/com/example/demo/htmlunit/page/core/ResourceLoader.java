package com.example.demo.htmlunit.page.core;

import com.example.demo.htmlunit.page.config.LoaderConfig;
import com.example.demo.htmlunit.page.entity.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description：资源加载线程
 * Author；JinHuatao
 * Date: 2019/12/6 11:13
 */
public class ResourceLoader implements Resource{

    private static Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    private Curi curi;

    private LoaderContext loaderContext;

    private HttpClient httpClient;

    public ResourceLoader(Curi curi, LoaderContext loaderContext, HttpClient httpClient) {
        this.curi = curi;
        this.loaderContext = loaderContext;
        this.httpClient = httpClient;
    }

    @Override
    public LoaderResult call() throws Exception {
        //传入的URL、下载该资源，并保存到指定目录下
        LoaderResult loaderResult = new LoaderResult();
        downloadFile(loaderContext, curi);
        if(StringUtils.isNotBlank(curi.getFileName())){
            String newHref;
            switch (curi.getContextType()){
                case js:
                    if(curi.getDepth() == 2){
                        newHref = LoaderConfig.RESOURCE_RELATIVE_JS_DIR + curi.getFileName();
                    }else {
                        newHref = LoaderConfig.RESOURCE_JS_DIR + curi.getFileName();
                    }
                    break;
                case img:
                    if(curi.getDepth() == 2){
                        newHref = LoaderConfig.RESOURCE_RELATIVE_IMG_DIR + curi.getFileName();
                    }else {
                        newHref = LoaderConfig.RESOURCE_IMG_DIR + curi.getFileName();
                    }
                    break;
                case css:
                default:
                    if(curi.getDepth() == 2){
                        newHref = LoaderConfig.RESOURCE_RELATIVE_CSS_DIR + curi.getFileName();
                    }else {
                        newHref = LoaderConfig.RESOURCE_CSS_DIR + curi.getFileName();
                    }
                    break;
            }
            String regexHref = HtmlParser.replaceUrlRegex(curi.getRawUrl());
            curi.setRegexHref(regexHref);
            curi.setReplaceHref(newHref);
            loaderContext.addRegexReplaceFile(curi.getSourceFilePath(), regexHref, newHref);
            loaderResult.setResult(true);
            loaderResult.setCuri(curi);
        }else {
            loaderResult.setResult(false);
            logger.warn("{} 未下载成功", curi.getUrl());
        }

        return loaderResult;
    }

    //静态方法与非静态方法别
    public HttpGet initGetRequest(LoaderContext loaderContext, String url) throws URISyntaxException {
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
        get.setHeader("Referer", loaderContext.getReferer());
        return get;
    }

    private void getFileStream(InputStream inputStream, File file) throws IOException {
        boolean newFile = file.createNewFile();
        if(newFile){
            FileUtils.copyInputStreamToFile(inputStream, file);
        }
    }

    /**
     * 根据url路径获取文件保存路径, 只获取文件，不下载文件
     * */
    public void downloadFile(LoaderContext loaderContext, Curi curi) {
        String url = curi.getUrl();
        try {
            HttpHost httpHost = new HttpHost("127.0.0.1", 1080);
            HttpGet httpRequest = initGetRequest(loaderContext, url);
            RequestConfig requestConfig;
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(100000)
                    .setConnectTimeout(100000)
                    .setConnectionRequestTimeout(100000).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                    .setProxy(httpHost)
                    .build();
            httpRequest.setConfig(requestConfig);

            CookieStore cookieStore = new BasicCookieStore();
            if(!loaderContext.getCookieSet().isEmpty()){
                for(Cookie cookie: loaderContext.getCookieSet()){
                    cookieStore.addCookie(cookie);
                }
            }

            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            long stTime = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(httpRequest, context);

            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                String fileContentType = httpResponse.getEntity().getContentType().getValue();
                logger.info("==========================" + fileContentType);
                String tmpLastSuffix = "." + fileContentType.substring(fileContentType.indexOf("/") + 1);
                if(tmpLastSuffix.contains(";")){
                    tmpLastSuffix = tmpLastSuffix.substring(0, tmpLastSuffix.indexOf(";"));
                }
                //此处需要根据具体类型做判断
                if(tmpLastSuffix.equals(".x-javascript") || tmpLastSuffix.equals(".javascript")){
                    tmpLastSuffix = ".js";
                    curi.setContextType(ContextType.js);
                }else if(tmpLastSuffix.equals(".plain")){
                    tmpLastSuffix = ".txt";
                    curi.setContextType(ContextType.css);
                }else if(tmpLastSuffix.equals(".css")){
                    curi.setContextType(ContextType.css);
                }else if(tmpLastSuffix.equals(".png") || tmpLastSuffix.equals(".jpg")
                        || tmpLastSuffix.equals(".jpeg") || tmpLastSuffix.equals(".gif") || tmpLastSuffix.equals(".ico")){
                    curi.setContextType(ContextType.img);
                }else if(tmpLastSuffix.equals(".octet-stream")){
                    logger.error("暂时不支持该请求内容类型");
                    return;
                }

                String fileName = url.substring(url.lastIndexOf("/") + 1);
                if(!fileName.contains(".")){
                    fileName += tmpLastSuffix;
                }
                if(fileName.length() > 50 || fileName.contains("?") || fileName.contains("!") || fileName.contains("&")){
                    String tmpFileName = fileName;
                    fileName = System.currentTimeMillis() + tmpLastSuffix;
                    logger.warn(tmpFileName + "包含特殊字符或长度太长，采用系统自命名:" + fileName);
                }

                String fileSavePath = curi.getSaveDir() + fileName;
                curi.setFileName(fileName);
                curi.setSavePath(fileSavePath);

                File file = new File(fileSavePath);
                InputStream inputStream = httpResponse.getEntity().getContent();
                long fileSize = httpResponse.getEntity().getContentLength();
                try {
                    if (file.exists()) {
                        long tmpSize = file.length();
                        if (fileSize != tmpSize) {
                            getFileStream(inputStream, file);
                        }
                    } else {
                        getFileStream(inputStream, file);
                    }
                } catch (Exception e) {
                    logger.info("文件写入异常" + statusLine.getStatusCode());
                } finally {
                    inputStream.close();
                }
                long edTime = System.currentTimeMillis();
                logger.info("下载耗时：" + (edTime - stTime) + "ms");
            } else {
                logger.info("下载失败！下载结果码:" + statusLine.getStatusCode());
            }
            //请求头
//            System.out.println("============Request===========");
//            for(Header header: httpRequest.getAllHeaders()){
//                System.out.println(header.getName() + ":" + header.getValue());
//            }
            //响应头
//            System.out.println("============Response===========");
//            for(Header header: httpResponse.getAllHeaders()){
//                System.out.println(header.getName() + ":" + header.getValue());
//            }
//            System.out.println("=======================");
        } catch (Exception e) {
            //请求头打印出来
            logger.error("下载失败," + e);
            e.printStackTrace();
        }
    }
}
