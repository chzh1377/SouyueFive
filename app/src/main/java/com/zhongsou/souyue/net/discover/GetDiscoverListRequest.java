package com.zhongsou.souyue.net.discover;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V
 * @Copyright (c) 2015 zhongsou
 * @Description 发现 -- 发现列表
 * @date 2015/12/14
 */
public class GetDiscoverListRequest extends BaseUrlRequest {
    private String url;
    private boolean refresh;
    private boolean mForceCache;

    public GetDiscoverListRequest(int id, IVolleyResponse response) {
        super(id, response);
        url = getSouyueHost() + "webdata/sy.applys.groovy";    //发现列表
    }

    public void setForceCache(boolean force){
        mForceCache = force;
    }

    @Override
    public boolean isForceCache() {
        return mForceCache;
    }

    public void setForceRefresh(boolean refresh){
        this.refresh = refresh;
    }
    @Override
    public boolean isForceRefresh() {
        return refresh;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getMethod() {
        return BaseUrlRequest.REQUEST_METHOD_GET;
    }

    public void setParams(String token){
        addParams("token", token);
        addParams("imei", DeviceInfo.getDeviceId());
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
    }
}
