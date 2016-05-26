package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import org.json.JSONArray;

/**
 * Created by wangqiang on 15/12/15.
 * 判定是否是srp管理员
 */
public class CircleIsAdminReq extends BaseUrlRequest {
    public   String URL = HOST + "interest/srp.isamdin.groovy"; //

    public CircleIsAdminReq(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(String srp_id,String keyword)
    {
        addParams("srpid ", srp_id);
        addParams("keyword", keyword);
    }
}
