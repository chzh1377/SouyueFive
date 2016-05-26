package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description:
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class MblogDelete2Req   extends BaseUrlRequest {
    String url =HOST+"interest/comment.del.groovy";

    public MblogDelete2Req(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return url;
    }
    public void setParams(String token, long comment_id, int operflag,
                          String srpword, String srpid, int type)
    {
        addParams("token", token);
        addParams("comment_id", comment_id+"");
        addParams("operflag", operflag+"");
        addParams("type", type+"");
        addParams("srpword", srpword);
        addParams("srpid", srpid);
    }
}
