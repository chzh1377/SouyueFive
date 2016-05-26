package com.zhongsou.souyue.module;


import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by Lenovo on 15-6-15.
 */
public class AppInfo implements DontObfuscateInterface{
    private String appName = "";
    private String packageName = "";
    private String preInstall  = "";

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPreInstall(String preInstall) {
        this.preInstall = preInstall;
    }

    public String getPreInstall() {
        return preInstall;
    }

}
