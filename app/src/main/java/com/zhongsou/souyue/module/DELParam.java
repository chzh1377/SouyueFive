package com.zhongsou.souyue.module;


public class DELParam extends ResponseObject {
    public String groupId;
    public String sid;
    public String srpId;
    public DELParam(String groupId,String sid,String srpId){
        this.groupId=groupId;
        this.sid=sid;
        this.srpId = srpId;
    }
}
