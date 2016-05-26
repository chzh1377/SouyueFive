package com.zhongsou.souyue.net.detail;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.VideoAboutResult;
import com.zhongsou.souyue.module.Ad;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 相关视频 5.2
 * @auther: qubian
 * @data: 2016/3/24.
 */
public class VideoAboutListReq extends BaseUrlRequest {
    public static final int VIDEO_DETAIL_LIST_FOCUS=0;
    public static final int VIDEO_DETAIL_LIST_TOP=1;
    public static final int VIDEO_DETAIL_LIST_NEWS=2;
    public static final int VIDEO_DETAIL_LIST_SHORT_URL=3;
    public static final int VIDEO_DETAIL_LIST_FOOTVIEW=4;
    private   String URL = HOST+"detail/video.detail.about.groovy";

    public VideoAboutListReq(int id, IVolleyResponse response) {
        super(id,  response);
    }


    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String kid,long userid)
    {
        addParams("userId", userid+"");
        addParams("kid", kid);
    }

    @Override
    public Object doParse(CVolleyRequest request, String response)
            throws Exception {
        HttpJsonResponse res = (HttpJsonResponse) super.doParse(request,
                response);
        Object result = null;
        switch (request.getmId()) {
            case HttpCommon.DETAIL_VIDEO_ABOUT:
                result = parseList(res);
                break;
            default:
                return super.doParse(request, response);
        }
        return result;
    }
    private Object parseList(HttpJsonResponse res) {

        List<VideoAboutResult> focusList = new Gson().fromJson(res
                        .getBody().getAsJsonArray("focusList"),
                new TypeToken<List<VideoAboutResult>>() {
                }.getType());
        List<VideoAboutResult> newsList = new Gson().fromJson(res
                        .getBody().getAsJsonArray("newsList"),
                new TypeToken<List<VideoAboutResult>>() {
                }.getType());
        List<VideoAboutResult> topList = new Gson().fromJson(res
                        .getBody().getAsJsonArray("topList"),
                new TypeToken<List<VideoAboutResult>>() {
                }.getType());
        String shortUrl= res.getBody().get("shortUrl").getAsString();
        FootItemBean footView = new Gson().fromJson(res.getBody().getAsJsonObject("footView"), new TypeToken<FootItemBean>() {
                }.getType());
        List<Object> result = new ArrayList<Object>();
        result.add(VIDEO_DETAIL_LIST_FOCUS, focusList);
        result.add(VIDEO_DETAIL_LIST_TOP, topList);
        result.add(VIDEO_DETAIL_LIST_NEWS, newsList);
        result.add(VIDEO_DETAIL_LIST_SHORT_URL, shortUrl);
        result.add(VIDEO_DETAIL_LIST_FOOTVIEW, footView);
        return result;

    }
}
