package com.zhongsou.souyue.module;

import java.util.List;

/**
 * User: zhangliang01@zhongsou.com
 * Date: 11/1/13
 * Time: 12:42 PM
 */
public class RecDYPage extends ResponseObject {
    private String title;
    private String category;
    private List<NavigationBar> nav;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RecDYPage(String title, String category, List<NavigationBar> nav) {
        this.title = title;
        this.category = category;
        this.nav = nav;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<NavigationBar> getNav() {
        return nav;
    }

    public void setNav(List<NavigationBar> nav) {
        this.nav = nav;
    }
}
