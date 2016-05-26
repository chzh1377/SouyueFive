package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 详情页--添加评论
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class AddCommentReq extends BaseUrlRequest {

    public   String commentAdd = HOST + "comment/comment.add.groovy";// 添加评论

    public AddCommentReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return commentAdd;
    }

    public void setParams(String token, String keyword,
                          String url, String voiceUrl,
                          int voiceLength, String content,
                          long replyToId, String title,
                          String srpId)
    {
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
}
