package com.zhongsou.souyue.net.sub;

import java.util.List;

import com.google.gson.Gson;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.GuideRecommendSRP;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * 引导页订阅SRP
 * @author chz
 *
 */
public class GuideSubSrpRequest extends BaseUrlRequest {

	public GuideSubSrpRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
    public void setParams(String token,List<GuideRecommendSRP> array){
        addParams("imei",DeviceInfo.getDeviceId());
        addParams("vc",DeviceInfo.getAppVersion());
        addParams("token", token);
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        addParams("subscribeData", new Gson().toJson(array));
    }
	@Override
	public String getUrl() {
		return HOST + "subscribe/subscribe.guide.groovy";
	}

	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
