package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 添加收藏
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class AddFavorite2Req extends BaseUrlRequest {

    private  String URL = HOST+"interest/favorite4.3.add.groovy";

    public AddFavorite2Req(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String _dataType, String _url, String _token,
                          int operflag, String mSrpId, String mKeyword, String mTitle,
                          String imageUrl)
    {
        addParams("data_type", _dataType);
        addParams("url", _url);
        addParams("token", _token);
        addParams("operflag", operflag+"");
        addParams("srpid", mSrpId);
        addParams("keyword", mKeyword);
        addParams("title", mTitle);
        addParams("image", imageUrl);
    }
    public void setParams( String token, long newsId)
    {
        addParams("token", token);
        addParams("newsId", String.valueOf(newsId));
    }
}
