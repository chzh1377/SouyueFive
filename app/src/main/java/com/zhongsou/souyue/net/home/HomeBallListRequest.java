package com.zhongsou.souyue.net.home;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 首页（球列表）
 * @author chz
 *
 */
public class HomeBallListRequest extends BaseUrlRequest {
	private boolean mRefresh;
	private boolean mForceCache;
	
	public HomeBallListRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public void setParams(String _token,int _type,boolean _refresh,boolean _forceCache){
        addParams("token", _token);
        addParams("type", _type+"");
        addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        this.mForceCache = _forceCache;
        addKeyValueTag("forceCache",_forceCache);
        this.mRefresh = _refresh;
    }
    
	@Override
	public String getUrl() {
		return HOST + "subscribe/srp.interest.sub5.2.groovy"; //首页球球接口
	}
	
	@Override
	public boolean isForceCache() {
		return mForceCache;
	}
	
	@Override
	public boolean isForceRefresh() {
		return mRefresh;
	}
	
    @Override
    public Object doParse(CVolleyRequest request, String _response) throws Exception {
        Object obj = super.doParse(request, _response);
        int id = request.getmId();
        HttpJsonResponse res = (HttpJsonResponse) obj;
        switch (id){
            case HttpCommon.HOME_LIST_BALL:
                ArrayList<HomeBallBean> bs = new Gson().fromJson(
                        res.getBodyArray(),
                        new TypeToken<ArrayList<HomeBallBean>>() {
                        }.getType());
                return bs;
        }
        return res;
    }
}
