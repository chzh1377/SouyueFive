package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.MySouYueRssAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.search.SearchResultRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * @author zcz
 *         搜悦报刊详情界面
 */
public class SouYueRssActivity extends BaseActivity implements LoadingDataListener,
         ProgressBarClickListener {
    public PullToRefreshListView pullToRefreshListView;
    public MySouYueRssAdapter newsAdapter;// 数据展示的adapter
    private ProgressBarHelper pbHelper;
//    private Http htp;//
    private SuberedItemInfo item;
    private boolean imgAble;
    private TextView ib_left;

    //private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.souyuerssactivity);
        item = (SuberedItemInfo) getIntent().getSerializableExtra("item");
        //index = getIntent().getIntExtra("index", 0);
        initView();
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.rss_listview);
//        htp = new Http(this);
//        htp.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_NETWORK);
        if (newsAdapter == null)
            newsAdapter = new MySouYueRssAdapter(this);
        newsAdapter.setLoadingDataListener(this);
        pbHelper = new ProgressBarHelper(this,
                findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(this);
        initEvent();

//		getAdapterImageAble();//需要问imageAble
        setAdapterImageAble();
        newsAdapter.isRss = true;
        //loadDataMore(0, "1");
        pullToRefreshListView.setAdapter(newsAdapter);
//        htp.searchResult(item.getUrl(), 0,15, SYUserManager.getInstance().getToken());
        SearchResultRequest.searchResult(HttpCommon.SRP_SEARCHRESULT_REQUESTID,
                this,item.getUrl(),0,15,SYUserManager.getInstance().getToken());

    }

    @Override
    public void onHttpResponse(IRequest request) {
       int id = request.getmId();
        switch (id){
            case HttpCommon.SRP_SEARCHRESULT_REQUESTID:
                searchResultSuccess(new SearchResult(request.<HttpJsonResponse>getResponse()));
                break;
            case HttpCommon.SRP_SEARCHRESULTPULLDOWNREFRESH_REQUESTID:
                searchResultToPullDownRefreshSuccess(new SearchResult(request.<HttpJsonResponse>getResponse()));
                break;
            case HttpCommon.SRP_SEARCHRESULTTOLOADMORE_REQUESTID:
                searchResultToLoadMoreSuccess(new SearchResult(request.<HttpJsonResponse>getResponse()));
                break;
            default:
                super.onHttpResponse(request);
                break;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (newsAdapter != null) {
            newsAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        ib_left = (TextView) findViewById(R.id.ib_left);
        ib_left.setText(item.getKeyword());
    }

    private void initEvent() {
        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//				Intent i = new Intent();
                // Bundle bundle = new Bundle();
                // bundle.putSerializable("list", (ArrayList<SearchResultItem>)
                // newsAdapter.getDatas());
//				newsAdapter.updateView(position - 1, lv);
//				IntentCacheHelper.getInstance(List.class).setObject(
//						newsAdapter.getDatas());
//				IntentCacheHelper.getInstance(List.class).setListFlag(true);
//				i.putExtra("position", position - 1);
//				i.setClass(SouYueRssActivity.this, ReadabilityActivity.class);
//				// i.putExtras(bundle);
//				startActivityForResult(i, 0);
                IntentUtil.skipDetailPage(SouYueRssActivity.this, newsAdapter.getDatas().get(position - 1), 0);
                SouYueRssActivity.this.overridePendingTransition(
                        R.anim.left_in, R.anim.left_out);
                newsAdapter.getDatas().get(position - 1).hasRead_$eq(true);
            }

        });
        pullToRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 重新请求数据
                        newsAdapter.isRefresh = true;
//                        htp.searchResultToPullDownRefresh(item.getUrl(), 0 + "",15,
//                                SYUserManager.getInstance().getToken());
                        SearchResultRequest.searchResultPullDown(HttpCommon.SRP_SEARCHRESULTPULLDOWNREFRESH_REQUESTID,SouYueRssActivity.this,
                                item.getUrl(), 0 + "",15,
                                SYUserManager.getInstance().getToken());
                    }

                });
        pullToRefreshListView
                .setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
                    @Override
                    public void onTimeRefresh() {
                        if (null != newsAdapter.getChannelTime())
                            pullToRefreshListView.onUpdateTime(StringUtils
                                    .convertDate(newsAdapter.getChannelTime()));
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            int readPos[] = data.getIntArrayExtra("readPos");
            if (readPos != null) {
                updateHasRead(readPos);
            }
        }
    }


    public void onActionsButtonClick(View view) {
        // TODO Auto-generated method stub
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void onSearchClick(View view) {
        IntentUtil.openSearchActivity(this);
    }

/*	public void onSubcibeClick(View view){
		Bundle bundle = new Bundle();
		//bundle.putInt(MySubscribeListActivity.INTENT_INDEX, index);
		IntentUtil.openManagerAcitivity(this,MySubscribeListActivity.class,bundle);
		finish();
	}*/


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
    public void loadDataMore(long start, String type) {
        Slog.d("c allback","start page--------"+start);
        if (newsAdapter.getHasMoreItems()) {
//            htp.searchResultToLoadMore(item.getUrl(), start,15, "", SYUserManager
//                    .getInstance().getToken(), AbstractAQuery.CACHE_POLICY_NETWORK);
            SearchResultRequest.searchResultLoadMore(HttpCommon.SRP_SEARCHRESULTTOLOADMORE_REQUESTID,this,
                    item.getUrl(), start,15, "", SYUserManager
                    .getInstance().getToken());
        }
    }

    public void searchResultToLoadMoreSuccess(final SearchResult sr) {
        newsAdapter.setHasMoreItems(sr.hasMore());
        newsAdapter.addMore(sr.items());
    }

    public void searchResultSuccess(final SearchResult sr) {
        if (CMainHttp.getInstance().isWifi(this))
            newsAdapter.setImgAble(true);
        else
            setAdapterImageAble();
        newsAdapter.setChannelTime(SystemClock.currentThreadTimeMillis() + "");
        pbHelper.goneLoading();
        newsAdapter.setHasMoreItems(sr.hasMore());
        newsAdapter.addDatas(sr.items());
        if (sr.hasExpired() && CMainHttp.getInstance().isNetworkAvailable(this))
            pullToRefreshListView.startRefresh();
        // if(flag){
        // outerTabPageIndicator.setCurrentItem(position);
        // flag = false;
        // position = 0;
        // }
        if(newsAdapter.getCount()==0){
            pbHelper.showNoData();
        }
    }

    public void searchResultToPullDownRefreshSuccess(final SearchResult sr) {
        if (CMainHttp.getInstance().isWifi(this))
            newsAdapter.setImgAble(true);
        else
            setAdapterImageAble();
        newsAdapter.setHasMoreItems(sr.hasMore());
        newsAdapter.setChannelTime(SystemClock.currentThreadTimeMillis() + "");
        pullToRefreshListView.onRefreshComplete();
        newsAdapter.clearDatas();
        newsAdapter.addDatas(sr.items());
    }

    @Override
    public void clickRefresh() {
//        htp.searchResult(item.getUrl(), 0,15, SYUserManager.getInstance().getToken());
        SearchResultRequest.searchResult(HttpCommon.SRP_SEARCHRESULT_REQUESTID,
                this,item.getUrl(), 0,15, SYUserManager.getInstance().getToken());
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        pullToRefreshListView.onRefreshComplete();
//        if (newsAdapter.dataSize() == 0)
//            pbHelper.showNetError();
//        else if (newsAdapter != null) {
//            newsAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onHttpError(IRequest request) {
        pullToRefreshListView.onRefreshComplete();
        if (newsAdapter.dataSize() == 0)
            pbHelper.showNetError();
        else if (newsAdapter != null) {
            newsAdapter.notifyDataSetChanged();
        }
    }
    // private void getAdapterImageAble(){
    // imgAble = getArguments().getBoolean(IMGABLE, true);
    // newsAdapter.setImgAble(imgAble);
    // }

    private void setAdapterImageAble() {
        imgAble = SettingsManager.getInstance().isLoadImage();
        newsAdapter.setImgAble(imgAble);
    }
}
