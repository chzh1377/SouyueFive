package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 订阅 热门推荐 搜索接口
 * @date 2016/03/24
 */
public class SuberSearchRequest extends BaseUrlRequest {

    private final String URL = HOST + "recommend/search.enjoy.content.groovy";

    public String getUrl() {
        return URL;
    }

    public SuberSearchRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String token, String keyword) {
        addParams("token", token);
        addParams("imei", DeviceInfo.getDeviceId());
        addParams("vc", DeviceInfo.getAppVersion());
        addParams("keyword", keyword);
    }
}
