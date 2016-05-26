package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 删除心情
 * Created by lvqiang on 15/12/16.
 */
public class UserDeleteMood extends BaseUrlRequest {

    private String URL = HOST + "user/mood.del.groovy";

    public UserDeleteMood(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String id){
        addParams("id",id);
    }
}
