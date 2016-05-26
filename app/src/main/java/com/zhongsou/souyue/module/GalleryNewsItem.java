package com.zhongsou.souyue.module;

import java.io.Serializable;

/**
 * "url": "http://zsimg5.b0.upaiyun.com/7b01a8e3-581c-4ab5-bf8a-8947a7baf8f2.jpg!sy",
 * "title": "图集标题",
 * "desc": "描述1--描述1--描述1--描述1--描述1--"
 */

@SuppressWarnings("serial")
public class GalleryNewsItem extends ResponseObject implements Serializable {

    public static final int TYPE_CONTENT_PAGE = -1; // 前面的图集页面
    public static final int TYPE_RECOMMEND_LIST_SINGLE = 0; // 一行的推荐
    public static final int TYPE_RECOMMENDLIST_DOUBLE = 1; // 2行的推荐

    private String desc; // 描述
    private String img; // 图片地址
    private String title; // 标题
    private String url; // 地址(用于跳转)
    private String srpid;
    private String newstime; // 用于跳转
    private String source; // 来源（用于跳转）
    private String keyword;
    private String channel; //频道 统计使用

    public String getNewstime() {
        return newstime;
    }

    public void setNewstime(String newstime) {
        this.newstime = newstime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSrpid() {
        return srpid;
    }

    public void setSrpid(String srpid) {
        this.srpid = srpid;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private int type; // 类型

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
