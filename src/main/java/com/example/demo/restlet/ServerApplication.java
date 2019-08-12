package com.example.demo.restlet;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.util.Template;

/**
 * Description：采集服务引擎入口
 * Author；JinHuatao
 * Date: 2019/8/12 11:42
 */
public class ServerApplication {

    public static void main(String[] args) {
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8989);
        component.getDefaultHost().attach("/result", EngineResource.class)
                .setMatchingMode(Template.MODE_EQUALS);//绑定engine映射地址,必须为全匹配
        component.getDefaultHost().attach("/status", StatusResource.class)
                .setMatchingMode(Template.MODE_EQUALS);//绑定status映射地址,必须为全匹配
        try {
            component.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
