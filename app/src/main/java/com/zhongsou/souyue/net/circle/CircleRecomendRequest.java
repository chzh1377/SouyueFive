package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by wangqiang on 15/12/13.
 * 获取系统推荐详情
 */
public class CircleRecomendRequest extends BaseUrlRequest {

    public  final String URL = HOST+ "interest/sys.recommend.info.groovy";//  获取系统推荐详情

    public  String getUrl(){
        return URL;
    }
    public CircleRecomendRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    public void addParams(long recommend_id) {
       addParams("recommend_id",recommend_id+"");
    }
}