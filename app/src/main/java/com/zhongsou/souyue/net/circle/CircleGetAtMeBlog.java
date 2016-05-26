package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 获取@我的帖子列表	interest/mentionblog.list.groovy
 * Method : GET
 * params :
 * "interest_id", interest_id
 * "token", token
 * "pno", pno - > current page
 * "psize", psize - > pageSize
 */
public class CircleGetAtMeBlog extends BaseUrlRequest {
//    public static final String URL = HOST
//            + "interest/mentionblog.list.groovy";

    public CircleGetAtMeBlog(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/mentionblog.list.groovy";
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    public void setParams(String token, long interest_id, int page,
                          int psize) {
        addParams("interest_id", interest_id+"");
        addParams("token", token);
        addParams("pno", page+"");
        addParams("psize", psize+"");
    }

    /**
     * 简写，发送请求
     * @param id
     * @param token
     * @param interest_id
     * @param page
     * @param psize
     * @param resp
     */
    public static void send(int id, String token, long interest_id, int page,
                            int psize, IVolleyResponse resp){
        CircleGetAtMeBlog req = new CircleGetAtMeBlog(id, resp);
        req.setParams(token,interest_id,page,psize);
        CMainHttp.getInstance().doRequest(req);
    }
}
