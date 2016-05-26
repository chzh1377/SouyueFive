package com.zhongsou.souyue.im.net;

import android.content.Context;
import com.zhongsou.souyue.net.volley.AHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONObject;

/**
 * Created by zcz on 2015/9/7
 */
public class CWebImLoginHttp extends AHttp {
    public static final int HTTP_REQUEST_WEBLOGIN_MODULE=1006;
    public CWebImLoginHttp(Context _context) {
        super(_context, CWebImLoginHttp.class.getName());
        mContext = _context;
    }

    public void doWebImLogin(String url,int id, String uuid, String appid, String chat_list,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("uuid", uuid);
        request.addParams("appid", appid);
        request.addParams("token",SYUserManager.getInstance().getToken());
        request.addParams("chat_list",chat_list);
        request.setParser(this);
        request.setCallBack(callback);
        request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
        request.setUrl(url);
        mVolley.doRequest(request);
    }

    @Override
    public Object doParse(CVolleyRequest request, String _response) throws Exception {
        int id = request.getmId();
        switch (id){
            case HTTP_REQUEST_WEBLOGIN_MODULE:
                JSONObject obj = new JSONObject(_response );
                return obj;
        }
        return super.doParse(request, _response);
    }
}
