package com.zhongsou.souyue.net.group;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.ListDeserializer;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyw on 2016/3/26.
 * 获取订阅分组 下面的list数据
 */
public class GroupListDataReq extends BaseUrlRequest {
    public static final  int    HOME_DATA_INDEX_HASMORE  = 0;//是否还有更多
    public static final  int    HOME_DATA_INDEX_TOPLIST  = 1;//置顶数据索引
    public static final  int    HOME_DATA_INDEX_FOCUS    = 2;//轮播图数据索引
    public static final  int    HOME_DATA_INDEX_NEWSLIST = 3;//正常列表数据索引
    private static final String PAGE_SIZE                = "20";

    public static final String TAG    = GroupListDataReq.class.getSimpleName();
    public static final String MY_URL = "webdata/groupNews.list.groovy";

    private boolean mForceRefresh = false;

    public GroupListDataReq(int id, IVolleyResponse response) {
        super(id, response);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseListData.class, new ListDeserializer());
        mGson = builder.create();
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.mForceRefresh = forceRefresh;
    }

    @Override
    public boolean isForceRefresh() {
        return mForceRefresh;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return getSouyueHost().concat(MY_URL);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse   response  = (HttpJsonResponse) super.doParse(requet, res);
        boolean            hasmore   = response.getHeadBoolean("hasMore");
        JsonObject         obj       = response.getBody();
        List<Object>       list      = new ArrayList<Object>();
        List<BaseListData> topList   = mGson.fromJson(obj.get("topList"), new TypeToken<ArrayList<BaseListData>>() {
        }.getType());
        List<BaseListData> focusList = mGson.fromJson(obj.get("focusList"), new TypeToken<ArrayList<BaseListData>>() {
        }.getType());
        List<BaseListData> newsList  = mGson.fromJson(obj.get("newsList"), new TypeToken<ArrayList<BaseListData>>() {
        }.getType());
        for (BaseListData data : topList) {//设置是置顶数据
            data.setLocalTop(true);
        }
        list.add(HOME_DATA_INDEX_HASMORE, hasmore);
        list.add(HOME_DATA_INDEX_TOPLIST, topList);
        list.add(HOME_DATA_INDEX_FOCUS, focusList);
        list.add(HOME_DATA_INDEX_NEWSLIST, newsList);
        return list;
    }


    public void setParams(String groupId, String lastId,String indexId) {
        addParams("groupId", groupId);
        addParams("pageSize", GroupListDataReq.PAGE_SIZE);
        addParams("lastId", lastId);
        addParams("lastId", lastId);
        addParams("indexId", indexId);
    }

    public static void send(int id, IVolleyResponse resp, String groupId, String lastId,String indexid, boolean forceRresh) {
        GroupListDataReq req = new GroupListDataReq(id, resp);
        req.setParams(groupId, lastId,indexid);
        req.setForceRefresh(forceRresh);
        CMainHttp.getInstance().doRequest(req);
    }
}
