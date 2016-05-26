package com.zhongsou.souyue.module;


@SuppressWarnings("serial")
public class SubscribeGroup extends ResponseObject {
    private String title;
    private long id;
    private String category;
    public boolean isCheck;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

}
