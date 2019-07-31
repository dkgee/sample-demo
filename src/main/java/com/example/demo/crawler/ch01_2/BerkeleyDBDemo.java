package com.example.demo.crawler.ch01_2;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.*;

import java.io.File;
import java.util.Map;

/**
 * Description：Berkeley DB示例
 * Author；JinHuatao
 * Date: 2019/7/31 16:48
 */
public class BerkeleyDBDemo {

    public static void sample01(){
        //管理多个内存数据库
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(false);
        envConfig.setAllowCreate(true);

        File envDir = new File("");
        Environment exampleEnv = null;
        StoredClassCatalog catalog = null;
        try {
            //创建内存数据库环境
            exampleEnv = new Environment(envDir, envConfig);
            //创建数据库
            String databaseName = "ToDoTaskList.db";
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            dbConfig.setTransactional(false);

            //打开用来存储类信息的数据库
            //用来存储类信息的数据库不要求能够存储重复的关键字
            dbConfig.setSortedDuplicates(false);
            Database database = exampleEnv.openDatabase(null, "classDb", dbConfig);

            //初始化用来存储序列化对象的catalog类
            catalog = new StoredClassCatalog(database);
            TupleBinding keyBinding = TupleBinding.getPrimitiveBinding(String.class);
            //把value作为对象的序列化方式存储
            SerialBinding valueBinding = new SerialBinding(catalog, NewsSource.class);//确定需要存放的数据类型
            Database store = exampleEnv.openDatabase(null, databaseName, dbConfig);

            EntryBinding keyBinding2 = new SerialBinding(catalog, String.class);
            Map map = new StoredSortedMap(store, keyBinding, valueBinding, true);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }finally {
            if(exampleEnv != null){
                try {
                    exampleEnv.sync();
                    exampleEnv.close();
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                exampleEnv = null;
            }
        }

    }
}

class NewsSource{

}
