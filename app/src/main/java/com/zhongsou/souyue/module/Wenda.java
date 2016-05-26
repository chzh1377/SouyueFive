package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Wenda extends ResponseObject {

    private String id = "";
    private User user = new User();
    private String content = "";
    private long date = 0;
    private int upCount = 0;
    private int downCount = 0;

    public String id() {
        return id;
    }

    public void id_$eq(String id) {
        this.id = id;
    }

    public User user() {
        return user;
    }

    public void user_$eq(User user) {
        this.user = user;
    }

    public String content() {
        return content;
    }

    public void content_$eq(String content) {
        this.content = content;
    }

    public long date() {
        return date;
    }

    public void date_$eq(long date) {
        this.date = date;
    }

    public int upCount() {
        return upCount;
    }

    public void upCount_$eq(int upCount) {
        this.upCount = upCount;
    }

    public int downCount() {
        return downCount;
    }

    public void downCount_$eq(int downCount) {
        this.downCount = downCount;
    }


}
