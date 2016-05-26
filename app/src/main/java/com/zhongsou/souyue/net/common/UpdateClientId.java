package com.zhongsou.souyue.net.common;

import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.receiver.NotificationMsgReceiverTwo;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by wangchunyan on 2015/12/15.
 * 把本地数据库同步到服务端
 */
public class UpdateClientId extends BaseUrlRequest {
    public  String updateClientId = HOST + "updateClientId.groovy";// 更新推送id
    @Override
    public String getUrl() {
        return updateClientId;
    }
    public UpdateClientId(int id,IVolleyResponse response){
        super(id,response);
    }
    public void setParams(String method){
        String clientId = NotificationMsgReceiverTwo.getClientId();
        User user = SYUserManager.getInstance().getUser();
        if (clientId == null || clientId.length() == 0 || user == null) return ;
        addParams("clientId", clientId);
        addParams("uid", user.userId() + "");
        addParams("type", "tuita");
        addParams("v", DeviceInfo.getAppVersion());
        addParams("method", method);
        addParams("op", DeviceInfo.getIMSI());
    }
}
