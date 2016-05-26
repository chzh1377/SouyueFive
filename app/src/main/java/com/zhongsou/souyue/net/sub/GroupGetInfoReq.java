package com.zhongsou.souyue.net.sub;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

/**
 * @description: 获取组成员
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class GroupGetInfoReq extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/group.children.groovy";

    public String getUrl() {
        return URL;
    }
    public GroupGetInfoReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String groupId ){
        addParams("groupId", groupId);
        addParams("token", SYUserManager.getInstance().getToken());
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse repo = (HttpJsonResponse) super.doParse(requet, res);
        return new Gson().fromJson(repo.getBodyArray(), new TypeToken<List<SuberedItemInfo>>() {}.getType());
    }
}
