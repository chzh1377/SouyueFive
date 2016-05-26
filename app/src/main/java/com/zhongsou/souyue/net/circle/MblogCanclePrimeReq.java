package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 帖子取消加精
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MblogCanclePrimeReq  extends BaseUrlRequest {
    public   String canclePrime = HOST
            + "interest/mblog.undoprime.groovy"; // 帖子取消加精

    public MblogCanclePrimeReq(int id,IVolleyResponse response) {
        super(id,  response);
    }

    @Override
    public String getUrl() {
        return canclePrime;
    }

    public void setParams(long postsId, String  token)
    {
        addParams("blog_id", postsId+"");
        addParams("token", token);
    }
}
