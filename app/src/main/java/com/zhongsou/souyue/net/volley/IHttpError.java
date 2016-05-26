package com.zhongsou.souyue.net.volley;

import com.zhongsou.souyue.net.HttpJsonResponse;

/**
 * Created by lvqiang on 15/8/4.
 */
public interface IHttpError {
    public static final int TYPE_SERVER_ERROR=0;//搜悦服务器返回的业务逻辑错误
    public static final int TYPE_HTTP_ERROR=1;//网络请求是http错误
    public static final int TYPE_TIME_OUT=2;//网络请求超时错误
    int getErrorType();
    boolean isError();
    int getErrorCode();
    HttpJsonResponse getJson();
    String getmErrorMessage();
}
