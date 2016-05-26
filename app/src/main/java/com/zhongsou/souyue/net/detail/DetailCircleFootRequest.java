package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 模板 - 详情相关阅读、广告、相关推荐等部分接口
 * @date 2016/03/30
 */
public class DetailCircleFootRequest extends BaseUrlRequest {

    public String URL = HOST + "detail/blog.detail.foot.groovy";

    public DetailCircleFootRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

//    public void setParams(String type, String keyword, String srpId, String url) {
//        addParams("blogId", "115651984");
//        addParams("userId", "42552");
//        addParams("keyword", "%E9%80%97%E6%AF%94%E5%9C%88");
//        addParams("srpId", "8843d6330fb84a5205647f42f6afe853");
//        addParams("signId", "d016bc71c63790cbfa53ad62492a45c0");
//    }

    public void setParams(String blogId, String srpId, String url) {
        addParams("blogId", blogId);
        addParams("srpId", srpId);
        addParams("url", url);
    }

    //订阅推荐
// http://192.168.31.240:8888/d3api2/detail/blog.detail.foot.groovy
// ?blogId=9115
// &userId=42552
// &srpId=a0590c00c4817e388d1cd82ab075ed1b
// &url=http%3A%2F%2Finterest.zhongsou.com%3Fsign_id%3D27df9dff6e8dca45f7c879c2fa610ee9%26blog_id%3D9115%26sign_info%3D9115%26srpid%3Da0590c00c4817e388d1cd82ab075ed1b%26srpword%3D%E7%99%BD%E8%89%B2%E6%AF%9B%E8%A1%A3%E5%9C%88

    //相关推荐
    //http://61.135.210.239:8888/d3api2/detail/blog.detail.foot.groovy?
    // blogId=115658770
    // &userId=42552
    // &srpId=f479751542203f3e5f4d300ddacd1127
    // &url=http%3A%2F%2Finterest.zhongsou.com%3Fsign_id%3De20dd8970911f747c2a576d7674cb5a7%26blog_id%3D115658770%26sign_info%3D115658770%26srpid%3Df479751542203f3e5f4d300ddacd1127%26srpword%3Dheyl%E5%85%AC%E5%BC%80%E5%9C%88
}
