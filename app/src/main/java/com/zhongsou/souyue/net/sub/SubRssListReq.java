package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/16.
 * 新闻源
 */
public class SubRssListReq  extends BaseUrlRequest {

    private  final String URL = HOST+"subscribe/rss.list.groovy";
    public  String getUrl(){
        return URL;
    }

    public  SubRssListReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(long cateId){
        addParams("cateId", cateId+"");
    }

}
