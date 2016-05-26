package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 获取回复列表
 * Created by lvqiang on 15/12/16.
 */
public class UserReplyList extends BaseUrlRequest {
    public String URL = HOST + "interest/follow.my.blog.groovy";

    public UserReplyList(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String interest_id,String psize,String pno){
        addParams("interest_id",interest_id);
        addParams("psize", psize);
        addParams("pno", pno);
    }
}
