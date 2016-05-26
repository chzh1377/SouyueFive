package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * @description: 编辑组
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class GroupEditReq extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/group.user.edit.groovy";

    public String getUrl() {
        return URL;
    }
    public GroupEditReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }
    /**
     *
     * @param groupId
     * @param
     */
    public void setParams(String groupId, String groupName,String groupMember,String groupImage){
        addParams("groupId", groupId);
        if(StringUtils.isNotEmpty(groupName))
        {
            addParams("groupName", groupName);
        }
        addParams("groupMember", groupMember);
        if(StringUtils.isNotEmpty(groupImage))
        {
            addParams("groupImage", groupImage);
        }
        addParams("token", SYUserManager.getInstance().getToken());
    }
}
