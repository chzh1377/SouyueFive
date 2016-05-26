package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/16.
 * 搜悦首页新闻
 */
public class CircleCateRecommendReq extends BaseUrlRequest {
    public  final String URL = HOST+"webdata/cate.recommend.groovy";
    public boolean isRefresh ;
    public boolean mForceCache ;

    public  String getUrl(){
        return URL;
    }

    public CircleCateRecommendReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(long userId, int id, boolean refresh) {
        addParams("userId",userId+"");
        addParams("id",id+"");
        isRefresh = refresh;
    }

    public void setForceCache(boolean forceCache){
        mForceCache = forceCache;
    }

    @Override
    public boolean isForceCache() {
        return mForceCache;
    }

    @Override
    public boolean isForceRefresh() {
        return isRefresh;
    }
}
