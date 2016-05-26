package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/15.
 * 设置图像
 */
public class CircleMemberImgRuquest extends BaseUrlRequest {
    public   String URL = HOST + "interest/member.setbgimg.groovy"; //

    public CircleMemberImgRuquest(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(long member_id, String bg_img)
    {
        addParams("member_id",member_id+"");
        addParams("bg_img",bg_img+"");
    }
}