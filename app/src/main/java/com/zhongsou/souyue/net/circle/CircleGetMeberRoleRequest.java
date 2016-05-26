package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/12.
 * 用户在兴趣圈中的角色
 * 共有四种角色
 * // 0-非圈子成员
 * // 1-圈主
 * // 2-圈子普通成员
 * // 3-游客
 * Method : GET
 * params :
 * param.put("token", token);
 * param.put("interest_id", interest_id);
 */
public class CircleGetMeberRoleRequest extends BaseUrlRequest {

    public CircleGetMeberRoleRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/member.role.groovy"; // 用户在兴趣圈中的角色;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    public void setParams(String token, long interest_id) {
        addParams("token", token);
        addParams("interest_id", interest_id+"");
    }

    /**
     *  biubiu~ -发请求
     * @param id
     * @param response
     * @param token
     * @param interest_id
     */
    public static void send(int id,IVolleyResponse response,String token, long interest_id){
        CircleGetMeberRoleRequest request = new CircleGetMeberRoleRequest(id, response);
        request.setParams(token, interest_id);
        CMainHttp.getInstance().doRequest(request);
    }
}
