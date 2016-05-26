package com.tuita.sdk.im.db.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/4/15.
 */
public class ImToCricle implements Serializable,DontObfuscateInterface {
    public static final int TYPE_ORDINARY =0 ;//普通圈
    public static final int TYPE_SECRETERY =1 ;// 私密圈

    private String srpId;
    private String keyword;
    private String interestName;
    private String interestLogo;
    private int type;// 1是私密圈, 0是普通圈

    private long interestId;

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

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public String getInterestLogo() {
        return interestLogo;
    }

    public void setInterestLogo(String interestLogo) {
        this.interestLogo = interestLogo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }
}
