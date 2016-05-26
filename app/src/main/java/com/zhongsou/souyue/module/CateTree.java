package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CateTree extends ResponseObject {

    private String title = "";
    private long id = 0;
    private String srpId = "";
    private boolean hasSubscribed = false;
    private String sid;
    private String category;

    public String category() {
        return category;
    }

    public void category_$eq(String category) {
        this.category = category;
    }

    private List<CateTree> child = new ArrayList<CateTree>();
    
    public String sid() {
        return sid;
    }

    public void sid_$eq(String sid) {
        this.sid = sid;
    }

    public void hassubscribed_$eq(boolean sub) {
        hasSubscribed = sub;
    }

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public long id() {
        return id;
    }

    public void id_$eq(long id) {
        this.id = id;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public List<CateTree> child() {
        return child;
    }

    public void child_$eq(List<CateTree> child) {
        this.child = child;
    }

    public boolean hasSubscribed() {
        return hasSubscribed;
    }

}
