package com.zhongsou.souyue.net.user;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/12.
 * 钱包列表	webdata/sy.pay.groovy
 * Method : GET
 * Params :
 * m.put("token", token);
 * m.put("imei", DeviceInfo.getDeviceId());
 * m.put("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
 */
public class UserWalletRequest extends BaseUrlRequest {

    private Boolean mRefresh; // 走缓存
    public UserWalletRequest(int id, IVolleyResponse response, Boolean forceCache) {
        super(id, response);
        if(forceCache != null){
            mRefresh = forceCache;
        }
    }



    public void setParams(String token) {
        addParams("token",token);
        addParams("imei", DeviceInfo.getDeviceId());
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "webdata/sy.pay.groovy";// 钱包列表
    }

    @Override
    public boolean isForceRefresh() {
        if(mRefresh == null){
            return super.isForceCache();
        }else{
            return mRefresh;
        }
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 直接发请求
     * @param id
     * @param resp
     * @param token
     * @param forceCache
     */
    public static void send(int id,IVolleyResponse resp,String token,Boolean forceCache){
        UserWalletRequest request = new UserWalletRequest(id, resp, forceCache);
        request.setParams(token);
        CMainHttp.getInstance().doRequest(request);
    }
}
