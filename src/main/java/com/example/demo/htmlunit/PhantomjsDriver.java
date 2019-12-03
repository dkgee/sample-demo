package com.example.demo.htmlunit;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/29 17:02
 */
public class PhantomjsDriver {

    private static void sample(){

//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        //step01：配置请求参数
        DesiredCapabilities dcbs = new DesiredCapabilities();
//        dcbs.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        dcbs.setCapability("acceptSslCerts", true);//ssl证书支持
        dcbs.setCapability("takesScreenshot", false);//截屏支持
        dcbs.setCapability("cssSelectorsEnabled", false);//css搜索支持
        dcbs.setCapability("phantomjs.page.settings.XSSAuditingEnabled",true);
        dcbs.setCapability("phantomjs.page.settings.webSecurityEnabled",false);
        dcbs.setCapability("phantomjs.page.settings.localToRemoteUrlAccessEnabled",true);
        dcbs.setCapability("phantomjs.page.settings.XSSAuditingEnabled",true);
        dcbs.setJavascriptEnabled(true);
        dcbs.setCapability("phantomjs.page.settings.loadImages",false);
        //dcbs.setCapability("phantomjs.binary.path",  "");//驱动支持
        dcbs.setCapability("ignoreProtectedModeSettings", true);
        dcbs.setPlatform(Platform.VISTA);
        dcbs.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        dcbs.setBrowserName(BrowserType.CHROME);
//        String remoteHost = "http://172.30.154.245:8910";
        dcbs.setCapability("phantomjs.binary.path", "/usr/local/bin/phantomjs");
        String remoteHost = "http://172.30.154.244:8910"; //8916会拒绝连接
        RemoteWebDriver driver = null;
        try {
            //step02: 创建远程web驱动连接
            driver = new RemoteWebDriver(new URL(remoteHost), dcbs);

//            String url = "https://passport.csdn.net/login?code=public";
            //打不开可能是已经被干掉了，不要一味以为是浏览器的原因。
            String url = "http://qw2.zb76f.club/?kmI00MCL2=eC_YX&bmDYoAuy0=TF&dv1=RC_-zigGc&DsBZUlI=FYCRRVzGITC&x4XhaqXucc=2-Wn";
//            String url = "http://www.baidu.com";
            driver.navigate().to(url);
           /* Map cap = driver.getCapabilities().asMap();
            Set<Map.Entry> ss = cap.entrySet();
            for(Map.Entry entry: ss){
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }*/

            //通过判断页面特定元素来是否存在来进行操作
//            WebElement webElement = null;
//            try {
//                webElement = driver.findElementById("articleInfo00");
//            }catch (NoSuchElementException e){
//                System.out.println("=======No found element");
//            }
//
//            if(webElement != null){
//                System.out.println(url + "is success,  page is open!");
//            }

//            driver.get(url);
            //driver.quit();//关闭session
//            Thread.sleep(5000);
//            String title = driver.getTitle();
            String html = driver.getPageSource();
            System.out.println(html);
//            WebElement upBtn = null;
//            try {
//                upBtn = driver.findElementByXPath("//*[@id=\"app\"]/div/div/div[1]/div[2]/div[5]/ul/li[2]/a");
//            }catch (NoSuchElementException e){
//                System.out.println("upBtn不存在");
//                return;
//            }
//            if(upBtn != null)
//                upBtn.click();
//
//            WebElement username = driver.findElementByXPath("//*[@id=\"all\"]");//用户名
//            WebElement password = driver.findElementByXPath("//*[@id=\"password-number\"]");
//            WebElement loginBtn = driver.findElementByXPath("//*[@id=\"app\"]/div/div/div[1]/div[2]/div[5]/div/div[6]/div/button");
//            username.sendKeys("xxxxxxxxxxx@qq.com");
//            password.sendKeys("xxxxxxxxxx");
//            loginBtn.click();
//            TimeUnit.SECONDS.sleep(2);
//            String html = driver.getPageSource();
//            System.out.println("========" + html);



            //获取所有类型的日志，并打印
//            LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
//            Iterator<LogEntry> tt = logs.iterator();
//            while (tt.hasNext()){
//                LogEntry logEntry = tt.next();
//                System.out.println(logEntry.toString());
//            }

//            LogEntries logs = driver.manage().logs().get(LogType.PERFORMANCE);
//            for (Iterator<LogEntry> it = logs.iterator(); it.hasNext();){
//                LogEntry entry = it.next();
//                System.out.println(entry.toString());
//            }

//            SessionId sessionId = driver.getSessionId();
//            Command command = new Command(sessionId, DriverCommand.GET_PAGE_SOURCE, ImmutableMap.of());
//            Response response = driver.getCommandExecutor().execute(command);
//            String state = response.getState();//success or fail
//            String sid = response.getSessionId();//5e1db200-cad5-11e9-b2d4-cbc68d72bc7e  唯一会话ID
//            Integer status = response.getStatus();// 0  执行成功，其他执行失败
//
//            System.out.println("state:" + state);
//            System.out.println("sid:" + sid);
//            System.out.println("status:" + status);
//
//            String pageSource = (String) response.getValue();//响应内容
//            System.out.println(pageSource);


//            System.out.println("============================###=================");
//            String fenghaungUrl  = "http://news.ifeng.com/";
//            driver.get(fenghaungUrl);
//            System.out.println(driver.getPageSource());

//            driver.setLogLevel(Level.INFO);

//            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);//每3s自动等待
//            driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
//            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);




            //TODO 测试流程化采集
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(driver != null){
                driver.close();//会关闭driver上的所有session
            }
        }

    }

    public static void main(String[] args) {
        sample();
    }
}
