package com.zhongsou.souyue.enterprise.api;

public class InternalShareContent {
    public String sourceUrl; //源url
    public String title;
    public String description;
    public String imageUrls; // 多张图片需要通过空格分隔开  "http://xxx http://xxx "
    public long publishDate; //发表日期
    public String source; 
    public String keyword;
    public String id;
    public long newsId; //如果newsId >0 则直接可以通过id分享
    
    public String content;
    
    /**
     * 分享到精华区的时候，当从服务端获取的newsid<0时 需要调用此构造函数，
     * @param sourceUrl 
     * @param title
     * @param description
     * @param imageUrls
     * @param publishDate
     * @param source
     * @param keyword
     * @param id
     */
    public InternalShareContent(String sourceUrl, String title, String description, String imageUrls, long publishDate, String source, String keyword, String id) {
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.description = description;
        this.imageUrls = imageUrls;
        this.publishDate = publishDate;
        this.source = source;
        this.keyword = keyword;
        this.id = id;
    }
    
    /**
     * 当分享到精华区 并且 news id > 0时  调用此构造函数
     * @param id
     */
    public InternalShareContent(long id){
        this.newsId = id;
    }

    /**
     * 当分享到搜悦好友时需要调用此函数
     * @param sourceUrl 分享的文章url
     * @param title 
     * @param description
     * @param keyword
     * @param id
     * @param content 分享的文字体部分
     */
    public InternalShareContent(String sourceUrl, String title, String description, String keyword, String id, String content) {
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.description = description;
        this.keyword = keyword;
        this.id = id;
        this.content = content;
    }
}
