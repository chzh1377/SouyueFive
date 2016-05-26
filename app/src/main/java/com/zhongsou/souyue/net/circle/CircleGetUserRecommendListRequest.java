package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 获取用户推荐列表	interest/user.recommend.list.groovy
 * Method : Get
 * params :
 * ("interest_id", interest_id);
 * ("token", token);
 * ("psize", psize+"");
 * ("pno", pno+"");
 * ("last_id", last_id+"");
 */
public class CircleGetUserRecommendListRequest extends BaseUrlRequest {


    public CircleGetUserRecommendListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/user.recommend.list.groovy";// 获取用户推荐列表;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    public void setParams(String interest_id, String token,
                          int psize, int pno, long last_id) {
        addParams("interest_id", interest_id);
        addParams("token", token);
        addParams("psize", psize + "");
        addParams("pno", pno + "");
        addParams("last_id", last_id + "");
    }

    /**
     * send req
     *
     * @param id
     * @param resp
     * @param interest_id
     * @param token
     * @param psize
     * @param pno
     * @param last_id
     */
    public static void send(int id, IVolleyResponse resp, String interest_id, String token,
                            int psize, int pno, long last_id) {
        CircleGetUserRecommendListRequest request = new CircleGetUserRecommendListRequest(id, resp);
        request.setParams(interest_id, token, psize, pno, last_id);
        CMainHttp.getInstance().doRequest(request);
    }
}
