package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 用户订阅的兴趣圈列表接口	interest/member.interest.list.groovy
 * Method : GET
 * params :
 * "token", token
 * "name", name -> 不知道是什么的名称
 * "interest_id",interest_id -> 兴趣圈id, 用于排除当前圈子，如果传0则无效
 */
public class CircleGetCircleListRequest extends BaseUrlRequest {
//
//    public static final String URL = HOST
//            + "interest/member.interest.list.groovy";// 用户订阅的兴趣圈列表接口

    public CircleGetCircleListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/member.interest.list.groovy";
    }

    public void setParams(String token, long interest_id, String name) {
        addParams("token", token);
        addParams("name", name);
        addParams("interest_id",interest_id+"");
    }

    public static void send(int id, IVolleyResponse resp, String token, long interest_id, String name){
        CircleGetCircleListRequest circleGetCircleList = new CircleGetCircleListRequest(id, resp);
        circleGetCircleList.setParams(token,interest_id,name);
        CMainHttp.getInstance().doRequest(circleGetCircleList);
    }
}
