package com.example.demo.htmlunit.page.entity;

import com.example.demo.htmlunit.page.config.LoaderConfig;
import com.example.demo.htmlunit.page.util.DomainUtil;
import com.example.demo.htmlunit.page.util.FileUtil;

/**
 * Description：下载种子
 * Author；JinHuatao
 * Date: 2019/12/6 11:40
 */
public class Seed {

    private String url;//请求页地址
    private String domain;
    private String hostUrl;//主域名地址

    private String protocolHead;//协议头
    private String html;//种子的html页

    private String mainDir;//文件保存目录
    private String seedFileDir;//种子文件保存目录
    private String seedIndexPath;//首页文件保存路径
    private String cssSaveDir;//页面抽取的css文件保存目录
    private String jsSaveDir;//页面抽取的js文件保存目录
    private String imgSaveDir;//页面抽取的img文件保存目录

    private int cssCount;//页面抽取的css链接数
    private int jsCount;//页面抽取的js链接数
    private int imgCount;//页面抽取的img链接数

    public Seed(String url, String mainDir) {
        this.url = url;
        this.domain = DomainUtil.getDomain(url);
        this.hostUrl = DomainUtil.getHostUrl(url);
        this.mainDir = mainDir;
        init();
    }

    private void init(){
        //初始化主目录
        FileUtil.mkDir(getMainDir());
        String seedFileDir = getMainDir() + getDomain() + FileUtil.Separator;
        FileUtil.mkDir(seedFileDir);
        setSeedFileDir(seedFileDir);
        String seedIndexPath = seedFileDir + LoaderConfig.INDEX_HTML ;
        setSeedIndexPath(seedIndexPath);
        String protocolHead = DomainUtil.getProtocolHead(getUrl());
        setProtocolHead(protocolHead);
        //初始化资源文件目录
        String cssSaveDir = seedFileDir + LoaderConfig.RESOURCE_CSS_DIR;
        String jsSaveDir = seedFileDir + LoaderConfig.RESOURCE_JS_DIR;
        String imgSaveDir = seedFileDir + LoaderConfig.RESOURCE_IMG_DIR;
        FileUtil.mkDir(cssSaveDir);
        FileUtil.mkDir(jsSaveDir);
        FileUtil.mkDir(imgSaveDir);
        setCssSaveDir(cssSaveDir);
        setJsSaveDir(jsSaveDir);
        setImgSaveDir(imgSaveDir);
    }

    public String getProtocolHead() {
        return protocolHead;
    }

    public void setProtocolHead(String protocolHead) {
        this.protocolHead = protocolHead;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getMainDir() {
        return mainDir;
    }

    public void setMainDir(String mainDir) {
        this.mainDir = mainDir;
    }

    public String getSeedFileDir() {
        return seedFileDir;
    }

    public void setSeedFileDir(String seedFileDir) {
        this.seedFileDir = seedFileDir;
    }

    public String getSeedIndexPath() {
        return seedIndexPath;
    }

    public void setSeedIndexPath(String seedIndexPath) {
        this.seedIndexPath = seedIndexPath;
    }

    public String getCssSaveDir() {
        return cssSaveDir;
    }

    public void setCssSaveDir(String cssSaveDir) {
        this.cssSaveDir = cssSaveDir;
    }

    public String getJsSaveDir() {
        return jsSaveDir;
    }

    public void setJsSaveDir(String jsSaveDir) {
        this.jsSaveDir = jsSaveDir;
    }

    public String getImgSaveDir() {
        return imgSaveDir;
    }

    public void setImgSaveDir(String imgSaveDir) {
        this.imgSaveDir = imgSaveDir;
    }

    public int getCssCount() {
        return cssCount;
    }

    public void setCssCount(int cssCount) {
        this.cssCount = cssCount;
    }

    public int getJsCount() {
        return jsCount;
    }

    public void setJsCount(int jsCount) {
        this.jsCount = jsCount;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public String toString() {
        return "Seed{" +
                "url='" + url + '\'' +
                ", domain='" + domain + '\'' +
                ", hostUrl='" + hostUrl + '\'' +
                ", protocolHead='" + protocolHead + '\'' +
                ", html='" + html + '\'' +
                ", mainDir='" + mainDir + '\'' +
                ", seedFileDir='" + seedFileDir + '\'' +
                ", seedIndexPath='" + seedIndexPath + '\'' +
                ", cssSaveDir='" + cssSaveDir + '\'' +
                ", jsSaveDir='" + jsSaveDir + '\'' +
                ", imgSaveDir='" + imgSaveDir + '\'' +
                ", cssCount=" + cssCount +
                ", jsCount=" + jsCount +
                ", imgCount=" + imgCount +
                '}';
    }

/* public static void main(String[] args) {
        String url = "https://www.baidu.com/";
        String mainDir = "/jxwz/";
        Seed seed = new Seed(url, mainDir);
        System.out.println(seed);
    }*/
}
