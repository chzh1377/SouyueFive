package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 获取回复列表
 * Created by lvqiang on 15/12/16.
 */
public class UserUpdatePwd extends BaseUrlRequest {
    public String URL = HOST + "user/updatePwd.groovy?";

    public UserUpdatePwd(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String mobile, String pwd, String verifyNum,int eventType){
        addParams("mobile",mobile);
        addParams("pwd", pwd);
        addParams("verifyNum", verifyNum);
        addParams("eventType", eventType+"");
    }
}
