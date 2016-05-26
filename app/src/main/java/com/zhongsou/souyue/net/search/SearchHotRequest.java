package com.zhongsou.souyue.net.search;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V5.2.0
 * @Copyright (c) 2015 zhongsou
 * @Description 搜索页面 新的热搜榜接口
 * @date 2016/04/23
 */
public class SearchHotRequest extends BaseUrlRequest {

    private String url;
    private boolean refresh;
    private boolean mForceCache;

    public SearchHotRequest(int id, IVolleyResponse response) {
        super(id, response);
        url = getSouyueHost() + "search/hotsearch.list.groovy?count=30";    //默认条数30
    }

    public void setForceCache(boolean force){
        mForceCache = force;
    }

    @Override
    public boolean isForceCache() {
        return mForceCache;
    }

    public void setForceRefresh(boolean refresh){
        this.refresh = refresh;
    }
    @Override
    public boolean isForceRefresh() {
        return refresh;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getMethod() {
        return BaseUrlRequest.REQUEST_METHOD_GET;
    }

}
