package com.zhongsou.souyue.net.im;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/1/22.
 * Im表情包列表
 * URL : "im/package_list.groovy"
 * 参数
 * ("token", token)
 * ("minSortNo", minSortNo + "")
 * ("pageSize", pageSize + "")
 * 返回值：
 * List<PackageBean>
 */
public class IMExpressionListRequest extends BaseUrlRequest {

    public static final String MYURL = "im/package_list.groovy";

    public IMExpressionListRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }

    public void setParmas(String token, long minSortNo, int pageSize) {
        addParams("token", token);
        addParams("minSortNo", minSortNo + "");
        addParams("pageSize", pageSize + "");
    }

    /**
     * 发出请求
     *
     * @param id
     * @param token
     * @param minSortNo
     * @param pageSize
     * @param callback
     */
    public static void send(int id, String token, long minSortNo,
                     int pageSize, IVolleyResponse callback) {
        IMExpressionListRequest request = new IMExpressionListRequest(id, callback);
        request.setParmas(token, minSortNo, pageSize);
        CMainHttp.getInstance().doRequest(request);
    }
}
