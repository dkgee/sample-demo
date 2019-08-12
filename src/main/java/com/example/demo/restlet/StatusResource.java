package com.example.demo.restlet;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Description：状态资源地址
 * Author；JinHuatao
 * Date: 2019/8/12 11:51
 */
public class StatusResource extends ServerResource{

    @Get
    public String getStatusInfo(){
        return "Crawler is online";
    }
}
