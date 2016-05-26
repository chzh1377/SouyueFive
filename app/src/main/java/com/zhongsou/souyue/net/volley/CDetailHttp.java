package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.module.CWidgetHead;
import com.zhongsou.souyue.module.CWidgetSecondList;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.moduleparse.CMoudleParse;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by lvqiang on 15/5/5.
 */
public class CDetailHttp extends AHttp {
    public static final int HTTP_GET_COMMENT_COUNT = 123;
    public static final int HTTP_GET_COMMENT_LIST = 124;
    public static final int HTTP_GET_SECOND_LIST = 125;
    public static final int HTTP_GET_DETAIL_HEAD = 126;
    public static final int HTTP_GET_SET_HOT = 127;
    public static final int HTTP_COMMMENT_UP = 128; // 举报
    public static final int HTTP_COMPLAIN = 200; // 举报

    public static final int HTTP_REQUEST_DETAIL_MODULE=1006;
    public static final int HTTP_REQUEST_DETAIL_MODULE_CIRCLE=1009;
    public static final int HTTP_REQUEST_DETAIL_DATA=1007;
    public static final int HTTP_REQUEST_DETAIL_COMMENT=1008;

    public static final String HOST = "http://123.56.131.88/files/";

    public static final int DETAIL_COMMENT_LIST_COMMENT=0;
    public static final int DETAIL_COMMENT_LIST_HOT=1;
	private Context mContext;

	public CDetailHttp(Context _context) {
		super(_context, CDetailHttp.class.getName());
		mContext = _context;
	}

    public void doCommentCount(int id,String url,String opaflag,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("url", url);
        request.addParams("operflag",opaflag);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.setCallBack(callback);
        request.setUrl(UrlConfig.mCommentCount);
        mVolley.doRequest(request);
    }

    public void doCommentList(int id, String url, int operflag, long last_sort_num, String srpid, String srpword, int type,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("url", url);
        request.addParams("operflag", operflag+"");
        request.addParams("psize", 10 + "");
        request.addParams("last_sort_num", last_sort_num+"");
        request.addParams("srpword", srpword);
        request.addParams("srpid", srpid);
        request.addParams("type", type+"");
        request.addParams("appname","souyue");
        request.setParser(this);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.newGetCommentList);
        mVolley.doRequest(request);
    }

    public void doSecondList(int id, String _keyword, String _srpid, String _url, String _pushid, String _blog_id, String channel, IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("keyword", _keyword);
        request.addParams("srpid", _srpid);
        request.addParams("url", _url);
        request.addParams("push_id", _pushid);
        request.addParams("blog_id", String.valueOf(_blog_id));
        request.addParams("newsChannel", channel);
        request.setParser(this);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.mUrlWidgetSecondList);
        mVolley.doRequest(request);
    }

    public void doDetailHead(int id, String _keyword, String _srpid,int keyworccate, String _url, int _type,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("keyword", _keyword);
        request.addParams("srpid", _srpid);
        request.addParams("keywordCate", keyworccate+"");
        request.addParams("url", _url);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("keyword_type",_type+"");
        request.addParams("network", NetWorkUtils.getCurrentNetworkType(mContext));
        request.setParser(this);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.mUrlWidgetHead);
        mVolley.doRequest(request);
    }



    public void doSetHot(int id,String _url,long comment_id,int status,int operflag,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("comment_id", comment_id + "");
        request.addParams("status", status + "");
        request.addParams("url", _url);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("operflag", operflag + "");
        request.setCallBack(callback);
        request.setUrl(UrlConfig.sethot);
        mVolley.doRequest(request);
    }

    public void home_complaintint(int id,String token,int type,String sor_url,String srp_id,String keyword,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("type", type+"");
        request.addParams("url", sor_url);
        request.addParams("srp_id", srp_id);
        request.addParams("keyword", keyword);
        request.setForceRefresh(true);//必定强制访问网络
        request.setmMethod(BaseUrlRequest.REQUEST_METHOD_POST);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.homecomplaint);
        mVolley.doRequest(request);
    }

    public void commentUp(int id,String _keyword,String _srpid,String _url,String _token,int operflag,int type,long comment_id,String main_title,String main_images,String main_decsription,String main_date,String main_source,long blog_user_id,CommentsForCircleAndNews postsNew,IVolleyResponse callback) {
        CVolleyRequest m = new CVolleyRequest();
        m.setmId(id);
        m.addParams("srpword", _keyword);
        m.addParams("srpid", _srpid);
        m.addParams("url", _url);
        m.addParams("token", _token);
        m.addParams("operflag", operflag + "");
        m.addParams("type", type + "");
        m.addParams("comment_id", comment_id + "");
        m.addParams("main_title", main_title);
        m.addParams("main_images", main_images);
        m.addParams("main_decsription", main_decsription);
        m.addParams("main_date", main_date);
        m.addParams("main_source", main_source);
        m.addParams("blog_user_id", blog_user_id + "");
        m.addKeyValueTag("CommentsForCircleAndNews", postsNew);
        m.setCallBack(callback);
        m.setUrl(UrlConfig.mCommentUp);
        mVolley.doRequest(m);
    }

    public void doGetUrlContentTxt(int id,String _type,String _keyword,String _srpid,String _url,IVolleyResponse _callback){
        final CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("type",_type);
        request.addParams("userId", SYUserManager.getInstance().getUserId());
        request.addParams("keyword", _keyword);
        request.addParams("srpId", _srpid);
        request.addParams("url", _url);
        request.setCallBack(_callback);
        request.setUrl(UrlConfig.mUrlGetContent);
        request.setParser(this);
        mVolley.doRequest(request);
    }

    public void doGetUrlContentTxtCircle(int id,long blog_id,IVolleyResponse _callback){
        final CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("blog_id",blog_id + "");
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.setCallBack(_callback);
        request.setUrl(UrlConfig.mUrlGetContentCircle);
        request.setParser(this);
        mVolley.doRequest(request);
    }


    public void doGetDetailData(int _id,String _srpid,String _keyword,String _url,String _pushid,String _blog_id,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(_id);
        request.addParams("keyword", _keyword);
        request.addParams("srpid", _srpid);
        request.addParams("url", _url);
        request.addParams("push_id", _pushid);
        request.addParams("blog_id", String.valueOf(_blog_id));
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.setCallBack(callback);
        request.setUrl(UrlConfig.mUrlGetDetailData);
        mVolley.doRequest(request);
    }
/*
vc=4.2.2
srpid=3803769493722c6be706492f8c48e56e
srpword=liyq005兴趣圈关闭
token=b8eee334-a5a6-4439-8991-e0f996a499a2
psize=10
last_sort_num=0
imei=866568024205725
url=http://interest.zhongsou.com?sign_id=db26ba7e9de61cda9bd6d001945b1f3c
&blog_id=12063
&sign_info=12063
&srpid=3803769493722c6be706492f8c48e56e
&srpwordliyq005兴趣圈关闭
 */
    public void doGetDetailCommentData(int _id,String url, int operflag, long last_sort_num, String srpid, String srpword, int type,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(_id);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("url", url);
        request.addParams("operflag", operflag + "");
        request.addParams("psize", 10+"");
        request.addParams("last_sort_num", last_sort_num+"");
        request.addParams("srpword", srpword);
        request.addParams("srpid", srpid);
        request.addParams("type", type+"");
        request.addParams("appname","souyue");
        request.setCallBack(callback);
        request.setUrl(UrlConfig.mUrlGetCommentData);
        mVolley.doRequest(request);
    }

    /**
     * 在非ui线程中解析
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object doParse(CVolleyRequest request, String response) throws Exception {
        HttpJsonResponse res = (HttpJsonResponse)super.doParse(request,response);
        Object result = null;
//        HttpJsonResponse res = (HttpJsonResponse) json;
//        processPre(res);
        switch (request.getmId()){
//            case HTTP_REQUEST_DETAIL_MODULE:
//                result = CMoudleParse.getInstance().parserModule(res);
//                break;
//            case HTTP_REQUEST_DETAIL_MODULE_CIRCLE:
//                result = CMoudleParse.getInstance().parserModule(res);
//                break;
            case HTTP_GET_COMMENT_LIST:
                result = parseCommentList(res);
                break;
            case HTTP_GET_SECOND_LIST:
                result = parseSecondList(res);
                break;
            case HTTP_GET_DETAIL_HEAD:
                result = new Gson().fromJson(res.getBody(),
                        CWidgetHead.class);
                break;
            default:
                return super.doParse(request,response);
        }

        return result;
    }

    public Object parseCommentList(HttpJsonResponse res) {

        List<CommentsForCircleAndNews> listLatest = new Gson().fromJson(res
                        .getBody().getAsJsonArray("list"),
                new TypeToken<List<CommentsForCircleAndNews>>() {
                }.getType());
        List<CommentsForCircleAndNews> listHot = new Gson().fromJson(res
                        .getBody().getAsJsonArray("hotlist"),
                new TypeToken<List<CommentsForCircleAndNews>>() {
                }.getType());
        List<Object> result = new ArrayList<Object>();
        result.add(DETAIL_COMMENT_LIST_COMMENT,listLatest);
        result.add(DETAIL_COMMENT_LIST_HOT,listHot);
        return result;

    }

    public Object parseSecondList(HttpJsonResponse res) throws UnsupportedEncodingException {
        CWidgetSecondList detail = new Gson().fromJson(res.getBody(),
                CWidgetSecondList.class);
        JsonArray navObject = res.getHead().getAsJsonArray("nav");
        if (navObject != null) {
            detail.setNav((List<NavigationBar>) new Gson().fromJson(
                    navObject, new TypeToken<List<NavigationBar>>() {
                    }.getType()));
        }
        detail.setShowMenu(res.getHeadBoolean("menu"));
        return detail;
    }
}
