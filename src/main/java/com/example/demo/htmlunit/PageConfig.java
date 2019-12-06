package com.example.demo.htmlunit;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/12/5 16:26
 */
public interface PageConfig {

     String RESOURCE = "resources";
     String DOUBLE_DOT = "..";

     String MAIN_DIR = "/snapshot/";
     String RESOURCE_CSS_DIR = "resources/css/";
     String RESOURCE_JS_DIR = "resources/js/";
     String RESOURCE_IMG_DIR = "resources/img/";
     String INDEX_HTML = "index.html";


    String TAG_LINK_REG = "(<link[\\s]+\\w+[^>]+>)";
    String TAG_SCRIPT_REG = "(<script[\\s]+\\w+[^>]+>)";
    String TAG_IMG_REG = "(<img[\\s]+\\w+[^>]+>)";
    String ATTR_HREF_OUTER_REG = "(href=[\\s]{0,1}\"(.+?)\")";
    String ATTR_SRC_REG = "(src=[\\s]{0,1}[\\\"|\\']{1}(.+?)[\\\"|\\']{1})";
    String ATTR_DATA_SRC_REG = "(data-src=[\\s]{0,1}\\\"|\\']{1}(.+?)\\\"|\\']{1})";
    String CSS_FILE_URL_REG = "url\\((.+?)\\)";

}
