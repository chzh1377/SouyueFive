package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/16.
 * 订阅 child
 * 订阅大全，三级分类
 */
public class SubGroupInterestReq extends BaseUrlRequest {

    private  final String URL = HOST+"interest/group.interest.groovy";

    public String getUrl(){
        return  URL;
    }

    public SubGroupInterestReq(int id, IVolleyResponse response) {
        super(id,response);
    }


    public void setParameters(String groupId){
        addParams("group_id",groupId);
    }


    public static void send(int id, String token,String groupId, IVolleyResponse callback){
        SubGroupInterestReq req = new SubGroupInterestReq(id,callback);
        req.setParameters(groupId);
        CMainHttp.getInstance().doRequest(req);
    }

}