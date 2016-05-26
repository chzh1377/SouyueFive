package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/15.
 * 获取成员列表
 */
public class CircleMemberListRequest extends BaseUrlRequest {
    public   String URL = HOST + "interest/member.mblog.list.groovy"; //

    public CircleMemberListRequest(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(long user_id, long interest_id, int pno,
                          int psize)
    {
        addParams("interest_id",interest_id+"");
        addParams("user_id",user_id+"");
        addParams("pno",pno+"");
        addParams("psize",psize+"");
    }
}
