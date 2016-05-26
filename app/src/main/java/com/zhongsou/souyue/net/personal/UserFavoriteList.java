package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 获取收藏列表
 * Created by lvqiang on 15/12/14.
 */
public class UserFavoriteList extends BaseUrlRequest {
    private String URL = HOST + "favorite/favorite.list.groovy";
    public UserFavoriteList(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(Long lastId){
        addParams("lastId", String.valueOf(lastId));
    }
}
