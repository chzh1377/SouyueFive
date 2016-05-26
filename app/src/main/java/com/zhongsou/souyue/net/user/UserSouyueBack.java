package com.zhongsou.souyue.net.user;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.UserAction;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wangchunyan on 2015/12/16.
 */
public class UserSouyueBack extends BaseUrlRequest {

    public  String back_souyue = getBackSouyueHost()
            + "api/user/visit";
    public static String getBackSouyueHost() {
        switch (Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env))) {
            case BaseUrlRequest.SOUYUE_TEST:
                return "http://usercentertest.zhongsou.com/";
            case SOUYUE_PRE_ONLINE:
                return "http://usercenter.zhongsou.com/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://usercentertest.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://usercenter.zhongsou.com/";
            case SOUYUE_DEVLOPER:
                return "http://usercentertest.zhongsou.com/";
            default:
                return "http://usercenter.zhongsou.com/";
        }
    }
    public UserSouyueBack(int id, IVolleyResponse response){
        super(id,response);
    }
    @Override
    public String getUrl() {
        return back_souyue;
    }
    public void setParams() {
        JSONArray json = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("type", UserAction.OPENAGAIN);
            jsonObj.put("systemSign", UserAction.SYSTEMSIGN);
            jsonObj.put("apiKey", UserAction.APIKEY);
            jsonObj.put("userName", UserAction.getUsername());
            jsonObj.put("userType", UserAction.USERTYPE);
            jsonObj.put("count", UserAction.COUNT);
            jsonObj.put("keyword", UserAction.KEYWORD);
            jsonObj.put("percentage", UserAction.PERCENTAGE);
            json.put(jsonObj);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addParams("sData", json.toString());
        addParams("encrypt", UserAction.ENCRYPT);
        addParams("appid", SYUserManager.getInstance().getUser().getAppId());
        addParams("openid", SYUserManager.getInstance().getUser().getOpenid());
        addParams("opid", SYUserManager.getInstance().getUser().getOpid());
        addParams("auth_token", SYUserManager.getInstance().getUser().getAuth_token());
        addParams("sysId", SYUserManager.getInstance().getUser().userId()+"");
    }
}
