package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 获取兴趣圈所有标签	interest/interest.tags.list.groovy
 * Method : Get
 * params :
 * "srpId", srp_id
 */
public class CircleGetTagsRequest extends BaseUrlRequest {
//    public static final String URL = HOST
//            +

    public CircleGetTagsRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() +"interest/interest.tags.list.groovy"; // 获取兴趣圈所有标签
    }

    public void setParams(String srp_id) {
        addParams("srpId", srp_id);
    }

    /**
     * 发送请求
     * @param id
     * @param resp
     * @param srpid
     */
    public static void send(int id, IVolleyResponse resp, String srpid){
        CircleGetTagsRequest circleGetTags = new CircleGetTagsRequest(id, resp);
        circleGetTags.addParams("srpId", srpid);
        CMainHttp.getInstance().doRequest(circleGetTags);
    }
}
