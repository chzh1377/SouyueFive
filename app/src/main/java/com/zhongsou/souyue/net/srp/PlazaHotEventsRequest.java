package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SRP问答
 * @author chz
 *
 */
public class PlazaHotEventsRequest extends BaseUrlRequest {

	public PlazaHotEventsRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	public void addParams(String md5, String questionId, String answerId) {
		addParams("md5", md5);
		addParams("q", questionId);
		addParams("a", String.valueOf(answerId));
	}
	
	@Override
	public String getUrl() {
		return HOST + "webdata/wenda.down.groovy";// 踩
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
}
