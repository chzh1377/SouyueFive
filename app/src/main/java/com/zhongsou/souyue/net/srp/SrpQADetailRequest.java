package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 问答 - 详情
 * @author chz
 *
 */
public class SrpQADetailRequest extends BaseUrlRequest {

	public SrpQADetailRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

	public void addParams(String md5, String questionId,
			String replyLastId){
		addParams("md5", md5);
		addParams("q", String.valueOf(questionId));
		addParams("lastId", String.valueOf(replyLastId));
	}
	
	@Override
	public String getUrl() {
		return HOST + "webdata/wenda.detail.groovy";// 问答详情;
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
}
