package com.zhongsou.souyue.module;

/**
 * @author YanBin
 * @version V5.2
 * @project trunk
 * @Description 搜索 - 热搜榜item
 * @date 2016/4/23
 */
public class HotSearchInfo {

//    "title":$String,        标题
//    "keyword":$String,      关键字
//    "url" : $String,        新闻url

    private String title;
    private String keyword;
    private String url;
    private String srpId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }
}
