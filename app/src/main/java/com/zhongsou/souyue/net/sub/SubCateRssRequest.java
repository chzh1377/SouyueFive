package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/12.
 * 取订阅列表页面
 */
public class SubCateRssRequest  extends BaseUrlRequest {

    private  final String URL = HOST+"cate.tree3.0.groovy";

    public String getUrl(){
        return  URL;
    }

    public SubCateRssRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    public void addParameters(String id){
        addParams("id",id);
    }



}
