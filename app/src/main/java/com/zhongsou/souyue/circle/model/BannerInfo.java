package com.zhongsou.souyue.circle.model;

/**
 * Created by wangqiang on 15/10/26.
 */
public class BannerInfo {
    private String imagePath; //图片地址
    private String title;  //banner标题

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
