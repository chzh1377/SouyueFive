package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @description:删除组
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class GroupDeleteReq extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/group.user.delete.groovy";

    public String getUrl() {
        return URL;
    }
    public GroupDeleteReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String groupId){
        addParams("groupId", groupId);
        addParams("token", SYUserManager.getInstance().getToken());
    }
}
