package com.example.demo.htmlunit.page.entity;

/**
 * Description：下载的URL
 * Author；JinHuatao
 * Date: 2019/12/6 11:37
 */
public class Curi {

    private String url;//请求地址
    private int depth;//链接深度
    private String saveDir;//文件保存目录
    private String fileName;//文件名称
    private String savePath;//文件保存路径
    private String sourceFilePath;//当前链接来源的文件路径
    private ContextType contextType;//资源类型
    private String rawUrl;//原始链接
    private String regexHref;//原始正则链接
    private String replaceHref;//替换的新链接

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    public String getRegexHref() {
        return regexHref;
    }

    public void setRegexHref(String regexHref) {
        this.regexHref = regexHref;
    }

    public String getReplaceHref() {
        return replaceHref;
    }

    public void setReplaceHref(String replaceHref) {
        this.replaceHref = replaceHref;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    @Override
    public String toString() {
        return "Curi{" +
                "url='" + url + '\'' +
                ", saveDir='" + saveDir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", savePath='" + savePath + '\'' +
                ", sourceFilePath='" + sourceFilePath + '\'' +
                ", contextType=" + contextType +
                ", rawUrl='" + rawUrl + '\'' +
                ", regexHref='" + regexHref + '\'' +
                ", replaceHref='" + replaceHref + '\'' +
                '}';
    }
}
