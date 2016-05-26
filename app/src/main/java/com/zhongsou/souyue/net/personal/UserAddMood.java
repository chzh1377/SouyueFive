package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 添加心情
 * Created by lvqiang on 15/12/16.
 */
public class UserAddMood extends BaseUrlRequest {

    private String URL = HOST + "user/mood.add.groovy";

    public UserAddMood(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String moodId,String signature){
        addParams("mood_id", moodId);
        addParams("signature", signature);
    }
}
