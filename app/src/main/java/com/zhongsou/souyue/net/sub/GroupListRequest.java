package com.zhongsou.souyue.net.sub;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.Group;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zhou on 2015/12/12.
 */
public class GroupListRequest extends BaseUrlRequest {
    private final String URL =HOST + "subscribe/group.list.groovy";

    public  String getUrl(){
        return URL;
    }
    public GroupListRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    public void setParams(String token, String srpId){
        addParams("token", token);
        addParams("srpId", srpId);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse repo = (HttpJsonResponse) super.doParse(requet, res);
        return new Gson().fromJson(repo.getBodyArray(), new TypeToken<List<Group>>() {}.getType());
    }
}
