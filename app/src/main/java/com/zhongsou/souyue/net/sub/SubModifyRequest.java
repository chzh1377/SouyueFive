package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zhou on 2015/12/13.
 */
public class SubModifyRequest extends BaseUrlRequest {
    private String URL =  HOST+"subscribe/group.modify.groovy";

    public  String getUrl(){
        return URL;
    }
    public SubModifyRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String type, String token,List<Object> movedRss, long groupId){
        addParams("type", type);
        addParams("token", token);
        addParams("id", mkString(movedRss));
        addParams("groupId", String.valueOf(groupId));
    }

    private String mkString(List list) {
        return list.toString().replaceAll("[\\[\\]\\s]", "");
    }
}
