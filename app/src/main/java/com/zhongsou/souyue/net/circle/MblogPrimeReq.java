package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 帖子加精
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MblogPrimeReq extends BaseUrlRequest {

    public   String toPrime = HOST + "interest/mblog.doprime.groovy"; // 帖子加精

    public MblogPrimeReq(int id, IVolleyResponse response) {
        super(id,  response);
    }

    @Override
    public String getUrl() {
        return toPrime;
    }

    public void setParams( long postsId, String token)
    {
        addParams("blog_id", postsId+"");
        addParams("token", token);
    }

}
