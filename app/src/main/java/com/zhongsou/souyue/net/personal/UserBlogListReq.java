package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 个人中心点击更多获取帖子列表
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class UserBlogListReq extends BaseUrlRequest {
    public   String getPostListForPerson = HOST
            + "interest/personal.mblog.list.groovy"; // 个人中心点击更多获取帖子列表
    public UserBlogListReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getPostListForPerson;
    }

    public void setParams(long user_id, String new_srpid,
                          long interest_id, int is_friend,
                          int pno, int psize,
                          long last_sort_num)
    {
        addParams("user_id", user_id+"");
        addParams("new_srpid", new_srpid);
        addParams("interest_id", interest_id+"");
        addParams("is_friend", is_friend+""); // 1是好友，2表示非好友
        addParams("pno", pno+"");
        addParams("psize", psize+"");
        addParams("last_sort_num", last_sort_num+"");
    }
}
