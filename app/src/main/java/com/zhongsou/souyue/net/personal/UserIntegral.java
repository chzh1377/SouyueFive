package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.ZSEncode;

/**
 * 获取用户积分接口
 * Created by lvqiang on 15/12/15.
 */
public class UserIntegral extends BaseUrlRequest {
    private String URL = HOST + "index.php?s=userscore/get/";

    public UserIntegral(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getSouyueHost() {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
                return "http://103.29.134.124:8081/";
            case SOUYUE_PRE_ONLINE:
                return "http://d3sc.zhongsou.com/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://d3sc.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://d3sc.zhongsou.com/";
            default:
                return "http://d3sc.zhongsou.com/";
        }
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return URL;
    }


    public void setParams(String username){
        addParams("username", ZSEncode.encodeURI(username));
        addParams("appkey", "4da74e36-bf77-40e3-df6b-6b0a573773aa");
        addParams("appid", "10003");
    }
}
