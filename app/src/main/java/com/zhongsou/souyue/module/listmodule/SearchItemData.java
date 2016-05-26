package com.zhongsou.souyue.module.listmodule;

import java.util.Map;

/**
 * @description: 5.2 搜索结果 返回圈子、srp需要的字段
 * @auther: qubian YanBin
 * @data: 2016/4/27.
 */
public class SearchItemData extends BaseListData  {
    private Map<String,String> titleIcon;

    public Map<String,String> getTitleIcon() {
        return titleIcon;
    }

    public void setTitleIcon(Map<String,String> titleIcon) {
        this.titleIcon = titleIcon;
    }

    private String subscribeId;
    private int isSubscribe;
    private int isPrivate;  //是否是私密圈

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public int getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(int isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }
}
