package com.zhongsou.souyue.net.pay;

import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.net.EntBaseRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyw on 2015/12/28.
 * 获取用户的手机号码 :
 * Method GET
 */
public class EntGetMobileNoRequest extends EntBaseRequest {

    public static final String MOBILENO = "user.getMobile"; // 获取手机号

    public EntGetMobileNoRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return API_URL;
    }

    public void setParams(long userId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", userId);
        addParams("m", MOBILENO);
        String param = encodeParams(params);
        param = param.replace("\n","");
        addParams("p", param);
    }


    public static void send(int id, IVolleyResponse response, long userid) {
        EntGetMobileNoRequest request = new EntGetMobileNoRequest(id, response);
        request.setParams(userid);
        CMainHttp.getInstance().doRequest(request);
    }
}
