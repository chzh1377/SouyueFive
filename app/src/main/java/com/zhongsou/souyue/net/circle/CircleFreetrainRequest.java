package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/13.
 * 获得免审状态
 */
public class CircleFreetrainRequest extends BaseUrlRequest {
    private  final String URL =
            HOST+"interest/interest.freetrial.info.groovy";

    public  String getUrl(){
        return URL;
    }

    public CircleFreetrainRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(long interest_id) {
        addParams("interest_id", interest_id+"");
    }
}
