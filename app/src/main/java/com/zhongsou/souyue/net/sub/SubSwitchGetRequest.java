package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 获取订阅开关GET
 * @author chz
 *
 */
public class SubSwitchGetRequest extends BaseUrlRequest {

	public SubSwitchGetRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public void setParams(String token){
        addParams("token", token);
    }
    
	@Override
	public String getUrl() {
		return HOST + "subscribe/user.switch.query.groovy";  // 获取订阅开关
	}
}
