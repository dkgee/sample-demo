package com.example.demo.mirror;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/29 14:26
 */
public class URL {

    private String url;

    //记录当前URL所处的深度,初始为0
    private int depth = 0;

    private int maxDepth = 0;

    public URL(String url, int maxDepth) {
        this.url = url;
        this.maxDepth = maxDepth;
    }

    public URL(String url, int depth, int maxDepth) {
        this.url = url;
        this.depth = depth;
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
