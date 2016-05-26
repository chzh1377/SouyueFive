package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")

/**
 * 原创数据封装
 * @author iamzl
 *
 */

public class SelfCreate extends ResponseObject {

    private boolean hasMore;
    private List<SelfCreateItem> items;
    private User userInfo;
    private String requestUrl;

    public SelfCreate(HttpJsonResponse response) {
        hasMore = response.getHeadBoolean("hasMore");
        JsonArray baseJo = response.getBodyArray();
        if (baseJo != null&&baseJo.size()>0){
            JsonArray jarry = baseJo.get(0).getAsJsonObject().getAsJsonArray("selfCreate"); 
            if (jarry != null)
                    items = new Gson().fromJson(jarry, new TypeToken<List<SelfCreateItem>>() {}.getType());
            JsonObject jo = baseJo.get(0).getAsJsonObject().getAsJsonObject("userInfo");
            if (jo != null)
                    userInfo = new Gson().fromJson(jo, new TypeToken<User>(){}.getType());
        }
        
        if (null != items && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                SelfCreateItem images = items.get(i);
                if (images.conpic().trim().length() == 0) {
                    images.conpics_$eq(null);
                } else {
                    images.conpics_$eq(Arrays.asList(images.conpic().trim().split(" ")));
                }
            }
        }
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<SelfCreateItem> items() {
        return items;
    }

    public void items_$eq(List<SelfCreateItem> items) {
        this.items = items;
    }

    public String requestUrl() {
        return requestUrl;
    }

    public void requestUrl_$eq(String requestUrl) {
        this.requestUrl = requestUrl;
    }
    
    public User userInfo(){
    		return userInfo;
    }
    
    public void userInfo_$eq(User userInfo){
    		this.userInfo = userInfo;
    }

}
