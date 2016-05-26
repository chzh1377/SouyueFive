package com.zhongsou.souyue.net.volley;

/**
 * Created by lvqiang on 15/8/5.
 */
public interface IHttpManager {
    void doRequest(CVolleyRequest _request);
    void doDownload(CVolleyRequest _request);
    void doUpload(CVolleyRequest _request);
}
