package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description:  踢出圈子操作
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class KickOutCircleReq extends BaseUrlRequest {
    public   String kickCircle = HOST
            + "interest/member.kicked.out.groovy"; // 踢出圈子操作
    public KickOutCircleReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return kickCircle;
    }

    public void setParams(long memberId, String token,String opSource)
    {
        addParams("member_id", memberId+"");
        addParams("token", token);
        addParams("opSource", opSource);
    }
}
