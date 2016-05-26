package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by wlong on 14-5-15.
 * YanBin update on 2016-04-08
 */
public class Interest implements DontObfuscateInterface {

//    "id": 61,
//            "keyword": "大赛复赛的",
//            "logo": "http://souyue-xqq.b0.upaiyun.com/newssource/1604/7jzgd7ror4df14600982269982-20160408145026_960_640.jpg!Intbig",
//            "srpId": "",
//            "category": "group",
//            "subscriber": 0,
//            "invokeType": 0,
//            "subscribe_id": ""

    private long id;
    private String keyword;
    private String logo;
    private String srpId;
    private String category;
    private int subscriber; // 1-用户已订阅的兴趣圈|0-非用户订阅兴趣圈
    private int invokeType;     // 跳转类型，70是频道
    private String subscribe_id; // 订阅id
    private String subCount; //   订阅数

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(int subscriber) {
        this.subscriber = subscriber;
    }

    public int getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(int invokeType) {
        this.invokeType = invokeType;
    }

    public String getSubscribe_id() {
        return subscribe_id;
    }

    public void setSubscribe_id(String subscribe_id) {
        this.subscribe_id = subscribe_id;
    }

    public String getSubCount() {
        return subCount;
    }

    public void setSubCount(String description) {
        this.subCount = description;
    }
}
