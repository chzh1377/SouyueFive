package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * 栏目ITEM Bean
 */
public class ChannelItem implements Serializable, DontObfuscateInterface {

    private static final long serialVersionUID = -6465237897027410019L;
    /**
     * 栏目ID
     */
    private String  channelId = "";
    /**
     * 栏目NAME
     */
    private String title;

    /**
     * 栏目url
     */
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getchannelId() {
        return channelId;
    }

    public String getName() {
        return this.title;
    }

    public void setchannelId(String paramInt) {
        this.channelId = paramInt;
    }

    public void setName(String paramString) {
        this.title = paramString;
    }

}