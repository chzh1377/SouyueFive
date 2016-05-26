package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Weibo extends ResponseObject implements DontObfuscateInterface{

    private int category = 0; // 微博类型（1原创、）
    private String id = ""; // 234,//此条微博的id
    private User user = new User();
    private String content = ""; // 内容
    private List<ImageUrlInfo> image = new ArrayList<ImageUrlInfo>(); // 图片url数组
    private String source = ""; // "新浪微博",//来源
    private String date = ""; // "12345678",//发布时间
    private String url = ""; // "http://t.zhongsou.net",//微博连接
    private ReplyTo replyTo = new ReplyTo();
    private String zsUrl = "";

    public int category() {
        return category;
    }

    public void category_$eq(int category) {
        this.category = category;
    }

    public String id() {
        return id;
    }

    public void id_$eq(String id) {
        this.id = id;
    }

    public User user() {
        return user;
    }

    public void user_$eq(User user) {
        this.user = user;
    }

    public String content() {
        return content;
    }

    public void content_$eq(String content) {
        this.content = content;
    }

    public List<ImageUrlInfo> image() {
        return image;
    }

    public void image_$eq(List<ImageUrlInfo> image) {
        this.image = image;
    }

    public String source() {
        return source;
    }

    public void source_$eq(String source) {
        this.source = source;
    }

    public String date() {
        return date;
    }

    public void date_$eq(String date) {
        this.date = date;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public ReplyTo replyTo() {
        return replyTo;
    }

    public void replyTo_$eq(ReplyTo replyTo) {
        this.replyTo = replyTo;
    }

    public String zsUrl() {
        return zsUrl;
    }

    public void zsUrl_$eq(String zsUrl) {
        this.zsUrl = zsUrl;
    }

}
