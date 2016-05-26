package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 获取用户推荐详情	interest/user.recommend.info.groovy
 * Method : GET
 * params :
 * params.put("recommend_id", recommend_id);
 */
public class CircleGetUserRecommendInfoRequest extends BaseUrlRequest {

    public CircleGetUserRecommendInfoRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(long recommend_id) {
        addParams("recommend_id", recommend_id + "");
    }

    @Override
    public String getUrl() {
        return getSouyueHost()  + "interest/user.recommend.info.groovy";
    }

    /**
     * 发送请求
     * @param id
     * @param resp
     * @param recommend_id
     */
    public static void send(int id,IVolleyResponse resp,long recommend_id){
        CircleGetUserRecommendInfoRequest request = new CircleGetUserRecommendInfoRequest(id, resp);
        request.setParams(recommend_id);
        CMainHttp.getInstance().doRequest(request);
    }
}
