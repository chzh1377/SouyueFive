package com.zhongsou.souyue.net.other;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/23.
 * 删除历史纪录
 */

public class HistoryClear extends BaseUrlRequest{
    private  final String URL = HOST+"webdata/history.del.groovy";

    public HistoryClear(int id, IVolleyResponse response) {
        super(id,response);
    }

    public void addParameters(long id){
        addParams("id", String.valueOf(id));
    }
    public void addParameters(){

    }
    @Override
    public String getUrl() {
        return URL;
    }
}
