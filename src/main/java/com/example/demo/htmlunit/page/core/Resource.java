package com.example.demo.htmlunit.page.core;

import com.example.demo.htmlunit.page.entity.LoaderResult;

import java.util.concurrent.Callable;

/**
 * Description：网页资源
 * Author；JinHuatao
 * Date: 2019/12/6 11:12
 */
public interface Resource extends Callable<LoaderResult> {


}
