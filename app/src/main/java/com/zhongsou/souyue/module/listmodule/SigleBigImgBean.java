package com.zhongsou.souyue.module.listmodule;

import java.util.Map;

/**
 * 单张大图,gif,视频,焦点图显示样式
 * Created by lvqiang on 15/12/23.
 */
public class SigleBigImgBean extends BaseListData {
    private String bigImgUrl;
    private Map<String,String> titleIcon;
    private String duration;
    private int imgCount;
    private int imgRatio;//图片宽高比，目前只支持gif
    private String phoneImageUrl;//gif原图，目前只支持gif 以及 videoUrl


    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public Map<String,String> getTitleIcon() {
        return titleIcon;
    }

    public void setTitleIcon(Map<String,String> titleIcon) {
        this.titleIcon = titleIcon;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public int getImgRatio() {
        return imgRatio;
    }

    public void setImgRatio(int imgRatio) {
        this.imgRatio = imgRatio;
    }

    public String getPhoneImageUrl() {
        return phoneImageUrl;
    }

    public void setPhoneImageUrl(String phoneImageUrl) {
        this.phoneImageUrl = phoneImageUrl;
    }
}
