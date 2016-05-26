package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

@SuppressWarnings("serial")
public class CommentList extends ResponseObject{

    private boolean hasMore;
    private List<Comment> comments;

    public CommentList(HttpJsonResponse response) {
        hasMore = response.getHeadBoolean("hasMore");
        comments = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<Comment>>() {}.getType());
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<Comment> comments() {
        return comments;
    }

    public void comments_$eq(List<Comment> comments) {
        this.comments = comments;
    }




}
