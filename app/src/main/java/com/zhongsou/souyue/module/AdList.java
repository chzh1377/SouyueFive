package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AdList extends ResponseObject {
    

    private List<AdListItem> list = new ArrayList<AdListItem>();
    
	public AdList(HttpJsonResponse response) {
        this.list = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<AdListItem>>() {}.getType());
    }

    public List<AdListItem> list() {
        return list;
    }

    public void list_$eq(List<AdListItem> list) {
        this.list = list;
    }
}
