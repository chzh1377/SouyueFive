package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 16/1/29.
 * 获取用户私密圈申请记录数量的接口
 */
public class SubTipsRequest extends BaseUrlRequest {

    public SubTipsRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String token){
        addParams("token", token);
    }

    @Override
    public String getUrl() {
        return HOST + "interest/interest.my.apply.tips.groovy";
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }
}

