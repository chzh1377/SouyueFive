package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.HashMap;
import java.util.Map;
/**
 * 广场基础请求
 * @author chz
 *
 */
public class PlazaBaseRequest extends BaseUrlRequest {
	
	private String url = "";
	
	public PlazaBaseRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	public void addParams(String requestUrl,HashMap<String, Object> params) {
		 this.url = requestUrl;
		 for (Map.Entry<String,Object> entry:params.entrySet()) {
             if (entry.getValue()!=null) {
                 addParams(entry.getKey(), entry.getValue().toString());
             }
         }
	}
	
	@Override
	public String getUrl() {
		return url;// 踩
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
}
