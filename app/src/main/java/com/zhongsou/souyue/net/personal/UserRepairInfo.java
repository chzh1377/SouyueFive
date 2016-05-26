package com.zhongsou.souyue.net.personal;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * 手机号注册接口
 * Created by lvqiang on 15/12/13.
 */
public class UserRepairInfo extends BaseUrlRequest {
    private String URL = HOST + "user/updateProfile.groovy";

    public UserRepairInfo(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String sex){
        addParams("sex", sex);
    }

    public void setParams(String token, String url, String nick, String bgUrl, String signature){
        addParams("token", token);
        if (nick != null && !(StringUtils.isEmpty(nick)) && url != null && !url.isEmpty()) {
            addParams("image", url);
            addParams("nick", nick);
        } else if (url != null && !StringUtils.isEmpty(url)) {
            addParams("image", url);
        } else if (!StringUtils.isEmpty(bgUrl)) {
            addParams("bgUrl", bgUrl);
        } else if (signature!=null){
            addParams("signature", signature);
        } else {
            addParams("nick", nick);
        }
    }

}
