package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 4.2.2评论接口
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class CommentDetailReq extends BaseUrlRequest{
    String url = HOST +"interest/comment.add.groovy";
    public CommentDetailReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    public void setParams(String token, String url, String content,
                          String voice, String images, int voice_length, int operflag,
                          String srpid, String srpword, int _type, String main_title,
                          String main_images, String main_decsription, String main_date,
                          String main_source, boolean isAnonymous)
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
        addParams("type", _type+"");
        addParams("main_title", main_title);
        addParams("main_images", main_images);
        addParams("main_decsription", main_decsription);
        addParams("main_date", main_date);
        addParams("main_source", main_source);
        addParams("is_anonymity",isAnonymous?"1":"0");
    }
}
