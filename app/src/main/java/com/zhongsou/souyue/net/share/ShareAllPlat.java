package com.zhongsou.souyue.net.share;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 分享接口
 * Created by lvqiang on 15/12/15.
 */
public class ShareAllPlat extends BaseUrlRequest {
    private String URL = HOST + "favorite/share.add.groovy";

    public ShareAllPlat(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }
    
    public void setParams(String url, String title, String image, String description, String date, String source, String keyword, String srpId){
        addParams("url", url);
        addParams("title", title);
        addParams("image", image);
        addParams("description", description);
        addParams("date", date);
        addParams("source", source);
        addParams("keyword", keyword);
        addParams("srpId", srpId);
    }

    public void setParams(long newsId ){
        addParams("newsId", String.valueOf(newsId));
    }
}
