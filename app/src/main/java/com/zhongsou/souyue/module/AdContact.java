package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class AdContact extends ResponseObject {

    private String phone = "";
    private String weixin = "";
    private String website = "";
    public String phone() {
        return phone;
    }
    public void phone_$eq(String phone) {
        this.phone = phone;
    }
    public String weixin() {
        return weixin;
    }
    public void weixin_$eq(String weixin) {
        this.weixin = weixin;
    }
    public String website() {
        return website;
    }
    public void website_$eq(String website) {
        this.website = website;
    }

}
