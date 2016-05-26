package com.zhongsou.souyue.net.guide;

import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/3/26.
 */
public class FirstGuideDataReq extends BaseUrlRequest {
    public static final  String TAG    = FirstGuideDataReq.class.getSimpleName();
    private static final String MY_URL = "webdata/guide.noice.show.groovy";

    public FirstGuideDataReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    public void setParams(String parentGroupName) {
        addParams("parentGroupName", parentGroupName);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MY_URL;
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse resp = (HttpJsonResponse) super.doParse(requet, res);
        return resp.getBodyArray();
    }

    public static void send(int id, IVolleyResponse cb,String parentGroupName) {
        FirstGuideDataReq req = new FirstGuideDataReq(id, cb);
        req.setParams(parentGroupName);
        CMainHttp.getInstance().doRequest(req);
    }
}
