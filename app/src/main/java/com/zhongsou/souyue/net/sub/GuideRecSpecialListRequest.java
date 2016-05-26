package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 引导页推荐专题列表
 * @author chz
 *
 */
public class GuideRecSpecialListRequest extends BaseUrlRequest {

	public GuideRecSpecialListRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

	 /**
     * 专题列表
     * @param token
     * @param installApps
     */
    public void setParams(String token,String installApps){
        addParams("imei", DeviceInfo.getDeviceId());
        addParams("vc",DeviceInfo.getAppVersion());
        addParams("token", token);
        addParams("installApps", installApps);
        addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
    }
    
	@Override
	public String getUrl() {
		return HOST + "webdata/guide.novice.groovy";
	}

	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
