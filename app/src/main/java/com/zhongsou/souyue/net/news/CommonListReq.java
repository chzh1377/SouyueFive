package com.zhongsou.souyue.net.news;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.util.StringUtils;
import com.zhongsou.souyue.module.Ad;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.ListDeserializer;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: 公共的列表 网络请求
 * @auther: qubian
 * @data: 2016/4/25.
 */
public class CommonListReq extends BaseUrlRequest {
    public static final int COMMENT_LIST_DATA_INDEX_HASMORE = 0;//是否还有更多
    public static final int COMMENT_LIST_DATA_INDEX_TOPLIST = 1;//置顶数据索引
    public static final int COMMENT_LIST_DATA_INDEX_FOCUS = 2;//轮播图数据索引
    public static final int COMMENT_LIST_DATA_INDEX_NEWSLIST = 3;//正常列表数据索引
    public static final int COMMENT_LIST_DATA_INDEX_ADLIST = 4;//广告列表索引
    public static final int COMMENT_LIST_DATA_INDEX_EXTENDS = 5;// 额外的字段

    public String mUrl;
    public Gson mGson;
    public boolean isRefresh;
    private boolean mForceRefresh;

    public String getUrl() {
        return getSouyueHost() + mUrl;
    }

    public CommonListReq(int id, String url, IVolleyResponse response) {
        super(id, response);
        mUrl = url;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseListData.class, new ListDeserializer());
        mGson = builder.create();
    }

    public void setParams(String lastid, boolean refresh) {
        setParams(lastid, 10, refresh);
    }

    public void setParams(String lastid, int count, boolean refresh) {
        addParams("lastId", lastid);
        addParams("count", count + "");// 默认传10，可能不传
        isRefresh = refresh;
    }

    public void setOtherParams(Map<String, String> params) {
        if (params != null) {
            Set<String> keys = params.keySet();
            if (keys != null) {
                for(String key :keys)
                {
                    addParams(key, params.get(key));
                }
            }
        }
    }

    public void setmForceRefresh(boolean mForceRefresh) {
        this.mForceRefresh = mForceRefresh;
    }

    @Override
    public boolean isForceRefresh() {
        return mForceRefresh;
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
        boolean hasmore = response.getHeadBoolean("hasMore");
        List<Ad> adList = new Gson().fromJson(response.getHead().getAsJsonArray("adList"), new TypeToken<List<Ad>>() {
        }.getType());
        JsonObject obj = response.getBody();
        List<Object> list = new ArrayList<Object>();
        List<BaseListData> topList = new ArrayList<>();
        List<BaseListData> focusList = new ArrayList<>();
        List<BaseListData> newsList = new ArrayList<>();
        List<BaseListData> extensList = new ArrayList<>();
        if (obj.has("topList")) {
            topList = mGson.fromJson(obj.get("topList"), new TypeToken<ArrayList<BaseListData>>() {
            }.getType());
            for (BaseListData data : topList) {//设置是置顶数据
                data.setLocalTop(true);
            }
        }
        if (obj.has("focusList")) {
            focusList = mGson.fromJson(obj.get("focusList"), new TypeToken<ArrayList<BaseListData>>() {
            }.getType());
        }
        if (obj.has("newsList")) {
            newsList = mGson.fromJson(obj.get("newsList"), new TypeToken<ArrayList<BaseListData>>() {
            }.getType());
        }
        if (obj.has("extensList")) {
            extensList = mGson.fromJson(obj.get("extensList"), new TypeToken<ArrayList<BaseListData>>() {
            }.getType());
        }
        list.add(COMMENT_LIST_DATA_INDEX_HASMORE, hasmore);
        list.add(COMMENT_LIST_DATA_INDEX_TOPLIST, topList);
        list.add(COMMENT_LIST_DATA_INDEX_FOCUS, focusList);
        list.add(COMMENT_LIST_DATA_INDEX_NEWSLIST, newsList);
        list.add(COMMENT_LIST_DATA_INDEX_ADLIST, adList);
        list.add(COMMENT_LIST_DATA_INDEX_EXTENDS, extensList);
        return list;
    }
}
