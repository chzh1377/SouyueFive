package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 添加收藏
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class AddFavoriteReq extends BaseUrlRequest {

    private   String URL = HOST+"favorite/favorite.add.groovy";

    public AddFavoriteReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String token, String url,
                          String title, String image,
                          String description, long date,
                          String source, String keyword,
                          String srpId)
    {
        addParams("token", token);
        addParams("url", url);
        addParams("title", title);
        addParams("image", image);
        addParams("description", description);
        addParams("date", String.valueOf(date));
        addParams("source", source);
        addParams("keyword", keyword);
        addParams("srpId", srpId);
    }
    public void setParams( String token, long newsId)
    {
        addParams("token", token);
        addParams("newsId", String.valueOf(newsId));
    }
}
