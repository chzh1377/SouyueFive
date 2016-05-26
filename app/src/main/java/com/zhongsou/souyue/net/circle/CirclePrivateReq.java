package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import org.json.JSONArray;

/**
 * Created by wangqiang on 15/12/15.
 * 私密圈邀请好友
 */
public class CirclePrivateReq  extends BaseUrlRequest {
    public   String URL = HOST + "interest/interest.private.invitation.groovy"; //

    public CirclePrivateReq(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(String invite_message , JSONArray friends ,long interest_id,String opSource)
    {
        addParams("invite_message", invite_message);
        addParams("friends", friends.toString());
        addParams("interest_id", interest_id+"");
        addParams("opSource", opSource);
    }
}