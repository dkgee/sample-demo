package com.example.demo.htmlunit.page.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/12/6 12:04
 */
public class DomainUtil {


    private static final String PROTOCOL = "://";

    private static final String W3 = "www.";

    private static final String QUESTION = "?";

    private static final String SLASH = "/";

    private static final String PORT = ":";

    private static final String DOT = "\\.";

    private static final String DOT2 = ".";

    /**
     * 顶级域名获取, 移除协议头，端口，等只取出网站地址, 划分. 为数组,
     * 如果是ip地址并且没有端口号, 如果是 ip 地址并且有端口号，则返回ip: 端口
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        String domain_tmp = removeUrlProtocolAndPort(url);

        String[] domain_segments = domain_tmp.split(DOT);

        if (domain_segments.length <= 1) {
            return null;
        }

        return domain_tmp;
    }

    /**
     * 把所有传过来的链接转换为小写, 然后去掉url中的协议，端口号，子链 子域名获取
     *
     * @param url
     * @return
     */
    public static String removeUrlProtocolAndPort(String url) {
        StringBuilder sb = new StringBuilder(url.toLowerCase());

        if (sb.indexOf(PROTOCOL) >= 0) {
            sb.delete(0, sb.indexOf(PROTOCOL) + PROTOCOL.length());
        }

        if(url.startsWith(W3)){
            if (sb.indexOf(W3) >= 0) {
                sb.delete(0, sb.indexOf(W3) + W3.length());
            }
        }

        if (sb.indexOf(QUESTION) >= 0) {
            sb.delete(sb.indexOf(QUESTION), sb.length());
        }

        if (sb.indexOf(PORT) >= 0) {
            sb.delete(sb.indexOf(PORT), sb.length());
        }

        if (sb.indexOf(SLASH) >= 0) {
            sb.delete(sb.indexOf(SLASH), sb.length());
        }
        return sb.toString();
    }

    public static String getProtocolHead(String url){
        StringBuilder sb = new StringBuilder(url.toLowerCase());

        if (sb.indexOf(PROTOCOL) >= 0) {
            return sb.substring(0, sb.indexOf(PROTOCOL) + 1);
        }

        return "";
    }

    public static String getHostUrl(String url){
        StringBuilder sb = new StringBuilder(url.toLowerCase());

        if (sb.indexOf(QUESTION) >= 0) {
            sb.delete(sb.indexOf(QUESTION), sb.length());
        }

        String hostUrl = sb.toString();
        if(hostUrl.endsWith(SLASH)){
            sb.delete(sb.length() - 1, sb.length());
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String url ="https://www.baidu.com/";
        System.out.println(getHostUrl(url));
    }
}
