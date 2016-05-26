package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.ResponseObject;

public class ImShareNews extends ResponseObject implements DontObfuscateInterface {
    private static final long serialVersionUID = 1L;
    public static final String NEWSCONTENT = "newscontent";
    private String keyword;
    private String srpid;
    private String title;
    private String url;
    private String imgurl;
    public ImShareNews(){}
    public ImShareNews(String keyword,String srpid,
                       String title,String url,String imgurl){
        this.keyword=keyword;
        this.srpid=srpid;
        this.title=title;
        this.url=url;
        this.imgurl=imgurl;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getSrpid() {
        return srpid;
    }
    public void setSrpid(String sprid) {
        this.srpid = sprid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getImgurl() {
        return imgurl;
    }
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    

}
