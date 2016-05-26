package com.zhongsou.souyue.net.personal;

import android.text.TextUtils;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 获取心情列表
 * Created by lvqiang on 15/12/16.
 */
public class UserMoodList extends BaseUrlRequest {

    private String URL = HOST + "user/mood.list.groovy";

    public UserMoodList(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String userId,String pno){
        if(!TextUtils.isEmpty(userId)){
            addParams("userId", userId);
        }
        addParams("pno", pno);
    }
}
