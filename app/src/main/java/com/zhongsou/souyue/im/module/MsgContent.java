package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.1)
 * @Copyright (c) 2016 zhongsou
 * @Description
 * IM消息中c的具体bean
 * @date 16/1/12
 */
public class MsgContent implements Serializable, DontObfuscateInterface {
    private static final long serialVersionUID = 3481528354798655333L;

    private String iconType;
    private String iconUrl;
    private String jumpType;
    private String text;
    private String url;

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
