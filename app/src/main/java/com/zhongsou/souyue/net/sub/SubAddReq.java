package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by wangqiang on 15/12/17.
 * 添加订阅
 */
public class SubAddReq  extends BaseUrlRequest {

    private final String URL = HOST + "subscribe/subscribe.add.groovy";

    public String getUrl() {
        return URL;
    }

    public SubAddReq(int id, IVolleyResponse response) {
        super(id, response);
    }


    public void addParameters(List<String> rssIdAdd,
                              List<String> idDelete) {
        addParams("type", "rss");
        addParams("id", mkString(rssIdAdd));
        addParams("delete", mkString(idDelete));
    }

    public void addParameters(String keyword, String srpId, String idDelete, String groupName,String _type,String opSource){
        addParams("keyword", keyword);
        addParams("srpId", srpId);
        addParams("groupName", groupName);
        addParams("delete", idDelete);
        addParams("type", _type);
        addParams("opSource", opSource);
    }

    private String mkString(List list) {
        return list.toString().replaceAll("[\\[\\]\\s]", "");
    }
}
