package com.zhongsou.souyue.net.srp;

import java.util.HashMap;
import java.util.Map;

import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.Utils;

/**
 * 广告点击请求
 * @author chz
 *
 */
public class AdClickRequest extends AdProtocol {
	
	private String url;//点击广告url
	
	public AdClickRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

	public void addParams(String keyword, String srpId, int page,
			String source, String url) {
		this.url = url;
        addParams("app_id", ConfigApi.getSouyuePlatform());//
        addParams("app_name", DeviceInfo.appName);
        addParams("ad_format", "banner");
        addParams("ad_location", String.valueOf(page));
        addParams("srp_key_word", keyword);
        addParams("srp_key_id", srpId);
        addParams("device_id", DeviceInfo.getDeviceId());
        addParams("device_name", DeviceInfo.deviceName);
        addParams("device_os", DeviceInfo.osName.toLowerCase());
        addParams("device_version", DeviceInfo.osVersion);
        addParams("network_type", DeviceInfo.getNetworkType() == null ? "" : DeviceInfo.getNetworkType().toLowerCase());
        addParams("network_operator", DeviceInfo.getNetworkOperatorName());
        addParams("network_ip", DeviceInfo.getLocalIpAddress());
        addParams("active_pix", DeviceInfo.getScreenSize());
        addParams("active_screenX", String.valueOf(Utils.getXYInScreen()[0]));
        addParams("active_screenY", String.valueOf(Utils.getXYInScreen()[1]));
        addParams("active_longitude", String.valueOf(DeviceInfo.getLocation()[0]));
        addParams("active_latitude", String.valueOf(DeviceInfo.getLocation()[1]));
        addParams("version", "1.0.1");//
        super.adDoPost(getParams(), source);
	}

	@Override
	public String getUrl() {
		return url;
	}
	
	@Override
	public int getMethod() {
		return super.getMethod();
	}
	
	@Override
	public Map<String, String> getRequestHeader() {
		return super.getRequestHeader();
	}
}
