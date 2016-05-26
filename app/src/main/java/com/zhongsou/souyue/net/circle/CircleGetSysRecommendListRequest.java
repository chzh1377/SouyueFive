package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 获取系统推荐列表	interest/sys.recommend.list.groovy
 * Method : GET
 * Params :
 * ("interest_id", interest_id);
 * ("token", token);
 * ("psize", psize);
 * ("pno", pno);
 * ("last_id", last_id);
 */
public class CircleGetSysRecommendListRequest extends BaseUrlRequest {

    public CircleGetSysRecommendListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String interest_id, String token,
                          int psize, int pno, long last_id) {
        addParams("interest_id", interest_id);
        addParams("token", token);
        addParams("psize", psize+"");
        addParams("pno", pno+"");
        addParams("last_id", last_id+"");
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/sys.recommend.list.groovy";//获取系统推荐列表
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 发送数据
     * @param id
     * @param response
     * @param interest_id
     * @param token
     * @param psize
     * @param pno
     * @param last_id
     */
    public static void send(int id,IVolleyResponse response,String interest_id, String token,
                            int psize, int pno, long last_id){
        CircleGetSysRecommendListRequest request = new CircleGetSysRecommendListRequest(id, response);
        request.setParams(interest_id,token,psize,pno,last_id);
        CMainHttp.getInstance().doRequest(request);
    }
}
