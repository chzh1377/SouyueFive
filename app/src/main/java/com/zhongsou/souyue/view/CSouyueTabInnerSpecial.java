package com.zhongsou.souyue.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.adapter.baselistadapter.ListViewAdapter;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.homepage.HomeList;
import com.zhongsou.souyue.db.homepage.UserHomeList;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.net.home.HomeListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.pulltorefresh.CFootView;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.BroadCastUtils;
import com.zhongsou.souyue.utils.CVariableKVO;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LocalBroadCastHelper;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页下面的列表
 * Created by lvqiang on 15/6/13.
 */
@SuppressLint("NewApi")
public class CSouyueTabInnerSpecial extends AFragmentBaseView<HomeBallBean> implements
        ProgressBarHelper.ProgressBarClickListener, AbsListView.OnScrollListener, IVolleyResponse, View.OnClickListener, ProgressBarHelper.OnLoadingListener {

    /**
     * 首页请求详情的requestCode
     */

    public static final int NEW_TAB_HEIGHT = 35;

    public static final int PAGE_SIZE = 20;//分页大小

    public static final int FOOT_STATE_LOADING   = 0;
    public static final int FOOT_STATE_DONE      = 1;
    public static final int FOOT_STATE_ERROR     = 2;
    public static final int FOOT_STATE_INVISIBLE = 3;

    public static final int HOT_NOT = 0;
    public static final int HOT_IS  = 1;//列表中热标签


    protected ProgressBarHelper pbHelp;

    private   HomeBallBean homeBallBean; //表示当前页面的分类信息，最有用的就是category了。
    protected ImageLoader  imageLoader;

    private ListViewAdapter       adapter;
    private HomeListManager       mHomeListManager;
    private boolean               hasMore; //是否包含更多元素（是否又下页？貌似没什么卵用）
    private boolean               mPushLoad; // 指示是否处于上拉加载的状态，如果正在加载，则不会发出第二次网络请求。
    private PullToRefreshListView pullToRefreshListView;
    private LinearLayout          llGroupNoData;
    private CFootView             footerView;
    private int                   mFootState;

    protected TextView  mTextNew;
    private   CMainHttp mMainHttp; //http请求框架，所有请求都通过这个发出
    private int visibleLast = 0;
    private boolean mLoadDataFromLocal;
    private boolean refreshing;
    private boolean mIsInLoading;
    private String  mSrpId;
    private long    mId;//球球id
    private String  mType;//球球类型
    HomePageDBHelper mDBHelper;
    private   Animation mTextNewInAnim;
    protected Animation mTextNewOutAnim;

    protected int               mShowCount;
    private   BroadcastReceiver broadcast;
    protected boolean           mIsDestory;
    protected boolean           mIsReadingCache;
    protected CVariableKVO      mKVO;
    private   SouyueTabFragment mFragment;
    private static Map<String, String> mTimer = new HashMap<String, String>(); //刷新时间


    public CSouyueTabInnerSpecial(Context context) {
        super(context);
    }

    public CSouyueTabInnerSpecial(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CSouyueTabInnerSpecial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initView() {
        imageLoader = ImageLoader.getInstance();

        initView(this);
    }

    @Override
    public void unInitView() {
        mIsInLoading = false;
        resetNewsTab();
    }

    @Override
    public void setData(HomeBallBean homeball, SouyueTabFragment fragment) {
        //滑动到当前页面 ，再次显示没有数据。
//        setGroupNoDataState();
        mFragment = fragment;
        homeBallBean = homeball;
        mSrpId = homeBallBean.getSrpId() == null ? homeBallBean.getId() + "" : homeBallBean.getSrpId();
        mId = homeBallBean.getId();
        mType = homeBallBean.getCategory();
        mIsDestory = false;
        pullToRefreshListView.resetTitle();
        mShowCount = 0;
        resetNewsTab();
        initData();

    }


    private void initData() {
        pbHelp.showLoadingUI();
        pullToRefreshListView.setVisibility(VISIBLE);
        //如果是专题页，不显示下划线
        ListView lv = pullToRefreshListView.getRefreshableView();
        if (homeBallBean.getCategory().equals(HomeBallBean.SPECIAL_TOPIE)) {
            lv.setDivider(null);
        } else if (isCard(homeBallBean)) {
            lv.setDivider(getResources().getDrawable(R.drawable.home_list_devider_joke));
            lv.setDividerHeight(DeviceUtil.dip2px(mActivity, 5));
        } else {
            //防止view复用的时候发生诡异的情况
            pullToRefreshListView.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.listview_divider));
            lv.setDividerHeight(1);

        }
        /*
        两个事件同事完成后做此回调（外部事件）
        1、缓存取出完成
        2、当前页面完全显示到页面上完成
         */
        mKVO = new CVariableKVO(2, new CVariableKVO.KVOCallback() {
            @Override
            public void doCallback() {
                if (pullToRefreshListView != null && adapter.getCount() > 0) {
                    pbHelp.goneLoadingUI();
                    pullToRefreshListView.startRefresh();
                } else {
                    pbHelp.showLoadingUI();
                    refreshing = true;
                    getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PULL, 0 + "", getFirstId(), true);
                }
            }
        });
        pullToRefreshListView.setVisibility(View.VISIBLE);
        adapter = new ListViewAdapter(mContext, null);
        mHomeListManager = new HomeListManager(mActivity);
        mHomeListManager.setView(adapter, pullToRefreshListView.getRefreshableView());
        mHomeListManager.setChannel(mType);
        mHomeListManager.setBallTitle(homeBallBean.getTitle());
        adapter.setManager(mHomeListManager);
        initCache();
        pullToRefreshListView.setAdapter(adapter);
    }

    public String getFirstId() {
        String             lastId = "";
        List<BaseListData> datas  = adapter.getDatas();
        if (datas == null) {
            return "0";
        }
        int count = datas.size();
        if (count == 0) {
            return "0";
        }
//        BaseListData data = datas.get(0);
        for (BaseListData data : datas) {//从前往后遍历,找到非置顶数据去更新
            if (!data.isLocalTop()) {
                lastId = data.getId() + "";
                break;
            }
        }
        return lastId;
    }

    public String getLastId() {
        String             lastId = "";
        List<BaseListData> datas  = adapter.getDatas();
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setDestory(boolean des) {
        mIsDestory = des;
        pbHelp.showLoadingUI();
        pullToRefreshListView.setVisibility(INVISIBLE);
        resetNewsTab();
    }


    private void initCache() {
        mIsReadingCache = true;
        if (!isSmartUpdate(mType)) {//srp页面没有增量刷新，所以用http缓存
            mKVO.reset(0);
            getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST, 0 + "", 0 + "", false);
            mPushLoad = true;
            mLoadDataFromLocal = false;
            hasMore = true;
            setFootDone();
        } else {
            getCache(Utils.MAX_LONG + "", new DataCallBack<List<BaseListData>>() {
                @Override
                public void callback(List<BaseListData> items) {
                    int l = items == null ? 0 : items.size();
                    if (l == 0) {
                        //显示loading
                        if (pbHelp != null) {
                            pbHelp.showLoadingUI();
                            //暂时先修复一个首页球球过多加载不出来的bug
//                            if (mKVO.getFalg() == 2) {
//                                getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PULL, 0 + "", getFirstId(), true);
//                            }
                        }
                    } else {
                        setFootDone();
                        hasMore = true;
                        mPushLoad = true;
                        if (pbHelp != null) {
                            pbHelp.goneLoadingUI();
                        }
                        adapter.setData(items);
                        adapter.notifyDataSetChanged();
                    }
                    mKVO.doDone();
                    mIsReadingCache = false;
                }
            });
        }

    }

    private void getHomeBallContentList(int _pullType, String lastID, String indexId, boolean refresh) {
        if(HomeBallBean.GROUP_NEWS.equals(mType)){
            mSrpId = homeBallBean.getId() + "";
        }
        switch (_pullType) {
            case CMainHttp.HTTP_REQUEST_HOMELIST://开始进来
                //1、取第一页缓存,先显示出来
//                setFootLoading();
                HomeListReq homeListReq1 = new HomeListReq(HttpCommon.HOME_GET_LIST, this);
                homeListReq1.setParams(mType, homeBallBean.getKeyword(), mSrpId, 0 + "", indexId);
                homeListReq1.addKeyValueTag("type", mType);
                homeListReq1.addKeyValueTag("id", mId);
                homeListReq1.setmForceRefresh(refresh);
                mMainHttp.doRequest(homeListReq1);
//                mMainHttp.getHomeBallContentList(_pullType, mType, mId, homeBallBean.getKeyword(), mSrpId,
//                        0 + "", indexId, refresh, this);
                break;
            case CMainHttp.HTTP_REQUEST_HOMELIST_PULL://下拉刷新
                mLoadDataFromLocal = false;
                if (mType.equals("headline")) {   //我的头条下拉刷新
                    UmengStatisticUtil.onEvent(getContext(), UmengStatisticEvent.HOME_HEADLINE_PULL_DOWN_CLICK);     //Umeng headline
                } else {
                    UmengStatisticUtil.onEvent(getContext(), UmengStatisticEvent.HOME_PULL_DOWN_CLICK);     //Umeng others
                }
                //非增量的类型下拉刷新时的请求参数应该和第一次请求列表的参数一致，即index都为0
                if (!isSmartUpdate(mType)) {
                    indexId = "0";
                }
//                setFootLoading();
                HomeListReq homeListReq2 = new HomeListReq(HttpCommon.HOME_LIST_PULL, this);
                homeListReq2.setParams(mType, homeBallBean.getKeyword(), mSrpId, lastID, indexId);
                homeListReq2.addKeyValueTag("type", mType);
                homeListReq2.addKeyValueTag("id", mId);
                homeListReq2.setmForceRefresh(refresh);
                mMainHttp.doRequest(homeListReq2);
//                mMainHttp.getHomeBallContentList(_pullType, mType, mId, homeBallBean.getKeyword(), mSrpId,
//                        lastID, indexId, refresh, this);
                break;
            case CMainHttp.HTTP_REQUEST_HOMELIST_PUSH://上拉刷新
                if (CMainHttp.getInstance().isNetworkAvailable(mContext) && !mLoadDataFromLocal) {
                    if (mType.equals("headline")) {    //我的头条上拉刷新
                        UmengStatisticUtil.onEvent(getContext(), UmengStatisticEvent.HOME_HEADLINE_PULL_UP_CLICK);     //Umeng headline
                    } else {
                        UmengStatisticUtil.onEvent(getContext(), UmengStatisticEvent.HOME_PULL_UP_CLICK);     //Umeng others
                    }
                    setFootLoading();
                    HomeListReq homeListReq3 = new HomeListReq(HttpCommon.HOME_LIST_PUSH, this);
                    homeListReq3.setParams(mType, homeBallBean.getKeyword(), mSrpId, lastID, indexId);
                    homeListReq3.addKeyValueTag("type", mType);
                    homeListReq3.addKeyValueTag("id", mId);
                    homeListReq3.setmForceRefresh(refresh);
                    mMainHttp.doRequest(homeListReq3);
                } else if (!isSmartUpdate(mType)) {
                    mPushLoad = true;
                    setFootError();
                } else {
                    mLoadDataFromLocal = true;
                    setFootLoading();
                    getCache(lastID + "", new DataCallBack<List<BaseListData>>() {

                        @Override
                        public void callback(List<BaseListData> data) {
                            if (data.size() == 0) {
                                //显示loading
                                setFootDone();
                            } else {
                                adapter.addLast(data);
                            }
                            mPushLoad = true;
                        }
                    });

                }
                break;
        }

    }

    private void setFootLoading() {
        mFootState = FOOT_STATE_LOADING;
        if (pullToRefreshListView != null) {
            footerView.setLoading();
            footerView.setVisibility(VISIBLE);
            ListView view = pullToRefreshListView.getRefreshableView();
            if (view.getFooterViewsCount() == 0) {
                view.addFooterView(footerView);
            }
        }
    }

    private void setFootDone() {
        mFootState = FOOT_STATE_DONE;
        if (pullToRefreshListView != null) {
            ListView view = pullToRefreshListView.getRefreshableView();
            if (view.getFooterViewsCount() > 0) {
                footerView.setVisibility(VISIBLE);
                footerView.setLoadDoneClick();
            }
        }
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHomeListManager != null) {
            mHomeListManager.doSsoHandler(requestCode, resultCode, data);
        }

    }

    /**
     * 分组空数据按钮点击
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        IntentUtil.gotoSubGroupEdit(mContext, String.valueOf(homeBallBean.getId()), homeBallBean.getTitle(), 2, homeBallBean.getImage());
    }

    @Override
    public void onLoading(int state) {
        if(state == ProgressBarHelper.STATE_LOADING){
            llGroupNoData.setVisibility(GONE);
        }else if(state == ProgressBarHelper.STATE_EMPTY){
            if(HomeBallBean.GROUP_NEWS.equals(homeBallBean.getCategory())){
                llGroupNoData.setVisibility(VISIBLE);
            }else{
                llGroupNoData.setVisibility(GONE);
            }
        }
    }

    class SavedState extends BaseSavedState {
        int mSaveKvo;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public void saveKvo(int value) {
            this.mSaveKvo = value;
        }

        public int getKvo() {
            return mSaveKvo;
        }

        @Override
        public String toString() {
            return "CSouyueTabInner SavedState{"
                    + mSaveKvo + "}";
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable stat  = super.onSaveInstanceState();
        SavedState state = new SavedState(stat);
        state.saveKvo(mKVO.getFalg());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        SavedState s     = (SavedState) state;
        int        value = s.getKvo();
        mKVO.reset(value);
    }

    private void setFootError() {
        mFootState = FOOT_STATE_ERROR;

        if (pullToRefreshListView != null) {
            ListView view = pullToRefreshListView.getRefreshableView();
            if (view.getFooterViewsCount() > 0) {
//                    view.removeFooterView(footerView);
                footerView.setVisibility(VISIBLE);
                footerView.setNetError();
            }
        }
    }


    private void initView(View view) {
        //这些东西和对象有关，对象不销毁就只创建一份
        mMainView = view;
        mMainHttp = CMainHttp.getInstance();
        mDBHelper = HomePageDBHelper.getInstance();


        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_list);
        //分组没有数据
        llGroupNoData = (LinearLayout) view.findViewById(R.id.group_empty_data);
        llGroupNoData.setVisibility(GONE);
        llGroupNoData.findViewById(R.id.btn_group_nodata).setOnClickListener(this);
        pbHelp = new ProgressBarHelper(mActivity,
                view.findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);
        pbHelp.setLoadingListener(this);
        mTextNew = (TextView) view.findViewById(R.id.homepage_recommend);
        mTextNewInAnim = new TranslateAnimation(0, 0, -DeviceUtil.dip2px(mContext, NEW_TAB_HEIGHT), 0);
        mTextNewOutAnim = new TranslateAnimation(0, 0, 0, -DeviceUtil.dip2px(mContext, NEW_TAB_HEIGHT));
        mTextNewOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTextNew.clearAnimation();
                mTextNew.post(new Runnable() {
                    @Override
                    public void run() {//防止布局突然改变
//                        pullToRefreshListView.setCanPullDown(true);
//                        mTextNew.setVisibility(View.GONE);
                        mShowCount--;
                        resetNewsTab();

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTextNewOutAnim.setDuration(500);
        mTextNewInAnim.setDuration(500);
        mTextNewInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTextNew.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                if (position > adapter.getCount()) {
                    if (mFootState == FOOT_STATE_DONE) {
                        pullToRefresh(true);
                    }
                    return;
                }
                BaseListData item = (BaseListData) parent
                        .getItemAtPosition(position);
                if (item.getViewType() == BaseListData.view_Type_CLICK_REFRESH) {
                    pullToRefresh(true);
                    return;
                }
                item.setHasRead(true);
                item.getInvoke().setChan(homeBallBean.getSy_channel());
                mHomeListManager.clickItem(item);
                setHasRead(item);
                adapter.notifyDataSetChanged();
                UmengStatisticUtil.onEvent(getContext(), UmengStatisticEvent.HOME_ITEM_CLICK);    //Umeng
                stopPlayVideo();
            }
        });

        //添加底部加载
        footerView = (CFootView) mActivity.getLayoutInflater().inflate(R.layout.list_refresh_footer, null);
        footerView.initView();
        //加载失败
        setFootDone();
        pullToRefreshListView.getRefreshableView().addFooterView(footerView);
        pullToRefreshListView.setOnScrollListener(this);
        // 下拉刷新监听

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mHomeListManager.setmRefreshing(true);
                if (adapter == null) {
                    return;
                }
                visibleLast = 0;
                refreshing = true;
                stopPlayVideo();
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.cricle_manage_networkerror);
                    refreshing = false;
                    pullToRefreshListView.onRefreshComplete();
                    return;
                }
                if (!mIsInLoading) {
                    pullToRefreshListView.setCanPullDown(false);
                    mIsInLoading = true;
                    String index = getFirstId();
                    String key = mType + mId;
                    setTimeValue(key);
                    getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PULL, 0 + "", index, true);
                }
            }
        });

        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (adapter != null) {
                    String key = mType + mId;
                    String orignTime = getTimeValue(key);

                    String time = StringUtils
                            .convertDate(orignTime);
                    pullToRefreshListView.setTimeLabel(time);
                }

            }
        });

        pullToRefreshListView.setOncompleteListener(new PullToRefreshBase.OnCompleteRefreshListener() {
            @Override
            public void onCompleteRefresh() {
                if (mShowCount <= 0) {
                    pullToRefreshListView.setCanPullDown(true);
                }
                mHomeListManager.setmRefreshing(false);
                mIsInLoading = false;
            }
        });
    }

    @Override
    public HomeListManager getListManager() {
        return mHomeListManager;
    }

    /**
     * 如果是图集，段子，gif页分割线将会是卡片类型的
     *
     * @return
     */
    public boolean isCard(HomeBallBean ball) {
        String title = ball.getTitle();
        if (ball.getInvokeType() == BaseInvoke.INVOKE_TYPE_FOCUSNEWS) {
            if (title.equals(ConstantsUtils.FR_INFO_PICTURES)
                    || title.equals(ConstantsUtils.FR_INFO_JOKE)
                    || title.equals(ConstantsUtils.FR_INFO_GIF)
                    || title.equals(ConstantsUtils.FR_INFO_VIDEO)
                    ) {
                return true;
            }
        }
        return false;
    }

    private String getTimeValue(String key) {
        if (!mTimer.containsKey(key)) {
            return "";
        } else {
            return mTimer.get(key);
        }

    }

    private void setTimeValue(String key) {
        long time = System.currentTimeMillis();
        mTimer.put(key, time + "");
    }

    /**
     * @param force 表示当前这个请求是否是需要强刷的
     *              调用时机，点击搜悦tab force为ture，滑动过来为false
     */
    @Override
    public void pullToRefresh(boolean force) {
        //
        if (!HomeBallBean.isEnable(homeBallBean.getCategory())) {
            mFragment.setEnterAphla(0.f);
        }
        //取得当前球球标识
        String tag = homeBallBean.getCategory() + "_" + homeBallBean.getId();
        if (!force) {//如果不是强行刷新，比如非用户手动刷新就按时间判断
            if (!Utils.isTimeExpire(tag) || refreshing) {
                return;
            }
        } else if (refreshing) {//如果是强行刷新，但是列表正在刷新中就什么也不做
            return;
        }
        refreshing = true;
        if (mKVO.isDone() && pullToRefreshListView != null && adapter.getCount() > 0) {
            pullToRefreshListView.startRefresh();
        } else {
            mKVO.doDone();
        }
    }

    public void resetNewsTab() {
        refreshing = false;
        pullToRefreshListView.setCanPullDown(true);//设置成可以下拉刷新
        pullToRefreshListView.resetTitle();//蓝条提示归位
        mTextNew.setVisibility(View.GONE);//蓝条不可见
        mTextNew.clearAnimation();//
        mMainView.clearAnimation();//
        mShowCount = 0;
    }

    @Override
    public void updateViewList() {
        Log.v(this.getClass().getName(), "----------刷新字体");
        adapter.notifyDataSetChanged();
    }


    /**
     * 显示蓝条提示
     *
     * @param num
     * @param type
     */
    private void showNewTabs(int num, String type) {
        if (num > 0) {
            String showstr = "";
            if (type.equals(HomeBallBean.RECOMMEND)) {
                showstr = mContext.getString(R.string.homepage_recommend, num);
            } else {
                showstr = mContext.getString(R.string.homepage_update, num);
            }
            mTextNew.setText(showstr);
            if (mShowCount > 0 && !isShown() || !refreshing) {
                return;
            }
            if (type.equals(HomeBallBean.YAOWEN)) {//要闻页大于一页不提示
                if (num <= 0 || num >= PAGE_SIZE) {
                    refreshing = false;
                    return;
                }
            }
            pullToRefreshListView.setSpaceTop(-DeviceUtil.dip2px(mContext, NEW_TAB_HEIGHT));
            mShowCount++;
            mTextNew.setVisibility(View.VISIBLE);
            mTextNew.startAnimation(mTextNewInAnim);
            mTextNew.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mIsDestory) {
                        mShowCount--;
                        resetNewsTab();
                        return;
                    }
                    if (mShowCount > 0) {
                        mMainView.startAnimation(mTextNewOutAnim);
                    }
                }
            }, 2000);
        } else {
            pullToRefreshListView.setSpaceTop(0);
            refreshing = false;
        }
    }

    @Override
    public void clickRefresh() {
        getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST, 0 + "", getFirstId(), true);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                imageLoader.resume();
                //设置进入按钮透明度，当列表停止时透明度恢复，其他状态时，设置为半透明
                if (HomeBallBean.isEnable(mType)) {
                    mFragment.setEnterButtonAphla(1.f);
                } else {
                    mFragment.setEnterButtonAphla(0.f);
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                imageLoader.pause();
                //persent为负值
                if (HomeBallBean.isEnable(mType)) {
                    mFragment.setEnterButtonAphla(0.5f);
                } else {
                    mFragment.setEnterButtonAphla(0.f);
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                imageLoader.pause();
                if (HomeBallBean.isEnable(mType)) {
                    mFragment.setEnterButtonAphla(0.5f);
                } else {
                    mFragment.setEnterButtonAphla(0.f);
                }
                break;
        }
        if (adapter == null) {
            return;
        }
        int itemsLastIndex = adapter.getCount();
        if (itemsLastIndex < 0) {
            return;
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast >= itemsLastIndex && mPushLoad) {
            String lastId = getLastId();
            if (hasMore) {
                mPushLoad = false;
                getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PUSH, lastId, "" + 0, true);
            } else {
                //如果是新增的专题类型的话...浏览历史
                if (mType.equals(HomeBallBean.SPECIAL_TOPIE) || mType.equals(HomeBallBean.HISTORY)) {
                    mPushLoad = false;
                    setFootLoading();
                    getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PUSH, lastId, "" + 0, true);
                } else if (adapter.getCount() != 0) {
                    setFootDone();
                }

            }

        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Log.v(this.getClass().getName(), "souyue tab inner:atatch " + homeBallBean.getTitle());
            broadcast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.v(this.getClass().getName(), "收到广播");
                    String time = intent.getStringExtra(BroadCastUtils.SEARCH_TIME);
                    //在详情页的时候点击了不感兴趣时，发送广播到首页如果是推荐和头条时删掉当前这条数据
                    if (adapter != null && (mType.equals(HomeBallBean.RECOMMEND) || mType.equals(HomeBallBean.HEADLINE))) {
                        long id = 0;
                        try {
                            id = Long.valueOf(time);
                        } catch (Exception e) {
                        }
                        List datas = adapter.getDatas();
                        BaseListData d, del = null;
                        for (Object data : datas) {
                            d = (BaseListData) data;
                            if (d.getId() == id) {
                                del = d;
                                break;
                            }
                        }
                        if (del != null) {
                            adapter.deleteData(del);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            };

            mContext.registerReceiver(broadcast, new IntentFilter(LocalBroadCastHelper.HOMELIST_DELETE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v(this.getClass().getName(), "souyue tab inner:detached " + homeBallBean.getTitle());
        try {
            if (broadcast != null) {
                mContext.unregisterReceiver(broadcast);
                broadcast = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上拉加载相关
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        visibleLast = firstVisibleItem + visibleItemCount;
        // 滑动播放的控制
        playVideoController();
    }

    /**
     * 滑动播放的控制
     */
    private void playVideoController() {
        ListView listview     = pullToRefreshListView.getRefreshableView();
        int      PlayPosition = mHomeListManager.getPlayPosition();
        if ((PlayPosition < listview.getFirstVisiblePosition() - 1
                || PlayPosition +1 > listview.getLastVisiblePosition())
                && mHomeListManager.getIsPalying()) {
            mHomeListManager.forceStopPlay();
        }
    }

    public boolean getRefreshing() {
        return refreshing;
    }

    public void setHeight(int height) {
        pullToRefreshListView.setMinimumHeight(height);
    }


    @Override
    public void onHttpStart(IRequest _request) {

    }

    @Override
    public void onHttpResponse(IRequest _request) {
        if (mIsDestory) {
            pullToRefreshListView.onRefreshComplete();
            return;
        }

        int id = _request.getmId();
        switch (id) {
            case HttpCommon.HOME_GET_LIST:
            case HttpCommon.HOME_LIST_PULL:
            case HttpCommon.HOME_LIST_PUSH:
                long thisid = (Long) _request.getKeyValueTag("id");
                Object type = _request.getKeyValueTag("type");
                if (thisid != mId || !mType.equals(type)) {//如果回来的不是当前数据就不做处理了
                    mIsInLoading = false;
                    return;
                }
                doResponse(id, (List<Object>) _request.getResponse());
                List<BaseListData> datas = adapter.getDatas();
                //遍历回来的列表设置已读未读
                setListHasRead(datas);
                adapter.notifyDataSetChanged();

                SouyueTabFragment.HighlightHandler handler = new SouyueTabFragment.HighlightHandler(mFragment);//功能引导
                handler.sendEmptyMessage(0);
                break;
        }
        stopPlayVideo();
    }


    private void doResponse(int id, List result) {
        List<BaseListData> topList   = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_TOPLIST);
        List<BaseListData> focusList = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_FOCUS);
        List<BaseListData> newsList  = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_NEWSLIST);
        String             tag       = mType + "_" + mId;
        if (isSmartUpdate(mType)) {//如果是增量更新，加入cache
            addCache(newsList);
        }
        switch (id) {
            case HttpCommon.HOME_GET_LIST:
                setDatas(topList, focusList, newsList);
                if (adapter.getCount() == 0) {
                    pbHelp.showNoData();
                } else {
                    pbHelp.goneLoadingUI();
                }
                boolean hasm1 = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                if (hasm1) {
                    mPushLoad = true;
                    mLoadDataFromLocal = false;
                    hasMore = true;
                } else {
                    mPushLoad = false;
                    setFootDone();
                }
                refreshing = false;
                break;
            case HttpCommon.HOME_LIST_PULL:
                pullToRefreshListView.onRefreshComplete();
                boolean hasm = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                setFootDone();
                int count = newsList.size();
                if (hasm) {
                    setDatas(topList, focusList, newsList);
                    mPushLoad = true;
                    mLoadDataFromLocal = false;
                    hasMore = true;
                } else {
                    //如果非增量更新，则清除列表，将列表页放到当前展示列表中
                    if (!isSmartUpdate(mType)) {
//                        adapter.setData(newsList);
//                        adapter.addFirst(topList);
                        adapter.clear();
                        addFirst(topList, focusList, newsList, false);
                        mPushLoad = false;
                    } else if (mType.equals(HomeBallBean.RECOMMEND)) {
                        clearClickRefresh();
                        if (newsList.size() != 0) {
                            BaseListData click = new BaseListData();
                            click.setViewType(BaseListData.view_Type_CLICK_REFRESH);
                            newsList.add(click);
                        }
                        addFirst(topList, focusList, newsList, false);
                    } else {
                        addFirst(topList, focusList, newsList, false);
                    }
                }
                if (isSmartUpdate(mType)) {//srp不显示,Specials也不显示(不增量更新的都打不更新)
                    showNewTabs(count, mType);
                } else {
                    refreshing = false;
                }
                if (adapter.getCount() == 0) {
                    pbHelp.showNoData();
                    break;
                } else {
                    pbHelp.goneLoadingUI();
                    Utils.resetTime(tag);
                }
                break;
            case HttpCommon.HOME_LIST_PUSH:
                hasMore = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                if (newsList.size() > 0) {
                    adapter.addLast(newsList);
                }
                if (!hasMore) {
                    setFootDone();
                    mPushLoad = false;
                } else {
                    mPushLoad = true;
                }
                break;
        }
    }

    public void stopPlayVideo() {
        if (mHomeListManager != null) {
            mHomeListManager.forceStopPlay();
        }
    }

    /**
     * 设置列表数据
     *
     * @param toplist
     * @param focusList
     * @param newslist
     */
    private void setDatas(List<BaseListData> toplist, List<BaseListData> focusList, List<BaseListData> newslist) {
        if (focusList != null && focusList.size() > 0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            toplist.add(0, bean);
        }
        adapter.setData(newslist);
        adapter.addFirst(toplist);
    }

    /**
     * 向列表最前面加数据
     *
     * @param toplist
     * @param newslist
     */
    private void addFirst(List<BaseListData> toplist, List<BaseListData> focusList, List<BaseListData> newslist, boolean hasmore) {
        if (focusList != null && focusList.size() > 0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            bean.setLocalTop(true);
            toplist.add(0, bean);
        }
        List<BaseListData> datas = adapter.getDatas();
        if (datas != null) {
            if (!hasmore) {

                List<BaseListData> delete = new ArrayList<BaseListData>();
                for (BaseListData d : datas) {
                    if (d.isLocalTop()) {
                        delete.add(d);
                    }
                }
                for (BaseListData d : delete) {
                    datas.remove(d);
                }
                adapter.setData(datas);
            } else {//下拉如果有更多就将当前页清除
                adapter.clear();
            }
        }
        adapter.addFirst(newslist);
        adapter.addFirst(toplist);
    }

    /**
     * 上拉刷新的时候清除列表中点击刷新项
     */
    public void clearClickRefresh() {
        List<BaseListData> listItems = adapter.getDatas();
        List<BaseListData> items     = new ArrayList<BaseListData>();
        for (BaseListData item : listItems) {
            if (item.getViewType() == BaseListData.view_Type_CLICK_REFRESH) {
                items.add(item);
            }
        }
        for (BaseListData item : items) {
            listItems.remove(item);
        }
    }


    /**
     * 新线程中执行
     *
     * @param _lastId
     * @return
     */
    private void getCache(String _lastId, DataCallBack callBack) {
//        DBhelperAsync async = new DBhelperAsync(callBack);
//        async.execute(_lastId);
        DBhelperAsyncNew asyncTask = new DBhelperAsyncNew(callBack);
        asyncTask.excute(_lastId);
    }

    private void addCache(List<BaseListData> datas) {
        int                  l      = datas.size();
        final List<HomeList> items  = new ArrayList<HomeList>(l);
        final String         userid = SYUserManager.getInstance().getUserId();
        if (userid == null) {
//            Utils.makeToastTest(getContext(), "userid 为 null 了，请查证！");
            return;
        }
        BaseListData it = null;
        for (int i = 0; i < l; i++) {
            it = datas.get(i);
            String dateid = it.getId() + "";
            String data = it.getJsonResource();
            ;
            //数据库数据唯一标识是球球id+球球category+数据id
            String id_type_time = mId + "_" + homeBallBean.getCategory() + "_" + dateid +"_" + homeBallBean.getSrpId();
            HomeList item = new HomeList(id_type_time, mId + "", homeBallBean.getCategory(), dateid, data);
            items.add(item);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDBHelper.addData(userid, items);
            }
        }).start();
    }

    /**
     * 设置已读未读状态
     *
     * @param items
     */
    private void setListHasRead(List<BaseListData> items) {

        String userid = SYUserManager.getInstance().getUserId();
        if (userid == null) {
            Log.e(this.getClass().getName(), "userid is null");
            return;
        }
        HashMap<String, UserHomeList> map = mDBHelper.getUserListDaoReadCache(userid);
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            BaseListData temp = items.get(i);
            if (temp != null) {
                String id_type_time = mId + "_" + homeBallBean.getCategory() + "_" + temp.getId();
                UserHomeList lis = map.get(id_type_time);
                if (lis != null && lis.getRead() != null && lis.getRead().equals("1")) {
                    temp.setHasRead(true);
                } else {
                    temp.setHasRead(false);
                }
            }
        }
    }

    /**
     * 点击记录当前这条数据为已读状态
     *
     * @param item
     */
    private void setHasRead(BaseListData item) {
        final String userid = SYUserManager.getInstance().getUserId();
        if (userid == null) {
//            Utils.makeToastTest(getContext(), "userid 为 null 了，请查证！");
            return;
        }
        final String dataid = item.getId() + "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDBHelper.setHasRead(userid, mId + "", mType, dataid);//耗时30-50毫秒
            }
        }).start();
    }

    @Override
    public void onHttpError(IRequest _request) {
        if (mIsDestory) {
            pullToRefreshListView.onRefreshComplete();
            return;
        }
        int id = _request.getmId();
        if (id == CMainHttp.HTTP_REQUEST_HOMELIST || id == CMainHttp.HTTP_REQUEST_HOMELIST_PUSH
                || id == CMainHttp.HTTP_REQUEST_HOMELIST_PULL) {
            long thisid = (Long) _request.getKeyValueTag("id");
            Object type = _request.getKeyValueTag("type");
            if (thisid != mId && !mType.equals(type)) {//如果回来的不是当前数据就不做处理了
                mIsInLoading = false;
                return;
            }
        }
        mPushLoad = true;

        switch (id) {
            case HttpCommon.HOME_GET_LIST:
                if (adapter.getCount() == 0) {
                    pbHelp.showNetError();
                } else {
                    pbHelp.goneLoadingUI();
                }
                refreshing = false;
                break;
            case HttpCommon.HOME_LIST_PULL:
                pullToRefreshListView.onRefreshComplete();
                pbHelp.goneLoadingUI();
                if (adapter.getCount() == 0) {
                    pbHelp.showNetError();
                }
                refreshing = false;
                break;
            case HttpCommon.HOME_LIST_PUSH:
                UIHelper.ToastMessage(mContext,
                        R.string.cricle_manage_networkerror);
//                showToast();
//                footerView.setNetError();
                setFootError();
                break;
            default:
                UIHelper.ToastMessage(mContext,
                        R.string.cricle_manage_networkerror);
        }
        stopPlayVideo();
    }

    /**
     * 判断是否是增量更新
     *
     * @return
     */
    private boolean isSmartUpdate(String type) {
        if (type.equals(HomeBallBean.SRP)
                || type.equals(HomeBallBean.SPECIAL_TOPIE)
                || type.equals(HomeBallBean.HISTORY)
                || type.equals(HomeBallBean.YAOWEN)) {
            return false;
        }
        return true;
    }

    public interface DataCallBack<T> {
        public void callback(T data);
    }

    class DBhelperAsync extends ZSAsyncTask<String, Integer, List<BaseListData>> {
        DataCallBack<List<BaseListData>> mCallBack;

        public DBhelperAsync(DataCallBack<List<BaseListData>> callback) {
            mCallBack = callback;
        }

        @Override
        protected List<BaseListData> doInBackground(String... params) {
            String userid = SYUserManager.getInstance().getUserId();
            if (userid == null) {
                Log.e(this.getClass().getName(), "userid is null");
                return new ArrayList<BaseListData>();
            }
            List<BaseListData> list = null;
            try {
                list = mDBHelper.getData(userid, homeBallBean.getCategory(), mId + "", params[0], 20);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<BaseListData> o) {
            super.onPostExecute(o);
            mCallBack.callback(o);
        }
    }

    class DBhelperAsyncNew<T> {
        DataCallBack mCallBack;
        ArrayList<BaseListData> mdata   = new ArrayList<BaseListData>();
        Handler                 handler = new Handler(Looper.getMainLooper());

        public DBhelperAsyncNew(DataCallBack<T> callBack) {
            mCallBack = callBack;
        }

        public void excute(final String... params) {
            new Thread() {
                @Override
                public void run() {
                    String userid = SYUserManager.getInstance().getUserId();
                    if (userid == null) {
                        Log.e(this.getClass().getName(), "userid is null");
                    } else {
                        try {
                            mdata.addAll(mDBHelper.getData(userid, homeBallBean.getCategory(), mId + "", params[0], 20));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallBack.callback(mdata);
                        }
                    });
                }
            }.start();
        }
    }
}
