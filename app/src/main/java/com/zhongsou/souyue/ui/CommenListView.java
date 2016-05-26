package com.zhongsou.souyue.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.adapter.baselistadapter.ListViewAdapter;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.net.news.CommonListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.pulltorefresh.CFootView;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description: 公共的列表 视图
 * 1.setNetUrl 设置网络请求的url  ，如果设置了 url 即走统一的格式
 * 2,setCommonListReq 统一继承 CommonListReq
 * 2.mType 跳转的数据类型    // HomeBallBean.HISTORY;
 * 3. 是否增量更新
 * 4.setPullDown 是否允许下拉刷新
 * @auther: qubian
 * @data: 2016/4/25.
 */
public class CommenListView extends RelativeLayout implements
        PullToRefreshBase.OnRefreshListener,
        PullToRefreshBase.OnTimeRefreshListener,
        AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener,
        View.OnClickListener,
        ProgressBarHelper.ProgressBarClickListener,
        IVolleyResponse {
    private static final String LASTID_PULL = "0";
    private View rootView;
    private Activity mActivity;
    private PullToRefreshListView pullToRefreshListView;
    private ListViewAdapter adapter;
    private CFootView footerView;
    protected ProgressBarHelper pbHelp;
    private int visibleLast = 0;
    private boolean mPushLoad = true; // 指示是否处于上拉加载的状态，如果正在加载，则不会发出第二次网络请求。
    private Map<String, String> mTimer; //刷新时间
    /**
     * 从外部设置的数据
     */
    private String netUrl; // 网络请求
    private String mType;  // 数据类型 - 用于跳转
    private boolean hasMore; //网络返回 --》是否拥有更多
    private CommonListReq commonListReq; // 网络请求
    private Map<String,String> otherParams; // 其他参数
    private DealNetCallBack dealNetCallBack;// 网络请求 自己处理回调
    private HomeListManager manager;

    public CommenListView(Activity context,String netUrl) {
        super(context);
        this.netUrl =netUrl;
        initView(context);
        setViewListener();
        loadData();
    }
    private void initView(Activity context) {
        mActivity = context;
        rootView =View.inflate(context, R.layout.comment_listview, this);
        pullToRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.comment_pull_listview);
        pullToRefreshListView.setCanPullDown(true);
        footerView = (CFootView) View.inflate(context, R.layout.list_refresh_footer, null);
        footerView.initView();
        pbHelp = new ProgressBarHelper(context, rootView.findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);
        pbHelp.goneLoading();
        mTimer = new HashMap<String, String>();
    }

    private void setViewListener() {
        pullToRefreshListView.setOnScrollListener(this);
        pullToRefreshListView.setOnTimeRefreshListener(this);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setOnRefreshListener(this);
    }

    private void loadData() {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            pbHelp.showNetError();
            return;
        }
        if(StringUtils.isNotEmpty(netUrl)|| commonListReq != null)
        {
            pbHelp.showLoading();
            getCommentList(HttpCommon.COMMEN_LIST_LIST_PULL, LASTID_PULL, true);
        }
        adapter = new ListViewAdapter(mActivity, null);
        manager = new HomeListManager(mActivity);
        adapter.setManager(manager);
        manager.setView(adapter, pullToRefreshListView.getRefreshableView());
        pullToRefreshListView.setAdapter(adapter);
    }

    /**
     *
     * @param pullType  COMMEN_LIST_LIST_PULL COMMEN_LIST_LIST_MORE
     * @param lastID
     * @param refresh
     */
    private void getCommentList(int pullType, String lastID, boolean refresh) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            pbHelp.showNetError();
            return;
        }
        switch (pullType) {
            case HttpCommon.COMMEN_LIST_GET_LIST:
            case HttpCommon.COMMEN_LIST_LIST_PULL://下拉刷新
                setFootGone();
                break;
            case HttpCommon.COMMEN_LIST_LIST_MORE://上拉刷新
                setFootLoading();
                break;
        }
        if(commonListReq==null) //如果没有传递请求，则默认的请求方式
        {
            CommonListReq req = new CommonListReq(pullType, netUrl, this);
            req.setParams(lastID, refresh);
            if(otherParams!=null)
            {
                req.setOtherParams(otherParams);
            }
            req.setmForceRefresh(refresh);
            CMainHttp.getInstance().doRequest(req);
        }else
        {
            CMainHttp.getInstance().doRequest(commonListReq);
        }

    }

    private void setFootGone() {
        if (pullToRefreshListView != null) {
            ListView view = pullToRefreshListView.getRefreshableView();
            if (view.getFooterViewsCount() > 0) {
                view.removeFooterView(footerView);
            }
        }
    }

    private void setFootLoading() {
        if (pullToRefreshListView.getRefreshableView().getFooterViewsCount() == 0) {
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


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (adapter == null) {
            return;
        }
        visibleLast = 0;
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            UIHelper.ToastMessage(mActivity,
                    R.string.cricle_manage_networkerror);
            pullToRefreshListView.onRefreshComplete();
            return;
        }
        String key = mType;
        setTimeValue(key);
        getCommentList(HttpCommon.COMMEN_LIST_LIST_PULL, LASTID_PULL, true);
    }

    private void setTimeValue(String key) {
        long time = System.currentTimeMillis();
        mTimer.put(key, time + "");
    }

    private String getTimeValue(String key) {
        if (!mTimer.containsKey(key)) {
            return "";
        } else {
            return mTimer.get(key);
        }
    }

    @Override
    public void onTimeRefresh() {
        if (adapter != null) {
            String key = mType;
            String orignTime = getTimeValue(key);
            String time = StringUtils.convertDate(orignTime);
            pullToRefreshListView.setTimeLabel(time);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        BaseListData item = (BaseListData) parent.getItemAtPosition(position);
        if (item.getViewType() == BaseListData.view_Type_CLICK_REFRESH) {
            pullToRefresh(true);
            return;
        }
        BaseInvoke invo = item.getInvoke();
        invo.setChan(mType);
        HomePagerSkipUtils.skip(mActivity, invo);
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
            getCommentList(HttpCommon.COMMEN_LIST_LIST_MORE, lastId, true);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        visibleLast = firstVisibleItem + visibleItemCount;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void clickRefresh() {
        getCommentList(HttpCommon.COMMEN_LIST_LIST_PULL, LASTID_PULL, true);
    }

    private String getLastId() {
        String lastId = "";
        List<BaseListData> datas = adapter.getDatas();
        if (datas == null) {
            return "0";
        }
        int count = datas.size();
        if (count == 0) {
            return "0";
        }
        BaseListData data = datas.get(datas.size() - 1);
        lastId = data.getId() + "";
        return lastId;
    }

    private void pullToRefresh(boolean force) {
        pullToRefreshListView.startRefresh();
    }

    @Override
    public void onHttpResponse(IRequest request) {
        int id = request.getmId();
        if(dealNetCallBack!=null) // 自己处理网络请求的返回
        {
            // 回调有3：
            // 1 基础数据
            // 2 frist 顶层数据
            // 3 是否拥有更多
            List adpterData= dealNetCallBack.getAdapterData(request);
            List adpterFirstData= dealNetCallBack.getAdapterFirstData(request);
            boolean hasMore =dealNetCallBack.isHasMore(request);
            dealWithCallBack(id,adpterData,adpterFirstData,hasMore);
            pullToRefreshListView.onRefreshComplete();
            adapter.notifyDataSetChanged();
            return;
        }
        switch (id) {
            case HttpCommon.COMMEN_LIST_GET_LIST:
            case HttpCommon.COMMEN_LIST_LIST_PULL:
            case HttpCommon.COMMEN_LIST_LIST_MORE:
                doResponse(id, (List<Object>) request.getResponse());
                pullToRefreshListView.onRefreshComplete();
                adapter.notifyDataSetChanged();
                break;
        }
    }
    private void dealWithCallBack(int id,List adpterData,List adpterFirstData,boolean hasMore)
    {
        switch (id) {
            case HttpCommon.COMMEN_LIST_GET_LIST:
            case HttpCommon.COMMEN_LIST_LIST_PULL://下拉刷新
                if (!hasMore) {
                    setFootGone();
                }
                adapter.setData(adpterData);
                adapter.addFirst(adpterFirstData);
                break;
            case HttpCommon.COMMEN_LIST_LIST_MORE: //上拉
                mPushLoad = true;
                if (adpterData.size() > 0) {
                    adapter.addLast(adpterData);
                } else {
                    if (!hasMore) {
                        setFootGone();
                    }
                }
                break;
        }
        if (adapter.getCount() == 0) {
            pbHelp.showNoData();
        } else {
            pbHelp.goneLoading();
        }
    }

    private void doResponse(int id, List<Object> result) {
        List<BaseListData> topList = (List<BaseListData>) result.get(CommonListReq.COMMENT_LIST_DATA_INDEX_TOPLIST);
        List<BaseListData> focusList = (List<BaseListData>) result.get(CommonListReq.COMMENT_LIST_DATA_INDEX_FOCUS);
        List<BaseListData> newsList = (List<BaseListData>) result.get(CommonListReq.COMMENT_LIST_DATA_INDEX_NEWSLIST);
        List<BaseListData> extendsList = (List<BaseListData>) result.get(CommonListReq.COMMENT_LIST_DATA_INDEX_EXTENDS);
        hasMore = (Boolean) result.get(CommonListReq.COMMENT_LIST_DATA_INDEX_HASMORE);
        switch (id) {
            case HttpCommon.COMMEN_LIST_GET_LIST:
            case HttpCommon.COMMEN_LIST_LIST_PULL://下拉刷新
                if (!hasMore) {
                    setFootGone();
                }
                setDatas(topList, focusList, newsList,extendsList);
                break;
            case HttpCommon.COMMEN_LIST_LIST_MORE: //上拉
                mPushLoad = true;
                if (newsList.size() > 0) {
                    adapter.addLast(newsList);
                } else {
                    if (!hasMore) {
                        setFootGone();
                    }
                }
                break;
        }
        if (adapter.getCount() == 0) {
            pbHelp.showNoData();
        } else {
            pbHelp.goneLoading();
        }
    }


    private void setDatas(List<BaseListData> toplist, List<BaseListData> focusList, List<BaseListData> newslist,List<BaseListData> extendsList) {
        if (focusList != null && focusList.size() > 0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            toplist.add(0, bean);
        }
        if(extendsList!=null && extendsList.size()>0)
        {
            newslist.addAll(0,extendsList);
        }
        adapter.setData(newslist);
        adapter.addFirst(toplist);
    }


    @Override
    public void onHttpError(IRequest request) {
        int id = request.getmId();
        switch (id) {
            case HttpCommon.COMMEN_LIST_GET_LIST:
            case HttpCommon.COMMEN_LIST_LIST_PULL:
            case HttpCommon.COMMEN_LIST_LIST_MORE:
                pullToRefreshListView.onRefreshComplete();
                pbHelp.goneLoading();
                break;
        }
    }


    @Override
    public void onHttpStart(IRequest request) {

    }
    public interface DealNetCallBack
    {
        List getAdapterData(IRequest request);

        List getAdapterFirstData(IRequest request);

        boolean isHasMore(IRequest request);
    }
    /**
     * 是否使用下拉刷新 false : 禁止下拉刷新；true ：允许下拉刷新
     * @param pullDown
     */
    public void setPullDown(boolean pullDown) {
        pullToRefreshListView.setCanPullDown(pullDown);
    }

    /**
     * 设置其他参数
     * @param otherParams
     */
    public void setOtherParams(Map<String, String> otherParams) {
        this.otherParams = otherParams;
    }

    /**
     *  ji仅需要设置URL
     * @param netUrl
     */
    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
        getCommentList(HttpCommon.COMMEN_LIST_LIST_PULL,LASTID_PULL, true);
    }

    /**
     * 设置跳转的chanal
     *
     * @param mType
     */
    public void setmType(String mType) {
        this.mType = mType;
    }

    /**
     * shezhi
     * @param commonListReq
     */
    public void setCommonListReq(CommonListReq commonListReq) {
        this.commonListReq = commonListReq;
    }

    /**
     * 处理网络的回调
     * @param dealNetCallBack
     */
    public void setDealNetCallBack(DealNetCallBack dealNetCallBack) {
        this.dealNetCallBack = dealNetCallBack;
    }

    /**
     * 设置数据通知
     */
    public void setListData(List<BaseListData> toplist,List<BaseListData> newslist,boolean hasMore){
        adapter.setData(newslist);
        adapter.addFirst(toplist);
        if (!hasMore) {
            setFootGone();
        }
    }

    public void gotoFirstItem(){
        if (pullToRefreshListView != null) {
            ListView refreshableView = pullToRefreshListView.getRefreshableView();
            if (refreshableView != null) {
                refreshableView.setSelection(0);
            }
        }
    }

    public HomeListManager getManager(){
        return manager;
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }
}
