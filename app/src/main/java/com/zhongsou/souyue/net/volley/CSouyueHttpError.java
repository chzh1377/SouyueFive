package com.zhongsou.souyue.net.volley;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.zhongsou.souyue.net.HttpJsonResponse;

/**
 * Created by lvqiang on 15/7/9.
 */
public class CSouyueHttpError extends VolleyError implements IHttpError{

    private int mErrorCode;
    private String mErrorMessage;
    private HttpJsonResponse mJson;
    private int mType;
    private VolleyError mError;
    public CSouyueHttpError(VolleyError error){
        mType = TYPE_HTTP_ERROR;
        mError = error;
        mErrorMessage = error.getMessage();
        if (mError instanceof TimeoutError){
            mType = TYPE_TIME_OUT;
        }
    }

    public CSouyueHttpError(HttpJsonResponse json){
        mJson = json;
        mErrorCode = json.getCode();
        if (mErrorCode>=700) {
            mErrorMessage = json.getBodyString();
        }
        mType = TYPE_SERVER_ERROR;
    }

    @Override
    public int getErrorType() {
        return mType;
    }

    @Override
    public boolean isError(){
        return mErrorCode != 200;
    }

    @Override
    public int getErrorCode(){
        return mErrorCode;
    }

    @Override
    public HttpJsonResponse getJson(){
        return mJson;
    }

    @Override
    public String getmErrorMessage() {
        return mErrorMessage;
    }
}
