package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 圈吧管理-退出圈子	interest/member.exitinterest.groovy
 * Method : GET
 * Params :
 * param.put("interest_id", interest_id);
 * param.put("token", token);
 */
public class CircleExitCircleRequest extends BaseUrlRequest {

    public CircleExitCircleRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/member.exitinterest.groovy";
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    public void setParams(long interest_id, String token, String opSource) {
        addParams("interest_id", interest_id + "");
        addParams("token", token);
        addParams("opSource", opSource);
    }

    public static void send(int id, IVolleyResponse response, long interest_id, String token, String opSource) {
        CircleExitCircleRequest request = new CircleExitCircleRequest(id, response);
        request.setParams(interest_id, token, opSource);
        CMainHttp.getInstance().doRequest(request);
    }

    public static void send(int id, String token, String vc, String interest_ids, String imei,
                            IVolleyResponse callback, String opSource) {
        CircleExitCircleRequest request = new CircleExitCircleRequest(id, callback);
        request.addParams("token", token);
        request.addParams("vc", vc);
        request.addParams("imei", imei);
        request.addParams("interest_id", interest_ids);
        request.addParams("opSource", opSource);
        CMainHttp.getInstance().doRequest(request);
    }

    /**
     * 添加订阅页面 - 添加订阅接口
     * @param new_srpid
     * @param opSource
     */
    public void setParams(String new_srpid, String opSource){
        addParams("new_srpid", new_srpid);
        addParams("opSource", opSource);
    }
}
