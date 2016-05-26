package com.zhongsou.souyue.net.other;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 请求版本更新信息
 * Created by lvqiang on 15/12/16.
 */
public class SettingCheckVersion extends BaseUrlRequest{
    private String URL = HOST + "checkVersion.groovy";
    public SettingCheckVersion(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    @Override
    public boolean isForceRefresh() {
        return true;
    }

    public void setParams(String p,String e,String c ){
        addParams("p", p);
        addParams("e",e);
        addParams("c", c);
    }
}
