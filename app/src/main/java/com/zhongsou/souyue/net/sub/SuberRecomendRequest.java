package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/1/22.
 * 热门推荐
 * URL : "recommend/top.recommend.list.groovy"
 * 参数：
 * addParams("token", token)
 * addParams("vc", vc)
 * addParams("imei", imei)
 * addParams("appname",com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()))
 * 返回值：
 * List<SuberedItemInfo>
 */
public class SuberRecomendRequest extends BaseUrlRequest {

    private static final String MYURL = "recommend/top.recommend.list.groovy";

    public SuberRecomendRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }

    public void setParams(String token, String vc, String imei) {
        addParams("token", token);
        addParams("vc", vc);
        addParams("imei", imei);
        addParams("appname", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
    }

    /**
     * 发出请求。
     *
     * @param id
     * @param token
     * @param vc
     * @param imei
     * @param callback
     */
    public static void send(int id, String token, String vc, String imei,
                            IVolleyResponse callback) {
        SuberRecomendRequest request = new SuberRecomendRequest(id, callback);
        request.setParams(token, vc, imei);
        CMainHttp.getInstance().doRequest(request);
    }
}
