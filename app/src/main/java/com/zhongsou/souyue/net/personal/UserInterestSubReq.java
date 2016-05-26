package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 个人中心点击更多获取主题列表
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class UserInterestSubReq extends BaseUrlRequest {
    public   String zhutiForUser = HOST
            + "interest/personal.interest.sub.groovy"; // 个人中心点击更多获取主题列表

    public UserInterestSubReq(int id, IVolleyResponse response) {
//        super(id, zhutiForUser, REQUEST_METHOD_GET, response);
        super(id,response);
    }


    @Override
    public String getUrl() {
        return zhutiForUser;
    }

    public void setParams(String user_id)
    {
        addParams("user_id", user_id);
    }
}
