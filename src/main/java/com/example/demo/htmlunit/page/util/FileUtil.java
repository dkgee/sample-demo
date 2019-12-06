package com.example.demo.htmlunit.page.util;

import java.io.File;

/**
 * Description：文件工具类
 * Author；JinHuatao
 * Date: 2019/12/6 11:49
 */
public class FileUtil {

    public static String Separator = "/";

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

}
