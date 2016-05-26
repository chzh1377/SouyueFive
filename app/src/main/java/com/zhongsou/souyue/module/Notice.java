package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Notice extends ResponseObject {

    private String title = "";
    private String keyword = "";
    private String content = "";
    private long date = 0;
    private long id = 0;
    private String nick = "";
    private String srpId = "";
    private long pushType = 0;
    private int IsGetContent = 1;// 消息推送打开标志 1表示需要抽取原文， 0; 是直接打开

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }

    public String content() {
        return content;
    }

    public void content_$eq(String content) {
        this.content = content;
    }

    public long date() {
        return date;
    }

    public void date_$eq(long date) {
        this.date = date;
    }

    public long id() {
        return id;
    }

    public void id_$eq(long id) {
        this.id = id;
    }

    public String nick() {
        return nick;
    }

    public void nick_$eq(String nick) {
        this.nick = nick;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public long pushType() {
        return pushType;
    }

    public void pushType_$eq(long pushType) {
        this.pushType = pushType;
    }

    public int IsGetContent() {
        return IsGetContent;
    }

    public void IsGetContent_$eq(int isGetContent) {
        IsGetContent = isGetContent;
    }

}
