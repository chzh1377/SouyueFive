package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

@SuppressWarnings("serial")
public class HomePage extends ResponseObject {

    private List<HomePageItem> item;
    private String guestToken;
    private long guestId;

    public HomePage(HttpJsonResponse response) {
        item = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<HomePageItem>>() {}.getType());
        guestToken = response.getHeadString("guestToken");
        guestId = response.getHeadLong("guestId", 0);
    }

    public List<HomePageItem> item() {
        return item;
    }

    public void item_$eq(List<HomePageItem> item) {
        this.item = item;
    }

    public String guestToken() {
        return guestToken;
    }

    public void guestToken_$eq(String guestToken) {
        this.guestToken = guestToken;
    }

    public long guestId() {
        return guestId;
    }

    public void guestId_$eq(long guestId) {
        this.guestId = guestId;
    }


}
