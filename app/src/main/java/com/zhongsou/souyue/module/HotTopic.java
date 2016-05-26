package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class HotTopic extends ResponseObject {

    private String title = ""; // : "话题标题",
    private String url = ""; // : "话题连接"
    private String source = "";// 新浪微博

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String source() {
        return source;
    }

    public void source_$eq(String source) {
        this.source = source;
    }

}
