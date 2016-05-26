package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/** 
 * @author : zoulu
 * 2014年5月10日
 * 下午3:27:36 
 * 类说明 :首页导航用来记录页数用
 */
public class HomePager implements Serializable ,DontObfuscateInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pagerNumber;
	private String title;
	private String category;
	private String url;
	private String srpId;
	
	public int getPagerNumber() {
		return pagerNumber;
	}
	public void setPagerNumber(int pagerNumber) {
		this.pagerNumber = pagerNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSrpId() {
		return srpId;
	}
	public void setSrpId(String srpId) {
		this.srpId = srpId;
	}
}
