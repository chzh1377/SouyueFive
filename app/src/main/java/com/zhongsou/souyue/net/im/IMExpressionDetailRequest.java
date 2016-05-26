package com.zhongsou.souyue.net.im;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/1/22.
 *
 */
public class IMExpressionDetailRequest extends BaseUrlRequest {
    private static final String MYURL = "im/package_detail.groovy";
    public IMExpressionDetailRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }
    public void setParams(String token, String vc,
                          String packageId){
        addParams("token", token);
        addParams("vc", vc);
        addParams("id", packageId);
    }

    /**
     * 发出请求
     * @param id
     * @param token
     * @param vc
     * @param packageId
     * @param callback
     */
    public static void send(int id, String token, String vc,
                            String packageId, IVolleyResponse callback){
        IMExpressionDetailRequest request = new IMExpressionDetailRequest(id,callback);
        request.setParams(token, vc, packageId);
        CMainHttp.getInstance().doRequest(request);
    }
}
