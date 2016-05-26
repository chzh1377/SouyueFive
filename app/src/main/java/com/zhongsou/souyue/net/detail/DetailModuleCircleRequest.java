package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description:
 * @auther: qubian YanBin
 * @data: 2016/2/29.
 */
public class DetailModuleCircleRequest extends BaseUrlRequest {

    public String URL = HOST + "detail/blog.detail.main.groovy";
    public boolean isForceCache = false;    //不只取缓存
    public boolean isForceRefresh = false;  //默认false ： 有缓存去缓存，没缓存访问网络

    public DetailModuleCircleRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setBlogParams(String blog_id) {
        addParams("blogId", blog_id);
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

    //   http://192.168.31.240:8888/d3api2/detail/blog.detail.main.groovy?
// vc=5.2.099
// &blogId=115651984
// &userId=42552


}
