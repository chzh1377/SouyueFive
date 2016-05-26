package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 短链接
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class ShortURLReq extends BaseUrlRequest {

    private   String URL = HOST+"shortURL.groovy";

    public ShortURLReq(int id, IVolleyResponse response) {
        super(id,  response);
    }


    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String url)
    {
        addParams("method", "set");
        addParams("url", url);
    }

}
