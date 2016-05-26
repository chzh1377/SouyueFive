package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SPR问答 - 同问
 * @author chz
 *
 */
public class SrpQASameRequest extends BaseUrlRequest {

	public SrpQASameRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

	public void addParams(String md5, String questionId,
			String kid){
		addParams("md5", md5);
		addParams("q", questionId);
		addParams("k", kid);
	}
	
	@Override
	public String getUrl() {
		return HOST + "webdata/wenda.sameAsk.groovy";
	}

	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
}
