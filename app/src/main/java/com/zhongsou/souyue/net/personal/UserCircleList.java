package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 个人中心点击更多获取帖子列表
 * Created by lvqiang on 15/12/12.
 */
public class UserCircleList extends BaseUrlRequest {
    private String URL = HOST+"interest/personal.querymblog.list.groovy";


    public UserCircleList(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return URL;
    }


    public void addParams(long user_id, String new_srpid,
                          long interest_id, int psize, long last_sort_num){
        addParams("user_id", user_id+"");
        addParams("new_srpid", new_srpid);
        addParams("interest_id", interest_id+"");
        addParams("psize", psize+"");
        addParams("last_sort_num", last_sort_num+"");
    }


}
