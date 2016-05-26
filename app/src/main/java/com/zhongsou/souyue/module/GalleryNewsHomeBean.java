package com.zhongsou.souyue.module;

import android.text.TextUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * 图集类型的跳转能拿到的数据
 */
@SuppressWarnings("serial")
public class GalleryNewsHomeBean extends ResponseObject {

    public static final String NEWS_TYPE = "图集";    //新闻类型 - 统计使用
    private String srpId; //- 来源列表
    private List<String> image; // 图片列表，分享用 - 来源列表
    private String keyword; // 关键词 - 来源列表
    private String url; // url地址 - 来源列表

    private String title; // - 标题 - 来源请求
    private String description; // 描述 - 来源请求
    private String source; // 来源 - 来源请求
    private String pubTime; // 发布时间 long - 来源请求

    private String channel; // 频道，统计用.
    private String clickFrom;//表示是条目点击还是其他点击
    private String pushFrom;//表示是哪里推送过来
    private String msgId;//消息唯一id
    private String category; //区分文章的类型，统计用
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {

        if(StringUtils.isNotEmpty(category)){
            this.category = category;
        }else{
            this.category = NEWS_TYPE;
        }
    }


    public String getClickFrom() {
        return clickFrom;
    }

    public void setClickFrom(String clickFrom) {
        this.clickFrom = clickFrom;
    }

    public String getPushFrom() {
        return pushFrom;
    }

    public void setPushFrom(String pushFrom) {
        this.pushFrom = pushFrom;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public GalleryNewsHomeBean() {
    }

    public static GalleryNewsHomeBean SearchResult2GalleryHomeBean(SearchResultItem item) {
        GalleryNewsHomeBean bean = new GalleryNewsHomeBean();
        bean.setSrpId(item.srpId()); //必须
        bean.setTitle(item.title()); //必须
        bean.setDescription(item.description()); //必须
        bean.setUrl(item.url()); //必须
        bean.setImage(item.image());//分享用
        bean.setSource(item.source());//评论页面用
        bean.setKeyword(item.keyword());//必须
        bean.setPubTime((!TextUtils.isEmpty(item.pubTime())) ? item.pubTime() : item.date()); //评论页面用
        bean.setChannel(item.getChannel());//统计用
        bean.setCategory(item.category());
        return bean;
    }

    public GalleryNewsHomeBean(String srpId, String title, String description, String url, List<String> image, String source, String keyword, String pubTime, String channel) {
        this.srpId = srpId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.image = image;
        this.source = source;
        this.keyword = keyword;
        this.pubTime = pubTime;
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

}
