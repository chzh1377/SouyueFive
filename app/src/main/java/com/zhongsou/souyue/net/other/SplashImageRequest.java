package com.zhongsou.souyue.net.other;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;
/**
 * 获取启动页广告图
 * @author chz
 *
 */
public class SplashImageRequest extends BaseUrlRequest {

	public SplashImageRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	public SplashImageRequest setParams(){
        addParams("machineType", "android");
        addParams("appName", DeviceInfo.appName);
        addParams("clientTime", System.currentTimeMillis()+"");
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("rwidth", String.valueOf(Utils.getScreenWidth()));
        addParams("rheight",String.valueOf(Utils.getScreenHeight()));
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        return this;
    }
	
	@Override
	public String getUrl() {
		return HOST + "webdata/startImg.config.groovy";
	}

	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
