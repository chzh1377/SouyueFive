package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/1/22.
 * 订阅大全 -》 二级分类
 * URL :interest/group.list.groovy
 * 参数 ： 没有
 * 返回值：
 * List<InterestGroup>
 */
public class SubAllInterestGroupRequest extends BaseUrlRequest {

    private static final String MYURL = "interest/group.list.groovy";

    public SubAllInterestGroupRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }

    /**
     * 发出请求
     */
    public static void send(int id,IVolleyResponse callback){
        SubAllInterestGroupRequest request = new SubAllInterestGroupRequest(id,callback);
        CMainHttp.getInstance().doRequest(request);
    }
}
