package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by lvqiang on 15/12/14.
 */
public class SrpReportReq extends BaseUrlRequest {
    private String URL = "comment/report.groovy";
    public SrpReportReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void setParams(Long id, int reportType){
        addParams("id", String.valueOf(id));
        addParams("type", String.valueOf(reportType));
    }
}
