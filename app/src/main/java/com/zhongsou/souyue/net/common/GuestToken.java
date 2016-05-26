package com.zhongsou.souyue.net.common;

import android.content.Context;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.Random;

/**
 * Created by wangchunyan on 2015/12/12.
 * 获取游客token
 */
public class GuestToken extends BaseUrlRequest {
    public String tokenUrl = HOST + "user/token.groovy";// 获取游客token
    private Integer mRetryTime;

    public GuestToken(int id, IVolleyResponse response, Integer retryTime) {
        this(id, response);
        mRetryTime = retryTime;
    }

    public GuestToken(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public int getRetryTimes() {
        if (mRetryTime != null) {
            return mRetryTime;
        }
        return super.getRetryTimes();
    }

    @Override
    public boolean isForceRefresh() {
        return true;
    }

    public void setParams(Context ctx) {
        addParams("app_version", DeviceInfo.getAppVersion());
        addParams("app_version_code", DeviceInfo.getAppVersionCode() + "");
        addParams("carrier", DeviceInfo.getIMSI());
        addParams("os", DeviceInfo.osName);
        addParams("os_version", DeviceInfo.osVersion);
        addParams("resolution", DeviceInfo.getScreenSize());
        addParams("device_name", DeviceInfo.deviceName);
        addParams("imei", DeviceUtil.getDeviceId(MainApplication.getInstance()));
        addParams("mac", com.tuita.sdk.DeviceUtil.getMacAddr(MainApplication.getInstance()));
        addParams("imsi", com.tuita.sdk.DeviceUtil.getSIMNum(MainApplication.getInstance()));
        addParams("uuid", com.tuita.sdk.DeviceUtil.getUUID(MainApplication.getInstance()));
        addParams("deviceInfo", DeviceUtil.getDeviceInfo(MainApplication.getInstance()));
    }

    @Override
    public String getUrl() {
        return tokenUrl;
    }
}
