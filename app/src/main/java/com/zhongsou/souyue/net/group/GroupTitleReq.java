package com.zhongsou.souyue.net.group;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.SubGroupModel;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zyw on 2016/3/26.
 * 获取订阅分组 顶部的tab数据
 */
public class GroupTitleReq extends BaseUrlRequest {
    public static final String TAG = GroupTitleReq.class.getSimpleName();

    public static final String MY_URL = "subscribe/group.children.groovy";

    public GroupTitleReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost().concat(MY_URL);
    }

    public void setParams(String groupId) {
        addParams("groupId", groupId);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse resp = (HttpJsonResponse) super.doParse(requet, res);
        JsonArray        body = resp.getBodyArray();
        List<SubGroupModel> titles = mGson.fromJson(body, new TypeToken<List<SubGroupModel>>() {
        }.getType());
        return titles;
    }

    public static void send(int id, IVolleyResponse resp, String groupId,String categary) {
        GroupTitleReq req = new GroupTitleReq(id, resp);
        req.setParams(groupId);
        req.addKeyValueTag("gid", groupId);
        req.addKeyValueTag("cat",categary);
        CMainHttp.getInstance().doRequest(req);
    }
}
