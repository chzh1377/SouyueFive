package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 频道管理列表
 * @author chz
 *
 */
public class ChannelListRequest extends BaseUrlRequest {

	public ChannelListRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public void setParams(String idDeafult){
        addParams("id",idDeafult);
        addParams("token", SYUserManager.getInstance().getToken());
    }
    
	@Override
	public String getUrl() {
		return HOST + "webdata/channel/channel.list.groovy"; //获取频道管理的列表
	}

}
