package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 获取订阅开关POST
 * @author chz
 *
 */
public class SubSwitchPostRequest extends BaseUrlRequest {

	public SubSwitchPostRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public void setParams(String token, String subSwitch){
        addParams("token", token);
        addParams("subSwitch",subSwitch);
    }
    
	@Override
	public String getUrl() {
		return HOST + "subscribe/user.switch.cfg.groovy";  
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
