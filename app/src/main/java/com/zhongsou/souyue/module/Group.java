package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Group extends ResponseObject {

    private long id = 0;
    private String name = "";
    private String isPushMsg = "";
    
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
    public String isPushMsg() {
        return isPushMsg;
    }
    public void isPushMsg_$eq(String isPushMsg) {
        this.isPushMsg = isPushMsg;
    }

}
