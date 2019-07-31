package com.example.demo.crawler.ch01;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

/**
 * Description：下载文件类
 * Author；JinHuatao
 * Date: 2019/7/31 10:49
 */
public class DownLoadFile {

    /**
     * 根据URL和网页类型生成需要保存的网页的文件名，去除URL中的非文件名字符
     * */
    public String getFileNameByUrl(String url, String contentType){
        //移除http
        url = url.substring(7);
        //text/html类型
        if(contentType.contains("html")){
            url = url.replaceAll("[\\?/:*l<>\"]","_") + ".html";
            return url;
        } else {
            //如application/pdf类型
            return url.replaceAll("[\\?/:*l<>\"]","_") + "." +
                    contentType.substring(contentType.lastIndexOf("/") + 1);
        }
    }

    /**
     * 保存网页字节数组到本地文件，filePath为要保存的文件的相对地址
     * */
    private void saveToLocal(byte[] data, String filePath){
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++){
                out.write(data[i]);
            }
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //下载URL指向的网页
    public String downloadFile(String url){
        String filePath = null;
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        HttpClient httpClient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
        try {
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != HttpStatus.SC_OK){
                System.err.println("Method Failded:" + response.getStatusLine());
                filePath = null;
            }
            //4. 处理HTTP响应内容
            InputStream is = response.getEntity().getContent();
            filePath = "D:\\temp\\" + getFileNameByUrl(url, response.getEntity().getContentType().getValue());
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int current = 0;
            int size = 0;
            while((current = bis.read(data,0,data.length)) != -1){
                buffer.write(data,0,current);
                size += current;
            }
            byte[] responseBody =  buffer.toByteArray();
            saveToLocal(responseBody, filePath);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
