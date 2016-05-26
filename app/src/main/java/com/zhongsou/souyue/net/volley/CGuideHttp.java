package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.google.gson.Gson;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.GuideRecommendSRP;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

public class CGuideHttp extends AHttp{
	
    private static final String TAG="CGuideHttp_volley";

    public CGuideHttp(Context _context){
    	super(_context,CGuideHttp.class.getName());
    }
    /**
     * 专题列表
     * @param id
     * @param token
     * @param installApps
     * @param callback
     */
    public void getGuideRecommendSpecial(int id,String token,String installApps,IVolleyResponse callback){
    	CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("imei", DeviceInfo.getDeviceId());
        request.addParams("vc",DeviceInfo.getAppVersion());
        request.addParams("token", token);
        request.addParams("installApps", installApps);
        request.addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        request.addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        request.setCallBack(callback);
        request.setUrl(UrlConfig.getGuideRecommandSpecial);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        mVolley.doRequest(request);
    }
    /**
     * 推荐SRP列表
     * @param id
     * @param token
     * @param callback
     */
    public void getGuideRecommendSRP(int id,String token,IVolleyResponse callback){
    	CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("imei", DeviceInfo.getDeviceId());
        request.addParams("vc",DeviceInfo.getAppVersion());
        request.addParams("token",  SYUserManager.getInstance().getToken());
        request.addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        request.addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        request.setCallBack(callback);
        request.setUrl(UrlConfig.getGuideRecommandSRP);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        mVolley.doRequest(request);
    }
    public void subscribeSrp(int id, String token,List<GuideRecommendSRP> array,IVolleyResponse callback){
    	CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("imei",DeviceInfo.getDeviceId());
        request.addParams("vc",DeviceInfo.getAppVersion());
        request.addParams("token", token);
        request.addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        request.addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
//        request.addParams("subscribeData", com.alibaba.fastjson.JSONArray.toJSONString(array));
        request.addParams("subscribeData", new Gson().toJson(array));
        request.setCallBack(callback);
        request.setUrl(UrlConfig.subscribeGuideRecommandSRP);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        mVolley.doRequest(request);
    }
}
