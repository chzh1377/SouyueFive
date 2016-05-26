package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 获取圈成员信息接口	interest/interest.member.personal.info.groovy
 * Method : Get
 * params :
 * ("token", token);
 * ("interest_id", interest_id);
 */
public class CircleGetCircleMeberInfoRequest extends BaseUrlRequest {

    public CircleGetCircleMeberInfoRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String token, long interest_id) {
        addParams("token", token);
        addParams("interest_id", interest_id+"");
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/interest.member.personal.info.groovy"; // 获取圈成员信息接口
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 发请求
     * @param id
     * @param resp
     * @param token
     * @param interest_id
     */
    public static void send(int id,IVolleyResponse resp,String token, long interest_id){
        CircleGetCircleMeberInfoRequest request = new CircleGetCircleMeberInfoRequest(id, resp);
        request.setParams(token, interest_id);
        CMainHttp.getInstance().doRequest(request);
    }
}
