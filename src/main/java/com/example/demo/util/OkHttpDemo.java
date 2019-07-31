package com.example.demo.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Description：okhttp示例
 * Author；JinHuatao
 * Date: 2019/7/31 16:14
 */
public class OkHttpDemo {

    public static void sample01(){
        OkHttpClient httpClient = new OkHttpClient.Builder().proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1",1080))).build();
        String url = "https://www.google.com.hk/";
        Request request = new Request.Builder().url(url).build();
        try{
            Response response = httpClient.newCall(request).execute();
            if(response.isSuccessful() && response.body() != null){
                System.out.println(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sample01();
    }
}
