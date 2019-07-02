package com.example.demo.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description：加载顺序，父委托机制：加载一个类时，先由父类加载，父类加载完成后，再由子类加载；
 *      BootstrapClassLoader    只加载 /jre/lib/*.jar文件        根加载器
 *      ExtClassLoader          只加载 /jre/lib/ext/*.jar文件    扩展加载器
 *      AppClassLoader          只加载 /jre/lib/ext/*.jar文件及自定义文件  应用加载器   所有依赖的第三方类库和项目中的class全都由AppClassLoader加载
 *
 * Author；JinHuatao
 * Date: 2019/7/2 17:40
 */
public class ClassPath_of_Bootstrap_Ext_AppClassLoader {

    public static void main(String[] args) {
        System.out.println("BootstrapClassLoader 的加载路径: ");

        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for(URL url : urls)
            System.out.println(url);
        System.out.println("----------------------------");

        //取得扩展类加载器
        URLClassLoader extClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader().getParent();

        System.out.println(extClassLoader);
        System.out.println("扩展类加载器 的加载路径: ");

        urls = extClassLoader.getURLs();
        for(URL url : urls)
            System.out.println(url);

        System.out.println("----------------------------");


        //取得应用(系统)类加载器
        URLClassLoader appClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();

        System.out.println(appClassLoader);
        System.out.println("应用(系统)类加载器 的加载路径: ");

        urls = appClassLoader.getURLs();
        for(URL url : urls)
            System.out.println(url);

        System.out.println("----------------------------");
    }
}
