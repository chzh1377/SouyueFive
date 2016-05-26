package com.zhongsou.souyue.net.selfCreate;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangchunyan on 2015/12/15.
 * 原创列表
 */
public class SelfCreateList extends BaseUrlRequest {
    public  String selfCreateList = HOST + "selfcreate/list.groovy";
    @Override
    public String getUrl() {
        return selfCreateList;
    }
    public SelfCreateList(int id,IVolleyResponse response){
        super(id,response);
    }
    public void setParams(String column_type, String lastId){
        addParams("column_type", column_type);
        addParams("lastId", lastId);
    }
}
