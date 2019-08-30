package com.example.demo.mirror;

import com.example.demo.util.DomainUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Description：顶级域名过滤器
 * Author；JinHuatao
 * Date: 2019/8/29 14:04
 */
public class TopDomainFilter implements DomainFilter{

    private static Logger logger = LoggerFactory.getLogger(TopDomainFilter.class);

    private Set<String> topDomianSet = new HashSet<>();

    @Override
    public void init(String[] domains) {
        for(String domain: domains){
            topDomianSet.add(domain);
        }
    }

    @Override
    public boolean accept(String url) {
        String topDomain = DomainUtil.getTopDomain(url);
        if(topDomain == null){
            logger.warn("未提取到该链接的顶级域名，请检查DomainUtil的顶级域名集合中是否包含：" + url);
            return true;
        }
        if(topDomianSet.contains(topDomain)){
            return false;
        }
        return true;
    }
}
