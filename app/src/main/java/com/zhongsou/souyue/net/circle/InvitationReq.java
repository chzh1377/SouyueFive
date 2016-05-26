package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 邀请好友加入圈
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class InvitationReq extends BaseUrlRequest {

    public   String imFriend = HOST
            + "interest/interest.invitation.groovy"; // 邀请好友加入圈
    public InvitationReq(int id, IVolleyResponse response) {
        super(id,  response);
    }

    @Override
    public String getUrl() {
        return imFriend;
    }

    public void setParams(long interest_id, String user_ids,String opSource)
    {
        addParams("interest_id", interest_id+"");
        addParams("user_ids", user_ids);
        addParams("opSource", opSource);
    }
}
