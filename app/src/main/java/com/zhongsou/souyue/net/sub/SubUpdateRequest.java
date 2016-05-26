package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/12.
 * 添加订阅和删除订阅
 */
public class SubUpdateRequest extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/subscribe.update.my5.0.groovy";

    @Override
    public String getUrl() {
        return URL;
    }

    public SubUpdateRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(String token,
                          String srpId, String type, String category, String interestid,
                          String keyword) {
        addParams("token", token);
        addParams("srpId", srpId);
        addParams("type", type);
        addParams("category", category);
        addParams("interestid", interestid);
        addParams("keyword", keyword);
    }

    public void setParams(String token, HomeBallBean bean) {
        addParams("token", token);
        addParams("keyword", bean.getKeyword());
        addParams("type", "del");
        addParams("category", bean.getCategory());
        addParams("srpId", bean.getSrpId());
        addParams("id", String.valueOf(bean.getId()));
        addParams("interestid", String.valueOf(bean.getId()));
    }

    public void setParams(String token, String imei,
                          String srpId, String type, String category, String interestid,
                          String keyword){
        addParams("token", token);
        addParams("imei", imei);
        addParams("keyword", keyword);
        addParams("type", type);
        addParams("category", category);
        addParams("srpId", srpId);
        addParams("id", interestid);
        addParams("interestid", interestid);
        addParams("opSource", ZSSdkUtil.TOPIC_SUBSCRIBE_MENU);
    }

    public static void send(int id, String token, HomeBallBean bean, IVolleyResponse callback) {
        SubUpdateRequest request = new SubUpdateRequest(id, callback);
        request.addKeyValueTag("homeball", bean);
        request.setParams(token, bean);
        CMainHttp.getInstance().doRequest(request);
    }

    public static void send(int id, String token, String imei,
                            String srpId, String type, String category, String interestid,
                            String keyword, IVolleyResponse callback){
        SubUpdateRequest request = new SubUpdateRequest(id,callback);
        request.setParams(token, imei, srpId, type, category, interestid, keyword);
        CMainHttp.getInstance().doRequest(request);

    }
}