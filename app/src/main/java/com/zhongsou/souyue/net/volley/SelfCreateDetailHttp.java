package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by liuyh on 15-8-3.
 */
public class SelfCreateDetailHttp extends AHttp {
    public static final int HTTP_REQUEST_SHORT_URL = 2001;
    public static final int HTTP_REQUEST_NEWS_COUNT = 2002;
    public static final int HTTP_REQUEST_UP = 2003;
    public static final int HTTP_REQUEST_FAVOURITE_DELETE = 2004;
    public static final int HTTP_REQUEST_FAVOURITE_ADD = 2005;
    public static final int HTTP_REQUEST_FAVOURITE_USER_SHARE_POINT = 2006;
    public static final int HTTP_REQUEST_FAVOURITE_DEL_SELCREATE = 2007;

    public SelfCreateDetailHttp(Context _context) {
        super(_context, SelfCreateDetailHttp.class.getName());
    }

    public void shortURL(int id, String url,IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("method", "set");
        request.addParams("url", url);
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.shortURL);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }

    public void newsCount(int id, String token, String url,IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("url", url);
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.newsCount);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }

    public void up(int id, String token, String url, String title, String image, String description, long date, String source, String keyword, String srpId,IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("url", url);
        request.addParams("title", title);
        request.addParams("image", image);
        request.addParams("description", description);
        request.addParams("date", String.valueOf(date));
        request.addParams("source", source);
        request.addParams("keyword", keyword);
        request.addParams("srpId", srpId);
        request.addParams("imei",  DeviceInfo.getDeviceId());
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.up);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }

    public void favoriteDelete(int id, String token, String url,int dataType, long blogId,IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("url", url);
        if(dataType == 2) { //如果不是2,就不用传dataType
            request.addParams("dataType", dataType + "");
        }
        if(blogId != 0l) {//如果是0l,就不用传blogId
            request.addParams("newsId", blogId + "");
        }
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.cancelCollect);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }

    public void favoriteAdd(int id, String token, String url, String title, String image, String description, long date, String source, String keyword, String srpId,IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("url", url);
        request.addParams("title", title);
        request.addParams("image", image);
        request.addParams("description", description);
        request.addParams("date", String.valueOf(date));
        request.addParams("source", source);
        request.addParams("keyword", keyword);
        request.addParams("srpId", srpId);
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.favoriteAdd);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }

    /**
     * 用户积分分享
     * @return
     */
    public void userSharePoint(int id, SharePointInfo info,IVolleyResponse callBack){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
//        m.put("token",SYUserManager.getInstance().getUser().token());
        //token动态生成造成发送不准确，解决使用新方案优先判断userid，没有userid参数或者userid=0时，去读token参数。
        long userid = SYUserManager.getInstance().getUser().userId();
        if(userid != 0) {
            request.addParams("userid",SYUserManager.getInstance().getUser().userId() + "");
        }else {
            request.addParams("token",SYUserManager.getInstance().getUser().token());
        }
        request.addParams("url",info.getUrl());
        request.addParams("srp",info.getKeyWord());
        request.addParams("srpid",info.getSrpId());
        request.addParams("platform", info.getPlatform());
        request.addParams("os", DeviceInfo.osName);
        request.addParams("model",DeviceInfo.deviceName);
        request.addParams("useragent", "");
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.share_result);
        request.setCallBack(callBack);
        mVolley.doRequest(request);
    }

    public void delSelfCreate(int requstId , String token, String id,IVolleyResponse callBack) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(requstId);
        request.addParams("token", token);
        request.addParams("id", id);
        request.setForceRefresh(true);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.selfCreateDel);
        request.setCallBack(callBack);
        mVolley.doRequest(request);
    }
}
