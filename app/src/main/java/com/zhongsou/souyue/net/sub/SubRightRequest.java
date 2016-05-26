package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description 添加订阅界面右侧接口
 * @date 2016/4/8
 */
public class SubRightRequest extends BaseUrlRequest {
    private static final String MYURL = "subscribe/cate.children.groovy";

    public SubRightRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }


    public void setParams(String parentId,String userId) {
        addParams("parentId",parentId);
        addParams("userId",userId);
    }

    //http://61.135.210.239:8888/d3api2/subscribe/cate.children.groovy?parentId=&userId=
}
