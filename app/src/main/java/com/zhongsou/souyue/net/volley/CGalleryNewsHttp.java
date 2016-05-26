package com.zhongsou.souyue.net.volley;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.module.CWidgetSecondList;
import com.zhongsou.souyue.module.GalleryNewsList;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * create by zyw
 * me@raw.so
 * 图集页面的请求类
 */
public class CGalleryNewsHttp extends AHttp {

    /**
     * 常量字段
     */
    public static final int DEVICE_COME_FROM = 3;// 来自搜悦客户端
    public static final int UP_TYPE_MAIN = 1; // 对主贴点赞
    public static final int UP_TYPE_SUB = 2; // 对主跟帖点赞

    /**
     * 特有接口
     * start with 8900x;
     */
    public static final int GALLERY_HOMELIST = 89001; // 首页的list
    public static final int GALLERY_SHORTURL = 89002; // 短地址
    public static final int GALLERY_SECONDLIST = 89003; // 再次获取数据的接口
    /**
     * 顶帖相关
     * start with 8910x;
     */
    //顶帖
    public static final int GALLERY_DING_COMMENT = 89101;
    //踩帖
    public static final int GALLERY_DOWN_COMMENT = 89102;
    //获取顶帖个数
    public static final int GALLERY_POST_GET_DING_COUNT = 89103;

    /**
     * 收藏相关
     * start with 8920x;
     */
    //增加收藏
    public static final int GALLERY_COLLECT_ADD = 89201;
    //取消收藏
    public static final int GALLERY_COLLECT_DEL = 89202;


    /**
     * 转发相关
     * start with 8930x
     */
    //分享到网友推荐
    public static final int GALLERY_SHARETO_WANGYOUTUIJIAN = 89301;

    /**
     * 评论相关
     * start with 8940x
     */
    public static final int GALLERY_GET_COMMENTLIST = 89401;

    //debug constants
    public static final String DEBUG_GALLERTNEWS_HOME = "http://sycms.zhongsou.com/pics.html";


    private Context mContext;
    private Gson mGson;
    private HashMap<Integer, Boolean> mRunningTask; // 正在运行的实例，保证每次只有一个相同的方法请求,防止回调污染

    public CGalleryNewsHttp(Context _context) {
        super(_context, CGalleryNewsHttp.class.getName());
        mContext = _context;
        mGson = new Gson();
        mRunningTask = new HashMap<Integer, Boolean>();
    }

    /**
     * 获取首页的图集列表，demo：
     * "http://sycms.zhongsou.com/pics.html"
     */
    public void doGetMainList(String url, String srpId, String keyword, String clickfrom, String mid, String pushfrom, IVolleyResponse callback) {
        //保证只有当前只有一个正在请求.
        if (!isRunning(GALLERY_HOMELIST)) {
            setRunning(GALLERY_HOMELIST);
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_HOMELIST);
            request.addParams("clickfrom", clickfrom);
            request.addParams("mid", mid);
            request.addParams("pushfrom", pushfrom);
            request.addParams("url", url);
            request.addParams("keyword", keyword);
            request.addParams("srpId", srpId);
            request.setCallBack(callback);
            request.setUrl(UrlConfig.gallerynewsHome);
            request.setCacheKey(url);
            request.setParser(this);
            request.setForceRefresh(true);
            mVolley.doRequest(request);
        }
    }

    /**
     * 顶帖
     *
     * @param _keyword
     * @param _srpid
     * @param _url
     * @param type
     * @param comment_id
     * @param main_title
     * @param main_images
     * @param main_decsription
     * @param main_date
     * @param main_source
     * @param blog_user_id
     * @param callback
     */
    public void doCommentUp(String _keyword, String _srpid, String _url, int type, long comment_id,
                            String main_title, String main_images, String main_decsription,
                            String main_date, String main_source, long blog_user_id, IVolleyResponse callback) {
        if (!isRunning(GALLERY_DING_COMMENT)) {
            setRunning(GALLERY_DING_COMMENT);
            Log.e("gallerynews", "setRunning...GALLERY_DING_COMMENT");
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_DING_COMMENT);
            request.addParams("srpword", _keyword);
            request.addParams("srpid", _srpid);
            request.addParams("url", _url);
            request.addParams("token", getToken());
            request.addParams("operflag", DEVICE_COME_FROM + "");
            request.addParams("type", type + "");
            request.addParams("comment_id", comment_id + "");
            request.addParams("main_title", main_title);
            request.addParams("main_images", main_images);
            request.addParams("main_decsription", main_decsription);
            request.addParams("main_date", main_date);
            request.addParams("main_source", main_source);
            request.addParams("blog_user_id", blog_user_id + "");
            request.setUrl(UrlConfig.mCommentUp);
            request.setCallBack(callback);
            request.setParser(this);
            request.setForceRefresh(true);
            mVolley.doRequest(request);
        }

    }

    /**
     * 踩帖
     *
     * @param _keyword
     * @param _srpid
     * @param _url
     * @param type
     * @param main_title
     * @param main_images
     * @param main_decsription
     * @param main_date
     * @param main_source
     * @param callback
     */
    public void doCommentDown(String _keyword, String _srpid, String _url, int type, String main_title,
                              String main_images, String main_decsription, String main_date, String main_source, IVolleyResponse callback) {

        if (!isRunning(GALLERY_DOWN_COMMENT)) {
            setRunning(GALLERY_DOWN_COMMENT);

            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_DOWN_COMMENT);
            request.addParams("srpword", _keyword);
            request.addParams("srpid", _srpid);
            request.addParams("url", _url);
            request.addParams("token", getToken());
            request.addParams("operflag", DEVICE_COME_FROM + "");
            request.addParams("type", type + "");
            request.addParams("main_title", main_title);
            request.addParams("main_images", main_images);
            request.addParams("main_decsription", main_decsription);
            request.addParams("main_date", main_date);
            request.addParams("main_source", main_source);
            request.setUrl(UrlConfig.mCommentDown);
            request.setForceRefresh(true);
            request.setCallBack(callback);
            mVolley.doRequest(request);
        }

    }


    //收藏相关...

    /**
     * 加入收藏
     *
     * @param _dataType
     * @param _url
     * @param mSrpId
     * @param mKeyword
     * @param mTitle
     * @param imageUrl
     * @param callback
     */
    public void doCollectPost(String _dataType, String _url, String mSrpId, String mKeyword, String mTitle, String imageUrl, IVolleyResponse callback) {
        if (!isRunning(GALLERY_COLLECT_ADD)) {
            setRunning(GALLERY_COLLECT_ADD);
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_COLLECT_ADD);
            request.addParams("data_type", _dataType);
            request.addParams("url", _url);
            request.addParams("token", getToken());
            request.addParams("operflag", DEVICE_COME_FROM + "");
            request.addParams("srpid", mSrpId);
            request.addParams("keyword", mKeyword);
            request.addParams("title", mTitle);
            request.addParams("image", imageUrl);
            request.setForceRefresh(true);
            request.setUrl(UrlConfig.mFavoriteAdd);
            request.setCallBack(callback);
            mVolley.doRequest(request);
        }
    }

    /**
     * 取消收藏
     *
     * @param _url
     * @param dataType
     */
    public void doCollectPostDel(String _url, int dataType, IVolleyResponse callback) {
        if (!isRunning(GALLERY_COLLECT_DEL)) {
            setRunning(GALLERY_COLLECT_DEL);
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_COLLECT_DEL);
            request.addParams("url", _url);
            request.addParams("dataType", dataType + "");
            request.addParams("token", getToken());
            request.addParams("operflag", DEVICE_COME_FROM + "");
            request.setUrl(UrlConfig.cancelCollect);
            request.setCallBack(callback);
            request.setForceRefresh(true);
            mVolley.doRequest(request);
        }
    }
//
//    /**
//     * 分享到网友推荐 -1
//     *
//     * @param newsId
//     * @param callback
//     */
//    public void shareToWangYouTuiJian(Long newsId, IVolleyResponse callback) {
//        if (!isRunning(GALLERY_SHARETO_WANGYOUTUIJIAN)) {
//            setRunning(GALLERY_SHARETO_WANGYOUTUIJIAN);
//            CVolleyRequest request = new CVolleyRequest();
//            request.setmId(GALLERY_SHARETO_WANGYOUTUIJIAN);
//            request.addParams("token", getToken());
//            request.addParams("newsId", String.valueOf(newsId));
//            request.setUrl(UrlConfig.share);
//            request.setCallBack(callback);
//            request.setForceRefresh(true);
//            mVolley.doRequest(request);
//        }
//    }
//
//    /**
//     * 分享到网友推荐 -2
//     *
//     * @param url
//     * @param title
//     * @param image
//     * @param description
//     * @param date
//     * @param source
//     * @param keyword
//     * @param srpId
//     * @param callback
//     */
//    public void shareToWangYouTuiJian(String url, String title, String image, String description, String date, String source, String keyword, String srpId, IVolleyResponse callback) {
//        if (!isRunning(GALLERY_SHARETO_WANGYOUTUIJIAN)) {
//            setRunning(GALLERY_SHARETO_WANGYOUTUIJIAN);
//            CVolleyRequest request = new CVolleyRequest();
//            request.setmId(GALLERY_SHARETO_WANGYOUTUIJIAN);
//            request.addParams("token", getToken());
//            request.addParams("url", url);
//            request.addParams("title", title);
//            request.addParams("image", image);
//            request.addParams("description", description);
//            request.addParams("date", date);
//            request.addParams("source", source);
//            request.addParams("keyword", keyword);
//            request.addParams("srpId", srpId);
//            request.setUrl(UrlConfig.share);
//            request.setCallBack(callback);
//            request.setForceRefresh(true);
//            mVolley.doRequest(request);
//        }
//    }


    /**
     * 获取顶帖，回复，是否收藏
     *
     * @param url
     * @param opaflag
     * @param callback
     */
    public void doCommentCount(String url, String opaflag, IVolleyResponse callback) {
        if (!isRunning(GALLERY_POST_GET_DING_COUNT)) {
            setRunning(GALLERY_POST_GET_DING_COUNT);
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_POST_GET_DING_COUNT);
            request.addParams("url", url);
            request.addParams("operflag", opaflag);
            request.addParams("token", SYUserManager.getInstance().getToken());
            request.setCallBack(callback);
            request.setUrl(UrlConfig.mCommentCount);
            mVolley.doRequest(request);
        }
    }

    /**
     * 获取评论列表
     *
     * @param url
     * @param operflag
     * @param last_sort_num
     * @param srpid
     * @param srpword
     * @param type
     * @param callback
     */
    public void doCommentList(String url, int operflag, long last_sort_num, String srpid, String srpword, int type, IVolleyResponse callback) {
        if (!isRunning(GALLERY_GET_COMMENTLIST)) {
            setRunning(GALLERY_GET_COMMENTLIST);
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(GALLERY_GET_COMMENTLIST);
            request.addParams("token", SYUserManager.getInstance().getToken());
            request.addParams("url", url);
            request.addParams("operflag", operflag + "");
            request.addParams("psize", 10 + "");
            request.addParams("last_sort_num", last_sort_num + "");
            request.addParams("srpword", srpword);
            request.addParams("srpid", srpid);
            request.addParams("type", type + "");
            request.addParams("appname", "souyue");
            request.setParser(this);
            request.setCallBack(callback);
            request.setUrl(UrlConfig.newGetCommentList);
            mVolley.doRequest(request);
        }
    }


    /**
     * 在非ui线程中解析
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object doParse(CVolleyRequest request, String response) throws Exception {
        if (response == null) {
            throw new Exception("response is null");
        }
        Object result = null;
        switch (request.getmId()) {
            case GALLERY_HOMELIST: // 进来之后，获取图集列表和推荐列表
                result = parseHomeList(request.getCacheKey(), (HttpJsonResponse) super.doParse(request, response));
                break;
            case GALLERY_SHORTURL: // 分享用的短地址生成
                return parseShortUrl((HttpJsonResponse) super.doParse(request, response));
            case GALLERY_DING_COMMENT: // 顶帖
                return super.doParse(request, response);
            case GALLERY_SECONDLIST:
                return parseSecondList((HttpJsonResponse) super.doParse(request, response));
            case GALLERY_GET_COMMENTLIST:
                return parseCommentList((HttpJsonResponse) super.doParse(request, response));
            case CDetailHttp.HTTP_GET_DETAIL_HEAD:
            case GALLERY_POST_GET_DING_COUNT:
            default:
                return response;
        }
        return result;
    }

    public static final int DETAIL_COMMENT_LIST_COMMENT = 0; // 评论列表中的帖子标记
    public static final int DETAIL_COMMENT_LIST_HOT = 1; // 热门标记

    /**
     * 解析评论列表
     *
     * @param res
     * @return
     */
    public Object parseCommentList(HttpJsonResponse res) {
        List<CommentsForCircleAndNews> listLatest = mGson.fromJson(res
                        .getBody().getAsJsonArray("list"),
                new TypeToken<List<CommentsForCircleAndNews>>() {
                }.getType());
        List<CommentsForCircleAndNews> listHot = mGson.fromJson(res
                        .getBody().getAsJsonArray("hotlist"),
                new TypeToken<List<CommentsForCircleAndNews>>() {
                }.getType());
        List<Object> result = new ArrayList<Object>();
        result.add(DETAIL_COMMENT_LIST_COMMENT, listLatest);
        result.add(DETAIL_COMMENT_LIST_HOT, listHot);
        return result;

    }

    /**
     * 解析二级导航
     *
     * @param res
     * @return
     * @throws UnsupportedEncodingException
     */
    public Object parseSecondList(HttpJsonResponse res) throws UnsupportedEncodingException {
        CWidgetSecondList detail = mGson.fromJson(res.getBody(),
                CWidgetSecondList.class);
        JsonArray navObject = res.getHead().getAsJsonArray("nav");
        if (navObject != null) {
            detail.setNav((List<NavigationBar>) new Gson().fromJson(
                    navObject, new TypeToken<List<NavigationBar>>() {
                    }.getType()));
        }

        return detail;
    }

    /**
     * 解析ShortUrl 返回的数据
     *
     * @param response
     * @return
     */
    private String parseShortUrl(HttpJsonResponse response) {
        return response.getBodyString();
    }

    /**
     * 解析获取首页内容返回的数据
     *
     * @param res
     * @return
     */
    private GalleryNewsList parseHomeList(final String cacheKey, final HttpJsonResponse res) {
        JsonObject body = res.getBody();
        final String s = body.toString();
        GalleryNewsList o = mGson.fromJson(body, new TypeToken<GalleryNewsList>() {
        }.getType());
        JsonObject data = body.getAsJsonObject("data");
        o.setKeyword(data.get("keyword").getAsString());
        o.setNewstime(data.get("newstime").getAsString());
        o.setSource(data.get("source").getAsString());
        o.setSrpid(data.get("srpid").getAsString());
        o.setTitle(data.get("title").getAsString());
        new Thread() {
            @Override
            public void run() {
                setCache(cacheKey, s);
            }
        }.start();
        return o;
    }

    public GalleryNewsList parseHomeListCache(String json) {
        return mGson.fromJson(json, new TypeToken<GalleryNewsList>() {
        }.getType());
    }

    /**
     * 获取token 太长了，懒得写。
     *
     * @return
     */
    private String getToken() {
        return SYUserManager.getInstance().getToken();
    }

    /**
     * 判断任务的执行状态 ,防止返回值被污染。共需要三个方法
     * 共同作用。
     * setFinished()
     * setRunning();
     * isRunning()
     * @param taskId
     * @return
     */
    public boolean isRunning(int taskId) {
        if (mRunningTask.containsKey(taskId))
            return mRunningTask.get(taskId);
        mRunningTask.put(taskId, false);
        return false;
    }

    /**
     * 如果当前的任务正在执行，就完成任务，否则就无法完成任务
     *
     * @param taskId
     * @return -> 状态是否设置成功
     */
    public boolean setFinished(int taskId) {
        switch (taskId) {
            //这几个改造后的接口，不能走这里
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
            case HttpCommon.CIRLCE_SHORT_URL_ID:
            case HttpCommon.SHARE_TO_PLATOM:
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                return true;
        }
        if (isRunning(taskId)) { //如果正在运行
            mRunningTask.put(taskId, false);
            return true; // 返回真
        }
        return false; //
    }

    public void setRunning(int taskId) {
        switch (taskId) {
            //这几个改造后的接口，不能走这里
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
            case HttpCommon.CIRLCE_SHORT_URL_ID:
            case HttpCommon.SHARE_TO_PLATOM:
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                return;
        }
        mRunningTask.put(taskId, true);
    }

    /**
     * 离线缓存
     *
     * @param key
     * @param value
     */
    public void setCache(String key, String value) {
        CommSharePreference.getInstance().putValue(0, key, value);
    }

    /**
     * 离线缓存
     *
     * @param key
     */
    public String getCache(String key) {
        return CommSharePreference.getInstance().getValue(0, key, "");
    }
}
