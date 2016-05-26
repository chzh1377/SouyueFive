package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.List;

@SuppressWarnings("serial")
public class Subscribe extends ResponseObject {
    private List<SubscribeItem> srp;
    private String sid;
    private String groupName;
    private long groupId;
    private String requestUrl = "";
    private List<SubscribeItem> items;

    public Subscribe(HttpJsonResponse response) {
        items = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<SubscribeItem>>() {}.getType());
    }

    public Subscribe(String groupName, long groupId, List<SubscribeItem> srp, String sid) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.srp = srp;
        this.sid = sid;
    }

    public List<SubscribeItem> getSrp() {
        return srp;
    }



    public void setSrp(List<SubscribeItem> srp) {
        this.srp = srp;
    }



    public String getSid() {
        return sid;
    }



    public void setSid(String sid) {
        this.sid = sid;
    }



    public String getGroupName() {
        return groupName;
    }



    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    public long getGroupId() {
        return groupId;
    }



    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }



    public String getRequestUrl() {
        return requestUrl;
    }



    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }



    public List<SubscribeItem> getItems() {
        return items;
    }



    public void setItems(List<SubscribeItem> items) {
        this.items = items;
    }



    public String requestUrl() {
        return requestUrl;
    }

    public void requestUrl_$eq(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public List<SubscribeItem> items() {
        return items;
    }

    public void items_$eq(List<SubscribeItem> items) {
        this.items = items;
    }



}
