package com.zhongsou.souyue.net.volley;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.Map;

/**
 *
 * Created by lvqiang on 15/5/27.
 */
public abstract class AHttp implements IParser{
    public static final int MESSAGE_PROCESS = 0;


    public static final String HOST = "http://blog.csdn.net/fbysss/article/details/5437157";
    protected IRequestProcess mVolley;
    protected IRequestCache mVolleyCache;
    protected Context mContext;
    protected final String TAG;


    public AHttp(Context _context,String tag){
        TAG = tag;
        mContext = _context;
        CRequestProcess pro=  new CRequestProcess(_context,TAG);
        mVolley = pro;
        mVolleyCache = pro;
    }

    public boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected();
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }

    public boolean isWifi(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected()
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }

    public void cancelAll(){
        mVolley.onDestory();
    }

//    public HttpJsonResponse getCache(String key){
//        return (HttpJsonResponse) mVolley.getCache(key, this);
//    }

    public void cancelDownload(int i) {
        mVolley.cancelDownload(i);
    }

    public void cancel(Object tag){
        mVolley.cancel(tag.getClass().getName());
    }

    public boolean isRunning(String url){
        return mVolley.isRunning(url);
    }

    public String getKey(String url,Map<String,String> params){

        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if(!url.endsWith("?")){
            builder.append("&");
        }

        for(Map.Entry<String,String> entity:params.entrySet()){
            builder.append(entity.getKey());
            builder.append("=");
            builder.append(entity.getValue());
            builder.append("&");
        }
        return builder.toString();
    }

    public int getCacheState(String key){
        return mVolleyCache.getCacheState(key);
    }

    public void clearCache(){
        mVolleyCache.clearCache();
    }

    public void removeCache(String key){
        mVolleyCache.deleteCache(key);
    }

    @Override
    public Object doParse(CVolleyRequest request,String _response) throws Exception {
        if(_response==null){
            throw new Exception("response is null");
        }
        HttpJsonResponse res = new HttpJsonResponse( (JsonObject) new JsonParser().parse(_response));
        ((CRequestProcess)mVolley).sendAuthMessage(res);//验证用户相关
        CSouyueHttpError error = new CSouyueHttpError(res);
        if (error.isError()){
            throw  error;
        }
        return res;
    }
}
