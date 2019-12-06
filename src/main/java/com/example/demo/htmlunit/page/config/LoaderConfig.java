package com.example.demo.htmlunit.page.config;

/**
 * Description：加载配置类
 * Author；JinHuatao
 * Date: 2019/12/6 11:28
 */
public interface LoaderConfig {

    String RESOURCE = "resources";
    String DOUBLE_DOT = "..";

    //资源保存目录
    String MAIN_DIR = "/snapshot/";
    String RESOURCE_CSS_DIR = "resources/css/";
    String RESOURCE_JS_DIR = "resources/js/";
    String RESOURCE_IMG_DIR = "resources/img/";
    String INDEX_HTML = "index.html";


    //HTML页面解析正则
    String TAG_LINK_REG = "(<link[\\s]+\\w+[^>]+>)";
    String TAG_SCRIPT_REG = "(<script[\\s]+\\w+[^>]+>)";
    String TAG_IMG_REG = "(<img[\\s]+\\w+[^>]+>)";
    String ATTR_HREF_OUTER_REG = "(href=[\\s]{0,1}\"(.+?)\")";
    String ATTR_SRC_REG = "(src=[\\s]{0,1}[\\\"|\\']{1}(.+?)[\\\"|\\']{1})";
    String ATTR_DATA_SRC_REG = "(data-src=[\\s]{0,1}\\\"|\\']{1}(.+?)\\\"|\\']{1})";
    String CSS_FILE_URL_REG = "url\\((.+?)\\)";

}
