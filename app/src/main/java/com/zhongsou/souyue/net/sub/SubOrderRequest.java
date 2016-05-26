package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/12.
 * 订阅排序
 */
public class SubOrderRequest extends BaseUrlRequest {

    /**
     * 接口从 5.0 改到 5.2
     * 客户端数据没有变化，服务器端添加的是组的修改
     */
    private  final String URL = HOST+"subscribe/subscribe.modify5.2.groovy";
    public  String getUrl(){
        return URL;
    }

    public SubOrderRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    public void addParams(String json){
        addParams("subscribeIds", json);
    }

}