package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class NewsCount extends ResponseObject {

    private long newsId = 0; //
    private int commentsCount = 0; // 评论数
    private int upCount = 0;//
    private boolean hasFavorited = false; // 是否收藏过
    private boolean hasUp = false; // 是否顶，新增加的字段
    public long newsId() {
        return newsId;
    }
    public void newsId_$eq(long newsId) {
        this.newsId = newsId;
    }
    public int commentsCount() {
        return commentsCount;
    }
    public void commentsCount_$eq(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    public boolean hasFavorited() {
        return hasFavorited;
    }
    public void hasFavorited_$eq(boolean hasFavorited) {
        this.hasFavorited = hasFavorited;
    }
    public boolean hasUp() {
        return hasUp;
    }
    public void hasUp_$eq(boolean hasUp) {
        this.hasUp = hasUp;
    }
    public int upCount(){
        return upCount;
    }
    public void upCount_$eq(int upCount){
        this.upCount = upCount;
    }

}
