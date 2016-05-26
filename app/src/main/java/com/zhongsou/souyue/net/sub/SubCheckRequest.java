package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/13.
 * 检查关键词是否订阅
 */
public class SubCheckRequest extends BaseUrlRequest {

    private  final String URL = HOST+"subscribe/subscribe.check3.0.groovy";

    @Override
    public  String getUrl() {
        return URL;
    }

    public SubCheckRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    public void addParams(String keyword,String id,String type) {
        addParams("keyword",keyword);
        addParams("id",id);
        addParams("type",type);
    }
}
