package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 获取用户于某个私密圈关系接口	interest/interest.user.audit.status.groovy
 * Method :GET
 * params :
 * ("interest_id", interest_id)
 * ("token", token)
 */
public class CircleGetUserRelationShipRequest extends BaseUrlRequest {
//
//    public static String URL = HOST
//            + "interest/interest.user.audit.status.groovy";// 获取用户与某个私密圈关系的接口

    public CircleGetUserRelationShipRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/interest.user.audit.status.groovy";// 获取用户与某个私密圈关系的接口;
    }

    public void setParams(long interest_id, String token) {
        addParams("interest_id", interest_id + "");
        addParams("token", token);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 直接调用
     * @param id
     * @param resp
     * @param interest_id
     * @param token
     */
    public static void send(int id, IVolleyResponse resp, long interest_id, String token) {
        CircleGetUserRelationShipRequest circleGetUserRelationShip = new CircleGetUserRelationShipRequest(id, resp);
        circleGetUserRelationShip.setParams(interest_id, token);
        CMainHttp.getInstance().doRequest(circleGetUserRelationShip);
    }
}
