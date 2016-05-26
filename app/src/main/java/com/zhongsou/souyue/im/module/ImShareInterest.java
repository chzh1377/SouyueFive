package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.ResponseObject;

public class ImShareInterest extends ResponseObject implements DontObfuscateInterface {
	private static final long serialVersionUID = 1L;
	private String interest_id;
	private String blog_id;
	private String blog_title;
	private String blog_content;
	private String blog_logo;
	private long user_id;
	private int is_prime;
	private int top_status;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getIs_prime() {
		return is_prime;
	}

	public void setIs_prime(int is_prime) {
		this.is_prime = is_prime;
	}

	public int getTop_status() {
		return top_status;
	}

	public void setTop_status(int top_status) {
		this.top_status = top_status;
	}

	public String getInterest_id() {
		return interest_id;
	}

	public void setInterest_id(String interest_id) {
		this.interest_id = interest_id;
	}

	public String getBlog_id() {
		return blog_id;
	}

	public void setBlog_id(String blog_id) {
		this.blog_id = blog_id;
	}

	public String getBlog_title() {
		return blog_title;
	}

	public void setBlog_title(String blog_title) {
		this.blog_title = blog_title;
	}

	public String getBlog_content() {
		return blog_content;
	}

	public void setBlog_content(String blog_content) {
		this.blog_content = blog_content;
	}

	public String getBlog_logo() {
		return blog_logo;
	}

	public void setBlog_logo(String blog_logo) {
		this.blog_logo = blog_logo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
