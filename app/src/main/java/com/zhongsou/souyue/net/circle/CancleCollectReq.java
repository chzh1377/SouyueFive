package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 取消收藏
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class CancleCollectReq extends BaseUrlRequest {
    public String cancelCollect = HOST
            + "favorite/favorite.delete.groovy";// 取消收藏
    public CancleCollectReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return cancelCollect;
    }

    public void setParams( String token, String url,int dataType, long blogId)
    {
        addParams("token", token);
        addParams("url", url);
        if(dataType == 2) { //如果不是2,就不用传dataType
            addParams("dataType", dataType+"");
        }
        if(blogId != 0l) {//如果是0l,就不用传blogId
            addParams("newsId", blogId+"");
        }
    }

    public void setParamsForOpenFlag(String token, String _url, int dataType,int _openflag)
    {
        addParams("token", token);
        addParams("url", _url);
        addParams("dataType", dataType+"");
        addParams("operflag", _openflag+"");
    }
    public void setParams( String token, String url) {
        addParams("token", token);
        addParams("url", url);
    }

}
