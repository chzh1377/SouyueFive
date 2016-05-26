package com.zhongsou.souyue.module;


import com.zhongsou.souyue.DontObfuscateInterface;

@SuppressWarnings("serial")
public class User extends ResponseObject implements DontObfuscateInterface {
    private String bigImage="";
    private String image = "";
    private String email = "";
    private long userId = 0;
    private String name = "";
    private String token = "";
    private String userType = "";
    private String url = "";
    private String userName = "";
    private String bgUrl = "";
    private String signature = "";
    private boolean freeTrial = false; // 免审用户
    private boolean loved = false; // 喜闻乐见的免审账号
    private int giveDrink = 0;//是否赠送饮料 大于0表示赠送
    private ActivityDrink activityDrink = new ActivityDrink();
    // add by trade start
    private String uid = "";
    private String syuid = "";
    private int sex;
    private String user_level;
    private String user_level_title;
    private String user_level_time;

    //add by yinguanping 新加密协议
    private String openid;//"用户唯一标识"
    private String opid;//"当前登录标识id",
    private String auth_token;//"当前登录凭证",
    private String private_key;//"私钥为加密时使用"
    private String appId;

    //等级过期时间

    private boolean expires;
    private static final long DEFAULT_USER_L_CACHE_TIME = 60 * 60 * 1000L;//

    public boolean isExpires() {
        if (getUser_level_time() == null) {
            this.expires = true;
        } else {
            this.expires = System.currentTimeMillis() -
                    Double.parseDouble(getUser_level_time()) > DEFAULT_USER_L_CACHE_TIME ? true : false;
        }
        return expires;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getUser_level() {
        return this.user_level;
    }

    public void user_level_$eq(String user_level) {
        this.user_level = user_level;
    }

    public String getUser_level_title() {
        return this.user_level_title;
    }

    public void user_level_title_$eq(String user_level_title) {
        this.user_level_title = user_level_title;
    }

    public String getUser_level_time() {
        return this.user_level_time;
    }

    public void user_level_time_$eq(String user_level_time) {
        this.user_level_time = user_level_time;
    }

    public String syuid() {
        return syuid;
    }

    public void syuid_$eq(String syuid) {
        this.syuid = syuid;
    }

    public String uid() {
        return uid;
    }

    public void uid_$eq(String uid) {
        this.uid = uid;
    }

    // add by trade end
    public boolean freeTrial() {
        return freeTrial;
    }

    public void freeTrial_$eq(boolean freeTrial) {
        this.freeTrial = freeTrial;
    }

    public boolean loved() {
        return loved;
    }

    public void loved_$eq(boolean loved) {
        this.loved = loved;
    }

    public String signature() {
        return signature;
    }

    public void signature_$eq(String signature) {
        this.signature = signature;
    }

    public String image() {
        return image;
    }

    public void image_$eq(String image) {
        this.image = image;
    }

    public String email() {
        return email;
    }

    public void email_$eq(String email) {
        this.email = email;
    }

    public long userId() {
        return userId;
    }

    public void userId_$eq(long userId) {
        this.userId = userId;
    }

    public String name() {
        return name;
    }

    public void name_$eq(String name) {
        this.name = name;
    }

    public String token() {
        return token;
    }

    public void token_$eq(String token) {
        this.token = token;
    }

    public String userType() {
        return userType;
    }

    public void userType_$eq(String userType) {
        this.userType = userType;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String userName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void userName_$eq(String userName) {
        this.userName = userName;
    }

    public String bgUrl() {
        return bgUrl;
    }

    public void bgUrl_$eq(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public int getGiveDrink() {
        return giveDrink;
    }

    public void setGiveDrink(int giveDrink) {
        this.giveDrink = giveDrink;
    }

    public ActivityDrink getActivityDrink() {
        return activityDrink;
    }

    public void setActivityDrink(ActivityDrink activityDrink) {
        this.activityDrink = activityDrink;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

}
