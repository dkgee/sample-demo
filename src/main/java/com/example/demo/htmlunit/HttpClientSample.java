package com.example.demo.htmlunit;

import com.example.demo.util.MD5Util;
import com.example.demo.util.TextUtil;
import com.gargoylesoftware.htmlunit.util.EncodingSniffer;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/27 18:33
 */
public class HttpClientSample {

    private static Logger LOG = LoggerFactory.getLogger(HttpClientSample.class);

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

    public static void sample01(String url) throws URISyntaxException, IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(100000)
                .setConnectTimeout(100000)
                .setConnectionRequestTimeout(100000).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        //TODO step00: 本次任务执行记录对象
        HttpGet get = initGetRequest(url);
        //TODO step01: 请求头初始化配置对象
        get.setConfig(requestConfig);
        get.setHeader("Origin", "http://z1w54dhm.fta4a.fuxy.info");
        get.setHeader("Referer", "http://z1w54dhm.fta4a.fuxy.info/");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(get);
        //TODO step02: 响应头过滤对象
        System.out.println("=============响应头过滤对象处理=============");
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String reason = httpResponse.getStatusLine().getReasonPhrase();
//        System.out.println("StatusCode:" + statusCode);
//        System.out.println("=============协议过滤=============");
        String protocol = httpResponse.getStatusLine().getProtocolVersion().getProtocol();
        int protocolMajor = httpResponse.getStatusLine().getProtocolVersion().getMajor();
        int protocolMinor = httpResponse.getStatusLine().getProtocolVersion().getMinor();
        System.out.println(protocol + "/" + protocolMajor + "." + protocolMinor + " " + statusCode + " "  + reason);

        System.out.println("=============Header过滤=============");
        Header[] headers = httpResponse.getAllHeaders();
        for(Header header: headers){
            System.out.println(header.getName() + ":" + header.getValue());
        }

        //TODO step03: 响应体限制过滤
        System.out.println("=============响应体限制过滤对象处理=============");
        String html = "";
        if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            String fileContentType = httpResponse.getEntity().getContentType().getValue();
            if(fileContentType.contains("text/html")){
                InputStream is = httpResponse.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int current = 0;
                int size = 0;
                while((current = bis.read(data,0,data.length)) != -1){
                    buffer.write(data,0,current);
                    size += current;
                    //网页内存最大为5M，超过限制则退出
//                    if(size > 5242880){
//                        return html;
//                    }
                }
                byte htmlByte[] =  buffer.toByteArray();
                String contentEncoding = TextUtil.getPageEncoding(htmlByte);
                html = new String(htmlByte, contentEncoding);
            }
        }
        //TODO step04: 响应结果限制过滤
        System.out.println("=============对HTML内容进行处理=============");
        System.out.println(html);
        System.out.println("============================================");
        //TODO step05: 最终结果保存对象
        System.out.println("=============持久化目标HTML内容=============");
        System.out.println("============================================");

    }

    public static byte[] sample02(String imageUrl) throws URISyntaxException, IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(100000)
                .setConnectTimeout(100000)
                .setConnectionRequestTimeout(100000).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        HttpGet get = initGetRequest(imageUrl);
        get.setConfig(requestConfig);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(get);
        if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            String fileContentType = httpResponse.getEntity().getContentType().getValue();
            if(fileContentType.contains("image")){
                InputStream is = httpResponse.getEntity().getContent();
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
            }
        }
        return null;
    }

    public static void sample03(String url) throws URISyntaxException, IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(100000)
                .setConnectTimeout(100000)
                .setConnectionRequestTimeout(100000).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        HttpGet get = initGetRequest(url);
        get.setConfig(requestConfig);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(get);
        if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            InputStream is = httpResponse.getEntity().getContent();
            Header[] headers = httpResponse.getAllHeaders();
            List<NameValuePair> headerList = new ArrayList<>();
            for(Header header: headers) {
                NameValuePair nameValuePair = new NameValuePair(header.getName(), header.getValue());
                headerList.add(nameValuePair);
            }
            Charset charset = EncodingSniffer.sniffHtmlEncoding(headerList ,is);
            System.out.println(charset.displayName());
        }
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
            HttpHost httpHost = new HttpHost("127.0.0.1", 1080);
            HttpGet get = initGetRequest(url);
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

    public static void main(String[] args) {
        try {
            //"https://dc.geye8.gq/"
            String url = "https://dc.geye8.gq/";
            sample03(url);
        }catch (Exception e){
            e.printStackTrace();
        }

//        String imageUrl = "https://avatar.csdnimg.cn/F/8/0/1_xiaoa_m.jpg";//652a8141d6d5c782f40344f4515ddeac
        //"https://aea2.vcdfw.ga/?0f=K5nCNJ&ty6cfr=CUqHED7H&J9a=0xFR8q&27CxFL48_ii=XOCH&-d4R0t=lWWFBwIUU&kvEdbZU=u&_dxf=img";
//        String imageUrl = "https://avatar.csdnimg.cn/3/1/B/1_ycagri.jpg";//a4b8421a1bfffe731ef59ce5c83e9c56  a4b8421a1bfffe731ef59ce5c83e9c56
        String imageUrl = "https://waiguo99a.herokuapp.com/?RqpLndCO=haAQ&cm=232K1J&T2Ov52spz=zWnAUIw&uqY=gCAmNcCOG55&3VK5FM2zzeX=ws&Ve5=5z&6&_cxw=img";
        String savePath = "/jxwz/";
        /*try {
            byte[] bytes = sample02(imageUrl);
            System.out.println(MD5Util.getFileMD5(bytes));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        getFileSavePath(imageUrl, savePath);

    }
}
