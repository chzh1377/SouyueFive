package com.zhongsou.souyue.net.volley;

/**
 * Created by lvqiang on 15/12/11.
 */
public interface IParser<T> {
    T doParse(CVolleyRequest requet,String _res) throws Exception;
}
