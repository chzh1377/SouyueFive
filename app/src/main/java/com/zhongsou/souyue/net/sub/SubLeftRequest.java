package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description 添加订阅界面左侧接口
 * @date 2016/4/8
 */
public class SubLeftRequest extends BaseUrlRequest {
    private static final String MYURL = "subscribe/subscribe.cate.list.groovy";

    public SubLeftRequest(int id, IVolleyResponse response) {
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
        SubLeftRequest request = new SubLeftRequest(id,callback);
        CMainHttp.getInstance().doRequest(request);
    }

    //http://61.135.210.239:8888/d3api2/subscribe/subscribe.cate.list.groovy
}
