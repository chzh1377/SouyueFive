package com.zhongsou.souyue.net.sub;

import com.google.gson.Gson;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by wangqiang on 15/12/12.
 * 订阅、退订srp词
 */
public class SubSrpRequest extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/srp.subscribe3.0.groovy";

    private Gson gson = new Gson();

    @Override
    public String getUrl() {
        return URL;
    }

    public SubSrpRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(List add, List del) {
        addParams("add", add != null ? gson.toJson(add) : "");
        addParams("del", del != null ? gson.toJson(del) : "");
        addParams("opSource", ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);
    }
}