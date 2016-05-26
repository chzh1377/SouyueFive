package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;

public class TaskCenterInfo extends ResponseObject {

    /**
     *
     */
    private static final long serialVersionUID = 244329519534048573L;

    private String type;
    private String category;
    private String msg;
    private String highlight;
    private String zsb;
    private String score;
    private String url;
    private String token;
    private String guide_url;
    private String guide_msg;
    private String time;
    private String guide_isforced;
    private long _expiry;
    private long isPre;
    private long listId;

    public long getIsPre() {
        return isPre;
    }

    public void setIsPre(long isPre) {
        this.isPre = isPre;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public long get_expiry() {
        return this._expiry;
    }

    public void set_expiry(long _expiry) {
        this._expiry = _expiry;
    }

    public String getGuide_isforced() {
        return this.guide_isforced;
    }

    public void setGuide_isforced(String guide_isforced) {
        this.guide_isforced = guide_isforced;
    }

    public String getGuide_url() {
        return this.guide_url;
    }

    public void setGuide_url(String guide_url) {
        this.guide_url = guide_url;
    }

    public String getGuide_msg() {
        return this.guide_msg;
    }

    public void setGuide_msg(String guide_msg) {
        this.guide_msg = guide_msg;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getZsb() {
        return zsb;
    }

    public void setZsb(String zsb) {
        this.zsb = zsb;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
