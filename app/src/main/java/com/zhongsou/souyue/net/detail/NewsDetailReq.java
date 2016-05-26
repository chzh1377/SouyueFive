package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @description: 新闻详情
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class NewsDetailReq extends BaseUrlRequest {

    private   String URL = HOST+"webdata/news.detail.groovy";

    public NewsDetailReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String url, String keyword, String srpId)
    {
        addParams("url", url);
        addParams("keyword", keyword);
        addParams("srpId", srpId);

    }
    public void setParams(String keyword, String srpId, long pushId)
    {
        addParams("keyword", keyword);
        addParams("srpId", srpId);
        addParams("pushId", String.valueOf(pushId));
        addParams("uid", SYUserManager.getInstance().getUserId());
    }
}
