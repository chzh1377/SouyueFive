package com.zhongsou.souyue.net.volley;

import android.content.Context;

/**
 * 订阅相关Http
 */
public class CSuberListHttp extends AHttp {

//    public static final int SUBER_INFO_METHOD = 0x0001;
//    public static final int SUBER_REMCOMMEND_METHOD = 0x0002;
//    public static final int SUBER_UPDATE_METHOD = 0x0003;
//    public static final int SUBER_SERACH_METHOD = 0x0004;
//    public static final int SUBER_ORDER_METHOD = 0x0005;
//    public static final int SUBER_REPLAY = 0x0006;
//    public static final int SUBER_POST_SWITH = 0x00007;
//    public static final int SUBER_GET_SWITH = 0x00008;

//    public static final String HOST = UrlConfig.getSouyueHost();
//    private static final String SUBER_INFO_URL = HOST
//            + "subscribe/subscribe.list.my5.0.groovy";// 订阅列表页
//    private static final String SUBER_REMCOMMEND_URL = HOST
//            + "recommend/top.recommend.list.groovy";// 热门推荐
//    private static final String SUBER_UPDATE_URL = HOST
//            + "subscribe/subscribe.update.my5.0.groovy"; // 取消订阅
//    private static final String SUBER_SERACH_URL = HOST
//            + "recommend/search.enjoy.content.groovy"; // 订阅搜索
//    private static final String SUBER_ORDER_URL = HOST
//            + "subscribe/subscribe.modify5.0.groovy"; // 订阅排序
//    private static final String SUBER_GET_SWITCH_URL = HOST + "subscribe/user.switch.query.groovy";  // 获取订阅开关
//    private static final String SUBER_POST_SWITCH_URL = HOST+"subscribe/user.switch.cfg.groovy";  //

    public CSuberListHttp(Context _context) {
        super(_context, CSuberListHttp.class.getName());
    }

//    /**
//     * 获取订阅列表
//     *
//     * @param token
//     * @param vc
//     * @param imei
//     * @param callback
//     */
//    public void doSuberInfos(int id, String token, String vc, String imei,
//                             IVolleyResponse callback) {
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.addParams("vc", vc);
//        request.addParams("imei", imei);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_INFO_URL);
//        mVolley.doRequest(request);
//    }
//
//    /**
//     * 获取热门推荐
//     *
//     * @param token
//     * @param vc
//     * @param imei
//     * @param callback
//     */
//    public void doSuberRecomend(int id, String token, String vc, String imei,
//                                IVolleyResponse callback) {
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.addParams("vc", vc);
//        request.addParams("imei", imei);
//        request.addParams("appname",com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_REMCOMMEND_URL);
//        mVolley.doRequest(request);
//    }
//
//    /**
//     * 取消订阅
//     *
//     * @param token
//     * @param imei     订阅号id
//     * @param callback
//     */
//    public void doDeleteOrAddSuberItem(int id, String token, String imei,
//                                       String srpId, String type, String category, String interestid,
//                                       String keyword, IVolleyResponse callback) {
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.addParams("imei", imei);
//        request.addParams("keyword", keyword);
//        request.addParams("type", type);
//        request.addParams("category", category);
//        request.addParams("srpId", srpId);
//        request.addParams("id", interestid);
//        request.addParams("interestid", interestid);
//        request.addParams("opSource", ZSSdkUtil.TOPIC_SUBSCRIBE_MENU);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_UPDATE_URL);
//        mVolley.doRequest(request);
//    }
//
//    /**
//     * 订阅搜索
//     *
//     * @param token
//     * @param keyword
//     * @param callback
//     */
//    public void doSuberSearch(int id, String token, String keyword,
//                              IVolleyResponse callback) {
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.addParams("imei", DeviceInfo.getDeviceId());
//        request.addParams("vc", DeviceInfo.getAppVersion());
//        request.addParams("keyword", keyword);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_SERACH_URL);
//        request.setmMethod(CVolleyRequest.REQUEST_METHOD_GET);
//        mVolley.doRequest(request);
//    }
//
//    public void doPostSuberOrder(int id, String token, String vc, String imei,
//                                 String json, IVolleyResponse callback) {
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.addParams("imei", imei);
//        request.addParams("vc", vc);
//        request.addParams("subscribeIds", json);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_ORDER_URL);
//        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
//        mVolley.doRequest(request);
//    }
//
//    public void doReplay(int id, String token, IVolleyResponse callback) {
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.getApplyTips);
//        request.setmMethod(CVolleyRequest.REQUEST_METHOD_GET);
//        mVolley.doRequest(request);
//    }
//
//    public void doGetSwitch(int id,String token,IVolleyResponse callback){
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_GET_SWITCH_URL);
//        request.setmMethod(CVolleyRequest.REQUEST_METHOD_GET);
//        mVolley.doRequest(request);
//    }
//
//    public void doPostSwith(int id,String token, String subSwitch,IVolleyResponse callback){
//        CVolleyRequest request = new CVolleyRequest();
//        request.setmId(id);
//        request.addParams("token", token);
//        request.addParams("subSwitch",subSwitch);
//        request.setCallBack(callback);
//        request.setUrl(UrlConfig.SUBER_POST_SWITCH_URL);
//        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
//        mVolley.doRequest(request);
//    }
}
