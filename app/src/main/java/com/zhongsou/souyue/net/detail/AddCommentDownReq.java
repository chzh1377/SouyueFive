package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 踩
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class AddCommentDownReq  extends BaseUrlRequest {

    public   String commentAdd = HOST+"interest/comment.dobad.groovy";

    public AddCommentDownReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return commentAdd;
    }
    public void setParams(String _keyword,String _srpid,String _url,String _token,int operflag,int type,String main_title,String main_images,String main_decsription,String main_date,String main_source)
    {
        addParams("srpword", _keyword);
        addParams("srpid", _srpid);
        addParams("url", _url);
        addParams("token", _token);
        addParams("operflag", operflag+"");
        addParams("type", type+"");
        addParams("main_title", main_title);
        addParams("main_images", main_images);
        addParams("main_decsription", main_decsription);
        addParams("main_date", main_date);
        addParams("main_source", main_source);
    }
}
