package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 圈子主贴列表（4.0）
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MblogList4Req extends BaseUrlRequest {
    
    public   String URL = HOST+ "interest/bar4.0.mblog.list.groovy";
    
    public MblogList4Req(int id, IVolleyResponse response) {
        super(id,  response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(long interest_id,long last_sort_num, int pno, int psize,String token, String tag_id,String onlyjing)
    {
        addParams("interest_id", interest_id+"");
        addParams("last_sort_num", last_sort_num+"");
        addParams("pno", pno+"");
        addParams("psize", psize+"");
        addParams("token", token);
        addParams("tag_id", tag_id);
        addParams("onlyjing", onlyjing);
    }
    
}
