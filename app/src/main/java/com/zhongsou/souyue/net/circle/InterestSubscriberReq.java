package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @description: 订阅兴趣圈
 * @auther: qubian
 * @data: 2015/12/12.
 */

public class InterestSubscriberReq extends BaseUrlRequest {

    public String saveRecommendCircleMethod = HOST // 订阅兴趣圈
            + "interest/interest.subscriber.groovy";

    public InterestSubscriberReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return saveRecommendCircleMethod;
    }

    public void setParams(String token, String mInterestId, String opSource) {
        addParams("token", token);
        if(checkParam(mInterestId) == 32){
            addParams("new_srpids", mInterestId);
        }else{
            addParams("interest_ids", mInterestId);
        }
        addParams("opSource", opSource);
    }

    public static void send(int id, String token, String vc, String interest_ids, String imei,
                            IVolleyResponse callback, String opSource) {
        InterestSubscriberReq req = new InterestSubscriberReq(id, callback);
        req.setParams(token, interest_ids, opSource);
        CMainHttp.getInstance().doRequest(req);
    }

    private int checkParam(String mInterestId){
        int result = 0;
        String temp = mInterestId.split(",")[0];
        result = temp.length();
        return result;
    }
}
