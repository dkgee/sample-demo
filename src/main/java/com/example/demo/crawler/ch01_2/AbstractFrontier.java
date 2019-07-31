package com.example.demo.crawler.ch01_2;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.*;

import java.io.File;

/**
 * Description：Berkeley DB 抽象存储类
 * Author；JinHuatao
 * Date: 2019/7/31 17:17
 */
public abstract class AbstractFrontier {

    private Environment env ;
    private static  final String CLASS_CATALOG = "java_class_catalog";
    protected StoredClassCatalog javaCatalog;
    protected Database catalogDatabase;
    protected Database database;

    public AbstractFrontier(String homeDirectory) throws DatabaseException {
        //打开env
        System.out.println("Opening environment in:" + homeDirectory);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        env = new Environment(new File(homeDirectory), envConfig);
        //设置DatabaseConfig
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
        //打开
        catalogDatabase = env.openDatabase(null, CLASS_CATALOG, dbConfig);
        javaCatalog = new StoredClassCatalog(catalogDatabase);
        //设置DatabaseConfig
        DatabaseConfig dbConfig0 = new DatabaseConfig();
        dbConfig0.setTransactional(true);
        dbConfig0.setAllowCreate(true);
        //打开
        database = env.openDatabase(null, "URL", dbConfig);
    }

    /**
     * 关闭数据库，关闭环境
     * */
    public void close() throws DatabaseException {
        database.close();
        javaCatalog.close();
        env.close();
    }

    //put方法
    protected abstract void put(Object key, Object value);
    //get方法
    protected abstract Object get(Object key);
    //delete方法
    protected abstract Object delete(Object key);
}
