package com.zhongsou.souyue.im.download;

/**
 * 
 * 自定义的一个记载下载器详细信息的类
 * 
 * 一个下载器对应一个LoadInfo
 * 
 * @author wangqiang
 * 
 * 
 */

public class LoadInfo

{

	public int fileSize;// 文件大小
	private int complete;// 完成度
	private String urlstring;// 文件下载路径
	private String packageId; // 下载包度id

	/**
	 * fileSize文件大小 complete完成度 urlstring下载地址
	 * **/

	public LoadInfo(int fileSize, int complete, String urlstring,
			String packageId) {
		this.fileSize = fileSize;
		this.complete = complete;
		this.urlstring = urlstring;
		this.packageId = packageId;
	}

	public LoadInfo() {
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}
	
	public String getUrlstring() {
		return urlstring;
	}

	public void setUrlstring(String urlstring) {
		this.urlstring = urlstring;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	@Override
	public String toString() {
		return "LoadInfo [fileSize=" + fileSize + ", complete=" + complete
		+ ", urlstring=" + urlstring + ", packageid=" + packageId + "]";
	}
}