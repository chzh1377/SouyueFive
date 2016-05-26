package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class ADInfo extends ResponseObject {
    private String url = "";
    private boolean download = false;
    private String event = "";
    
    //4.2.2广告升级新增  souyue.onclick://{“category”:”getAdList”,"keyword":"关键词",”srpId”:””,"callback":""}
    //  souyue.onclick://{“category”:”AdClick”,"keyword":"关键词",”srpId”:””,"url":"列表中的url字段","event":"列表中的event字段","type":"列表中的type字段"}
    private String category = "";
    private String keyword = "";
    private String srpId = "";
    private String callback = "";
    private String type = "";
    
    public static final int HOME_PAGE_LOCATION = 1;
    public static final int SPR_PAGE_LOCATION = 2;
    public static final int SRP_DETAIL_PAGE_LOCATION = 3;
    
    public String url() {
        return url;
    }
    public void url_$eq(String url) {
        this.url = url;
    }
    public boolean download() {
        return download;
    }
    public void download_$eq(boolean download) {
        this.download = download;
    }
    public String event() {
        return event;
    }
    public void event_$eq(String event) {
        this.event = event;
    }
    public String category() {
        return category;
    }
    public void category_$eq(String category) {
        this.category = category;
    }
    public String keyword() {
        return keyword;
    }
    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }
    public String srpId() {
        return srpId;
    }
    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }
    public String callback() {
        return callback;
    }
    public void callback_$eq(String callback) {
        this.callback = callback;
    }
    public String type() {
        return type;
    }
    public void type_$eq(String type) {
        this.type = type;
    }
}
