package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 第三方登录
 * Created by lvqiang on 15/12/12.
 */
public class DetailUpReq extends BaseUrlRequest {

    public String URL=HOST+"favorite/up.add.groovy";

    public DetailUpReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String url, String title, String image,
                          String description, Long date, String source, String keyword,
                          String srpId){
        addParams("url", url);
        addParams("title", title);
        addParams("image", image);
        addParams("description", description);
        addParams("date", String.valueOf(date));
        addParams("source", source);
        addParams("keyword", keyword);
        addParams("srpId", srpId);
    }

    public void setParams(long newsId ){
        addParams("newsId", String.valueOf(newsId));
    }
}
