package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 我的评论列表
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MyCommentListReq extends BaseUrlRequest {

    private   String URL = HOST+"interest/comment4.3.list.my.groovy";

    public MyCommentListReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String token, long commentLastId, Integer commentType)
    {
        addParams("token", token);
        addParams("pno", String.valueOf(commentLastId));
        addParams("type", commentType+"");
        addParams("psize", 10+"");
        addParams("appName", DeviceInfo.appName);
        addParams("operflag", 3+"");
        addParams("last_id",String.valueOf(commentLastId));
    }

}
