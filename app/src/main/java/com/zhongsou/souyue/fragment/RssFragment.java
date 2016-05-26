package com.zhongsou.souyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.adapter.NewsListAdapter;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.search.SearchResultRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Rss 订阅
 *
 * @author zhangliang01@zhongsou.com
 */
public class RssFragment extends Fragment implements LoadingDataListener, ProgressBarClickListener, Serializable, IVolleyResponse {
    private static final long serialVersionUID = 6764934175201289274L;
    public static final String URL = "url";
    public static final String IMGABLE = "imgable";
    public static final String POSITION = "pos";

    public static final String TAG = RssFragment.class.getSimpleName();
    public PullToRefreshListView pullToRefreshListView;
    public NewsListAdapter newsAdapter;
    private String homePageUrl;
    private ProgressBarHelper pbHelper;
//    private Http htp;
    private boolean imgAble;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rss_list, null);
        pullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.rss_listview);
//        htp = new Http(this);
        if (newsAdapter == null)
            newsAdapter = new NewsListAdapter(getActivity());
        newsAdapter.setLoadingDataListener(this);
        pbHelper = new ProgressBarHelper(getActivity(), v.findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(this);

        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("list", (ArrayList<SearchResultItem>) newsAdapter.getDatas());
                // newsAdapter.updateView(position - 1, lv);
//                IntentCacheHelper.getInstance(List.class).setObject(newsAdapter.getDatas());
//                IntentCacheHelper.getInstance(List.class).setListFlag(true);
//                i.putExtra("position", position - 1);
//                i.setClass(getActivity(), ReadabilityActivity.class);
////				i.putExtras(bundle);
//                startActivityForResult(i, 0);
                IntentUtil.skipDetailPage(getActivity(), newsAdapter.getDatas().get(position), 0);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

        });
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // 重新请求数据
                newsAdapter.isRefresh = true;
//                htp.searchResultToPullDownRefresh(homePageUrl, 0 + "",15, SYUserManager.getInstance().getToken());
                SearchResultRequest.searchResultPullDown(HttpCommon.SRP_SEARCHRESULTPULLDOWNREFRESH_REQUESTID,RssFragment.this,homePageUrl, 0 + "",15, SYUserManager.getInstance().getToken());
            }

        });
        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (null != newsAdapter.getChannelTime())
                    pullToRefreshListView.onUpdateTime(StringUtils.convertDate(newsAdapter.getChannelTime()));
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homePageUrl = getArguments().getString(URL);
        getAdapterImageAble();
        newsAdapter.isRss = true;
        pullToRefreshListView.setAdapter(newsAdapter);

//		if(newsAdapter.dataSize()==0)
//        htp.searchResult(homePageUrl, 0, 15,SYUserManager.getInstance().getToken());
        SearchResultRequest.searchResult(HttpCommon.SRP_SEARCHRESULT_REQUESTID,this,homePageUrl, 0, 15,SYUserManager.getInstance().getToken());
//		else
//			pbHelper.goneLoading();
    }

//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (data != null && resultCode == 6) {
//			int[] readPos = data.getIntArrayExtra("readPos");
//			if (readPos != null && newsAdapter != null) {
//				updateHasRead(readPos);
//			}
//		}
//	}



    @Override
    public void loadDataMore(long start, String type) {// 滑动到底部自动获取更多
        if (newsAdapter.getHasMoreItems()) {
//            htp.searchResultToLoadMore(homePageUrl, start,15, "", SYUserManager.getInstance().getToken(), AbstractAQuery.CACHE_POLICY_CACHE);
            SearchResultRequest.searchResultLoadMore(HttpCommon.SRP_SEARCHRESULTTOLOADMORE_REQUESTID,this,homePageUrl, start,15, "", SYUserManager.getInstance().getToken());
        }

    }

    public void searchResultSuccess(final SearchResult sr) {
        if (CMainHttp.getInstance().isWifi(getActivity()))
            newsAdapter.setImgAble(true);
        else
            setAdapterImageAble();
        newsAdapter.setChannelTime(SystemClock.currentThreadTimeMillis() + "");
        pbHelper.goneLoading();
        newsAdapter.setHasMoreItems(sr.hasMore());
        newsAdapter.addDatas(sr.items());
        if (sr.hasExpired() && CMainHttp.getInstance().isNetworkAvailable(getActivity()))
            pullToRefreshListView.startRefresh();
//		if(flag){
//			outerTabPageIndicator.setCurrentItem(position);
//			flag = false;
//			position = 0;
//		}
    }

    public void searchResultToLoadMoreSuccess(final SearchResult sr) {
        newsAdapter.setHasMoreItems(sr.hasMore());
        newsAdapter.addMore(sr.items());
    }

    public void searchResultToPullDownRefreshSuccess(final SearchResult sr) {
        if (CMainHttp.getInstance().isWifi(getActivity()))
            newsAdapter.setImgAble(true);
        else
            setAdapterImageAble();
        newsAdapter.setHasMoreItems(sr.hasMore());
        newsAdapter.setChannelTime(SystemClock.currentThreadTimeMillis() + "");
        pullToRefreshListView.onRefreshComplete();
        newsAdapter.clearDatas();
        newsAdapter.addDatas(sr.items());
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//
//    }

    @Override
    public void clickRefresh() {
//        htp.searchResult(homePageUrl, 0,15, SYUserManager.getInstance().getToken());
        SearchResultRequest.searchResult(HttpCommon.SRP_SEARCHRESULT_REQUESTID,this,homePageUrl, 0,15, SYUserManager.getInstance().getToken());
    }

    public void updateHasRead(int[] readPos) {
        if (newsAdapter != null) {
            List<SearchResultItem> datas = newsAdapter.getDatas();
            boolean needNotiy = false;
            if (datas != null && datas.size() == readPos.length) {
                for (int i = 0; i < readPos.length; i++) {
                    if (readPos[i] == 1) {
                        datas.get(i).hasRead_$eq(true);
                        needNotiy = true;

                    }
                }
                if (needNotiy)
                    newsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pullToRefreshListView.setAdapter(null);
        newsAdapter = null;
        pullToRefreshListView = null;
        pbHelper.context = null;
        pbHelper = null;
//        htp = null;
    }

    private void getAdapterImageAble() {
        imgAble = getArguments().getBoolean(IMGABLE, true);
        newsAdapter.setImgAble(imgAble);
    }

    private void setAdapterImageAble() {
        imgAble = SettingsManager.getInstance().isLoadImage();
        newsAdapter.setImgAble(imgAble);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.SRP_SEARCHRESULT_REQUESTID:
                searchResultSuccess(new SearchResult(request.<HttpJsonResponse>getResponse()));
                break;
            case HttpCommon.SRP_SEARCHRESULTTOLOADMORE_REQUESTID:
                searchResultToLoadMoreSuccess(new SearchResult(request.<HttpJsonResponse>getResponse()));
                break;
            case HttpCommon.SRP_SEARCHRESULTPULLDOWNREFRESH_REQUESTID:
                searchResultToPullDownRefreshSuccess(new SearchResult(request.<HttpJsonResponse>getResponse()));
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    public void onHttpStart(IRequest request) {
        if (pbHelper.isLoading) {
            pbHelper.goneLoading();
        }
        pullToRefreshListView.onRefreshComplete();
        if (newsAdapter.dataSize() == 0)
            pbHelper.showNetError();
        else if (newsAdapter != null) {
            newsAdapter.notifyDataSetChanged();
        }
    }
}
