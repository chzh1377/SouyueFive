package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 圈成员列表
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class MemberListReq extends BaseUrlRequest {
    public   String getMemberList = HOST
            + "interest/member.list.groovy"; // 圈成员列表
    public MemberListReq(int id,  IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getMemberList;
    }

    public void setParams(long interest_id, String name , int pno , int psize)
    {
        addParams("interest_id", interest_id+"");
        addParams("name", name);
        addParams("pno", pno+"");
        addParams("psize", psize+"");
    }
}
