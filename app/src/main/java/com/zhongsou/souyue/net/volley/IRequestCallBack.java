package com.zhongsou.souyue.net.volley;

/**
 * Created by lvqiang on 15/8/5.
 */
public interface IRequestCallBack<T> {
    void onCallBackSucess(CVolleyRequest _request,Object response);
    void onCallBackError(CVolleyRequest _request,T response);
}
