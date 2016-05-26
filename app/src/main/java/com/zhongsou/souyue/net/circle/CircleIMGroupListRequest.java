package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/14.
 * 圈子IM群列表接口	interest/im.group.list.groovy
 * Method : GET
 * params :
 * ("srp_id", srp_id);
 * ("token", token);
 * ("last_sort_num", last_sort_num+"");
 * ("psize", psize+"");
 */
public class CircleIMGroupListRequest extends BaseUrlRequest {

    public CircleIMGroupListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void setParams(String srp_id, String token, long last_sort_num, int psize) {
        addParams("srp_id", srp_id);
        addParams("token", token);
        addParams("last_sort_num", last_sort_num + "");
        addParams("psize", psize + "");
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/im.group.list.groovy";
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    /**
     * 发请求
     *
     * @param id
     * @param resp
     * @param srp_id
     * @param token
     * @param last_sort_num
     * @param psize
     */
    public static void send(int id, IVolleyResponse resp, String srp_id, String token, long last_sort_num, int psize) {
        CircleIMGroupListRequest request = new CircleIMGroupListRequest(id, resp);
        request.setParams(srp_id, token, last_sort_num, psize);
        CMainHttp.getInstance().doRequest(request);
    }
}
