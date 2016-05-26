package com.zhongsou.souyue.module;

/**
 * Created by zyw on 2016/1/13.
 * 需要上传到App信息.
 */
public class AppData extends ResponseObject {
    private String name;
    private String pkg;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
