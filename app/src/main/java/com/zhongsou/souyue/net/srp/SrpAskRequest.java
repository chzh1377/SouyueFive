package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SRP问答-踩
 * @author chz
 *
 */
public class SrpAskRequest extends BaseUrlRequest {
	
	private Boolean mRefresh;
	
	public SrpAskRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	public void addParams(String userName, String md5,
			String kid, String keyword, String srpId, String content){
		addParams("userName", userName);
		addParams("md5", md5);
		addParams("k", kid);
		addParams("content", content);
		addParams("keyword", keyword);
		addParams("srpId", srpId);
	}
	
	public void addParams(String url, int start,int count,Boolean refresh){
		this.mRefresh = refresh;
		addParams("start", start+"");
		addParams("count", count+"");
		
	}
	
	@Override
	public String getUrl() {
		return HOST + "webdata/wenda.ask.groovy";
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
	
	@Override
	public boolean isForceRefresh() {
		return mRefresh;
	}
}
