package com.zhongsou.souyue.net.circle;

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
 * Created by zyw on 2015/12/25.
 * 圈吧首页，二级导航
 */
public class CircleIndexNavRequest extends BaseUrlRequest {

    public static final String WIDGET_LIST_CIRCLEINDEX  = "1";

    public CircleIndexNavRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
//        return  HOST + "search.result5.1.groovy";
        return getSouyueHost() + "webdata/widget.list.groovy";
    }

    @Override
    public boolean isForceRefresh() {
        return true;
    }

    /**
     * 接口已经去掉了isSearch这个参数。
     * @param keyword
     * @param srpId
     * @param opSource
     * @param showLevel
     */
    public void setParams(String keyword, String srpId,
                          String opSource, String showLevel) {
        addParams("keyword", keyword);
        addParams("srpId", srpId);
//        addParams("isSearch", isSearch + "");
        addParams("start", "0");
        addParams("opSource", opSource);
        addParams("showLevel", showLevel);
    }

    @Override
    public Object doParse(CVolleyRequest requet, String res) throws Exception {
        HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
        Object o = parseSecondList(response);
        return o;
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

    public static void send(int id, IVolleyResponse response,String keyword, String srpId,
                            String opSource,String showLevel){
        CircleIndexNavRequest re = new CircleIndexNavRequest(id, response);
        re.setParams(keyword, srpId, opSource, showLevel);
        CMainHttp.getInstance().doRequest(re);

    }
}
