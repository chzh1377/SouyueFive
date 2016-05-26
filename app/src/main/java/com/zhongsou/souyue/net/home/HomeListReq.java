package com.zhongsou.souyue.net.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.ListDeserializer;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取首页列表
 * Created by lvqiang on 15/12/12.
 */
public class HomeListReq extends BaseUrlRequest {

    public static final int HOME_DATA_INDEX_HASMORE=0;//是否还有更多
    public static final int HOME_DATA_INDEX_TOPLIST=1;//置顶数据索引
    public static final int HOME_DATA_INDEX_FOCUS=2;//轮播图数据索引
    public static final int HOME_DATA_INDEX_NEWSLIST=3;//正常列表数据索引

    public String HTTP_URL_LOGIN=HOST+"webdata/homepage.news5.0.groovy";
    private boolean mForceRefresh;
    private Gson mGson;

    public HomeListReq(int id, IVolleyResponse response) {
        super(id, response);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseListData.class, new ListDeserializer());
        mGson = builder.create();
    }

    @Override
    public String getUrl() {
        return HTTP_URL_LOGIN;
    }

    @Override
    public boolean isForceRefresh() {
        return mForceRefresh;
    }

    public void setParams(String type, String keyword,String srpId,String lastId, String indexId){
        addParams("type", type);
        addParams("keyword", keyword);
        addParams("srpId", srpId);
        addParams("lastId", lastId);
        addParams("indexId", indexId);
    }

    public void setmForceRefresh(boolean forceRefresh) {
        this.mForceRefresh = forceRefresh;
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
        boolean hasmore = response.getHeadBoolean("hasMore");
        JsonObject obj = response.getBody();
        List<Object> list = new ArrayList<Object>();
        List<BaseListData> topList = mGson.fromJson(obj.get("topList"),new TypeToken<ArrayList<BaseListData>>(){}.getType());
        List<BaseListData> focusList = mGson.fromJson(obj.get("focusList"),new TypeToken<ArrayList<BaseListData>>(){}.getType());
        List<BaseListData> newsList = mGson.fromJson(obj.get("newsList"),new TypeToken<ArrayList<BaseListData>>(){}.getType());
        for (BaseListData data:topList){//设置是置顶数据
            data.setLocalTop(true);
        }
        list.add(HOME_DATA_INDEX_HASMORE,hasmore);
        list.add(HOME_DATA_INDEX_TOPLIST,topList);
        list.add(HOME_DATA_INDEX_FOCUS,focusList);
        list.add(HOME_DATA_INDEX_NEWSLIST,newsList);
        return list;
    }


}
