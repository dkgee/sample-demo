package com.example.demo.htmlunit.page.entity;

/**
 * Description：记录资源加载结果
 * Author；JinHuatao
 * Date: 2019/12/6 11:17
 */
public class LoaderResult {

    private boolean result;//true：成功、false：失败

    private Curi curi;//返回的curi结果


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Curi getCuri() {
        return curi;
    }

    public void setCuri(Curi curi) {
        this.curi = curi;
    }
}
