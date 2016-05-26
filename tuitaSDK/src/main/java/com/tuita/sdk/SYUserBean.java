package com.tuita.sdk;

@SuppressWarnings("serial")
public class SYUserBean extends ResponseObject {

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
    // add by trade start
    private String uid = "";
    private String syuid = "";

    private int sex;

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

}
