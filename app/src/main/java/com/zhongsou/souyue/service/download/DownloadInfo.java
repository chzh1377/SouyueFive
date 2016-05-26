package com.zhongsou.souyue.service.download;

import android.app.Notification;
import com.zhongsou.souyue.service.download.DownloadService.OnDownloadListener;

import java.util.HashSet;

public class DownloadInfo {
	public String url;
	public int notifyId;
	public Notification notification;
	public String name = "*.APK";
	public HashSet<String> serviceTask;
	public OnDownloadListener listener;
	private String event;
	private String keyword;
	private String srpId;
	public DownloadInfo(String url, int notifyId, Notification notification) {
		super();
		this.url = url;
		this.notifyId = notifyId;
		this.notification = notification;
	}
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSrpId() {
		return srpId;
	}
	public void setSrpId(String srpId) {
		this.srpId = srpId;
	}
}
