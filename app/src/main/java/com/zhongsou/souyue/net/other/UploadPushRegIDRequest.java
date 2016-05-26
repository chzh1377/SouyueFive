package com.zhongsou.souyue.net.other;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.countUtils.AppInfoUtils;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.receiver.NotificationMsgReceiverTwo;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONObject;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 向IM上传push regID，get方式
 * @date 2016/01/06
 */
public class UploadPushRegIDRequest extends BaseUrlRequest {

    private String url;
    private boolean refresh;
    private static final int DEVICE_ANDROID = 1;    //标记Android设备



    public UploadPushRegIDRequest(int id, IVolleyResponse response) {
        super(id, response);
        url = getIMHost();
    }

    public void setForceRefresh(boolean refresh){
        this.refresh = refresh;
    }
    @Override
    public boolean isForceRefresh() {
        return refresh;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getMethod() {
        return BaseUrlRequest.REQUEST_METHOD_POST;
    }

    /**
     * 添加参数
     * @param regID 推送的注册id
     * @param pushChannel 小米推送（1） or 极光推送（2）
     */
    public void setParams(String regID, int pushChannel){
        setProcessStragegy(REQUEST_ADDPARAMS,false);
        String clientId = NotificationMsgReceiverTwo.getClientId();
        String phone = android.os.Build.MODEL.replaceAll(" ","_");  //替换空格
        if(StringUtils.isEmpty(clientId)){
            clientId = null;
        }
        try {
            addParams("sid", SYUserManager.getInstance().getToken());    //会话ID"				// 在搜悦中指搜悦的token
            addParams("deviceToken", regID);   // "设备标识"	// 在小米，极光推送中是registe id，在ios中是device token
            addParams("pushChannel", String.valueOf(pushChannel));   //int			// 推送通道, 1. MiPush, 2. JPush, 3.IOS(apns)
            addParams("clientId", clientId);  //"13834AD93EF112739833"		// 搜悦IM clenit id

            addParams("device", String.valueOf(DEVICE_ANDROID));    // int				// 设备类型, 1. android, 2. ios
            addParams("deviceName", phone);    //: "设备名称" 	// 例如:xiaomi 4c, huawei p8, iphone 6s...
            addParams("appId", AppInfoUtils.getPackageName(MainApplication.getInstance())); // "souyue"
            addParams("ver", AppInfoUtils.getAppVersion(MainApplication.getInstance()));    // "5.0.8"
            addParams("r", String.valueOf(System.currentTimeMillis())); // long  					// unix时间戳，用来防止请求被缓存
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        JSONObject json = null;
        if(res != null){
            json = new JSONObject(res);
        }
        return json;
    }

    public String getIMHost(){
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {

            case SOUYUE_DEVLOPER:
                return "http://103.7.221.128:8098/api/device/update";
            case SOUYUE_TEST:
//                return "http://103.7.221.129:8098/api/device/update";
                return "http://103.7.221.130:8098/api/device/update";
            case SOUYUE_PRE_ONLINE:
                return "http://103.29.134.182:8098/api/device/update";
            case SOUYUE_ONLINE:
                return "http://im.zhongsou.com/api/device/update";
            default:
                return "http://im.zhongsou.com/api/device/update";
        }
    }
}
