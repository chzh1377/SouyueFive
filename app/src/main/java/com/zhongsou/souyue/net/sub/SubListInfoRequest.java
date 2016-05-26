package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/12.
 * 订阅列表接口
 * 订阅大全-> 我的订阅
 */
public class SubListInfoRequest extends BaseUrlRequest {

//    private  final String URL = HOST+"subscribe/subscribe.list.my5.0.groovy";

    /**
     * 5.2 的时候 添加组
     */
    private final String URL = HOST + "subscribe/subscribe.list.my5.2.groovy";

    @Override
    public String getUrl() {
        return URL;
    }

    public SubListInfoRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(String token) {
        addParams("token", token);
    }
}
