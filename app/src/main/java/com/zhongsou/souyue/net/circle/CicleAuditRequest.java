package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/13.
 * //收稿箱审批接口
 */
public class CicleAuditRequest extends BaseUrlRequest{
    public  final String URL = HOST+"interest/recommend.audit.groovy";

    public  String getUrl(){
        return URL;
    }

    public CicleAuditRequest(int id, IVolleyResponse response) {

        super(id, response);
    }

    public void addParams(long recommend_id, int from, int type) {
       addParams("recommend_id",recommend_id+"");
        addParams("from",from+"");
        addParams("type",type+"");
    }

}
