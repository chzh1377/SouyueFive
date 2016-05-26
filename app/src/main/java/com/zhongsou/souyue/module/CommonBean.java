package com.zhongsou.souyue.module;

import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

/**
 * Created by Administrator on 2014/8/27.
 * wzyax@qq.com
 */
public class CommonBean extends ResponseObject {
    public static final int NEWS_TYPE_FOCUS = -1;// 新闻布局类型，焦点
    public static final int NEWS_TYPE_FOCUS_NOPIC = -2;//新闻焦点，但无图片
    public static final int NEWS_TYPE_HEADLINETOP = -3;// 新闻布局类型，要闻
    public static final int NEWS_TYPE_NORMAL = 0; // 新闻布局类型，无图
    public static final int NEWS_TYPE_IMAGE_ONE = 1; // 新闻布局类型，一张
    public static final int NEWS_TYPE_IMAGE_TWO = 2; // 新闻布局类型，两张
    public static final int NEWS_TYPE_IMAGE_THREE = 3; // 新闻布局类型，三张
    private String category;
    private String description;
    private String id;//可能含有字母
    private boolean isRecommend;
    private boolean isHost;
    private String title;
    private String url;
    private List<String> image;

    //headline
    private String keyword;
    private long pubTime;
    private String source;
    private String srpId;
    private String type;



    //interest
    private int interest_id;
    private int blog_id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getPubTime() {
        return pubTime;
    }

    public void setPubTime(long pubTime) {
        this.pubTime = pubTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(int interest_id) {
        this.interest_id = interest_id;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    private int layoutType = SearchResult.NEWS_TYPE_NORMAL; // 新闻布局类型

    public int getLayoutType() {
        if(image==null)
            return 0;
        else
        return image.size()>3?3:image.size();
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public CommonBean(HttpJsonResponse response) {

    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public boolean isRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean isRecommend) {
        this.isRecommend = isRecommend;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
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
}
