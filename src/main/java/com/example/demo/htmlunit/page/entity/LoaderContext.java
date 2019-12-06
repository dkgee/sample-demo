package com.example.demo.htmlunit.page.entity;


import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description：加载器上下文，记录请求响应信息
 * Author；JinHuatao
 * Date: 2019/12/6 11:15
 */
public class LoaderContext {

    private String html;

    private Set<Cookie> cookieSet = new HashSet<>();

    private String referer;

    private Map<String, String> indexHtmlRegexReplace = new ConcurrentHashMap<>();

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Set<Cookie> getCookieSet() {
        return cookieSet;
    }

    public void setCookieSet(Set<Cookie> cookieSet) {
        this.cookieSet = cookieSet;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public Map<String, String> getIndexHtmlRegexReplace() {
        return indexHtmlRegexReplace;
    }

    public void addIndexHtmlRegex(String key, String value){
        this.indexHtmlRegexReplace.put(key, value);
    }

    public void addCookie(String name, String value, String domain, String path, Date expires){
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setExpiryDate(expires);
        cookieSet.add(cookie);
    }
}
