package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/15.
 * 设置免审
 */
public class CircleSetfreetrialRequest extends BaseUrlRequest{
    public   String URL = HOST + "interest/interest.setfreetrial.groovy"; //

    public CircleSetfreetrialRequest(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(long interest_id, int oper_type, int status)
    {
       addParams("interest_id",interest_id+"");
        addParams("oper_type",oper_type+"");
        addParams("stauts",status+"");
    }
}
