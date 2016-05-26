package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.DownOrUpRequest;
import com.android.volley.Request;
import com.android.volley.RequestDownOrUpQueue;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

//import com.android.volley.Request.ILoadProcess;


/**
 *
 * Created by lvqiang on 15/5/5.
 */
public class CVolleyManager implements IHttpManager{

    public static final boolean NET_RECT_VOLLEY=true;
    public static final int MY_SOCKET_TIMEOUT_MS=8*1000;
    public static final int MY_SOCKET_TIMEOUT_MAX_MS=800*1000;

    protected Context mContext;
    protected RequestQueue mQueue;
    protected RequestDownOrUpQueue mDownloadQueue;
    protected String mTag;
    public CVolleyManager(Context context,String tag){
        mContext = context;
        mTag=tag;
        mQueue= Volley.newRequestQueue(mContext);
    }


    @Override
    public void doRequest(final CVolleyRequest request){
//        final CVolleyRequest request = request;

        int method = request.getmMethod();
        int requestMethod;
        if ( method==CVolleyRequest.REQUEST_METHOD_POST){
            requestMethod = Request.Method.POST;
        }else {
            requestMethod = Request.Method.GET;
        }
        CSYRequest<Object> jsObjRequest = new CSYRequest<Object>
                (requestMethod, request.getUrl(), request.getParams(), new Response.Listener<Object>() {

                    @Override
                    public void onResponse(Object response) {
                        IRequestCallBack process = request.getRequestProcess();
                        process.onCallBackSucess(request,response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        IRequestCallBack process = request.getRequestProcess();
                        process.onCallBackError(request, error);
                    }
                });

        // Add the request to the RequestQueue.
        int timeout = request.getmTimeOut()==0?MY_SOCKET_TIMEOUT_MS:request.getmTimeOut();
        float retry = request.getmRetryTimes()==0?DefaultRetryPolicy.DEFAULT_BACKOFF_MULT:request.getmRetryTimes();
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                0,
                retry));
        jsObjRequest.setTag(request.getTag());
        jsObjRequest.setDoParse(request.getParser());
        jsObjRequest.setCacheKey(request.getCacheKey());
        jsObjRequest.setHeaders(request.getHeader());
        jsObjRequest.setPreProcess(request.getPreProcess());
        jsObjRequest.setPostProcess(request.getPostProcess());
        jsObjRequest.setmRequest(request);
        jsObjRequest.setForceCache(request.isForceCache());
        if (request.isForceRefresh()){
            jsObjRequest.setShouldCache(false);
        }
        request.onStart();
        mQueue.add(jsObjRequest);
    }


    public void doDownload(final CVolleyRequest request){
        if (mDownloadQueue==null){
            mDownloadQueue = Volley.newDownloadQueue(mContext);
        }
//        final CVolleyRequest request = request;
        DownOrUpRequest jsObjRequest = new DownOrUpRequest
                (Request.Method.GET, request.getUrl(), new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        IRequestCallBack process = request.getRequestProcess();
                        process.onCallBackSucess(request, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        IRequestCallBack process = request.getRequestProcess();
                        process.onCallBackError(request, error);

                    }
                });
        jsObjRequest.setmLoadingProcess(new DownOrUpRequest.ILoadProcess() {

            @Override
            public void process(long max, long cur) {
                IVolleyLoadResponse load = request.getmLoading();
                if (load != null) {
                    load.onHttpProcess(max, cur);
                }
            }
        });
        int timeout = request.getmTimeOut()==0?MY_SOCKET_TIMEOUT_MS:request.getmTimeOut();
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        jsObjRequest.set
        // Add the request to the RequestQueue.
        jsObjRequest.setId(request.getmId());
        jsObjRequest.setDownloadPath(request.getDownloadFilePath());
        jsObjRequest.setmDownloadName(request.getDownloadFileName());
        jsObjRequest.setTag(request.getmId());
//        jsObjRequest.setTag(mTag);
        request.onStart();
        mDownloadQueue.add(jsObjRequest);
    }

    public void doUpload(CVolleyRequest request){

    }


    public int getCacheState(String key){
        int state = 0;
        if (mQueue == null){
            mQueue= Volley.newRequestQueue(mContext);
        }
        Cache cache = mQueue.getCache();
        if (cache == null){
            return IRequestCache.CACHE_STATE_NO_CACHE;
        }
        Cache.Entry enty=cache.get(key);
        if(enty==null){
            state = IRequestCache.CACHE_STATE_NO_CACHE;
        }else if(enty.isExpired()){
            state = IRequestCache.CACHE_STATE_IS_EXPIRE;
        }else {
            state = CRequestProcess.CACHE_STATE_HAVE;
        }
        return state;
    }

    public boolean isRunning(String url){
        if (mQueue.isRunning(url)){
            return true;
        }
        if(mDownloadQueue == null)
            return false;
        if (mDownloadQueue.isRunning(url)){
            return true;
        }
        return false;
    }

    public void cancelDownload(int id){
    	mDownloadQueue.cancelAll(id);
    }

    public void cancel(String tag){
        mQueue.cancelAll(tag);
    }

    public void removeCache(String key){
        mQueue.getCache().remove(key);
    }

    public void clearCache(){
        mQueue.getCache().clear();
        mQueue.getCache().initialize();
    }

    public void cancelAll(){
            mQueue.cancelAll(mTag);
        if (mDownloadQueue!=null) {
    	    mDownloadQueue.stop();
        }
        mQueue.stop();
    }
}
