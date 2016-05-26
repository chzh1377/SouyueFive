package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

@SuppressWarnings("serial")
public class WendaDetail extends ResponseObject {

    private String id = "";
    private List<Wenda> wendaList;
    private boolean hasMore;

    public WendaDetail(HttpJsonResponse response) {
        wendaList = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<Wenda>>() {}.getType());
        hasMore = response.getHeadBoolean("hasMore");
    }

    public String id() {
        return id;
    }

    public void id_$eq(String id) {
        this.id = id;
    }

    public List<Wenda> wendaList() {
        return wendaList;
    }

    public void wendaList_$eq(List<Wenda> wendaList) {
        this.wendaList = wendaList;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }


}
