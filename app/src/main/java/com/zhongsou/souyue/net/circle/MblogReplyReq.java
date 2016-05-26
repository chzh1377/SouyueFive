package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: /4.2.2回复接口
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class MblogReplyReq  extends BaseUrlRequest {
    String url =HOST+"interest/comment.reply.add.groovy";

    public MblogReplyReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return url;
    }
    public void setParams(String token, String url, String content,
                          String voice, String images, int voice_length, int operflag,
                          String srpid, String srpword, long comment_id, int _detailtype,boolean anonymous)
    {
        addParams("token", token);
        addParams("url", url);
        addParams("content", content);
        addParams("voice", voice);
        addParams("images", images);
        addParams("voice_length", voice_length+"");
        addParams("operflag", operflag+"");
        addParams("srpid", srpid);
        addParams("srpword", srpword);
        addParams("comment_id", comment_id+"");
        addParams("type", _detailtype+"");
        addParams("is_anonymity",anonymous?"1":"0");
    }
}