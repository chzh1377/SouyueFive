package com.zhongsou.souyue.net.user;

import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 我的背景图（获取用户背景图）
 * @author chz
 *
 */
public class MyBgImgRequest extends BaseUrlRequest {

	public MyBgImgRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public MyBgImgRequest setParams(String _token){
        addParams("token", _token);
        return this;
    }
    
	@Override
	public String getUrl() {
		return HOST + "user/myBack.img.groovy"; //获取用户背景图
	}

	@Override
	public boolean isForceRefresh() {
		return true;
	}
}
