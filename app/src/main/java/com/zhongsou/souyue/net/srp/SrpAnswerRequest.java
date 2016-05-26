package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SRP问答
 * @author chz
 *
 */
public class SrpAnswerRequest extends BaseUrlRequest {

	public SrpAnswerRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	public void addParams(String userName, String md5, String questionId, String kid, String content){
		addParams("userName", userName);
		addParams("md5", md5);
		addParams("q", questionId);
		addParams("k", kid);
		addParams("content", content);
	}
	
	@Override
	public String getUrl() {
		return HOST + "webdata/wenda.answer.groovy";// 问答;
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
