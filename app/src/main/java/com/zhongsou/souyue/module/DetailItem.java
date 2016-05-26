package com.zhongsou.souyue.module;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DetailItem extends ResponseObject implements Serializable{
    public static final int SKIP_TO_COMMENT = 1;
    private String keyword;
    private String srpId;
    private String url;
    private long blogId;
    private long pushId;
    private String title;
    private String description;
    private List<String> images;
    private String channel;
    private String id;//列表唯一标识
    private String category;
    private long interestId;
    private String interestType;
    private int optionRoleType;
    private String clickFrom;//表示是条目点击还是其他点击
    private String pushFrom;//表示是哪里推送过来
    private String msgId;//消息唯一id
    private int skip;//跳转标识

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    private String source; // 来源
    private String pubTime; // 发布时间 long
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

    public String getId() {
        return id;
    }

    public void setId(String date) {
        this.id = date;
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

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public static DetailItem SearchResultToDetailItem(SearchResultItem item){
        DetailItem i = new DetailItem();
        i.setCategory(item.category());
        i.setTitle(item.title());
        i.setBlogId(item.getBlog_id());
        i.setChannel(item.getChannel());
        i.setId(item.date());
        i.setDescription(item.description());
        i.setImages(item.image());
        i.setInterestId(item.getInterest_id());
        i.setInterestType(item.getInterestType());
        i.setKeyword(item.keyword());
        i.setSrpId(item.srpId());
        i.setOptionRoleType(item.getmOptionRoleType());
        i.setPushId(item.pushId());
        i.setUrl(item.url());
        i.setStatisticsJumpPosition(item.getStatisticsJumpPosition());
        i.setSource(item.source());
        i.setPubTime(item.pubTime());
        i.setPushFrom(item.getPushFrom());
        i.setClickFrom(item.getClickFrom());
        i.setMsgId(item.getMsgId());
        return i;
    }

    public static SearchResultItem SearchResultToDetailItem(DetailItem item){
        SearchResultItem i = new SearchResultItem();
        i.category_$eq(item.getCategory());
        i.title_$eq(item.getTitle());
        i.setBlog_id(item.getBlogId());
        i.setChannel(item.getChannel());
        i.date_$eq(item.getId());
        i.description_$eq(item.getDescription());
        i.image_$eq(item.getImages());
        i.setInterest_id(item.getInterestId());
        i.setInterestType(item.getInterestType());
        i.keyword_$eq(item.getKeyword());
        i.srpId_$eq(item.getSrpId());
        i.setmOptionRoleType(item.getOptionRoleType());
        i.pushId_$eq(item.getPushId());
        i.url_$eq(item.getUrl());
        i.setStatisticsJumpPosition(item.getStatisticsJumpPosition());
        return i;
    }
}
