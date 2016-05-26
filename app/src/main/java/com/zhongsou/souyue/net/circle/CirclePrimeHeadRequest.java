package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 精华区头部数据接口	interest/prime.head.groovy
 * Method : GET
 * params :
 * ("srp_id", srp_id);
 * ("token", token);
 */
public class CirclePrimeHeadRequest extends BaseUrlRequest {

    public CirclePrimeHeadRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/prime.head.groovy";
    }

    public void setParams(String srp_id, String token) {
        addParams("srp_id", srp_id);
        addParams("token", token);
    }

    public static void send(int id, IVolleyResponse resp, String srp_id, String token) {
        CirclePrimeHeadRequest request = new CirclePrimeHeadRequest(id, resp);
        request.setParams(srp_id, token);
        CMainHttp.getInstance().doRequest(request);
    }
}
