package com.example.demo.htmlunit.page.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description：index主页解析器
 * Author；JinHuatao
 * Date: 2019/12/6 11:31
 */
public class HtmlParser {

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
}
