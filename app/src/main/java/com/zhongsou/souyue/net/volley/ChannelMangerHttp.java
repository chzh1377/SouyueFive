package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;

public class ChannelMangerHttp extends AHttp {

    public ChannelMangerHttp(Context _context){
        super(_context,ChannelMangerHttp.class.getName());
    }

    public void getChannelList(int id,String idDeafult,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("id",idDeafult);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.setCallBack(callback);
        request.setUrl(UrlConfig.channelList);
        mVolley.doRequest(request);
    }

    public void editChannel(int id,String channelTrue,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("channelTrue",channelTrue);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.setForceRefresh(true);//必定强制访问网络
        request.setCallBack(callback);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(UrlConfig.editChannel);
        mVolley.doRequest(request);
    }

}
