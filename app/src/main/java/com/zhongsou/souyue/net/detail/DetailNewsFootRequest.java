package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 模板 - 详情相关阅读、广告、相关推荐等部分接口 - 新闻
 * @date 2016/03/30
 */
public class DetailNewsFootRequest extends BaseUrlRequest {

    public String URL = HOST + "detail/srp.detail.foot.groovy";

    public DetailNewsFootRequest(int id, IVolleyResponse response) {
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

    public void setParams(String keyword, String srpId, String pushId, String url) {
        addParams("keyword", keyword);
        addParams("srpId", srpId);
        addParams("pushId", pushId);
        addParams("url", url);  //新闻URL
    }

    //http://61.135.210.239:8888/d3api2/detail/srp.detail.foot.groovy
    // ?keyword=%E9%A9%AC%E4%BA%91
    // &srpId=bafd40c853b579b292ab7cb49a7a5597
    // &url=http://sd.china.com.cn/a/2016/rdtj_0406/523595.html
    // &userId=42552
}
