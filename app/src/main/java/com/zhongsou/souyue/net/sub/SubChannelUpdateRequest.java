package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 订阅频道后的保存
 * @author chz
 *
 */
public class SubChannelUpdateRequest extends BaseUrlRequest {

	public SubChannelUpdateRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public void setParams(String channelTrue){
        addParams("channelTrue",channelTrue);
        addParams("token", SYUserManager.getInstance().getToken());
    }
    
	@Override
	public String getUrl() {
		return HOST + "webdata/channel/channel.update.groovy"; 
	}
	
	@Override
	public boolean isForceRefresh() {
		return true;
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
}
