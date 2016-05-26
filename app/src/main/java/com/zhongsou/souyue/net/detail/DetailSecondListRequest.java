package com.zhongsou.souyue.net.detail;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.CWidgetSecondList;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by zyw on 2016/1/22.
 * 获二级导航菜单
 * Url :"webdata/widget.list.groovy"
 * 参数
 * ("keyword", _keyword)
 * ("srpid", _srpid)
 * ("url", _url)
 * ("push_id", _pushid)
 * ("blog_id", String.valueOf(_blog_id))
 * ("newsChannel", channel)
 */
public class DetailSecondListRequest extends BaseUrlRequest {

    private static final String MYURL = "webdata/widget.list.groovy";

    public DetailSecondListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }

    public void setParams(String _keyword, String _srpid, String _url, String _pushid, String _blog_id, String channel) {
        addParams("keyword", _keyword);
        addParams("srpid", _srpid);
        addParams("url", _url);
        addParams("push_id", _pushid);
        addParams("blog_id", String.valueOf(_blog_id));
        addParams("newsChannel", channel);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
        return parseSecondList(response);
    }

    public Object parseSecondList(HttpJsonResponse res) throws UnsupportedEncodingException {

        CWidgetSecondList detail = new Gson().fromJson(res.getBody(),
                CWidgetSecondList.class);
        JsonArray navObject = res.getHead().getAsJsonArray("nav");
        if (navObject != null) {
            detail.setNav((List<NavigationBar>) new Gson().fromJson(
                    navObject, new TypeToken<List<NavigationBar>>() {
                    }.getType()));
        }
        detail.setShowMenu(res.getHeadBoolean("menu"));
        return detail;
    }

    /**
     * 发出请求
     *
     * @param id
     * @param _keyword
     * @param _srpid
     * @param _url
     * @param _pushid
     * @param _blog_id
     * @param channel
     * @param callback
     */
    public static void send(int id, String _keyword, String _srpid, String _url, String _pushid, String _blog_id, String channel, IVolleyResponse callback) {
        DetailSecondListRequest request = new DetailSecondListRequest(id, callback);
        request.setParams(_keyword, _srpid, _url, _pushid, _blog_id, channel);
        CMainHttp.getInstance().doRequest(request);
    }
}
