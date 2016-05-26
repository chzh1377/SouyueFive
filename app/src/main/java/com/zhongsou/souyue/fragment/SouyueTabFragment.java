package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.SplashActivity;
import com.zhongsou.souyue.adapter.HorazAdapter;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.CustomProgress;
import com.zhongsou.souyue.common.dialog.CommDialog;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.component.HomeTitleView;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.SplashAd;
import com.zhongsou.souyue.module.SubGroupModel;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.firstleader.ChildGroupItem;
import com.zhongsou.souyue.module.firstleader.UserGuideInfo;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.group.GroupTitleReq;
import com.zhongsou.souyue.net.guide.FirstGuideSubReq;
import com.zhongsou.souyue.net.home.HomeBallListRequest;
import com.zhongsou.souyue.net.other.SplashImageRequest;
import com.zhongsou.souyue.net.sub.GroupDeleteReq;
import com.zhongsou.souyue.net.sub.SubUpdateRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.Fabutton;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.highlight.Highlight;
import com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog;
import com.zhongsou.souyue.ui.webview.JavascriptInterface;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.TemplateUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.AFragmentBaseView;
import com.zhongsou.souyue.view.CSouyueTabInnerSpecial;
import com.zhongsou.souyue.view.CSouyueTabWeb;
import com.zhongsou.souyue.view.CirlTitleView;
import com.zhongsou.souyue.view.CirlTitleView.OnItemChanged;
import com.zhongsou.souyue.view.CirlTitleView.OnItemChangedStop;
import com.zhongsou.souyue.view.CirlTitleView.OnItemClickListener;
import com.zhongsou.souyue.view.FaceRelativeLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2014/8/20. mailto:wzyax@qq.com
 */
public class SouyueTabFragment extends BaseTabFragment implements
        OnItemClickListener, OnItemChanged,
        ProgressBarHelper.ProgressBarClickListener, OnPageChangeListener,
        Runnable, OnItemChangedStop, FaceRelativeLayout.onTitleLensiter,
        View.OnClickListener, CirlTitleView.onItemLongClickListener, CirlTitleView.OnScrollListener {
    public static final int HOME_ENTER_HEIGHT = 126;

    public static final int UPDATE_ALL = 1;

    public static final int SCROLL_SETDATA = 0;
    public static final int SCROLL_DEFAULT = 1;

    public static final String SAVE_STATE_VISIBLE = "visible";
    public static final String SAVE_STATE_CIRCLELIST = "circlelist";
    public static final String SAVE_STATE_SRPID = "srpid";

    public static final String ACTION_CHANGER_FONT = "update_font";

    protected int mTitleSpeed;//手指松开时顶部隐藏的速度
    public int mOffsety;
    protected boolean isScroll;
    protected boolean mDoWork;

    protected int mTitleHeight;
    protected int mPrePosition;

    protected int mScrollState;
    private boolean mIsFreshFromNet;

    private CMainHttp mMainHttp;
    private CustomProgress mLoading;
    private ViewPager viewPager;
    private CirlTitleView mcirclepageindicator; //球球
    private FaceRelativeLayout mTotalLayout;//可以滑动的总得布局
    private HorazAdapter circleAdapter;
    private HomeListAdapter mViewPagerAdapter;
    private int mLeftCount = -1;
    private List<HomeBallBean> mBeans;
    private List<HomeBallBean> mHomesCache;
    private String mSrpidCache;
    private TextView circle_center_title_text;
    private SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    public HomeTitleView mTitleView;//顶部标签
    private ImageView mIndicaterRight;
    private ImageView mIndicaterLeft;
    private Fabutton mHomeEnter;//首页进入按钮
    private ViewGroup mRootView;
    private UpdateBroadCastRecever receiver;

    private Animation mLeftIn;
    private Animation mLeftOut;
    private Animation mRightIn;
    private Animation mRightOut;

    private SuberDaoImp mSuberDao;
    private float mPreAlpha = 1.f;
    private int mEnterOffsetY;  //右下角按钮上边距
    private int mEnterOffsetX;  //右下角按钮左边距
    private int mTipEnterOffsetY;  //右下角按钮提示上边距
    private int mMiddlePosition;    //我的头条的索引

    // viewpage scroll 状态
    private int mVPScrollState;
    private int mCircleState;
    private int mCurChangeIndex, mPreChangeIndex;
    private boolean mCurEnable, mPreEnable;
    private int mCurIndex = -1;
    private int mFreshCount = 0;//当网络回来数据时，有一定数目不移动球球

    private boolean mFirstIn = true;//每次从网络取到数据时需要更新当前页
    private boolean mIsLoadCache = true;//第一次进入更新球球去网络取数据
    private boolean mCreate;//oncreate执行了吗
    private boolean mVisible;//
    private boolean mResumeAble;//控制第一次进来setvisibletouser和onresume执行的情况，保证只执行一次

    private Highlight mNewsHighlight;    //要闻特性引导
    private Highlight mHomeHighlight;    //头条特性引导
    private Highlight mHomeSubHighlight;
    private Highlight mItemTagHighlight;

    public static final String REFRESH_HOMEBALL_FROMCACHE = "refresh_ball_from_cache"; //通知首页再次去缓存，更新球球
    public static final String REFRESH_HOMEGROUP_DATA = "refresh_homegroup_data"; // 刷新球球分组的广播
    public static final String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String ACTION_UNSUB_BALL = "action_unsub_ball";//取消订阅球球.
    private String lastSplashUrl;
    private boolean isLoadSplashImg = true;
    private boolean isClicked; //是否是点击球球跳转的

    private Long mCurrentGroupId = -1L;
    private boolean hideState;  //当前Fragment是否隐藏
    public static int DEL_BALL_RESOTRE_POSITION = -1;

    public static class HighlightHandler extends Handler {
        private SouyueTabFragment fragment;

        public HighlightHandler(SouyueTabFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {

            fragment.setHighlight();
        }
    }

    //分组信息
    private Map<Long, List<SubGroupModel>> mGroupInfos = Collections.synchronizedMap(new HashMap<Long, List<SubGroupModel>>());
    private TextView btnAddSub; //添加订阅按钮

    public static SouyueTabFragment newInstance(String param1, String param2) {
        SouyueTabFragment fragment = new SouyueTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SouyueTabFragment() {

    }

    @Override
    public void onResume() {
        if (mHomeEnter != null) {
            mHomeEnter.setVisibility(View.GONE);
        }
        if (!mResumeAble) {
            initBall();
        }
        Log.v(getClass().getName(), "resumeable " + mResumeAble);
        super.onResume();
        resetTitle();
        mResumeAble = false;
        if (mTotalLayout != null) {
            mTotalLayout.setIsCanScroll(true);
        }

        removeHighlight(); //移除引导提示

        if (viewPager != null) {
            int index = viewPager.getCurrentItem();
            AFragmentBaseView v = mViewPagerAdapter.getCurrentFragment(index);
            if (v != null) {
                HomeListManager manager = v.getListManager();
                if (manager != null) {
                    manager.onResume();
                }
            }
        }
    }

    @Override
    public void onPause() {
        mResumeAble = false;
        removeHighlight(); //移除引导提示
        super.onPause();
        if (viewPager != null) {
            int index = viewPager.getCurrentItem();
            AFragmentBaseView v = mViewPagerAdapter.getCurrentFragment(index);
            if (v != null) {
                HomeListManager manager = v.getListManager();
                if (manager != null) {
                    manager.onPause();
                }
            }
        }
    }

    private boolean initTitleBall() {
        boolean havecache = false;
//		if (mBeans == null || mBeans.size() == 0) {
        String key = Utils.getCacheKey(SYUserManager.getInstance().getToken());
        int state = mMainHttp.getCacheState(key);
//        Log.e(this.getClass().getName(),"suber cache:state="+state);
        if (state > 0) {
            getBallList(false, true);//强取缓存
            havecache = true;
        } else {
            havecache = false;
        }
        return havecache;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainHttp = CMainHttp.getInstance();//初始化网络类
        //判断是否设置了清除首页球球缓存的标识，如果设置了就清除球球缓存
        if (SYSharedPreferences.getInstance().getBoolean(SYSharedPreferences.CLEAR_HOME_CACHE, false)) {
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.CLEAR_HOME_CACHE);
            String key = Utils.getCacheKey(SYUserManager.getInstance().getToken());
            mMainHttp.removeCache(key);
        }
        mSuberDao = new SuberDaoImp();
        mCreate = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {//当app被杀掉后回来重建后一些值得恢复
            mVisible = savedInstanceState.getBoolean(SAVE_STATE_VISIBLE);
            mHomesCache = (List<HomeBallBean>) savedInstanceState.getSerializable(SAVE_STATE_CIRCLELIST);
            mSrpidCache = savedInstanceState.getString(SAVE_STATE_SRPID);
        }
        return inflater.inflate(R.layout.fragment_souyue_tab, container, false);
    }

    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = (ViewGroup) getView();//设置根view，用来承载进入标签的
        //加载loading类
        pbHelp = new ProgressBarHelper(getActivity(),
                view.findViewById(R.id.ll_data_loading));
        mTotalLayout = (FaceRelativeLayout) view.findViewById(R.id.souyue_tab_container);
        mTotalLayout.setOnTitleLensiter(this);
        mTotalLayout.setIsCanScroll(true);//设置首页可以上滑隐藏

        loadAnimIndicater(view);


        DisplayMetrics metric = getActivity().getResources()
                .getDisplayMetrics();
        int width = metric.widthPixels;
        int height = metric.heightPixels;

        //加载首页进入标签按钮
        mHomeEnter = new Fabutton(getActivity());
        mHomeEnter.setBackgroundDrawable(getActivity().getResources().getDrawable(
                R.drawable.homepage_enter_selector));
        mHomeEnter.setMode(Fabutton.MODE_BUTTON);
        mHomeEnter.setOnClickListener(this);
        mHomeEnter.setButtonState();
        setEnterAphla(0.f);//将进入标签设置为不可见
        //以下为计算进入标签按钮的位置
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                DeviceUtil.dip2px(getActivity(), 35),
                DeviceUtil.dip2px(getActivity(), 35));

        mEnterOffsetY = height - DeviceUtil.dip2px(getActivity(), HOME_ENTER_HEIGHT);
        mEnterOffsetX = width - DeviceUtil.dip2px(getActivity(), 56);
        mTipEnterOffsetY = mEnterOffsetY;   //设置提示信息的边距
        params.gravity = Gravity.END;
        params.rightMargin = DeviceUtil.dip2px(getActivity(), 15);
        params.topMargin = mEnterOffsetY;
        //将进入标签按钮加到根view上
        mRootView.addView(mHomeEnter, params);


        mTitleView.init(this);
        //初始化
        mTitleSpeed = DeviceUtil.dip2px(context, 5);
        pbHelp.setProgressBarClickListener(this);
        //首页顶部球球
        mcirclepageindicator = (CirlTitleView) view
                .findViewById(R.id.pageCircleIndicator);
        //首页顶部球球下方标签
        circle_center_title_text = (TextView) view
                .findViewById(R.id.circle_center_title_text);
        //列表viewpager
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        //设置球球点击事件
        mcirclepageindicator.setOnItemClickListener(this);
        //设置球球改变回调
        mcirclepageindicator.setOnItemChange(this);
        //球球手动滑动后最后停下来的位置
        mcirclepageindicator.setOnItemChangedStop(this);
        //球球长按事件
        mcirclepageindicator.setOnItemLongClickListener(this);
        //球球滑动事件
        mcirclepageindicator.setOnScrollListener(this);
        viewPager.setOnPageChangeListener(this);
        //球球adapter
        circleAdapter = new HorazAdapter(getActivity());
        if (mViewPagerAdapter == null) {
            //viewpager adapter
            mViewPagerAdapter = new HomeListAdapter(getActivity());
            viewPager.setAdapter(mViewPagerAdapter);
        }
//        他大爷的 下面隐藏需求终于去掉了
//        ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0)
//                .addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top,
//                                               int right, int bottom, int oldLeft, int oldTop,
//                                               int oldRight, int oldBottom) {
//                        if (mInitFaceHeight == 0) {
//                            mTabHeight = ((MainActivity) context)
//                                    .getmWidgetHeight();
//                            mTitleHeight = mTitleView.getHeight();
//                            mInitFaceHeight = mTotalLayout.getHeight();
//                        }
//                        mHandler.sendEmptyMessageDelayed(UPDATE_SCREEN, 20);
//                    }
//                });
        // initTitleBall();
        setReciever();
        //开启自动滑动回原位置监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTotalLayout.postOnAnimation(this);
        } else {
            mTotalLayout.postDelayed(this, 10);
        }

        if (getActivity() != null) {
            mTotalLayout.post(new Runnable() {
                @Override
                public void run() {
                    IntentUtil.checkKickUserMsg(getActivity());
                }
            });
        }
        btnAddSub = (TextView) view.findViewById(R.id.ico_home_title_subcribe);
    }

    /**
     * 加载点击球球左右两个标记回到球球中间位置的动画
     *
     * @param view
     */
    private void loadAnimIndicater(View view) {
        mTitleView = (HomeTitleView) view.findViewById(R.id.title_layout);
        mIndicaterLeft = (ImageView) view.findViewById(R.id.iv_home_page_indicater_arrow_left);
        mIndicaterRight = (ImageView) view.findViewById(R.id.iv_home_page_indicater_arrow_right);
        mIndicaterLeft.setVisibility(View.INVISIBLE);
        mIndicaterRight.setVisibility(View.INVISIBLE);
        mIndicaterLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcirclepageindicator.smoothScrollToMiddleIndex(mMiddlePosition);
            }
        });
        mIndicaterRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.HOME_HEADLINE_BACK);   //Umeng
                mcirclepageindicator.smoothScrollToMiddleIndex(mMiddlePosition);
            }
        });
        mLeftIn = AnimationUtils.loadAnimation(getActivity(), R.anim.left_in);
        mLeftOut = AnimationUtils.loadAnimation(getActivity(), R.anim.left_out);
        mRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);

    }

    /**
     * 当当前fragment被销毁时，要保存的一些变量
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVE_STATE_VISIBLE, mVisible);
        outState.putSerializable(SAVE_STATE_CIRCLELIST, (ArrayList) mHomesCache);
        outState.putString(SAVE_STATE_SRPID, mSrpidCache);
        super.onSaveInstanceState(outState);
    }


    private void caculateViewPageHeight(int h) {
        int circleHeight = mcirclepageindicator.getHeight();
        // mHandler.sendEmptyMessageDelayed(UPDATE_SCREEN, 20);
        // mTitleHeight1 = circleHeight + mTitleHeight;
        mTotalLayout.setTopHeight(circleHeight + mTitleHeight + mOffsety);
    }

    private void setTotalLayoutHeight(int height) {
        if (mTotalLayout.getHeight() != height) {
            mTotalLayout.getLayoutParams().height = height;
            mTotalLayout.requestLayout();
        }
    }

//    Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case UPDATE_SCREEN:
//                    if (mScrollState == FaceRelativeLayout.ACTION_TITLE_STOP) {
//                        int height = Utils.getDeviceWH(context)[1];
//                        mRelativeHeight = height - Utils.mStutas + mTitleHeight;
//                        setTotalLayoutHeight(mRelativeHeight);
//                    }
//                    break;
//            }
//            return false;
//        }
//    });

    /**
     * 显示左边球球下方标签
     */
    private void showLeft() {
        if (mIndicaterLeft.getVisibility() == View.INVISIBLE) {
            mIndicaterLeft.startAnimation(mRightIn);
            mIndicaterLeft.setVisibility(View.VISIBLE);
            mIndicaterRight.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示右边球球下方标签
     */
    private void showRight() {
        if (mIndicaterRight.getVisibility() == View.INVISIBLE) {
            mIndicaterRight.startAnimation(mLeftIn);
            mIndicaterRight.setVisibility(View.VISIBLE);
            mIndicaterLeft.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 隐藏球球下面左右标签
     */
    private void hideAll() {
        if (mIndicaterRight.getVisibility() == View.VISIBLE) {
            mIndicaterRight.setVisibility(View.INVISIBLE);
            mIndicaterRight.startAnimation(mRightOut);
        } else if (mIndicaterLeft.getVisibility() == View.VISIBLE) {
            mIndicaterLeft.setVisibility(View.INVISIBLE);
            mIndicaterLeft.startAnimation(mLeftOut);
        }
    }

    /**
     * 点击tabhost第一个标签时做下拉刷新动作
     */
    public void PullListRefresh() {
        if (mBeans == null || mBeans.size() == 0) {
            return;
        }
        int index = viewPager.getCurrentItem();
        AFragmentBaseView fragment = mViewPagerAdapter
                .getCurrentFragment(index);
//		mViewPagerAdapter.resetFragment(index);
        if (fragment != null) {
            fragment.pullToRefresh(true);
        }
    }

    /**
     * 获取球球列表
     *
     * @param _refresh    是否强取网络
     * @param _forcecache 是否强取缓存，这个参数的优先级，优先于_refresh
     */
    public void getBallList(boolean _refresh, boolean _forcecache) {
        HomeBallListRequest request = new HomeBallListRequest(HttpCommon.HOME_LIST_BALL, this);
        request.setParams(SYUserManager.getInstance().getToken(), UPDATE_ALL, _refresh, _forcecache);
        mMainHttp.doRequest(request);
        //fab不可店家
//        mMainHttp.getHomeBallList(CMainHttp.HTTP_REQUEST_HOMEBALL,
//                SYUserManager.getInstance().getToken(), UPDATE_ALL, _refresh, _forcecache,
//                this);
    }

    /**
     * 加载失败时点击刷新
     */
    @Override
    public void clickRefresh() {
        try {
            //订阅上次未注册的信息
            subUserGuideInfo();
        } catch (Exception e
                ) {

        }
    }


    /**
     * 订阅上次用户的信息
     */
    private void subUserGuideInfo() {
        final UserGuideInfo savedUser = UserGuideInfo.getSavedUser();
        List<AppData> datas = DeviceInfo.getAppData(getActivity());
        if (savedUser != null) {
            FirstGuideSubReq.send(HttpCommon.FIRST_GUIDE_SUBSCRIPE, new IVolleyResponse() {
                @Override
                public void onHttpResponse(IRequest request) {
                    new Thread() {
                        @Override
                        public void run() {
                            List<SuberedItemInfo> addList = new ArrayList<SuberedItemInfo>();// 添加数组
                            List<ChildGroupItem> subItems = savedUser.getSubItems();
                            for (ChildGroupItem item : subItems) {
                                SuberedItemInfo info = new SuberedItemInfo();
                                info.setCategory(item.getCategory());
                                info.setKeyword(item.getKeyword());
                                info.setSrpId(item.getSrpId());
                                addList.add(info);
                            }
                            SuberDaoImp suberDaoImp = new SuberDaoImp();
                            suberDaoImp.addAll(addList);
                            Intent intentGotoBall = new Intent();
                            intentGotoBall.setAction(SouyueTabFragment.REFRESH_HOMEBALL_FROMCACHE);
                            getActivity().sendBroadcast(intentGotoBall);
                            UserGuideInfo.clearSavedInfo();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);
                                    initBall(); // 订阅上次用户未订阅的信息，需要刷新球球
                                }
                            });
                        }
                    }.start();

                }

                @Override
                public void onHttpError(IRequest request) {
                    getBallList(true, false);//强取网络
                }

                @Override
                public void onHttpStart(IRequest request) {

                }
            }, savedUser, datas);
        }else{
            getBallList(true, false);//强取网络
        }
    }

    /**
     * 滑动列表时，根据手指滑动距离的回调
     *
     * @param offsety 距离点击的滑动距离
     * @param state   当前滑动状态
     * @return
     */
    public boolean scrollTo(float offsety, int state) {
        mScrollState = state;
        isScroll = false;
        if (mTitleHeight == 0) {
            mTitleHeight = mTitleView.getHeight();
            setTotalLayoutHeight(mTotalLayout.getHeight() + mTitleHeight);
        }
        mOffsety += offsety;
        if (mOffsety < -mTitleHeight) {
            mOffsety = -mTitleHeight;
            isScroll = false;
        } else if (mOffsety > 0) {
            mOffsety = 0;
            isScroll = false;
        } else {
            scrollToPosition(mOffsety);
            isScroll = true;
        }
        if (!mDoWork) {
            mDoWork = true;
        }
        return isScroll;
    }

    /**
     * 恢复隐藏的头部或底部
     */
    public void resetTitle() {
        mOffsety = 0;
        isScroll = false;
        mDoWork = false;
        if (context != null) {
            ((MainActivity) context).updateTabWidget(0);
        }
        if (mTotalLayout != null) {
            mTotalLayout.scrollTo(0, 0);
            mScrollState = FaceRelativeLayout.ACTION_TITLE_STOP;
            scrollToPosition(mOffsety);
        }
    }

    /**
     * 头部或底部自从恢复功能
     */
    @SuppressLint("NewApi")
    @Override
    public void run() {
        if (mScrollState == FaceRelativeLayout.ACTION_TITLE_STOP) {
            //如果滑动在中间位置，会有一个恢复的操作
            if (mOffsety > -mTitleHeight / 2 && mOffsety < 0) {
                mOffsety += mTitleSpeed;
            } else if (mOffsety <= -mTitleHeight / 2
                    && mOffsety > -mTitleHeight) {
                mOffsety -= mTitleSpeed;
            }
            if (mOffsety < -mTitleHeight) {
                mOffsety = -mTitleHeight;
            } else if (mOffsety > 0) {
                mOffsety = 0;
            }
        }
        if (mOffsety == 0 || mOffsety == -mTitleHeight) {
            if (mDoWork) {

                scrollToPosition(mOffsety);
                mDoWork = false;
            }
        } else {
            scrollToPosition(mOffsety);
            mTotalLayout.setTopHeight(mTitleHeight + mOffsety);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTotalLayout.postOnAnimation(this);
        } else {
            mTotalLayout.postDelayed(this, 10);
        }
    }

    /**
     * 隐藏或显示头部时，当前要滑动到的位置
     *
     * @param position
     */
    protected void scrollToPosition(final float position) {
        mTotalLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mPrePosition == -(int) (position)) {
                    return;
                }
                mTotalLayout.scrollTo(0, -(int) (position));
                mPrePosition = -(int) (position);
            }
        });

    }

    /**
     * 网络回调
     *
     * @param request
     */
    @Override
    public void onHttpResponse(IRequest request) {
        int id = request.getmId();
        switch (id) {
            case HttpCommon.HOME_LIST_BALL://球球回调
                String srpid = sysp.getString(SYSharedPreferences.SUBER_SRPID, "");
                List<HomeBallBean> homes = request.getResponse();
                //获取是否是强取缓存的球球
                boolean forceCache = (Boolean) request.getKeyValueTag("forceCache");
                if (forceCache) {//如果是强取缓存，则认为是要拼接http缓存和订阅数据库中的数据来重建球球数据
                    if (homes.size() > 0) {
                        //处理缓存中返回的球球数据，即，去掉我的头条右边的数据并将数据库中的订阅数据拼接到右边
                        homes = dealWithHomeBalls(homes);
                    }
                    //只有取缓存的时候才根据srpid定位位置

                } else {
                    //如果是非强取缓存，则认为是走http请求回来的球球数据，将其入库
                    saveTitleBall(homes);
                    if (!mVisible) {//如果当前页面不可见，则将其数据暂存，等到页面可见时，在@setUserVisibleHint方法中恢复
                        mHomesCache = homes;
                        //有定位取定位，没定位，取缓存
                        if (TextUtils.isEmpty(srpid)) {
                            mSrpidCache = srpid;
                        }
                        return;
                    }
                }
                doRefreshBall(homes, srpid);
                //解压模板
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream inputStream = MainApplication.getInstance().getResources()
                                    .getAssets().open(TemplateUtils.DEFAULT_TEMPLATE_ZIP_NAME);
                            TemplateUtils.unZipTemplate(inputStream, TemplateUtils.getTemplatePath(getActivity()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                sysp.remove(SYSharedPreferences.SUBER_SRPID);
                break;
            case HttpCommon.SPLASH_GET_IMAGE_REQUEST:
                Object obj = request.getResponse();
                if (obj != null) {
                    getSplashImageSuccess((HttpJsonResponse) obj);
                }
                break;
            case CMainHttp.HTTP_GET_GET_DOWNLOAD_TEST:
                Log.e(this.getClass().getName(), "下载完毕");
                SouYueToast.makeText(getActivity(), "下载完成", Toast.LENGTH_LONG).show();
                break;
            case CMainHttp.HTTP_REQUEST_HOMELIST_DELSUB:
            case HttpCommon.GROUP_DELETE_REQ:
                //取消订阅球球
                HomeBallBean ballBean = (HomeBallBean) request.getKeyValueTag("homeball");//获得当前要取消订阅的球球
                HttpJsonResponse response = request.getResponse();
                try {
                    response.getBodyInt();//这句代码检测是否返回的是数字，如果是数字就不会抛异常，表示取消订阅成功
                    SouYueToast.makeText(getActivity(), ballBean.getTitle() + getString(R.string.subscibe_cancel_success), Toast.LENGTH_LONG).show();
                    //数据库中清除取消订阅的这条数据
                    mSuberDao.clearOne(convertHomeBall(ballBean));
                    sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);
                    initBall();
                    mLoading.dismiss();
                } catch (Exception e) {
                    try {
                        int code = response.getBody().get("result").getAsInt();
                        if (code == 200) {//取消订阅成功
                            SouYueToast.makeText(getActivity(), ballBean.getTitle() + getString(R.string.subscibe_cancel_success), Toast.LENGTH_LONG).show();
                            mSuberDao.clearOne(convertHomeBall(ballBean));
                            sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);
                            initBall();
                            mLoading.dismiss();
                        } else if (code == 500) {//取消订阅失败
                            SouYueToast.makeText(getActivity(), R.string.subscibe_delete_fail, Toast.LENGTH_LONG).show();
                            mLoading.dismiss();
                        } else if (code == 501) {//圈主不能退出圈子
                            SouYueToast.makeText(getActivity(), R.string.cricle_admin_no_quit_setting_text, Toast.LENGTH_LONG).show();
                            mLoading.dismiss();
                        }
                    } catch (Exception ex) {
                        if (response.isOk()) {
                            SouYueToast.makeText(getActivity(), ballBean.getTitle() + getString(R.string.subscibe_cancel_success), Toast.LENGTH_LONG).show();
                            mSuberDao.clearOne(convertHomeBall(ballBean));
                            sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);
                            initBall();
                            mLoading.dismiss();
                        }
                    }
                }
                break;
        }
    }

    /**
     * 向数据库中插入当前返回的球球
     *
     * @param homes
     */
    private void saveTitleBall(List<HomeBallBean> homes) {
        int index = getLastChanged(homes);
        List<SuberedItemInfo> balls = new ArrayList<SuberedItemInfo>();
        int count = homes.size();
        for (int i = index + 1; i < count; i++) {
            HomeBallBean ball = homes.get(i);
            balls.add(convertHomeBall(ball));
        }
//        mSuberDao.clearAll();
        mSuberDao.updateDb(balls);
        mSuberDao.getResultCode();
    }

    /**
     * 转换数据结构，从首页的数据结构转换成数据库中插入的数据结构
     *
     * @param ball
     * @return
     */
    private SuberedItemInfo convertHomeBall(HomeBallBean ball) {
        SuberedItemInfo info = new SuberedItemInfo();
        info.setSrpId(ball.getSrpId());
        info.setCategory(ball.getCategory());
        info.setImage(ball.getImage());
        info.setId(ball.getId());
        info.setTitle(ball.getTitle());
        info.setKeyword(ball.getKeyword());
        info.setUrl(ball.getUrl());
        info.setInvokeType(ball.getInvokeType());
        return info;
    }

    /**
     * 处理缓存返回回来的球球数据，即去掉我的头条右边的数据将数据库中的数据插入到右边
     *
     * @param homes
     * @return
     */
    private List<HomeBallBean> dealWithHomeBalls(List<HomeBallBean> homes) {
        List<HomeBallBean> balls = new ArrayList<HomeBallBean>();
        List<SuberedItemInfo> subs = mSuberDao.queryAll();
        int state = mSuberDao.getResultCode();//这里只有查询给错误状态
        if (subs == null || state == SuberDaoImp.CODE_ERROR) {
            return null;
        }
        int count = subs.size();
        boolean isHave = false;
        int contChangeIndex = getLastChanged(homes);
        for (int i = 0; i < contChangeIndex + 1; i++) {
            isHave = false;
            HomeBallBean ball = homes.get(i);
            String srpid = ball.getSrpId();

            for (int j = 0; j < count; j++) {
                if (srpid != null && srpid.equals(subs.get(j).getSrpId())) {//左边专题在右边有的话要去掉
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                balls.add(homes.get(i));
            }
        }
        for (SuberedItemInfo info : subs) {
            HomeBallBean bean = getHomeBallBeans(info);
            String category = info.getCategory();
            if ("rss".equals(category)) {//报刊类型的球球不在首页显示
                continue;
            }
            balls.add(bean);
        }
        return balls;
    }


    /**
     * 转换数据结构，同@convertHomeBall相反
     *
     * @param info
     * @return
     */
    public HomeBallBean getHomeBallBeans(SuberedItemInfo info) {
        HomeBallBean ball = new HomeBallBean();
        ball.setId(info.getId());
        String category = info.getCategory();
        ball.setCategory(category);
        ball.setImage(info.getImage());
        ball.setKeyword(info.getKeyword());
        ball.setSrpId(info.getSrpId());
        ball.setImage(info.getImage());
        ball.setTitle(info.getTitle());
        ball.setUrl(info.getUrl());
        ball.setInvokeType(info.getInvokeType());
        return ball;
    }

    /**
     * 刷新球球
     *
     * @param bs
     * @param srpid
     */
    private void doRefreshBall(List<HomeBallBean> bs, String srpid) {
        mFirstIn = true;
        if (bs.size() > 0) {
            this.mBeans = new ArrayList<HomeBallBean>();
            this.mBeans.addAll(bs);
//            mCurIndex = -1;
            mFreshCount = 3;
            //左边有的空位
            mLeftCount = mcirclepageindicator.getLeftcount();
            List<HomeBallBean> bean = getSubscription(bs, mLeftCount);
            // 找头条
            setTitleHeader(bean);
            circleAdapter.setData(bean);
            mcirclepageindicator.setAdapter(circleAdapter);
            mIsFreshFromNet = true;
//			circleAdapter.notifyDataSetChanged();
            //球球定位
            int index = 0;
            if (srpid.equals("")) {//如果当前没有设置求取定位
                index = getTitleHeader(bean);
                mMiddlePosition = index;
                mcirclepageindicator.setMiddleStopIndex(mMiddlePosition);
            } else {//如果设置了球球定位，就通过id取球球位置
                index = getTitleHeader(bean, srpid);
                if (index == -1) {//如果没找到就去上次用户设置的或者我的头条
                    index = getTitleHeader(bean);
                }
            }
            mCurIndex = index;
            //设置球球位置
            mcirclepageindicator.setCurrentMiddleItem(index);
            mViewPagerAdapter.notifyDataSetChanged();

        }
        //移除首页球球更新标识
        sysp.remove(SYSharedPreferences.KEY_HOME_UPDATE);
        if (isLoadSplashImg) {
            isLoadSplashImg = false;
            //下载图片
            getSplashImg();
        }
        pbHelp.goneLoadingUI();
        pbHelp.goneLoading();
        Log.v(getClass().getName(), "resumeable goneloading");
    }

    /**
     * 网络错误回调
     *
     * @param _request
     */
    @Override
    public void onHttpError(IRequest _request) {
        int id = _request.getmId();

        if (id == CMainHttp.HTTP_REQUEST_HOMELIST_DELSUB || id == HttpCommon.GROUP_DELETE_REQ) {//取消订阅回调
            SouYueToast.makeText(getActivity(), R.string.neterror, Toast.LENGTH_LONG).show();
            mLoading.dismiss();
        }
        if (mBeans == null || mBeans.size() == 0) {
//			pbHelp.goneLoading();
            pbHelp.showNetError();
        } else {
            pbHelp.goneLoading();
        }
        Log.v(getClass().getName(), "resumeable goneloading");
    }

    /**
     * 生成默认数据，目前没有用到
     *
     * @return
     */
    private ArrayList<HomeBallBean> getHomeDefaultList() {
        ArrayList<HomeBallBean> ballList = new ArrayList<HomeBallBean>();
        HomeBallBean bean_master = new HomeBallBean();
        bean_master.setCategory("masternews");
        bean_master.setTitle("要闻");
        bean_master.setImage("");
        ballList.add(bean_master);
        HomeBallBean bean_head = new HomeBallBean();
        bean_head.setCategory("headline");
        bean_head.setTitle("我的头条");
        bean_head.setImage("");
        ballList.add(bean_head);
        HomeBallBean bean_recom = new HomeBallBean();
        bean_recom.setCategory("recommend");
        bean_recom.setTitle("推荐");
        bean_recom.setImage("");
        ballList.add(bean_recom);
        return ballList;

    }

    /**
     * 目前应用于新浪认证回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int posi = viewPager.getCurrentItem();
        AFragmentBaseView view = mViewPagerAdapter.getCurrentFragment(posi);
        view.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 球球长按回调
     *
     * @param mAdapter
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemLongClick(BaseAdapter mAdapter, View view, int position, long id) {
        int pos = mcirclepageindicator.getCurMiddleIndex();
        if (pos != position) {
            return;
        }

        HomeBallBean bean = circleAdapter.getItem(position);
        //首次进入要闻 屏蔽长按事件 避免功能引导出现问题
        if (bean.getCategory().equals(HomeBallBean.YAOWEN)
                && isVisitNewsFirst()) {//首次进入要闻
//            Toast.makeText(getActivity(), " ======== ", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialogSelect(bean);
        UpEventAgent.onZSHomePagerLongClick(getActivity()); //ZSSDK
        UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_LONG_CLICK);  //Umeng
    }

    /**
     * 球球滑动状态改变回调
     *
     * @param state
     */
    @Override
    public void onScrollChange(int state) {
        if (state == CirlTitleView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_SLIDE);
        }
    }

    /**
     * 首页viewpager的adapter
     */
    class HomeListAdapter extends PagerAdapter {
        // AFragmentBaseView mCache;
        public static final int TYPE_COUNT = 2;//总类型数量
        public static final int TYPE_LIST = 0;//列表页类型
        public static final int TYPE_SPECIAL = 1;//webview页类型
        private int mChildCount = 0;

        Map<Integer, View> mPageViews;
        private Context mContext;
        private LayoutInflater mInflater;
        private AFragmentBaseView savedView;

        public HomeListAdapter(Context _context) {
            mContext = _context;
            mPageViews = new HashMap<Integer, View>();
            mInflater = LayoutInflater.from(mContext);
        }

        public AFragmentBaseView getSavedView() {
            return savedView;
        }

        public void setSavedView(AFragmentBaseView savedView) {
            this.savedView = savedView;
        }

        private View getView(View convertView, int position) {
            AFragmentBaseView view = null;
            if (convertView == null) {
                int type = getPageType(position);
                view = getFragmentView(type);
                view.attachActivity(getActivity());
                view.initView();
            } else {
                view = (AFragmentBaseView) convertView;
            }
            view.setData(mBeans.get(position), SouyueTabFragment.this);
            return view;
        }

        private AFragmentBaseView getFragmentView(int type) {
            View view = null;
            if (type == TYPE_LIST) {
                view = mInflater.inflate(R.layout.fragment_souyue_tab_recommond, null);
            } else if (type == TYPE_SPECIAL) {
                view = mInflater.inflate(R.layout.fragment_souyue_tab_web, null);
            }
            return (AFragmentBaseView) view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            Log.v(this.getClass().getName(), "android position instantiateItem:" + position);
            int type = getPageType(position);
            View view = getView(pullViewFromPool(type), position);
            if (mBeans.get(position).getCategory().equals(HomeBallBean.HEADLINE)) {
                //头条特殊处理
                if (savedView != null) {
                    view = savedView;
                    savedView = null;
                }
            }
            // 记录该view以在destroyItem中能找到
            mPageViews.put(position, view);
            // 添加到view pager
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            Log.v(this.getClass().getName(), "android position destroyItem:" + position);
            View view = mPageViews.get(position);
            // 把要删除的view放到pool里以供复用
            AFragmentBaseView f = (AFragmentBaseView) view;
            if (!f.equals(savedView)) {
                f.setDestory(true);
                pushViewToPool(view, getPageType(position));
            }
            container.removeView(view);
            // 从view pager中删除
        }

        public AFragmentBaseView getCurrentFragment(int position) {
            return (AFragmentBaseView) mPageViews.get(position);
        }

        public void resetFragment(int index) {
            for (Map.Entry<Integer, View> set : mPageViews.entrySet()) {
                int ind = set.getKey();
                if (ind != index) {
                    ((AFragmentBaseView) set.getValue()).unInitView();
                }
            }
        }

        @Override
        public int getCount() {
            mChildCount = 0;
            if (mBeans != null) {
                mChildCount = mBeans.size();
            } else {
                mChildCount = 0;
            }
            return mChildCount;
        }

        public int getPageTypeCount() {
            return TYPE_COUNT;
        }

        /**
         * 获得当前pager的类型
         *
         * @param position
         * @return
         */
        public int getPageType(int position) {
            if (position >= getCount()) {
                return TYPE_LIST;
            }
            String cortary = mBeans.get(position).getCategory();
            if (cortary.equals(HomeBallBean.SPECIAL)) {
                return TYPE_SPECIAL;
            } else {
                return TYPE_LIST;
            }
        }

        /**
         * 判断是否是同一个对象
         *
         * @param view
         * @param o
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        /**
         * 刷新viewpager的所有view
         */
        public void updateView() {
            Set<Map.Entry<Integer, View>> views = mPageViews.entrySet();
            for (Map.Entry<Integer, View> data : views) {
                AFragmentBaseView view = (AFragmentBaseView) data.getValue();
                view.updateViewList();
            }
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        /**
         * viewpager 的view池，引入这个是为了避免每次init的时候创建，而只用复用就行了
         */


        private HashSet<View> mViewPoolNew = new HashSet<View>();

        private View pullViewFromPool(int type) {
            View view = null;
            Iterator<View> iterator = mViewPoolNew.iterator();
            while (iterator.hasNext()) {
                View next = iterator.next();
                if (type == TYPE_LIST) {
                    if (next instanceof CSouyueTabInnerSpecial) {
                        view = next;
                        iterator.remove();
                    }
                } else if (type == TYPE_SPECIAL) {
                    if (next instanceof CSouyueTabWeb) {
                        view = next;
                        iterator.remove();
                    }
                }
            }
            return view;
        }

        private void pushViewToPool(View view, int type) {
            mViewPoolNew.add(view);
        }
//        private Map<Integer,List<View>> mPageViewPool = new HashMap<Integer,List<View>>();
//        private View pullViewFromPool(int type) {
//            View view = null;
//            List<View> list = mPageViewPool.get(type);
//            if (list == null){
//                return null;
//            }
//            for (View v : list) {
//                view = v;
//                break;
//            }
//            if (view != null) {
//                list.remove(view);
//            }
//            return view;
//        }
//
//        private void pushViewToPool(View view,int type) {
//            List<View> list = mPageViewPool.get(type);
//            if (list == null){
//                list = new ArrayList<View>();
//                list.add(view);
//                mPageViewPool.put(type,list);
//            }else if (!list.contains(view)) {
//                list.add(view);
//            }
//        }
    }


    /**
     * 取得球球定位，球球中isstop为1是我的头条所在位置，为2是用户定义的停止位置
     *
     * @param beans
     * @return
     */
    public int getTitleHeader(List<HomeBallBean> beans) {
        int l = beans.size();
        //由先取用户设置的，如果没找到就找默认的
        for (int i = 0; i < l; i++) {
            HomeBallBean b = beans.get(i);
            if (b.getIsStop() == 2) {
                return i;
            }
        }
        for (int i = 0; i < l; i++) {
            HomeBallBean b = beans.get(i);
            if (b.getIsStop() == 1) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 设置首页
     *
     * @param beans
     */
    public void setTitleHeader(List<HomeBallBean> beans) {

        String home_id = CommSharePreference.getInstance().getValue(getCurrentUid(),
                SYSharedPreferences.CLEAR_HOME_MAIN, "");
        int l = beans.size();
        for (int i = 0; i < l; i++) {
            HomeBallBean b = beans.get(i);
            //如果已经设置首页了
            if ((b.getId() + b.getCategory()).equals(home_id)) {
                b.setIsStop(2);
                //将以前设置的停止位置归零
            } else if (b.getIsStop() != 1) {
                b.setIsStop(0);
            }
        }
    }

    private long getCurrentUid() {
        String uid = SYUserManager.getInstance().getUserId();
        long luid;
        if (uid == null || uid.equals("")) {
            luid = 0l;
        } else {
            luid = Long.parseLong(uid);
        }
        return luid;
    }

    /**
     * 找到从哪个球球开始，是本地管理获得的数据
     *
     * @param beans
     * @return
     */
    public int getLastChanged(List<HomeBallBean> beans) {
        int l = beans.size();
        for (int i = 0; i < l; i++) {
            HomeBallBean b = beans.get(i);
            if (b.getLastFixed() == 1) {
                return i;
            }
        }
        return getTitleHeader(beans);
    }

    /**
     * 获得当前球球要定位的位置
     *
     * @param beans
     * @param id
     * @return
     */
    public int getTitleHeader(List<HomeBallBean> beans, String id) {
        int l = beans.size();
        for (int i = 0; i < l; i++) {
            HomeBallBean b = beans.get(i);
            if (b.getSrpId() != null && b.getSrpId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 生成有左右空格的球球
     *
     * @param bs
     * @param subscription
     * @return
     */
    public List<HomeBallBean> getSubscription(List<HomeBallBean> bs,
                                              int subscription) {

        HomeBallBean srp;
        for (int i = 0; i < subscription; i++) {
            srp = new HomeBallBean();
            srp.setCategory("srp");
            srp.setSubscription(true);
            bs.add(0, srp);
        }
        HomeBallBean interest;
        for (int i = 0; i < subscription; i++) {
            interest = new HomeBallBean();
            interest.setCategory("interest");
            if (i == 0) {
                interest.setSubscription(false);
                interest.setSub(true);
            } else {
                interest.setSubscription(true);

            }
            bs.add(bs.size(), interest);
        }
        return bs;
    }

    // 点击球球
    //TODO 添加统计 球球的点击 ball.item.click
    @Override
    public boolean onItemClick(BaseAdapter mAdapter, View view, int position,
                               long id) {
        int middleid = mcirclepageindicator.getCurMiddleIndex();
        HomeBallBean tags = circleAdapter.getItem(position);
        isClicked = true;
        if (position < mMiddlePosition) {     //点击我的头条左边球球
            UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_LEFT_CLICK);   //Umeng
            UpEventAgent.onZSDevBallLeftClick(getActivity(), tags.getTitle(), tags.getSrpId(), tags.getKeyword());
        } else if (position > mMiddlePosition) {   //点击我的头条右边球球
            UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_RIGHT_CLICK);   //Umeng
            UpEventAgent.onZSDevBallRightClick(getActivity(), tags.getTitle(), tags.getSrpId(), tags.getKeyword());
        } else {  //点击我的头条
            UpEventAgent.onZSDevBallMiddleClick(getActivity(), tags.getTitle(), tags.getSrpId(), tags.getKeyword());
        }
        boolean isSubscription = tags.isSubscription();
        boolean isSub = tags.isSub();
        if (middleid != position) {//如果点击的非当前球球
            if (!isSubscription) {//如果是非空格
                if (isSub) {//如果是+号
                    HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SUBSCRIB_SUBALL, getActivity(), R.string.manager_grid_subject + "");
                } else {
                    UpEventAgent.onHomePageInMiddleBall(getActivity());
                    mcirclepageindicator.smoothScrollToMiddleIndex(position);
                }
                return true;
            }
            return false;
        }
        UpEventAgent.onHomePageMiddleBall(getActivity());
        UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_MIDDLE_CLICK);   //Umeng 点击当前在中间的球球
        doSkipBall(tags, isSubscription);
        return false;
    }

    /**
     * 球球跳转事件
     *
     * @param tags
     * @param isSubscription
     */
    private void doSkipBall(HomeBallBean tags, boolean isSubscription) {

        String category = tags.getCategory();
        int invokeType = tags.getInvokeType();
        if (invokeType == BaseInvoke.INVOKE_TYPE_FOCUSNEWS) {
            IntentUtil.gotoSouYueYaoWen(getActivity(), tags.getTitle());
            return;
        }

        if (category.equals(HomeBallBean.SRP)
                || category.equals(HomeBallBean.SPECIAL)) {
            if (isSubscription) {
//                IntentUtil.openSubscribeListActivity(getActivity(),
//                        R.string.manager_grid_subject);
                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SUBSCRIB, getActivity(), R.string.manager_grid_subject + "");
            } else {
//                IntentUtil.gotoSouYueSRP(getActivity(), tags.getKeyword(),
//                        tags.getSrpId(), tags.getImage());
                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SRP, getActivity(), tags.getKeyword(), tags.getSrpId(), tags.getImage());
            }
        } else if (category.equals(HomeBallBean.INTEREST)) {
            if (isSubscription) {
//                IntentUtil.openSubscribeListActivity(getActivity(),
//                        R.string.manager_grid_insterest);
                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SUBSCRIB, getActivity(), R.string.manager_grid_insterest + "");
            } else {
//                UIHelper.showCircleIndex(getActivity(), tags.getSrpId(),
//                        tags.getKeyword(), tags.getTitle(), tags.getImage());
                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_CIRCLEHOME, getActivity(), tags.getSrpId(),
                        tags.getKeyword(), tags.getTitle(), tags.getImage());
            }
        } else if (category.equals(HomeBallBean.YAOWEN)) {
//            IntentUtil.gotoSouYueYaoWen(getActivity());
            HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_MASTERNEWS, getActivity());
        }// 推荐和要闻不跳转
        else if (HomeBallBean.GROUP_NEWS.equals(category)) {
            //跳转到分组首页
            IntentUtil.gotoSubGroupHome(getActivity(), tags.getId() + "", tags.getTitle(), tags.getImage());
        }
    }

    /**
     * viewpager回调
     *
     * @param arg0
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
//		Log.e(this.getClass().getName(), "pageadapter:arg0:" + arg0);
        mVPScrollState = arg0;
        //
        mTotalLayout.setScrollYState(false);
        if (mVPScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
            mCircleState = SCROLL_DEFAULT;
        } else if (mVPScrollState == ViewPager.SCROLL_STATE_IDLE) {
            refreshTab();
            mTotalLayout.setScrollYState(true);
        }
    }

    /**
     * 刷新，即当页面滑到中间时自动刷新调用
     */
    protected void refreshTab() {
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                int index = viewPager.getCurrentItem();
                if (mCurIndex == index || index > mBeans.size()) {
                    return;
                }
//                if (bean.getCategory().equals(HomeBallBean.YAOWEN) && isVisitNewsFirst()){//首次进入要闻,显示引导
//                    mHighlight = initFeatureGuide(); //初始化要闻引导
//                    mHighlight.show();
//                }
//                final int location[] = new int[2];
//                mTitleView.getLocationOnScreen(location);
//                Toast.makeText(getActivity(), "---"+location[0]+"----"+location[1],Toast.LENGTH_SHORT).show();
                //TODO：首页球球更改到中间

                mCurIndex = index;
                AFragmentBaseView fragment = mViewPagerAdapter
                        .getCurrentFragment(mCurIndex);
                mViewPagerAdapter.resetFragment(mCurIndex);
                if (fragment != null) {
                    fragment.pullToRefresh(false);
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mFreshCount > 0) {
            mFreshCount--;
            return;
        }
        if (mCircleState == SCROLL_DEFAULT) {
            mcirclepageindicator.smoothScrollTo(true, arg0, arg1);//平滑滑动到
            mCurChangeIndex = arg0;
            int curindex = mCurChangeIndex;
            if (mCurChangeIndex == mPreChangeIndex) {
                curindex = mCurChangeIndex + 1;
            }
            //根据手指滑动渐变
            float marg = getAlpha(curindex, arg1);
            setEnterAphla(marg);
        }
    }

    /**
     * 获得进入按钮的透明度
     *
     * @param curindex
     * @param arg1
     * @return
     */
    private float getAlpha(int curindex, float arg1) {
        float marg = 1.f;
        boolean curenable = isCurEnable(curindex);//当前球球是否要显示进入按钮
        boolean preenable = isPreEnable();
        if (curenable && !preenable) {
            if (mCurChangeIndex < mPreChangeIndex) {
                marg = 1 - arg1;
            } else {
                marg = arg1;
            }
        }
        if (curenable && preenable) {
            marg = 1.f;
        }

        if (!curenable && !preenable) {
            marg = 0.f;
        }

        if (!curenable && preenable) {
            if (mCurChangeIndex < mPreChangeIndex) {
                marg = arg1;
            } else {
                marg = 1 - arg1;
            }
        }

        return marg;
    }

    /**
     * 设置进入按钮透明度
     *
     * @param aphla
     */
    public void setEnterButtonAphla(float aphla) {
//        int index = mcirclepageindicator.getCurMiddleIndex();
        if (isCurEnable(mCurIndex)) {
            setEnterAphla(aphla);
        }
    }

    /**
     * 优化比较函数,减少字符串比较次数
     *
     * @param index
     * @return
     */
    private boolean isCurEnable(int index) {
        mCurEnable = isEnable(index);
        return mCurEnable;
    }

    private boolean isPreEnable() {
        return mPreEnable;
    }

    private boolean isEnable(int position) {
        boolean able;
        if (position >= mBeans.size()) {
            able = true;
            return able;
        }
        String category = mBeans.get(position).getCategory();
        boolean enable = HomeBallBean.isEnable(category);
        if (HomeBallBean.GROUP_NEWS.equals(category)) {
            return (mGroupInfos.containsKey(mBeans.get(position).getId())
                    && !mGroupInfos.get(mBeans.get(position).getId()).isEmpty());
        }
        return enable;
    }

    @SuppressLint("NewApi")
    public void setEnterAphla(float _aphla) {
        if (_aphla == mPreAlpha) {
            return;
        }
        if (_aphla < 0.01) {
            mHomeEnter.setVisibility(View.GONE);
        } else {
            mHomeEnter.setVisibility(View.VISIBLE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            mHomeEnter.setAlpha(_aphla);
        } else {
            mHomeEnter.setAlpha((int) _aphla * 0xff);
        }
        mPreAlpha = _aphla;
    }

    /**
     * 获得当前中间球球的srpid
     *
     * @return
     */
    public String getCurrentMiddleSrpId() {
        int middleid = mcirclepageindicator.getCurMiddleIndex();
        HomeBallBean tags = circleAdapter.getItem(middleid);
        return tags.getSrpId();
    }

    /**
     * 滑动viewpager停止后的回调
     *
     * @param arg0
     */
    @Override
    public void onPageSelected(final int arg0) {
        int middleid = mcirclepageindicator.getCurMiddleIndex();
        if (arg0 >= mBeans.size()) {
            return;
        }
        final HomeBallBean homeballbean = mBeans.get(arg0);
        if (!isClicked) {
            if (middleid < mMiddlePosition) {     //滑动到我的头条左边球球
                UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_LEFT_SELECTED);
                //Umeng
                UpEventAgent.onZSDevBallLeftSelected(getActivity(), homeballbean.getTitle(), homeballbean.getSrpId(), homeballbean.getKeyword());
            } else if (middleid > mMiddlePosition) {   //滑动到我的头条右边球球
                UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.BALL_RIGHT_SELECTED);   //Umeng
                UpEventAgent.onZSDevBallRightSelected(getActivity(), homeballbean.getTitle(), homeballbean.getSrpId(), homeballbean.getKeyword());
            } else {  //滑动到我的头条

            }
        }
        isClicked = false;
        stopPlayVideo(mPreChangeIndex);
        UmengStatisticUtil.onEvent(getActivity(), ""); //当前是页内滑动
        mPreChangeIndex = arg0;
        if (mPreChangeIndex < mBeans.size()
                && mBeans.get(mPreChangeIndex).getCategory().equals(HomeBallBean.YAOWEN)
                && isVisitNewsFirst()) {//首次进入要闻,显示引导

            mNewsHighlight = initFeatureGuide(); //初始化要闻引导
//            mNewsHighlight.setmClickHighlight(new Highlight.ClickHighlight() {
//                @Override
//                public void onClickHighlight() {
//                    mNewsHighlight.remove();
//                    com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog.reShowDialog();
//                }
//            });
            mNewsHighlight.show();
            CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, "isFirstVisitNews", false);    //存储常量
        } else if (mPreChangeIndex < mBeans.size()
                && mBeans.get(mPreChangeIndex).getCategory().equals(HomeBallBean.HEADLINE)
                && isVisitHeadlineFirst()) {
        } else {
//            removeHighlight();
            if (mNewsHighlight != null) {   //只移除要闻下的提示
                mNewsHighlight.remove();
            }
        }
        final Long groupId = homeballbean.getId();
        //加载分组信息
        if (HomeBallBean.GROUP_NEWS.equals(homeballbean.getCategory())) {
            //如果是分组发请求
            mCurrentGroupId = groupId;
            getFabButtonInfo(homeballbean.getCategory());
        }
        mPreEnable = isEnable(mPreChangeIndex);
        if (mPreEnable) {
            mHomeEnter.setEnabled(true);
            setEnterAphla(1.f);
        } else {
            setEnterAphla(0.f);
        }
        setFabButtonState(HomeBallBean.GROUP_NEWS.equals(homeballbean.getCategory())
                && mGroupInfos.containsKey(groupId), mGroupInfos.get(groupId));
        if (mFirstIn) {//如果第一次进入程序，刷新当前页
            mFirstIn = false;
            refreshTab();
        }
    }

    /**
     * 移除功能提示
     */
    private void removeHighlight() {
        if (mNewsHighlight != null) {
            mNewsHighlight.remove();
        }
        if (mHomeHighlight != null) {
            mHomeHighlight.remove();
        }
        if (mHomeSubHighlight != null) {
            mHomeSubHighlight.remove();
        }
        if (mItemTagHighlight != null) {
            mItemTagHighlight.remove();
        }
    }

    private void getFabButtonInfo(String category) {
        GroupTitleReq.send(HttpCommon.GROUP_TITLE_REQ, new IVolleyResponse() {
            @Override
            public void onHttpResponse(IRequest request) {
                List<SubGroupModel> models = request.getResponse();
                Long gid = Long.decode(request.getKeyValueTag("gid").toString());
                String categary = (String) request.getKeyValueTag("cat");
                mGroupInfos.put(gid, models);
                if (mCurrentGroupId.equals(gid)) {
                    mPreEnable = isEnable(mPreChangeIndex);
                    if (mPreEnable) {
                        mHomeEnter.setEnabled(true);
                        setEnterAphla(1.f);
                    } else {
                        setEnterAphla(0.f);
                    }
                    //没有切换页面的话..
                    setFabButtonState(HomeBallBean.GROUP_NEWS.equals(categary)
                            && mGroupInfos.containsKey(gid) && mCurrentGroupId.equals(gid), mGroupInfos.get(gid));
                }
            }

            @Override
            public void onHttpError(IRequest request) {

            }

            @Override
            public void onHttpStart(IRequest request) {

            }
        }, String.valueOf(mCurrentGroupId), category);
    }

    private void setFabButtonState(boolean isMenu, List<SubGroupModel> subGroupInfo) {
        if (mHomeEnter != null) {
            mHomeEnter.setVisibility(View.VISIBLE);
            int widthPixels = MainApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
            if (isMenu) {
//                if (!mHomeEnter.isOpened()) {
                mHomeEnter.setMode(Fabutton.MODE_MENU);
                mHomeEnter.setBackgroundResource(R.drawable.fab_background);
                mHomeEnter.setOpenAndCloseImage(R.drawable.home_bottom_arrow_right, R.drawable.home_bottom_arrow_left);
                if (subGroupInfo != null) {
                    int tWidth = DeviceUtil.dip2px(getActivity(), 30);
                    int normalColor = Color.parseColor("#282828");
                    int pressedColor = Color.parseColor("#db4743");
                    ColorStateList colorStateList =
                            com.zhongsou.souyue.common.utils.Utils.createColorStateList(normalColor, pressedColor, pressedColor, normalColor);
                    for (int x = 0; x < subGroupInfo.size(); x++) {
                        SubGroupModel model = subGroupInfo.get(x);
                        TextView textView = new TextView(getActivity());
                        textView.setTextColor(colorStateList);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        textView.setText(model.getTitle());
                        textView.setOnClickListener(new TabClickListener(model));
                        mHomeEnter.addInnerButton(textView, x == (subGroupInfo.size() - 1));
                        try {
//                            tWidth += (model.getTitle().length() * DeviceUtil.dip2px(getActivity(), 15));
                            tWidth += computeStringLength(textView);
                            tWidth += DeviceUtil.dip2px(getActivity(), 14);
                        } catch (Exception e) {

                        }
                    }
                    int maxWidth = widthPixels - DeviceUtil.dip2px(getActivity(), 30);
                    if (tWidth > maxWidth) {
                        tWidth = maxWidth;
                    }
                    Log.e("fabutton", "width = " + tWidth);
                    mHomeEnter.setMaxWidth(tWidth);
//                    }
                }
            } else {
                mHomeEnter.setMode(Fabutton.MODE_BUTTON);
                mHomeEnter.setBackgroundResource(R.drawable.homepage_enter_selector);
            }
            mHomeEnter.setOnClickListener(this);
            mHomeEnter.setButtonState();
        }
    }

    private int computeStringLength(TextView view) {
        view.measure(0, 0);
        return view.getMeasuredWidth();
    }

    class TabClickListener implements View.OnClickListener {
        SubGroupModel model;

        public TabClickListener(SubGroupModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            int type = 0;
            BaseInvoke dest = new BaseInvoke();
            if ("srp".equals(model.getCategory())) {
                type = model.getInvokeType() > 0 ? model.getInvokeType() : BaseInvoke.INVOKE_TYPE_SRP_INDEX;
            } else if ("interest".equals(model.getCategory())) {
                type = BaseInvoke.INVOKE_TYPE_INTEREST_INDEX;
                dest.setInterestName(model.getTitle());
            }
            dest.setType(type);
            dest.setSrpId(model.getSrpId());
            dest.setKeyword(model.getKeyword());
            dest.setIconUrl(model.getImage());
            if (type == BaseInvoke.INVOKE_TYPE_FOCUSNEWS) {
                IntentUtil.gotoSouYueYaoWen(getActivity(), model.getTitle());
            } else {
                HomePagerSkipUtils.skip(getActivity(), dest);
            }
        }
    }

    private void stopPlayVideo(int position) {
        AFragmentBaseView view = mViewPagerAdapter.getCurrentFragment(position);
        if (view != null && view instanceof CSouyueTabInnerSpecial) {
            ((CSouyueTabInnerSpecial) view).stopPlayVideo();
        }
    }

    /**
     * 当球球滑动中每到一个球球会回调一次
     *
     * @param position
     */
    @Override
    public void onChangedMiddlePosition(int position) {
        if (position < mMiddlePosition - mLeftCount) {
            showLeft();

        } else if (position > mMiddlePosition + mLeftCount) {
            showRight();

        } else {
            hideAll();
        }
        HomeBallBean bean = circleAdapter.getItem(position);
        if (bean != null) {
            circle_center_title_text.setText(bean.getTitle());
        }
    }

    /**
     * 球球最终停止的位置
     *
     * @param position
     */
    @Override
    public void onChangedMiddlePositionStop(int position) {
        int posi = position;
        if (position >= circleAdapter.getCount()) {
            posi = circleAdapter.getCount() - 1;
        } else if (position < 0) {
            posi = 0;
        }
        final int pos = posi;
        viewPager.post(new Runnable() {// 防止卡顿
            @Override
            public void run() {
                if (circleAdapter != null && viewPager != null) {
                    mCircleState = SCROLL_SETDATA;
                    if (mIsFreshFromNet) {
                        mIsFreshFromNet = false;
                        int index = viewPager.getCurrentItem();
                        int cur = pos - mLeftCount;
                        if (index == cur) {
                            onPageSelected(cur);
                        } else {
                            viewPager.setCurrentItem(pos - mLeftCount,
                                    false);
                        }
                    } else {
                        viewPager.setCurrentItem(pos - mLeftCount);
                    }
                    onChangedMiddlePosition(pos);
                }
            }
        });
    }

    /**
     * 右下角进入按钮被点击
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mcirclepageindicator == null || circleAdapter == null) {
            // 第一次进来的时候circleAdapter还没有创建呢
            return;
        }
        int middleid = mcirclepageindicator.getCurMiddleIndex();
        HomeBallBean tags = circleAdapter.getItem(middleid);
        if (tags == null) {
            return;
        }

        UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.HOME_RIGHT_DOWN_CLICK);   //Umeng
        boolean isSubscription = tags.isSubscription();
        UpEventAgent.onHomePageEnterButton(getActivity());
        doSkipBall(tags, isSubscription);
    }

    /**
     * 注册广播
     */
    private void setReciever() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(MineFragment.logoutAction);
        inf.addAction(ACTION_CHANGER_FONT);
        inf.addAction(REFRESH_HOMEBALL_FROMCACHE);
        inf.addAction(REFRESH_HOMEGROUP_DATA);
        inf.addAction(ACTION_UNSUB_BALL);
//		getLogoutreceiver = new LogoutReceiver();
//		getActivity().registerReceiver(getLogoutreceiver, inf);

        receiver = new UpdateBroadCastRecever();
        getActivity().registerReceiver(receiver, inf);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        Log.v(this.getClass().getName(), "取消注册广播");
        super.onDestroy();
        if (viewPager != null) {
            int index = viewPager.getCurrentItem();
            AFragmentBaseView v = mViewPagerAdapter.getCurrentFragment(index);
            if (v != null) {
                HomeListManager manager = v.getListManager();
                if (manager != null) {
                    manager.onDestroy();
                }
            }
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        resetTitle();
    }

    public class UpdateBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mViewPagerAdapter != null && "update_font".equals(action)) {
                Slog.d("callback", "-------------刷新字体大小");
//				mViewPagerAdapter.notifyDataSetChanged();
                mViewPagerAdapter.updateView();
            } else if (action.equals(MineFragment.logoutAction)) {
                Utils.clearTimeMap();
                sysp.putBoolean(SYSharedPreferences.KEY_USER_UPDATE, true);
                initBall();//切换用户更新球球
//				if (sysp.getBoolean(SYSharedPreferences.KEY_USER_UPDATE, false)) {
//					sysp.remove(SYSharedPreferences.KEY_USER_UPDATE);
//					getBallList(true);
//				}
            } else if (action.equals(REFRESH_HOMEBALL_FROMCACHE)) {
                if (!initTitleBall()) {//取缓存，若是没取到，就取网络.或者第一次进入程序不管有没有缓存都取一次网络
                    getBallList(true, false);
                    mIsLoadCache = false;
                }
//                sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);
            } else if (action.equals(REFRESH_HOMEGROUP_DATA)) {
                //刷新分组数据
                Log.e("refresh group", "刷新 分组");
                mGroupInfos.clear();
                sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);
            } else if (action.equals(ACTION_UNSUB_BALL)) {
                //移除球球
                final String srpId = intent.getStringExtra("srpId");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int delBallCount = -1;
                        List<HomeBallBean> data = circleAdapter.getData();
//                        List<HomeBallBean> dels         = new ArrayList<HomeBallBean>();
                        Iterator<HomeBallBean> iterator = data.iterator();
                        while (iterator.hasNext()) {
                            HomeBallBean bean = iterator.next();
                            if (bean.getSrpId() != null && bean.getSrpId().equals(srpId)) {
                                iterator.remove();
                                mBeans.remove(bean);
                                delBallCount++;
                            }
                        }
//                        for (int i = 0; i < dels.size(); i++) {
//                            if (data.contains(dels.get(i))) {
//                                circleAdapter.getData().remove(dels.get(i));
//                                mBeans.remove(dels.get(i));
//                            }
//                        }
                        circleAdapter.notifyDataSetChanged();
                        if (delBallCount != -1) {
                            AFragmentBaseView currentFragment = mViewPagerAdapter.getCurrentFragment(mCurIndex);
                            mViewPagerAdapter.setSavedView(currentFragment);
                            mViewPagerAdapter.notifyDataSetChanged();
                        }
                    }
                }, 600);
            }
        }
    }

    /**
     * 当souyuetabfragment被显示到屏幕或者不显示时的回调
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisible = isVisibleToUser;
        //如果当前fragment可见并且创建方法被调用过了（第一次暂时这个fragment的时候这儿方法优先于oncreate方法调用的）
        if (isVisibleToUser && mCreate) {
            mResumeAble = true;//设置onResume不会再调用initBall了
            Log.v(getClass().getName(), "resumeable " + mResumeAble);
            if (mTotalLayout != null) {
                mTotalLayout.setIsCanScroll(true);//设置首页view可以滑动隐藏
            }
            if (mHomesCache != null) {//如果网络返回数据被缓存了就直接加载
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doRefreshBall(mHomesCache, mSrpidCache);
                        mHomesCache = null;
                        mSrpidCache = null;
                    }
                });
            } else {
                initBall();//初始化首页球球
            }
        } else {
            mResumeAble = false;
            if (mTotalLayout != null) {
                mTotalLayout.setIsCanScroll(false);
                scrollToPosition(0);
            }
        }
        //下面代码在tab切换的过程中如果点击不感兴趣，不能点击
        if (viewPager != null) {
            int index = viewPager.getCurrentItem();
            AFragmentBaseView v = mViewPagerAdapter.getCurrentFragment(index);
            if (v != null) {
                HomeListManager manager = v.getListManager();
                if (isVisibleToUser) {
                    if (manager != null) {
                        manager.setmRefreshing(false);
                    }
                } else {
                    if (manager != null) {
                        manager.setmRefreshing(true);
                    }
                }
            }
        }
    }

    /**
     * 显示首页长按选择设为首页还是取消订阅对话框
     *
     * @param _bean
     */
    private void showDialogSelect(final HomeBallBean _bean) {
        final CommDialog dialog = new CommDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_circle_select_dialog, null);
        dialog.setTitleColor(getResources().getColor(R.color.reply_title_color))
                .setView(view)
                .setMiddleLineViewColor(getResources().getColor(R.color.homepage_dialog_line))
                .setPositiveButton(R.string.dialog_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
        TextView tv1 = (TextView) view.findViewById(R.id.tv_home_dialog_setmain);
        TextView tv2 = (TextView) view.findViewById(R.id.tv_home_dialog_dissubcrib);
        if (!HomeBallBean.isEnableSub(_bean.getCategory())) {
            tv2.setEnabled(false);
        }
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.setTitle()
                dialog.dismiss();
                String str = getString(R.string.home_title_dialog_comfirm_message, _bean.getTitle());
//                Drawable able = getResources().getDrawable(R.drawable.)
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.homepage_dialog_confirm_message, null);
                ImageView iv = (ImageView) view.findViewById(R.id.home_title_dialog_subicon);
                TextView tv = (TextView) view.findViewById(R.id.tv_home_dialog_message);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, _bean.getImage(), iv, MyDisplayImageOption.homeTitle);
                tv.setText(str);
                final CommDialog confirm = new CommDialog(getActivity());
                confirm.setTitle(R.string.home_title_dialog_setmain)
                        .setTitleColor(getResources().getColor(R.color.reply_title_color))
                        .setView(view)
                        .setMiddleLineViewColor(getResources().getColor(R.color.homepage_dialog_line))
                        .setNegativeButton(R.string.tg_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirm.dismiss();
                                CommSharePreference.getInstance().putValue(getCurrentUid(),
                                        SYSharedPreferences.CLEAR_HOME_MAIN, _bean.getId() + _bean.getCategory());
                                List<HomeBallBean> beas = circleAdapter.getData();
                                setTitleHeader(beas);
                                mMiddlePosition = getTitleHeader(beas);
                                mcirclepageindicator.setMiddleStopIndex(mMiddlePosition);

                                SouYueToast.makeText(getActivity(), getString(R.string.homepage_sethome_sucess), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton(R.string.dialog_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirm.dismiss();
                            }
                        })
                        .show();

            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mLoading = new CustomProgress(context, "", false, null);
//                mMainHttp.doDeleteSuberItem(CMainHttp.HTTP_REQUEST_HOMELIST_DELSUB,SYUserManager.getInstance().getToken(),_bean,SouyueTabFragment.this);
                if (HomeBallBean.GROUP_NEWS.equals(_bean.getCategory())) {
                    GroupDeleteReq req = new GroupDeleteReq(HttpCommon.GROUP_DELETE_REQ, SouyueTabFragment.this);
                    req.setParams(String.valueOf(_bean.getId()));
                    req.addKeyValueTag("homeball", _bean);
                    mMainHttp.doRequest(req);
                } else {
                    SubUpdateRequest.send(CMainHttp.HTTP_REQUEST_HOMELIST_DELSUB, SYUserManager.getInstance().getToken(), _bean, SouyueTabFragment.this);
                }
                //取消订阅事件 TODO 加取消订阅统计？
//                UpEventAgent.onSrpUnsubscribe(getActivity(),_bean.getKeyword(),_bean.getSrpId());
                mLoading.show();
            }
        });
    }


    /**
     * 目前首页球球有三个逻辑
     * 1、第一次进入程序时，强取一次网络数据
     * 2、从订阅回到首页时，若是有数据就直接用订阅页面带回来的数据，若是没有就走原来逻辑
     */
    private void initBall() {
        //取缓存
//        mCurIndex = -1;
        if (sysp.getBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_USER_UPDATE, false)) {//第一次进入程序更新
            sysp.putBoolean(SYSharedPreferences.KEY_HOME_UPDATE, true);//标记球球需要从网络更新    TODO
            sysp.remove(SYSharedPreferences.KEY_UPDATE);
            sysp.remove(SYSharedPreferences.KEY_UPDATE_CIRCLE);
            if (sysp.getBoolean(SYSharedPreferences.KEY_USER_UPDATE, false)
                    || sysp.getBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, false)) {
                Utils.clearTimeMap();
//                String key = getCacheKey();
//                mMainHttp.removeCache(key);//清除球球缓存
//                mSuberDao.clearAll();//清除订阅数据库
                mIsLoadCache = true;//如果切换用户了，强取一次网络
                sysp.remove(SYSharedPreferences.KEY_USER_UPDATE);
                sysp.remove(SYSharedPreferences.KEY_REGISTERSUCCESS);

            }
            // }
        }

        if (sysp.getBoolean(SYSharedPreferences.KEY_HOME_UPDATE, false) || mIsLoadCache) {
            boolean isSend = false;
            if (!initTitleBall()) {//取缓存，若是没取到，就取网络.或者第一次进入程序不管有没有缓存都取一次网络
                getBallList(true, false);
                mIsLoadCache = false;
                isSend = true;
            }
            //isLoad是判断前面已经发了请求了吗？如果发了 这里就不处理了
            //mIsloadCahce是表示第一次进入程序会向网络发送请求获取球球数据
            if (mIsLoadCache && !isSend) {
                mIsLoadCache = false;
                getBallList(true, false);
            }
            if (mBeans != null && mBeans.size() > 0) {
                mBeans.clear();
                mViewPagerAdapter.notifyDataSetChanged();
            }
            if (mHomeEnter != null) {
                mHomeEnter.setMode(Fabutton.MODE_BUTTON);
            }
            pbHelp.showLoadingUI();
        }
    }

    private void getSplashImg() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//				mMainHttp.getSplashImage(CMainHttp.HTTP_GET_GET_SPLASH_IMAGE,
//						SouyueTabFragment.this);
                SplashImageRequest splashImgReq = new SplashImageRequest(
                        HttpCommon.SPLASH_GET_IMAGE_REQUEST,
                        SouyueTabFragment.this).setParams();
                mMainHttp.doRequest(splashImgReq);
            }
        }, 3000);
    }

    private void getSplashImageSuccess(HttpJsonResponse res) {
//        JsonObject result = res.getBody();
        SplashAd splashAd = new Gson().fromJson(res.getBody(), new TypeToken<SplashAd>() {
        }.getType());
        lastSplashUrl = sysp.getString(SplashAd.LAST_SPLASH_IMAGE_URL, "");
        final String lastId = sysp.getString(SplashAd.SPLASH_LASTID, "");
        try {
            String startTime = splashAd.getExpiredStartTime();
            String endTime = splashAd.getExpiredEndTime();
            String url = splashAd.getUrl();
            String id = splashAd.getId();
            long offsetTime = splashAd.getOffsetTime();

            long startTimeValue = Utils.getTimeMillis(startTime, DATE_FORMAT_STR);
            long endTimeValue = Utils.getTimeMillis(endTime, DATE_FORMAT_STR);
            long currentTime = System.currentTimeMillis() + offsetTime;

            sysp.putInt(SplashAd.SPLASH_DISPLAY_TIME, splashAd.getExhibitionTime());//启动图展示时间
            sysp.putInt(SplashAd.SPLASH_IS_DISPLAY_JUMP, splashAd.getIsJump());//是否显示跳转按钮
            sysp.putInt(SplashAd.SPLASH_JUMP_TYPE, splashAd.getJumpType());//跳转类型
            sysp.putString(SplashAd.SPLASH_JUMP_URL, splashAd.getJumpUrl());//跳转Url
            sysp.putString(SplashAd.SPLASH_ID, splashAd.getId()); //广告id
            if (startTimeValue == 0 && endTimeValue == 0 && StringUtils.isEmpty(url)) {// 过期
                deleteCacheFile();
            } else {
                if (currentTime > startTimeValue && currentTime < endTimeValue) {
                    if (StringUtils.isNotEmpty(url) && !url.equals(lastSplashUrl)) {
                        imageLoader.loadImage(url, SplashActivity.options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String url,
                                                          View arg1, Bitmap arg2) {
                                deleteCacheFile();
                                sysp.putString(SplashAd.SPLASH_LASTID, lastId);
                                sysp.putString(SplashAd.LAST_SPLASH_IMAGE_URL, url);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除启动页缓存图片及配置
     */
    private void deleteCacheFile() {
        if (StringUtils.isNotEmpty(lastSplashUrl)) {
            File cacheFile = imageLoader.getDiskCache().get(lastSplashUrl);
            if (cacheFile != null && cacheFile.exists()) {
                cacheFile.delete();
            }
            sysp.remove(SplashAd.LAST_SPLASH_IMAGE_URL);
            sysp.remove(SplashAd.SPLASH_DISPLAY_TIME);
            sysp.remove(SplashAd.SPLASH_IS_DISPLAY_JUMP);
            sysp.remove(SplashAd.SPLASH_JUMP_TYPE);
            sysp.remove(SplashAd.SPLASH_JUMP_URL);
            sysp.remove(SplashAd.SPLASH_LASTID);
            sysp.remove(SplashAd.SPLASH_ID);
        }
    }

    public List<SubGroupModel> getGroupInfos(Long groupId) {
        return mGroupInfos.get(groupId);
    }

    /**
     * 设置要闻中新特性引导
     */
    private Highlight initFeatureGuide() {

        final int location[] = new int[2];
        mcirclepageindicator.getLocationOnScreen(location);
//        Toast.makeText(getActivity(), "1---"+location[0]+"----"+location[1],Toast.LENGTH_SHORT).show();

        Highlight highLight = Highlight.getInstance(getActivity())
//                .anchor(mRootView)
                .shadow(false)

                .maskColor(getActivity().getResources().getColor(R.color.transparent_75))
                .addHighlight(mHomeEnter, R.layout.tip_right_button,
                        new Highlight.OnPosCallback() {
                            @Override
                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
                                int btnLocation[] = new int[2];
                                mHomeEnter.getLocationInWindow(btnLocation);
                                int btnWidth = mHomeEnter.getMeasuredWidth();
                                int btnHeight = mHomeEnter.getMeasuredHeight();
//                                marginInfo.bottomMargin = DeviceInfo.getScreenHeight()- btnLocation[1] - btnHeight - DeviceUtil.dip2px(getActivity(), 10);
                                marginInfo.topMargin = mTipEnterOffsetY - DeviceUtil.dip2px(getActivity(), 91);
//                                marginInfo.rightMargin = DeviceInfo.getScreenWidth() - btnLocation[0] - btnWidth - DeviceUtil.dip2px(getActivity(), 10);
                                marginInfo.leftMargin = mEnterOffsetX - DeviceUtil.dip2px(getActivity(), 120);
                            }
                        })
                .addHighlight(mHomeEnter, R.layout.tip_news,
                        new Highlight.OnPosCallback() {
                            @Override
                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
//                                marginInfo.topMargin = 48 + DeviceUtil.px2dip(getActivity(), Utils.getStatusHeight(getActivity()));
//                                marginInfo.topMargin = DeviceUtil.dip2px(getActivity(), 48);
                                marginInfo.topMargin = location[1] - Utils.getStatusHeight(getActivity());
                                marginInfo.leftMargin = DeviceInfo.getScreenWidth() / 2 - DeviceUtil.dip2px(getActivity(), 62);
//                                Toast.makeText(getActivity(), "2=="+DeviceInfo.getScreenWidth(), Toast.LENGTH_SHORT).show();
                            }
                        });
        return highLight;
    }

    /**
     * 设置我的头条中新特性引导
     */
    private Highlight initHeadlineFeatureGuide() {

        Highlight highLight = new Highlight(getActivity())
//                .anchor(mRootView)
                .shadow(false)
                .maskColor(getActivity().getResources().getColor(R.color.transparent_75))
                .addHighlight(mHomeEnter, R.layout.tip_home_headline,
                        new Highlight.OnPosCallback() {
                            @Override
                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
                                int location[] = new int[2];
                                mcirclepageindicator.getLocationOnScreen(location);
                                marginInfo.topMargin = location[1] - Utils.getStatusHeight(getActivity()) - DeviceUtil.dip2px(getActivity(), 10);
                                marginInfo.leftMargin = DeviceInfo.getScreenWidth() / 2 - DeviceUtil.dip2px(getActivity(), 134);
                            }
                        });
        highLight.setNextHighlight(mHomeSubHighlight);
        return highLight;
    }

    /**
     * 设置我的头条中订阅按钮的新特性引导
     */
    private Highlight initHomeSubFeatureGuide() {

        Highlight highLight = new Highlight(getActivity())
//                .anchor(mRootView)
                .shadow(false)
                .maskColor(getActivity().getResources().getColor(R.color.transparent_75))
                .addHighlight(mHomeEnter, R.layout.tip_home_sub,
                        new Highlight.OnPosCallback() {
                            @Override
                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
                                int btnLocation[] = new int[2];
                                btnAddSub.getLocationOnScreen(btnLocation);
                                marginInfo.topMargin = 0;
                                marginInfo.leftMargin = btnLocation[0] - DeviceUtil.dip2px(getActivity(), 150);
                            }
                        });
        highLight.setNextHighlight(mItemTagHighlight);
        return highLight;
    }

    /**
     * 设置我的头条中每个条目右下角标签的新特性引导
     */
    private Highlight initItemTagFeatureGuide() {

        Highlight highLight = new Highlight(getActivity())
//                .anchor(mRootView)
                .shadow(false)
                .maskColor(getActivity().getResources().getColor(R.color.transparent_75))
                .addHighlight(mHomeEnter, R.layout.tip_home_tag,
                        new Highlight.OnPosCallback() {
                            @Override
                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
                                int itemWidth = CircleUtils.getDeviceWidth(context);
                                int width = (itemWidth - DeviceUtil.dip2px(context, 48)) / 3;
                                int height = (int) ((2 * width) / 3);

                                int location[] = new int[2];
                                mcirclepageindicator.getLocationOnScreen(location);
//                                marginInfo.bottomMargin = DeviceInfo.getScreenHeight()- btnLocation[1] - btnHeight - DeviceUtil.dip2px(getActivity(), 10);
                                marginInfo.topMargin = mcirclepageindicator.getHeight() + location[1] + height - DeviceUtil.dip2px(getActivity(), 40);
//                                marginInfo.rightMargin = DeviceInfo.getScreenWidth() - btnLocation[0] - btnWidth - DeviceUtil.dip2px(getActivity(), 10);
                                marginInfo.leftMargin = DeviceInfo.getScreenWidth() - DeviceUtil.dip2px(getActivity(), 203);
                            }
                        });

        highLight.setmClickHighlight(new Highlight.ClickHighlight() {
            @Override
            public void onClickHighlight() {
                SYSharedPreferences.getInstance().putBoolean(Highlight.SHOW_TIP_NAME, true);
                SubRecommendDialog.showDialog(getActivity(), true, 0L, 0L, false);
            }
        });
        return highLight;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        hideState = hidden;
        if (hidden) {
            removeHighlight();
        } else {
            setHighlight();
        }
    }

    private void setHighlight() {
        if (hideState) return;
        if (mBeans != null
                && mPreChangeIndex < mBeans.size()
                && mBeans.get(mPreChangeIndex).getCategory().equals(HomeBallBean.HEADLINE)
                && isVisitHeadlineFirst()) {

            mItemTagHighlight = initItemTagFeatureGuide();
            mHomeSubHighlight = initHomeSubFeatureGuide();
            mHomeHighlight = initHeadlineFeatureGuide();

            if (SubRecommendDialog.getIsShowingMe()) {
                SubRecommendDialog instance = SubRecommendDialog.getInstance();
                if (instance != null) {
                    instance.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mHomeHighlight.show();
                            CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, "isVisitHeadlineFirst", false);    //存储常量
                        }
                    });
                }
            } else {
                mHomeHighlight.show();
                CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, "isVisitHeadlineFirst", false);    //存储常量
            }
        }
    }

    /**
     * 判断是否第一次进入要闻
     *
     * @return true ： 是；false ：否
     */
    private boolean isVisitNewsFirst() {
        boolean isFirst;
        isFirst = CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, "isFirstVisitNews", true);
        return isFirst;
    }

    /**
     * 判断是否第一次进入我的头条
     *
     * @return true ： 是；false ：否
     */
    private boolean isVisitHeadlineFirst() {
        boolean isFirst;
        isFirst = CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, "isVisitHeadlineFirst", true);
        return isFirst;
    }
}
