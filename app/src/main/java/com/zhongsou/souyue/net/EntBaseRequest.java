package com.zhongsou.souyue.net;

import android.util.Base64;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by zyw on 2015/12/28.
 * 原来ent包下面的请求基类
 */
public abstract class EntBaseRequest extends BaseUrlRequest {

    protected IVolleyResponse mResponse;

    public EntBaseRequest(int id, IVolleyResponse response) {
        super(id, response);
        mResponse = response;
    }

    @Override
    public boolean isForceRefresh() {
        return true;
    }

    /**
     * host神马的就先放到这里了...
     */
    private static final String HTTP_DOMAIN = getHTTP_DOMAIN();
    public static final String IMAGE_DOMAIN = getIMAGE_DOMAIN();
    public static final String API_URL = HTTP_DOMAIN + "/ent/rest";

    /**
     * 获取图片地址，貌似就评论者里用了。
     *
     * @param url
     * @return
     */
    public static String getImageUrl(String url) {
        if (url != null && !url.startsWith("http://")) {
            return IMAGE_DOMAIN + url;
        }
        return url;
    }

    private static String getHTTP_DOMAIN() {
        switch (UrlConfig.SOUYUE_SERVICE) {
            case UrlConfig.SOUYUE_TEST:
                return "http://61.135.210.177:8280";
            case UrlConfig.SOUYUE_PRE_ONLINE:
                return "http://61.135.210.178:8280";
            case UrlConfig.SOUYUE_ONLINE:
                return "http://sye.zhongsou.com";
            default:
                return "http://sye.zhongsou.com";
        }
    }

    private static String getIMAGE_DOMAIN() {
        switch (UrlConfig.SOUYUE_SERVICE) {
            case UrlConfig.SOUYUE_TEST:
                return "http://61.135.210.177";
            case UrlConfig.SOUYUE_PRE_ONLINE:
                return "http://61.135.210.178";
            case UrlConfig.SOUYUE_ONLINE:
                return "http://sye.img.zhongsou.com";
            default:
                return "http://sye.img.zhongsou.com";
        }
    }

    /**
     * 编码请求参数
     *
     * @param params
     * @return
     */
    protected static String encodeParams(Map<String, Object> params) {
        String param = "{}";
        if (params != null) {
            JSONObject json = new JSONObject();
            try {
                for (String key : params.keySet()) {
                    json.put(key, params.get(key));
                }
                Log.i("ent_net_params", json.toString());
                param = Base64.encodeToString(json.toString().getBytes("UTF-8"), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return param;
    }

}
