package com.zhongsou.souyue.net.discover;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 *
 * 二维码扫描界面网络请求
 * Created by zhou on 2015/12/12.
 */
public class QrcodeRequest extends BaseUrlRequest {
    private String URL = HOST + "function/qrcodeJump.groovy";

    public QrcodeRequest(int id,IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String data){
        addParams("data", data);
    }


}
