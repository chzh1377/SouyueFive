package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.listmodule.FootItemBean;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/3/25.
 */
public class VideoAboutResult implements Serializable, DontObfuscateInterface,Cloneable  {


    private long id;
    private String title;
    private String category;
    private String bigImgUrl;
    private String phoneImageUrl;
    private String url;
    private List<String> image;
    private String srpId;
    private String keyword;
    private String duration;
    private FootItemBean footView;

    public String getPhoneImageUrl() {
        return phoneImageUrl;
    }

    public void setPhoneImageUrl(String phoneImageUrl) {
        this.phoneImageUrl = phoneImageUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List getImage() {
        return image;
    }

    public void setImage(List image) {
        this.image = image;
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

    public FootItemBean getFootView() {
        return footView;
    }

    public void setFootView(FootItemBean footView) {
        this.footView = footView;
    }
}
