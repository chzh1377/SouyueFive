package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 4.2.2获取回复列表
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class MBlogCommentsReq extends BaseUrlRequest {
    String url =HOST+"interest/comment.reply.list.groovy";
    
    public MBlogCommentsReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return url;
    }
    public void setParams(String token, long comment_id, int pno,
                          int psize, int operflag, String srpword, String srpid,
                          long last_sort_num)
    {
        addParams("token", token);
        addParams("comment_id", comment_id+"");
        addParams("operflag", operflag+"");
        addParams("psize", psize+"");
        addParams("pno", pno+"");
        addParams("srpword", srpword);
        addParams("srpid", srpid);
        addParams("last_sort_num", last_sort_num+"");
    }
}
