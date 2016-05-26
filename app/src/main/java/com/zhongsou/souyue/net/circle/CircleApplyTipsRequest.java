package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 获取用户私密圈申请记录数量的接口	interest/interest.my.apply.tips.groovy
 * Method :
 * GET
 * params :
 * ("token", token);
 */
public class CircleApplyTipsRequest extends BaseUrlRequest {

    public CircleApplyTipsRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() +  "interest/interest.my.apply.tips.groovy";
    }

    public void setParams(String token) {
        addParams("token", token);
    }

    /**
     * 发送请求
     * @param id
     * @param resp
     * @param token
     */
    public static void send(int id, IVolleyResponse resp, String token) {
        CircleApplyTipsRequest circleApplyTips = new CircleApplyTipsRequest(id, resp);
        circleApplyTips.setParams(token);
        CMainHttp.getInstance().doRequest(circleApplyTips);
    }
}
