package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 圈吧管理-删除回复	interest/reply.del.groovy
 * Method : GET
 * params :
 * "reply_id", reply_id
 * "token", token
 */
public class CircleDelReplyRequest extends BaseUrlRequest {

    public CircleDelReplyRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String reply_id, String token) {
        addParams("reply_id", reply_id);
        addParams("token", token);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/reply.del.groovy";
    }

    /**
     * 简写
     * @param id
     * @param reply_id
     * @param token
     * @param resp
     */
    public static void send(int id,  IVolleyResponse resp,String reply_id, String token){
        CircleDelReplyRequest circleDelReply = new CircleDelReplyRequest(id, resp);
        circleDelReply.setParams(reply_id,token);
        CMainHttp.getInstance().doRequest(circleDelReply);
    }
}
