package com.zhongsou.souyue.net.home;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 首页（不感兴趣）
 * @author chz
 *
 */
public class DisLikeRequest extends BaseUrlRequest {

	public static final String HOME_PAGE_DISLIKE="SEARCH_ITEM";
	public static final String HOME_PAGE_DISLIKE_OPERATOR_DISLIKE = "dislike";
	public static final String HOME_PAGE_DISLIKE_OPERATOR_CANCEL = "cancel";

	public DisLikeRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
    public void setParams(String _token,String _url,String _blog_id,String _interest_id,String dis,Object _obj,String channel,String type){
        addParams("token", _token);
        addParams("url", _url);
        addParams("blog_id", _blog_id);
        addParams("interest_id", _interest_id);
        addParams("disLike", dis);
        addParams("channel", channel);
        addParams("type", type);
        addKeyValueTag(HOME_PAGE_DISLIKE, _obj);
    }

    
	@Override
	public String getUrl() {
		return HOST + "news/disLike.groovy"; //首页不感兴趣接口
	}

	@Override
	public boolean isForceRefresh() {
		return true;//必定强制访问网络
	}
}
