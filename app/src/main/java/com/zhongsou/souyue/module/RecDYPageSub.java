package com.zhongsou.souyue.module;

/**
 * User: zhangliang01@zhongsou.com
 * Date: 11/1/13
 * Time: 12:24 PM
 */
public class RecDYPageSub extends ResponseObject{
    private String keyword;
    private String shopId;
    private String image;
    private String srpId;
    public boolean cstatus = false;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public boolean isCstatus() {
        return cstatus;
    }

    public void setCstatus(boolean cstatus) {
        this.cstatus = cstatus;
    }
}
