package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class RssCate extends ResponseObject {

    private long id = 0;
    private String name = "";
    public long id() {
        return id;
    }
    public void id_$eq(long id) {
        this.id = id;
    }
    public String name() {
        return name;
    }
    public void name_$eq(String name) {
        this.name = name;
    }

}
