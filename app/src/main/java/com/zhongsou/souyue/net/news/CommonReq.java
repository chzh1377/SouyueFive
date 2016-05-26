package com.zhongsou.souyue.net.news;

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
 * 外部给url,要闻频道页面请求
 * Created by lvqiang on 15/12/25.
 */
public class CommonReq extends BaseUrlRequest {
    public static final int YAOWEN_DATA_INDEX_HASMORE=0;//是否还有更多
    public static final int YAOWEN_DATA_INDEX_TOPLIST=1;//置顶数据索引
    public static final int YAOWEN_DATA_INDEX_FOCUS=2;//轮播图数据索引
    public static final int YAOWEN_DATA_INDEX_NEWSLIST=3;//正常列表数据索引
    public static final int YAOWEN_DATA_INDEX_ADLIST=4;//广告列表索引

//    public  final String URL = HOST+"webdata/cate.recommend.groovy";
    public String mUrl;
    public Gson mGson;
    public boolean isRefresh ;

    public  String getUrl(){
        return mUrl;
    }

    public CommonReq(int id, String url, IVolleyResponse response) {
        super(id, response);
        mUrl = url;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseListData.class, new ListDeserializer());
        mGson = builder.create();
    }

    public void setParams(String lastid, boolean refresh) {
        addParams("lastId",lastid);
        isRefresh = refresh;
    }

    public void setParams(String lastid, int count, boolean refresh) {
        addParams("lastId",lastid);
        addParams("count",count+"");
        isRefresh = refresh;
    }

    @Override
    public boolean isForceRefresh() {
        return isRefresh;
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
