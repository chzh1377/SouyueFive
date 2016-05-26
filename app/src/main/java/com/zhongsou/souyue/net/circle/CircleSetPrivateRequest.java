package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 圈吧管理-修改是否保护隐私	interest/member.setprivate.groovy
 * Method : GET
 * Params :
 * "interest_id", interest_id
 * "status", status  ->  0关闭，1开启
 * "token", token
 */
public class CircleSetPrivateRequest extends BaseUrlRequest {

    public CircleSetPrivateRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/member.setprivate.groovy";
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    public void setParams(long interest_id, int status, String token) {
        addParams("interest_id", interest_id + "");
        addParams("status", status + "");
        addParams("token", token);
    }

    /**
     * 圈吧管理-修改是否保护隐私
     * @param id
     * @param resp
     * @param interest_id
     * @param status
     * @param token
     */
    public static void send(int id,IVolleyResponse resp,long interest_id, int status, String token){
        CircleSetPrivateRequest request = new CircleSetPrivateRequest(id, resp);
        request.setParams(interest_id, status, token);
        CMainHttp.getInstance().doRequest(request);
    }
}
