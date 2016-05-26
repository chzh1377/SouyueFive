package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;

public class CSplashHttp extends AHttp{
	
	private Context context;
    private static final String TAG="CSplashHttp_volley";

    public CSplashHttp(Context _context){
    	super(_context,CSplashHttp.class.getName());
    	this.context = _context;
    }

    public void getSplashImage(int id,IVolleyResponse callback){
    	CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("machineType", "android");
        request.addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("rwidth",String.valueOf(Utils.getScreenWidth(context)));
        request.addParams("rheight",String.valueOf(Utils.getScreenHeight(context)));
        request.addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        request.setCallBack(callback);
        request.setUrl(UrlConfig.getSplashImage);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        mVolley.doRequest(request);
    }
}
