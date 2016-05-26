package com.zhongsou.souyue.module;

public class SharePointInfo extends ResponseObject{

    private String url;
    private String keyWord;
    private String srpId;
    private String platform;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getKeyWord() {
        return keyWord;
    }
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
    public String getSrpId() {
        return srpId;
    }
    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }
    public String getPlatform() {
        return platform;
    }
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
}
