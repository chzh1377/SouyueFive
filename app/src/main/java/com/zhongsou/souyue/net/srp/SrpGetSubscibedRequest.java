package com.zhongsou.souyue.net.srp;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.GroupKeywordItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 获取已经订阅的SRP
 * @author chz
 *
 */
public class SrpGetSubscibedRequest extends BaseUrlRequest {
	
	private String url = "";
	
	public SrpGetSubscibedRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	@Override
	public String getUrl() {
		return HOST + "subscribe/subscribe.list.groovy?subType=1";
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
	@Override
	public Object doParse(CVolleyRequest requet, String res) throws Exception {
		HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
		ArrayList<GroupKeywordItem> beans = new Gson().fromJson(
				response.getBodyArray(),
				new TypeToken<ArrayList<GroupKeywordItem>>() {
				}.getType());
		return beans;
	}
}
