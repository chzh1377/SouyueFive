package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 发送短信给好友邀请注册，成功后赠送中搜币
 * Created by lvqiang on 15/12/16.
 */
public class UserInviteFriend extends BaseUrlRequest {
    public String URL = HOST + "interest/follow.my.blog.groovy";

    public UserInviteFriend(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String mobile){
        addParams("mobile", mobile);
    }
}
