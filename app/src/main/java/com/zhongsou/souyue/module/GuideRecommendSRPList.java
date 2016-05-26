package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.ArrayList;
import java.util.List;

public class GuideRecommendSRPList extends ResponseObject {
	
    private List<GuideRecommendSRP> list = new ArrayList<GuideRecommendSRP>();
    
	public GuideRecommendSRPList(HttpJsonResponse response) {
        this.list = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<GuideRecommendSRP>>() {}.getType());
    }
	
    public List<GuideRecommendSRP> list() {
        return list;
    }

    public void list_$eq(List<GuideRecommendSRP> list) {
        this.list = list;
    }
    
}
