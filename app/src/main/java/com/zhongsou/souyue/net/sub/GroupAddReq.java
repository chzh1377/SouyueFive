package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

/**
 * @description:创建组
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class GroupAddReq extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/group.user.create.groovy";

    public String getUrl() {
        return URL;
    }

    public GroupAddReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    /**
     *
     * @param groupName
     * @param groupMember [{category:"srp", id:srpid},{category:"interest", id:interestId}]
     */
    public void setParams(String groupName, String groupMember,String groupImage){
        addParams("groupName", groupName);
        addParams("groupMember", groupMember);
        addParams("groupImage", groupImage);
        addParams("token", SYUserManager.getInstance().getToken());
    }
}
