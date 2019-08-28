package com.example.demo.htmlunit;

import com.example.demo.util.MD5Util;
import com.example.demo.util.TextUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/27 18:33
 */
public class HttpClientSample {

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

    public static void main(String[] args) {
//        try {
//            String url = "http://www.ecer.com/";
//            sample01(url);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        String imageUrl = "https://avatar.csdnimg.cn/F/8/0/1_xiaoa_m.jpg";//652a8141d6d5c782f40344f4515ddeac
        //"https://aea2.vcdfw.ga/?0f=K5nCNJ&ty6cfr=CUqHED7H&J9a=0xFR8q&27CxFL48_ii=XOCH&-d4R0t=lWWFBwIUU&kvEdbZU=u&_dxf=img";
        String imageUrl = "https://avatar.csdnimg.cn/3/1/B/1_ycagri.jpg";//a4b8421a1bfffe731ef59ce5c83e9c56  a4b8421a1bfffe731ef59ce5c83e9c56
        try {
            byte[] bytes = sample02(imageUrl);
            System.out.println(MD5Util.getFileMD5(bytes));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
