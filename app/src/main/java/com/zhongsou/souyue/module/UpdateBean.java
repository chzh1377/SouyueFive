package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

public class UpdateBean implements DontObfuscateInterface{
	private String version;
	private String url;
	private String minVersion;
	private String[] disable;
	private Discribe[] desc;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMinVersion() {
		return minVersion;
	}
	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}
	public String[] getDisable() {
		return disable;
	}
	public void setDisable(String[] disable) {
		this.disable = disable;
	}
	public Discribe[] getDesc() {
		return desc;
	}
	public void setDesc(Discribe[] desc) {
		this.desc = desc;
	}
	
}
