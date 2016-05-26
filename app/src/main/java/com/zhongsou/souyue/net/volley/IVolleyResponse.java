package com.zhongsou.souyue.net.volley;

/**
 *
 * Created by lvqiang on 15/5/5.
 */
public interface IVolleyResponse {
    public void onHttpResponse(IRequest request);
    public void onHttpError(IRequest request);
    public void onHttpStart(IRequest request);
}
