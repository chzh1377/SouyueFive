package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 不知道
 * Created by lvqiang on 16/1/12.
 */
public class CircleReadNewsDetailReq extends BaseUrlRequest {
    private String mUrl;
    private boolean mForceRefresh;
    public CircleReadNewsDetailReq(int id, String url,IVolleyResponse response) {
        super(id, response);
        mUrl = url;
    }

    public void setRefresh(boolean isrefresh){
        mForceRefresh = isrefresh;
    }

    @Override
    public boolean isForceRefresh() {
        return mForceRefresh;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        return res;
    }
}
