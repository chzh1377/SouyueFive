package com.zhongsou.souyue.net.gallerynews;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.module.GalleryNewsList;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/1/22.
 * 获取图集首页的信息
 * URL:photos/details.groovy
 * 参数:
 * 返回值：
 * GalleryNewsList.java
 */
public class GalleryHomeListPushRequest extends BaseUrlRequest {

    public static final String MYURL = "detail/pic.push.detail.groovy";

    public GalleryHomeListPushRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }

    public void setParams(String pushId) {
        addParams("keyId", pushId);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse resp = (HttpJsonResponse) super.doParse(requet, res);
        return parseHomeList(requet.getCacheKey(), resp);
    }

    public static GalleryNewsList parseHomeListCache(String json) {
        return new Gson().fromJson(json, new TypeToken<GalleryNewsList>() {
        }.getType());
    }

    /**
     * 解析获取首页内容返回的数据
     *
     * @param res
     * @return
     */
    private  GalleryNewsList parseHomeList(final String cacheKey, final HttpJsonResponse res) {
        JsonObject   body = res.getBody();
        final String s    = body.toString();
        GalleryNewsList o = new Gson().fromJson(body, new TypeToken<GalleryNewsList>() {
        }.getType());
        JsonObject data = body.getAsJsonObject("data");
        o.setKeyword(data.get("keyword").getAsString());
        o.setNewstime(data.get("newstime").getAsString());
        o.setSource(data.get("source").getAsString());
        o.setSrpid(data.get("srpid").getAsString());
        o.setTitle(data.get("title").getAsString());
        o.setUrl(data.get("url").getAsString());
        new Thread() {
            @Override
            public void run() {
                setCache(cacheKey, s);
            }
        }.start();
        return o;
    }

    /**
     * @param pushId
     * @param callback
     * @param id
     */
    public static void send(String pushId,IVolleyResponse callback, int id) {
        GalleryHomeListPushRequest request = new GalleryHomeListPushRequest(id, callback);
        request.setParams(pushId);
        CMainHttp.getInstance().doRequest(request);
    }

    /**
     * 离线缓存
     *
     * @param key
     * @param value
     */
    public static void setCache(String key, String value) {
        CommSharePreference.getInstance().putValue(0, key, value);
    }

    /**
     * 离线缓存
     *
     * @param key
     */
    public static String getCache(String key) {
        return CommSharePreference.getInstance().getValue(0, key, "");
    }
}
