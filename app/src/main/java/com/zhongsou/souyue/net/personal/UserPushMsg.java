package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 自动推送
 * Created by lvqiang on 15/12/15.
 */
public class UserPushMsg extends BaseUrlRequest {
    private String URL = HOST + "notice/userPushMsg.groovy";

    public UserPushMsg(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }
    
    public void setParams( String keyword, String srpId, String url, String title, String pushCont, String pushReason){
        addParams("keyword", keyword);
        addParams("srpId", srpId);
        addParams("url", url);
        addParams("title", title);
        addParams("pushCont", pushCont);
        addParams("pushReason", pushReason);
    }
}
