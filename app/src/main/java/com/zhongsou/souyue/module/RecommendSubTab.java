package com.zhongsou.souyue.module;

import java.util.List;

public class RecommendSubTab extends ResponseObject  {
	private String title; //
	private String titleimageurl;
	private String titlecolor;
	private String backgroundimage;
	private List<RecommendTabSubListItem> tablist;
	
	
//	public RecommendSubTab(String title, String titleimageurl, String titlecolor, List<RecommendTabSubListItem> tablist) {
//		super();
//		this.title = title;
//		this.titleimageurl = titleimageurl;
//		this.titlecolor = titlecolor;
//		this.tablist = tablist;
//	}

	public RecommendSubTab(String title, String titleimageurl, String titlecolor, String backgroundimage, List<RecommendTabSubListItem> tablist) {
		this.title = title;
		this.titleimageurl = titleimageurl;
		this.titlecolor = titlecolor;
		this.backgroundimage = backgroundimage;
		this.tablist = tablist;
	}

	public String getBackgroundimage() {
		return backgroundimage;
	}

	public void setBackgroundimage(String backgroundimage) {
		this.backgroundimage = backgroundimage;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleimageurl() {
		return titleimageurl;
	}
	public void setTitleimageurl(String titleimageurl) {
		this.titleimageurl = titleimageurl;
	}
	public String getTitlecolor() {
		return titlecolor;
	}
	public void setTitlecolor(String titlecolor) {
		this.titlecolor = titlecolor;
	}
	public List<RecommendTabSubListItem> getTablist() {
		return tablist;
	}
	public void setTablist(List<RecommendTabSubListItem> tablist) {
		this.tablist = tablist;
	}
	
	
}
