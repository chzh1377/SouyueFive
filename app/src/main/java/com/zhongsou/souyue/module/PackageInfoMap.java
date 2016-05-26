package com.zhongsou.souyue.module;

/**
 * Created by wangqiang on 15/12/25.\
 * 安装包
 */
public class PackageInfoMap {
    public PackageInfoMap(String applicationName, String packageName) {
        this.applicationName = applicationName;
        this.packageName = packageName;
    }

    private String applicationName;
    private String packageName;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


}
