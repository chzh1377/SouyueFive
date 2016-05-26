package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NewsDetail extends ResponseObject {

    private String url = "";
    private String srpId = "";
    private String urlOrig = "";
    private String title = "";
    private String date = "";
    private String source = "";
    private long interest_id;
    private List<String> image = new ArrayList<String>();
    private List<NavigationBar> nav = new ArrayList<NavigationBar>();

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public String urlOrig() {
        return urlOrig;
    }

    public void urlOrig_$eq(String urlOrig) {
        this.urlOrig = urlOrig;
    }

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String date() {
        return date;
    }

    public void date_$eq(String date) {
        this.date = date;
    }

    public String source() {
        return source;
    }

    public void source_$eq(String source) {
        this.source = source;
    }

    public List<String> image() {
        return image;
    }

    public void image_$eq(List<String> image) {
        this.image = image;
    }

    public List<NavigationBar> nav() {
        return nav;
    }

    public void nav_$eq(List<NavigationBar> nav) {
        this.nav = nav;
    }

    public long getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }
}
