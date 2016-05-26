package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class SearchTop extends ResponseObject {
    private String keyword = "";
    private String srpId = "";
    private String title = "";

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

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

}
