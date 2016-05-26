package com.zhongsou.souyue.net.personal;

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
 * 用户中心回访
 * Created by lvqiang on 15/12/16.
 */
public class UserCallBack extends BaseUrlRequest {
    private String URL = HOST + "api/user/visit" ;
    public UserCallBack(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getSouyueHost() {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
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

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(){
        JSONArray json = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("type", UserAction.OPENAGAIN);
            jsonObj.put("systemSign", UserAction.SYSTEMSIGN);
            jsonObj.put("apiKey", UserAction.APIKEY);
            jsonObj.put("userName", UserAction.getUsername());
            jsonObj.put("userType", UserAction.USERTYPE);
//      jsonObj.put("createTime", System.currentTimeMillis());//操作时间 可不传 服务器会处理
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
