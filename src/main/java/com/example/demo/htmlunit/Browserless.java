package com.example.demo.htmlunit;

import java.net.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.*;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/11/9 9:51
 */
public class Browserless {

    public static void main(String[] args)throws MalformedURLException {
        final ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
//        chromeOptions.setCapability("browserless.token", "YOUR-API-TOKEN");

        WebDriver driver = new RemoteWebDriver(
                new URL("http://172.30.154.244:3000/"),
                chromeOptions
        );

        driver.get("https://www.baidu.com");
        String title = driver.getTitle();
        System.out.println(title);
        driver.quit();
    }
}
