package com.zhongsou.souyue.net.search;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2016/1/12.
 */
public class SearchResultRequest extends BaseUrlRequest {

    private String mUrl ;
    private boolean mRefresh;
    private SearchResultRequest(int id, IVolleyResponse response,String url,boolean refresh) {
        super(id, response);
        mUrl = url;
        mRefresh = refresh;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    /**
     * searchresult接口，希望没改错
     * @param id
     * @param response
     * @param url
     * @param start
     * @param count
     * @param token
     */
    public static void searchResult(int id,IVolleyResponse response,String url, int start,int count, String token){
        if (url != null && url.trim().toLowerCase().startsWith("http")) {
            String reqUrl = url + "&start=" + start+"&token="+token+"&count="+count;
            SearchResultRequest request = new SearchResultRequest(id,response,reqUrl,false);
            CMainHttp.getInstance().doRequest(request);
        }
    }

    /**
     * 下拉刷新
     * @param id
     * @param response
     * @param url
     * @param lastId
     * @param count
     * @param token
     */
    public static void searchResultPullDown(int id,IVolleyResponse response,String url, String lastId,int count,String token){
        if (url != null && url.trim().toLowerCase().startsWith("http")) {
            String reqUrl = url + "&lastId=" + lastId+"&token="+token+"&count="+count;
            SearchResultRequest request = new SearchResultRequest(id,response,reqUrl,true);
            CMainHttp.getInstance().doRequest(request);
        }
    }

    /**
     * 伤啦加载
     * @param id
     * @param response
     * @param url
     * @param start
     * @param count
     * @param list
     * @param token
     */
    public static void searchResultLoadMore(int id,IVolleyResponse response,String url, long start,int count, String list,String token){
        if (url != null && url.trim().toLowerCase().startsWith("http")) {
            String reqUrl = url + "&start=" + start + "&list=" + list+"&token="+token+"&count="+count;
            SearchResultRequest request = new SearchResultRequest(id,response,reqUrl,true);
            CMainHttp.getInstance().doRequest(request);
        }
    }
}
