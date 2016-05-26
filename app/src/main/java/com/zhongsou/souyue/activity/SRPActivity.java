package com.zhongsou.souyue.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.UmengDefParams;
import com.zhongsou.souyue.adapter.SRPFragmentPagerAdapter;
import com.zhongsou.souyue.adapter.SouyueAdapter;
import com.zhongsou.souyue.adapter.SrpPopTitleAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.activity.DetailModuleActivity;
import com.zhongsou.souyue.circle.fragment.CircleBarFragment;
import com.zhongsou.souyue.circle.fragment.EssencePostFragment;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.view.PagerSlidingTabStrip;
import com.zhongsou.souyue.circle.view.PagerSlidingTabStrip.OnTextViewclick;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.fragment.BlogFragment;
import com.zhongsou.souyue.fragment.ChatRoomFragment;
import com.zhongsou.souyue.fragment.CommonFragment;
import com.zhongsou.souyue.fragment.ForumFragment;
import com.zhongsou.souyue.fragment.IndexFragment;
import com.zhongsou.souyue.fragment.KunlunJueFragment;
import com.zhongsou.souyue.fragment.MySharesFragment;
import com.zhongsou.souyue.fragment.PhotoSearchFragment;
import com.zhongsou.souyue.fragment.QAFragment;
import com.zhongsou.souyue.fragment.RecommendFragment;
import com.zhongsou.souyue.fragment.SRPFragment;
import com.zhongsou.souyue.fragment.SRPSelfCreateFragment;
import com.zhongsou.souyue.fragment.WebpageFragment;
import com.zhongsou.souyue.fragment.WeiboFragment;
import com.zhongsou.souyue.fragment.XiaoDanganFragment;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.ADInfo;
import com.zhongsou.souyue.module.AdList;
import com.zhongsou.souyue.module.AdListItem;
import com.zhongsou.souyue.module.BoZhu;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.HotTopic;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.NewsCount;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.ShortCutInfo;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.SubscribeBack;
import com.zhongsou.souyue.module.TitleBarSource;
import com.zhongsou.souyue.module.Weibo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.circle.CircleIsAdminReq;
import com.zhongsou.souyue.net.detail.AddFavoriteReq;
import com.zhongsou.souyue.net.detail.DetailUpReq;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.srp.AdClickRequest;
import com.zhongsou.souyue.net.srp.AdListRequest;
import com.zhongsou.souyue.net.srp.SrpListRequest;
import com.zhongsou.souyue.net.srp.SrpNavRequest;
import com.zhongsou.souyue.net.sub.SubAddReq;
import com.zhongsou.souyue.net.sub.SubCheckRequest;
import com.zhongsou.souyue.net.sub.SubDeleteReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.ac.SrpWebViewActivity;
import com.zhongsou.souyue.service.LogService;
import com.zhongsou.souyue.service.download.DownloadAlert;
import com.zhongsou.souyue.service.download.DownloadService;
import com.zhongsou.souyue.share.SRPShareMenu;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.trade.net.TradeUrlConfig;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.ViewPagerWithTips;
import com.zhongsou.souyue.ui.indicator.CirclePageIndicator;
import com.zhongsou.souyue.ui.indicator.PageIndicator;
import com.zhongsou.souyue.ui.indicator.TabPageIndicator.OnTabReselectedListener;
import com.zhongsou.souyue.ui.lib.DialogHelper;
import com.zhongsou.souyue.ui.lib.DialogPlus;
import com.zhongsou.souyue.ui.lib.Holder;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.uikit.FavoriteAlert;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ActivityUtils;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.FastDoubleCliceUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import com.zhongsou.juli.factory.Advert;
//import com.zhongsou.juli.factory.AdvertFactory;

public class SRPActivity extends BaseActivity implements OnClickListener,
        OnTabReselectedListener, PickerMethod,
        IShareContentProvider, OnTextViewclick {
    private ViewPagerWithTips mViewPager;
    // private OuterTabPageIndicator hsv_widgets;
    public List<NavigationBar> navs;
    // private NavigationAdapter navigationAdapter;
    public String keyword, srpId, imgUrl;
    private String mUrl, currentTitle, md5, shareImageUrl;
    public int generation;
    private boolean showMenu; // 是否显示二级导航
    private SRPFragmentPagerAdapter fragmentPagerAdapter;
    private ArrayList<SRPFragment> srpFragments;
    private ArrayList<View> views;
    private View longtengView;
    private ViewFlipper vf;
    private int replaceBg;
    private TextView subBtn;
    private ImageButton titleShare; // 分享
    private TextView titleText; // 标题
    private boolean isSearch;
    private boolean hasIndex; // 是否有首页fragment
    private RelativeLayout titlebar_bg;
    private ImageButton goBack;
    private ViewGroup rightParentView;
    private String sup = "";
    private int subScribeState;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private PopupWindow mTabMsgPopupWindow;
    private View barRoot;
    private SRPShareMenu srpShareMenu;
    private PopupWindow popWindow = null;// 弹出框
    private boolean isOpenPop = false;// pop是否弹出
    private SrpPopTitleAdapter srpPopTitleAdapter;// 弹出框gridview适配器
    private ImageLoader imageLoader;
    private String contentStr = "";
    private String srpShareUrl = "";
    private View popupView;
    private ImageView ivSubscribeStateIcon;// 订阅、退订图标
    private TextView tvSubscribeStateText;// 订阅、退订文字
    private LinearLayout llSubscribeStateLayout;// 订阅、退订Layout

    // 切换微件
    public static final String SWITCH_WIDGET = "SWITCH_WIDGET";
    private View pop_zuti;
    private ImageView popZutiIcon;
    private TextView popZutiTitle;
    private int version;
    private View view_translucent;
    private boolean backHome = false;

    private boolean isExpired;
    private boolean isAdmin;

    // add by trade
    public LinearLayout bottom_bar; // 底部点赞、分享菜单 zhaobo 2014-10-31
    private View comment_layout, upLayout;
    private ImageButton ib_collect, web_sre_up, ib_share, comment_icon;
    private TextView tv_read_commentcount, upCount;
    private ShareMenuDialog mShareMenuDialog;
    public static final int START_FOR_RESULT = 1010;
    private boolean hasFavorited, hasUp;
    private NewsCount newsCount;
    public ShareContent content;
    private SsoHandler mSsoHandler;
    private Bitmap imageBitmap;
    private String utype;
    private String shortUrl;
    public String currentFragmentWebUrl = "";
    private String shareType; // 活动微件分享类型
    private Long newsId;
    private String from;
    private boolean isFromPush;
    // private boolean mIsFinish;
    public static List<TitleBarSource> titleBarSourceList = new ArrayList<TitleBarSource>();

    private SearchResult searchResult;// 移动统计要用，所以被抽取到外面来了
    public static final String subScribe = "com.zhongsou.souyue.subScribe";

    private CustomWebView mWebView;
    private SearchResultItem sri;
    private DialogPlus dialog;
    private Holder holder;

    private int mAllowEdit = 1;

    private UpdateBroadCastRecever receiver = new UpdateBroadCastRecever();
    private IntentFilter mFilter = new IntentFilter("update_font");

    private SuberDao suberDao;
    private boolean tabClickFlag;
    private String opSource;    //统计SRP的来源 add by YanBin
    private CMainHttp mainHttp;
    private ImageButton subBtn2;
    private boolean isSubscrible;
    private boolean isClickable = true;
    private boolean subIsRunning = false;    // 是否正在订阅或退订中
    private Intent backIntent;
    private LocalBroadcastManager localBroadcastManager;

    public class UpdateBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (fragmentPagerAdapter != null && "update_font".equals(action)) {
                fragmentPagerAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ConfigApi.isPrintWebViewLogToSDCard) {
            // Add by ygp 收集log日志服务
            startService(new Intent(this, LogService.class));
        }
//        http = new Http(this);
        mainHttp = CMainHttp.getInstance();
        imageLoader = ImageLoader.getInstance();
//        http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
        sup = sysp.getString(SYSharedPreferences.UM_SUPERSEARCHURL,
                UmengDefParams.SUPERSEARCHURL_VALUE);
        setContentView(R.layout.srp_main);
        getDataFromIntent();
        // add by trade
        if (keyword.equals(TradeUrlConfig.MEGAGAME_SEARCH_KEYWORD)) { // 超级搜索
            replaceBg = 4;
        }
        initBarView();
        setBarData();
        initAllViews();
        pbHelp = new ProgressBarHelper(this, null);
        pbHelp.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                pbHelp.showLoading();
                sendSearchResultRequest(mUrl, 0);
            }
        });
        navs = new ArrayList<NavigationBar>();
        srpFragments = new ArrayList<SRPFragment>();
//        initsendSearchResultRequest(http, mUrl, 0);
        initsendSearchResultRequest(mUrl, 0);
        setLayouListener();
        mViewPager.setOnBeginListener(new ViewPagerWithTips.OnBeginListener() {
            @Override
            public void onBeginListener() {
                onBackPressClick(null);
            }

        });
        // 注册广播，用于首页，js onclick跳转相应的微件
        IntentFilter filter = new IntentFilter();
        filter.addAction(SWITCH_WIDGET);
        registerReceiver(switchWidgetReceiver, filter);
        registerReceiver(receiver, mFilter);
        // 统计 srp关键词查看
        UpEventAgent.onSrpSearch(this, keyword, srpId);
        suberDao = new SuberDaoImp();

        /**
         * 聚力广告
         */
//        AdvertFactory factory = new AdvertFactory();
//        Advert advert = factory.getBannerAd(this);
//        advert.showAdvert();
//        AdvertFactory.getInstance().getBannerAd(this).show();
    }

    private void initBarView() {
        ViewStub barStub = null;
        switch (replaceBg) {
            case 1:
                barStub = (ViewStub) findViewById(R.id.srp_bar_supershare_layout);
                break;
            case 2:
                barStub = (ViewStub) findViewById(R.id.srp_bar_cm_supershare_layout);
                break;
            case 4:// 大赛搜索
                barStub = (ViewStub) findView(R.id.srp_bar_cm_super_search_layout);
                break;
            default:
                barStub = (ViewStub) findViewById(R.id.srp_bar_layout);
                break;
        }
        barRoot = barStub.inflate();
        goBack = (ImageButton) barRoot.findViewById(R.id.goBack);
        titlebar_bg = (RelativeLayout) barRoot;
        titleText = (TextView) findViewById(R.id.activity_bar_title);

        subBtn2 = (ImageButton) barRoot.findViewById(R.id.btn_detail_subscribe1);
        rightParentView = (ViewGroup) barRoot
                .findViewById(R.id.srp_title_right_layout);

        subBtn = (TextView) barRoot.findViewById(R.id.text_btn);
        subBtn.setVisibility(View.GONE);
        subBtn.setOnClickListener(this);
        subBtn.setEnabled(false);
        // 菜单订阅控件实例化
        dynamicChangeMenu();

        titleShare = (ImageButton) findViewById(R.id.ib_srp_share);
        if (mUrl != null && mUrl.length() > 0)
            titleShare.setVisibility(View.GONE);
        titleShare.setOnClickListener(this);
        if (!ConfigApi.isSouyue()) {
            initTradeView();
        }
    }

    /**
     *
     */
    private void dynamicChangeMenu() {
        popupView = getLayoutInflater().inflate(R.layout.ac_srp_pop, null);
        llSubscribeStateLayout = (LinearLayout) popupView
                .findViewById(R.id.pop_tuiding);
        ivSubscribeStateIcon = (ImageView) popupView
                .findViewById(R.id.iv_subscribe_state_icon);
        tvSubscribeStateText = (TextView) popupView
                .findViewById(R.id.tv_subscribe_state_text);
        llSubscribeStateLayout.setVisibility(View.GONE);
        pop_zuti = popupView.findViewById(R.id.pop_zuti);
        popZutiIcon = (ImageView) popupView.findViewById(R.id.pop_zuti_icon);
        popZutiTitle = (TextView) popupView.findViewById(R.id.pop_zuti_title);
        pop_zuti.setOnClickListener(this);
    }

    int currentTitleMargin = 0;
    private OnGlobalLayoutListener victim;

    private void resizeTitleWidth() {
        if (replaceBg != 0)
            return;

        if (currentTitleMargin >= rightParentView.getWidth()) {
            return;
        }

        currentTitleMargin = Math.max(goBack.getWidth(),
                rightParentView.getWidth());
        MarginLayoutParams newParams = (LinearLayout.LayoutParams) titleText
                .getLayoutParams();
        newParams.leftMargin = currentTitleMargin;
        newParams.rightMargin = currentTitleMargin;
        titleText.setLayoutParams(newParams);
    }

    private void setBarData() {
        goBack.setScaleType(ScaleType.CENTER);
        titleShare.setScaleType(ScaleType.CENTER);
        // if (replaceBg != 0) {
        switch (replaceBg) {
            case 1:
                subBtn.setTag(new Long(-2));
                subBtn.setVisibility(View.VISIBLE);
                subBtn.setEnabled(true);
                subBtn.setBackgroundResource(R.drawable.btn_super_share_search);
                subBtn.setText("");
                llSubscribeStateLayout.setTag(new Long(-2));// 订阅/退订菜单添加标志
                goBack.setImageResource(R.drawable.btn_super_share_goback);
                titleText.setText("");
                titleShare.setImageResource(R.drawable.btn_super_share_share);
                break;
            case 2:
                subBtn.setTag(new Long(-3));
                subBtn.setVisibility(View.VISIBLE);
                subBtn.setEnabled(true);
                subBtn.setBackgroundResource(R.drawable.btn_super_cm_share_search);
                subBtn.setText("");
                llSubscribeStateLayout.setTag(new Long(-3));
                goBack.setImageResource(R.drawable.btn_super_share_goback);
                titleText.setText("");
                titleShare.setImageResource(R.drawable.btn_super_share_share);
                break;
            case 4:
                titleShare
                        .setImageResource(R.drawable.megagame_btn_super_cm_share_share);
                subBtn.setVisibility(View.VISIBLE);
                subBtn.setEnabled(true);
                subBtn.setTag(new Long(-4));
                subBtn.setBackgroundResource(R.drawable.btn_cm_megagame_search_selector);
                goBack.setImageResource(R.drawable.megagame_btn_goback);
                titleText
                        .setBackgroundResource(R.drawable.zh_ac_title_super_image);
                break;
            default:

                titleText.setText(keyword);
                titleText.setTextColor(getResources().getColor(
                        R.color.title_text_color));
                titleShare.setVisibility(View.GONE);
                victim = new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // resizeTitleWidth();
                    }
                };
                rightParentView.getViewTreeObserver().addOnGlobalLayoutListener(
                        victim);

                break;
        }

        // }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("keyword", keyword);
        outState.putString("srpId", srpId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            keyword = savedInstanceState.getString("keyword");
            srpId = savedInstanceState.getString("srpId");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public String getToken() {
        return SYUserManager.getInstance().getToken();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        keyword = intent.getStringExtra("keyword");
        keyword = StringUtils.isNotEmpty(keyword) ? keyword
                .replaceAll(" ", " ") : "";
        srpId = intent.getStringExtra("srpId");
        imgUrl = intent.getStringExtra("imgUrl");
        from = intent.getStringExtra("from");
        isFromPush = intent.getBooleanExtra("isfrompush", false);
        // mIsFinish = intent.getBooleanExtra("isfinish", false);

        opSource = intent.getStringExtra("opSource");   //获得来源字段 ZSSDK

        if (isFromAppWidget()) {
            if (StringUtils.isNotEmpty(SYUserManager.getInstance().getToken())) {
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                Intent i = new Intent();
                i.setClass(this, FirstInActivity.class);
                startActivity(i);
            }
            finish();
            return;
        }

        if (ShortCutInfo.FROM_SHORTCUT.equals(from)) {
            if (StringUtils.isNotEmpty(SYUserManager.getInstance().getToken())) {
                if (!MainApplication.getInstance().isRunning()) {
                    Intent mainIntent = new Intent(this,
                            CommonStringsApi.getHomeClass());
                    mainIntent.putExtra("from", ShortCutInfo.FROM_SHORTCUT);
                    ShortCutInfo info = new ShortCutInfo();
                    info.setSrpId(srpId);
                    info.setKeyword(keyword);
                    info.setGoTo(ShortCutInfo.GO_TO_SRP);
                    mainIntent.putExtra(MainActivity.SHORTCUT_EXTRA, info);
                    startActivity(mainIntent);
                    finish();
                    return;
                }
            } else { // 极其特殊的情况，即当用户清空所有搜悦数据，包括用户信息后，点击快捷方式直接走引导页
                Intent i = new Intent();
                i.setClass(SRPActivity.this, FirstInActivity.class);
                startActivity(i);
            }

        }
        srpId = srpId == null ? "" : srpId;
        replaceBg = StringUtils.isSuperSrp(keyword, srpId);
        currentTitle = intent.getStringExtra("currentTitle");
        md5 = intent.getStringExtra("md5");
        isSearch = intent.getBooleanExtra("isSearch", true);
    }

    /**
     * @return 如果是RSS返回""
     */
    public String getKeyword() {
        return TextUtils.isEmpty(mUrl) ? keyword : "";// Rss的mUrl不是空
    }

    public void hideTitleBar() {
        LayoutParams params = pagerSlidingTabStrip.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.space_1);
        pagerSlidingTabStrip.setLayoutParams(params);
    }

    public void showTitleBar() {
        LayoutParams params = pagerSlidingTabStrip.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.space_35);
        pagerSlidingTabStrip.setLayoutParams(params);
    }

    private void initAllViews() {
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.circle_index_indicator);
        //初始化pagerSlidingTabStrip的样式
        initPagerSlidingTabStrip();
        //viewpager
        mViewPager = (ViewPagerWithTips) findViewById(R.id.vp_srp_pages);
        longtengView = findViewById(R.id.rl_longteng_ad);
        // hsv_widgets = (OuterTabPageIndicator)
        // findViewById(R.id.hsv_srp_widgets);
        // hsv_widgets.setOnTabReselectedListener(this);
        /*
                     * view_translucent = findViewById(R.id.view_translucent);
		 * view_translucent.getBackground().setAlpha(100);
		 */
        /* ll_srp_3g = findViewById(R.id.ll_srp_3g);// 3代结果的横条 */
        view_translucent = findViewById(R.id.view_translucent);
        view_translucent.getBackground().setAlpha(100);
        fragmentPagerAdapter = new SRPFragmentPagerAdapter(
                getSupportFragmentManager());
        mViewPager.setAdapter(fragmentPagerAdapter);// 设置ViewPager的适配器
        pagerSlidingTabStrip
                .setOnPageChangeListener(new MyPagerOnPageChangeListener());

    }

    /**
     * 初始化pagerSlidingTabStrip
     */
    private void initPagerSlidingTabStrip() {
        pagerSlidingTabStrip
                .setTextColorResource(R.color.pstrip_text__normal_color);
        //设置高亮颜色按钮
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.pstrip_text_selected_color_red);
        //设置选中文本颜色
        pagerSlidingTabStrip.setTextSelectedColor(
                getResources().getColor(R.color.pstrip_text_selected_color_red));
        //设置字体大小
        pagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.space_14));
        //设置下划线高度
        pagerSlidingTabStrip.setUnderlineHeight(getResources().getDimensionPixelOffset(R.dimen.space_0_8));
        //设置divider的高度
        pagerSlidingTabStrip.setIndicatorHeight(getResources().getDimensionPixelOffset(R.dimen.space_1));
        //设置线条的颜色
        pagerSlidingTabStrip.setUnderlineColorResource(R.color.bar_line_color);
        pagerSlidingTabStrip.setOnTextViewclick(this);
    }

/*    public void searchResultSuccess(SearchResult searchResult, AjaxStatus as) {
        isExpired = false;
        imgUrl = searchResult.getSrpImage();
        if (TradeUrlConfig.MEGAGAME_SEARCH_KEYWORD.equals(keyword)) {
            a.ajax(imgUrl, File.class, new AjaxCallback<File>() {
                @Override
                public void callback(String url, File file, AjaxStatus status) {
                    if (file != null) {
//                        imageBitmap = BitmapFactory.decodeFile(file
//                                .getAbsolutePath());
                        BitmapFactory.Options  options= new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inJustDecodeBounds = false;
                        imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                    }
                }

                ;
            });
        }
        if (StringUtils.isNotEmpty(imgUrl)) {
            doCacheImg();
        }
        if (searchResult.isXiaoqi()) {
            if (replaceBg != 0)
                subBtn.setVisibility(View.INVISIBLE);
            titleShare.setVisibility(View.GONE);
            generation = 3;
            initXiaoQiView(searchResult);
            showView(4);
            return;
        }
        // registerBoradcastReceiver();// 消岐页不注册广播
        registerSubscribeStateBroadcastReceiver();// 消岐页不注册广播
        registerChangeSubscribeState();
        version = searchResult.version();
        if (searchResult.version() == 3
                && "微博搜索".equals(searchResult.category()))
            searchResult.items_$eq(createSearchResultItems(searchResult));
        if (searchResult.version() == 3) {

            if (navs.size() == 0) {
                */

    /**
     * 显示龙腾按钮
     *//*
                generation = 3;
                init3GFirstResult(searchResult);
                // 请求底部条广告
                // 全部SRP词都要取广告 update by wcy
                // if (getCurrentItemByTitle("推荐企业") > 0
                // || "推荐企业".equals(navs.get(0).title()))

//                http.getAdList(keyword, srpId, ADInfo.SPR_PAGE_LOCATION, "");
                AdListRequest adListRequest = new AdListRequest(HttpCommon.SRP_AD_LIST_REQUEST,this);
                adListRequest.addParams(keyword, srpId, ADInfo.SPR_PAGE_LOCATION, "");
                mainHttp.doRequest(adListRequest);

                requestCheckSub();
                showView(3);
            }
        } else if ((searchResult.version() == 2 || searchResult.version() == 1)
                && searchResult.items().size() > 0) {
            generation = 2;
            if (!"rss".equals(searchResult.category().toLowerCase()))
                requestCheckSub();
            init2GFirstResult(searchResult);
            showView(2);
        } else if (searchResult.items().size() == 0) {
            generation = 2;
            showView(5);// 没有结果的布局
        }
        if (as.hasExpired == true) {
            isExpired = true;
            sendSearchResultRequest(http, mUrl, 0);
        }
    }*/
    public void searchResultSuccess(SearchResult searchResult) {
        isExpired = false;
        imgUrl = searchResult.getSrpImage();
        if (TradeUrlConfig.MEGAGAME_SEARCH_KEYWORD.equals(keyword)) {
//            a.ajax(imgUrl, File.class, new AjaxCallback<File>() {
//                @Override
//                public void callback(String url, File file, AjaxStatus status) {
//                    if (file != null) {
////                        imageBitmap = BitmapFactory.decodeFile(file
////                                .getAbsolutePath());
//                        BitmapFactory.Options  options= new BitmapFactory.Options();
//                        options.inJustDecodeBounds = true;
//                        options.inPreferredConfig = Bitmap.Config.RGB_565;
//                        options.inJustDecodeBounds = false;
//                        imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
//                    }
//                }
//            });
            CMainHttp.getInstance().doDownload(new Random().nextInt(),
                    getCacheDir().getAbsolutePath(),
                    imgUrl,
                    null,
                    new IVolleyResponse() {
                        @Override
                        public void onHttpResponse(IRequest request) {
                            File file = new File(request.getResponse().toString());
                            if (file != null) {
                                imageBitmap = BitmapFactory.decodeFile(file
                                        .getAbsolutePath());
                            }
                        }

                        @Override
                        public void onHttpError(IRequest request) {

                        }

                        @Override
                        public void onHttpStart(IRequest request) {

                        }
                    });
        }
        if (StringUtils.isNotEmpty(imgUrl)) {
            doCacheImg();
        }
        if (searchResult.isXiaoqi()) {
            if (replaceBg != 0)
                subBtn.setVisibility(View.INVISIBLE);
            titleShare.setVisibility(View.GONE);
            generation = 3;
            initXiaoQiView(searchResult);
            showView(4);
            return;
        }
        // registerBoradcastReceiver();// 消岐页不注册广播
        registerSubscribeStateBroadcastReceiver();// 消岐页不注册广播
        registerChangeSubscribeState();
        version = searchResult.version();
        if (searchResult.version() == 3
                && "微博搜索".equals(searchResult.category()))
            searchResult.items_$eq(createSearchResultItems(searchResult));
        if (searchResult.version() == 3) {

            if (navs.size() == 0) {
                /**
                 * 显示龙腾按钮
                 */
                generation = 3;
                init3GFirstResult(searchResult);
                // 请求底部条广告
                // 全部SRP词都要取广告 update by wcy
                // if (getCurrentItemByTitle("推荐企业") > 0
                // || "推荐企业".equals(navs.get(0).title()))

//                http.getAdList(keyword, srpId, ADInfo.SPR_PAGE_LOCATION, "");
                AdListRequest adListRequest = new AdListRequest(HttpCommon.SRP_AD_LIST_REQUEST, this);
                adListRequest.addParams(keyword, srpId, ADInfo.SPR_PAGE_LOCATION, "");
                mainHttp.doRequest(adListRequest);

                requestCheckSub();
                showView(3);
            }
        } else if ((searchResult.version() == 2 || searchResult.version() == 1)
                && searchResult.items().size() > 0) {
            generation = 2;
            if (!"rss".equals(searchResult.category().toLowerCase()))
                requestCheckSub();
            init2GFirstResult(searchResult);
            showView(2);
        } else if (searchResult.items().size() == 0) {
            generation = 2;
            showView(5);// 没有结果的布局
        }
//        if (as.hasExpired == true) {
//            isExpired = true;
//            sendSearchResultRequest(http, mUrl, 0);
//        }
    }

    private ArrayList<SearchResultItem> createSearchResultItems(SearchResult searchResult) {
        ArrayList<SearchResultItem> searchResultItems = new ArrayList<SearchResultItem>();
        try {
            BoZhu boZhu = searchResult.boZhu();
            if (boZhu != null && boZhu.user() != null
                    && !TextUtils.isEmpty(boZhu.user().name())) {
                boZhu.newWeibo_$eq(searchResult.newWeiBo());
                SearchResultItem boZhuItem = new SearchResultItem();
                boZhuItem.boZhu_$eq(boZhu);
                searchResultItems.add(boZhuItem);
            }
            List<HotTopic> hotTopics = searchResult.hotTopics();
            if (hotTopics != null && hotTopics.size() > 0) {
                SearchResultItem hotTopicsItem = new SearchResultItem();
                hotTopicsItem.hotTopics_$eq(hotTopics);
                searchResultItems.add(hotTopicsItem);
            }
            if (searchResult.weibo() != null)
                for (Weibo weibo : searchResult.weibo()) {
                    SearchResultItem item = new SearchResultItem();
                    item.weibo_$eq(weibo);
                    item.boZhu_$eq(null);
                    item.hotTopics_$eq(null);
                    searchResultItems.add(item);
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return searchResultItems;
    }

    public void showShareBtn() {
        if ((showShareBtn) && titleShare != null) {
            titleShare.setVisibility(View.VISIBLE);
        } else {
            titleShare.setVisibility(View.GONE);
        }
    }


    public BroadcastReceiver mSubscribeStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestCheckSub();
        }
    };

    private BroadcastReceiver switchWidgetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SWITCH_WIDGET.equals(action)) {
                String md5 = intent.getStringExtra("md5");
                backHome = intent.getBooleanExtra("backHome", false);
                if (!StringUtils.isEmpty(md5)) {
                    jsClickToWidget(md5);
                }
            }
        }
    };

    private boolean showShareBtn;
    protected boolean keybordShowing;

    // public void registerBoradcastReceiver()
    // IntentFilter myIntentFilter = new IntentFilter();
    // myIntentFilter.addAction("changeText");
    // registerReceiver(mBroadcastReceiver, myIntentFilter);
    // }


    public void registerSubscribeStateBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("subscribeState");
        registerReceiver(mSubscribeStateBroadcastReceiver, myIntentFilter);
    }


    public void registerChangeSubscribeState() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("ChangeSubscribeState");
        registerReceiver(mSubscribeStateBroadcastReceiver, myIntentFilter);
    }

    public void changeText() {
        if (subScribeState > 0) {
            requestCheckSub();
        } else if (subScribeState == 0) {
            // subBtn.setText(getResources().getString(
            // R.string.subscibe_titlebar_string));
            subBtn.setTag(new Long(0));
            llSubscribeStateLayout.setTag(new Long(0));
        }
    }

    public void subscribeDeleteSuccess() {
//        if (subBtn2 != null) subBtn2.setClickable(true);
        SouYueToast.makeText(this, R.string.subscibe_cancel_success, 0).show();
        if (subBtn != null) {
            if (subBtn2 != null) subBtn2.setImageResource(R.drawable.srp_subscribe_selector);
            // subBtn.setText("");
            // subBtn.setBackgroundColor(Color.parseColor("#57b6f2"));
            // subBtn.setBackgroundResource(R.drawable.srp_subscribe_selector);
            subBtn.setTag(new Long(0));
            llSubscribeStateLayout.setTag(new Long(0));
            requestCheckSub();
            SYSharedPreferences.getInstance().putBoolean(
                    SYSharedPreferences.KEY_UPDATE, true);
            if (searchResult.getKeywordCate() == DetailActivity.KEYWORD_CATE_SPECIAL) {//专题类型
                //数据库操作
                SuberedItemInfo info = new SuberedItemInfo();
                info.setTitle(keyword);
                info.setCategory("special");
                info.setSrpId(srpId);
                info.setKeyword(keyword);
                suberDao.clearOne(info);
            } else {
                //数据库操作
                SuberedItemInfo info = new SuberedItemInfo();
                info.setTitle(keyword);
                info.setCategory("srp");
                info.setSrpId(srpId);
                info.setKeyword(keyword);
                suberDao.clearOne(info);
            }
        }
        subIsRunning = false;
        setBackIntent(2, srpId);
    }

    private void init2GFirstResult(SearchResult searchResult) {
        this.searchResult = searchResult;
        NavigationBar nav = new NavigationBar();
        if (!TextUtils.isEmpty(mUrl))// RSS 用的是url 2代结果用的是keyword
            nav.url_$eq(mUrl);
        else
            nav.url_$eq(UrlConfig.searchResult + "?keyword="
                    + keyword + "&srpId=" + srpId);
        SRPFragment srpFragment = new CommonFragment(this, nav);
        srpFragments.add(srpFragment);
        srpFragment.searchResult = searchResult;
        fragmentPagerAdapter.setFragments(srpFragments);
        fragmentPagerAdapter.notifyDataSetChanged();
    }

    // 第一次请求返回结果
    private void init3GFirstResult(SearchResult searchResult) {
        this.searchResult = searchResult;
        srpId = searchResult.srpId();
        navs = searchResult.nav();
        showMenu = searchResult.isShowMenu();
        showShareBtn = false;
        for (int i = 0; i < navs.size(); i++) {
            NavigationBar nav = navs.get(i);
            String category = nav.category();
            if ("新闻搜索".equals(category) || "百科".equals(category)) {
                showShareBtn = true;
                if (nav.image() != null && nav.image().size() > 0) {
                    shareImageUrl = nav.image().get(0);
                    break;
                }
            }
        }

        // 排除，超级分享，超模大赛，造梦着，三个栏目
        /*
         * if(replaceBg != 1 && replaceBg != 2 &&
		 * !"1a86a13b9f3934d64ff43f3ac5649882".equals(srpId)){ NavigationBar
		 * indexNav = new NavigationBar(); indexNav.title_$eq("首页");
		 * indexNav.category_$eq("首页"); navs.add(0, indexNav); }
		 */

		/*
         * // 排除，超级分享，超模大赛，造梦着，三个栏目 if (replaceBg != 1 && replaceBg != 2 &&
		 * !"1a86a13b9f3934d64ff43f3ac5649882".equals(srpId)) { NavigationBar
		 * indexNav = new NavigationBar(); indexNav.title_$eq("首页");
		 * indexNav.category_$eq("首页"); navs.add(0, indexNav); }
		 */
        // navigationAdapter = new NavigationAdapter(this);
        // navigationAdapter.addNavs(navs);
        // navigationAdapter.addNavs(naviData);
        int currentPage = getCurrentItemByTitle(StringUtils
                .isEmpty(currentTitle) ? md5 : currentTitle);

        if (showMenu || currentPage > 0) {
            showTitleBar();
        }

        fragmentPagerAdapter.setTitles(navs);

        for (int i = 0; i < navs.size(); i++) {// 初始化界面
            // =======分享桌面快捷方式:取固定产品微件：logo图,超A专用======================
//            if (!ConfigApi.isSouyue() && navs.get(i).category().equals("web")
//                    && navs.get(i).title().equalsIgnoreCase("logo图")) {
//                ShortCutUtils.getInstance(this).loadLogoImgUrl(
//                        navs.get(i).md5());
//            }
            // =============================
            SRPFragment srpFragment = getFragment(navs.get(i));
            srpFragments.add(srpFragment);
        }
        if (srpFragments.size() > 0) {
            // srpFragments.get(0).searchResult = searchResult;//
            // 第一次的请求结果给第一个fragment
            fragmentPagerAdapter.setFragments(srpFragments);
            fragmentPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(currentPage);
        }
        pagerSlidingTabStrip.setViewPager(mViewPager);
        pagerSlidingTabStrip.updateSelectedTextColor(currentPage);

        if (!StringUtils.isEmpty(currentTitle)) {// 如果title不为空，则设置到指定Fragment子项
            mViewPager.setCurrentItem(getCurrentItemByTitle(currentTitle));
        }
        if (navs.get(currentPage).getTypeId() != -4) {
            UpEventAgent.onWidgetView(this, navs.get(currentPage).getTypeId()
                            + "." + navs.get(currentPage).category(),
                    navs.get(currentPage).md5() + "."
                            + navs.get(currentPage).title(),
                    searchResult.srpId() + "." + searchResult.keyword());// 移动统计系统查看微件统计
        } else {
            UpEventAgent.onWidgetView(this, navs.get(currentPage).getTypeSt()
                            + "." + navs.get(currentPage).category(),
                    navs.get(currentPage).md5() + "."
                            + navs.get(currentPage).title(),
                    searchResult.srpId() + "." + searchResult.keyword());// 移动统计系统查看微件统计
        }
        showPublicIcon(currentPage);
    }

    private int getCurrentItemByTitle(String value) {
        if (TextUtils.isEmpty(value))
            return 0;
        for (NavigationBar n : navs) {
            if (value.equals(n.title()) || value.equals(n.md5()))
                return navs.indexOf(n);
        }
        return 0;
    }

    private void initXiaoQiView(SearchResult searchResult) {
        views = new ArrayList<View>();
        View xiaoqipage = findViewById(R.id.xiaoqi_page);
        ViewPager viewPager = (ViewPager) xiaoqipage
                .findViewById(R.id.viewpager);
        List<SearchResultItem> items = searchResult.items();
        for (SearchResultItem searchResultItem : items) {
            View view = createXiaoqiItem(searchResultItem);
            views.add(view);
        }
        viewPager.setAdapter(new MyPagerAdapter());
        PageIndicator mIndicator = (CirclePageIndicator) xiaoqipage
                .findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
    }

    public void getAdListSuccess(AdList list) {
        if (!MainApplication.srpRecAd) {
            longtengView.setVisibility(View.GONE);
            return;
        }

        viewFlipper(list.list());
        /*
         * 4.2.2以前版本SPR页广告不支持下载 List<AdListItem> newList = new
		 * ArrayList<AdListItem>(); if (list != null) { List<AdListItem> lists =
		 * list.list(); for (int i = 0; i < lists.size(); i++) { AdListItem item
		 * = lists.get(i); if (item.type() != null &&
		 * AdListItem.WEB_AD.equals(item.type())) { newList.add(item); } }
		 * viewFlipper(newList); }
		 */
    }

    /**
     * 消岐页Viewpager的适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        public void destroyItem(View v, int position, Object obj) {
            ((ViewPager) v).removeView(views.get(position));
        }

        public void finishUpdate(View arg0) {
        }

        public int getCount() {
            return views.size();
        }

        // 把布局放到viewpager上
        @Override
        public Object instantiateItem(View v, int position) {
            ((ViewPager) v).addView(views.get(position));
            return views.get(position);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View arg0) {
        }
    }

    private View createXiaoqiItem(SearchResultItem searchResultItem) {
        View view = View.inflate(this, R.layout.xiaoqi_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);
        if (searchResultItem.image().size() > 0)
            //aq.id(image).image(searchResultItem.image().get(0), true, true);
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP, searchResultItem.image().get(0), image);
        PhotoUtils.showCard(PhotoUtils.UriType.HTTP, searchResultItem.image().get(0), image);
        title.setText(searchResultItem.title());
        description.setText(searchResultItem.description());
        view.setTag(searchResultItem);
        description.setTag(searchResultItem);
        OnClickListener onClickListener = new OnClickListener() {
            public void onClick(View v) {
                SearchResultItem item = (SearchResultItem) v.getTag();
                if (item.getInterest_id() > 0) {
                    UIHelper.showCircleIndex(SRPActivity.this, item.srpId(),
                            item.keyword(), item.interestName(),
                            item.getInterestLogo());
                } else {
                    String kw = item.keyword();
                    String sId = item.srpId();
                    Intent intent = new Intent();
                    intent.setClass(SRPActivity.this, SRPActivity.class);
                    intent.putExtra("keyword", kw);
                    intent.putExtra("srpId", sId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            }
        };
        view.setOnClickListener(onClickListener);
        description.setOnClickListener(onClickListener);
        return view;
    }

    private class MyPagerOnPageChangeListener implements OnPageChangeListener {

        public void onPageSelected(int position) {
            if (!tabClickFlag) {
                UpEventAgent.onZSNavigationbarSlide(SRPActivity.this);  //ZSSDK 统计事件
            }
            tabClickFlag = false;
            if (searchResult != null) {
                if (navs.get(position).getTypeId() != -4) {
                    UpEventAgent
                            .onWidgetView(
                                    SRPActivity.this,
                                    navs.get(position).getTypeId() + "."
                                            + navs.get(position).category(),
                                    navs.get(position).md5() + "."
                                            + navs.get(position).title(),
                                    searchResult.srpId() + "."
                                            + searchResult.keyword());// 移动统计系统查看微件统计
                } else {
                    UpEventAgent
                            .onWidgetView(
                                    SRPActivity.this,
                                    navs.get(position).getTypeSt() + "."
                                            + navs.get(position).category(),
                                    navs.get(position).md5() + "."
                                            + navs.get(position).title(),
                                    searchResult.srpId() + "."
                                            + searchResult.keyword());// 移动统计系统查看微件统计
                }
            }
            SRPFragment currentFragment = getCurrentFragment();
            if (ConfigApi.isSouyue()) {
                if (hasIndex) {
                    if (position > 0) {
                        showTitleBar();
                    } else {
                        if (showMenu) {
                            showTitleBar();
                        } else {
                            hideTitleBar();
                        }
                    }
                }
            } else {
                // 添加底部菜单 显示与隐藏逻辑 zhaobo 2014-10-31
                currentFragmentWebUrl = navs.get(position).url();
                if ((currentFragment instanceof KunlunJueFragment)
                        && ((KunlunJueFragment) currentFragment).isBottomMenuShow) {
                    ((KunlunJueFragment) currentFragment).loadNewsCount();
                    setBottomMenu(View.VISIBLE);
                } else {
                    setBottomMenu(View.GONE);
                }
            }

            if (!currentFragment.hasDatas
                    && !(currentFragment instanceof MySharesFragment)
                    && !(currentFragment instanceof ChatRoomFragment)) {
                // currentFragment.pbHelper.showLoading();// 滑到当前页如果数据为空显示正在加载
                currentFragment.loadData();
            }
            if (keybordShowing) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(SRPActivity.this
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            }
            showPublicIcon(position);
            isEditSrpWiget(position);// 微件是否可编辑
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    private void showPublicIcon(int position) {
        if (srpFragments != null && srpFragments.size() > 0
                && position < srpFragments.size() && position > 0) {
            if (srpFragments.get(position) instanceof BlogFragment) { // 信息发布微件，设定发布权限
                ((BlogFragment) srpFragments.get(position))
                        .updatePenView(isAdmin);
            }
        }

    }

    private void setLayouListener() {
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        activityRootView.getWindowVisibleDisplayFrame(r);
                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - (r.bottom - r.top);
                        if (heightDiff > 100) {
                            keybordShowing = true;
                        } else {
                            keybordShowing = false;
                        }
                    }
                });
        if (subBtn2 != null) subBtn2.setOnClickListener(this);
    }

    private void viewFlipper(List<AdListItem> list) {
        Animation in, out;
        out = AnimationUtils.loadAnimation(this, R.anim.viewflipper_out);
        in = AnimationUtils.loadAnimation(this, R.anim.viewflipper_in);
        vf = (ViewFlipper) longtengView.findViewById(R.id.longteng_flipper);
        addViewFlipperViews(vf, list);
        if (vf.getTag() == null)
            return;
        final ImageButton cancelLongTengLED = (ImageButton) longtengView
                .findViewById(R.id.cancel_longteng_led);
        cancelLongTengLED.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                longtengView.setVisibility(View.GONE);
                vf.stopFlipping();
                MainApplication.srpRecAd = false;
            }
        });
        vf.setOutAnimation(out);
        vf.setAnimateFirstView(false);
        vf.setInAnimation(in);
        vf.setFlipInterval(5000);// 每隔x秒自动显示ViewFlipper里面的图片
        if (list.size() > 1)
            vf.startFlipping();// 开始显示
        out.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                cancelLongTengLED.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                cancelLongTengLED.setVisibility(View.VISIBLE);
            }
        });

        longtengView.setVisibility(View.VISIBLE);
    }

    public void addViewFlipperViews(ViewFlipper vf, List<AdListItem> items) {
        ImageLoader imgloader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.default_small)
                .showImageOnFail(R.drawable.default_small)
                .showImageForEmptyUri(R.drawable.default_small).build();
        View view = null;
        for (int i = 0; i < items.size(); i++) {
            final AdListItem adListItem = items.get(i);
            // if (adListItem.type().equals(AdListItem.WEB_AD)) {
            view = View.inflate(SRPActivity.this,
                    R.layout.list_item_ad_pic_bottom, null);
            ImageView image = (ImageView) view.findViewById(R.id.iv_image);
            imgloader.displayImage(adListItem.image(), image, options);
            view.setTag(adListItem);
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
					/*
					 * String url = adListItem.event();
					 *
					 * Intent intent = new Intent();
					 * intent.setClass(SRPActivity.this,
					 * WebSrcViewActivity.class);
					 * intent.putExtra(WebSrcViewActivity.PAGE_URL,
					 * adListItem.url()); startActivity(intent);
					 * overridePendingTransition(R.anim.left_in,
					 * R.anim.left_out);
					 *
					 * if (url != null) { //广告点击调用接口 广告位置==>1：首页 2：SRP首页
					 * 3：SRP详情页面 http.adClick(keyword, srpId,
					 * ADInfo.SPR_PAGE_LOCATION, "", url); }
					 */

                    ADInfo adinfo = new ADInfo();

                    adinfo.srpId_$eq(srpId);
                    adinfo.keyword_$eq(keyword);
                    adinfo.url_$eq(adListItem.url());
                    adinfo.event_$eq(adListItem.event());
                    adinfo.type_$eq(adListItem.type());

                    clickAd(adinfo);
                }
            });
            vf.addView(view, i, new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));
            vf.setTag(true);
        }
        // }
    }

	/*
	 * public void clickAd(String json) { try { final ADInfo adinfo = new
	 * Gson().fromJson(json, ADInfo.class); if (adinfo == null ||
	 * StringUtils.isEmpty(adinfo.url())) return;
	 *
	 * doDownload(adinfo);
	 *
	 * } catch (JsonParseException jpe) { } }
	 */

    private void clickAd(final ADInfo adinfo) {
        final Intent intent = new Intent();
        if (AdListItem.DOWNLOAD_AD.equals(adinfo.type())) {
            if (!CMainHttp.getInstance().isWifi(MainApplication.getInstance())) {  //不是wifi环境
                new DownloadAlert(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.setClass(MainApplication.getInstance(),
                                DownloadService.class);
                        intent.putExtra("url", adinfo.url());
                        intent.putExtra("event", adinfo.event());
                        intent.putExtra("keyword", adinfo.keyword());
                        intent.putExtra("srpId", adinfo.srpId());
                        intent.putExtra("page", ADInfo.SPR_PAGE_LOCATION);
                        MainApplication.getInstance().startService(intent);
                    }
                }).show();
            } else {
                intent.setClass(MainApplication.getInstance(),
                        DownloadService.class);
                intent.putExtra("url", adinfo.url());
                intent.putExtra("event", adinfo.event());
                intent.putExtra("keyword", adinfo.keyword());
                intent.putExtra("srpId", adinfo.srpId());
                intent.putExtra("page", ADInfo.SPR_PAGE_LOCATION);
                MainApplication.getInstance().startService(intent);
            }

        } else {
            if (adinfo == null || StringUtils.isEmpty(adinfo.url()))
                return;

            String event = adinfo.event();
            if (event != null) {
                // 广告点击调用接口 广告位置==>1：首页 2：SRP首页 3：SRP详情页面
//                http.adClick(adinfo.keyword(), adinfo.srpId(),
//                        ADInfo.SPR_PAGE_LOCATION, "", event);
                AdClickRequest adClickRequest = new AdClickRequest(HttpCommon.SRP_AD_CLICK_REQUEST, this);
                adClickRequest.addParams(adinfo.keyword(), adinfo.srpId(), ADInfo.SPR_PAGE_LOCATION, "", event);
                mainHttp.doRequest(adClickRequest);
            }

            intent.setClass(this, WebSrcViewActivity.class);
            intent.putExtra(WebSrcViewActivity.PAGE_URL, adinfo.url());
            startActivity(intent);
        }
    }

    public SRPFragment getCurrentFragment() {
        SRPFragment item = null;
        try {
            item = (SRPFragment) fragmentPagerAdapter.getItem(mViewPager
                    .getCurrentItem());
        } catch (Exception ex) {
        }
        return item;
    }

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    private SRPFragment getFragment(NavigationBar nav) {
        if (nav == null)
            return new CommonFragment(this, nav);
        String category = nav.category();
        if (ConstantsUtils.FR_BAIKE.equals(category)) {// 小档案
            return new XiaoDanganFragment(this, nav);
        }
        if (ConstantsUtils.FR_QA.equals(category)) {
            return new QAFragment(this, nav);
        }
        if (ConstantsUtils.FR_SRP_SELFCREATE.equals(category)) { // 原创微件
            SRPSelfCreateFragment srpSelfCreateFragment = new SRPSelfCreateFragment(
                    this, nav);
            srpSelfCreateFragment.setKeyWord(keyword);
            srpSelfCreateFragment.setSrpid(srpId);
            return srpSelfCreateFragment;
        }
        if (ConstantsUtils.FR_WEIBO_SEARCH.equals(category)) {
            return new WeiboFragment(this, nav);
        }
        if (ConstantsUtils.FR_BLOG_SEARCH.equals(category)) {
            BlogFragment blogFragment = new BlogFragment(this, nav);
            blogFragment.setKeyWord(keyword);
            blogFragment.setSrpid(srpId);
            return blogFragment;
        }
        if (ConstantsUtils.FR_INFO_PUB.equals(category)) { // sy4.1新引入的信息发布微件
            BlogFragment blogFragment = new BlogFragment(this, nav);
            blogFragment.setKeyWord(keyword);
            blogFragment.setSrpid(srpId);
            if (BlogFragment.ONLY_ADMIN.equals(nav.getRight())) { // 仅SRP管理员在当前微件下有发布权限
                // http.isSRPAdmin(getToken(), srpId, keyword);

                CircleIsAdminReq req = new CircleIsAdminReq(HttpCommon.CIRCLE_SRPISADMIN_REQEUST, this);
                req.addParams(srpId, keyword);
                mainHttp.doRequest(req);
            }
            return blogFragment;
        }
        if (ConstantsUtils.FR_BBS_SEARCH.equals(category)) {
            return new ForumFragment(this, nav);
        }
        if (ConstantsUtils.FR_JHQ.equals(category)) {
            return new RecommendFragment(this, nav);
        }
        if (ConstantsUtils.FR_IMG_SEARCH.equals(category)) {
            return new PhotoSearchFragment(this, nav);
        }
        if (ConstantsUtils.FR_SELF_CREATE.equals(category)) { // 原创大赛
            return new MySharesFragment(this, nav);
        }
        if (ConstantsUtils.FR_WEB.equals(category)) {
            KunlunJueFragment kunlunJueFragment = new KunlunJueFragment(this,
                    nav);
            kunlunJueFragment.setKeyWord(keyword);
            return kunlunJueFragment;
        }
        if (ConstantsUtils.FR_WEB_SUB.equals(category)) {
            return new WebpageFragment(this, nav);
        }

        if (ConstantsUtils.FR_CHAT_ROOM.equals(category)) {
            SearchResultItem item = new SearchResultItem();
            item.keyword_$eq(this.keyword);
            item.srpId_$eq(srpId);
            item.url_$eq(nav.url());
            return new ChatRoomFragment(item);
        }

        if (ConstantsUtils.FR_NEW_INDEX.equals(category)) {
            IndexFragment indexFragment = new IndexFragment();
            Bundle bundle = new Bundle();
            bundle.putString("srpId", srpId);
            bundle.putString("keyword", keyword);
            bundle.putBoolean("isSearch", isSearch);
            indexFragment.setArguments(bundle);
            hasIndex = true;
            return indexFragment;
        }

        if (ConstantsUtils.FR_INTEREST_GROUP.equals(category)) { // 精华区
            EssencePostFragment epf = new EssencePostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", keyword);
            bundle.putString("srp_id", srpId);
            bundle.putString("tag_id", nav.getTag_id());
            bundle.putLong("interest_id", nav.getInterest_id());
            epf.setArguments(bundle);
            return epf;
        }
        if (ConstantsUtils.FR_INTEREST_BAR.equals(category)) { // 圈吧
            CircleBarFragment cb = new CircleBarFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("interest_id", nav.getInterest_id());
            bundle.putString("title", keyword);
            bundle.putString("srp_id", srpId);
            bundle.putString("tag_id", nav.getTag_id());
            bundle.putString("onlyjing", nav.getOnlyjing());
            cb.setArguments(bundle);
            return cb;
        }

        return new CommonFragment(this, nav);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_srp_share:
                SrpShareBtnOnclick();
                break;
            case R.id.text_btn:     //页内菜单按钮
                // Toast.makeText(this, v.getTag() + "", Toast.LENGTH_LONG).show();

                UpEventAgent.onZSMenuClick(this);    //ZSSDK 统计事件
                long bid = (Long) v.getTag();
                if (bid == -2) {
                    Intent intent = new Intent(this, WebSrcViewActivity.class);
                    intent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.sup);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (bid == -3) {
                    Intent intent = new Intent(this, WebSrcViewActivity.class);
                    intent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.cm);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (bid == -4) {
                    Intent intent = new Intent(this, WebSrcViewActivity.class);
                    intent.putExtra(WebSrcViewActivity.PAGE_URL,
                            TradeUrlConfig.MEGAGAME_SEARCH_URL); // 大赛搜索，设置特定的url
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    createMorePupWindow();

                    if (mTabMsgPopupWindow != null && popupView != null) {
                        mTabMsgPopupWindow.showAtLocation(
                                popupView,
                                Gravity.RIGHT | Gravity.TOP,
                                20,
                                DeviceUtil.dip2px(this, 47)
                                        + Utils.getTitleBarHeight(this) - getResources().getDimensionPixelOffset(R.dimen.space_5));
                    }
                    return;
                }
                break;
            case R.id.from_srp_search:// 搜索
                // IntentUtil.openSearchActivity(this);
                // overridePendingTransition(R.anim.left_in, R.anim.left_out);
                // showDialog(DialogPlus.Gravity.TOP);
//                JSClick jscClick = new JSClick();
//                String sKeyWord = StringUtils.enCodeRUL(keyword);
//                jscClick.setUrl(UrlConfig.S_CURRENT_PAGE + "?k=" + sKeyWord);
//                DialogHelper.getInstance().showDialog(SRPActivity.this,
//                        DialogPlus.ScreenType.HALF, jscClick);

                IntentUtil.gotoSearchAll(this, getClass().getSimpleName());
                break;

            case R.id.pop_zuti:     //页内菜单：编辑
                UpEventAgent.onZSMenuEdit(this);    //ZSSDK 统计事件
                int currentItem2 = getCurrentItem();
                if (navs == null || navs.size() == 0) {
                    return;
                }
                NavigationBar nav = navs.get(currentItem2);
                String mURL = "";
                if (null == SouyueAPIManager.getUserInfo()) {

                    mURL = UrlConfig.SRPURL + "m=" + nav.md5() + "&k=" + keyword
                            + "&s=" + srpId + "&username=null";
                } else {
                    mURL = UrlConfig.SRPURL + "m=" + nav.md5() + "&k=" + keyword
                            + "&s=" + srpId + "&username="
                            + SouyueAPIManager.getUserInfo().userName().toString();
                }
                Intent intent2 = new Intent(this, WebSrcViewActivity.class);
                intent2.putExtra(WebSrcViewActivity.PAGE_URL, mURL);
                startActivity(intent2);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);

                break;
            case R.id.pop_senderror:    //页内菜单：报错
                UpEventAgent.onZSMenuReporterror(this);    //ZSSDK 统计事件
                int currentItem = getCurrentItem();
                if (navs == null || navs.size() == 0) {
                    return;
                }
                NavigationBar nav2 = navs.get(currentItem);
                String mErrorURL = "";
                if (null == SouyueAPIManager.getUserInfo()) {

                    mErrorURL = UrlConfig.SRPFINDERROR + "m=" + nav2.md5() + "&k="
                            + keyword + "&s=" + srpId + "&username=null" + "&c="
                            + nav2.title();
                } else {
                    mErrorURL = UrlConfig.SRPFINDERROR + "m=" + nav2.md5() + "&k="
                            + keyword + "&s=" + srpId + "&username="
                            + SouyueAPIManager.getUserInfo().userName().toString()
                            + "&c=" + nav2.title();
                }
                Intent intent3 = new Intent(this, WebSrcViewActivity.class);
                intent3.putExtra(WebSrcViewActivity.PAGE_URL, mErrorURL);
                startActivity(intent3);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.pop_erweima:      //页内菜单 ：二维码
                UpEventAgent.onZSMenuQrcode(this);    //ZSSDK 统计事件
                Intent intent = new Intent(mContext, DimensionalCodeActivity.class);
                intent.putExtra(DimensionalCodeActivity.INTENT_K, getKeyword());
                intent.putExtra(DimensionalCodeActivity.INTENT_ID, srpId);
                intent.putExtra(DimensionalCodeActivity.INTENT_URL, imgUrl);
                startActivity(intent);
                break;
            case R.id.pop_fenxiang:     //页内菜单：分享
                UpEventAgent.onZSMenuShare(this);    //ZSSDK 统计事件
                SrpShareBtnOnclick();
                break;
            case R.id.pop_tuiding:      //页内菜单：退订
                // http.subscribeDelete(getToken(), (Long) subBtn.getTag());
                if (llSubscribeStateLayout != null) {
                    Long subTagValue = (Long) llSubscribeStateLayout.getTag();
                    if (subTagValue > 0) {
                        String st = "srp";
                        if (searchResult.getKeywordCate() == 4) {
                            st = "special";
                        }

                        SubDeleteReq request = new SubDeleteReq(HttpCommon.SUB_DELETE_REQUEST, this);
                        request.addParameters(subTagValue, st, searchResult.srpId(), ZSSdkUtil.SRP_SUBSCRIBE_MENU);
                        mainHttp.doRequest(request);

                        // http.subscribeDelete(getToken(), subTagValue, st);
                        UpEventAgent.onSrpUnsubscribe(this, keyword, srpId);
                    } else {
                        // if (isSearch) {
                        UpEventAgent.onZSMenuSubscribe(this);    //ZSSDK 统计事件  页内菜单：订阅 统计
                        dismissPupWindows();
                        LoginAlert loginDialog = new LoginAlert(SRPActivity.this,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        subKeyword(ZSSdkUtil.SRP_SUBSCRIBE_MENU); //菜单如果没有登录弹框
                                    }

                                });
                        loginDialog.show();
                        return;
                    }

                    // }

                }

                break;
            case R.id.pop_create_shortcut:      //页内菜单：创建桌面快捷方式
                UpEventAgent.onZSMenuShortcut(this);    //ZSSDK 统计事件
                Map<String, Object> extras = new HashMap<String, Object>();
                extras.put("srpId", srpId);
                extras.put("keyword", keyword);
                extras.put("from", "shortcut");
//                Bitmap bitmap = a.getCachedImage(imgUrl);
                Bitmap bitmap = null;
                File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(imgUrl);
                if (fileImage != null) {
                    bitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
                }

                if (bitmap == null) {
                    File cacheFile = ImageLoader.getInstance().getDiskCache().get(imgUrl);
                    if (cacheFile != null) {
                        bitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                    }
                }
                // 超A创建桌面快捷方式专用 update by FM
//                if (!ConfigApi.isSouyue()
//                        && !StringUtils.isEmpty(ShortCutUtils.getInstance(this)
//                        .getShortImgBitmap())) {
//                    bitmap = ShortCutUtils.getInstance(this).getShortImgBitmap();
//                }
                if (bitmap != null) {
                    bitmap = ImageUtil.getRoundCornerRect(bitmap, 18, true); // 魔数18，圆角半径，采用18比较合适
                    bitmap = ImageUtil.zoomImg(bitmap,
                            CircleUtils.dip2px(this, 45),
                            CircleUtils.dip2px(this, 45)); // 魔数45,经过测试，比较合适，且对各个大小的屏幕兼容性较好
                }
                ActivityUtils.addShortCut(this,
                        "com.zhongsou.souyue.activity.SRPActivity", bitmap,
                        keyword, extras);
                break;
            case R.id.pop_cooper_manage:    //页内菜单：合作经营
                UpEventAgent.onZSMenuCooperation(this);    //ZSSDK 统计事件
                Intent i = new Intent(mContext, SrpWebViewActivity.class);
                i.putExtra(WebSrcViewActivity.PAGE_URL,
                        UrlConfig.HOST_COOPER_MANAGE + "?srpid=" + srpId
                                + "&uname="
                                + SYUserManager.getInstance().getUserName()
                                + "&from=" + "souyue"
                                + "&imgurl=" + imgUrl
                );
                mContext.startActivity(i);
                break;

            case R.id.btn_detail_subscribe1:
//                if (subBtn2 != null) subBtn2.setClickable(false);
                if (subIsRunning) return;
                subIsRunning = true;
                if (isSubscrible) {  //退订
                    if (llSubscribeStateLayout != null) {
                        Long subTagValue = (Long) llSubscribeStateLayout.getTag();
                        if (subTagValue > 0) {
                            String st = "srp";
                            if (searchResult.getKeywordCate() == 4) {
                                st = "special";
                            }

                            SubDeleteReq request = new SubDeleteReq(HttpCommon.SUB_DELETE_REQUEST, this);
                            request.addParameters(subTagValue, st, searchResult.srpId(), ZSSdkUtil.SRP_SUBSCRIBE_TITLE);
                            mainHttp.doRequest(request);

                            // http.subscribeDelete(getToken(), subTagValue, st);
                            UpEventAgent.onSrpUnsubscribe(this, keyword, srpId);
                        } else {
                            // if (isSearch) {
                            UpEventAgent.onZSMenuSubscribe(this);    //ZSSDK 统计事件  页内菜单：订阅 统计
                            dismissPupWindows();
                            LoginAlert loginDialog = new LoginAlert(SRPActivity.this,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            subKeyword(ZSSdkUtil.SRP_SUBSCRIBE_MENU);
                                        }

                                    });
                            loginDialog.show();
                            subIsRunning = false;
                            return;
                        }
                    }
                } else {
                    subKeyword(ZSSdkUtil.SRP_SUBSCRIBE_TITLE); //订阅  //SRP头部
                }
                break;
        }
        dismissPupWindows();

    }

    /**
     * Srp词按钮点击分享
     */
    private void SrpShareBtnOnclick() {
        this.srpShareUrl = UrlConfig.srp + StringUtils.enCodeRUL(keyword)
                + "&srpId=" + srpId + CommonStringsApi.getUrlAppendIgId();
        if (shortUrl == null && !StringUtils.isEmpty(srpShareUrl)) {
//            http.shortURL(srpShareUrl);
            getShorUrl(srpShareUrl);
        }
        if (keyword.equals(TradeUrlConfig.MEGAGAME_SEARCH_KEYWORD)) { // 超级搜索
//            http.shortURL(TradeUrlConfig.YZD_WEINXIN_SHARE_URL);
            getShorUrl(TradeUrlConfig.YZD_WEINXIN_SHARE_URL);
        }
        returnSRPShareStr();
        popShareWindow();
    }

    /**
     * 获取短链
     *
     * @param url
     */
    private void getShorUrl(String url) {
        ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
        req.setParams(url);
        CMainHttp.getInstance().doRequest(req);
    }

    /**
     * 返回SRP分享内容
     */
    private void returnSRPShareStr() {
        // int type = StringUtils.isSuperSrp(keyword, null);
        switch (replaceBg) {
            case 1:
                contentStr = getString(R.string.srp_cj_share_title);
                contentStr = String.format(contentStr, CommonStringsApi.APP_NAME);
                break;
            case 2:
                contentStr = getString(R.string.srp_cm_share_title);
                contentStr = String.format(contentStr, CommonStringsApi.APP_NAME);
                break;
            default:
                contentStr = getString(R.string.srp_share_content);
                contentStr = String.format(contentStr, CommonStringsApi.APP_NAME,
                        keyword);
                break;
        }
        contentStr = String.format(contentStr, keyword);
    }

    /**
     * 选择提示对话框
     */
    public void ShowPickDialog() {
        MMAlert.showAlert(this, " ",
                getResources().getStringArray(R.array.fenxiang_item), null,
                new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0: // 拍照
                                srpShareMenu.onClick(srpShareMenu.getLl_weixin());
                                break;
                            case 1: // 相册
                                srpShareMenu.onClick(srpShareMenu.getLl_friend());
                                break;
                            default:
                                break;
                        }
                        // view_translucent.setVisibility(View.VISIBLE);
                    }

                });
    }

    private void setTitleBarBackground(int type) {
        if (type == 3) {
            switch (replaceBg) {
                case 1:
                    titlebar_bg.setBackgroundResource(R.drawable.titlebar_bg_red);
                    break;
                case 2:
                    titlebar_bg
                            .setBackgroundResource(R.drawable.cm_titlebar_bg_red);
                    break;
                case 4:
                    titlebar_bg
                            .setBackgroundResource(R.drawable.zh_ac_all_title_bg_red);
                    break;
                default:
                    titlebar_bg.setBackgroundColor(getResources().getColor(
                            R.color.all_titlebar_bg_color));
                    break;
            }
        } else {
            switch (replaceBg) {
                case 1:
                    titlebar_bg.setBackgroundResource(R.drawable.all_title_bg_red);
                    break;
                case 2:
                    titlebar_bg
                            .setBackgroundResource(R.drawable.cm_all_title_bg_red);
                    break;
                case 4:
                    titlebar_bg
                            .setBackgroundResource(R.drawable.zh_ac_all_title_bg_red);
                    break;
                default:
                    titlebar_bg.setBackgroundColor(getResources().getColor(
                            R.color.all_titlebar_bg_color));
                    break;
            }
        }
    }

    /**
     * 弹出框消失
     */
    private void dismissPupWindows() {
        if (null != mTabMsgPopupWindow) {
            mTabMsgPopupWindow.dismiss();
        }
    }

    public void showView(int type) {
        setTitleBarBackground(type);
        switch (type) {
            case 0: // 网络连接失败，显示刷新view
                pbHelp.showNetError();
                break;
            case 1: // 连接成功,隐藏刷新view
                pbHelp.goneLoading();
                break;
            case 2: // 2代结果
                pagerSlidingTabStrip.setVisibility(View.GONE);
                findViewById(R.id.ll_3g).setVisibility(View.VISIBLE);
                // findViewById(R.id.view_tmp).setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                showView(1);
                break;
            case 3: // 3代结果
                pagerSlidingTabStrip.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                findViewById(R.id.ll_3g).setVisibility(View.VISIBLE);
                showView(1);
                break;
            case 4: // 消歧页
                findViewById(R.id.xiaoqi_page).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_3g).setVisibility(View.GONE);
                showView(1);
                break;
            case 5: // 没有搜索结果
                findViewById(R.id.ll_nosearchresult).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_3g).setVisibility(View.GONE);
                showView(1);
                break;
            default:
                break;
        }
    }

    public void initsendSearchResultRequest(String url, int start) {
        if (!TextUtils.isEmpty(url)) { // RSS
//            http.searchResult(url, start, 10, getToken());
            SrpListRequest srpRequest = new SrpListRequest(HttpCommon.SRP_LIST_RSS_REQUEST, this);
            srpRequest.addParams(url, start, 10, false);
            mainHttp.doRequest(srpRequest);
        } else if (!TextUtils.isEmpty(keyword)) {// 第一次进入srp页或者消岐
            if (start == 0) {
//                http.searchResultByKeyword(keyword, srpId, isSearch, opSource, false);
                SrpNavRequest navRequest = new SrpNavRequest(HttpCommon.SRP_LIST_NAV_REQUEST, this);
                navRequest.addParams(keyword, srpId, isSearch, opSource, false);
                mainHttp.doRequest(navRequest);
            } else {
//                htp.searchResultByKeyword(keyword, srpId, start, opSource, false);
                SrpNavRequest navRequest = new SrpNavRequest(HttpCommon.SRP_LIST_NAV_REQUEST, this);
                navRequest.addParams(keyword, srpId, start, opSource, false);
                mainHttp.doRequest(navRequest);
            }
        }
    }

    public void sendSearchResultRequest(String url, int start) {
        if (!TextUtils.isEmpty(url)) { // RSS
//            htp.searchResult(url, start, 10, getToken());
            SrpListRequest srpRequest = new SrpListRequest(HttpCommon.SRP_LIST_RSS_REQUEST, this);
            srpRequest.addParams(url, start, 10, false);
            mainHttp.doRequest(srpRequest);
        } else if (!TextUtils.isEmpty(keyword)) {// 第一次进入srp页或者消岐
            if (start == 0) {
//                htp.searchResultByKeyword(keyword, srpId, isSearch, opSource, true);// 强刷
                SrpNavRequest navRequest = new SrpNavRequest(HttpCommon.SRP_LIST_NAV_REQUEST, this);
                navRequest.addParams(keyword, srpId, isSearch, opSource, true);
                mainHttp.doRequest(navRequest);
            } else {
//                htp.searchResultByKeyword(keyword, srpId, start, opSource, true);// 暂时用不到
                SrpNavRequest navRequest = new SrpNavRequest(HttpCommon.SRP_LIST_NAV_REQUEST, this);
                navRequest.addParams(keyword, srpId, start, opSource, true);
                mainHttp.doRequest(navRequest);
            }
        }
    }

    @Override
    public void onTabReselected(int position) {
        mViewPager.setCurrentItem(position);

    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//
//        if ("adList".equals(methodName))
//            return;
//        if ("subscribeDelete".equals(methodName)) {
//            SouYueToast.makeText(getApplicationContext(),
//                    R.string.subscribe_fail_cancel, SouYueToast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
//        if ("subscribeAddSrp".equals(methodName)) {
//            if (as.getCode() != 200) {
//                SouYueToast.makeText(getApplicationContext(),
//                        R.string.subscribe_fail, SouYueToast.LENGTH_SHORT)
//                        .show();
//            }
//            return;
//        }
//
//        if (isExpired && "searchResultByKeyword".equals(methodName)) {
//            return;
//        }
//        showView(0);
//    }

    @Override
    protected void onPause() {
        if (vf != null)
            vf.stopFlipping();
        super.onPause();
//        ToastUtil.show(this, "============ " + isFinishing());     //true
    }

    @Override
    protected void onResume() {
        if (vf != null)
            vf.startFlipping();
        requestCheckSub();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (ConfigApi.isPrintWebViewLogToSDCard) {
            // Add by ygp 收集log日志服务
            stopService(new Intent(this, LogService.class));
        }
        try {
            // unregisterReceiver(mBroadcastReceiver);
            unregisterReceiver(receiver);
            unregisterReceiver(mSubscribeStateBroadcastReceiver);
            unregisterReceiver(switchWidgetReceiver);
            // unregisterReceiver(mSubscribeStateBroadcastReceiver);//取消两次？？？？

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (victim != null)
            rightParentView.getViewTreeObserver().removeGlobalOnLayoutListener(
                    victim);
        super.onDestroy();
//        stopService(new Intent(this, DownloadBookZipService.class));
    }

    @Override
    public void onBackPressed() {
        backSrpHomePagerNav();
    }

    public void closeCurrentSrpActivity() {
        setResult(0xde, backIntent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void backSrpHomePagerNav() {
        if (!backHome) {
            if (!ConfigApi.isSouyue() && isFromPush) {
                IntentUtil.goHomeSouyue(this);
                this.finish();
            } else {
                closeCurrentSrpActivity();
            }
        } else {
            backHome = false;
            if (navs == null || navs.size() == 0) {
                return;
            }
            int currentItemId = getCurrentItem();

            int homePosition = 0;
            for (int i = 0; i < navs.size(); i++) {
                NavigationBar navbar = navs.get(i);
                if (navbar != null
                        && ConstantsUtils.FR_NEW_INDEX
                        .equals(navbar.category())) {
                    homePosition = i;
                    break;
                }
            }

            if (currentItemId == homePosition) {
                closeCurrentSrpActivity();
                return;
            }

            if (mViewPager != null && pagerSlidingTabStrip != null) {
                mViewPager.setCurrentItem(homePosition);
                pagerSlidingTabStrip.setViewPager(mViewPager);
                pagerSlidingTabStrip.updateSelectedTextColor(homePosition);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        if (data != null && resultCode == 6) {
            int[] readPos = data.getIntArrayExtra("readPos");
            if (readPos != null) {
                SRPFragment fragment = getCurrentFragment();
                if (fragment != null) {
                    SouyueAdapter tmpAdapter = fragment.adapter;
                    if (tmpAdapter != null) {
                        List<SearchResultItem> datas = tmpAdapter.getDatas();
                        if (datas != null && datas.size() == readPos.length) {
                            for (int i = 0; i < readPos.length; i++) {
                                if (readPos[i] == 1) {
                                    datas.get(i).hasRead_$eq(true);
                                }
                            }
                            tmpAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            // 从新闻详情页回来跳转到指定页面
            String toTitle = data.getStringExtra("toPos");
            if (StringUtils.isNotEmpty(toTitle) && mViewPager != null) {
                mViewPager.setCurrentItem(getCurrentItemByTitle(toTitle));
            }

            if (!ConfigApi.isSouyue() && requestCode == START_FOR_RESULT) {
                int count = data.getExtras().getInt("comment_count")
                        + Integer.parseInt(tv_read_commentcount.getText()
                        .toString());

                if (data.getExtras().getInt("comment_count") > 0) {
                    if (tv_read_commentcount != null)
                        tv_read_commentcount.setText("" + count);
                }
            }

        }
        if (ConfigApi.isSouyue()) {
            if (requestCode == KunlunJueFragment.FILECHOOSER_RESULTCODE) {
                getCurrentFragment().onActivityResult(requestCode, resultCode,
                        data);
            }
        } else {
            // 表单微件 上传 by zhaobo 2014-10-24
            SRPFragment mFragment = getCurrentFragment();
            if (mFragment instanceof KunlunJueFragment) {
                if (resultCode == -1 && data != null) {
                    mFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestCheckSub() {// 检查订阅状态
        if (null != keyword && null != srpId) {
            // if (subBtn != null && replaceBg == 0) {
            // subBtn.setVisibility(View.INVISIBLE);
            // http.subscribeCheck(getToken(), keyword, srpId);
            // }
            if (replaceBg == 0) {
                String type = "srp";
                if (searchResult != null && searchResult.getKeywordCate() == 4) {
                    type = "special";
                }

                SubCheckRequest request = new SubCheckRequest(HttpCommon.SUB_CHECK_REQUEST, this);
                request.addParams(keyword, srpId, type);
                mainHttp.doRequest(request);

                // .http.subscribeCheck(getToken(), keyword, srpId, type);
            }
        }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.SUB_CHECK_REQUEST:
                HttpJsonResponse response = request.getResponse();
                long bid = response.getBodyLong();
                subBtn2.setVisibility(View.VISIBLE);
                if (null != llSubscribeStateLayout) {
                    subBtn.setVisibility(View.VISIBLE);
                    llSubscribeStateLayout.setVisibility(View.VISIBLE);
                    showShareBtn();
                    boolean b = bid > 0;
                    if (b) {
                        if (ivSubscribeStateIcon != null && tvSubscribeStateText != null) {
                            ivSubscribeStateIcon.setImageResource(R.drawable.pop_tuiding);
                            tvSubscribeStateText.setText("退订");
                            llSubscribeStateLayout.setTag(bid);
                        }
                        isSubscrible = true;
                        if (subBtn2 != null)
                            subBtn2.setImageResource(R.drawable.srp_no_subscribe_selector);
                        subBtn.setTag(bid);
                    } else {
                        isSubscrible = false;
                        if (subBtn2 != null)
                            subBtn2.setImageResource(R.drawable.srp_subscribe_selector);
                        if (ivSubscribeStateIcon != null && tvSubscribeStateText != null) {
                            ivSubscribeStateIcon.setImageResource(R.drawable.pop_dingyue);
                            tvSubscribeStateText.setText("订阅");
                            llSubscribeStateLayout.setTag(new Long(0));
                        }
                        subBtn.setTag(new Long(0));
                    }
                    subBtn.setEnabled(true);
                }
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                favoriteDeleteSuccess();
                break;
            case HttpCommon.CIRLCE_ADD_FAVORATE_ID:
                favoriteAddSuccess(request.<HttpJsonResponse>getResponse().getBodyLong("newsId"));
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortURLSuccess(request.<HttpJsonResponse>getResponse().getBodyString());
                break;
            case HttpCommon.SRP_AD_LIST_REQUEST:
                getAdListSuccess((AdList) request.getResponse());
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse resShare = request.getResponse();
                shareCallback(resShare.getBodyLong("newsId"));
                break;
            case HttpCommon.DETAIL_UP:
                HttpJsonResponse resUp = request.getResponse();
                upSuccess(resUp.getBodyLong("newsId"));
                break;
            case HttpCommon.SRP_LIST_NAV_REQUEST:
            case HttpCommon.SRP_LIST_RSS_REQUEST:
                searchResultSuccess((SearchResult) request.getResponse());
                break;
            case HttpCommon.SUB_ADD_REQUEST:
                subscribeAddSrp((HttpJsonResponse) request.getResponse());
                break;
            case HttpCommon.SUB_DELETE_REQUEST:
                subscribeDeleteSuccess();
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.SUB_CHECK_REQUEST:
                showShareBtn();
                if (llSubscribeStateLayout != null) {
                    if (llSubscribeStateLayout.getVisibility() == View.VISIBLE) {
                        llSubscribeStateLayout.setVisibility(View.GONE);
                    }
                }
                break;
            case HttpCommon.SRP_AD_LIST_REQUEST:
                return;
            case HttpCommon.DETAIL_UP:
                break;
            case HttpCommon.SUB_ADD_REQUEST:
//                if (subBtn2 != null) subBtn2.setClickable(true);
                SouYueToast.makeText(getApplicationContext(),
                        R.string.subscribe_fail, SouYueToast.LENGTH_SHORT)
                        .show();
                subIsRunning = false;
                return;
            case HttpCommon.SUB_DELETE_REQUEST:
//                if (subBtn2 != null) subBtn2.setClickable(true);
                SouYueToast.makeText(getApplicationContext(),
                        R.string.subscribe_fail_cancel, SouYueToast.LENGTH_SHORT)
                        .show();
                subIsRunning = false;
                return;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                SouYueToast.makeText(this, "取消失败", Toast.LENGTH_SHORT).show();
                break;

        }
        showView(0);
    }


    public void subscribeAddSrp(HttpJsonResponse response) {
//        if (subBtn2 != null) subBtn2.setClickable(true);
        if (response != null) {
            if (subBtn2 != null) subBtn2.setImageResource(R.drawable.srp_no_subscribe_selector);
            SubscribeBack subscribeBack = new Gson().fromJson(response.getBody(), SubscribeBack.class);
            Integer id = subscribeBack.id().get(0);
            SouYueToast.makeText(getApplicationContext(),
                    R.string.subscribe__success, SouYueToast.LENGTH_SHORT).show();
            sysp.putBoolean(SYSharedPreferences.KEY_UPDATE, true);
            subScribeState = 1;
            changeText();
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_SRP_CLICK);
            //获取srp的icon
            String imageIcon = CommonStringsApi.getSrpIconUrl(this, srpId);
            if (searchResult.getKeywordCate() == DetailActivity.KEYWORD_CATE_SPECIAL) {//专题类型
                //数据库操作
                SuberedItemInfo info = new SuberedItemInfo();
                info.setId(id);
                info.setTitle(keyword);
                info.setCategory("special");
                info.setSrpId(srpId);
                info.setKeyword(keyword);
                NavigationBar bar = navs.get(0);
                info.setUrl(bar.url());
                info.setImage(imageIcon);
                suberDao.addOne(info);
            } else {
                //数据库操作
                SuberedItemInfo info = new SuberedItemInfo();
                info.setId(id);
                info.setTitle(keyword);
                info.setCategory("srp");
                info.setSrpId(srpId);
                info.setKeyword(keyword);
                info.setImage(imageIcon);
                suberDao.addOne(info);
            }
        }
        subIsRunning = false;
        setBackIntent(1, srpId);
    }

    private void subKeyword(String opSource) {
        if (keyword != null && !keyword.trim().equals("")) {
            String type = "srp";
            if (searchResult != null && searchResult.getKeywordCate() == 4) {
                type = HomeBallBean.SPECIAL;
            }

            SubAddReq req = new SubAddReq(HttpCommon.SUB_ADD_REQUEST, this);
            req.addParameters(keyword, srpId, -1 + "", "", type, opSource);
            mainHttp.doRequest(req);

            UpEventAgent.onSrpSubscribe(this, keyword, srpId);
        } else {
            subIsRunning = false;
//            SouYueToast.makeText(getApplicationContext(),
//                    R.string.subscribe_fail, SouYueToast.LENGTH_SHORT).show();
        }
    }



	/*
	 * public void onSubcibeClick(View view) { if (mIsFinish) { finish();
	 * return; } // 订阅列表定位Tab，传Bundle Bundle bundle = new Bundle();
	 * bundle.putInt(MySubscribeListActivity.INTENT_INDEX,
	 * R.string.manager_grid_subject);
	 *
	 * IntentUtil.openManagerAcitivity(this, MySubscribeListActivity.class,
	 * bundle); }
	 */

    private void createMorePupWindow() {
        // View popupView = getLayoutInflater().inflate(R.layout.ac_srp_pop,
        // null);
        if (mTabMsgPopupWindow == null && popupView != null) {
            mTabMsgPopupWindow = new PopupWindow(popupView, getResources()
                    .getDimensionPixelSize(R.dimen.space_210),
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            mTabMsgPopupWindow.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss() {
                    // subBtn.setBackgroundResource(R.drawable.title_bar_menu_selector);
                    Drawable nav_up = getResources().getDrawable(
                            R.drawable.title_bar_menu_selector);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
                            nav_up.getMinimumHeight());
                    subBtn.setCompoundDrawables(null, null, nav_up, null);
                }
            });

            mTabMsgPopupWindow.setFocusable(true);
            mTabMsgPopupWindow.setOutsideTouchable(true);
            mTabMsgPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (popupView != null) {
            popupView.findViewById(R.id.pop_erweima).setOnClickListener(this);
            popupView.findViewById(R.id.pop_fenxiang).setOnClickListener(this);
            popupView.findViewById(R.id.pop_tuiding).setOnClickListener(this);
            popupView.findViewById(R.id.pop_senderror).setOnClickListener(this);
            popupView.findViewById(R.id.pop_create_shortcut)
                    .setOnClickListener(this);
            popupView.findViewById(R.id.pop_cooper_manage).setOnClickListener(
                    this);
            if (version != 3) {
                pop_zuti.setVisibility(View.GONE);
            }
        }
        // subBtn.setBackgroundResource(R.drawable.imgbtn_menu_selected);
        int currItemPos = getCurrentItem();
        if (navs == null || navs.size() == 0) {
            return;
        }
        isEditSrpWiget(currItemPos);

        Drawable nav_up = getResources().getDrawable(
                R.drawable.imgbtn_menu_selected);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
                nav_up.getMinimumHeight());
        subBtn.setCompoundDrawables(null, null, nav_up, null);
    }

    /**
     * add by yinguanping 标题栏小三角点击弹出事件
     *
     * @param v
     */
    public void btnPopTitle(View v) {
        if (mViewPager != null) {
            if (mViewPager.getAdapter().getCount() == 0
                    || mViewPager.getAdapter().getCount() == 1)
                return;
        }
        // 点击事件处理,更改pop状态
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            findViewById(R.id.btn_popTitle).setBackgroundResource(
                    R.drawable.srp_poptitle_sanjiao_press);
            popAwindow(v);
            // SrpPopTile_Animation.popTiltleAnimation(popWindow,
            // v.getBottom() * 1.5, popWindow.getHeight() * 1.5);
        } else {
            findViewById(R.id.btn_popTitle).setBackgroundResource(
                    R.drawable.srp_poptitle_sanjiao_normal);
            if (popWindow != null) {
                popWindow.dismiss();
            }
        }

        UpEventAgent.onZSSrpClick(this);    //ZSSDK 统计事件
    }

    /**
     * 弹出框
     *
     * @param parent
     */
    private void popAwindow(View parent) {
        if (popWindow == null) {
            createPopWindow();
        }
        if (srpPopTitleAdapter != null) {
            srpPopTitleAdapter.setIndex(mViewPager.getCurrentItem());
            srpPopTitleAdapter.notifyDataSetChanged();
        }

        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        popWindow.setOnDismissListener(new OnDismissListener() {

            public void onDismiss() {
                isOpenPop = false;
                findViewById(R.id.btn_popTitle).setBackgroundResource(
                        R.drawable.srp_poptitle_sanjiao_normal);
            }
        });
        popWindow.update();
        popWindow.showAtLocation(
                parent,
                Gravity.TOP,
                0,
                DeviceUtil.dip2px(this, 47)
                        + Utils.getTitleBarHeight(this));

    }

    private void createPopWindow() {
        LayoutInflater lay = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = lay.inflate(R.layout.srp_poptitile, null);
        GridView gridView = (GridView) v
                .findViewById(R.id.srp_poptitle_gridview);
        if (srpPopTitleAdapter == null) {
            srpPopTitleAdapter = new SrpPopTitleAdapter(this, mViewPager);
        }
        gridView.setAdapter(srpPopTitleAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                srpPopTitleAdapter.setIndex(position);
                srpPopTitleAdapter.notifyDataSetChanged();
                if (position < mViewPager.getAdapter().getCount()) {
                    mViewPager.setCurrentItem(position);
                }
                popWindow.dismiss();
            }
        });

        popWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置背景
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.common_pop_bg));
    }

    /**
     * SRP分享
     */
    private void popShareWindow() {

        if (TextUtils.isEmpty(srpId))
            return;

        if (StringUtils.isEmpty(keyword)) {
            mShareMenuDialog = new ShareMenuDialog(this, this,
                    ShareConstantsUtils.READABILITYKEYWORD);
        } else if (StringUtils.isSuperSrp(keyword, null) != 0) {
            mShareMenuDialog = new ShareMenuDialog(this, this,
                    ShareConstantsUtils.SRP);// 超级分享、超模大赛分享改成和SPR分享菜单一致
        } else {
            mShareMenuDialog = new ShareMenuDialog(this, this,
                    ShareConstantsUtils.SRP);
        }
        mShareMenuDialog.showBottonDialog();
    }

    /**
     * 首页，点击跳转到相应的微件
     */
    public void jsClickToWidget(String md5) {
        for (int i = 0; i < navs.size(); i++) {
            NavigationBar bar = navs.get(i);
            Log.d(TAG, "md5==" + md5 + ",new====" + bar.md5());
            if (md5.equals(bar.md5())) {
                if (popWindow == null) {
                    createPopWindow();
                }
                srpPopTitleAdapter.setIndex(i);
                srpPopTitleAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void loadData(int position) {

        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(this)) {
            // 点击分享了此处加统计
            // UpEventAgent.onNewsShare(this, keyword, keyword, srpId, title,
            // sourceUrl);

            content = getShareContent();
            switch (position) {
                case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                    boolean islogin = (SYUserManager.getInstance().getUser()
                            .userType().equals(SYUserManager.USER_ADMIN));
                    if (islogin) {
                        UpEventAgent.onSrpShare(this, keyword, srpId, "syfriend");
                        ImShareNews imsharenews = new ImShareNews(
                                content.getKeyword(), content.getSrpId(),
                                content.getContent(), null, content.getPicUrl());
                        // ContactsListActivity.startSYIMFriendAct(this,
                        // imsharenews);
                        IMShareActivity.startSYIMFriendAct(this, imsharenews);
                    } else {
                        toLogin();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_SINA:
                    UpEventAgent.onSrpShare(this, keyword, srpId, "sina");
                    mSsoHandler = ShareByWeibo.getInstance().share(this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    UpEventAgent.onSrpShare(this, keyword, srpId, "wx");
                    ShareByWeixin.getInstance().share(content, false);
                    break;
                case ShareMenuDialog.SHARE_TO_FRIENDS:
                    String wxFriendUrl = content.getUrl();
                    if (null != wxFriendUrl
                            && wxFriendUrl.contains("urlContent.groovy?")) {
                        wxFriendUrl = wxFriendUrl.replace(
                                "urlContent.groovy?",
                                "urlContent.groovy?keyword="
                                        + StringUtils.enCodeRUL(keyword)
                                        + "&srpId=" + srpId + "&");
                    }
                    content.setUrl(wxFriendUrl);
                    UpEventAgent.onSrpShare(this, keyword, srpId, "friend");
                    ShareByWeixin.getInstance().share(content, true);
                    break;
                case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                    UpEventAgent.onSrpShare(this, keyword, srpId, "qfriend");
                    ShareByTencentQQ.getInstance().share(this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                    UpEventAgent.onSrpShare(this, keyword, srpId, "qzone");
                    ShareByTencentQQZone.getInstance().share(this, content);
                    break;
                default:
                    break;
            }
        } else {
            SouYueToast.makeText(this, getString(R.string.nonetworkerror),
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public ShareContent getShareContent() {
        if (!TextUtils.isEmpty(imgUrl)) {
            if (!keyword.equals(TradeUrlConfig.MEGAGAME_SEARCH_KEYWORD)) {
//                imageBitmap = a.getCachedImage(imgUrl);
                File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(imgUrl);
                if (fileImage != null) {
                    imageBitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
                }
                if (imageBitmap == null) {
                    File cache = imageLoader.getDiscCache().get(imgUrl);
                    if (cache != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        imageBitmap = BitmapFactory.decodeFile(cache
                                .getAbsolutePath());
                    }
                }
            }
        } else {
            imageBitmap = null;
        }

        String url = shortUrl;
        if (StringUtils.isEmpty(url)) {
            url = ZSEncode.encodeURI(StringUtils.enCodeKeyword(srpShareUrl));
        }

        ShareContent result;
        if (keyword.equals(TradeUrlConfig.MEGAGAME_SEARCH_KEYWORD)) { // 超级搜索分享设置参数
            if (TradeUrlConfig.MATCH_SHAREURL_IGID.equals(TradeUrlConfig.IGID)) {
                result = new ShareContent(keyword,
                        StringUtils.isNotEmpty(shortUrl) ? shortUrl
                                : TradeUrlConfig.YZD_WEINXIN_SHARE_URL,
                        imageBitmap, "千家机构，亿元基金，“中华之梦”创业大赛恭候创业新贵！", imgUrl);
            } else {
                result = new ShareContent(keyword, url, imageBitmap,
                        "千家机构，亿元基金，“中华之梦”创业大赛恭候创业新贵！", imgUrl);
            }
        } else {
            result = new ShareContent(keyword, url, imageBitmap, contentStr,
                    imgUrl);
        }

        result.setSharePointUrl(srpShareUrl != null ? srpShareUrl : "");
        result.setKeyword(keyword);
        result.setSrpId(srpId);
        return result;
    }

    public void shortURLSuccess(String url) {
        shortUrl = url;
    }

    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        startActivity(intent);
    }

    // add by trade
    private void initTradeView() {
        utype = SYUserManager.getInstance().getUserType();
        initBottomBar();
    }

    /**
     * setBottomMenu:设置底部菜单是否显示. <br/>
     *
     * @param visibility
     * @author zhaobo
     * @date 2014-10-31 下午3:45:49
     */
    public void setBottomMenu(final int visibility) {
        bottom_bar.post(new Runnable() {

            @Override
            public void run() {

                bottom_bar.setVisibility(visibility);

            }
        });

    }

    /**
     * showShareWindow:显示底部导航. <br/>
     *
     * @author zhaobo
     * @date 2014-10-31 下午4:01:15
     */
    private void showShareWindow() {
        if (mShareMenuDialog == null) {
            if (StringUtils.isEmpty(keyword)) {
                mShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.READABILITYKEYWORD);
            } else if (StringUtils.isSuperSrp(keyword, null) != 0) {
                mShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.SUPERSRP);
            } else {
                mShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.READABILITY);
            }
        }
        int bottomHeight = bottom_bar.getHeight();
        if (bottomHeight > 0) {
            mShareMenuDialog.showBottonDialog(bottomHeight);
        }

    }

    /**
     * 查询当前新闻的评论数目、赞和收藏的状态回调
     *
     * @param newsCount
     */
    public void changeNewsCount(NewsCount newsCount) {
        hasFavorited = newsCount.hasFavorited();
        hasUp = newsCount.hasUp();
        this.newsCount = newsCount;
        setButtonState();
        changeBottomBarValues();
    }

    /**
     * 设置底部图片
     */
    private void setButtonState() {
        changeCollectState();
        changetUpState();
    }

    public void changeBottomBarValues() {
        if (newsCount != null) {
            tv_read_commentcount.setText(newsCount.commentsCount() + "");
            upCount.setText(newsCount.upCount() + "");
        } else {
            tv_read_commentcount.setText("0");
            upCount.setText("0");
        }
    }

    /**
     * trade use only 修改收藏图标
     */
    private void changeCollectState() {
        if (hasFavorited) {
            ib_collect.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_collect_unnormal));
        } else {
            ib_collect.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_collect_normal));
        }
        ib_collect.setEnabled(true);
    }

    /**
     * trade use only 修改赞图标
     */
    private void changetUpState() {
        if (hasUp) {
            upLayout.setEnabled(false);
            web_sre_up.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_up_unnormal));
        } else {
            upLayout.setEnabled(true);
            web_sre_up.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_up_normal));
        }
    }

    /**
     * initBottomBar:初始化底部导航：点赞、分享、评论、收藏. <br/>
     *
     * @author fm
     * @date 2014年11月3日 下午3:22:29
     */
    private void initBottomBar() {
        bottom_bar = (LinearLayout) findViewById(R.id.web_webview_bottom_bar);
        web_sre_up = (ImageButton) findViewById(R.id.web_sre_up);
        upCount = (TextView) findViewById(R.id.up_count);
        comment_icon = (ImageButton) findViewById(R.id.comment_icon);
        ib_share = (ImageButton) findViewById(R.id.ib_read_share);
        ib_collect = (ImageButton) findViewById(R.id.ib_read_collect);
        view_translucent = findViewById(R.id.view_translucent);
        view_translucent.getBackground().setAlpha(100);
        tv_read_commentcount = (TextView) findViewById(R.id.tv_read_commentcount);
        comment_layout = findViewById(R.id.comment_layout);
        BottomButtonClick bbc = new BottomButtonClick();
        comment_layout.setOnClickListener(bbc);
        upLayout = findViewById(R.id.up_layout);
        upLayout.setOnClickListener(bbc);
        comment_icon.setOnClickListener(bbc);
        ib_collect.setOnClickListener(bbc);
        ib_share.setOnClickListener(bbc);
    }

    /**
     * 底部菜单点击事件 目前只支持kunlunjueFragment <br/>
     * Company: ZhongSou.com<br/>
     * Copyright: 2003-2014 ZhongSou All right reserved<br/>
     *
     * @author zhaobo
     * @date 2014-11-12 上午10:42:16
     */
    private class BottomButtonClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            final String webViewUrl = ((KunlunJueFragment) getCurrentFragment()).webViewUrl;
            final SearchResultItem searchResultItem = ((KunlunJueFragment) getCurrentFragment()).searchResultItem;
            List<String> images = searchResultItem.image();
            final String picUrl = images != null ? images.get(0) : "";
            switch (v.getId()) {
                case R.id.up_layout: // 点赞
                    if (!hasUp) {
                        upLayout.setEnabled(false);
                        upCount.setText(Integer.parseInt(upCount.getText()
                                .toString()) + 1 + "");
                        DetailUpReq up = new DetailUpReq(HttpCommon.DETAIL_UP, SRPActivity.this);
                        up.setParams(webViewUrl, searchResultItem.title(),
                                picUrl, searchResultItem.description(), 0L, "",
                                searchResultItem.keyword(),
                                searchResultItem.srpId());
                        mMainHttp.doRequest(up);
//                        http.up(getToken(), webViewUrl, searchResultItem.title(),
//                                picUrl, searchResultItem.description(), 0L, "",
//                                searchResultItem.keyword(),
//                                searchResultItem.srpId());
                        web_sre_up.setEnabled(false);
                    }
                    break;
                case R.id.comment_icon: // 评论
                    if (FastDoubleCliceUtils.isFastDoubleClick())
                        return;
                    Intent i = new Intent();
                    Bundle bundle = new Bundle();
                    searchResultItem.url_$eq(webViewUrl);
                    bundle.putSerializable("searchResultItem", searchResultItem);
                    i.setClass(SRPActivity.this, CommentaryActivity.class);
                    i.putExtras(bundle);
                    startActivityForResult(i, START_FOR_RESULT);
                    break;
                case R.id.ib_read_collect: // 收藏
                    // 发请求 如果回调成功 调用changeCollectState()改变全局变量hasfavorited和图片状态
                    if (hasFavorited) {// 当前已订阅,取消订阅
//                        http.favoriteDelete(getToken(), webViewUrl);
                        CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID, SRPActivity.this);
                        req.setParams(getToken(), webViewUrl);
                        CMainHttp.getInstance().doRequest(req);
                        ib_collect.setEnabled(false);
                    } else {
                        FavoriteAlert favoriteDialog = new FavoriteAlert(
                                SRPActivity.this,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        http.favoriteAdd(getToken(), webViewUrl,
//                                                searchResultItem.title(), picUrl,
//                                                searchResultItem.description(), 0L,
//                                                "", searchResultItem.keyword(),
//                                                searchResultItem.srpId());
                                        AddFavoriteReq req = new AddFavoriteReq(HttpCommon.CIRLCE_ADD_FAVORATE_ID, SRPActivity.this);
                                        req.setParams(getToken(), webViewUrl,
                                                searchResultItem.title(), picUrl,
                                                searchResultItem.description(), 0L,
                                                "", searchResultItem.keyword(),
                                                searchResultItem.srpId());
                                        CMainHttp.getInstance().doRequest(req);

                                        ib_collect.setEnabled(false);
                                    }
                                });
                        favoriteDialog.show();
                    }
                    break;
                case R.id.ib_read_share: // 分享
                    if (getCurrentFragment() instanceof KunlunJueFragment) {
                        showShareWindow();
                    }
                    break;
            }
        }
    }

    /**
     * 修改收藏图标
     */
    public void commentResult(int count) {
        int commentCount = count
                + Integer.parseInt(tv_read_commentcount.getText().toString());

        if (count > 0) {
            if (tv_read_commentcount != null)
                tv_read_commentcount.setText("" + commentCount);
        }
    }

    // 添加订阅回调
    public void favoriteAddSuccess(Long l) {
        SouYueToast.makeText(this, R.string.favorite_add,
                SouYueToast.LENGTH_SHORT).show();
        hasFavorited = true;
        this.newsId = l;
        changeCollectState();
    }

    /**
     * 取消收藏成功回调
     *
     * @param
     */
    public void favoriteDeleteSuccess() {
        SouYueToast.makeText(this, R.string.favorite_del,
                SouYueToast.LENGTH_SHORT).show();
        hasFavorited = false;
        changeCollectState();
    }

    /**
     * 点赞成功回调
     *
     * @param id
     */
    public void upSuccess(Long id) {
        hasUp = true;
        this.newsId = id;
        changetUpState();
    }

    public void shareCallback(Long id) {
        this.newsId = id;
        SouYueToast.makeText(this, R.string.share_success,
                SouYueToast.LENGTH_SHORT).show();
    }

    private void share2SYFriends(ShareContent content) {
        Bundle b = new Bundle();
        Intent i = new Intent();
        SearchResultItem searchResultItem = new SearchResultItem();
        // searchResultItem.url_$eq(url);
        searchResultItem
                .url_$eq(((KunlunJueFragment) getCurrentFragment()).shareUrl);
        searchResultItem.keyword_$eq(getString(R.string.SRP_KW));
        searchResultItem.title_$eq(getString(R.string.SRP_KW));
        searchResultItem.srpId_$eq(getString(R.string.SRP_ID));
        b.putSerializable("searchResultItem", searchResultItem);
        i.setClass(this, ShareToSouyueFriendsDialog.class);
        i.putExtras(b);
        i.putExtra("content", content.getContent());
        startActivity(i);
    }

    // 分享到精华区
    public void shareToDigest() {
        long newsId = newsCount != null ? newsCount.newsId() : 0;
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
//            http.share(getToken(), newsId);
        } else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(((KunlunJueFragment) getCurrentFragment()).shareUrl,
                    StringUtils.shareTitle(getString(R.string.KW),
                            getString(R.string.KW)), "",
                    getString(R.string.KW), "", getString(R.string.KW),
                    getString(R.string.SRP_KW), getString(R.string.SRP_ID));
            mMainHttp.doRequest(share);
//            http.share(getToken(),
//                    ((KunlunJueFragment) getCurrentFragment()).shareUrl,
//                    StringUtils.shareTitle(getString(R.string.KW),
//                            getString(R.string.KW)), "",
//                    getString(R.string.KW), "", getString(R.string.KW),
//                    getString(R.string.SRP_KW), getString(R.string.SRP_ID));
        }
    }

    /**
     * 创建桌面快捷方式，有些手机会创建桌面小工具，此处正是判断是否来自桌面小工具的 属于非常特殊的情况，不必过于纠结
     *
     * @return
     */
    private boolean isFromAppWidget() {
        return StringUtils.isEmpty(keyword) && StringUtils.isEmpty(srpId)
                && StringUtils.isEmpty(mUrl) && StringUtils.isEmpty(from);
    }

    /**
     * 缓存SRP图片到本地备用 如果已经缓存过，则不会重复缓存
     */
    private void doCacheImg() {
        if (ImageLoader.getInstance().getDiscCache().get(imgUrl) != null) {
            if (BitmapFactory.decodeFile(ImageLoader.getInstance()
                    .getDiscCache().get(imgUrl).getAbsolutePath()) == null) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheOnDisk(true).cacheInMemory(true)
                        .showImageOnLoading(R.drawable.default_small)
                        .displayer(new SimpleBitmapDisplayer()).build();
                // ImageLoader.getInstance().displayImage(imgUrl, new
                // ImageView(this), options);
                MyImageLoader.imageLoader.displayImage(imgUrl, new ImageView(
                        this), MyImageLoader.options);
            }
        }
    }

    private void isEditSrpWiget(int position) {
        int mAllowEdit = navs.get(position).getAllowEdit();// 0：不可以编辑，1：可以编辑
        if (pop_zuti != null) {
            if (mAllowEdit != 0) {
                pop_zuti.setEnabled(true);
                popZutiIcon.setImageResource(R.drawable.pop_zuti);
                popZutiIcon.setScaleType(ScaleType.CENTER_INSIDE);
                popZutiTitle.setTextColor(
                        getResources().getColor(R.color.srp_pop_menu_enable_bg));
            } else {
                popZutiIcon.setImageResource(R.drawable.srp_pop_bgp);
                popZutiIcon.setScaleType(ScaleType.CENTER_INSIDE);
                popZutiTitle.setTextColor(getResources().getColor(
                        R.color.srp_pop_menu_disable_bg));
                pop_zuti.setEnabled(false);
            }
        }
    }

    private void setBackIntent(int result, String srpId) {  // result 1: 订阅；2：退订
        //设置返回数据
        backIntent = new Intent();
        backIntent.putExtra("result", result);
        backIntent.putExtra("srp_id", srpId);
//        setResult(DetailModuleActivity.SRP_REQUEST_CODE, backIntent);

        if(result == 2) result = 0;     //订阅标志位映射

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(ConstantsUtils.SUB_STATUS_ACTION);
        intent.putExtra("result", result);   // result 1: 订阅；0：未订阅
        intent.putExtra("srp_id", srpId);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void textViewOnclick() {
        tabClickFlag = true;
        UpEventAgent.onZSNavigationbarClick(SRPActivity.this);  //ZSSDK 统计事件
    }
}
