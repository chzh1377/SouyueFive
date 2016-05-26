package com.zhongsou.souyue.module;
/** 
 * @author : zoulu
 * 2014年5月11日
 * 上午11:25:18 
 * 类说明 :
 */
public class NewsBean extends ResponseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	private String url;
	private String category;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
