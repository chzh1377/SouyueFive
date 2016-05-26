package com.zhongsou.souyue.module;

/**
 * User: zhangliang01@zhongsou.com
 * Date: 11/6/13
 * Time: 9:58 AM
 */
public class SRP extends ResponseObject {
    public SRP(String keyword, String srpId) {
        this.keyword = keyword;
        this.srpId = srpId;
    }

    public String keyword;
    public String srpId;
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
