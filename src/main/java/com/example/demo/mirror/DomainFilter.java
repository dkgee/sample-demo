package com.example.demo.mirror;

/**
 * Description：域名过滤器
 * Author；JinHuatao
 * Date: 2019/8/29 14:02
 */
public interface DomainFilter extends LinkFilter{

    void init(String[] domains);

}
