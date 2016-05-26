package com.zhongsou.souyue.net.share;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zyw on 2015/12/12.
 * 分享统计，没有做回调，不知道什么鬼。
 * 参数
 * type
 * taget
 * uid
 * url
 * client = android
 * 方法
 * post
 * <p/>
 * （分享的目的地，为以下值其一）souyue | sinaweibo | renren | weixin |
 * tenxunweibo | mail | sms
 */
public class SharePvRequest extends BaseUrlRequest {

    public SharePvRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "pv/pv.groovy";// 分享统计
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    public void setParams(String taget, String url) {
        String uid = SYUserManager.getInstance().getUserId();
        addParams("type", "share");
        addParams("taget", taget);
        addParams("uid", uid + "");
        addParams("url", url + "");
        addParams("client", "android");
    }


    /**
     * 直接发请求
     *
     * @param target
     * @param url
     * @param response
     */
    public static void send(String target, String url, IVolleyResponse response) {
        CMainHttp http = CMainHttp.getInstance();
        SharePvRequest sharePv = new SharePvRequest(HttpCommon.SHARE_BASE_REQUEST, response);
        sharePv.setParams(target, url);
        http.doRequest(sharePv);
    }

    public static final IVolleyResponse EMPTY_RESPONSE = null;
}
