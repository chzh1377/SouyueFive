package com.zhongsou.souyue.module;

import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 模板接口关键信息
 * @date 2016/3/31
 */
public class TemplateKeyData {
//    keywordType: 1,
//    interestType: 0,
//    longUrl: "http://61.135.210.239:8888/d3api2/webdata/interest.share.groovy?&blogId=115658581&interestId=272",
//    isSubscribe: 0,
//    interestRole: 0,
//    signId: "7f8c1e104062e894b8e6e44d3c01bcd8",
//    shareTitle: "老师为了向学生证明吸烟的害处，特意把从香烟中提取的尼古丁放在...",
//    shareImage: [
//            "http://sns-img.b0.upaiyun.com/dunzd/1601/2715/55/145571444692051453881347.jpeg!android"
//            ],
//    souyueTagName: "souyue-content souyue-content-bottom",
//    souyueTagId: "souyue-content",
//    idDel: 0,
//    keyword: "段子",
//    srpId: "7aa5fb2b1884b98a37eec82e3613d27f",
//    shortUrl: "http://103.29.134.224/d3api2/shortURL.groovy?id=J4en"

    private int keywordType;
    private int interestType;
    private String longUrl;
    private int isSubscribe;
    private int interestRole;
    private String signId;
    private String shareTitle;
    private List<String> shareImage;
    private List<String> imageList;
    private String souyueTagName;
    private String souyueTagId;
    private int idDel;
    private String keyword;
    private String srpId;
    private String shortUrl;
    private int showAdminMoreOper;
    private int interestId;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public int getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(int isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public int getInterestRole() {
        return interestRole;
    }

    public void setInterestRole(int interestRole) {
        this.interestRole = interestRole;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String singId) {
        this.signId = singId;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public List<String> getShareImage() {
        return shareImage;
    }

    public void setShareImage(List<String> shareImage) {
        this.shareImage = shareImage;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getSouyueTagName() {
        return souyueTagName;
    }

    public void setSouyueTagName(String souyueTagName) {
        this.souyueTagName = souyueTagName;
    }

    public int getIdDel() {
        return idDel;
    }

    public void setIdDel(int idDel) {
        this.idDel = idDel;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public int getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(int keywordType) {
        this.keywordType = keywordType;
    }

    public int getInterestType() {
        return interestType;
    }

    public void setInterestType(int interestType) {
        this.interestType = interestType;
    }

    public String getSouyueTagId() {
        return souyueTagId;
    }

    public void setSouyueTagId(String souyueTagId) {
        this.souyueTagId = souyueTagId;
    }

    public int getShowAdminMoreOper() {
        return showAdminMoreOper;
    }

    public void setShowAdminMoreOper(int showAdminMoreOper) {
        this.showAdminMoreOper = showAdminMoreOper;
    }

    public int getInterestId() {
        return interestId;
    }

    public void setInterestId(int interestId) {
        this.interestId = interestId;
    }
}
