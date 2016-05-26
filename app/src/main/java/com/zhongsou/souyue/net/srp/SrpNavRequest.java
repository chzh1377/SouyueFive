package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SRP导航
 * @author chz
 *
 */
public class SrpNavRequest extends BaseUrlRequest {

	public String mSearchResult = "";
	private boolean mRefresh;
	
	public SrpNavRequest(int id, IVolleyResponse response) {
		super(id, response);
	}
	
	/**
	 * 初始化加载
	 * @param url
	 * @param start
	 * @param count
	 * @param refresh
	 */
	public void addParams(String keyword, String srpId, boolean isSearch,
			String opSource, Boolean refresh) {
		this.mRefresh = refresh;
		addParams("keyword", keyword);
		addParams("srpId", srpId);
		addParams("isSearch", isSearch + "");
		addParams("start", "0");
		addParams("opSource", opSource);
	}

	public void addParams(String keyword, String srpId,
			int start, String opSource, Boolean refresh) {
		this.mRefresh = refresh;
		addParams("keyword", keyword);
		addParams("srpId", srpId);
		addParams("start", String.valueOf(start));
		addParams("opSource", opSource);
	}
	@Override
	public String getUrl() {
		return HOST + "webdata/search.result5.1.groovy";
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
		SearchResult result = new SearchResult(response);
		return result;
	}
}
