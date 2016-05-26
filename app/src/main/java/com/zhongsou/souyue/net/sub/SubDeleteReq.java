package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/17.
 * 删除订阅
 */
public class SubDeleteReq  extends BaseUrlRequest {

    private  final String URL = HOST+"subscribe/subscribe.delete5.2.groovy";

    @Override
    public  String getUrl() {
        return URL;
    }

    public SubDeleteReq(int id, IVolleyResponse response) {
        super(id,response);
    }


    /**
     *
     * @param id    srp 订阅id
     * @param type
     * @param opSource 用于统计，退订来源
     */
    public void addParameters(long id,String type,String srpId, String opSource){
       setParameters(String.valueOf(id), type, srpId, opSource);
    }

    public void setParameters(String id,String type,String srpId, String opSource){
        addParams("id", id);
        addParams("category",type);
        addParams("srpId",srpId);
        addParams("opSource", opSource);
    }
}