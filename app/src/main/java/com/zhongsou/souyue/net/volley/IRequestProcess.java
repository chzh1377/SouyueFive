package com.zhongsou.souyue.net.volley;

/**
 *
 * Created by lvqiang on 15/5/29.
 */
public interface IRequestProcess {
    void doRequest(CVolleyRequest _request);
    void doRequest(BaseUrlRequest _request);
    void onDestory();
    boolean isRunning(String url);
    void cancelDownload(int id);
    void cancel(String tag);
}
