package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/2/29.
 */
public class DetailModuleCommenReq extends BaseUrlRequest {

    public String URL=HOST+"detail/comment5.0.list.count.groovy";

    public DetailModuleCommenReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(String url, int operflag, long last_sort_num, String srpid, String srpword, int type){
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("url", url);
        addParams("operflag", operflag + "");
        addParams("psize", 10 + "");
        addParams("last_sort_num", last_sort_num + "");
        addParams("srpword", srpword);
        addParams("srpid", srpid);
        addParams("type", type + "");
        addParams("appname", "souyue");
    }

}
