package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Voice extends ResponseObject {

    private String url = "";
    private long length = 0;

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public long length() {
        return length;
    }

    public void length_$eq(long length) {
        this.length = length;
    }

}
