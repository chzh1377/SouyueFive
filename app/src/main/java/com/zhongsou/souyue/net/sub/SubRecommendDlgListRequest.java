package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/21.
 * 请求订阅弹窗接口
 */
public class SubRecommendDlgListRequest extends BaseUrlRequest {
    private static final String TAG = "SubRecommendDlgListRequest";
    private String URL =  HOST+"webdata/recommend.subscribe.groovy";
    public static final String FIRST_TIME = "first"; // 第一次启动搜悦
    public static final String NORMAL = "normal"; // 一般情况
    public SubRecommendDlgListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
//        return "http://192.168.31.117:8888/d3api2/webdata/recommend.subscribe.groovy";
        return URL;
        //?token=0008abcb-c937-4e57-8a54-79a6a3926105&push_type=normal&appName=souyue
    }
    public void setParams(String push_type,long isPre,long listId){
        addParams("push_type",push_type);
        addParams("isPre",isPre+"");
        addParams("listId",listId+"");
    }

    @Override
    public boolean isForceRefresh() {
        return true;
    }

    //    @Override
//    public Object doParse(CVolleyRequest requet, String res) throws Exception {
//        Log.e(TAG,res);
//        return null;
//    }
}
