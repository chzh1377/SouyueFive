package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Comment extends ResponseObject {

    private int commentType = CommentType.COMMENTTYPE_MINE; // 回复类型，int值，1为我评论的，2我回复他人的，3为他人回复我的
    private String content = "";
    private long date = 0;
    private long id = 0;
    private String keyword = "";
    private String srpId = "";
    private String title = "";
    private String url = "";
    private User user = new User();
    private Voice voice = null;
    private Comment replyTo = null;

    public int commentType() {
        return commentType;
    }

    public void commentType_$eq(int commentType) {
        this.commentType = commentType;
    }

    public String content() {
        return content;
    }

    public void content_$eq(String content) {
        this.content = content;
    }

    public long date() {
        return date;
    }

    public void date_$eq(long date) {
        this.date = date;
    }

    public long id() {
        return id;
    }

    public void id_$eq(long id) {
        this.id = id;
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

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public User user() {
        return user;
    }

    public void user_$eq(User user) {
        this.user = user;
    }

    public Voice voice() {
        return voice;
    }

    public void voice_$eq(Voice voice) {
        this.voice = voice;
    }

    public Comment replyTo() {
        return replyTo;
    }

    public void replyTo_$eq(Comment replyTo) {
        this.replyTo = replyTo;
    }

}
