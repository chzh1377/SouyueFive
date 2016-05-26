package com.zhongsou.souyue.net.other;

import com.google.gson.Gson;
import com.zhongsou.souyue.module.Comment;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zhou on 2015/12/13.
 */
public class ChatRoomAddRequest extends BaseUrlRequest {
    private String URL = HOST + "chat/chat.add.groovy?";

    public ChatRoomAddRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String token, String keyword, String url, String voiceUrl, int voiceLength, String content, long replyToId, String title, String srpId){
        addParams("token", token);
        addParams("keyword", keyword);
        addParams("url", url);
        addParams("voiceUrl", voiceUrl);
        addParams("voiceLength", voiceLength <= 0 ? null : String.valueOf(voiceLength));
        addParams("content", content);
        addParams("replyToId", String.valueOf(replyToId));
        addParams("title", title);
        addParams("srpId", srpId);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse repo = (HttpJsonResponse) super.doParse(requet, res);
        return new Gson().fromJson(repo.getBodyArray(), Comment.class);
    }
}