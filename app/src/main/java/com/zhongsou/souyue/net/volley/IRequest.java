package com.zhongsou.souyue.net.volley;

/**
 * Created by lvqiang on 15/8/4.
 */
public interface IRequest {
    int getmId();
    <T> T getResponse();

    void addKeyValueTag(String key,Object obj);
    Object getKeyValueTag(String key);

    IHttpError getVolleyError();

    String getCacheKey();

    void setTag(Object tag);
    String getTag();
}
