package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.adapter.baselistadapter.ListViewAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.im.view.CustomerDialog;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.net.home.HomeListReq;
import com.zhongsou.souyue.net.other.HistoryClear;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.pulltorefresh.CFootView;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 历史浏览页面
 * Created by wangqiang on 15/10/29.
 */
public class HistoryActivity extends BaseActivity implements View.OnClickListener,AbsListView.OnScrollListener {

    private PullToRefreshListView pullToRefreshListView;
    private ListViewAdapter adapter;
    private View goBack;
    protected ProgressBarHelper pbHelp;
    private String mType = HomeBallBean.HISTORY;
    private CFootView footerView;
    private TextView tvTips;

    private int mFootState;
    public static final int FOOT_STATE_LOADING = 0;
    public static final int FOOT_STATE_DONE = 1;
    public static final int FOOT_STATE_ERROR = 2;

    private boolean hasMore;
    private int visibleLast = 0;
    private boolean mPushLoad = true; // 指示是否处于上拉加载的状态，如果正在加载，则不会发出第二次网络请求。
    private  Map<String,String> mTimer = new HashMap<String,String>(); //刷新时间
    private View tvClear;
    private CustomerDialog customerDialog;
    private View btnCancle, btnConfirm;
    private HistoryClear clearReq;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_history);
        initUI();
        setListener();
        loadData();
    }

    private void initUI(){
        goBack = findViewById(R.id.goBack);
        pullToRefreshListView = (PullToRefreshListView)findView(R.id.history_list);
        pullToRefreshListView.setCanPullDown(true);
        footerView = (CFootView)this.getLayoutInflater().inflate(R.layout.list_refresh_footer, null);
        footerView.initView();
        //pullToRefreshListView.getRefreshableView().addFooterView(footerView);
        tvTips = (TextView)this.findViewById(R.id.selfcreate_nodata);
        tvClear = this.findViewById(R.id.tv_clear);
        //setFootDone();
        pbHelp = new ProgressBarHelper(this,
                this.findViewById(R.id.ll_data_loading));
//        pbHelp.setProgressBarClickListener(this);
    }

    private void setFootDone() {
        mFootState = FOOT_STATE_DONE;


//        if(pullToRefreshListView.getRefreshableView().getFooterViewsCount()==0){
//            pullToRefreshListView.getRefreshableView().addFooterView(footerView);
 //       }
        if (pullToRefreshListView != null) {
            ListView view = pullToRefreshListView.getRefreshableView();
            if (view.getFooterViewsCount() > 0) {
               // footerView.setVisibility(View.GONE);
                view.removeFooterView(footerView);
               // footerView.setLoadDoneClick();
            }
        }

    }
    private void setFootLoading() {
        mFootState = FOOT_STATE_LOADING;

        if(pullToRefreshListView.getRefreshableView().getFooterViewsCount()==0){
            pullToRefreshListView.getRefreshableView().addFooterView(footerView);
        }
        if (pullToRefreshListView != null) {
            footerView.setLoading();
            footerView.setVisibility(View.VISIBLE);
            ListView view = pullToRefreshListView.getRefreshableView();
            if (view.getFooterViewsCount() == 0) {
                view.addFooterView(footerView);
            }
        }
    }


    private void setListener(){
        goBack.setOnClickListener(this);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if(adapter==null){
                    return;
                }
                visibleLast = 0;
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.cricle_manage_networkerror);
                    pullToRefreshListView.onRefreshComplete();
                    return;
                }
                String key = mType;
                setTimeValue(key);
                getHomeBallContentList(HttpCommon.HOME_GET_LIST, 0 + "",0+"" , true);

            }
        });
        pullToRefreshListView.setOnScrollListener(this);
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 ) {
                    return;
                }
                if(position>adapter.getCount()){
                    if (mFootState==FOOT_STATE_DONE) {
                        pullToRefresh(true);
                    }
                    return;
                }
                BaseListData item = (BaseListData) parent
                        .getItemAtPosition(position);
                if (item.getViewType() == BaseListData.view_Type_CLICK_REFRESH){
                    pullToRefresh(true);
                    return;
                }
                BaseInvoke invo = item.getInvoke();
                invo.setChan(mType);
                HomePagerSkipUtils.skip(HistoryActivity.this,invo);
//                SearchResultItem item = (SearchResultItem) parent
//                        .getItemAtPosition(position);
//                if(item.url().contains("PicNews")){
//                    //图集
//                    IntentUtil.getToGalleryNews(HistoryActivity.this,item);
//                }else{
//                    HomePagerSkipUtils.skip(HistoryActivity.this,item);
//                }
            }
        });

        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (adapter != null) {
                    String key = mType;
                    String orignTime =  getTimeValue(key);

                    String time = StringUtils
                            .convertDate(orignTime);
                    pullToRefreshListView.setTimeLabel(time);
                }
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialog();
            }
        });
    }

    private void showDialog() {
        if (customerDialog == null) {
            customerDialog = new CustomerDialog(mContext, 40,
                    R.layout.mine_history_dialog, R.style.im_dialog_style, false);
            btnCancle = (Button) customerDialog
                    .findViewById(R.id.dialog_cancel);
            btnConfirm = (Button) customerDialog
                    .findViewById(R.id.dialog_confirm);
            customerDialog.setCanceledOnTouchOutside(false);
        }

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (customerDialog != null)
                    customerDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                customerDialog.dismiss();
                 clearReq = new HistoryClear(HttpCommon.HIISTORY_CLEAR_REQUEST, HistoryActivity.this);
                clearReq.addParameters();
                mMainHttp.doRequest(clearReq);
            }
        });
        customerDialog.show();
    }

    private void loadData(){
        if(!CMainHttp.getInstance().isNetworkAvailable(mContext)){
            pbHelp.showNetError();
            tvClear.setVisibility(View.GONE);
            return;
        }
        pbHelp.showLoading();
        getHomeBallContentList(HttpCommon.HOME_GET_LIST, 0 + "", 0 + "", true);
        adapter = new ListViewAdapter(this,null);
        HomeListManager manager = new HomeListManager(this);
        adapter.setManager(manager);
        manager.setView(adapter, pullToRefreshListView.getRefreshableView());
        pullToRefreshListView.setAdapter(adapter);

//        pullToRefreshListView.onUpdateTime(adapter.getChannelTime());
    }

    private void getHomeBallContentList(int _pullType, String lastID, String indexId, boolean refresh) {
        switch (_pullType) {
            case HttpCommon.HOME_GET_LIST://开始进来
//                http.getHomeBallContentList(_pullType, mType, 0, "", "",
//                        0 + "", 0+"", refresh, this);
                HomeListReq homeListReq1 = new HomeListReq(HttpCommon.HOME_GET_LIST,this);
                homeListReq1.setParams(mType, "", "",0 + "", 0+"");
                homeListReq1.setmForceRefresh(refresh);
                mMainHttp.doRequest(homeListReq1);
                break;

            case HttpCommon.HOME_LIST_PULL://下拉刷新

//                http.getHomeBallContentList(_pullType, mType, 0, "", "",
//                        lastID, indexId, refresh, this);
                HomeListReq homeListReq2 = new HomeListReq(HttpCommon.HOME_LIST_PULL,this);
                homeListReq2.setParams(mType, "", "",lastID, indexId);
                homeListReq2.setmForceRefresh(refresh);
                mMainHttp.doRequest(homeListReq2);
                break;

            case HttpCommon.HOME_LIST_PUSH://上拉刷新
                if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    setFootLoading();
//                    http.getHomeBallContentList(_pullType, mType, 0, "", "",
//                            lastID, indexId, refresh, this);
                    HomeListReq homeListReq3 = new HomeListReq(HttpCommon.HOME_LIST_PUSH,this);
                    homeListReq3.setParams(mType, "", "",lastID, indexId);
                    homeListReq3.setmForceRefresh(refresh);
                    mMainHttp.doRequest(homeListReq3);
                }
                break;
        }

    }

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        int id = _request.getmId();

        switch (id) {
            case HttpCommon.HOME_GET_LIST:
            case HttpCommon.HOME_LIST_PULL:
            case HttpCommon.HOME_LIST_PUSH:

                doResponse(id, (List<Object>) _request.getResponse());
                pullToRefreshListView.onRefreshComplete();
                adapter.notifyDataSetChanged();
                break;

            case HttpCommon.HIISTORY_CLEAR_REQUEST:
                if(adapter!=null){
                    adapter.clear();
                    tvClear.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public static final int UPDATE_ALL = 1;


    private void doResponse(int id, List<Object> result) {
//      解析放到非UI线程了
        List<BaseListData> topList = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_TOPLIST);
        List<BaseListData> focusList= (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_FOCUS);
        List<BaseListData> newsList= (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_NEWSLIST);
        switch (id) {
            case HttpCommon.HOME_GET_LIST:// 首次进入

                hasMore = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                if (!hasMore) {
                    setFootDone();
                }
                setDatas(topList,focusList,newsList);
                List<BaseListData> list = adapter.getDatas();

                if(list==null || list.size()==0){
                    pullToRefreshListView.setVisibility(View.GONE);
                    tvTips.setText("您还没有浏览记录");
                    tvClear.setVisibility(View.GONE);
                    tvTips.setVisibility(View.VISIBLE);
                    pbHelp.showNoData();
                }
                break;
            case HttpCommon.HOME_LIST_PULL://下拉刷新
                hasMore = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                if (!hasMore) {
                    setFootDone();
                }
                setDatas(topList,focusList,newsList);
                break;

            case HttpCommon.HOME_LIST_PUSH: //上拉
                hasMore = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                mPushLoad = true;
                if (newsList.size() > 0) {
                    adapter.addLast(newsList);
                } else {
                    if (!hasMore) {
                        setFootDone();
                    }
                }
                break;
        }
        if (adapter.getCount() == 0) {
            pbHelp.showNoData();
        }else{
            pbHelp.goneLoading();
        }
    }


    private void setDatas(List<BaseListData> toplist,List<BaseListData> focusList,List<BaseListData> newslist){
        if (focusList!=null&&focusList.size()>0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            toplist.add(0, bean);
        }
        adapter.setData(newslist);
        adapter.addFirst(toplist);
    }


    @Override
    public void onHttpError(IRequest _request) {
        super.onHttpError(_request);
        pullToRefreshListView.onRefreshComplete();
        pbHelp.goneLoading();
        if(adapter.getCount()==0){
            pullToRefreshListView.setVisibility(View.GONE);
            tvTips.setText("您还没有浏览记录");
            tvTips.setVisibility(View.VISIBLE);
            return ;
        }
    }

    private String getTimeValue(String key){
        if(!mTimer.containsKey(key)){
            return "";
        }else{
            return mTimer.get(key);
        }

    }
//    Error:java: Internal compiler error: java.lang.ArrayIndexOutOfBoundsException: -1 at org.eclipse.jdt.internal.compiler.codegen.StackMapFrame.addStackItem(StackMapFrame.java:92)

    private void setTimeValue(String key){
        long time = System.currentTimeMillis();
        mTimer.put(key,time+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBack:
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.right_out);
                break;
            default:
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (adapter == null) {
            return;
        }
        int itemsLastIndex = adapter.getCount();
        if (itemsLastIndex < 0) {
            return;
        }

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast >= itemsLastIndex && mPushLoad) {
            String lastId = getLastId();
            mPushLoad = false;
            setFootLoading();
            getHomeBallContentList(HttpCommon.HOME_LIST_PUSH, lastId, "" + 0, true);


        }

    }


    public String getLastId(){
        String lastId = "";
        List<BaseListData> datas = adapter.getDatas();
        if (datas == null){
            return "0";
        }
        int count = datas.size();
        if (count == 0){
            return "0";
        }
        BaseListData data = datas.get(datas.size()-1);
        lastId = data.getId()+"";
        return lastId;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        visibleLast = firstVisibleItem + visibleItemCount;
    }

    public void pullToRefresh(boolean force) {
        pullToRefreshListView.startRefresh();

    }

}
