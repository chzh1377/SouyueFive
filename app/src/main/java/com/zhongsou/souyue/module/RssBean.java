package com.zhongsou.souyue.module;
/** 
 * @author : zoulu
 * 2014年5月11日
 * 下午2:12:22 
 * 类说明 :
 */
public class RssBean extends ResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String srpId;
	private String keyword;
	private String url;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSrpId() {
		return srpId;
	}
	public void setSrpId(String srpId) {
		this.srpId = srpId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
