package com.zhongsou.souyue.net.circle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.Ad;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.ListDeserializer;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:  5.1.1 圈吧 列表 和 精华区列表
 * @auther: qubian
 * @data: 2016/1/25.
 */

public class CircleEssenceListReq extends BaseUrlRequest {
    public static final int YAOWEN_DATA_INDEX_HASMORE=0;//是否还有更多
    public static final int YAOWEN_DATA_INDEX_TOPLIST=1;//置顶数据索引
    public static final int YAOWEN_DATA_INDEX_FOCUS=2;//轮播图数据索引
    public static final int YAOWEN_DATA_INDEX_NEWSLIST=3;//正常列表数据索引
    public static final int YAOWEN_DATA_INDEX_ADLIST=4;//广告列表索引
    private  String URL = HOST+"interest/prime4.0.mblog.list.groovy";
    public Gson mGson;
    public CircleEssenceListReq(int id, IVolleyResponse response) {
        super(id, response);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseListData.class, new ListDeserializer());
        mGson = builder.create();
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String token,long interest_id, int page, int psize, long last_sort_num)
    {
        addParams("token", token);
        addParams("interest_id", interest_id+"");
        addParams("pno", page+"");
        addParams("psize", psize+"");
        addParams("last_sort_num", last_sort_num+"");
    }
    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
        boolean hasmore = response.getHeadBoolean("hasMore");
        List<Ad> adList = new Gson().fromJson(response.getHead().getAsJsonArray("adList"), new TypeToken<List<Ad>>() {
        }.getType());
        JsonObject obj = response.getBody();
        List<Object> list = new ArrayList<Object>();
        List<BaseListData> topList = mGson.fromJson(obj.get("topList"),new TypeToken<ArrayList<BaseListData>>(){}.getType());
        List<BaseListData> focusList = mGson.fromJson(obj.get("focusList"),new TypeToken<ArrayList<BaseListData>>(){}.getType());
        List<BaseListData> newsList = mGson.fromJson(obj.get("newsList"),new TypeToken<ArrayList<BaseListData>>(){}.getType());
        for (BaseListData data:topList){//设置是置顶数据
            data.setLocalTop(true);
        }
        list.add(YAOWEN_DATA_INDEX_HASMORE,hasmore);
        list.add(YAOWEN_DATA_INDEX_TOPLIST,topList);
        list.add(YAOWEN_DATA_INDEX_FOCUS,focusList);
        list.add(YAOWEN_DATA_INDEX_NEWSLIST,newsList);
        list.add(YAOWEN_DATA_INDEX_ADLIST,adList);
        return list;
    }
}
