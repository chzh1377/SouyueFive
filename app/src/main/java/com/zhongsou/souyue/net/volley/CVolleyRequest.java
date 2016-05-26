package com.zhongsou.souyue.net.volley;

import com.android.volley.Request;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.utils.TelephonyInfo;
import com.zhongsou.souyue.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lvqiang on 15/5/5.
 */
public class CVolleyRequest implements IRequest{
    public static final int REQUEST_METHOD_GET=0;
    public static final int REQUEST_METHOD_POST=1;
    public static final int REQUEST_METHOD_DOWNLOAD=2;
    public static final int REQUEST_METHOD_UPLOAD=3;

    public static final boolean ENCRYPTABLE=true;



    private HashMap<String,Object> mTagMap=new HashMap<String, Object>();
    private Map<String, String> mRequestMap=new HashMap<String, String>();
    private Map<String, String> mHeader=new HashMap<String, String>();
    private IVolleyResponse mVolleyResponse;
    private IVolleyLoadResponse mLoading;
    private IRequestCallBack mRequestProcess;
    private Request.IProcess mPreProcess;
    private Request.IProcess mPostProcess;
    private IParser mParser;
    private BaseUrlRequest mUrlRequest;


    private Object mResponse;
    private IHttpError mVolleyError;
    private String mCacheKey;
    private String mUrl;
    private String mDownloadFilePath;
    private String mTag;
    private String mFile;
    private int mMethod;
    private boolean mForceRefresh;
    private boolean mForceCache;
    private boolean mIsFromAquary;
    private int mId;
    private int mRetryTimes;
    private int mTimeOut;

    public int getmId() {
        if (mUrlRequest != null){
            return mUrlRequest.getmId();
        }
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmMethod() {
        if (mUrlRequest != null){
            return mUrlRequest.getMethod();
        }
        return mMethod;
    }

    public void setmMethod(int mMethod) {
        this.mMethod = mMethod;
    }

    public int getmTimeOut() {
        if (mUrlRequest != null){
            return mUrlRequest.getTimeOut();
        }
        return mTimeOut;
    }

    public void setmTimeOut(int mTimeOut) {
        this.mTimeOut = mTimeOut;
    }

    public void setUrl(String _url){
        mUrl = _url;
    }

    public String getUrl(){
        if (mUrlRequest != null){
            return mUrlRequest.getUrl();
        }
        return mUrl;
    }

    public boolean ismIsFromAquary() {
        return mIsFromAquary;
    }

    public void setmIsFromAquary(boolean mIsFromAquary) {
        this.mIsFromAquary = mIsFromAquary;
    }

    public IRequestCallBack getRequestProcess() {
        return mRequestProcess;
    }

    @Override
    public void setTag(Object tag) {
        mTag = tag.getClass().getName();
    }

    @Override
    public String getTag() {
        if (mUrlRequest != null){
            return mUrlRequest.getTag();
        }
        return mTag;
    }

    /**
     *
     * 获取get请求的url，这里面已经是加密过的了；
     * @return
     */
    public String getGetUrl(){
        addExtraParams("vc", DeviceInfo.getAppVersion());
        addExtraParams("state", TelephonyInfo.getSimState(MainApplication.getInstance()) + "");
        addExtraParams("imei", DeviceInfo.getDeviceId());
        return getGetUrl(mRequestMap);
    }

    private String getGetUrl(Map<String, String> map) {
        String url = getUrl();
        if (url.contains("sy_c")) {
        } else{
            String[] qqq = url.split("\\?");
            if (Utils.isEncryt(url)) {
                url = qqq[0] + "?" + Utils.encryptGet(map, true);
            } else {
                url = qqq[0] + "?" + Utils.encryptGet(map, false);
            }
        }
        return url;
    }


    private void addExtraParams(String key,String value){
        if (!mRequestMap.containsKey(key)){
            mRequestMap.put(key, value);
        }
    }


    public Map<String,String> getPostParams(){
        Map<String,String> params = new HashMap<String, String>();
        String pairs = "";
        try {
            pairs = Utils.encryptPost(mUrl, mRequestMap, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pairs==null){
            return mRequestMap;
        }
        params.put("sy_c",pairs);
        params.put("vc", DeviceInfo.getAppVersion());
        return params;
    }


    public void setRequestProcess(IRequestCallBack mRequestProcess) {
        this.mRequestProcess = mRequestProcess;
    }

    public void addParams(String key,String value){
        if(key==null||key.equals("")){
            throw new IllegalStateException("params is null ");
        }
        mRequestMap.put(key,value);
    }

    public Map<String, String> getParams(){
        return mRequestMap;
    }

    public Map<String, String> getHeader() {
        if (mUrlRequest != null){
            return mUrlRequest.getRequestHeader();
        }
        return mHeader;
    }

    public void setHeader(Map<String, String> mHeader) {
        this.mHeader = mHeader;
    }

    public void addKeyValueTag(String key,Object obj){
        mTagMap.put(key,obj);
    }

    public Object getKeyValueTag(String key){
        return mTagMap.get(key);
    }

    public String getCacheKey(){
        if (mUrlRequest != null){
            return mUrlRequest.getCacheKey();
        }
        if(mCacheKey!=null){
            return mCacheKey;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(mUrl);
        if(!mUrl.endsWith("?")){
            builder.append("&");
        }

        for(Map.Entry<String, String> entity:mRequestMap.entrySet()){
            builder.append(entity.getKey());
            builder.append("=");
            builder.append(entity.getValue());
            builder.append("&");
        }
        mCacheKey = builder.toString();
        return mCacheKey;
    }

    public <T> T getResponse() {
        return (T) mResponse;
    }

    public void setResponse(Object mResponse) {
        this.mResponse = mResponse;
    }

    public IHttpError getVolleyError() {
        return mVolleyError;
    }

    public Request.IProcess getPreProcess() {
        return mPreProcess;
    }

    public void setPreProcess(Request.IProcess mPreProcess) {
        this.mPreProcess = mPreProcess;
    }

    public Request.IProcess getPostProcess() {
        return mPostProcess;
    }

    public void setPostProcess(Request.IProcess mPostProcess) {
        this.mPostProcess = mPostProcess;
    }

    /**
     *
     * @param _key
     */
    public void setCacheKey(String _key){
        mCacheKey = _key;
    }

    public boolean isForceRefresh() {
        if (mUrlRequest != null){
            return mUrlRequest.isForceRefresh();
        }
        return mForceRefresh;
    }

    public boolean isForceCache() {
        if (mUrlRequest != null){
            return mUrlRequest.isForceCache();
        }
        return mForceCache;
    }

    public void setForceCache(boolean mForceCache) {
        this.mForceCache = mForceCache;
    }

    public void setForceRefresh(boolean mForceRefresh) {
        this.mForceRefresh = mForceRefresh;
    }

    public IParser getParser() {
        if (mUrlRequest != null){
            return mUrlRequest;
        }
        return mParser;
    }

    public void setParser(IParser mParser) {
        this.mParser = mParser;
    }

    public void onCallBackError(IHttpError error){
        if (mUrlRequest != null){
            mUrlRequest.onHttpError(error);
            return;
        }
        mVolleyError = error;
        if (mVolleyResponse!=null) {
            mVolleyResponse.onHttpError(this);
        }
    }

    public void setCallBack(IVolleyResponse _response){
        mVolleyResponse =_response;
    }

    public IVolleyResponse getCallBack(){
        return mVolleyResponse;
    }

    public String getDownloadFilePath() {
        if (mUrlRequest != null){
            return mUrlRequest.getDownLoadFilePath();
        }
        return mDownloadFilePath;
    }

    public void setDownloadFilePath(String mDownloadFilePath) {
        this.mDownloadFilePath = mDownloadFilePath;
    }

    /**
     * Notes!This method is not in the main thread
     *
     * @param max
     * @param cur
     */
    public void onProcess(long max,long cur){
        if (mUrlRequest != null){
            mUrlRequest.onLoading(max, cur);
            return;
        }
        if(mLoading!=null){
            mLoading.onHttpProcess(max,cur);
        }
    }

    public IVolleyLoadResponse getmLoading() {
        return mLoading;
    }

    public void setmLoading(IVolleyLoadResponse mLoading) {
        this.mLoading = mLoading;
    }

	public void onCallBack(Object response) {
        if (mUrlRequest != null){
            mUrlRequest.onHttpResponse(response);
            return;
        }
		if(mVolleyResponse !=null){
            mResponse = response;
            mVolleyResponse.onHttpResponse(this);
		}
	}

    public int getmRetryTimes() {
        if (mUrlRequest != null){
            return mUrlRequest.getRetryTimes();
        }
        return mRetryTimes;
    }

    public void setmRetryTimes(int mRetryTimes) {
        this.mRetryTimes = mRetryTimes;
    }

    public void onStart(){
        if (mUrlRequest != null){
            mUrlRequest.onHttpStart();
            return;
        }
        if (mVolleyResponse !=null){
            mVolleyResponse.onHttpStart(this);
        }
    }

    public void setBaseUrlRequest(BaseUrlRequest baseUrlRequest) {
        this.mUrlRequest = baseUrlRequest;
        mRequestMap = mUrlRequest.getParams();
    }

    public void setDownloadFileName(String file){
        mFile = file;
    }

    public String getDownloadFileName(){
        return  mFile;
    }

    public BaseUrlRequest getBaseUrlRequest(){
        return mUrlRequest;
    }
}
