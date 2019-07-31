package com.example.demo.crawler.ch01_2;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.DatabaseException;

import java.util.Map;
import java.util.Set;

/**
 * Description：Berkeley DB 抽象实现类
 * Author；JinHuatao
 * Date: 2019/7/31 17:29
 */
public class BDBFrontier extends AbstractFrontier implements Frontier{

    private StoredMap pendingUrisDB = null;

    //使用默认的路径和缓存大小构造函数
    public BDBFrontier(String homeDirectory) throws DatabaseException {
        super(homeDirectory);
        EntryBinding keyBinding = new SerialBinding(javaCatalog, String.class);
        EntryBinding valueBinding = new SerialBinding(javaCatalog, CrawlUrl.class);
        pendingUrisDB = new StoredMap(database, keyBinding, valueBinding, true);
    }

    @Override
    public CrawlUrl getNext() throws Exception {
        CrawlUrl result = null;
        if(!pendingUrisDB.isEmpty()){
            Set entrys = pendingUrisDB.entrySet();
            System.out.println(entrys);
            Map.Entry<String, CrawlUrl> entry = (Map.Entry<String, CrawlUrl>) pendingUrisDB.entrySet().iterator().next();
            result = entry.getValue();
            delete(entry.getKey());
        }
        return result;
    }

    public boolean putUrl(CrawlUrl url){
        put(url.getOriUrl(), url);
        return true;
    }

    @Override
    protected void put(Object key, Object value) {
        pendingUrisDB.put(key, value);
    }

    @Override
    protected Object get(Object key) {
        return pendingUrisDB.get(key);
    }

    @Override
    protected Object delete(Object key) {
        return pendingUrisDB.remove(key);
    }

    private String caculateUrl(String url){
        return url;
    }

    public static void main(String[] args) {
        try {
            BDBFrontier bdbFrontier = new BDBFrontier("D:\\bdb");
            CrawlUrl url = new CrawlUrl();
            url.setOriUrl("http://www.163.com");
            bdbFrontier.putUrl(url);
            System.out.println(bdbFrontier.getNext().getOriUrl());
            bdbFrontier.close();
        } catch (DatabaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
