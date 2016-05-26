package com.zhongsou.souyue.module;


public class QQ_Oauth2AccessToken extends ResponseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2888956137740789544L;
	private String openid;
	private String access_token;
	private long expires_in;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

}
