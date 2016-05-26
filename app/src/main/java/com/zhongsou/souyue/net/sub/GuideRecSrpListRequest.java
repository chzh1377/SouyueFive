package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * 引导页推荐SRP列表
 * @author chz
 *
 */
public class GuideRecSrpListRequest extends BaseUrlRequest {

	public GuideRecSrpListRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

	/**
	 * 推荐SRP列表
	 */
    public void setParams(){
        addParams("imei", DeviceInfo.getDeviceId());
        addParams("vc",DeviceInfo.getAppVersion());
        addParams("token",  SYUserManager.getInstance().getToken());
        addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
    }
	@Override
	public String getUrl() {
		return HOST + "subscribe/subscribe.guide.list.groovy";
	}

	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
