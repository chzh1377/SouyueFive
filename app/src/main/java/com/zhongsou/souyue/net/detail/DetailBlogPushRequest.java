package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/12.
 * 帖子推送接口
 * Method : GET
 * params :
 * ("interest_id", interest_id);
 * ("blog_id", blog_id);
 * ("token", token);
 * ("msg_type", msg_type);
 */
public class DetailBlogPushRequest extends BaseUrlRequest {

    public DetailBlogPushRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/mblog.push.groovy"; // 帖子推送
    }

    public void setParams(long interest_id, long blog_id, String token,
                          int msg_type) {
        addParams("interest_id", interest_id + "");
        addParams("blog_id", blog_id + "");
        addParams("token", token);
        addParams("msg_type", msg_type + "");
    }

    public static void send(int id,IVolleyResponse resp,long interest_id, long blog_id, String token,
                            int msg_type){
        DetailBlogPushRequest request = new DetailBlogPushRequest(id, resp);
        request.setParams(interest_id, blog_id, token, msg_type);
        CMainHttp.getInstance().doRequest(request);
    }
}
