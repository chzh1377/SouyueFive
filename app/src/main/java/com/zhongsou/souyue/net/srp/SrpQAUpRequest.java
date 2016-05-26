package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SRP问答
 * @author chz
 *
 */
public class SrpQAUpRequest extends BaseUrlRequest {

	public SrpQAUpRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	public void addParams(String md5, String questionId, String answerId) {
		addParams("md5", md5);
		addParams("q", questionId);
		addParams("a", answerId);
	}
	
	@Override
	public String getUrl() {
		return  HOST + "webdata/wenda.up.groovy";// 顶！
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
}
