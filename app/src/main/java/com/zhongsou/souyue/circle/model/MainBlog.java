package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.ArrayList;

public class MainBlog implements DontObfuscateInterface {
    private String mblog_id;
    private String brief;
    private String interest_id;
    private String user_id;
    private String title;
    private String srp_word;
    private String srp_id;
    private String nickname;
    private String image_url;
    private String new_srpid;
    private ArrayList<String> images;
    private String voice;
    private long voice_length = 0;
    private String url;
    private String blog_id;
    private int type;
    private String srp_logo;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getVoice_length() {
        return voice_length;
    }

    public void setVoice_length(long voice_length) {
        this.voice_length = voice_length;
    }

    public String getSrp_logo() {
        return srp_logo;
    }

    public void setSrp_logo(String srp_logo) {
        this.srp_logo = srp_logo;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMblog_id() {
        return mblog_id;
    }

    public void setMblog_id(String mblog_id) {
        this.mblog_id = mblog_id;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getNew_srpid() {
        return new_srpid;
    }

    public void setNew_srpid(String new_srpid) {
        this.new_srpid = new_srpid;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }


}
