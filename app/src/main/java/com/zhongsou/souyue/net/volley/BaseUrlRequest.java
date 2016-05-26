package com.zhongsou.souyue.net.volley;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;

import java.util.HashMap;
import java.util.Map;

/**
 * 基本的网络请求类
 * Created by lvqiang on 15/12/11.
 */
public abstract class BaseUrlRequest implements IParser, IRequest {
    // 0 测试环境
    public static final int SOUYUE_TEST       = 0;
    // 1预上线环境
    public static final int SOUYUE_PRE_ONLINE = 1;
    // 2线上环境
    public static final int SOUYUE_ONLINE     = 2;

    // 3开发服务器
    public static final int SOUYUE_DEVLOPER           = 3;
    // 4专门为SRP搜索提供的一套预上线测试环境
    public static final int SOUYUE_PRE_ONLINE_FOR_SRP = 4;

    public static final int PROCESS_GUEST_TOKEN    = 0;
    public static final int PROCESS_VALI_NO        = 1;
    //请求加密策略
    public static final int REQUEST_ENCRYPT        = 0;//是否加密，目前没用到
    public static final int REQUEST_ADDPARAMS      = 1;//是否在底层加入公参
    public static final int REQUEST_ALREADY_ENTITY = 2;//是否已经是一个实体了


    public static final int MY_SOCKET_TIMEOUT_MS = 8 * 1000;

    public static final int REQUEST_METHOD_GET      = 0;//get方法请求
    public static final int REQUEST_METHOD_POST     = 1;//post方法请求
    public static final int REQUEST_METHOD_DOWNLOAD = 2;//下载

    public static final String POST_ENTITY = "%entity";

    public CRequestProcess mProcess;

    public String HOST = getSouyueHost();

    /**
     * tag 列表
     */
    private HashMap<String, Object> mTagMap     = new HashMap<String, Object>();
    /**
     * 请求参数
     */
    private HashMap<String, String> mRequestMap = new HashMap<String, String>();
    /**
     * 缓存key
     */
    private String          mCacheKey;
    /**
     * 请求id
     */
    private int             mId;
    /**
     * tag用来标识一个请求组可以用来取消网络请求
     */
    private String          mTag;
    /**
     * 回调接口
     */
    private IVolleyResponse mResponse;
    /**
     * 返回数据
     */
    private Object          mData;
    /**
     * 网络错误类
     */
    private IHttpError      mError;

    /**
     * 加密策略，默认加密且默认会添加公参
     *
     * @param id
     * @param response
     */
    private int mFlag = 1 | 1 << REQUEST_ADDPARAMS;

    /**
     * GSON
     */

    protected Gson mGson = new Gson();

    public BaseUrlRequest(int id, IVolleyResponse response) {
        mId = id;
        if (mTag != null) {
            mTag = response.getClass().getName();
        }
        mResponse = response;
        mRequestMap = new HashMap<String, String>();
    }

    @Override
    public int getmId() {
        return mId;
    }

    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    public abstract String getUrl();

    public void setmProcess(CRequestProcess mProcess) {
        this.mProcess = mProcess;
    }

    // 搜悦环境
    public String getSouyueHost() {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
                return "http://103.29.134.224/d3api2/";
            case SOUYUE_PRE_ONLINE:
                return "http://103.29.134.225/d3api2/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://111.206.69.38:8111/d3api2/";
            case SOUYUE_ONLINE:
                return "http://api2.souyue.mobi/d3api2/";

            case SOUYUE_DEVLOPER:
                return "http://61.135.210.239:8888/d3api2/";
            default:
                return "http://api2.souyue.mobi/d3api2/";
        }
    }

    /**
     * 用来取消网络请求时做的标记
     *
     * @param tag
     */
    @Override
    public void setTag(Object tag) {
        mTag = tag.getClass().getName();
    }

    @Override
    public String getTag() {
        return mTag;
    }

    /**
     * tag 用于存储临时变量
     *
     * @param key 存的key
     * @param obj 存的值
     */
    public void addKeyValueTag(String key, Object obj) {
        mTagMap.put(key, obj);
    }

    /**
     * tag 用于去临时变量
     *
     * @param key 取变量的key
     * @return 返回临时存储的变量
     */
    public Object getKeyValueTag(String key) {
        return mTagMap.remove(key);
    }

    /**
     * 向参数列表中添加参数
     *
     * @param key
     * @param value
     */
    protected void addParams(String key, String value) {
        if (key == null || key.equals("")) {
            throw new IllegalStateException("params is null ");
        }
        mRequestMap.put(key, value);
    }

    /**
     * 获取网络请求参数
     *
     * @return
     */
    public Map<String, String> getParams() {
        return mRequestMap;
    }

    /**
     * 子类重写此方法传入额外http头信息
     *
     * @return
     */
    public Map<String, String> getRequestHeader() {
        return null;
    }

    /**
     * 获取网络请求时的缓存key
     *
     * @return
     */
    public String getCacheKey() {
        if (mCacheKey != null) {
            return mCacheKey;
        }
        StringBuilder builder = new StringBuilder();
        String        url     = getUrl();
        builder.append(url);
        if (!url.endsWith("?") && url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }
        int count = mRequestMap.size();
        int i     = 0;
        for (Map.Entry<String, String> entity : mRequestMap.entrySet()) {
            builder.append(entity.getKey());
            builder.append("=");
            builder.append(entity.getValue());
            if (i != count - 1) {
                builder.append("&");
            }
            i++;
        }
        mCacheKey = builder.toString();
        return mCacheKey;
    }

    /**
     * 是否只取缓存，优先于@isForceRefresh方法判定
     *
     * @return true：只取缓存，false：不只取缓存
     */
    public boolean isForceCache() {
        return false;
    }

    /**
     * 是否判定缓存存在
     *
     * @return true：不判定缓存状态，直接访问网络，false：判定缓存状态，缓存存在取缓存，缓存不存在，取网络
     */
    public boolean isForceRefresh() {
        return false;
    }

    /**
     * 当前网络请求超时时间
     *
     * @return
     */
    public int getTimeOut() {
        return MY_SOCKET_TIMEOUT_MS;
    }

    /**
     * 当前请求如果是下载请求时，需要给定下载路径
     *
     * @return
     */
    public String getDownLoadFilePath() {
        return "";
    }

    /**
     * 网络返回数据解析方法，这里当需要override时要先调super方法，这个方法会对搜悦自定义相应错误做处理
     *
     * @param requet
     * @param res
     * @return
     * @throws Exception
     */
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        if (res == null) {
            throw new Exception("response is null");
        }
        HttpJsonResponse r = new HttpJsonResponse((JsonObject) new JsonParser().parse(res));
        mProcess.sendAuthMessage(r);
        CSouyueHttpError error = new CSouyueHttpError(r);
        if (error.isError()) {
            throw error;
        }
        return r;
    }

    /**
     * 设置网络重试次数
     *
     * @return
     */
    public int getRetryTimes() {
        return 1;
    }

    public void onPreProcess(Request<?> request) {
        throw new IllegalStateException("this method will implement in the future");
    }

    /**
     * 如果当前请求时下载请求时，这里返回下载进度
     *
     * @param max
     * @param cur
     */
    public void onLoading(long max, long cur) {
        if (getMethod() != REQUEST_METHOD_DOWNLOAD) {
            throw new IllegalStateException("this method is not download ");
        }
    }

    /**
     * 网络开始请求时回调，这个方法是在mainthread调用的
     */
    public void onHttpStart() {

    }

    /**
     * 网络回调，一般子类不用重写此方法
     *
     * @param response
     */
    public void onHttpResponse(Object response) {
        mData = response;
        if (mResponse != null) {
            mResponse.onHttpResponse(this);
        }
    }


    public void onHttpError(IHttpError error) {
        mError = error;
        if (mResponse != null) {
            mResponse.onHttpError(this);
        }
    }

    /**
     * 用于获取网络返回数据
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> T getResponse() {
        return (T) mData;
    }

    /**
     * 返回网络错误
     *
     * @return
     */
    @Override
    public IHttpError getVolleyError() {
        return mError;
    }


    protected void setProcessStragegy(int index, boolean flag) {
        if (flag) {
            mFlag = mFlag | (1 << index);
        } else {
            mFlag = ~(1 << index) & mFlag;
        }
    }

    public boolean getProcessStragegy(int index) {
        int flag = 1 << index & mFlag;
        if (flag != 0) {
            return true;
        } else {
            return false;
        }
    }
}
