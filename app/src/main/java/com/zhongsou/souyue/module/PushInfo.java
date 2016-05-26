package com.zhongsou.souyue.module;


import com.zhongsou.souyue.circle.model.CircleResponseResultItem;

@SuppressWarnings("serial")
public class PushInfo extends ResponseObject {

    private String keyword = "";
    private String pushId = "";
    private String srpId = "";
    private String url = "";
    private String g = "";
    private String slot;
    private String im;
    private String jumpType = "";

    private String statisticsJumpPosition = "";     //统计跳转来源

    private String pushFrom;
    private String clickFrom;
    private String mid;

    public String getPushFrom() {
        return pushFrom;
    }

    public void setPushFrom(String pushFrom) {
        this.pushFrom = pushFrom;
    }

    public String getClickFrom() {
        return clickFrom;
    }

    public void setClickFrom(String clickFrom) {
        this.clickFrom = clickFrom;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
    public String getStatisticsJumpPosition() {
        return statisticsJumpPosition;
    }

    public void setStatisticsJumpPosition(String statisticsJumpPosition) {
        this.statisticsJumpPosition = statisticsJumpPosition;
    }

//    private String ifextract = "";

	private CircleResponseResultItem interestBlog; // 兴趣圈帖子
	
	private GalleryNewsHomeBean mGalleryNews;//图集
    
    public GalleryNewsHomeBean getGalleryNews() {
		return mGalleryNews;
	}

	public void setGalleryNews(GalleryNewsHomeBean mGalleryNews) {
		this.mGalleryNews = mGalleryNews;
	}

	public String getJumpType() {
		return jumpType;
	}

	public void setJumpType(String jumpType) {
		this.jumpType = jumpType;
	}

	public boolean isIm(){
        return this.im != null && "im".equals(im);
    }
    
    public String getIm(){
        return this.im;
    }
    
    public void setIm(String im){
        this.im = im;
    }

	public String getSlot() {
		return this.slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}
	public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }

    public String pushId() {
        return pushId;
    }

    public void pushId_$eq(String pushId) {
        this.pushId = pushId;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String g() {
        return g;
    }

    public void g_$eq(String g) {
        this.g = g;
    }


    public CircleResponseResultItem getInterestBlog() {
        return interestBlog;
    }

    public void setInterestBlog(CircleResponseResultItem interestBlog) {
        this.interestBlog = interestBlog;
    }
    
    /*public String getIfextract() {
		return ifextract;
	}

	public void setIfextract(String ifextract) {
		this.ifextract = ifextract;
	}*/

}
