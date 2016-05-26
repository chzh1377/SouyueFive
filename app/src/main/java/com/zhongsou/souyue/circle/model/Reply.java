package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Desc: 帖子回复类
 * User: tiansj
 * DateTime: 14-4-18 下午4:57
 */
public class Reply implements Serializable, DontObfuscateInterface{

    private long reply_id;
    private String content;
    private String images;
    private String voice;
    private long voice_length;
    private long user_id;
    private String nickname;
    private String reply_time;
    private int is_host;
    private int is_current_reply;

    public int getIs_current_reply() {
        return is_current_reply;
    }

    public void setIs_current_reply(int is_current_reply) {
        this.is_current_reply = is_current_reply;
    }

    public long getReply_id() {
        return reply_id;
    }

    public void setReply_id(long reply_id) {
        this.reply_id = reply_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getReply_time() {
        return reply_time;
    }

    public void setReply_time(String reply_time) {
        this.reply_time = reply_time;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getVoice_length() {
        return voice_length;
    }

    public void setVoice_length(long voice_length) {
        this.voice_length = voice_length;
    }

    public int getIs_host() {
        return is_host;
    }

    public void setIs_host(int is_host) {
        this.is_host = is_host;
    }
}
