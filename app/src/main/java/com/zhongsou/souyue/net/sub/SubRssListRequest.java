package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/12.
 * 新闻源订阅分组
 */
public class SubRssListRequest extends BaseUrlRequest {

    private  final String URL = HOST+"subscribe/rss.cate.list.groovy";

    public  String getUrl(){
        return URL;
    }

    public SubRssListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }


}
