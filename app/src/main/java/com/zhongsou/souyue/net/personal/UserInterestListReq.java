package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 个人中心点击更多获取兴趣圈列表
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class UserInterestListReq extends BaseUrlRequest {
    public   String interestForUser = HOST
            + "interest/personal.interest.list.groovy"; // 个人中心点击更多获取兴趣圈列表
    public UserInterestListReq(int id,  IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return interestForUser;
    }

    public void setParams(String token, long user_id)
    {
        addParams("token", token);
        addParams("user_id", user_id+"");
    }
}
