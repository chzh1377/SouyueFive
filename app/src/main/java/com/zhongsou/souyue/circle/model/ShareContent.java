package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;

public class ShareContent implements Serializable, DontObfuscateInterface {

    private String interest_ids;    // 分享到兴趣圈的ID，逗号隔开
    private String title;           // 标题
    private String brief;           // 摘要
    private String content = "";         // 内容
    private List<String> images;    // 分享到兴趣圈的图片列表
    private int textType;           // 0-普通文本 1-富文本
    private String newsUrl;         // 新闻链接地址
    private String keyword;
    private String srpId;
    public static final int TYPEHTML = 1;//抽取正文，抽取正文地址
    public static final int TYPETEXT = 0; //原创，原创内容
    public static final int TYPESOURCE = 2;//查看原文，原文章URL
    private String channel; //统计新增channel字段，因为分享需要，所以在此添加 channel属性

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getTextType() {
        return textType;
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public String getInterest_ids() {
        return interest_ids;
    }

    public void setInterest_ids(String interest_ids) {
        this.interest_ids = interest_ids;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
