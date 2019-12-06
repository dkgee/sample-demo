package com.example.demo.htmlunit;

import com.gargoylesoftware.htmlunit.util.Cookie;

import java.util.Set;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/12/5 16:12
 */
public class PageContext {

    private String requestUrl;

    private String requestHost;

    private String domain;

    private String protocolHead;

    private String html;

    private Set<Cookie> cookieSet;

    private String referer;

    private String mainDir;

    private String jsDir;

    private String imgDir;

    private String cssDir;

    public String getMainDir() {
        return mainDir;
    }

    public void setMainDir(String mainDir) {
        this.mainDir = mainDir;
    }

    public String getJsDir() {
        return jsDir;
    }

    public void setJsDir(String jsDir) {
        this.jsDir = jsDir;
    }

    public String getImgDir() {
        return imgDir;
    }

    public void setImgDir(String imgDir) {
        this.imgDir = imgDir;
    }

    public String getCssDir() {
        return cssDir;
    }

    public void setCssDir(String cssDir) {
        this.cssDir = cssDir;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getRequestHost() {
        return requestHost;
    }

    public void setRequestHost(String requestHost) {
        this.requestHost = requestHost;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocolHead() {
        return protocolHead;
    }

    public void setProtocolHead(String protocolHead) {
        this.protocolHead = protocolHead;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
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
}
