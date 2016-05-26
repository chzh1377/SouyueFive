package com.zhongsou.souyue.module.listmodule;

/**
 * 专题展示样式
 * Created by lvqiang on 15/12/23.
 */
public class SpecialItemData extends BaseListData {
    private String bigImgUrl;
    private String happenTime;
    private String focus;

    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public String getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(String headTime) {
        this.happenTime = headTime;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }
}
