package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wlong on 14-4-28.
 */
public class RecommendInfo implements Serializable, DontObfuscateInterface{


    public static final int RECOMMEND_STATE_UNERCOMMEND = 0; //未审核状态
    public static final int RECOMMEND_STATE_ENSSENCE = 1; //已选入精华区
    public static final int RECOMMEND_STATE_REFUSED = 2; //已拒绝
    public static final int RECOMMEND_STATE_INTEREST = 3; //已选入兴趣圈

    private long recommend_id;
    private long blog_id;
    private long user_id;
    private String title;
    private String brief;
    private String srp_word;
    private String srp_id;
    private long create_time;
    private List<String> images;
    private int audit_state;

    private int text_type;

    private String nickname;
    private String content;
    private String url;

    public int getText_type() {
        return text_type;
    }

    public void setText_type(int text_type) {
        this.text_type = text_type;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }

    public long getRecommend_id() {
        return recommend_id;
    }

    public void setRecommend_id(long recommend_id) {
        this.recommend_id = recommend_id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getNickname() {
        return nickname;
    }
    public void setBlog_id(long blog_id) {
        this.blog_id = blog_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setSrp_word(String srp_word) {
        this.srp_word = srp_word;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setAudit_state(int audit_state) {
        this.audit_state = audit_state;
    }

    public long getBlog_id() {
        return blog_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getBrief() {
        return brief;
    }

    public String getSrp_word() {
        return srp_word;
    }

    public long getCreate_time() {
        return create_time;
    }

    public List<String> getImages() {
        return images;
    }

    public int getAudit_state() {
        return audit_state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
