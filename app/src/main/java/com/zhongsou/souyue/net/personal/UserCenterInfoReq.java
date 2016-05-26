package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 个人中心头部数据接口
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class UserCenterInfoReq extends BaseUrlRequest {
    public   String getPersonalCenterInfo = HOST
            + "user/userCenterInfo.groovy"; // 个人中心头部数据接口
    
    public UserCenterInfoReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getPersonalCenterInfo;
    }

    public void setParams(String srp_id,int status, long user_id, int from,long comment_id, String token)
    {
        addParams("srp_id",srp_id);
        addParams("is_friend",status+"");
        addParams("user_id",user_id+"");
        addParams("from",from+"");
        addParams("token",token);
        addParams("comment_id",comment_id+"");
    }
}
