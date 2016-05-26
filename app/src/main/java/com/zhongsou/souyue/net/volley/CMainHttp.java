package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by lvqiang on 15/5/27.
 */
public class CMainHttp extends AHttp {

    public static final int HOME_LIST_RESULT=0;
    public static final int HOME_LIST_BODY=1;
    public static final int HOME_LIST_HASMORE=2;



    public static final int HTTP_REQUEST_HOMEBALL=1001;
    public static final int HTTP_REQUEST_HOMELIST=1002;
    public static final int HTTP_REQUEST_HOMELIST_PULL=1003;
    public static final int HTTP_REQUEST_HOMELIST_PUSH=1004;
    public static final int HTTP_REQUEST_HOMELIST_DISLIKE=1005;
    public static final int HTTP_REQUEST_HOMELIST_DELSUB=1008;

    public static final int HTTP_GET_GET_DOWNLOAD_TEST = 1007;

    public static CMainHttp mMainHttp;

    public static CMainHttp getInstance(){
        if(mMainHttp == null){
            throw new IllegalStateException("Must create "+CMainHttp.class.getName()+" when application is lunching!");
        }
        return mMainHttp;
    }
    public CMainHttp(Context _context) {
        super(_context, CMainHttp.class.getName());
        mMainHttp = this;
    }

//    //兼容aquary
//    public void add(CVolleyRequest request) {
//        mVolley.doRequest(request);
//    }

    public void doRequest(BaseUrlRequest request){
        mVolley.doRequest(request);
    }

    @Override
    public void cancelAll() {
        throw new IllegalStateException(this.getClass().getName()+" con't support!");
    }

    public void getHomeBallContentList(int _id,String _type,long id,String _keyword,String _srpId,
                                       String lastId,String _indexId, boolean refresh,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(_id);
        request.addKeyValueTag("type",_type);
        request.addParams("type", _type);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("keyword", _keyword);
        request.addParams("srpId", _srpId);
        request.addParams("lastId", lastId);
        request.addParams("indexId", _indexId);
        request.addKeyValueTag("id", id);
        request.setCallBack(callback);
        request.setForceRefresh(refresh);
        request.setParser(this);
        request.setUrl(UrlConfig.GET_SHOW_HOME_LIST);
        //这里需要一点不同的缓存策略，如果是专题页(HomeBallBean.SPECIAL_TOPIC) 的话，
        // 缓存策略不按超时算（Utils.isTimeExpired(String tag)。按照http的缓存策略发出请求.
        //仅对下拉刷新作出缓存
//        if (HomeBallBean.SPECIAL_TOPIE.equals(_type) &&  _id == CMainHttp.HTTP_REQUEST_HOMELIST_PULL) {
//            request.setCacheKey(_type);
////            Log.e(this.getClass().getSimpleName(),"《《《《《《《《《《专题页面开始缓存《《《《《《《《《");
//        }
//        注掉上面逻辑，因为这里的cachekey不应该自己设定，牵扯到首页initcache方法中的刚开始进入这个页面时需要用到缓存
//        而不管专题还是历史记录都应该走首页缓存逻辑，不应该在这里的更下一层重新设置cachekey
        mVolley.doRequest(request);
    }


    public void getHomeBallList(int _id,String _token,int _type,boolean _refresh,boolean _forceCache,IVolleyResponse _callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(_id);
        request.addParams("token", _token);
        request.addParams("type", _type+"");
        request.addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        request.addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        request.setParser(this);
        request.setForceCache(_forceCache);
        request.addKeyValueTag("forceCache",_forceCache);
        request.setCallBack(_callback);
        request.setForceRefresh(_refresh);
        request.setUrl(UrlConfig.GET_SHOW_HOME_BALL);
        mVolley.doRequest(request);
    }
//    token--
//    url--新闻url
//    blog_id--兴趣圈ID或原创ID（新闻传0）
//    interest_id
/*    public void doDisLike(int _id,String _token,String _url,String _blog_id,String _interest_id,String dis,IVolleyResponse _callback,Object _obj,String channel,String type){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(_id);
        request.addParams("token", _token);
        request.addParams("url", _url);
        request.addParams("blog_id", _blog_id);
        request.addParams("interest_id", _interest_id);
        request.addParams("disLike", dis);
        request.addParams("channel", channel);
        request.addParams("type", type);
        request.addKeyValueTag(HOME_PAGE_DISLIKE, _obj);
        request.setCallBack(_callback);
        request.setForceRefresh(true);//必定强制访问网络
        request.setUrl(UrlConfig.GET_SHOW_HOME_DISLIKE);
        mVolley.doRequest(request);
    }*/


    public void getUserBg(int _id,String _token,IVolleyResponse _callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(_id);
        request.addParams("token", _token);
        request.setCallBack(_callback);
        request.setForceRefresh(true);//必定强制访问网络
        request.setUrl(UrlConfig.GET_USER_BG);
        mVolley.doRequest(request);
    }

    /**
     * 取消订阅
     *
     * @param token
     * @param callback
     */
    public void doDeleteSuberItem(int id, String token, HomeBallBean bean, IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addKeyValueTag("homeball",bean);
        request.addParams("token", token);
        request.addParams("keyword", bean.getKeyword());
        request.addParams("type", "del");
        request.addParams("category", bean.getCategory());
        request.addParams("srpId", bean.getSrpId());
        request.addParams("id", String.valueOf(bean.getId()));
        request.addParams("interestid", String.valueOf(bean.getId()));
        request.setCallBack(callback);
        request.setUrl(UrlConfig.SUBER_UPDATE_URL);
        mVolley.doRequest(request);
    }

    @Override
    public Object doParse(CVolleyRequest request, String _response) throws Exception {
        Object obj = super.doParse(request, _response);
        int id = request.getmId();
        HttpJsonResponse res = (HttpJsonResponse) obj;
//        processPre(res);
        switch (id){
            case HTTP_REQUEST_HOMELIST_PULL:
            case HTTP_REQUEST_HOMELIST_PUSH:
            case HTTP_REQUEST_HOMELIST:
                List<Object> objs = new ArrayList<Object>();
                SearchResult result = new SearchResult(res);
                objs.add(HOME_LIST_RESULT, result);
                objs.add(HOME_LIST_BODY,res.getBodyArray());
                objs.add(HOME_LIST_HASMORE,res.getHeadBoolean("hasMore"));
                return objs;
            case HTTP_REQUEST_HOMEBALL:
                ArrayList<HomeBallBean> bs = new Gson().fromJson(
                        res.getBodyArray(),
                        new TypeToken<ArrayList<HomeBallBean>>() {
                        }.getType());
                return bs;
        }
        return res;
    }

    /**
     * 是否展示系统推荐数据
     */
    public void isRecommendSpecial(int id,String state,IVolleyResponse _callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("state", state);
        request.setCallBack(_callback);
        request.setForceRefresh(true);//必定强制访问网络
        request.setUrl(UrlConfig.GET_USER_RECOMMEND);
        mVolley.doRequest(request);
    }

/*    public void getSplashImage(int id,IVolleyResponse callback){
    	CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("machineType", "android");
        request.addParams("appName", DeviceInfo.appName);
        request.addParams("clientTime", System.currentTimeMillis()+"");
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("rwidth", String.valueOf(Utils.getScreenWidth()));
        request.addParams("rheight",String.valueOf(Utils.getScreenHeight()));
        request.addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        request.setCallBack(callback);
        request.setUrl(UrlConfig.getSplashImage);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        mVolley.doRequest(request);
    }*/

    public void doDownload(int id,String downpath,String url,IVolleyLoadResponse loading,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.setDownloadFilePath(downpath);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_DOWNLOAD);
        request.setUrl(url);
        request.setmLoading(loading);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }
    public void doDownloadRename(int id,String downpath,String fileName,String url,IVolleyLoadResponse loading,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.setDownloadFilePath(downpath);
        request.setDownloadFileName(fileName);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_DOWNLOAD);
        request.setUrl(url);
        request.setmLoading(loading);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }
}
