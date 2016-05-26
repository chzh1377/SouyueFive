package com.zhongsou.souyue.net.srp;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zhongsou.souyue.module.AdList;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.ConfigApi;
/**
 * 广告列表请求
 * @author chz
 *
 */
public class AdListRequest extends AdProtocol {
	
	public AdListRequest(int id,IVolleyResponse response) {
		super(id,response);
	}

    public void addParams(String keyword, String srpId, int page,String source){
    	
    	Map<String, String> m = new HashMap<String, String>();
        m.put("app_id", ConfigApi.getSouyuePlatform());//
        m.put("app_name", DeviceInfo.appName);
        m.put("ad_format", "banner");
        m.put("ad_location", String.valueOf(page));
        m.put("srp_key_word", keyword);
        m.put("srp_key_id", srpId);
        m.put("device_id", DeviceInfo.getDeviceId());
        m.put("device_name", DeviceInfo.deviceName);
        m.put("device_os", DeviceInfo.osName.toLowerCase());
        m.put("device_version", DeviceInfo.osVersion);
        m.put("network_type", DeviceInfo.getNetworkType() == null ? "" : DeviceInfo.getNetworkType().toLowerCase());
        m.put("network_operator", DeviceInfo.getNetworkOperatorName());
        m.put("network_ip", DeviceInfo.getLocalIpAddress());
        m.put("active_pix", DeviceInfo.getScreenSize());
        m.put("active_longitude", String.valueOf(DeviceInfo.getLocation()[0]));
        m.put("active_latitude", String.valueOf(DeviceInfo.getLocation()[1]));
        m.put("version", "1.0.1");//
        super.adDoPost(m, source);
         
    }
    
    @Override
    public String getUrl() {
    	return super.getUrl();
    }
    @Override
    public int getMethod() {
    	return super.getMethod();
    }
    
    @Override
	public Map<String, String> getRequestHeader() {
		return super.getRequestHeader();
	}
    
    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
    	HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
    	AdList result = new AdList(response);
    	return result;
    }
}