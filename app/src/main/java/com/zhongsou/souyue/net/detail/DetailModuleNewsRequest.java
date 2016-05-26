package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 模板网络请求
 * @auther: qubian YanBin
 * @data: 2016/2/29.
 */
public class DetailModuleNewsRequest extends BaseUrlRequest {

    public String URL = HOST + "detail/srp.detail.main.groovy";
    public boolean isForceCache = false;    //不只取缓存
    public boolean isForceRefresh = false;  //默认false ： 有缓存去缓存，没缓存访问网络

    public DetailModuleNewsRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String keyword, String pushId, String srpId, String url) {
        addParams("pushId", pushId);
        addParams("keyword", keyword);
        addParams("srpId", srpId);
        addParams("url", url);
    }

    @Override
    public boolean isForceCache() {
        return isForceCache;
    }

    @Override
    public boolean isForceRefresh() {
        return isForceRefresh;
    }

    public void setForceCache(boolean forceCache) {
        isForceCache = forceCache;
    }

    public void setForceRefresh(boolean forceRefresh) {
        isForceRefresh = forceRefresh;
    }

    //http://61.135.210.239:8888/d3api2/detail/srp.detail.main.groovy
    // ?keyword=%E9%A9%AC%E4%BA%91&srpId=bafd40c853b579b292ab7cb49a7a5597
    // &url=http%3A%2F%2Fmt.sohu.com%2F20160407%2Fn443569141.shtml
    // &userId=42552
    // &vc=5.1
}
