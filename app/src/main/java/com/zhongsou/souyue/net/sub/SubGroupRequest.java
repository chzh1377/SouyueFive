package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description 添加订阅页面 - 添加组几口
 * @date 2016/4/11
 */
public class SubGroupRequest extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/subscribe.newGroup.groovy";

    @Override
    public String getUrl() {
        return URL;
    }

    public SubGroupRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String groupId) {
        addParams("groupId", groupId);
    }
}