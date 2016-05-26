package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 4.0精华区主页
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class PrimeListReq extends BaseUrlRequest {

    private  String URL = HOST+"interest/prime4.0.mblog.list.groovy";

    public PrimeListReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String token,long interest_id, int page, int psize, long last_sort_num)
    {
        addParams("token", token);
        addParams("interest_id", interest_id+"");
        addParams("pno", page+"");
        addParams("psize", psize+"");
        addParams("last_sort_num", last_sort_num+"");
    }
}
