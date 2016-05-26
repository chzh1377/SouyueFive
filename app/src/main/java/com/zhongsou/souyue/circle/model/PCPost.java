package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob zhou on 14-11-3.
 * <p/>
 * PC = personal center 个人中心
 * <p/>
 * 个人中心展示帖子model
 */
public class PCPost implements Serializable, DontObfuscateInterface {

    private long blog_id; // 帖子ID
    private String brief; // 概要
    private long interest_id; // 圈子ID
    private long user_id;
    private String title; // 标题
    private List<String> images = new ArrayList<String>();// 图片地址
    private String srp_word;
    private String srp_id;
    private String new_srpid;
    private String create_time; // 时间
    private String name;


    private int postLayoutType = CircleResponseResult.POSTS_TYPE_NOPIC; // 默认布局类型

    public long getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(long blog_id) {
        this.blog_id = blog_id;
    }

    public long getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getPostLayoutType() {
        return postLayoutType;
    }

    public void setPostLayoutType(int postLayoutType) {
        this.postLayoutType = postLayoutType;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getSrp_word() {
        return srp_word;
    }

    public void setSrp_word(String srp_word) {
        this.srp_word = srp_word;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }

    public String getNew_srpid() {
        return new_srpid;
    }

    public void setNew_srpid(String new_srpid) {
        this.new_srpid = new_srpid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
