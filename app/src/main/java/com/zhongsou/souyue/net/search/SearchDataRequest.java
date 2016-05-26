package com.zhongsou.souyue.net.search;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V5.2.0
 * @Copyright (c) 2015 zhongsou
 * @Description 搜索页面 搜索返回结果
 * @date 2016/04/23
 */
public class SearchDataRequest extends BaseUrlRequest {

    private String url;
    private boolean refresh;
    private boolean mForceCache;

    public SearchDataRequest(int id, IVolleyResponse response) {
        super(id, response);
        url = getSouyueHost() + "search/search.content.groovy";
//        http://103.29.134.224/d3api2/search/search.content.groovy?keyword=刘德华&page=1&pageSize=10&vc=5.2
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

    /**
     *
     * @param keyword 关键字
     * @param page 页码
     * @param pageSize 数据条数
     */
    public void setParams(String keyword, int page, int pageSize){
        addParams("keyword", keyword);
        addParams("page", String.valueOf(page));
        addParams("pageSize", String.valueOf(pageSize));
    }
}
