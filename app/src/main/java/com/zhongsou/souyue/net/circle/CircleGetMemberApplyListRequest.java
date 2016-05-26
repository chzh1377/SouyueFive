package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 用户私密圈申请记录列表接口	interest/member.interest.apply.list.groovy
 * Method: GET
 * params:
 * ("token", token);
 * ("pno", pno); -> 当前页码
 * ("psize", psize); -> 页面大小
 */
public class CircleGetMemberApplyListRequest extends BaseUrlRequest {


    public CircleGetMemberApplyListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/member.interest.apply.list.groovy";
    }

    public void setParams(String token, int pno, int psize) {
        addParams("token", token);
        addParams("pno", pno+"");
        addParams("psize", psize+"");
    }

    public static void send(int id, IVolleyResponse response, String token, int pno, int psize){
        CircleGetMemberApplyListRequest circleGetMemberApplyList = new CircleGetMemberApplyListRequest(id, response);
        circleGetMemberApplyList.setParams(token,pno,psize);
        CMainHttp.getInstance().doRequest(circleGetMemberApplyList);
    }
}
