package com.zhongsou.souyue.net.guide;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.module.firstleader.ChildGroupItem;
import com.zhongsou.souyue.module.firstleader.UserGuideInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zyw on 2016/3/28.
 * 首次引导 提交订阅
 */
public class FirstGuideSubReq extends BaseUrlRequest {
    public static final String TAG    = FirstGuideSubReq.class.getSimpleName();
    public static final String MY_URL = "webdata/guide.novice.5.2.groovy";

    public FirstGuideSubReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return getSouyueHost().concat(MY_URL);
    }

    public void setParams(UserGuideInfo info, List<AppData> datas) {
        addParams("installApps", new Gson().toJson(datas));
        List<ChildGroupItem> subItems  = info.getSubItems();
        JsonArray            jsonArray = new JsonArray();
        for (ChildGroupItem item : subItems) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("category", item.getCategory());
                jsonObject.addProperty("srpId", item.getSrpId());
                jsonObject.addProperty("keyword", item.getKeyword());
                jsonArray.add(jsonObject);
        }
        addParams("subscribeList", jsonArray.toString());
        addParams("sex",info.getSex());
        addParams("age",info.getCharacter());
        UpEventAgent.onFirstEnterSouyueAccount(MainApplication.getInstance());
        UpEventAgent.onFirstEnterSouyueBookMark(MainApplication.getInstance());
    }

    public static void send(int id, IVolleyResponse response, UserGuideInfo info, List<AppData> datas) {
        FirstGuideSubReq req = new FirstGuideSubReq(id, response);
        req.setParams(info, datas);
        CMainHttp.getInstance().doRequest(req);
    }
}
