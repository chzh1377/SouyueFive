package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 帖子取消置顶
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MblogCancleTopReq extends BaseUrlRequest {
    public   String cancleTotop = HOST
            + "interest/mblog.undotop.groovy"; // 帖子取消置顶
    public MblogCancleTopReq(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return cancleTotop;
    }

    public void setParams(long postsId, String  token)
    {
        addParams("blog_id", postsId+"");
        addParams("token", token);
    }
}
