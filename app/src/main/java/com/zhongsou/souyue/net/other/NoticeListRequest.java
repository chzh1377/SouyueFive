package com.zhongsou.souyue.net.other;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.Group;
import com.zhongsou.souyue.module.Notice;
import com.zhongsou.souyue.module.NoticeList;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zhou on 2015/12/13.
 */
public class NoticeListRequest extends BaseUrlRequest {
    private String URL = HOST + "notice/notice.list.groovy";

    public NoticeListRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return URL;
    }


    public void setParams(String token, Long noticeMaxId){
        addParams("token", token);
        addParams("lastId", String.valueOf(noticeMaxId));
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse repo = (HttpJsonResponse) super.doParse(requet, res);
        return new Gson().fromJson(repo.getBodyArray(), new TypeToken<NoticeList>() {}.getType());
    }
}