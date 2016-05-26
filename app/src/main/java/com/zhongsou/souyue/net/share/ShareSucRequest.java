package com.zhongsou.souyue.net.share;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 中搜零拍分享回调接口
 * @date 2016/01/11
 */
public class ShareSucRequest extends BaseUrlRequest {

    private String url;
    private boolean refresh;
    private boolean mForceCache;

    public ShareSucRequest(int id, String url, IVolleyResponse response) {
        super(id, response);
        this.url = url;
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
     * 添加参数
     * @param shareTo
     * @param content
     */
    public void setParams(String shareTo,
                          String content){
        addParams("cab", "share");
        addParams("shareTo", shareTo);
        addParams("content", content);
    }
}
