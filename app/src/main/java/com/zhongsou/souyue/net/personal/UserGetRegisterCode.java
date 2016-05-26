package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * 手机找回密码，获取验证码
 * Created by lvqiang on 15/12/12.
 */
public class UserGetRegisterCode extends BaseUrlRequest {
    public String FIND_PWD_CODE=HOST+"user/sendMobileVerify.groovy";
    public UserGetRegisterCode(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return FIND_PWD_CODE;
    }

    public void setParams(String phoneNum){
        addParams("mobile",phoneNum);
    }

    public void setParams(String phoneNum, int eventType){
        addParams("mobile",phoneNum);
        addParams("eventType",eventType+"");
    }
}
