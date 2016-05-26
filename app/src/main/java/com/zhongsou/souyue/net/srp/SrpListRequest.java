package com.zhongsou.souyue.net.srp;

import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
/**
 * SRP列表
 * @author chz
 *
 */
public class SrpListRequest extends BaseUrlRequest {

	public String mSearchResult = "";
	private String mUrl = "";
	private boolean mRefresh;
	
	public SrpListRequest(int id, IVolleyResponse response) {
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
	 * 上拉刷新
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
	 * 下拉加载
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
	public Object doParse(CVolleyRequest request, String res) throws Exception {
		HttpJsonResponse response = (HttpJsonResponse) super.doParse(request, res);
		SearchResult result = new SearchResult(response);
		return result;
		/*switch (request.getmId()) {
		case HttpCommon.SRP_LIST_MY_CREATE_REQUEST:
		case HttpCommon.SRP_LIST_MY_CREATE_MORE_REQUEST:
		case HttpCommon.SRP_LIST_MY_CREATE_REFRESH_REQUEST:
			SelfCreate selfCreate = new SelfCreate(response);
			selfCreate.requestUrl_$eq(mUrl);
			return selfCreate;
		default:
			SearchResult result = new SearchResult(response);
			return result;
		}*/
	}
}
