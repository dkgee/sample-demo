package com.example.demo.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.util.Template;

/**
 * Description：应用服务资源映射地址
 * Author；JinHuatao
 * Date: 2019/8/12 11:46
 */
public class ResourceMapping extends Application{

    /**
     * 使用此应用为创建服务映射地址
     * */
    @Override
    public Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/engine", EngineResource.class)
                .setMatchingMode(Template.MODE_EQUALS);//绑定engine映射地址,必须为全匹配
        router.attach("/status", StatusResource.class)
                .setMatchingMode(Template.MODE_EQUALS);//绑定status映射地址,必须为全匹配
        return router;
    }
}
