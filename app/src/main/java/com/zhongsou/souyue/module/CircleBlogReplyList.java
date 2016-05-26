package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.model.CircleBlogReply;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/4/9.
 */
public class CircleBlogReplyList extends ResponseObject {

    private boolean hasMore;
    private List<CircleBlogReply> circleList;

    public CircleBlogReplyList(HttpJsonResponse response) {
        hasMore = response.getHeadBoolean("hasMore");
        circleList = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<CircleBlogReply>>() {}.getType());
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<CircleBlogReply> circleList() {
        return circleList;
    }

    public void circleList_$eq(List<CircleBlogReply> circleList) {
        this.circleList = circleList;
    }
}
