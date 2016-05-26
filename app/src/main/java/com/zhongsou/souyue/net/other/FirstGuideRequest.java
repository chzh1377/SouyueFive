package com.zhongsou.souyue.net.other;

import com.google.gson.Gson;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zyw on 2016/1/13.
 * 第一次请求搜悦的时候调用的接口，用于上传App信息，进行默认订阅操作
 */
public class FirstGuideRequest extends BaseUrlRequest {

    private static final int    DEFAULT_RETRYTIME = 3;
    private static final String API_URL           = "webdata/guide.novice.5.1.groovy";

    public FirstGuideRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + API_URL;
    }

    public void setParams(String token, List<AppData> datas) {
        addParams("token", token);
        addParams("installApps", new Gson().toJson(datas));
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public boolean isForceRefresh() {
        return true;
    }

    @Override
    public int getRetryTimes() {
        return DEFAULT_RETRYTIME;
    }

    /**
     * 发出请求
     * @param id
     * @param response
     * @param token
     * @param datas
     */
    public static void send(int id, IVolleyResponse response, String token, List<AppData> datas) {
        FirstGuideRequest request = new FirstGuideRequest(id, response);
        request.setParams(token, datas);
        CMainHttp.getInstance().doRequest(request);
    }

}
