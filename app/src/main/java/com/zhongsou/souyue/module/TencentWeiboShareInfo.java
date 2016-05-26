package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.content.ShareContent;

import java.io.Serializable;



public class TencentWeiboShareInfo implements Serializable,DontObfuscateInterface{
	private static final long serialVersionUID = 1L;
	public String format;
	public String content;
	public String picUrl;//图片的网络路径
	public String picPath;//图片的本地路径
	public String locIp;
	public String url;
	public ShareContent sharecontent;
	public int dimensionalcode;
	public String callback;
	public String keyword;
	public String srpId;
	public String sharePointUrl;
	
	public TencentWeiboShareInfo() {
	}

	/**
	 * 
	 * sso 考虑到oa2参数貌似没什么用
	 * @param format
	 * @param conent
	 * @param url
	 * @param picPath
	 * @param locIp
	 */
	public TencentWeiboShareInfo(String format, String conent, String url, String picUrl, 
	                             String locIp,String picPath,int dimensionalcode,
	                             String callback,String keyword,String srpId,String sharePointUrl) {
		this.format = format;
		this.content = conent;
		this.picUrl = picUrl;
		this.locIp = locIp;
		this.url = url;
		this.picPath=picPath;
		this.callback=callback;
		this.dimensionalcode=dimensionalcode;
		this.keyword=keyword;
		this.srpId=srpId;
		this.sharePointUrl=sharePointUrl;
	}
	
}
