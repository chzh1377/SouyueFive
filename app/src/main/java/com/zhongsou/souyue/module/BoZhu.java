package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

@SuppressWarnings("serial")
public class BoZhu extends ResponseObject implements DontObfuscateInterface {

    private User user = new User();
    private long follow = 0; // 关注数
    private long fans = 0; // 粉丝数
    private long weibo = 0; // 微博数
    private String source = ""; // "新浪微博"//来源
    private Weibo newWeibo = new Weibo();

    public User user() {
        return user;
    }

    public void user_$eq(User user) {
        this.user = user;
    }

    public long follow() {
        return follow;
    }

    public void follow_$eq(long follow) {
        this.follow = follow;
    }

    public long fans() {
        return fans;
    }

    public void fans_$eq(long fans) {
        this.fans = fans;
    }

    public long weibo() {
        return weibo;
    }

    public void weibo_$eq(long weibo) {
        this.weibo = weibo;
    }

    public String source() {
        return source;
    }

    public void source_$eq(String source) {
        this.source = source;
    }

    public Weibo newWeibo() {
        return newWeibo;
    }

    public void newWeibo_$eq(Weibo newWeibo) {
        this.newWeibo = newWeibo;
    }

}
