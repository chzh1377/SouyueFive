package com.zhongsou.souyue.module;

/**
 * Created by yinguanping on 15/1/7.
 */
public class MineListInfo extends ResponseObject {

    private String title = "";//标题
    private String imgUrl = "";//图标地址url
    private int imgId;//此为数据在本地取时的本地图片id
    private String content = "";//行内小字显示的内容详介

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;//标记 0:空字段(显示断行用) 1:正常行

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
