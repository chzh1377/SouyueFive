package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 新闻详情顶数，评论数
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class NewsCountReq extends BaseUrlRequest {

    private   String URL = HOST+"webdata/news.count.groovy";

    public NewsCountReq(int id, IVolleyResponse response) {
        super(id,  response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String token, String url)
    {
        addParams("token", token);
        addParams("url", url);
    }

}
