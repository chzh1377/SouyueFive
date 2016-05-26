package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 帖子删除
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MblogDeleteReq extends BaseUrlRequest {
    public   String deletePosts = HOST + "interest/blog.del.groovy"; // 帖子删除
    public MblogDeleteReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return deletePosts;
    }

    public void setParams(long postsId, String token)
    {
        addParams("blog_id", postsId+"");
        addParams("token", token);
    }
}
