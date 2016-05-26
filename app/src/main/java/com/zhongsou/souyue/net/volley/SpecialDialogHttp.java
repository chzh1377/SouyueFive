package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.SpecialDialogData;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;



public class SpecialDialogHttp extends AHttp {

    public SpecialDialogHttp(Context _context) {
        super(_context, SpecialDialogHttp.class.getName());
    }
    public void getSpecialListData(int id,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.setUrl(UrlConfig.specialRecommendList);
        request.setParser(this);
        request.setCallBack(callback);
        mVolley.doRequest(request);
    }
    public void subscribeSpecialSpecial(int id, String keyword, String srpId, String idDelete,IVolleyResponse callback) {
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", SYUserManager.getInstance().getToken());
        request.addParams("type", "special");
        request.addParams("keyword", keyword);
        request.addParams("srpId", srpId);
        request.addParams("delete", idDelete);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.subscribeAdd);
        mVolley.doRequest(request);
    }
    @Override
    public Object doParse(CVolleyRequest request, String response) throws Exception {
//        String content = response;
//        JsonObject res = JsonParser.parse(content).getAsJsonObject();
//        HttpJsonResponse http = new HttpJsonResponse(res);
        HttpJsonResponse http = (HttpJsonResponse) super.doParse(request,response);
        if(http.getCode()==200){
            List<SpecialDialogData> specialData= new Gson().fromJson(http.getBodyArray(), new TypeToken<List<SpecialDialogData>>() {}.getType());
            return specialData;
        }
        return null;
    }
    
}
