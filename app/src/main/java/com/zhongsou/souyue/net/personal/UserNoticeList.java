package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 自动推送列表
 * Created by lvqiang on 15/12/15.
 */
public class UserNoticeList extends BaseUrlRequest {
    private String URL = HOST + "notice/notice.user.list.groovy";

    public UserNoticeList(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }
    
    public void setParams( long lastId){
        addParams("lastId", lastId == 0 ? "" : String.valueOf(lastId));
    }
}
