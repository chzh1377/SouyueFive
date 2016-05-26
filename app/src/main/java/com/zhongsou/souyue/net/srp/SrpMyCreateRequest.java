package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.module.SelfCreate;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * 我的原创
 * @author chz
 *
 */
public class SrpMyCreateRequest extends BaseUrlRequest {

	public String mSearchResult = "";
	private String mUrl = "";
	private boolean mRefresh;
	
	public SrpMyCreateRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	/**
	 * 初始化加载
	 * @param url
	 * @param start
	 * @param count
	 * @param refresh
	 */
	public void addParams(String url, int start,int count,boolean refresh) {
		
		if (url != null && url.trim().toLowerCase().startsWith("http")) {
			this.mUrl = url;
			this.mRefresh = refresh;
            addParams("start", String.valueOf(start));
            addParams("count", String.valueOf(count));
        }
		
	}
	
	/**
	 * 下拉刷新
	 * @param url
	 * @param start
	 * @param refresh
	 */
	public void addParams(String url, Long start,boolean refresh) {

		if (url != null && url.trim().toLowerCase().startsWith("http")) {
			this.mUrl = url;
			this.mRefresh = refresh;
			addParams("start", String.valueOf(start));
		}

	}
	
	/**
	 * 上拉加载
	 * @param url
	 * @param start
	 * @param count
	 * @param list
	 * @param token
	 * @param refresh
	 */
	public void addParams(String url, Long start,int count, String list,boolean refresh)  {

		if (url != null && url.trim().toLowerCase().startsWith("http")) {
			this.mUrl = url;
			this.mRefresh = refresh;
			addParams("start", String.valueOf(start));
			addParams("count", String.valueOf(count));
		}

	}
	
	public void addParams(String url, Long start, String list,boolean refresh)  {

		if (url != null && url.trim().toLowerCase().startsWith("http")) {
			this.mUrl = url;
			this.mRefresh = refresh;
			addParams("start", String.valueOf(start));
		}

	}
	
	@Override
	public String getUrl() {
		return mUrl;//SRP列表
	}
	
	@Override
	public boolean isForceRefresh() {
		return mRefresh;
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_GET;
	}
	
	@Override
	public Object doParse(CVolleyRequest requet, String res) throws Exception {
		HttpJsonResponse response = (HttpJsonResponse) super.doParse(requet, res);
		SelfCreate result = new SelfCreate(response);
		result.requestUrl_$eq(mUrl);
		return result;
	}
}
