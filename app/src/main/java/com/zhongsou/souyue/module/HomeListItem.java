package com.zhongsou.souyue.module;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HomeListItem extends ResponseObject implements Serializable{
    private String category;

    private String title;

    private String descrption;

    private String isRecommend;

    private String isHost;

    private String id;

    private String date;

    private List<String> images ;

    private String bigImgUrl;

    private String keyword;

    private String srpId;

    private String source;

    private String showMenu;

    private String blog_id;

    private String interest_id;

    private String interestName;

    private String interestLogo;

    private String channelName;

    public void setCategory(String category){
        this.category = category;
    }
    public String getCategory(){
        return this.category;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setDescrption(String descrption){
        this.descrption = descrption;
    }
    public String getDescrption(){
        return this.descrption;
    }
    public void setIsRecommend(String isRecommend){
        this.isRecommend = isRecommend;
    }
    public String getIsRecommend(){
        return this.isRecommend;
    }
    public void setIsHost(String isHost){
        this.isHost = isHost;
    }
    public String getIsHost(){
        return this.isHost;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public void setImage(List<String> image){
        this.images = image;
    }
    public List<String> getImage(){
        return this.images;
    }
    public void setBigImgUrl(String bigImgUrl){
        this.bigImgUrl = bigImgUrl;
    }
    public String getBigImgUrl(){
        return this.bigImgUrl;
    }
    public void setKeyword(String keyword){
        this.keyword = keyword;
    }
    public String getKeyword(){
        return this.keyword;
    }
    public void setSrpId(String srpId){
        this.srpId = srpId;
    }
    public String getSrpId(){
        return this.srpId;
    }
    public void setSource(String source){
        this.source = source;
    }
    public String getSource(){
        return this.source;
    }
    public void setShowMenu(String showMenu){
        this.showMenu = showMenu;
    }
    public String getShowMenu(){
        return this.showMenu;
    }
    public void setBlog_id(String blog_id){
        this.blog_id = blog_id;
    }
    public String getBlog_id(){
        return this.blog_id;
    }
    public void setInterest_id(String interest_id){
        this.interest_id = interest_id;
    }
    public String getInterest_id(){
        return this.interest_id;
    }
    public void setInterestName(String interestName){
        this.interestName = interestName;
    }
    public String getInterestName(){
        return this.interestName;
    }
    public void setChannelName(String channelName){
        this.channelName = channelName;
    }
    public String getChannelName(){
        return this.channelName;
    }
}
