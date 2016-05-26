package com.zhongsou.souyue.net.search;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 搜索接口 ，获取第三代srp页面，首页数据，get方式
 * @date 2015/12/13
 */
public class GetSrpIndexDataRequest extends BaseUrlRequest {

    private String url;
    private boolean refresh;
    private boolean mForceCache;

    public GetSrpIndexDataRequest(int id, IVolleyResponse response) {
        super(id, response);
        url = getSouyueSRPHost() + "search";
    }

    public void setForceCache(boolean force){
        mForceCache = force;
    }

    @Override
    public boolean isForceCache() {
        return mForceCache;
    }

    public void setForceRefresh(boolean refresh){
        this.refresh = refresh;
    }
    @Override
    public boolean isForceRefresh() {
        return refresh;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getMethod() {
        return BaseUrlRequest.REQUEST_METHOD_GET;
    }


//    public String getHost() {
//
//        switch (env) {
//            case SOUYUE_TEST:
//                return "http://103.29.135.93:2046/d3api3/";
//            case SOUYUE_PRE_ONLINE:
//                return "http://103.7.220.208:2046/d3api3/";// SRP首页现在貌似没有预上线环境
//            case SOUYUE_PRE_ONLINE_FOR_SRP:
//                return "http://103.7.220.208:2046/d3api3/";
//            case SOUYUE_ONLINE:
//                return "http://api2.souyue.mobi/d3api3/";
//            default:
//                return "http://api2.souyue.mobi/d3api3/";
//        }
//    }

    // 搜悦SRP首页环境
    private static String getSouyueSRPHost() {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
            case SOUYUE_DEVLOPER:
                return "http://103.29.135.93:2046/d3api3/";
            case SOUYUE_PRE_ONLINE:
                return "http://103.7.220.208:2046/d3api3/";// SRP首页现在貌似没有预上线环境
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://103.7.220.208:2046/d3api3/";
            case SOUYUE_ONLINE:
                return "http://api2.souyue.mobi/d3api3/";
            default:
                return "http://api2.souyue.mobi/d3api3/";
        }
    }

    /**
     *
     * 添加参数
     *
     * @param srpId
     * @param keyword
     * @param latitude 维度
     * @param longitude 经度
     * @param city 城市
     * @param userId 用户ID
     * @param from  SRP来源1:订阅页 2：搜索页
     */
    public void setParams(String srpId,
                          String keyword,
                          String latitude,
                          String longitude,
                          String city,
                          String userId,
                          int from){
        try {
            addParams("keyword", keyword);
            addParams("city",city);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addParams("srpId", srpId);
        addParams("souyueId", userId);
        addParams("latitude", latitude);
        addParams("longitude", longitude);
        addParams("type", String.valueOf(from));
    }
}
