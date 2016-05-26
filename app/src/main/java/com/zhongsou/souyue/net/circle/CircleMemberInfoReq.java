package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/15.
 * 获取成员了信息
 */
public class CircleMemberInfoReq extends BaseUrlRequest {
    public   String URL = HOST + "interest/member.info.groovy"; //

    public CircleMemberInfoReq(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(long member_id, long interest_id)
    {
        addParams("interest_id",interest_id+"");
        addParams("member_id",member_id+"");
    }
}
