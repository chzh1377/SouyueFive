package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 评论列表
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class CommentListReq extends BaseUrlRequest {

    private   String URL = HOST+"comment/comment.list.groovy";

    public CommentListReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String url, long commentLastId)
    {
        addParams("url", url);
        addParams("lastId", String.valueOf(commentLastId));
    }
}
