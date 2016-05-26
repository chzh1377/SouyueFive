package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 *  圈吧管理-用户信息设置	interest/member.setinfo.groovy
 * Method: GET
 * params :
 * param.put("interest_id", interest_id);
 * param.put("oper_type", oper_type);
 * param.put("parameter", parameter);
 * param.put("token", token);
 * oper_type :
 * private static final int CRICLE_MANAGE_PHOTO_SETTING = 1; // 头像
 * public static final int CRICLE_MANAGE_NICKNAME_SETTING = 2; // 昵称
 * private static final int CRICLE_MANAGE_SIGNATURE_SETTING = 3; // 签名
 */
public class CircleSetUserInfoRequest extends BaseUrlRequest {

    public CircleSetUserInfoRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/member.setinfo.groovy";
    }

    public void setParams(long interest_id, int oper_type, String parameter, String token) {
        addParams("interest_id", interest_id + "");
        addParams("oper_type", oper_type + "");
        addParams("parameter", parameter);
        addParams("token", token);
    }

    /**
     * 发送请求
     * @param id
     * @param response
     * @param interest_id
     * @param oper_type
     * @param parameter
     * @param token
     */
    public static void send(int id, IVolleyResponse response,long interest_id, int oper_type, String parameter, String token){
        CircleSetUserInfoRequest request = new CircleSetUserInfoRequest(id, response);
        request.setParams(interest_id, oper_type, parameter, token);
        CMainHttp.getInstance().doRequest(request);

    }
}
