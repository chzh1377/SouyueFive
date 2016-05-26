package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @description: 禁言操作
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class BanTalkReq extends BaseUrlRequest {
    public String banTalk = HOST
            + "interest/member.bantalk.groovy"; // 禁言操作

    public BanTalkReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return banTalk;
    }

    public void setParams(long memberId, int status)
    {
        addParams("member_id", memberId+"");
        addParams("status", status+"");
        addParams("token", SYUserManager.getInstance().getToken());
    }
}
