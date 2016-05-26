package com.zhongsou.souyue.module;

import java.util.List;

@SuppressWarnings("serial")
public class GalleryCommentDetailItem extends ResponseObject {
    private String keyword;
    private String srpId;
    private String url;
    private long blogId;
    private long pushId;
    private String title;
    private String description;
    private List<String> images;
    private String channel;
    private String date;
    private String category;
    private long interestId;
    private String interestType;
    private int optionRoleType;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private String source;
    public String nickname;
    public int is_bantalk;
//    public String image;
    public int mRoletype;
    public long pubTime;



    //统计
    private String statisticsJumpPosition;//推送新闻到达统计标识

    public String getStatisticsJumpPosition() {
        return statisticsJumpPosition;
    }

    public void setStatisticsJumpPosition(String statisticsJumpPosition) {
        this.statisticsJumpPosition = statisticsJumpPosition;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public long getPushId() {
        return pushId;
    }

    public void setPushId(long pushId) {
        this.pushId = pushId;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public int getOptionRoleType() {
        return optionRoleType;
    }

    public void setOptionRoleType(int optionRoleType) {
        this.optionRoleType = optionRoleType;
    }


}
