package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.ArrayList;
/**
 * 回复我的dto
 * @author Administrator
 *
 */
public class SubBlog implements  DontObfuscateInterface  {

	private String blog_id;
	private	String interest_id;
	private String mblog_id;
	private long user_id;
	private String content;
	private int good_num;
	private String new_srpid;
	private String nickname;
	private String image_url;
	private String create_time;
	private String update_time;
	private ArrayList<String> images;
	private String comment_id;
	private String voice;
	private long voice_length = 0;

    public long getVoice_length() {
        return voice_length;
    }

    public void setVoice_length(long voice_length) {
        this.voice_length = voice_length;
    }

    public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}


	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getBlog_id() {
		return blog_id;
	}
	public void setBlog_id(String blog_id) {
		this.blog_id = blog_id;
	}
	public String getInterest_id() {
		return interest_id;
	}
	public void setInterest_id(String interest_id) {
		this.interest_id = interest_id;
	}
	public String getMblog_id() {
		return mblog_id;
	}
	public void setMblog_id(String mblog_id) {
		this.mblog_id = mblog_id;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getGood_num() {
		return good_num;
	}
	public void setGood_num(int good_num) {
		this.good_num = good_num;
	}
	public String getNew_srpid() {
		return new_srpid;
	}
	public void setNew_srpid(String new_srpid) {
		this.new_srpid = new_srpid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public ArrayList<String> getImages() {
		return images;
	}
	public void setImages(ArrayList<String> images) {
		this.images = images;
	}
	
	
	
	
}
