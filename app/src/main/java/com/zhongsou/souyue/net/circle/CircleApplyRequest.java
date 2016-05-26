package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 私密圈申请加入接口	interest/interest.apply.groovy
 * Method : GET
 * params :
 * params.put("interest_id", interest_id);
 * params.put("token", token);
 * params.put("content", content);  -> 申请感言
 * params.put("nickname", nickname);  -> 昵称
 */
public class CircleApplyRequest extends BaseUrlRequest {

    public CircleApplyRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/interest.apply.groovy";// 私密圈申请加入接口;
    }

    public void setParams(long interest_id, String token,
                          String content, String nickname) {
        addParams("interest_id", interest_id+"");
        addParams("token", token);
        addParams("content", content);
        addParams("nickname", nickname);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 发请求
     */
    public static void sendRequest(int id,IVolleyResponse resp,long interest_id, String token,
                                   String content, String nickname){
        CircleApplyRequest circleApply = new CircleApplyRequest(id, resp);
        circleApply.setParams(interest_id,token,content,nickname);
        CMainHttp.getInstance().doRequest(circleApply);

    }
}
