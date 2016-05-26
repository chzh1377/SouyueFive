package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 帖子置顶
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MblogTopReq extends BaseUrlRequest {
    public   String toTop = HOST + "interest/mblog.dotop.groovy"; // 帖子置顶

    public MblogTopReq(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return toTop;
    }



    public void setParams(long postsId, String token, int toTopDays)
    {
        addParams("blog_id", postsId+"");
        addParams("token", token);
        addParams("top_day", toTopDays+"");
    }
}
