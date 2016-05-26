package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 模板下载管理 请求
 * @date 2016/5/4
 */
public class TemplateDownRequest extends BaseUrlRequest {
    private String URL = HOST + "detail/template.path.groovy";

    public TemplateDownRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public static void send(int id, IVolleyResponse response) {
        TemplateDownRequest request = new TemplateDownRequest(id, response);
        request.setProcessStragegy(BaseUrlRequest.REQUEST_ENCRYPT, false);  //true:使用加密；false:不使用加密
        CMainHttp.getInstance().doRequest(request);
    }
}
