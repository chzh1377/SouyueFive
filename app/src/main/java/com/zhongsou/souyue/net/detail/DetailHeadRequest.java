package com.zhongsou.souyue.net.detail;

import com.google.gson.Gson;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.CWidgetHead;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zyw on 2016/1/22.
 * 获取详情头信息
 * webdata/widget.head.groovy
 * 请求参数：
 * keyword = 关键词
 * srpid = srpid
 * keyworccate = 关键词类型
 * _url = url地址
 * _type = 类型
 * 返回：
 * CWidgetHead.java
 */
public class DetailHeadRequest extends BaseUrlRequest {

    private static final String MYURL = "webdata/widget.head.groovy";

    public DetailHeadRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }


    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    /**
     * 请求参数
     *
     * @param _keyword
     * @param _srpid
     * @param keyworccate
     * @param _url
     * @param _type
     */
    public void setParams(String _keyword, String _srpid, int keyworccate, String _url, int _type) {
        addParams("keyword", _keyword);
        addParams("srpid", _srpid);
        addParams("keywordCate", keyworccate + "");
        addParams("url", _url);
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("keyword_type", _type + "");
        addParams("network", NetWorkUtils.getCurrentNetworkType(MainApplication.getInstance()));
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
        return new Gson().fromJson(response.getBody(),
                CWidgetHead.class);
    }

    /**
     * 发出请求
     *
     * @param id
     * @param _keyword
     * @param _srpid
     * @param keyworccate
     * @param _url
     * @param _type
     * @param callback
     */
    public static void send(int id, String _keyword, String _srpid, int keyworccate, String _url, int _type, IVolleyResponse callback) {
        DetailHeadRequest request = new DetailHeadRequest(id, callback);
        request.setParams(_keyword, _srpid, keyworccate, _url, _type);
        CMainHttp.getInstance().doRequest(request);
    }
}
