package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/3/29.
 */
public class FootItemBeanSer implements Serializable,DontObfuscateInterface {
    private int isFavorator;
    private int isUp;
    private int isDown;
    private int upCount;
    private int downCount;
    private int commentCount;
    private int deleteId;
    private String shareUrl;//分享短链

    public int getIsFavorator() {
        return isFavorator;
    }

    public void setIsFavorator(int isFavorator) {
        this.isFavorator = isFavorator;
    }

    public int getIsUp() {
        return isUp;
    }

    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }

    public int getIsDown() {
        return isDown;
    }

    public void setIsDown(int isDown) {
        this.isDown = isDown;
    }

    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(int deleteId) {
        this.deleteId = deleteId;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
