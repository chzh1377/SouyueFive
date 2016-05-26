package com.zhongsou.souyue.net.selfCreate;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangchunyan on 2015/12/15.
 * 删除原创
 */
public class SelfCreateDel extends BaseUrlRequest {
    public  String selfCreateDel = HOST
            + "selfcreate/delete.groovy"; // 删除用户原创
    public SelfCreateDel(int id,IVolleyResponse response){
        super(id,response);
    }
    @Override
    public String getUrl() {
        return selfCreateDel;
    }
    public void setParams(String id){
        addParams("id", id);
    }
}
