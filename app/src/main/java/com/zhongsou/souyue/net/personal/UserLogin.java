package com.zhongsou.souyue.net.personal;

import com.androidquery.util.AQUtility;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;

/**
 * 用户登录
 * Created by lvqiang on 15/12/12.
 */
public class UserLogin extends BaseUrlRequest {
    public String HTTP_URL_LOGIN=HOST+"user/login.groovy";

    public UserLogin(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return HTTP_URL_LOGIN;
    }

    public void setParams(String name, int verifyNum){
        addParams("name", name);
        addParams("verifyNum", verifyNum+"");
        addAllParams();

    }

    public void setParams(String name, String password, String nick){
        addParams("name", name);
        addParams("password", password);
        addParams("nick", nick);
        addAllParams();
    }

    private void addAllParams(){
        addParams("mac", com.tuita.sdk.DeviceUtil.getMacAddr(MainApplication.getInstance()));
        addParams("imsi", com.tuita.sdk.DeviceUtil.getSIMNum(MainApplication.getInstance()));
        addParams("uuid", com.tuita.sdk.DeviceUtil.getUUID(MainApplication.getInstance()));

        addParams("deviceInfo", DeviceUtil.getDeviceInfo(MainApplication.getInstance()));
        addParams("valiNo", AQUtility.readValiNoFile());

        addParams("province", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_PROVINCE, ""));
        addParams("city", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_CITY, ""));
        addParams("lat", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LAT, ""));
        addParams("log", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LNG, ""));

        addParams("modelType", DeviceUtil.getDeviceModel());
        addParams("systemVc", DeviceInfo.osVersion);
        addParams("network", NetWorkUtils.getCurrentNetworkType(MainApplication.getInstance()));
    }
}
