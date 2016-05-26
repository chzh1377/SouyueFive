package com.zhongsou.souyue.net.common;

import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 控制客户端数据及开关
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class ClientConfigReq extends BaseUrlRequest {
    private String url = HOST
            + "client/clientConfig.groovy?vc="
            + DeviceInfo.getAppVersion();
    public ClientConfigReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return url;
    }
    public void setParams(String userId)
    {
        addParams("userId",userId);
    }

}
