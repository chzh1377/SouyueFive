package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 获取圈子菜单信息接口	interest/interest.menu.info.groovy
 * Method : GET
 * params :
 * ("token", token);
 * ("new_srpid", srp_id);
 */
public class CircleGetCircleMenuRequest extends BaseUrlRequest {
    public CircleGetCircleMenuRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String token, String srp_id) {
        addParams("token", token);
        addParams("new_srpid", srp_id);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/interest.menu.info.groovy";// 获取圈子菜单信息接口;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 直接发请求
     * @param resp
     * @param token
     * @param srp_id
     */
    public static void send(IVolleyResponse resp, String token, String srp_id){
        CircleGetCircleMenuRequest request = new CircleGetCircleMenuRequest(HttpCommon.CIRCLE_GET_CIRCLE_MENU_REQUESTID, resp);
        request.setParams(token, srp_id);
        CMainHttp.getInstance().doRequest(request);
    }
}
