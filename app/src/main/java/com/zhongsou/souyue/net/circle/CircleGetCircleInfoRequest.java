package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 获取兴趣圈圈名片信息	interest/interest.carte.info.groovy
 * Method : GET
 * Params :
 * params.put("interest_id", interest_id); -> 兴趣圈id
 * params.put("type", type); 兴趣圈类型 0 普通，1 私密
 * Constant.INTEREST_TYPE_NORMAL
 */
public class CircleGetCircleInfoRequest extends BaseUrlRequest {


    public CircleGetCircleInfoRequest(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/interest.carte.info.groovy";// 获取兴趣圈圈名片信息
    }

    public void setParams(long interest_id, int type) {
        addParams("interest_id", interest_id+"");
        addParams("type", type+"");
    }

    /**
     * 发送请求
     * @param id
     * @param resp
     * @param interest_id
     * @param type
     */
    public static void send(int id, IVolleyResponse resp, long interest_id, int type){
        CircleGetCircleInfoRequest circleGetCircleInfo = new CircleGetCircleInfoRequest(id, resp);
        circleGetCircleInfo.setParams(interest_id,type);
        CMainHttp.getInstance().doRequest(circleGetCircleInfo);
    }
}
