package com.zhongsou.souyue.module;

@SuppressWarnings("serial")

/**
 * {
 "state":1,
 "body":"<html>msg<html/>"
 }
 微信红包请求接口返回
 */
public class WXShareBean extends ResponseObject {
    public static final int STATE_GETSUCCESS = 1; // 领取成功
    public static final int STATE_GETFAIL = 2; // 领取失败
    public static final int STATE_ALREADYGET = 3; // 已经领取

    private int state;
    private String body;
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
}
