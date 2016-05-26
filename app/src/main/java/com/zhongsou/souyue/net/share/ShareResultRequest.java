package com.zhongsou.souyue.net.share;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zyw on 2015/12/16.
 * 分享积分回调
 * "souyueapi/sharestats.ashx";
 * Method : POST
 * Params :
 * setParams("userid","");
 * setParams("token","");
 * setParams("url",info.getUrl());
 * setParams("srp",info.getKeyWord());
 * setParams("srpid",info.getSrpId());
 * setParams("platform", info.getPlatform());
 * setParams("os", DeviceInfo.osName);
 * setParams("model",DeviceInfo.deviceName);
 * setParams("useragent", "");
 */
public class ShareResultRequest extends BaseUrlRequest {

    // 搜悦环境
    public String getYouBao() {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
//          这里要求测试环境也得按照正式环境的来
                return "http://202.108.33.137:8122/";
            case SOUYUE_PRE_ONLINE:
                return "http://n.zhongsou.net/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://n.zhongsou.net/";
            case SOUYUE_ONLINE:
                return "http://n.zhongsou.net/";
            default:
                return "http://n.zhongsou.net/";
        }
    }

    public ShareResultRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getYouBao() + "souyueapi/sharestats.ashx";
    }

    public void setParams(SharePointInfo info) {
        long userid = SYUserManager.getInstance().getUser().userId();
        if (userid != 0) {
            addParams("userid", SYUserManager.getInstance().getUser().userId() + "");
        } else {
            addParams("token", SYUserManager.getInstance().getUser().token());
        }
        addParams("url", info.getUrl());
        addParams("srp", info.getKeyWord());
        addParams("srpid", info.getSrpId());
        addParams("platform", info.getPlatform());
        addParams("os", DeviceInfo.osName);
        addParams("model", DeviceInfo.deviceName);
        addParams("useragent", "");
    }

    public static void send(int id,IVolleyResponse resp,SharePointInfo info){
        ShareResultRequest request = new ShareResultRequest(id, resp);
        request.setParams(info);
        CMainHttp.getInstance().doRequest(request);
    }

}
