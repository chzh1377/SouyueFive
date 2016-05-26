package com.zhongsou.souyue.module;

public class SubscribeKeywordBack extends ResponseObject{
    /**
     * 
     */
    private static final long serialVersionUID = -3210916472933280927L;
    private String keyword;
    private String sid;
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    
}
