package com.zhongsou.souyue.net.selfCreate;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangchunyan on 2015/12/15.
 * IM好友原创list
 */
public class FriendCreateList extends BaseUrlRequest {
    public  String friendCreateList = HOST
            + "selfcreate/list.2.groovy"; // 用户原创列表
    public FriendCreateList(int id,IVolleyResponse response){
        super(id,response);
    }
    @Override
    public String getUrl() {
        return friendCreateList;
    }
    public void setParams(String column_type,String lastId,long uid,int status){
        addParams("column_type", column_type);
        addParams("lastId", lastId);
        addParams("uid", uid+"");
        addParams("status", status+"");
    }
}
