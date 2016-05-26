package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Favorite extends ResponseObject {

    private long id = 0;
    private String url = "";
    private String title = "";
    private String image = "";
    private String description = "";
    private String date = "";
    private String source = "";
    private String keyword = "";
    private String srpId = "";
    private String createTime = "";
    private String category;
    private String upCount = "0";
    
	private int favoriteLayoutType = LAYOUT_TYPE_NO_PIC; // 收藏布局类型

	public static final int LAYOUT_TYPE_ONE_PIC = 1;//收藏 布局一张图
    public static final int LAYOUT_TYPE_NO_PIC = 0;//收藏 无图

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //兴趣圈帖子相关字段
    private int dataType;   // 2帖子，1新闻
    private long interestId;
    private long blogId;
    private int isPrime;    // 是否加精
    private int topStatus;  // 是否置顶
    private long userId;    // 发帖人ID

    public int getTopStatus() {
        return topStatus;
    }

    public void setTopStatus(int topStatus) {
        this.topStatus = topStatus;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getIsPrime() {
        return isPrime;
    }

    public void setIsPrime(int isPrime) {
        this.isPrime = isPrime;
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public long id() {
        return id;
    }

    public void id_$eq(long id) {
        this.id = id;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String image() {
        return image;
    }

    public void image_$eq(String image) {
        this.image = image;
    }

    public String description() {
        return description;
    }

    public void description_$eq(String description) {
        this.description = description;
    }

    public String date() {
        return date;
    }

    public void date_$eq(String date) {
        this.date = date;
    }

    public String createTime() {
        return createTime;
    }

    public void createTime_$eq(String createTime) {
        this.createTime = createTime;
    }

    public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public String source() {
        return source;
    }

    public void source_$eq(String source) {
        this.source = source;
    }
    
    public int getFavoriteLayoutType() {
		return favoriteLayoutType;
	}

	public void setFavoriteLayoutType(int favoriteLayoutType) {
		this.favoriteLayoutType = favoriteLayoutType;
	}
	
	public String getUpCount() {
		return upCount;
	}

	public void setUpCount(String upCount) {
		this.upCount = upCount;
	}

}
