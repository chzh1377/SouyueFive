package com.zhongsou.souyue.circle.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tuita.sdk.ContextUtil;
import com.zhongsou.souyue.BuildConfig;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.MyFavoriteActivity;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.adapter.TemplateNavigationAdapter;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.adapter.ListViewPostsAdapterNew;
import com.zhongsou.souyue.circle.model.CircleResponseResult;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.model.Reply;
import com.zhongsou.souyue.circle.ui.CPairSecondListView;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleFollowDialogNew;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.AppInfoUtils;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.dialog.WXShareEnveDialog;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.ADInfo;
import com.zhongsou.souyue.module.AdList;
import com.zhongsou.souyue.module.AdListItem;
import com.zhongsou.souyue.module.CWidgetHead;
import com.zhongsou.souyue.module.DetailItem;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.SubscribeBack;
import com.zhongsou.souyue.module.TemplateCommentsInfo;
import com.zhongsou.souyue.module.TemplateData;
import com.zhongsou.souyue.module.TemplateFootData;
import com.zhongsou.souyue.module.TemplateKeyData;
import com.zhongsou.souyue.module.TemplateShowData;
import com.zhongsou.souyue.module.TemplateWidgetData;
import com.zhongsou.souyue.module.TemplateWidgetNavData;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.circle.CircleExitCircleRequest;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.circle.MblogCanclePrimeReq;
import com.zhongsou.souyue.net.circle.MblogCancleTopReq;
import com.zhongsou.souyue.net.circle.MblogDeleteReq;
import com.zhongsou.souyue.net.circle.MblogPrimeReq;
import com.zhongsou.souyue.net.circle.MblogTopReq;
import com.zhongsou.souyue.net.detail.AddCommentDownReq;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.detail.AddFavorite2Req;
import com.zhongsou.souyue.net.detail.DetailBlogPushRequest;
import com.zhongsou.souyue.net.detail.DetailCircleFootRequest;
import com.zhongsou.souyue.net.detail.DetailModuleCircleRequest;
import com.zhongsou.souyue.net.detail.DetailModuleNewsRequest;
import com.zhongsou.souyue.net.detail.DetailNewsFootRequest;
import com.zhongsou.souyue.net.detail.NewCommentListRequest;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.detail.TemplateDownRequest;
import com.zhongsou.souyue.net.home.DisLikeRequest;
import com.zhongsou.souyue.net.moduleparse.CMoudleParse;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.srp.AdClickRequest;
import com.zhongsou.souyue.net.srp.AdListRequest;
import com.zhongsou.souyue.net.sub.SubAddReq;
import com.zhongsou.souyue.net.sub.SubDeleteReq;
import com.zhongsou.souyue.net.sub.SubUpdateRequest;
import com.zhongsou.souyue.net.volley.CDetailHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyLoadResponse;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.service.download.DownloadAlert;
import com.zhongsou.souyue.service.download.DownloadService;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.PairScrollView;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.indicator.TabPageIndicator;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface;
import com.zhongsou.souyue.ui.webview.onMeasureChangedListener;
import com.zhongsou.souyue.ui.webview.onScrollChangedListener;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.BrightnessUtil;
import com.zhongsou.souyue.utils.BroadCastUtils;
import com.zhongsou.souyue.utils.CVariableKVO;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.TemplateUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @description: 模板详情页
 * @auther: qubian YanBin
 * @data: 2016/2/29.
 */
public class DetailModuleActivity extends RightSwipeActivity implements
        OnClickListener, PickerMethod, IShareContentProvider,
        JavascriptInterface.ImagesListener,
        JavascriptInterface.OnJSClickListener, onScrollChangedListener,
        onMeasureChangedListener, GestureDetector.OnGestureListener, JavascriptInterface.GotoSrpListener {

    public static final long PAGE_SIZE_5 = 5; //每页数据N

    public static final int DEVICE_COME_FROM = 3;// 来自搜悦客户端
    public static final int UP_TYPE_MAIN = 1; // 对主贴点赞

    public static final int EDIT_IS_ALLOW = 1;

    public static final int DETAIL_TYPE_CIRCLE = 1;// 圈子类型
    public static final int DETAIL_TYPE_NEWS = 2;// srp类型
    public static final int DETAIL_TYPE_RSS = 3;// rss类型
    public static final int KEYWORD_CATE_SPECIAL = 4;//专题类型（属于srp类型）

    public static final int CIRCLE_TYPE_NEWS = 0;// 新闻
    public static final int CIRCLE_TYPE_CIRCLE = 1;// 帖子

    public static int is_bantalk;// //0-不禁言 1-禁言

    private ProgressBarHelper progress;
    private View listLoading;   //评论区圆形进度条
    private PairScrollView pairScrollView;
    private CPairSecondListView mPairSecond;
    private ViewGroup mFirstView;

    private TextView mActionBarTitle;
    private TextView titlebar_read_toac_text;
    private ImageButton goBack;
    private ImageButton btn_option;
    private ImageView img_arrow;

    private TemplateNavigationAdapter mHsvAdapter;
    private TabPageIndicator mHsvWidgets;
    private List<TemplateWidgetNavData> mHsvDatas;

    private CustomWebView mWebView;
    private ListView lvPost;
    private ProgressBarHelper list_progress;
    private LinearLayout mHaveNoComment;
    private ImageView mHeadUpIcon, mHeadDownIcon;
    private LinearLayout mHeaderView;
    private LinearLayout mHeadLayoutUp, mHeadLayoutDown, mHeadLayoutPay;
    private LinearLayout circle_bottom_bar;// 底部栏
    private View mListHeaderView;
    private View footerView;
    private TextView mHeadUpCount, mHeadDownCount, mHeadPayCount;

    private int mUpCount;
    private int mDownCount;
    private int mCommentCount;
    private boolean mHasUp;
    private boolean mHasDown;
    private boolean mUpDowning;
    private boolean mHasFavorited;
    private int mZsbCount;
    private int mShowComment;

    private ListViewPostsAdapterNew postAdapter;
    private List<CommentsForCircleAndNews> postsList;
    private List<CommentsForCircleAndNews> postsListHot;

    private RelativeLayout ding_layout;
    private RelativeLayout follow_post_layout;
    private TextView ding_count;// 赞的个数
    private TextView follow_post_count;// 跟帖的个数
    private ImageButton collect_imagebutton, ding_imagebutton,
            share_imagebutton;

    private String mKeyword;// 分享的关键字
    private String mSrpId;// 分享的关键字
    private StringBuilder mUrl = new StringBuilder();//页面要加载的url
    private String mSourceUrl; //列表中传过来的url
    private String interestType; // 私密圈是“1”，公开圈是“0”
    private String mChannel;
    private String mWeijianMd5;
    private String mTitle;
    private String mContent;
    private String imgs;
    private String interest_name = "";
    private String interest_logo = "";
    private String utype;
    private String nickname; // 登录用户在圈成员昵称
    private String image; // 登录用户在圈成员头像
    private String broadcast_tag_id; // 发帖后广播的标签ID，是单个
    private String broadcast_tag_name;// 发帖后广播标签名称
    private String mParamUrl; // 获取评论等信息的url；
    private String sign_id = ""; // 每条详情的唯一标识
    private String blogShareUrl;
    private String imageUrl = "";
    private String mStatisticsJumpPosition; // 推送和服务号点击统计标识

    public List<String> mImageUrls;
    public List<String> mShareImageUrls;

    private long mPushId;
    private long mBlogId;
    private long mInterestId;
    private long mSubcribeId;
    private long mblog_userId; // 发帖作者id
    private long newsId;
    private long last_sort_num;// 上一页最后一个跟帖的blog_id

    private int mDetailType;
    private int mOptionRoleType;
    private int mCircleType;
    private int mIsCanEdit;
    private int mWebOffsety;
    private int pno = 1;
    private int visibleLast;
    private int posState; // 是不是匿名帖子
    private int backFlag;
    private int brightness; // 屏幕亮度

    // 1新增，2编辑，3删除
    private int result_type; // 详情帖子编辑/删除后返回更新列表
    private boolean mSecondSuccess;
    private boolean mHeadSuccess;
    protected boolean mWebSuccess;
    private boolean mSignIdSuccess;
    private boolean mTouchShareOrEdit;
    private boolean mTouchTitle;
    private boolean isfreeTrial;
    private boolean isPrime;
    private boolean isToTop;
    private boolean prime;
    private boolean top;
    private boolean up;
    private boolean follow;
    private boolean needLoad;
    private boolean isLoadAll;
    private boolean isNew;
    private boolean isRefreshData;
    private boolean hasChanged;
    private boolean isSubscribeSuccess; // 弹出dialog，点击加入兴趣圈，成功后的标识
    private boolean isUpdateSuccess; //通知圈子首页刷新数据
    private boolean isLogin;
    private boolean isQuite;
    private boolean startedSrc;
    private boolean isWebViewScrollBottom;
    private boolean isFirstComing = true;
    private boolean isHasEdit; // 是不是已经编辑
    private boolean isFromEdit; // 是不是已经编辑
    private boolean mHasCommentDismiss; //SYFX-2191

    private com.zhongsou.souyue.circle.model.ShareContent circleShareContent;
    private SsoHandler mSsoHandler;
    private Posts mainPosts;
    private Uri imageFileUri;
    private DetailItem mItem;
    private SuberDao suberDao;

    private CircleFollowDialogNew circleFollowDialog;
    private ShareMenuDialog mCircleShareMenuDialog;

    private Bitmap imageBitmap;
    private ImageLoader imageLoader;

    protected CDetailHttp mVolleyHttp;

    private ViewGroup toTopView;
    private ViewGroup adminMoreView;
    private ViewGroup menuView;
    private ViewGroup addIMgView;

    private Dialog showDialog;
    private Dialog showDialogAdminMore;
    private Dialog showDialogAddImg;
    private Dialog menuViewDialog;

    private LinearLayout ll_admin_top;
    private LinearLayout ll_admin_cancle_top;
    private LinearLayout ll_admin_prime;
    private LinearLayout ll_admin_cancle_prime;
    private LinearLayout ll_admin_push;
    private LinearLayout ll_admin_cancle_push;
    private ImageButton btn_subscribe;  //顶部订阅按钮
    private ImageButton imgSwitch;
    private TextView tvBig;
    private TextView tvMiddle;
    private TextView tvSmall;
    private SeekBar seekBar;

    protected HomePageDBHelper mDBHelper;
    private boolean isSubcribe;
    private int subStatusFromJSON;     //接口数据返回的订阅状态
    private static final String OPSOURCE_FROM_HEAD = "newsdetail.srpclick.srp.view";  //新闻详情页头部srp点击访问srp
    private static final String OPSOURCE_FROM_SECONDLIST = "newsdetail.topnavigationbarclick.srp.view";  //新闻详情页头部导航点击访问srp
    private boolean is_private;
    private boolean subIsRunning;   //订阅按钮是否在运行
    private WXShareEnveDialog mWxShareDlg;
    //js订阅所需要传递的参数
    private String jsInsterestId;
    private String jsKeyword;
    private String jsSrpId;
    private String jsimageLogo;
    private static final int SUB_ADD_SUCCESS = 1;
    private static final int SUB_UPDATE = 2; //通知js刷新
    private CVariableKVO mDoneKvo;
    private CVariableKVO mCircleFootData; //加载圈子模板底部数据
    private CVariableKVO mNewsFootData; //加载新闻模板底部数据

    private int showAdminMoreOper;  //是否显示圈主工具“更多操作”
    public static String templatePath;    //区分模板版本和类型的路径，比如：module_1/interest_content_000
    private String templateVersion;    //模板最新版本
    public static final int SRP_REQUEST_CODE = 900;
    public static final int CIRCLE_REQUEST_CODE = 901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long cur = System.currentTimeMillis();
        mVolleyHttp = new CDetailHttp(this);
        mDBHelper = HomePageDBHelper.getInstance();

        setContentView(R.layout.details_posts);
        setCanRightSwipe(true);
        mTouchTitle = true;
        initView();// 初始化页面主View
        initData();
        setDetailTitle();

        lvPost.setVisibility(View.INVISIBLE);
        debugWebView(); //TODO
    }

    private void initCircle() {
        // 圈子刚开始需要请求的url
        mUrl.append(UrlConfig.HOST).append("interest/interest.content.groovy?blog_id=")
                .append(mBlogId).append("&token=")
                .append(SYUserManager.getInstance().getToken()).append("&pushfrom=")
                .append(mItem.getPushFrom()).append("&mid=").
                append(mItem.getMsgId()).append("&clickfrom=").append(mItem.getClickFrom());
        if (mInterestId > 0) {
            mUrl.append("&wjType=0");
        } else {
            mUrl.append("&wjType=1");
        }
        mUrl.append("&appName=").append(AppInfoUtils.getAppName(this));
        MakeCookie.synCookies(this, mUrl.toString());
        Log.d("callback", "url-----" + mUrl);
//        mWebView.loadUrl(mUrl.toString());

        sendCircleRequest(false);

        mWebSuccess = true;// 预计成功
    }

    private void sendCircleRequest(boolean isForceRefresh) {
        int requestId = HttpCommon.DETAIL_MODULE_CIRCLE;
        if(isForceRefresh){
            requestId = HttpCommon.DETAIL_MODULE_CIRCLE_FORCE;
        }
        DetailModuleCircleRequest request = new DetailModuleCircleRequest(requestId, this);
        request.setBlogParams(String.valueOf(mBlogId));
        request.setForceRefresh(isForceRefresh);
        CMainHttp.getInstance().doRequest(request);
    }

    private void initNews() {

        if (mSourceUrl != null && !mSourceUrl.equals("") || mPushId != 0) {
            mUrl.append(getNewsUrl());
            MakeCookie.synCookies(this, mUrl.toString());// 耗时33毫秒
            if (mUrl.toString().contains("?")) {
                mUrl.append("&");
            } else {
                mUrl.append("?");
            }
            mUrl.append("pushfrom=")
                    .append(mItem.getPushFrom()).append("&mid=")
                    .append(mItem.getMsgId()).append("&clickfrom=").append(mItem.getClickFrom())
                    .append("&keyword=" + mKeyword).append("&srpid=").append(mSrpId)
            ;
            if (mInterestId > 0) {
                mUrl.append("&wjType=0");
            } else {
                mUrl.append("&wjType=1");
            }
            Log.d("callback", "-----" + mUrl);
//            mWebView.loadUrl(mUrl.toString());
            DetailModuleNewsRequest request = new DetailModuleNewsRequest(HttpCommon.DETAIL_MODULE_NEWS, this);
            request.setParams(mKeyword, String.valueOf(mPushId), mSrpId, mSourceUrl);   //传入 pushId
            CMainHttp.getInstance().doRequest(request);

            mWebSuccess = true;// 预计成功
            // Log.v(this.getClass().getName(),"init Detail: "+pre2);
        }
    }

    /**
     * 判断是段子 或者是 gif 类型
     *
     * @return
     */
    private boolean isJokeOrGif() {
        if (mSourceUrl.contains("jokesDetail.groovy") || mSourceUrl.contains("gifDetail.groovy")) {
            return true;
        }
        return false;
    }

    private String getNewsUrl() {
        String url = "";
        if (mSourceUrl == null || mSourceUrl.contains("ugc.groovy")
                || mSourceUrl.contains("urlContent.groovy")
                || mSourceUrl.contains("interest.content.groovy")
                || mSourceUrl.contains("isextract=1")
                || mSourceUrl.contains("or_id")
                || mSourceUrl.contains("jokesDetail.groovy")
                || mSourceUrl.contains("gifDetail.groovy")) {
            url = mSourceUrl;
        } else {
            if (mKeyword == null) {  //从微信过来的报刊是没有mkeyword的，为了防止编码报错
                mKeyword = "";
            }
            try {
                //图集页面跳转
                if (mSourceUrl.contains("PicNews?")) {
                    url = UrlConfig.HOST_SHARE + "newsdetail/index?category=picnews&keyword="
                            + URLEncoder.encode(mKeyword, "utf-8") + "&srpId=" + mSrpId + "&url="
                            + URLEncoder.encode(mSourceUrl, "utf-8") + "&title=" + URLEncoder.encode(mTitle, "utf-8")
                            + "&source=" + URLEncoder.encode(mItem.getSource(), "utf-8") + "&pubTime=" + mItem.getPubTime();
                } else {
                    url = UrlConfig.HOST_SHARE + "newsdetail/index?keyword="
                            + URLEncoder.encode(mKeyword, "utf-8") + "&srpId=" + (TextUtils.isEmpty(mSrpId) ? "" : mSrpId) + "&url="
                            + URLEncoder.encode(mSourceUrl, "utf-8");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotEmpty(mSourceUrl)) {
            if (mSourceUrl.contains("jokesDetail.groovy")) {
                UpEventAgent.onZSNewsView(DetailModuleActivity.this, mChannel, mSrpId,
                        mTitle, mSourceUrl, mItem != null && StringUtils.isNotEmpty(mItem.getCategory())
                                ? mItem.getCategory() : ConstantsUtils.VJ_DUANZI_SEARCH, "0");
            } else if (mSourceUrl.contains("gifDetail.groovy")) {
                UpEventAgent.onZSNewsView(DetailModuleActivity.this, mChannel, mSrpId,
                        mTitle, mSourceUrl, mItem != null && StringUtils.isNotEmpty(mItem.getCategory())
                                ? mItem.getCategory() : ConstantsUtils.VJ_GIF_SEARCH, "0");
            }
        }

        url += "&token=" + SYUserManager.getInstance().getToken();
        url += ("&appName=") + AppInfoUtils.getAppName(this);

        if (mSourceUrl != null && !mSourceUrl.equals("")) {
            try {
                mParamUrl = URLEncoder.encode(mSourceUrl, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //Log.d("callback1","url-----"+url);
        return url;
    }

    private void initSecondTitle() {
        if (mHsvWidgets != null) {
            return;
        }
        mHsvWidgets = (TabPageIndicator) findViewById(R.id.title_Indicator);
        mHsvAdapter = new TemplateNavigationAdapter(this);
        mHsvWidgets.setViewAdapter(mHsvAdapter);
        mHsvWidgets
                .setOnTabReselectedListener(new TabPageIndicator.OnTabReselectedListener() {
                    public void onTabReselected(int position) {
                        if (mHsvDatas != null) {
                            TemplateWidgetNavData navigationBar = (TemplateWidgetNavData) (mHsvDatas.get(position));
                            if (position == 0) {
                                return;
                            }
                            if (mDetailType == DETAIL_TYPE_NEWS || mDetailType == KEYWORD_CATE_SPECIAL || mDetailType == DETAIL_TYPE_RSS) {
                                startActivityToSrp(navigationBar.getUrl(),
                                        navigationBar.getTitle(),
                                        navigationBar.getMd5());
                            } else {
                                showCircleIndex(DetailModuleActivity.this, mSrpId,
                                        mKeyword, mKeyword, interest_logo,
                                        navigationBar.getTitle(),
                                        navigationBar.getMd5(), 0x3);
                            }
                            UpEventAgent.onDetailWidget(DetailModuleActivity.this);
                            UmengStatisticUtil.onEvent(DetailModuleActivity.this, UmengStatisticEvent.DETAIL_CHANNEL);    //Umeng
                        }
                    }
                });

        mHsvWidgets.setVisibility(View.GONE);
    }

    public void setWidgets(List<TemplateWidgetNavData> navs) {
        initSecondTitle();
        mHsvAdapter.addNavs(navs);
        mHsvWidgets.setVisibility(View.VISIBLE);
        mHsvWidgets.notifyDataSetChanged();
        mHsvWidgets.postInvalidate();
    }

    /**
     * 模板处理接口中二级导航数据
     *
     * @param widgetData
     */
    public void handleSecondList(TemplateWidgetData widgetData) {
        if (widgetData == null) return;
        mSecondSuccess = true;

        if (widgetData.isMenuShow()) {
            if (mHsvDatas == null) {
                mHsvDatas = widgetData.getNavDataList();
                mWeijianMd5 = mHsvDatas.get(0).getMd5();
                if (mHsvDatas.size() > 1 && mHsvDatas.get(0).getTitle().equals("内容")) {
                    setWidgets(mHsvDatas);
                }
            }
        }

        mTouchTitle = false;
        boolean isUrlHaveNo = false;
        if (mSourceUrl == null || mSourceUrl.equals("")) {
            isUrlHaveNo = true;
        }
        if (mCircleType == CIRCLE_TYPE_CIRCLE) {
            if (StringUtils.isEmpty(mUrl)) {
                mUrl.append(UrlConfig.HOST).append("interest/interest.content.groovy?blog_id=")
                        .append(mBlogId).append("&token=").append(SYUserManager.getInstance().getToken());
            }
            mSourceUrl = mUrl.toString(); // 微件头的url
        }
        if (mSignIdSuccess) {
//            getCommentCount();
            getCommentList(false);
        }
//        getCircleMemberInfo(); // 获取当前是否是隐私保护状态
        if (mPushId == 0) {
            setWebViewData(isUrlHaveNo);
        }
    }

    private void setWebViewData(boolean isHaveNo) {
//        getDetailHead();
        switch (mCircleType) {
            case CIRCLE_TYPE_NEWS:
                if (isHaveNo) {
                    initNews();
                }
//                getNewsShortUrl();// 分享短链
                postAdapter.setmUrl(mParamUrl);
                //list_progress.showLoading();
//                getCommentCount();// 获取顶，赞，打赏信息数量
//                getCommentList(false);
                if (StringUtils.isNotEmpty(mPushId) && mPushId != 0
                        && StringUtils.isNotEmpty(mStatisticsJumpPosition)) {
                    UpEventAgent.onNewsPushView(MainApplication.getInstance(),
                            String.valueOf(mPushId), mStatisticsJumpPosition);
                }
                break;
            case CIRCLE_TYPE_CIRCLE:
                initAdminMoreLayout(); // 圈主更多
//                getShortUrl();// 分享短链
                initToTopLayout();// 点击置顶的dialog
                break;
            default:

        }
    }

    public void handleWidgetHead(CWidgetHead data) {
        if (data == null) data = new CWidgetHead(); //保证新闻执行后续逻辑
        try {
            String str = data.getAuthorUserId();
            if (str == null || str.equals("")) {
                mblog_userId = 0l;
            } else {
                mblog_userId = Long.parseLong(str);
            }

        } catch (Exception e) {

        }
        int roletype = data.getUserRole();
        if (roletype == Constant.ROLE_NONE) {     //非圈成员
            btn_subscribe.setImageResource(R.drawable.srp_subscribe_selector);
            isSubcribe = false;
        } else {
            btn_subscribe.setImageResource(R.drawable.srp_no_subscribe_selector);
            isSubcribe = true;
        }

        btn_subscribe.setVisibility(View.VISIBLE);

        if (mDetailType == DETAIL_TYPE_RSS) {
            btn_subscribe.setVisibility(View.GONE);
        } else if (backFlag == 1) {
            btn_subscribe.setVisibility(View.VISIBLE);
            if (roletype == Constant.ROLE_NONE) {
                isSubcribe = false;
                btn_subscribe.setImageResource(R.drawable.srp_subscribe_selector);
            } else {
                isSubcribe = true;
                btn_subscribe.setImageResource(R.drawable.srp_no_subscribe_selector);
            }
        } else {

        }

        if (mDetailType == KEYWORD_CATE_SPECIAL) {
            btn_subscribe.setVisibility(View.GONE);
        }

        nickname = data.getNickName();
        if (mCircleType == CIRCLE_TYPE_NEWS) {
            if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) {
                postAdapter.setNickName("游客");
            } else {
                postAdapter.setNickName(SYUserManager.getInstance().getName());
            }
        } else {
            if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) { //如果是游客显示默认头像和"游客"
                postAdapter.setNickName(StringUtils.isNotEmpty(nickname) ? nickname : getResources().getString(R.string.user_guest));
            } else if (mOptionRoleType == Constant.ROLE_NONE) {  //登录的非圈成员，显示搜悦昵称和搜悦头像
                postAdapter.setNickName(StringUtils.isNotEmpty(nickname) ? nickname : SYUserManager.getInstance().getName());
            } else {
                postAdapter.setNickName(StringUtils.isNotEmpty(nickname) ? nickname : "匿名用户");
            }
        }

        is_bantalk = data.getIsBantalk();
        image = data.getPortraitImage();
        is_private = data.getIsPrivate() == 1;
        AnoyomousUtils.setCurrentPrivateHeadIcon(image, mInterestId + "");
        postAdapter.setImage(image);
        switch (mDetailType) {
            case DETAIL_TYPE_NEWS:
                // 目前没有帖子管理员，所以roletype暂时不认为会是1哦~
                mSubcribeId = data.getSubscribeId();
                mOptionRoleType = Constant.ROLE_NONE;
                break;
            case DETAIL_TYPE_CIRCLE:
                if (mCircleType == CIRCLE_TYPE_NEWS) {
                    if (roletype == Constant.ROLE_VISITOR) {
                        mOptionRoleType = Constant.ROLE_NORMAL;
                    } else {
                        mOptionRoleType = roletype;
                    }
                } else if (mCircleType == CIRCLE_TYPE_CIRCLE) {
                    mIsCanEdit = data.getIsAllowEdit();
                    isToTop = data.getIsTop() == 1;
                    isPrime = data.getIsGood() == 1;
                    if (roletype == Constant.ROLE_VISITOR) {
                        mOptionRoleType = Constant.ROLE_NORMAL;
                    } else {
                        mOptionRoleType = roletype;
                    }
                }
                break;
            default:
        }
        if (mDetailType == DETAIL_TYPE_RSS
                || mOptionRoleType == Constant.ROLE_NONE
                && interestType.equals("1")) {
            img_arrow.setVisibility(View.INVISIBLE);
            mActionBarTitle.setClickable(false);
        } else {
            img_arrow.setVisibility(View.VISIBLE);
        }
        postAdapter.setRole(mOptionRoleType);
        mHeadSuccess = true;
    }

    public void startActivityToSrp(String url, String title, String _md5) {// 点击新闻详情页的导航条跳到srp页
        Intent intent = new Intent(this, SRPActivity.class);
        intent.putExtra("keyword", mKeyword);
        intent.putExtra("srpId", mSrpId);
        intent.putExtra("currentTitle", title);// viewpager跳转到的页
        intent.putExtra("md5", _md5);
        intent.putExtra("opSource", OPSOURCE_FROM_SECONDLIST);   //ZSSDK 来源
        startActivityForResult(intent, 0x3);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void showCircleIndex(Activity context, String srp_id,
                                String keyword, String interest_name, String interest_logo,
                                String _title, String _md5, int requestcode) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("keyword", keyword);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("interest_logo", interest_logo);
        intent.putExtra("title", _title);
        intent.putExtra("md5", _md5);
        context.startActivityForResult(intent, requestcode);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void initData() {
        utype = SYUserManager.getInstance().getUserType();
        suberDao = new SuberDaoImp();
        pairScrollView.setmTouchEnable(false);
        Intent intent = getIntent();
        mItem = (DetailItem) intent.getSerializableExtra("SearchResultItem");// 这里16毫秒
        getDataFromDetailItem();
        mShowComment = mItem.getSkip();

        if (StringUtils.isNotEmpty(mKeyword)
                && mKeyword.equals(ConstantsUtils.MEGAGAME_SEARCH_KEYWORD)) {
            backFlag = 1;
        } else {
            backFlag = 0;
        }

        if (mShareImageUrls != null && mShareImageUrls.size() > 0) {
            imageUrl = mShareImageUrls.get(0);
        }
        broadcast_tag_id = intent.getStringExtra("broadcast_tag_id");
        broadcast_tag_name = intent.getStringExtra("broadcast_tag_name");
        isNew = getIntent().getBooleanExtra("isNew", false);

        hasChanged = isNew;
        mInterestId = getIntent().getLongExtra("interest_id", -1);
        if (mBlogId > 0) {
            mCircleType = CIRCLE_TYPE_CIRCLE;
        } else {
            mCircleType = CIRCLE_TYPE_NEWS;
        }

        postsList = new ArrayList<CommentsForCircleAndNews>();
        postsListHot = new ArrayList<CommentsForCircleAndNews>();
        postAdapter = new ListViewPostsAdapterNew(this, postsList, postsListHot,
                mblog_userId, mInterestId, DEVICE_COME_FROM);// 创建将近耗时10毫秒
        // listView.setAdapter(adapter);
//        setAdapterData();
//        adapter.setmDeatilType(mDetailType);
        postAdapter.setCircleType(mCircleType);
//        adapter.setDetailHttp(mVolleyHttp);
//        http = new Http(this);
        imageLoader = ImageLoader.getInstance();

        getImage();
        // 预加载Emoji表情
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiPattern.getInstace().getFileText(DetailModuleActivity.this);
            }
        }).start();

        mActionBarTitle.setText(mKeyword);// 先设置title，二级列表回来再改
        mSecondSuccess = false;
        mWebSuccess = false;
        mHasCommentDismiss = true;

        mDoneKvo = new CVariableKVO(3, new CVariableKVO.KVOCallback() {
            @Override
            public void doCallback() {
                //三个条件都满足才能评论1、微贱头回来，2、内容回来，3、评论列表回来
                if (mShowComment == DetailItem.SKIP_TO_COMMENT) {
                    mShowComment = 0;
                    follow_post_layout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            showComment();
                            //评论等三件事都回来了就跳到第二个页面但是不弹键盘和评论框了
                            pairScrollView.scrollToSecondView();
                        }
                    }, 300);
                }
                follow_post_layout.setEnabled(true);
                mHaveNoComment.setEnabled(true);
            }
        });

//        getSecondList();
        bindListener();//

        long cur7 = System.currentTimeMillis();
        switch (mCircleType) {
            case CIRCLE_TYPE_NEWS:
                initNews();// 如果是新闻这个方法耗时将近40毫秒
                break;
            case CIRCLE_TYPE_CIRCLE:
                initCircle();
                break;
            default:

        }
        // Log.v(this.getClass().getName(),"init Detail: "+" mcir  "+mCircleType+"   "+pre1+"   "+pre3+"   "+pre8);

        mCircleFootData = new CVariableKVO(2, new CVariableKVO.KVOCallback() {
            @Override
            public void doCallback() {
                //两个条件都满足才请求模板底部数据
                DetailCircleFootRequest request = new DetailCircleFootRequest(HttpCommon.DETAIL_CIRCLE_FOOT_REQUEST, DetailModuleActivity.this);
                request.setParams(String.valueOf(mBlogId), mSrpId, mParamUrl);  //相关推荐
                CMainHttp.getInstance().doRequest(request);
                mWebView.loadUrl("javascript:showMoreOpera("
                        + showAdminMoreOper + ")");
            }
        });
        mNewsFootData = new CVariableKVO(2, new CVariableKVO.KVOCallback() {
            @Override
            public void doCallback() {
                //两个条件都满足才请求模板底部数据
                DetailNewsFootRequest request = new DetailNewsFootRequest(HttpCommon.DETAIL_NEWS_FOOT_REQUEST, DetailModuleActivity.this);
                request.setParams(mKeyword, mSrpId, String.valueOf(mPushId), mSourceUrl);  //相关推荐
                CMainHttp.getInstance().doRequest(request);
            }
        });
    }

    private void getDataFromDetailItem() {
        mKeyword = mItem.getKeyword();
        mSrpId = mItem.getSrpId();
        mSourceUrl = mItem.getUrl();
        mBlogId = mItem.getBlogId();
        mPushId = mItem.getPushId();
        mStatisticsJumpPosition = mItem.getStatisticsJumpPosition();

        mTitle = mItem.getTitle();
        mContent = mItem.getDescription();
        mShareImageUrls = mItem.getImages();
        mChannel = mItem.getChannel();

    }


    // 初始化置顶dialog
    private void initToTopLayout() {
        LayoutInflater mLayoutInflater = getLayoutInflater();
        toTopView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.circle_totop_menu, null, true);
//        AQuery aq = new AQuery(toTopView);
        findViewAndSetListener(toTopView, R.id.textView_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
            }
        });

        findViewAndSetListener(toTopView, R.id.textView_totop_forver, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
//                http.getToTop(mBlogId, SYUserManager.getInstance().getToken(), 3);
                setBlogToTop(mBlogId, SYUserManager.getInstance().getToken(), 3);
            }
        });

        findViewAndSetListener(toTopView, R.id.textView_totop_forver1, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
//                http.getToTop(mBlogId, SYUserManager.getInstance().getToken(),1000);
                setBlogToTop(mBlogId, SYUserManager.getInstance().getToken(), 1000);
            }
        });

        findViewAndSetListener(toTopView, R.id.textView_totop_oneday, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
//                http.getToTop(mBlogId, SYUserManager.getInstance().getToken(),1);
                setBlogToTop(mBlogId, SYUserManager.getInstance().getToken(), 1);
            }
        });

        findViewAndSetListener(toTopView, R.id.textView_totop_oneweek, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
//                http.getToTop(mBlogId, SYUserManager.getInstance().getToken(),2);
                setBlogToTop(mBlogId, SYUserManager.getInstance().getToken(), 2);
            }
        });
    }

    private void findViewAndSetListener(View rootView, int id, OnClickListener listener) {
        View view = rootView.findViewById(id);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    private void findViewAndSetListener(int id, OnClickListener listener) {
        View view = findView(id);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    private void setBlogToTop(long postsId, String token, int toTopDays) {
        MblogTopReq req = new MblogTopReq(HttpCommon.CIRLCE_BLOG_TOP_ID, this);
        req.setParams(postsId, token, toTopDays);
        CMainHttp.getInstance().doRequest(req);
    }

    private void setAdminMoreViable(boolean isTop, boolean isPrime,
                                    int mOptionRoleType) {
        if (isPrime) {
            ll_admin_cancle_prime.setVisibility(View.VISIBLE);
            ll_admin_prime.setVisibility(View.GONE);
        } else {
            ll_admin_cancle_prime.setVisibility(View.GONE);
            ll_admin_prime.setVisibility(View.VISIBLE);
        }

        if (isTop) {
            ll_admin_top.setVisibility(View.GONE);
            ll_admin_cancle_top.setVisibility(View.VISIBLE);
        } else {
            ll_admin_top.setVisibility(View.VISIBLE);
            ll_admin_cancle_top.setVisibility(View.GONE);
        }

        if (mOptionRoleType == Constant.ROLE_SUB_ADMIN) {
            ll_admin_push.setVisibility(View.GONE);
            ll_admin_cancle_push.setVisibility(View.VISIBLE);
        } else if (mOptionRoleType == Constant.ROLE_ADMIN) {
            ll_admin_push.setVisibility(View.VISIBLE);
            ll_admin_cancle_push.setVisibility(View.GONE);
        } else {
            ll_admin_push.setVisibility(View.GONE);
            ll_admin_cancle_push.setVisibility(View.GONE);
            ll_admin_cancle_prime.setVisibility(View.GONE);
            ll_admin_prime.setVisibility(View.GONE);
            ll_admin_top.setVisibility(View.GONE);
            ll_admin_cancle_top.setVisibility(View.GONE);
        }

    }

    private void initMenuLayout() {
//		if (menuView != null) {
//			return;
//		}
        LayoutInflater mLayoutInflater = getLayoutInflater();
        menuView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.detail_menu_dialog, null, false);
        imgSwitch = (ImageButton) menuView.findViewById(R.id.img_only_wifi);
        if (SYSharedPreferences.getInstance().getLoadWifi(DetailModuleActivity.this)) {
            imgSwitch.setImageResource(R.drawable.detail_switch_open);
        } else {
            imgSwitch.setImageResource(R.drawable.detail_switch_close);
        }
        seekBar = (SeekBar) menuView.findViewById(R.id.seek_bar_light);
        seekBar.setMax(255);
        tvBig = (TextView) menuView.findViewById(R.id.tv_detail_font_big);
        tvMiddle = (TextView) menuView.findViewById(R.id.tv_detail_font_middle);
        tvSmall = (TextView) menuView.findViewById(R.id.tv_detail_font_small);
        float fontSize = SYSharedPreferences.getInstance().loadResFont(
                DetailModuleActivity.this);
        if (fontSize == SYSharedPreferences.FONT_VALUE_BIG_SIZE) {
            setBigFont();
        } else if (fontSize == SYSharedPreferences.FONT_VALUE_MIDDLE_SIZE) {
            setMiddleFont();
        } else if (fontSize == SYSharedPreferences.FONT_VALUE_SMALL_SIZE) {
            setSmallFont();
        }
        LinearLayout llDontInterest = (LinearLayout) menuView
                .findViewById(R.id.ll_detail_dont_interest);
        LinearLayout llJuBao = (LinearLayout) menuView
                .findViewById(R.id.ll_detail_jubao);
        Button btnOk = (Button) menuView.findViewById(R.id.btn_detail_ok);

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuViewDialog.dismiss();
            }
        });

        imgSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SYSharedPreferences.getInstance().getLoadWifi(
                        DetailModuleActivity.this)) {
                    SYSharedPreferences.getInstance().putLoadWifi(
                            DetailModuleActivity.this, false);
                    imgSwitch.setImageResource(R.drawable.detail_switch_close);
                    ((MainApplication) getApplication()).initImageLoader();
                } else {
                    SYSharedPreferences.getInstance().putLoadWifi(
                            DetailModuleActivity.this, true);
                    imgSwitch.setImageResource(R.drawable.detail_switch_open);
                    ((MainApplication) getApplication()).initImageLoader();
                }
            }
        });

        tvBig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setBigFont();
                SYSharedPreferences.getInstance().putFontString(
                        SYSharedPreferences.getInstance().FONT_VALUE_BIG,
                        DetailModuleActivity.this);
                initFontSize();
            }
        });

        tvMiddle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMiddleFont();
                SYSharedPreferences.getInstance().putFontString(
                        SYSharedPreferences.getInstance().FONT_VALUE_MIDDLE,
                        DetailModuleActivity.this);
                initFontSize();
            }
        });

        tvSmall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSmallFont();
                SYSharedPreferences.getInstance().putFontString(
                        SYSharedPreferences.getInstance().FONT_VALUE_SMALL,
                        DetailModuleActivity.this);
                initFontSize();
            }
        });

        llJuBao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mVolleyHttp.home_complaintint(CDetailHttp.HTTP_COMPLAIN, SYUserManager
                                .getInstance().getToken(), 0, "", mSrpId, mKeyword,
                        DetailModuleActivity.this);
                menuViewDialog.dismiss();
            }
        });

        llDontInterest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCircleType == CIRCLE_TYPE_CIRCLE) {
                    DisLikeRequest request = new DisLikeRequest(
                            HttpCommon.HOME_LIST_DISLIKE, DetailModuleActivity.this);
                    request.setParams(SYUserManager.getInstance().getToken(), "", mBlogId
                            + "", mInterestId + "", "", mItem, mChannel, mItem != null && StringUtils.isNotEmpty(mItem.getCategory()) ? mItem.getCategory() : ConstantsUtils.VJ_NEW_SEARCH);
                    mMainHttp.doRequest(request);

//                    mMainHttp.doDisLike(CMainHttp.HTTP_REQUEST_HOMELIST_DISLIKE,
//                            SYUserManager.getInstance().getToken(), "", mBlogId
//                                    + "", mInterestId + "", "", DetailModuleActivity.this,
//                            mItem,mChannel,mItem!=null&&StringUtils.isNotEmpty(mItem.getCategory())?mItem.getCategory():ConstantsUtils.VJ_NEW_SEARCH);
                } else {
                    DisLikeRequest request = new DisLikeRequest(
                            HttpCommon.HOME_LIST_DISLIKE, DetailModuleActivity.this);
                    request.setParams(SYUserManager.getInstance().getToken(), mUrl.toString(), 0
                            + "", 0 + "", "", mItem, mChannel, mItem != null && StringUtils.isNotEmpty(mItem.getCategory()) ? mItem.getCategory() : ConstantsUtils.VJ_NEW_SEARCH);
                    mMainHttp.doRequest(request);

//                    mMainHttp.doDisLike(CMainHttp.HTTP_REQUEST_HOMELIST_DISLIKE,
//                            SYUserManager.getInstance().getToken(), mUrl.toString(), 0
//                                    + "", 0 + "", "", DetailModuleActivity.this,
//                            mItem,mChannel,mItem!=null&& StringUtils.isNotEmpty(mItem.getCategory())?mItem.getCategory():ConstantsUtils.VJ_NEW_SEARCH);
                }

                menuViewDialog.dismiss();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isAutoBright()) {
                    stopAutoBright();
                }
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int curProgress = seekBar.getProgress();// 得到当前进度值
                // 当进度小于10时，设置成10，防止太黑看不见的情况。
                if (curProgress < 10) {
                    curProgress = 10;
                }
                // 根据当前进度改变屏幕亮度
                if (!isAutoBright()) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, curProgress);
                    curProgress = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, -1);
                    BrightnessUtil.setBrightness(DetailModuleActivity.this, curProgress);
                    BrightnessUtil.saveBrightness(DetailModuleActivity.this, curProgress);
                }

            }
        });
        if (isAutoBright()) {
            seekBar.setProgress(75);  //如果是自动亮度，就设置为亮度的30%
        } else {
            seekBar.setProgress(brightness);
        }

    }

    private void setBigFont() {
        tvBig.setTextColor(getResources().getColor(R.color.white));
        tvBig.setBackgroundResource(R.drawable.font_big_on);
        tvMiddle.setTextColor(getResources().getColor(R.color.color_srp_title));
        tvMiddle.setBackgroundResource(R.drawable.font_middle_off);
        tvSmall.setTextColor(getResources().getColor(R.color.color_srp_title));
        tvSmall.setBackgroundResource(R.drawable.font_small_off);
    }

    private void setMiddleFont() {
        tvBig.setTextColor(getResources().getColor(R.color.color_srp_title));
        tvBig.setBackgroundResource(R.drawable.font_big_off);
        tvMiddle.setTextColor(getResources().getColor(R.color.white));
        tvMiddle.setBackgroundResource(R.drawable.font_middle_on);
        tvSmall.setTextColor(getResources().getColor(R.color.color_srp_title));
        tvSmall.setBackgroundResource(R.drawable.font_small_off);
    }

    private void setSmallFont() {
        tvBig.setTextColor(getResources().getColor(R.color.color_srp_title));
        tvBig.setBackgroundResource(R.drawable.font_big_off);
        tvMiddle.setTextColor(getResources().getColor(R.color.color_srp_title));
        tvMiddle.setBackgroundResource(R.drawable.font_middle_off);
        tvSmall.setTextColor(getResources().getColor(R.color.white));
        tvSmall.setBackgroundResource(R.drawable.font_small_on);
    }

    /**
     * 圈主工具
     */
    private void initAdminMoreLayout() {
        LayoutInflater mLayoutInflater = getLayoutInflater();
        adminMoreView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.detail_admin_more_dialog, null, false);
        LinearLayout ll_admin_edit = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_edit);
        LinearLayout ll_admin_delelte = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_delete);
        ll_admin_top = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_top);
        ll_admin_cancle_top = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_cancle_top);
        ll_admin_prime = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_prime);
        ll_admin_cancle_prime = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_cancle_prime);
        ll_admin_push = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_push);
        ll_admin_cancle_push = (LinearLayout) adminMoreView
                .findViewById(R.id.ll_admin_cancle_push);
        Button btnCancle = (Button) adminMoreView
                .findViewById(R.id.admin_cancle);

        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdminMore.dismiss();
            }
        });

        ll_admin_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCanEdit == EDIT_IS_ALLOW) {
                    // 获取要编辑的内容
                    if (mTouchShareOrEdit) {
                        UIHelper.showPublish(DetailModuleActivity.this, mainPosts,
                                mInterestId, mSrpId, mKeyword,
                                PublishActivity.PUBLISH_TYPE_M_EDIT, nickname);
                    }
                } else {
                    UIHelper.ToastMessage(DetailModuleActivity.this, "该帖子不支持编辑");
                }
                showDialogAdminMore.dismiss();
            }

        });

        ll_admin_delelte.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePosts();
                showDialogAdminMore.dismiss();
            }
        });

        /**
         * 置顶
         */
        ll_admin_top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showZhiDingMenu();
                showDialogAdminMore.dismiss();
            }
        });

        /**
         * 取消置顶
         */
        ll_admin_cancle_top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                http.cancleTotop(mBlogId, SYUserManager.getInstance() .getToken());
                cancelBlogTop(mBlogId, SYUserManager.getInstance().getToken());
                showDialogAdminMore.dismiss();

            }
        });
        /**
         * 加精
         */
        ll_admin_prime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                http.getPrime(mBlogId, SYUserManager.getInstance().getToken());
                setBlogPrime(mBlogId, SYUserManager.getInstance().getToken());
                showDialogAdminMore.dismiss();
            }
        });

        /**
         * 取消加精
         */
        ll_admin_cancle_prime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                http.canclePrime(mBlogId, SYUserManager.getInstance().getToken());
                cancelBlogPrime(mBlogId, SYUserManager.getInstance().getToken());
                showDialogAdminMore.dismiss();

            }
        });

        /**
         * 推送
         */
        ll_admin_push.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //我也不知道参数里面的2是咋来的，别问我...
//                DetailBlogPushRequest bp = new DetailBlogPushRequest(HttpCommon.CIRCLE_DETAILBLOGPUSH_REQUESTID,DetailModuleActivity.this);
//                bp.setParams(mInterestId,mBlogId,SYUserManager.getInstance()
//                        .getToken(),2);
//                mMainHttp.doRequest(bp);
                DetailBlogPushRequest.send(HttpCommon.CIRCLE_DETAILBLOGPUSH_REQUESTID, DetailModuleActivity.this
                        , mInterestId, mBlogId, SYUserManager.getInstance()
                                .getToken(), 2);
//                http.tuiSong(mInterestId, mBlogId, SYUserManager.getInstance()
//                        .getToken(), 2);
                showDialogAdminMore.dismiss();
            }
        });

    }

    /**
     * 帖子加精
     *
     * @param postsId
     * @param token
     */
    private void setBlogPrime(long postsId, String token) {
        MblogPrimeReq req = new MblogPrimeReq(HttpCommon.CIRLCE_BLOG_PRIME_ID, this);
        req.setParams(postsId, token);
        CMainHttp.getInstance().doRequest(req);
    }

    /**
     * 帖子取消加精
     *
     * @param postsId
     * @param token
     */
    private void cancelBlogPrime(long postsId, String token) {
        MblogCanclePrimeReq req = new MblogCanclePrimeReq(HttpCommon.CIRLCE_BLOG_PRIME_CANCLE_ID, this);
        req.setParams(postsId, token);
        CMainHttp.getInstance().doRequest(req);
    }

    /**
     * 帖子取消置顶
     *
     * @param postsId
     * @param token
     */
    private void cancelBlogTop(long postsId, String token) {
        MblogCancleTopReq req = new MblogCancleTopReq(HttpCommon.CIRLCE_BLOF_TOP_CANCLE_ID, this);
        req.setParams(postsId, token);
        CMainHttp.getInstance().doRequest(req);
    }

    /**
     * 帖子删除
     *
     * @param postsId
     * @param token
     */
    private void deleteBlog(long postsId, String token) {
        MblogDeleteReq req = new MblogDeleteReq(HttpCommon.CIRLCE_BLOG_DELETE_ID, this);
        req.setParams(postsId, token);
        CMainHttp.getInstance().doRequest(req);
    }


    // 初始化跟帖控件添加图片View
    private void initAddImgLayout() {
        if (addIMgView != null) {
            return;
        }
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        addIMgView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.circle_follow_add_img_menu, null, false);
        TextView textView_xiangce = (TextView) addIMgView
                .findViewById(R.id.textView_xiangce);
        TextView textView_photo = (TextView) addIMgView
                .findViewById(R.id.textView_photo);
        TextView textView_cancel = (TextView) addIMgView
                .findViewById(R.id.textView_cancel);
        textView_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
            }
        });
        textView_xiangce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                IntentUtil.jumpImgGroup(DetailModuleActivity.this,
                        circleFollowDialog.getImgLen());
            }
        });
        textView_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                jumpTakePhoto();
            }
        });
    }

    // 初始化页面主View
    @SuppressLint({"NewApi", "SetJavaScriptEnabled", "JavascriptInterface"})
    @android.webkit.JavascriptInterface
    private void initView() {
        progress = new ProgressBarHelper(this, null);
        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    mWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (null != progress) {
                                progress.showNetError();
                            }
                        }
                    }, 500);
                    return;
                }
                if (mSecondSuccess) {
                    setWebViewData(true);
                } else {
//                    getSecondList();
                    mWebView.loadUrl(mUrl.toString());  //当正文请求失败，走此逻辑！！
                    mWebSuccess = true;// 预计成功
                    switch (mCircleType) {
                        case CIRCLE_TYPE_NEWS:
                            initNews();
                            break;
                        case CIRCLE_TYPE_CIRCLE:
                            initCircle();
                            break;
                        default:

                    }
                }
            }
        });
        listLoading = findView(R.id.list_loading);
        listLoading.setBackgroundColor(getResources().getColor(R.color.transparent));
        list_progress = new ProgressBarHelper(this, listLoading);
        list_progress
                .setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                    @Override
                    public void clickRefresh() {
                        list_progress.showLoading();
                        if (mSecondSuccess) {
//                            getCommentCount();
                            getCommentList(false);
                        }
                    }
                });

        mHeaderView = findView(R.id.circle_bar_layout);

        mActionBarTitle = (TextView) findViewById(R.id.activity_bar_title);
        mActionBarTitle.setOnClickListener(this);
        btn_option = (ImageButton) findViewById(R.id.btn_option);
        img_arrow = findView(R.id.img_arrow);
        img_arrow.setOnClickListener(this);
        titlebar_read_toac_text = (TextView) findViewById(R.id.titlebar_read_toac_text);
        mFirstView = findView(R.id.pair_first);
        mWebView = new CustomWebView(DetailModuleActivity.this); //
        mFirstView.addView(mWebView);
        setWebViewClient();
        if (ConfigApi.isUseWebViewImageBlock) {
            mWebView.getSettings().setRenderPriority(
                    WebSettings.RenderPriority.HIGH);// 提高渲染优先级
            mWebView.getSettings().setBlockNetworkImage(true);// 设置图片最后加载渲染
        }

        mWebView.setmScrollChanged(this);
        mWebView.setmMeasureChanged(this);
        mWebView.getSettings().setUseWideViewPort(false);
        mWebView.setImagesListener(this);
        mWebView.setOnJSClickListener(this);
        mWebView.setGotoSrpListener(this);

        mHaveNoComment = findView(R.id.detail_have_no_comment);
        mHaveNoComment.setOnClickListener(this);
        mHaveNoComment.setVisibility(View.INVISIBLE);
        mHaveNoComment.setEnabled(false);
        collect_imagebutton = (ImageButton) this
                .findViewById(R.id.collect_imagebutton);
        ding_layout = (RelativeLayout) this.findViewById(R.id.ding_layout);
        follow_post_layout = (RelativeLayout) this
                .findViewById(R.id.follow_post_layout);
        ding_count = (TextView) this.findViewById(R.id.ding_count);
        follow_post_count = (TextView) this
                .findViewById(R.id.follow_post_count);
        ding_imagebutton = (ImageButton) this
                .findViewById(R.id.ding_imagebutton);
        share_imagebutton = (ImageButton) this
                .findViewById(R.id.share_imagebutton);
        circle_bottom_bar = (LinearLayout) this
                .findViewById(R.id.ll_circle_post_bottom_bar);

        share_imagebutton.setFocusable(false);
        follow_post_layout.setEnabled(false);
        share_imagebutton.setEnabled(false);

        lvPost = (ListView) findViewById(R.id.listView);
        goBack = (ImageButton) findViewById(R.id.goBack);
        btn_subscribe = (ImageButton) findViewById(R.id.btn_detail_subscribe);
        btn_subscribe.setOnClickListener(this);

        // initCommentUpOrDown();//这个东西归结为一progress个方法，放到列表数据回来时再初始化

        pairScrollView = (PairScrollView) findViewById(R.id.pair_scroll);
        pairScrollView.setVisibility(View.INVISIBLE);
        mPairSecond = findView(R.id.pair_second);
        mPairSecond.init(this);


//		initBrightness();
        // initMenuLayout();//这个东东放到点击更多按钮的时候再初始化,这个方法占用30-40毫秒
    }

    private void initCommentUpOrDown() {
        if (mListHeaderView != null) {
            return;
        }
        mListHeaderView = View
                .inflate(this, R.layout.detail_comment_head, null);
        mHeadUpIcon = (ImageView) mListHeaderView
                .findViewById(R.id.detail_up_icon);
        mHeadDownIcon = (ImageView) mListHeaderView
                .findViewById(R.id.detail_down_icon);
        mHeadUpCount = (TextView) mListHeaderView
                .findViewById(R.id.detail_up_count);
        mHeadDownCount = (TextView) mListHeaderView
                .findViewById(R.id.detail_down_count);
        mHeadPayCount = (TextView) mListHeaderView
                .findViewById(R.id.detail_pay_count);

        mHeadLayoutUp = (LinearLayout) mListHeaderView
                .findViewById(R.id.detail_up);
        mHeadLayoutDown = (LinearLayout) mListHeaderView
                .findViewById(R.id.detail_down);
        mHeadLayoutPay = (LinearLayout) mListHeaderView
                .findViewById(R.id.detail_pay);
        mHeadUpCount.setText("");
        mHeadDownCount.setText("");
        mHeadPayCount.setText("");
        lvPost.addHeaderView(mListHeaderView);
        if (mCircleType == CIRCLE_TYPE_NEWS) {
            mHeadLayoutPay.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeadLayoutDown
                    .getLayoutParams();
            params.rightMargin = DeviceUtil.dip2px(getBaseContext(), 15);
        }
        lvPost.setAdapter(postAdapter);
    }

    @Override
    public void gotoSRP(String keyword, String srpId) {
        if (!StringUtils.isEmpty(keyword) && !StringUtils.isEmpty(srpId)) {
            Intent intent = new Intent();
            intent.setClass(this, SRPActivity.class);
            intent.putExtra("keyword", keyword);
            intent.putExtra("srpId", srpId);
            startActivity(intent);
            this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    public class Update {

        private Handler handler;

        public Update(Handler handler, WebView webView) {
            this.handler = handler;
        }

        public void init() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:changeFontSize("
                            + DeviceInfo.getSize() + ")");
                }
            });
        }
    }

    private void initFontSize() {
        mWebView.initFontSize(DeviceInfo.getSize());
        if (postAdapter != null)
            postAdapter.notifyDataSetChanged();
//            setAdapterData();
    }

    private void setDetailTitle() {
        switch (backFlag) {
            case 1:
                mHeaderView
                        .setBackgroundResource(R.drawable.zh_ac_all_title_bg_red);
                titlebar_read_toac_text.setVisibility(View.VISIBLE);
                titlebar_read_toac_text
                        .setBackgroundResource(R.drawable.zh_ac_title_super_image_small);
                mActionBarTitle.setVisibility(View.GONE);
                img_arrow.setVisibility(View.GONE);
                btn_option.setVisibility(View.GONE);
                goBack.setImageResource(R.drawable.megagame_btn_goback2);
                break;

            default:
                mHeaderView.setBackgroundResource(R.color.all_titlebar_bg_color);
                titlebar_read_toac_text.setVisibility(View.GONE);
                mActionBarTitle.setVisibility(View.VISIBLE);
                img_arrow.setVisibility(View.GONE);
                goBack.setImageResource(R.drawable.goback_button_selector);
                break;
        }
    }

    private View getFootView() {
        if (footerView == null) {
            footerView = getLayoutInflater().inflate(
                    R.layout.ent_refresh_footer, null);
            footerView.setBackgroundColor(0xffffffff);
        }
        return footerView;
    }

    private void bindListener() {
        btn_option.setOnClickListener(this);
        collect_imagebutton.setOnClickListener(this);
        ding_layout.setOnClickListener(this);
        follow_post_layout.setOnClickListener(this);
        share_imagebutton.setOnClickListener(this);

        postAdapter.setChangeListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                if (postAdapter.getCount() == 0) {
                    mHaveNoComment.setVisibility(View.VISIBLE);
                    lvPost.removeFooterView(footerView);
                }
                follow_post_count.setText(Integer.parseInt(follow_post_count
                        .getText().toString()) - 1 + ""); // 帖子删除的时候，跟帖数减一
            }
        });
        lvPost.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && visibleLast == postAdapter.getCount() && needLoad) {
                    needLoad = false;
                    getCommentList(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (lvPost.getFooterViewsCount() == 0) {
                    visibleLast = firstVisibleItem + visibleItemCount - 1;
                } else {
                    visibleLast = firstVisibleItem + visibleItemCount - 2;
                }

            }
        });
    }

//    // 获取点赞数打赏数等信息
//    private void getCommentCount() {  //整合到模板接口
//        initCommentUpOrDown();
//        CommentCountRequest.send(CDetailHttp.HTTP_GET_COMMENT_COUNT, mParamUrl + "",
//                DEVICE_COME_FROM + "", this);
//    }


    /**
     * 打赏信息接口返回处理
     *
     * @param commentsInfo
     */
    private void handleCommentCount(TemplateCommentsInfo commentsInfo) {
        if (commentsInfo == null) return;
        ding_layout.setClickable(true);
        mHeadLayoutUp.setEnabled(true);
        mHeadLayoutDown.setEnabled(true);

        mUpCount = commentsInfo.getUpCount();
        mDownCount = commentsInfo.getDownCount();
        mCommentCount = commentsInfo.getCommentsCount();
        mHasUp = commentsInfo.isHasUp();
        mHasDown = commentsInfo.isHasDown();
        mHasFavorited = commentsInfo.isHasFavorited();
        mZsbCount = commentsInfo.getZsbCount();

        mHeadPayCount.setText(mZsbCount + "");
        follow_post_count.setText(mCommentCount + "");
        ding_count.setText(mUpCount + "");
        mHeadUpCount.setText(mUpCount + "");
        mHeadDownCount.setText(mDownCount + "");
        mHeadLayoutPay.setEnabled(true);
        if (mHasFavorited) {
            collect_imagebutton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.circle_collect_unnormal));
        } else {
            collect_imagebutton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.circle_collect_normal));
        }
        if (mHasUp) {
            mHeadUpIcon.setImageResource(R.drawable.detail_up_red);
            mHeadUpCount.setTextColor(getResources().getColor(
                    R.color.detail_red));
            ding_imagebutton.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_up_unnormal));
        }
        if (mHasDown) {
            mHeadDownIcon.setImageResource(R.drawable.detail_down_red);
            mHeadDownCount.setTextColor(getResources().getColor(
                    R.color.detail_red));
        }
        if (!mHasDown && !mHasUp) {
            ding_imagebutton.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_up_normal));
            mHeadUpIcon.setImageResource(R.drawable.detail_up_blue);
            mHeadUpCount.setText(mUpCount + "");
            mHeadDownIcon.setImageResource(R.drawable.detail_down_blue);
            mHeadDownCount.setText(mDownCount + "");
            mHeadUpCount.setTextColor(getResources().getColor(
                    R.color.detail_blue));
            mHeadDownCount.setTextColor(getResources().getColor(
                    R.color.detail_blue));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mSecondSuccess) {
//            getDetailHead();
//        }
        if (isRefreshData) {
            postsList.clear();
            last_sort_num = 0;
            pno = 1;
            visibleLast = 0;
            needLoad = false;
            isLoadAll = false;
            if (mHsvWidgets != null) {
                mHsvWidgets.setCurrentItem(0);
            }
//            getCommentCount();
            getCommentList(false);
        }
        if (!isFirstComing) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("appname", ContextUtil.getAppId(this));
            jsonObject.addProperty("token", SYUserManager.getInstance().getToken());
            if (mInterestId > 0) {
                jsonObject.addProperty("isStatus", 0);
            } else {
                jsonObject.addProperty("isStatus", 1);
            }
            mWebView.loadUrl("javascript:handlerInterestSubEvent(" + jsonObject + ")");
        }
        isFirstComing = false;
        isRefreshData = false;

        changeSubState(subStatusFromJSON); //更新订阅状态
        mWebView.loadUrl("javascript:uiResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    // 对落地页进行短链处理，避免分享出去被截断
//    private void getShortUrl() {
//        oriUrl = UrlConfig.shareInterestBlog + mBlogId + "&interestId="
//                + mInterestId + CommonStringsApi.getUrlAppendIgId();// 圈子分享的落地页
////        http.shortURL(oriUrl);
//        ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
//        req.setParams(oriUrl);
//        CMainHttp.getInstance().doRequest(req);
//    }

    /**
     * 图集，新闻，gif，段子的短链获取都走新闻类型
     */
    private void getNewsShortUrl() {
//        http.shortURL(mUrl.toString());
        //预先设置分享的url，如果短链获取失败了就用这个预先设置的，成功了会重新赋值
        if (mSourceUrl.contains("jokesDetail.groovy")) {
            blogShareUrl = UrlConfig.HOST_SHARE_JOKE_URL + "appName=" + ContextUtil.getAppId(MainApplication.getInstance()) +
                    "&userId=" + SYUserManager.getInstance().getUserId() +
                    "&url=" + mSourceUrl;
        } else if (mSourceUrl.contains("gifDetail.groovy")) {
            blogShareUrl = UrlConfig.HOST_SHARE_GIF_URL + "appName=" + ContextUtil.getAppId(MainApplication.getInstance()) +
                    "&userId=" + SYUserManager.getInstance().getUserId() +
                    "&url=" + mSourceUrl;
        }
        ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
        req.setParams(mUrl.toString());
        CMainHttp.getInstance().doRequest(req);
    }


    public void shortURLSuccess(String url) {
        blogShareUrl = url;
    }

    private void finishWithDelete() {
        Intent data = new Intent();
        result_type = 3;
        data.setAction(PublishActivity.ACTION_NEW_POST);
        CircleResponseResultItem item_delete = new CircleResponseResultItem();
        item_delete.setBlog_id(mBlogId);
        data.putExtra("resultType", result_type);
        data.putExtra(PublishActivity.ACTION_KEY_RESPONSEITEM, item_delete);
        sendBroadcast(data);
        finish();
    }

    public void tuiSongSuccess(HttpJsonResponse res) {
        int result = res.getBody().get("result").getAsInt();
        UIHelper.ToastMessage(this,
                Constant.get_POST_TUISONG_STATE_Label(result));
    }

    // 删除帖子
    private void deletePosts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                DetailModuleActivity.this);
        builder.setMessage("确定删除吗？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 确定删除的时候
                        dialog.dismiss();
                        deleteBlog(mBlogId, SYUserManager.getInstance().getToken());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 取消删除的时候
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void showZhiDingMenu() {
        showDialog = showAlert(this, toTopView, Gravity.BOTTOM);
    }

    private void showAdminMoreMenu() {
        showDialogAdminMore = showAdminAlert(this, adminMoreView);
    }

    private void showAddImgMenu() {
        initAddImgLayout();
        showDialogAddImg = showAlert(this, addIMgView, Gravity.BOTTOM);
    }

    private void showMenu() {
        getBrightness();
        initMenuLayout();
        menuViewDialog = showMenuAlert(this, menuView, Gravity.BOTTOM);
    }

    private Dialog showMenuAlert(final Context context, ViewGroup content, int gravity) {
        if (content.getParent() != null) {
            ((ViewGroup) content.getParent()).removeView(content);
        }
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        dlg.getWindow().setWindowAnimations(R.style.menu_anim_style);
        Window w = dlg.getWindow();
        LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = -1000;   //cMakeBottom
        lp.gravity = gravity;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(content);
        dlg.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        dlg.show();
        return dlg;
    }

    private Dialog showAdminAlert(final Context context, ViewGroup content) {
        if (content.getParent() != null) {
            ((ViewGroup) content.getParent()).removeView(content);
        }
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        dlg.getWindow().setWindowAnimations(android.R.anim.fade_in);
        Window w = dlg.getWindow();
        LayoutParams lp = w.getAttributes();
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(content);
        dlg.getWindow().setLayout(
                Utils.getScreenWidth(DetailModuleActivity.this) * 9 / 10,
                LayoutParams.WRAP_CONTENT);
        dlg.show();
        return dlg;
    }

    public static Dialog showAlert(final Context context, ViewGroup content,
                                   int gravity) {
        if (content.getParent() != null) {
            ((ViewGroup) content.getParent()).removeView(content);
        }
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        Window w = dlg.getWindow();
        LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = -1000;   //cMakeBottom
        lp.gravity = gravity;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(content);
        dlg.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        dlg.show();
        return dlg;
    }

    public void subscribeAddSrpSuccess(HttpJsonResponse response) {
        if (response != null) {
            String subId = response.getBody().getAsJsonArray("id").get(0).toString();
            try {
                if (!StringUtils.isEmpty(subId)) {
                    mSubcribeId = Long.parseLong(subId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SubscribeBack subscribeBack = new Gson().fromJson(response.getBody(), SubscribeBack.class);
            SouYueToast.makeText(getApplicationContext(),
                    R.string.subscribe__success, SouYueToast.LENGTH_SHORT).show();
            sysp.putBoolean(SYSharedPreferences.KEY_UPDATE, true);

            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_DETAIL_CLICK);
            isSubcribe = true;
            if (mDetailType == DETAIL_TYPE_NEWS || mDetailType == KEYWORD_CATE_SPECIAL) {
                String imageIcon = CommonStringsApi.getSrpIconUrl(this, mSrpId);
                if (mDetailType == KEYWORD_CATE_SPECIAL) {//专题类型
                    //数据库操作
                    SuberedItemInfo info = new SuberedItemInfo();
                    info.setId(mInterestId);
                    info.setTitle(mKeyword);
                    info.setCategory("special");
                    info.setImage(imageIcon);
                    info.setSrpId(mSrpId);
                    info.setKeyword(mKeyword);
                    suberDao.addOne(info);
                } else {
                    //数据库操作
                    if (jsSrpId == null || jsSrpId != null && mSrpId.equals(jsSrpId + "")) {
                        SuberedItemInfo info = new SuberedItemInfo();
                        info.setId(mInterestId);
                        info.setTitle(mKeyword);
                        info.setId(subscribeBack.id().get(0));
                        info.setCategory("srp");
                        info.setImage(imageIcon);
                        info.setSrpId(mSrpId);
                        info.setKeyword(mKeyword);
                        suberDao.addOne(info);
                        UpEventAgent.onSrpSubscribe(this, mKeyword, mSrpId);
                    } else {  //插入推荐订阅数据
                        SuberedItemInfo info = new SuberedItemInfo();
                        jsimageLogo = CommonStringsApi.getSrpIconUrl(this, jsSrpId);
                        info.setId(subscribeBack.id().get(0));
                        info.setTitle(jsKeyword);
                        info.setCategory("srp");
                        info.setImage(jsimageLogo);
                        info.setSrpId(jsSrpId);
                        info.setKeyword(jsKeyword);
                        suberDao.addOne(info);
                    }
                }
            } else if (mDetailType == DETAIL_TYPE_RSS) {
                //数据库操作
                SuberedItemInfo info = new SuberedItemInfo();
                info.setId(mInterestId);
                info.setTitle(mKeyword);
                info.setCategory("rss");
//            info.setImage(image);
                info.setSrpId(mSrpId);
                info.setKeyword(mKeyword);
                suberDao.addOne(info);
            }
            if ((jsSrpId != null && mSrpId.equals(jsSrpId + "")) || jsSrpId == null) {
                btn_subscribe.setImageResource(R.drawable.srp_no_subscribe_selector);
            }
            //发送订阅广播，改变SRP页面菜单状态
            sendSubStateBroadcast(mContext);

            //通知js
            JsonObject obj = new JsonObject();
            obj.addProperty("result", SUB_ADD_SUCCESS);
            if (jsSrpId != null) {
                obj.addProperty("srp_id", jsSrpId);
            } else {
                obj.addProperty("srp_id", mSrpId);
            }
            mWebView.loadUrl("javascript:addSrpSubCallback(" + obj + ")");
            jsSrpId = null;
            jsimageLogo = null;
            jsKeyword = null;
        }

        //btn_subscribe.setClickable(true);
        subIsRunning = false;
    }

    public void subscribeDeleteSrpSuccess() {
        SouYueToast.makeText(this, R.string.subscibe_cancel_success, 0).show();
        SYSharedPreferences.getInstance().putBoolean(
                SYSharedPreferences.KEY_UPDATE, true);
        UpEventAgent.onSrpUnsubscribe(this, mKeyword, mSrpId);
        isSubcribe = false;
        isUpdateSuccess = true;
        sendSubStateBroadcast(this);
        btn_subscribe.setImageResource(R.drawable.srp_subscribe_selector);

        //通知js
        JsonObject obj = new JsonObject();
        obj.addProperty("result", 2);
        obj.addProperty("srp_id", mSrpId);
        mWebView.loadUrl("javascript:addSrpSubCallback(" + obj + ")");

        //数据库操作
        SuberedItemInfo info = new SuberedItemInfo();
        info.setTitle(mKeyword);
        info.setCategory("srp");
//        info.setImage(image);
        info.setSrpId(mSrpId);
        info.setKeyword(mKeyword);
        suberDao.clearOne(info);

        //btn_subscribe.setClickable(true);
        subIsRunning = false;
    }

    public void subscribeDeleteSuccess(HttpJsonResponse res) {
        int code = res.getBody().get("result").getAsInt();
        if (code == 500) {
            SouYueToast.makeText(this, R.string.subscibe_delete_fail, Toast.LENGTH_LONG).show();
            return;
        } else if (code == 501) {
            SouYueToast.makeText(this, R.string.cricle_admin_no_quit_setting_text, Toast.LENGTH_LONG).show();
            return;
        }

        SouYueToast.makeText(this, R.string.subscibe_cancel_success, 0).show();
        sendSubStateBroadcast(this);
        SYSharedPreferences.getInstance().putBoolean(
                SYSharedPreferences.KEY_UPDATE, true);
        UpEventAgent.onSrpUnsubscribe(this, mKeyword, mSrpId);
        isSubcribe = false;
        isUpdateSuccess = true;
        btn_subscribe.setImageResource(R.drawable.srp_subscribe_selector);

        //通知js
        JsonObject obj = new JsonObject();
        obj.addProperty("result", SUB_UPDATE);
        obj.addProperty("interest_id", mInterestId);
        mWebView.loadUrl("javascript:addCircleSubCallback(" + obj + ")");

        //数据库操作
        SuberedItemInfo info = new SuberedItemInfo();
        info.setCategory("interest");
        info.setSrpId(mSrpId);
        suberDao.clearOne(info);
        sendSubStateBroadcast(mContext);

        //btn_subscribe.setClickable(true);
        subIsRunning = false;
    }

    public void getToTopSuccess(HttpJsonResponse res) {
        int state = res.getBody().get("state").getAsInt();
        UIHelper.ToastMessage(this, Constant.get_POST_TOP_STATE_Label(state));
        if (state == Constant.POST_TOP_STATE_SUCCESS) {
            hasChanged = true;
            top = true;
            isToTop = true;
        }
    }

    public void getPrimeSuccess(HttpJsonResponse res) {
        int state = res.getBody().get("state").getAsInt();
        UIHelper.ToastMessage(this, Constant.get_POST_PRIME_STATE_Label(state));
        if (state == Constant.POST_PRIME_STATE_SUCCESS) {
            hasChanged = true;
            prime = true;
            isPrime = true;
        }
    }

    public void deletePostsSuccess(HttpJsonResponse res) {
        int state = res.getBody().get("state").getAsInt();
        UIHelper.ToastMessage(this, Constant.get_POST_DELETE_STATE_Label(state));
        if (state == Constant.POST_DELETE_STATE_SUCCESS) {
            finishWithDelete();
        } else if (state == Constant.POST_DELETE_STATE_NOT_EXIST) {
            finish();
        }
    }

    public void cancleTotopSuccess(HttpJsonResponse res) {
        int state = res.getBody().get("state").getAsInt();
        UIHelper.ToastMessage(this,
                Constant.get_POST_CANCEL_TOP_STATE_Label(state));
        if (state == Constant.POST_CANCEL_TOP_STATE_SUCCESS) {
            hasChanged = true;
            top = true;
            isToTop = false;

        }
    }

    public void canclePrimeSuccess(HttpJsonResponse res) {
        int statusCode = res.getBody().get("state").getAsInt();
        int state = res.getBody().get("state").getAsInt();
        UIHelper.ToastMessage(this,
                Constant.get_POST_CANCEL_PRIME_STATE_Label(state));
        if (statusCode == Constant.POST_CANCEL_PRIME_STATE_SUCCESS) {
            hasChanged = true;
            prime = true;
            isPrime = false;
            String type = "heanline";
            if (mItem.getCategory().equals(ConstantsUtils.FR_INTEREST_BAR)) {
                type = HomeBallBean.INTEREST;
            }
            BroadCastUtils.sendToDeleteSearchResultItemData(this,
                    mItem.getId());
            mDBHelper.deleteData(SYUserManager.getInstance().getUserId(),
                    mItem.getId());// 这里只用dataid,因为只有我的头条有删除
        }
    }

    @Override
    public void onClick(View v) {
        utype = SYUserManager.getInstance().getUserType();
        int id = v.getId();
        switch (id) {
            case R.id.btn_detail_subscribe: //顶部 订阅按钮点击
                if (subIsRunning) return;
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    SouYueToast.makeText(this, R.string.neterror,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //btn_subscribe.setClickable(false);  //设置为不可以点击
                subIsRunning = true;
                if (mDetailType == DETAIL_TYPE_CIRCLE) {
                    if (mOptionRoleType == Constant.ROLE_NONE
                            && interestType.equals("1")) {
                        IntentUtil.gotoSecretCricleCard(
                                DetailModuleActivity.this,
                                mInterestId, 0);
                        return;
                    }
                    if (!isSubcribe) {
                        loadSaveRecomentCircles(SYUserManager.getInstance().getToken(),
                                mInterestId + "",
                                ZSSdkUtil.NEWSDETAIL_SUBSCRIBE_TOP);
                    } else {
                        CircleExitCircleRequest.send(
                                HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID,
                                DetailModuleActivity.this,
                                mInterestId,
                                SYUserManager.getInstance().getToken(),
                                ZSSdkUtil.OTHER_SUBSCRIBE_MENU);
                    }
                } else if (mDetailType == DETAIL_TYPE_NEWS || mDetailType == KEYWORD_CATE_SPECIAL) {
                    String st = "srp";
                    if (mDetailType == KEYWORD_CATE_SPECIAL) {
                        st = "special";
                    }
                    if (isSubcribe) {
                        SuberedItemInfo infoFromDb = suberDao.queryOne(mSrpId);
                        if (infoFromDb != null) {
                            SubUpdateRequest request = new SubUpdateRequest(HttpCommon.SUB_ADDDEL_REQUEST, this);
                            request.setTag(this);
                            request.addParams(
                                    SYUserManager.getInstance().getToken(),
                                    infoFromDb.getSrpId(),
                                    "del",
                                    infoFromDb.getCategory(),
                                    infoFromDb.getId() + "", infoFromDb.getKeyword());
                            mMainHttp.doRequest(request);
                        } else {
                            SubDeleteReq request = new SubDeleteReq(HttpCommon.SUB_DELETE_REQUEST, this);   //退订
                            request.addParameters(mSubcribeId, st, mSrpId, ZSSdkUtil.NEWSDETAIL_SUBSCRIBE_TOP);
                            mMainHttp.doRequest(request);
                        }
                    } else {
                        SubAddReq req = new SubAddReq(HttpCommon.SUB_ADD_REQUEST, this);    //订阅
                        req.addParameters(mKeyword, mSrpId, -1 + "", "", st, ZSSdkUtil.NEWSDETAIL_SUBSCRIBE_TOP);
                        mMainHttp.doRequest(req);
                    }
                }
                break;
            case R.id.img_arrow:
            case R.id.activity_bar_title:
                if (mTouchTitle) {
                    return;
                }
                if (mDetailType == DETAIL_TYPE_NEWS) {
                    IntentUtil.gotoSrp(this, mSrpId, mKeyword, mWeijianMd5,
                            mKeyword, OPSOURCE_FROM_HEAD);
                } else if (mDetailType == DETAIL_TYPE_CIRCLE) {
                    UIHelper.showCircleIndex(this, mSrpId, mKeyword, mKeyword,
                            interest_logo);
                }
                UmengStatisticUtil.onEvent(this, UmengStatisticEvent.DETAIL_ARROW_CLICK);    //Umeng
                break;
            case R.id.btn_option:
                showMenu();
                UmengStatisticUtil.onEvent(this, UmengStatisticEvent.DETAIL_MENU_CLICK);    //Umeng
                break;
            case R.id.collect_imagebutton:// 收藏
                if (mParamUrl != null) {
                    if (!mHasFavorited) {
                        AddFavorite2Req req = new AddFavorite2Req(HttpCommon.DETAIL_ADDFAVORITE_ID, this);
                        String title = StringUtils.replaceBlank(StringUtils.shareTitle(mTitle, mContent));
                        req.setParams(String.valueOf(mDetailType),

                                mParamUrl, SYUserManager.getInstance().getToken(),
                                DEVICE_COME_FROM, mSrpId, mKeyword, title,
                                imageUrl);
                        CMainHttp.getInstance().doRequest(req);
                    } else {
                        // 取消收藏
                        CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID, this);
                        req.setParamsForOpenFlag(SYUserManager.getInstance().getToken(),
                                mSourceUrl, mDetailType, DEVICE_COME_FROM);
                        CMainHttp.getInstance().doRequest(req);
                    }
                }
                break;
            case R.id.ding_layout:// 顶
                if (mHasUp) {
                    Toast.makeText(DetailModuleActivity.this, R.string.detail_have_ding,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mUpDowning) {
                    mUpDowning = true;
                    AddCommentUpReq req = new AddCommentUpReq(HttpCommon.DETAIL_ADDUP_ID, this);
                    req.setParams(mKeyword, mSrpId, mParamUrl, SYUserManager
                                    .getInstance().getToken(), DEVICE_COME_FROM,
                            UP_TYPE_MAIN, 0, mTitle, imgs, "", "", "", mblog_userId);
                    CMainHttp.getInstance().doRequest(req);
                }
                break;
            case R.id.follow_post_layout:// 跟帖
                // 判断用户是否登陆
                showComment();
                break;
            case R.id.share_imagebutton:// 分享
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    SouYueToast.makeText(this, R.string.neterror,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mTitle = StringUtils.replaceBlank(StringUtils.shareTitle(mTitle, mContent));
                showShareWindow();
                break;
            case R.id.detail_up:
                if (mHasUp) {
                    Toast.makeText(DetailModuleActivity.this, R.string.detail_have_ding,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mUpDowning) {
                    mUpDowning = true;
                    AddCommentUpReq req = new AddCommentUpReq(HttpCommon.DETAIL_ADDUP_ID, this);
                    req.setParams(mKeyword, mSrpId, mParamUrl, SYUserManager
                                    .getInstance().getToken(), DEVICE_COME_FROM,
                            UP_TYPE_MAIN, 0, mTitle, imgs, "", "", "", mblog_userId);
                    CMainHttp.getInstance().doRequest(req);
                }
                break;
            case R.id.detail_down:
                if (mHasDown) {
                    Toast.makeText(DetailModuleActivity.this, R.string.detail_have_cai,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mUpDowning) {
                    mUpDowning = true;
                    AddCommentDownReq req = new AddCommentDownReq(HttpCommon.DETAIL_ADDDOWN_ID, this);
                    req.setParams(mKeyword, mSrpId, mParamUrl, SYUserManager
                                    .getInstance().getToken(), DEVICE_COME_FROM,
                            UP_TYPE_MAIN, mTitle, imgs, "", "", "");
                    CMainHttp.getInstance().doRequest(req);
                }
                break;
            case R.id.detail_pay:
                isRefreshData = true; // 为了让页面回来也刷新
                if (!SouyueAPIManager.isLogin()) {
                    SouyueAPIManager.goLogin(DetailModuleActivity.this, true);
                    return;
                }
                UIHelper.showRewardsPage(DetailModuleActivity.this, mBlogId, mInterestId,
                        mblog_userId);
                break;
            // 创业大赛点击订阅退订按钮
            case R.id.ib_title_bar_sub:
                String st = "srp";
                if (mDetailType == KEYWORD_CATE_SPECIAL) {
                    st = "special";
                }
                break;
            case R.id.detail_have_no_comment:
                if (mHasCommentDismiss) {
                    showComment();
                }
                break;
        }
    }


    /**
     * 加载数据-----订阅兴趣圈
     *
     * @param token
     * @param interest_id
     */
    private void loadSaveRecomentCircles(String token, String interest_id, String opSource) {
        InterestSubscriberReq req = new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID, this);
        req.setParams(token, interest_id, opSource);
        CMainHttp.getInstance().doRequest(req);
    }

    private void showComment() {
        if (!SouyueAPIManager.isLogin() && mCircleType == CIRCLE_TYPE_CIRCLE && interestType.equals("1")) {
            isRefreshData = true;
            SouyueAPIManager.goLoginForResult(DetailModuleActivity.this, 0x500);
        } else if (mOptionRoleType == Constant.ROLE_NONE
                && mCircleType == CIRCLE_TYPE_CIRCLE && interestType.equals("1")) { // 非圈成员
            showJoinInterest();
        } else if (is_bantalk == Constant.MEMBER_BAN_TALK_YES) {
            SouYueToast.makeText(DetailModuleActivity.this, R.string.you_hadtalk,
                    Toast.LENGTH_SHORT).show();
        } else {
            lvPost.setSelection(0);
            lvPost.invalidate();

            pairScrollView.scrollToSecondView();
            circle_bottom_bar.setVisibility(View.INVISIBLE);
            lvPost.post(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        showCommentDialog();
                    }
                }
            });
        }
    }

    private void showCommentDialog() {

        // 当前页跟帖
        if (mBlogId > 0) {
            // 这里用follow_post_layout.getContext()防止内存溢出
            circleFollowDialog = new CircleFollowDialogNew(
                    follow_post_layout.getContext(), this, mParamUrl,
                    DEVICE_COME_FROM, mSrpId, mKeyword, mainPosts);
        } else {
            circleFollowDialog = new CircleFollowDialogNew(
                    follow_post_layout.getContext(), this, mParamUrl,
                    DEVICE_COME_FROM, mSrpId, mKeyword, null);
        }
        circleFollowDialog.setmInterestId(mInterestId + "");
        circleFollowDialog.setMain_title(mTitle);
        circleFollowDialog.setMain_decsription(mContent);
        circleFollowDialog.setMain_images(imgs);
        circleFollowDialog.mRole = mOptionRoleType;
        circleFollowDialog.setDetailType(mDetailType);
        circleFollowDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (circleFollowDialog != null)
                            circleFollowDialog.saveInfo(mUrl.toString());
                    }
                });
        circleFollowDialog.setListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                if (circleFollowDialog.getImgLen() == 0) {
                    IntentUtil.jumpImgGroup(DetailModuleActivity.this,
                            circleFollowDialog.getImgLen());
                }
            }
        });
        circleFollowDialog.setPhotoListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                jumpTakePhoto();
            }
        });
        circleFollowDialog.setAddImgListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                showAddImgMenu();
            }
        });
        circleFollowDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        pairScrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHasCommentDismiss = true;
                                circle_bottom_bar.setVisibility(View.VISIBLE);
                                pairScrollView.setmInputOut(false);
                                postAdapter.notifyDataSetChanged();
//                                setAdapterData();
                                if (postAdapter.getCount() == 0) {
                                    mHaveNoComment.setVisibility(View.VISIBLE);
                                } else {
                                    mHaveNoComment.setVisibility(View.GONE);
                                }
                            }
                        }, 500);
                        circleFollowDialog.hideKeyboard();
                    }
                });
        pairScrollView.setmInputOut(true);
        circleFollowDialog.showDialog();
        mHasCommentDismiss = false;
        circleFollowDialog.setEditText(SYSharedPreferences.getInstance()
                .getString(mUrl + "_text", ""));
        String strImg = SYSharedPreferences.getInstance().getString(
                mUrl + "_img", "");
        if (strImg != null && !strImg.equals("")) {
//            List<String> list = JSON.parseArray(strImg, String.class);
            List<String> list = new Gson().fromJson(strImg, new TypeToken<List<String>>() {
            }.getType());
            if (list != null && list.size() != 0) {
                circleFollowDialog.addImagePath(list);
            }
        }
    }

    private void getCommentList(boolean _ispull) {
        initCommentUpOrDown();
        if (isLoadAll) {
            if (list_progress.isLoading) {
                list_progress.goneLoading();
            }
            UIHelper.ToastMessage(this, "已全部加载");
            needLoad = false;
            return;
        }
        if (lvPost.getFooterViewsCount() == 0 && _ispull) {
            View v = getFootView();
            v.setVisibility(View.VISIBLE);
            lvPost.addFooterView(footerView);
        }
        NewCommentListRequest request = new NewCommentListRequest(HttpCommon.DETAIL_COMMENT_NEW_LIST_ID, this);
        request.setParams(mParamUrl, DEVICE_COME_FROM, last_sort_num, mSrpId, mKeyword, mDetailType);
        mMainHttp.doRequest(request);
    }

    public void commentUpSuccess() {
        up = true;
        ding_count.setText(Integer.parseInt(ding_count.getText().toString())
                + 1 + "");
        ding_imagebutton.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_up_unnormal));
        mHasUp = true;
        hasChanged = true;
        mUpDowning = false;
        mUpCount++;
        mHeadUpCount.setText(mUpCount + "");
        mHeadUpCount.setTextColor(getResources().getColor(R.color.detail_red));
        mHeadUpIcon.setImageResource(R.drawable.detail_up_red);
        // 统计
        switch (mCircleType) {
            case CIRCLE_TYPE_NEWS:
                UpEventAgent.onNewsUp(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl);
                break;
            case CIRCLE_TYPE_CIRCLE:
                UpEventAgent.onGroupUp(this, mInterestId + "." + mKeyword + "", "",
                        mBlogId + "");
                break;
            default:
        }
    }

    public void commentDownSuccess(HttpJsonResponse res) {
        mHasDown = true;
        hasChanged = true;
        mUpDowning = false;
        mDownCount++;
        mHeadDownCount.setText(mDownCount + "");
        mHeadDownIcon.setImageResource(R.drawable.detail_down_red);
        mHeadDownCount
                .setTextColor(getResources().getColor(R.color.detail_red));
    }

    public void getCommentListSuccess(List<CommentsForCircleAndNews> listLatest, List<CommentsForCircleAndNews> listHot) {
        list_progress.goneLoading();
        if (!listLatest.isEmpty() && postsList.containsAll(listLatest)) {
            return;
        }
        if (!listHot.isEmpty()) {
            postsList.addAll(listHot);
            postsListHot.addAll(listHot);
        }
        if (!listLatest.isEmpty()) {
            last_sort_num = listLatest.get(listLatest.size() - 1)
                    .getComment_id();
            postsList.addAll(listLatest);
            postAdapter.notifyDataSetChanged();
//            setAdapterData();
            pno++;
        }
        if (postAdapter.getCount() == 0) {
            mHaveNoComment.setVisibility(View.VISIBLE);
            lvPost.removeFooterView(footerView);
            isLoadAll = true;
            needLoad = false;
        } else {
            if (CollectionUtils.isEmpty(listLatest)
                    || listLatest.size() < PAGE_SIZE_5) {
                isLoadAll = true;
                needLoad = false;
                lvPost.removeFooterView(footerView);
            } else {
                needLoad = true;
            }
            mHaveNoComment.setVisibility(View.GONE);
        }

//        if (mHeadSuccess) {
//            follow_post_layout.setEnabled(true);
//            mHaveNoComment.setEnabled(true);
//        }
        mDoneKvo.doDone();
    }

    // 收藏返回
    public void newFavoriteAddSuccess(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            SouYueToast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
            return;
        }

        SouYueToast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        mHasFavorited = true;
        newsId = res.getBody().get("newsId").getAsInt();
        collect_imagebutton.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_collect_unnormal));

        changeFavoriteStateBroadcast();

        switch (mCircleType) {
            case CIRCLE_TYPE_NEWS:
                // 统计
                UpEventAgent.onNewsFavorite(this, mChannel, mKeyword, mSrpId,
                        mTitle, mSourceUrl);
                break;
            case CIRCLE_TYPE_CIRCLE:
                // 统计
                UpEventAgent.onGroupFavorite(this, mInterestId + "." + mKeyword
                        + "", "", mBlogId + "");
                break;
            default:
        }
    }

    // 取消收藏成功
    public void cancelCollectSuccess(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            return;
        }
        SouYueToast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
        mHasFavorited = false;
        collect_imagebutton.setEnabled(true);
        collect_imagebutton.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_collect_normal));

        changeFavoriteStateBroadcast();

    }

    /**
     * 收藏状态改变广播，主要用来刷新收藏列表
     */
    private void changeFavoriteStateBroadcast() {
        Intent favIntent = new Intent();
        favIntent.setAction(MyFavoriteActivity.FAVORITE_ACTION);
        sendBroadcast(favIntent);
    }

    /**
     * 分享提示框
     */
    private void showShareWindow() {
        if (mDetailType != DETAIL_TYPE_RSS && mCircleType == CIRCLE_TYPE_NEWS) {
            if (isJokeOrGif()) {
                mCircleShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.JOKE_AND_GIF);
            } else {
                mCircleShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.READABILITY);
            }

        } else if (mDetailType == DETAIL_TYPE_CIRCLE) {
            mCircleShareMenuDialog = new ShareMenuDialog(this, this,
                    ShareConstantsUtils.NEW_DETAIL);
        } else {
            mCircleShareMenuDialog = new ShareMenuDialog(this, this,
                    ShareConstantsUtils.WEBSRCVIEWKEYWORD);
        }
        mCircleShareMenuDialog.showBottonDialog(); // 遮挡住底部条来展示
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        if (requestCode == 2 && resultCode != RESULT_CANCELED) { // 从相机回来
            String picPath = null;
            if (imageFileUri != null) {
                picPath = Utils.getPicPathFromUri(imageFileUri,
                        DetailModuleActivity.this);
                int degree = 0;
                if (!StringUtils.isEmpty(picPath))
                    degree = ImageUtil.readPictureDegree(picPath);
                Matrix matrix = new Matrix();
                if (degree != 0) {// 解决旋转问题
                    matrix.preRotate(degree);
                }
                ArrayList<String> list = new ArrayList<String>();
                list.add(picPath);
                circleFollowDialog.addImagePath(list);

            } else {
                Toast.makeText(this, R.string.self_get_image_error,
                        Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 0x500) { // 详情页登陆后，在关闭时候通知列表做数据的更新
            isLogin = true;
        } else if (requestCode == 0x3) {

            if (mHsvWidgets != null) {
                mHsvWidgets.setCurrentItem(0);
            }
        } else if (resultCode == RESULT_OK && mCircleType == CIRCLE_TYPE_CIRCLE) {
            if (data != null) {
                isQuite = data.getBooleanExtra("isQuite", false);
                isSubscribeSuccess = data.getBooleanExtra("isSubscribeSuccess",
                        false);
            }
        }
        if (data == null) {
            return;
        }

        if (resultCode == UIHelper.RESULT_OK) {     //帖子编辑返回
            Posts published = (Posts) data.getSerializableExtra("publishPosts");
            int publish_type = data.getIntExtra("publish_type", 0);
            if (published == null) {
                return;
            }

            if (publish_type == PublishActivity.PUBLISH_TYPE_M_EDIT) { // 主帖编辑
                isHasEdit = true;
                isFromEdit = true;
                hasChanged = true;
                mWebView.clearCache(true);
                mWebView.clearHistory();
                mWebView.clearFormData();
                deleteDatabase("webview.db");
                deleteDatabase("webviewCache.db");
                mainPosts = published;
                result_type = 2;

                sendCircleRequest(true);    //编辑完成后,强取网络
                mWebView.loadUrl(mUrl.toString());
                mWebSuccess = true;// 预计成功
            }
        } else if (resultCode == UIHelper.RESULT_CODE_REPLY) { // 回复后更新回复列表
            long mBlogId = data.getLongExtra("comment_id", 0);
            ArrayList<Reply> newReplyList = (ArrayList<Reply>) data
                    .getSerializableExtra("newReplyList");
            for (CommentsForCircleAndNews p : postsList) {
                if (p.getComment_id() == mBlogId) {
                    p.setReplyList(newReplyList);
                    postAdapter.notifyDataSetChanged();
//                    setAdapterData();
                }
            }
        } else if (resultCode == 0x200) { // 从相册回来
            List<String> list = data.getStringArrayListExtra("imgseldata");
            circleFollowDialog.addImagePath(list);
        }

        if (resultCode == 222 && requestCode == SRP_REQUEST_CODE) {   //从srp首页回来
            //通知js
            JsonObject obj = new JsonObject();
            obj.addProperty("result", data.getIntExtra("result", 0));
            if (data.getStringExtra("srp_id") != null) {
                obj.addProperty("srp_id", data.getStringExtra("srp_id"));
            }
            mWebView.loadUrl("javascript:addSrpSubCallback(" + obj + ")");
        } else if (resultCode == CIRCLE_REQUEST_CODE) {   //从circle首页回来
            JsonObject obj = new JsonObject();
            obj.addProperty("result", data.getExtras().getInt("result", 0));
            if (data.getExtras().getString("interest_id") != null) {
                obj.addProperty("interest_id", data.getExtras().getString("interest_id"));
            }
            mWebView.loadUrl("javascript:addCircleSubCallback(" + obj + ")");
        }
    }

    // 分享到精华圈
    private void shareToDigest(ShareContent shareContent) {
        // 转用圈子的分享对象
        if (circleShareContent == null) {
            circleShareContent = new com.zhongsou.souyue.circle.model.ShareContent();
        }
        circleShareContent.setContent(shareContent.getContent());
        circleShareContent.setTitle(shareContent.getTitle());
        circleShareContent.setKeyword(shareContent.getKeyword());
        circleShareContent.setSrpId(shareContent.getSrpId());
        circleShareContent.setNewsUrl(shareContent.getUrl());
        UIHelper.showPostsFriend(DetailModuleActivity.this, mBlogId, getToken(),
                interest_name, mInterestId, circleShareContent, true);// 为了统计
        // 新增interest_name
    }

    private String getToken() {
        return SYUserManager.getInstance().getToken();
    }

    @Override
    public ShareContent getShareContent() {
//        imageBitmap = null;
//        getImage();
        ShareContent result = new ShareContent(
                StringUtils.shareTitle(mTitle, mContent),
                StringUtils.isNotEmpty(blogShareUrl) ? blogShareUrl : mUrl.toString(),
                imageBitmap,
                StringUtils.shareDesc(mContent),
                imageUrl);
        result.setSharePointUrl(blogShareUrl);
        result.setKeyword(mKeyword);
        result.setSrpId(mSrpId);
        return result;
    }

    public ShareContent getNewsShareContent() {
//        getImage();
        ShareContent result = new ShareContent(StringUtils.shareTitle(mTitle,
                mContent), StringUtils.isNotEmpty(blogShareUrl) ? blogShareUrl
                : mUrl.toString(), imageBitmap, StringUtils.shareDesc(mContent), imageUrl);
        result.setSharePointUrl(mSourceUrl == null ? "" : mSourceUrl);
        result.setKeyword(mKeyword);
        result.setSrpId(mSrpId);
        return result;
    }

    private void getImage() {
        if (mShareImageUrls != null && mShareImageUrls.size() > 0) {
            imageUrl = mShareImageUrls.get(0);
        } else {
            imageUrl = "";
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            try {
                //分享图片质量降低，防止oom
                imageBitmap = ImageUtil.getSmallBitmap(imageLoader.getDiskCache().get(imageUrl).getAbsolutePath());
            } catch (Exception e) {
                imageBitmap = null;
            }
            if (imageBitmap == null) {
                PhotoUtils.showCard(
                        PhotoUtils.UriType.HTTP, imageUrl,
                        new ImageView(DetailModuleActivity.this),
                        MyDisplayImageOption.bigoptions);
                try {
                    File cache = imageLoader.getDiskCache().get(imageUrl);
                    String path = "";
                    if (cache != null) {//这个文件可能为null导致错误，修复bug  add by lvqiang
                        path = cache.getAbsolutePath();
                    }
                    imageBitmap = ImageUtil.getSmallBitmap(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebOffsety = mWebView.getScrollY();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
//            该处的处理尤为重要:
//            应该在内置缩放控件消失以后,再执行mWebView.destroy()
//            否则报错WindowLeaked
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.setVisibility(View.GONE);
            long delayTime = ViewConfiguration.getZoomControlsTimeout();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mWebView != null) {
                                    mFirstView.removeView(mWebView);
                                    mWebView.stopLoading();
                                    mWebView.removeAllViews();
                                    mWebView.destroyDrawingCache();
                                    mWebView.destroy();
                                    mWebView = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, delayTime);
        }
        postsList.clear();
        postsListHot.clear();
        circleFollowDialog = null;
        mVolleyHttp.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.loadUrl("javascript:uiPause()");
    }

    private boolean isDoingGetImage = false;

    // 点击分享第三方时调用
    @Override
    public void loadData(final int position) {
        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
            if (!isDoingGetImage) {
                isDoingGetImage = true;
                getImage();
                if (imageBitmap == null) {
                    new Thread() {
                        @Override
                        public void run() {
                            PhotoUtils.getImageLoader().loadImage(imageUrl, MyImageLoader.mShareImage, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {
                                    SouYueToast.makeText(DetailModuleActivity.this,
                                            getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                                            .show();
                                    isDoingGetImage = false;
                                }

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                    imageBitmap = bitmap;
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            doShare(position);
                                        }
                                    });
                                }
                            });
                        }
                    }.start();
                } else {
                    doShare(position);
                }
            } else {
                SouYueToast.makeText(DetailModuleActivity.this,
                        "别点这么快啊..", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            SouYueToast.makeText(DetailModuleActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    private void doShare(int position) {
        isDoingGetImage = false;
        ShareContent content = null;
        if (mCircleType == CIRCLE_TYPE_CIRCLE) {
            content = getShareContent();
            doShareCircle(position, content);
        } else if (mDetailType == DETAIL_TYPE_RSS) {
            content = getNewsShareContent();
            doShareRss(position, content);
        } else if (mCircleType == CIRCLE_TYPE_NEWS) {
            content = getNewsShareContent();
            doShareNews(position, content);
        }
    }

    private void doShareRss(int position, ShareContent content) {
        switch (position) {
            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                boolean islogin = (SYUserManager.getInstance().getUser().userType()
                        .equals(SYUserManager.USER_ADMIN));
                if (islogin) {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mSourceUrl, "sy_friend");
                    ImShareNews imsharenews = new ImShareNews(content.getKeyword(),
                            content.getSrpId(), content.getTitle(),
                            content.getSharePointUrl(), content.getPicUrl());
                    ContactsListActivity.startSYIMFriendAct(DetailModuleActivity.this,
                            imsharenews);
                } else {
                    toLogin();
                }
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "sina_wb");
                mSsoHandler = ShareByWeibo.getInstance().share(DetailModuleActivity.this,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "wx");// 点击分享了此处加统计
                ShareByWeixin.getInstance().share(content, false);
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                String wxFriendUrl = content.getUrl();
                if (null != wxFriendUrl
                        && wxFriendUrl.contains("urlContent.groovy?")) {
                    wxFriendUrl = wxFriendUrl.replace(
                            "urlContent.groovy?",
                            "urlContent.groovy?keyword="
                                    + StringUtils.enCodeRUL(mKeyword) + "&mSrpId="
                                    + mSrpId + "&");
                }
                content.setUrl(wxFriendUrl);
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "friend");
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_INTEREST:
                LoginAlert loginDialog = new LoginAlert(DetailModuleActivity.this,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shareToInterest();
                            }
                        }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                loginDialog.show();
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "qfriend");
                content.setContent("");
                ShareByTencentQQ.getInstance().share(DetailModuleActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "qzone");
                content.setContent("");
                ShareByTencentQQZone.getInstance().share(DetailModuleActivity.this,
                        content);
                break;
            default:
                break;
        }
    }

    private void doShareNews(int position, final ShareContent content) {
        switch (position) {
            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                boolean islogin = (SYUserManager.getInstance().getUser().userType()
                        .equals(SYUserManager.USER_ADMIN));
                if (islogin) {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mSourceUrl, "sy_friend");
                    ImShareNews imsharenews = new ImShareNews(content.getKeyword(),
                            content.getSrpId(), content.getTitle(),
                            content.getSharePointUrl(), content.getPicUrl());
                    IMShareActivity.startSYIMFriendAct(DetailModuleActivity.this,
                            imsharenews);
                } else {
                    toLogin();
                }
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "sina_wb");
                mSsoHandler = ShareByWeibo.getInstance().share(DetailModuleActivity.this,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "wx");// 点击分享了此处加统计
                ShareByWeixin.getInstance().share(content, false);
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                String wxFriendUrl = content.getUrl();
                if (null != wxFriendUrl
                        && wxFriendUrl.contains("urlContent.groovy?")) {
                    wxFriendUrl = wxFriendUrl.replace(
                            "urlContent.groovy?",
                            "urlContent.groovy?keyword="
                                    + StringUtils.enCodeRUL(mKeyword) + "&mSrpId="
                                    + mSrpId + "&");
                }
                content.setUrl(wxFriendUrl);
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "friend");
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_INTEREST:
                LoginAlert loginDialog = new LoginAlert(DetailModuleActivity.this,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shareToInterest();
                            }
                        }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                loginDialog.show();
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "qfriend");
                content.setContent("");
                ShareByTencentQQ.getInstance().share(DetailModuleActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mSourceUrl, "qzone");
                content.setContent("");
                ShareByTencentQQZone.getInstance().share(DetailModuleActivity.this,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                isfreeTrial = SYUserManager.getInstance().getUser().freeTrial();
                if (isfreeTrial) {
                    Dialog alertDialog = new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.share_mianshen))
                            .setPositiveButton(getString(R.string.alert_assent),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // 点击分享了此处加统计
                                            UpEventAgent.onNewsShare(
                                                    DetailModuleActivity.this, mChannel,
                                                    mKeyword, mSrpId, mTitle, mSourceUrl,
                                                    "sy_webfriend");
                                            share2SYwangyou(content);
                                        }
                                    })
                            .setNegativeButton(getString(R.string.alert_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                    alertDialog.show();
                } else {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mSourceUrl, "sy_webfriend");
                    share2SYwangyou(content);
                }
                break;
            case ShareMenuDialog.SHARE_TO_DIGEST:
                // 判断用户是否登陆
                if (null != utype && !utype.equals("1")) {

                    LoginAlert loginDialog1 = new LoginAlert(DetailModuleActivity.this,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 点击分享了此处加统计
                                    UpEventAgent.onNewsShare(DetailModuleActivity.this,
                                            mChannel, mKeyword, mSrpId, mTitle,
                                            mSourceUrl, "jhq");
                                    shareToWangyouTuiJian();
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 0);

                    loginDialog1.show();

                } else {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mSourceUrl, "jhq");
                    // 登陆用户直接分享到精华区
                    shareToWangyouTuiJian();
                }
                break;
            default:
                break;
        }
    }

    public void shareToWangyouTuiJian() {
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
        } else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(mUrl.toString(), StringUtils.shareTitle(mTitle,
                    mContent), imageUrl == null ? "" : imageUrl.toString()
                    .trim(), mContent, "", "", mKeyword, mSrpId);
            mMainHttp.doRequest(share);
        }
    }

    public void shareSuccess(Long id) {
        newsId = id;
        SouYueToast.makeText(this, R.string.share_success,
                SouYueToast.LENGTH_SHORT).show();
    }

    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        startActivity(intent);
    }

    private void share2SYwangyou(final ShareContent content) {
        if (null != utype && !utype.equals("1")) {
            LoginAlert loginDialog = new LoginAlert(DetailModuleActivity.this,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            share2SYFriends(content);
                        }
                    }, CommonStringsApi.SHARE_JHQ_WARNING, 1);
            loginDialog.show();
        } else {
            share2SYFriends(content);
        }
    }

    private void share2SYFriends(ShareContent content) {
        Bundle b = new Bundle();
        Intent i = new Intent();
        SearchResultItem is = DetailItem.SearchResultToDetailItem(mItem);
        b.putSerializable("searchResultItem", is);
        i.setClass(this, ShareToSouyueFriendsDialog.class);
        i.putExtras(b);
        i.putExtra("content", content.getContent());
        i.putExtra("shareUrl", content.getSharePointUrl());
        startActivity(i);
    }

    private void shareToInterest() {
        com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
        interestmodel.setTitle(mTitle);
        interestmodel.setImages(mShareImageUrls);
        interestmodel.setKeyword(mKeyword);
        interestmodel.setSrpId(mSrpId);
        interestmodel.setChannel(mChannel);
        interestmodel.setBrief(mContent);
        String url = ZSEncode.encodeURI(StringUtils.enCodeKeyword(mUrl.toString()));
        if (containsUGC(mUrl.toString())) {
            interestmodel
                    .setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPESOURCE);
            interestmodel.setNewsUrl(url);
        } else {
            interestmodel
                    .setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPEHTML);
            interestmodel.setNewsUrl(mSourceUrl);
        }
        UIHelper.shareToInterest(DetailModuleActivity.this, interestmodel,
                mInterestId);
    }

    public boolean containsUGC(String url) {
        if (!TextUtils.isEmpty(url))
            return url.toLowerCase().contains("ugc.groovy")
                    || url.toLowerCase().contains("interest.content.groovy");
        return false;
    }

    private void doShareCircle(int position, final ShareContent content) {
        switch (position) {
            case ShareMenuDialog.SHARE_TO_INTEREST:
                // 判断用户是否登陆
                if (null != utype && !utype.equals("1")) {

                    LoginAlert loginDialog = new LoginAlert(DetailModuleActivity.this,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    shareToDigest(content);
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                    loginDialog.show();
                } else {
                    shareToDigest(content);
                }
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                mSsoHandler = ShareByWeibo.getInstance().share(DetailModuleActivity.this,
                        content);
                // 点击分享了此处加统计
                UpEventAgent.onGroupShare(this, mInterestId + "." + interest_name,
                        "", mBlogId + "", "sina_wb");
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                ShareByWeixin.getInstance().share(content, false);
                // 点击分享了此处加统计
                UpEventAgent.onGroupShare(this, mInterestId + "." + interest_name,
                        "", mBlogId + "", "wx");
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                String wxFriendUrl = content.getUrl();
                if (null != wxFriendUrl
                        && wxFriendUrl.contains("urlContent.groovy?")) {
                    wxFriendUrl = wxFriendUrl.replace(
                            "urlContent.groovy?",
                            "urlContent.groovy?keyword="
                                    + StringUtils.enCodeRUL(mKeyword) + "&srpId="
                                    + mKeyword + "&");
                }
                content.setUrl(wxFriendUrl);
                ShareByWeixin.getInstance().share(content, true);

                // 点击分享了此处加统计
                UpEventAgent.onGroupShare(this, mInterestId + "." + interest_name,
                        "", mBlogId + "", "friend");
                break;
            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                if (null != utype && !utype.equals("1")) {
                    LoginAlert loginDialog = new LoginAlert(DetailModuleActivity.this,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    gotoInterestCircle();
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 1);
                    loginDialog.show();
                } else {
                    gotoInterestCircle();
                    // 点击分享了此处加统计
                    UpEventAgent.onGroupShare(this, mInterestId + "."
                            + interest_name, "", mBlogId + "", "sy_friend");
                }

                break;

            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友

                ShareByTencentQQ.getInstance().share(DetailModuleActivity.this, content);
                // 点击分享了此处加统计
                UpEventAgent.onGroupShare(this, mInterestId + "." + interest_name,
                        "", mBlogId + "", "qfriend");
                break;

            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
//                content.setUrl(oriUrl);
                ShareByTencentQQZone.getInstance().share(DetailModuleActivity.this,
                        content);
                // 点击分享了此处加统计
                UpEventAgent.onGroupShare(this, mInterestId + "." + interest_name,
                        "", mBlogId + "", "qzone");
                break;

            default:
                break;
        }
    }

    private void gotoInterestCircle() {
        /**
         * 邀请好友
         */
        Posts mPost = new Posts();

        mPost.setBlog_id(mBlogId);
        mPost.setTitle(mTitle);
        mPost.setContent(mContent);
        mPost.setUser_id(mblog_userId);
        mPost.setIs_prime(isPrime ? 1 : 0);
        mPost.setTop_status(isToTop ? 1 : 0);
        int type = 0;
        try {
            type = Integer.parseInt(interestType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIHelper.showImFriend(DetailModuleActivity.this, mInterestId, true, imageUrl,
                mPost.getContent(), mPost, type,
                true, getShareContent().getSharePointUrl(),
                String.valueOf(mInterestId));

    }

    private void finishWithResult() {
        Intent data = new Intent();
        CircleResponseResultItem resultItem = wrapCircleResponseResultItem();
        if (resultItem != null) {
            // 以下几种状态都是在帖子详情中更新，返回列表时单个item进行更新
            if (isNew) {// 新增
                resultItem.setFollow_num("0");
                resultItem.setBroadcast_tag_id(broadcast_tag_id);
                resultItem.setBroadcast_tag_name(broadcast_tag_name);
                data.putExtra("resultType", Constant.CIRCLE_BROADCAST_TYPE_ADD);
                setBroadEvent(data, resultItem);
            } else if (top || up || follow || prime) {
                data.putExtra("resultType",
                        Constant.CIRCLE_BROADCAST_TYPE_UPDATE);
                setBroadEvent(data, resultItem);
            }
            if (isHasEdit) {
                data.putExtra("resultType", Constant.CIRCLE_BROADCAST_TYPE_EDIT);
                setBroadEvent(data, resultItem);
            }
        }
        Intent intent = new Intent();
        intent.putExtra("isSubscribeSuccess", isSubscribeSuccess);
        intent.putExtra("isUpdateSuccess", isUpdateSuccess);
        intent.putExtra("isLogin", isLogin);
        intent.putExtra("isQuite", isQuite);
        intent.putExtra("isBackSearchRefresh", false);//此变量只用来表示返回搜索页面屏蔽刷新页面
        setResult(RESULT_OK, intent);
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void setBroadEvent(Intent data, CircleResponseResultItem resultItem) {
        data.setAction(PublishActivity.ACTION_NEW_POST);
        data.putExtra(PublishActivity.ACTION_KEY_RESPONSEITEM, resultItem);
        if (hasChanged)
            sendBroadcast(data);
    }

    private CircleResponseResultItem wrapCircleResponseResultItem() {
        if (mainPosts != null) {
            CircleResponseResultItem item = new CircleResponseResultItem();
            item.setBrief(mainPosts.getContent());
            item.setTitle(mainPosts.getTitle());
            item.setBlog_id(mainPosts.getBlog_id());
            String nickName = posState == 1 ? "匿名" : mainPosts.getNickname();
            item.setNickname(nickName);
            item.setUser_image(mainPosts.getImage_url());
            item.setCreate_time(mainPosts.getCreate_time());
            item.setInterest_id(mInterestId);
            item.setImages(mainPosts.getImages());
            item.setUser_id(mainPosts.getUser_id());
            item.setSrp_word(mKeyword);
            item.setSrp_id(mSrpId);
            item.setIs_prime(isPrime ? 1 : 0); // 是否精华 0||1
            item.setTop_day(3);
            item.setTop_status(isToTop ? 1 : 0);
            item.setGood_num(ding_count.getText().toString());
            item.setFollow_num(follow_post_count.getText().toString());
            item.setHas_praised(mHasUp);
            // 设置帖子布局类型
//			if (item.getTop_status() == 1) {
//				item.setPostLayoutType(CircleResponseResult.POSTS_TYPE_IMAGE_TOP);
//				return item;
//			}
            if (item.getImages() != null && item.getImages().size() != 0) {
                item.setPostLayoutType(CircleResponseResult.getLayoutType(item
                        .getImages().size()));
            }
            return item;
        }
        return null;
    }

    @Override
    public void setImages(String iags) {

        if (null != iags) {
            mImageUrls = Arrays.asList(iags.trim().split(" "));
            com.tencent.mm.sdk.platformtools.Log.i("", "imageUrls size: "
                    + mImageUrls.size());
        }
    }

    /**
     * 更新微信红包状态
     * 呵呵，又不需要存了，your sister !!
     *
     * @param state
     */
    private void setWXShareHBState(final int state) {
        mWebView.loadUrl("javascript:getSharePrizeCallback(" + state + ")");

    }

    // 设置WebView拦截里面的图片链接和查看原文
    private void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {

            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Slog.d("callback", "url-------" + url);
                //修改页面退出后 mWebView ==null的异常
                if (mWebView == null) {
                    return;
                }
                mWebView.loadUrl("javascript:changeFontSize("
                        + DeviceInfo.getSize() + ")");
                if (ConfigApi.isUseWebViewImageBlock)
                    mWebView.getSettings().setBlockNetworkImage(false);
                if (mWebSuccess) {
                    progress.goneLoading();
                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mFirstView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mDoneKvo.doDone();
                    share_imagebutton.setEnabled(true);
                    pairScrollView.setmTouchEnable(true);
                    pairScrollView.setVisibility(View.VISIBLE);
                    if (mCircleType == CIRCLE_TYPE_NEWS) {
                        mWebView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (null != mWebView) {
                                    mWebView.loadUrl("javascript:getNewsInfo()");
                                }
                            }
                        });
                    }
                    mWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mWebView) {
                                mWebView.requestLayout();
                                mWebView.scrollTo(0, mWebOffsety);
                            }
                        }
                    }, 200);
                    //更新微信红包状态
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("appname", ContextUtil.getAppId(DetailModuleActivity.this));
                jsonObject.addProperty("token", SYUserManager.getInstance().getToken());
                mWebView.loadUrl("javascript:handlerStatusEvent(" + jsonObject + ")");

                if (mCircleType == CIRCLE_TYPE_CIRCLE) {
                    mCircleFootData.doDone();
                } else if (mCircleType == CIRCLE_TYPE_NEWS) {
                    mNewsFootData.doDone();
                }

                new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        lvPost.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).sendEmptyMessageDelayed(111, 1000);
            }

            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                Slog.d("callback", "intercept url-----------" + url);
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                customShouldOverrideUrlLoading(url);    //内部锚点处理逻辑
//                Log.e("-----","----url --" + url);
                // 处理内部点击url拦截 在自定义webview中打开，指向jianxing－fan
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mWebSuccess = false;
                progress.showNetError();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {

            private String innerAnchor = "";    //h5 中内部锚点

            /**
             * 解决圈子页面中锚点无响应异常
             * @param view
             * @param isDialog
             * @param isUserGesture
             * @param resultMsg
             * @return
             */
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String url = result.getExtra();

                if (url != null && !url.equals(innerAnchor)) {
//                    view.loadUrl(url);
//                    innerAnchor = url;

                    customShouldOverrideUrlLoading(url);    //内部锚点处理逻辑

                }
                return false;
            }
        });
    }

    /**
     * 供shouldOverrideUrlLoading 和 onCreateWindow方法调用，用于处理内部锚点跳转
     *
     * @param url h5页面内部锚点链接
     */
    public void customShouldOverrideUrlLoading(String url) {
        if (!TextUtils.isEmpty(url)) {
            // Slog.d("callback", "url1----"+url);
            if (url.toLowerCase().startsWith("showimage")) {
                Slog.d("callback", "页面内部跳转");
                int imagepos = 0;
                try {
                    imagepos = Integer.parseInt(url.substring(
                            url.lastIndexOf("//") + 2, url.length()));
                    if (null == mImageUrls || mImageUrls.size() == 0)
                        mImageUrls = mainPosts.getImages();

                    if (mImageUrls.size() == 1
                            && "".equals(mImageUrls.get(0)))
                        mImageUrls = mainPosts.getImages();
                    if (mImageUrls != null && mImageUrls.size() > 0
                            && imagepos < mImageUrls.size()) {
                        Intent intent = new Intent();
                        intent.setClass(DetailModuleActivity.this,
                                TouchGalleryActivity.class);
                        TouchGallerySerializable tg = new TouchGallerySerializable();
                        tg.setItems(mImageUrls);
                        tg.setClickIndex(imagepos);
                        Bundle extras = new Bundle();
                        extras.putSerializable("touchGalleryItems", tg);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                } catch (Exception e) {

                }
            } else if (url.endsWith("#extractnone")) {// 正文抽取失败
                if (!startedSrc) {
                    startedSrc = true;
                    toStartSrcPage(url.replaceAll("#extractnone", ""),
                            true);
                }
            } else if (containsUGC(url)) {
                Intent webViewIntent = new Intent();
                webViewIntent.setClass(DetailModuleActivity.this,
                        WebSrcViewActivity.class);
                webViewIntent
                        .putExtra(WebSrcViewActivity.PAGE_URL, url);
                startActivity(webViewIntent);
            } else {
                toStartSrcPage(url, false);
            }
        }
    }

    public void toStartSrcPage(String url, boolean isClose) {
        Intent intent = new Intent(this, WebSrcViewActivity.class);
        Bundle bundle = new Bundle();

        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.url_$eq(url);
        bundle.putSerializable(WebSrcViewActivity.ITEM_INFO, searchResultItem);

        intent.putExtras(bundle);
        startActivity(intent);
        if (isClose) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    /**
     * 处理signId - 5.2.0之前由js回调传到客户端，5.2.0使用模板之后直接在模板接口里返回
     */
    private void handleSignId(String signId) {  //模板接口直接返回signId,不再走词逻辑
        if (!mWebSuccess) {   //如果webview加载失败了，就不再加载评论列表，防止在某些手机上出现两个加载失败
            return;
        }
        if (mCircleType == CIRCLE_TYPE_NEWS) return; //如果是srp 则返回

        sign_id = signId;
        // json =
        // {java.lang.String@830063910344}"{"category":"get_signid","sign_id":"09185d307a1d8e1b89a27db5027751ec"}"
        String url = null;
        try {
            url = URLEncoder.encode(
                    "http://interest.zhongsou.com?sign_id=" + sign_id
                            + "&blog_id=" + mBlogId + "&sign_info="
                            + mBlogId + "&srpid=" + mSrpId + "&srpword="
                            + mKeyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mParamUrl = url;
        postAdapter.setmUrl(mParamUrl);
        if (!isFromEdit) {//如果是从编辑回来的，这个方法不应该调用
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list_progress.showLoading();
                    if (mSecondSuccess) {
//                        getCommentCount();// 获取顶，赞，打赏信息数量
                        getCommentList(false);
                    }
                }
            });
        } else {
            //如果是从编辑回来的，就在2秒后将其重置

            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFromEdit = false;
                }
            }, 2000);
        }

        mTouchTitle = false;
    }

    private void setAdapterData(String mainDate, String mainName) {
        postAdapter.setMain_title(mTitle);
        postAdapter.setMain_images(imgs);
        postAdapter.setMain_decsription(mContent);
        postAdapter.setMain_date(mainDate);
        postAdapter.setMain_name(mainName);
    }

    private void setAdapterData(TemplateShowData showData) {
        postAdapter.setMain_title(showData.getTitle());
        postAdapter.setMain_date(showData.getDatetime());
        postAdapter.setMain_name(showData.getSource());
        postAdapter.setMain_images(imgs);
        postAdapter.setMain_decsription(mContent);
        mTouchShareOrEdit = true;

        templatePath = showData.getTemplateVersion().replaceAll("!", File.separator);
    }

    @Override
    public void onJSClick(JSClick click) {
        // {"category":"interest","interest_id":"兴趣圈id","interest_logo":"兴趣圈logo","keyword":"关键词","srpId":"",type:"home"}

        if (click.isIsget_signid()) {// 获取到signid
            handleSignId(click.getSign_id());
            if (mCircleType == CIRCLE_TYPE_CIRCLE) {

                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mWebView) {
                            mWebView.loadUrl("javascript:getBlogInfo()");
                        }
                    }
                });
            }
        } else if (click.isIsget_blog_info()) {// 获取主贴内容 TODO 删除
            getMainPost(click);
            mShareImageUrls = mainPosts.getImages();
            mTitle = mainPosts.getTitle();
            mContent = click.description();
            if (TextUtils.isEmpty(mTitle)) {
                mTitle = mContent.length() > 50 ? mContent.substring(0, 50)
                        : mContent;
            }
            mTouchShareOrEdit = true;
            getImage();
            setAdapterData(click.getCreate_time(), click.getNickname());

            UpEventAgent.onGroupView(this, "souyue", mInterestId + "."
                    + mKeyword, "", mainPosts != null ? mainPosts.getTitle()
                    : mTitle, mBlogId + "");
            UpEventAgent.onZSGroupView(DetailModuleActivity.this,
                    mInterestId + "." + mKeyword, mainPosts != null ? mainPosts.getTitle() : mTitle, mBlogId + "", mChannel);
        } else if (click.isIsget_news_info()) { //TODO 删除？
            if (TextUtils.isEmpty(mTitle)) {
                mTitle = click.title();
            }
            if (TextUtils.isEmpty(mContent)) {
                mContent = click.getBrief();
            }
            if (TextUtils.isEmpty(mContent)) {
                mContent = click.description();
            }

            if (mShareImageUrls == null || mShareImageUrls.size() == 0) {
                imgs = click.getImages();
                mShareImageUrls = new ArrayList<String>();
                if (StringUtils.isNotEmpty(imgs)) {
                    String[] igs = imgs.split(",");
                    for (int i = 0; i < igs.length; i++) {
                        mShareImageUrls.add(igs[i]);
                    }
                    getImage();
                }

            }

            setAdapterData(click.getDate(), click.getSource());

            mTouchShareOrEdit = true;

            String type = (mItem != null && StringUtils.isNotEmpty(mItem.getCategory())) ?
                    mItem.getCategory() : ConstantsUtils.VJ_NEW_SEARCH;

            UpEventAgent.onNewsView(this, "souyue", mChannel, mKeyword, mSrpId,
                    mTitle, mSourceUrl);
            UpEventAgent.onZSNewsView(DetailModuleActivity.this,
                    mChannel, mSrpId, mTitle,
                    mSourceUrl,
                    type, "0");

        } else if (click.getIsGetAdList()) {// 获取广告列表
            processGetAdList(click);
        } else if (click.isAdClick()) {// 点击广告
            try {
                processClickAD(click);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (click.isInterest()) {// 打开兴趣圈 当类型是interest
            try {
                processInterest(click);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (click.isSrp()) {// 当类型是SRP
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.DETAIL_RELATIVED_CLICK);    //Umeng
            IntentUtil.gotoSrp(this, click, SRP_REQUEST_CODE);
        } else if (click.isAdminMoreOper()) {   //圈主工具 - 更多操作
            if (!mHeadSuccess) {
                SouYueToast.makeText(this, R.string.wait_data, SouYueToast.LENGTH_SHORT).show();
                return;
            }
            setAdminMoreViable(isToTop, isPrime, mOptionRoleType);
            showAdminMoreMenu();
        } else if (click.isPasePage()) {    //跳转到详情页
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.DETAIL_RELATIVED_CLICK);    //Umeng
            mItem.setInterestId(mInterestId);
            mItem.setInterestType(interestType);
            mItem.setOptionRoleType(mOptionRoleType);
            SearchResultItem is = DetailItem.SearchResultToDetailItem(mItem);
            is.setStatisticsJumpPosition(null);     //将来源置空，避免IMAndWebJump方法走到第二分支，避免传入pushId
            ImJump2SouyueUtil.IMAndWebJump(this, click, is);
        } else if (click.isShareToWX()) {//从webview中分享到微信
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    loadData(ShareMenuDialog.SHARE_TO_WEIX);
                }
            });
            UpEventAgent.onZSDetailWXShare(this);   //ZSSDK
        } else if (click.isShareToWXFriend()) {//从webview中分享到微信朋友圈
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    loadData(ShareMenuDialog.SHARE_TO_FRIENDS);
                }
            });
            UpEventAgent.onZSDetailFriendShare(this);   //ZSSDK
        } else if (click.isGetSharePrize()) {   //领取红包
            UpEventAgent.onZSDetailMoney(this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getSharePrize();
                    } catch (Exception e) {

                    }
                }
            });
        } else if (click.isAddCircleSub()) {
            //订阅圈子
            if (jsInsterestId != null && jsInsterestId.equals(click.getInterest_id())) {
                return;
            }
            jsInsterestId = click.getInterest_id();
            jsimageLogo = click.getInterestLogo();
            jsKeyword = click.keyword();
            jsSrpId = click.srpId();
            loadSaveRecomentCircles(SYUserManager.getInstance().getToken(), jsInsterestId, ZSSdkUtil.NEWSDETAIL_SUBSCRIBE_BUTTOM);


        } else if (click.isAddSrpSub()) {
            //防止重复调用
            if (jsSrpId != null && jsSrpId.equals(click.srpId())) {
                return;
            }
            //订阅srp
            SubAddReq req = new SubAddReq(HttpCommon.SUB_ADD_REQUEST, this);
            jsSrpId = click.srpId();
            jsKeyword = click.keyword();
            // Toast.makeText(this,"嗲用srpjs",Toast.LENGTH_LONG).show();
            req.addParameters(jsKeyword, jsSrpId, -1 + "", "", "srp", ZSSdkUtil.NEWSDETAIL_SUBSCRIBE_BUTTOM);
            mMainHttp.doRequest(req);
        } else {
            ImJump2SouyueUtil.IMAndWebJump(this, click, null);
        }
    }

    /**
     * 获取微信积分
     */
    private void getSharePrize() {
        if (mWxShareDlg == null) {
            mWxShareDlg = new WXShareEnveDialog(this);
//            mWxShareDlg.setStateListener(new WXShareEnveDialog.IGetJFInterface() {
//                //积分领取回调
//                @Override
//                public void onGetJF(int state) {
//                    if (state == WXShareBean.STATE_ALREADYGET) {
//                        setWXShareHBState(WXShareHBUtils.STATE_HASGET);
//                        WXShareHBUtils.setShareState(WXShareHBUtils.STATE_HASGET);
//                    } else {
//                        setWXShareHBState(WXShareHBUtils.STATE_NOTGETYET);
//                        WXShareHBUtils.setShareState(WXShareHBUtils.STATE_NOTGETYET);
//                    }
//                }
//            });
        }
        if (mWxShareDlg.isShowing()) {
            mWxShareDlg.dismiss();
        }
        mWxShareDlg.show();
    }


    private void getMainPost(JSClick jSClick) {
        mainPosts = new Posts();
        mainPosts.setTitle(jSClick.title());
        mainPosts.setContent(jSClick.getContent());
        mainPosts.setImage_url(jSClick.getImage_url());
        mainPosts.setBlog_id(mBlogId);
        mainPosts.setMblog_id(mBlogId);
        mainPosts.setPosting_state(jSClick.getPosting_state());
        posState = jSClick.getPosting_state();
        String[] images = null;
        List<String> imageList = null;
        if (jSClick.getImages() != null && jSClick.getImages().length() > 0) {
            images = jSClick.getImages().split(",");
        }
        if (images != null) {
            imageList = new ArrayList<String>();
            for (int i = 0; i < images.length; i++) {
                imageList.add(images[i]);
            }
        }

        mainPosts.setImages(imageList);
        mainPosts.setNickname(jSClick.getNickname());
        mainPosts.setCreate_time(jSClick.getCreate_time());
        mainPosts.setUpdate_time(jSClick.getUpdate_time());
    }

    private void getMainPost(TemplateShowData showData,
                             TemplateKeyData keyData,
                             CWidgetHead head) {
        mainPosts = new Posts();
        mainPosts.setTitle(showData.getTitle());
        mainPosts.setContent(TemplateUtils.delHTMLTag(showData.getContent()));
//        mainPosts.setImage_url(jSClick.getImage_url());
        mainPosts.setBlog_id(mBlogId);
        mainPosts.setMblog_id(mBlogId);

//        mainPosts.setPosting_state(jSClick.getPosting_state());
//        posState = jSClick.getPosting_state();
//        String[] images = null;
//        List<String> imageList = null;
//        if (jSClick.getImages() != null && jSClick.getImages().length() > 0) {
//            images = jSClick.getImages().split(",");
//        }
//        if (images != null) {
//            imageList = new ArrayList<String>();
//            for (int i = 0; i < images.length; i++) {
//                imageList.add(images[i]);
//            }
//        }

        mainPosts.setImages(keyData.getImageList());
        if (head != null) {
            mainPosts.setNickname(head.getNickName());
        }
        mainPosts.setCreate_time(showData.getDatetime());
//        mainPosts.setUpdate_time(jSClick.getUpdate_time());

        mShareImageUrls = mainPosts.getImages();

        if (StringUtils.isEmpty(mContent)) {
            mContent = TemplateUtils.delHTMLTag(showData.getContent());
        }
        getImage(); //获得分享图片
    }

    private void processGetAdList(JSClick click) {
        if (click != null) {
//            http.getAdList(click.keyword(), click.srpId(),
//                    ADInfo.SRP_DETAIL_PAGE_LOCATION, "");
            AdListRequest adListRequest = new AdListRequest(HttpCommon.SRP_AD_LIST_REQUEST, this);
            adListRequest.addParams(click.keyword(), click.srpId(), ADInfo.SRP_DETAIL_PAGE_LOCATION, "");
            mMainHttp.doRequest(adListRequest);
        }
    }

    private void processClickAD(JSClick click) throws JSONException {
        // onJSClick://{“category”:”AdClick”,"keyword":"关键词",”srpId”:””,"url":"列表中的url字段","event":"列表中的event字段","type":"列表中的type字段"}
        JsonObject obj = new JsonObject();
        obj.addProperty("keyword", click.keyword());
        obj.addProperty("srpId", click.srpId());
        obj.addProperty("url", click.url());
        obj.addProperty("event", click.getEvent());
        obj.addProperty("type", click.getType());
        clickAd(obj.toString());
    }

    private void processInterest(JSClick obj) throws JSONException {
        long interestid = Long.valueOf(obj.getInterest_id());
        String interestlogo = obj.getInterest_logo();
        String keyword = obj.keyword();
        String srpid = obj.srpId();
        String type = obj.getType();
        switch (type) {
            case "home":
                UIHelper.showCircleIndex(this, srpid, keyword, keyword,
                        interestlogo, CIRCLE_REQUEST_CODE);
                break;
            case "card":
                IntentUtil.gotoSecretCricleCard(this, interestid, 0, CIRCLE_REQUEST_CODE);
                break;
            default:
                IntentUtil.gotoSecretCricleCard(this, interestid, 0, CIRCLE_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void scrollChanged(int l, int t, int ol, int ot) {
        if (mWebView.getContentHeight() * mWebView.getScale()
                - (mWebView.getHeight() + mWebView.getScrollY()) == 0) {
            isWebViewScrollBottom = true;
            if (t - ot > 5 && t - ot < 1000 && !pairScrollView.getTouchAble()) {
                pairScrollView.startScroll(150);
            }
            // 已经处于底端
        } else {
            isWebViewScrollBottom = false;
        }

    }

    @Override
    public void measureChanged(int specwidth, int specheight) {

        if (pairScrollView.getScrollY() > 0 && !pairScrollView.getmInputOut()
                && isWebViewScrollBottom) {
            mWebView.scrollTo(
                    0,
                    (int) (mWebView.getContentHeight() * mWebView.getScale() - mWebView
                            .getHeight()));
        }
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        int id = _request.getmId();
        switch (id) {
//            case CDetailHttp.HTTP_GET_COMMENT_COUNT:    //===5/6
//                HttpJsonResponse res = (HttpJsonResponse) _request.getResponse();
//                getCommentCountSuccess(res);
//                break;
            case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID: //===7
                List<Object> comls = (List<Object>) _request.getResponse();
                getCommentListSuccess((List<CommentsForCircleAndNews>) comls.get(CDetailHttp.DETAIL_COMMENT_LIST_COMMENT),
                        (List<CommentsForCircleAndNews>) comls.get(CDetailHttp.DETAIL_COMMENT_LIST_HOT));
                lvPost.postInvalidate();
                break;
//            case CDetailHttp.HTTP_GET_SECOND_LIST:      //1
////                CWidgetSecondList detail = (CWidgetSecondList) _request.getResponse();
////                getSecondListSuccess(detail);
//                break;
//            case CDetailHttp.HTTP_GET_DETAIL_HEAD:      //4===5/6
//                CWidgetHead head = (CWidgetHead) _request.getResponse();
//                getWidgetHeadSuccess(head);
//                break;
            case CDetailHttp.HTTP_COMPLAIN:
                SouYueToast.makeText(this, "举报成功！", SouYueToast.LENGTH_SHORT)
                        .show();
                break;
            case HttpCommon.HOME_LIST_DISLIKE:
                DetailItem item = (DetailItem) _request
                        .getKeyValueTag(DisLikeRequest.HOME_PAGE_DISLIKE);
                BroadCastUtils.sendToDeleteSearchResultItemData(this,
                        mItem.getId());
                mDBHelper.deleteData(SYUserManager.getInstance().getUserId(),
                        item.getId());// 这里只用dataid,因为只有我的头条有删除
                SouYueToast
                        .makeText(this, "将减少为您推荐此类信息！", SouYueToast.LENGTH_SHORT)
                        .show();
                break;
            case HttpCommon.CIRCLE_DETAILBLOGPUSH_REQUESTID: // 帖子推送，返回成功
                tuiSongSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                saveRecomentCirclesSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_BLOG_TOP_ID:
                getToTopSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_BLOG_PRIME_ID:
                getPrimeSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_BLOF_TOP_CANCLE_ID:
                cancleTotopSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_BLOG_PRIME_CANCLE_ID:
                canclePrimeSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_BLOG_DELETE_ID:
                deletePostsSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                cancelCollectSuccess(_request.<HttpJsonResponse>getResponse());
                break;
//            case HttpCommon.CIRLCE_SHORT_URL_ID:    //3
////                shortURLSuccess(_request.<HttpJsonResponse>getResponse().getBodyString());
//                break;
            case HttpCommon.SRP_AD_LIST_REQUEST:
                getAdListSuccess(_request.<AdList>getResponse());
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse response = _request.getResponse();
                shareSuccess(response.getBodyLong("newsId"));
                break;
            case HttpCommon.SUB_ADD_REQUEST:
                subscribeAddSrpSuccess((HttpJsonResponse) _request.getResponse());
                break;
            case HttpCommon.SUB_ADDDEL_REQUEST:
            case HttpCommon.SUB_DELETE_REQUEST:
                subscribeDeleteSrpSuccess();
                break;
            case HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID:
                HttpJsonResponse res2 = (HttpJsonResponse) _request.getResponse();
                subscribeDeleteSuccess(res2);
                break;
            case HttpCommon.DETAIL_ADDDOWN_ID:
                commentDownSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                newFavoriteAddSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                commentDetailSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_ADDUP_ID:
                mUpDowning = false;
                commentUpSuccess();
                break;

            case HttpCommon.DETAIL_MODULE_NEWS:     //2
            case HttpCommon.DETAIL_MODULE_CIRCLE:

                HttpJsonResponse moduleRepose = (HttpJsonResponse) _request.getResponse();

                JsonObject jsonBody = moduleRepose.getBody();
                if (mCircleType == CIRCLE_TYPE_CIRCLE) {
                    mCircleFootData.doDone();
                } else if (mCircleType == CIRCLE_TYPE_NEWS) {
                    mNewsFootData.doDone();
                }
                handleTemplateData(jsonBody);   //处理模板数据

                break;

            case HttpCommon.DETAIL_CIRCLE_FOOT_REQUEST:    //模板 底部相关数据接口 - 圈子
            case HttpCommon.DETAIL_NEWS_FOOT_REQUEST:    //模板 底部相关数据接口 - 新闻
                HttpJsonResponse fooData = (HttpJsonResponse) _request.getResponse();
                JsonObject footJson = fooData.getBody();
                handleTemplateFootData(footJson);
                break;
            case HttpCommon.TEMPLATE_DOWN_REQUEST:    //模板版本管理
                HttpJsonResponse downResponse = (HttpJsonResponse) _request.getResponse();
                String downloadDir = downResponse.getBody().get("path").getAsString();
                downloadTemplate(downloadDir, templateVersion);
                break;
        }
    }

    /**
     * 处理模板需要显示的数据
     *
     * @param body
     */
    private void handleTemplateData(JsonObject body) {
        Gson gson = new Gson();
        TemplateData templateData = gson.fromJson(body, TemplateData.class);
        JsonObject jsonShowData = body.getAsJsonObject("showData");
        TemplateShowData showData = templateData.getShowData();   //模板显示数据
        TemplateKeyData keyData = templateData.getNotShowData();   //重要的数据比如blogid、keyword等
        CWidgetHead widgetHead = templateData.getWidgetHead();   //获得帖子头部信息，比如帖子作者等
        TemplateWidgetData widgetData = templateData.getWidgetData();   //二级导航数据

        handleTemplateKeyData(keyData);
        handleSecondList(widgetData);   //处理二级导航
        handleWidgetHead(widgetHead);

        setAdapterData(showData);
        if (StringUtils.isEmpty(showData.getContent())) {
            if (mCircleType == CIRCLE_TYPE_NEWS) {
                customShouldOverrideUrlLoading(mSourceUrl);
                finish();
            }
            return;
        }
        handleTemplateShowData(jsonShowData);   //处理显示数据 - Map
//        handleTemplateShowData(showData);   //处理显示数据 - 对象

        getMainPost(showData, keyData, widgetHead); //获得帖子相关信息，用于编辑

        changeSubState(subStatusFromJSON);
        checkTemplateVersion(showData.getTemplateVersion().split("!")[0]);  //获得版本号，并处理
    }

    /**
     * 处理模板显示数据
     *
     * @param showData 需要显示数据的对象
     */
    private void handleTemplateShowData(TemplateShowData showData) {
        if (showData == null) return;
        String title = showData.getTitle();
        String datetime = showData.getDatetime();
        String source = showData.getSource();
        String content = showData.getContent();
        String templateVersion = showData.getTemplateVersion();

        Map<String, String> blogData = new HashMap<>();
        blogData.put("title", title);
        blogData.put("datetime", datetime);
        blogData.put("source", source);
        blogData.put("content", content);
        blogData.put("templateVersion", templateVersion);

        String result = null;
        if (mCircleType == CIRCLE_TYPE_CIRCLE) {
            result = CMoudleParse.getInstance().bindMapData(blogData, CIRCLE_TYPE_CIRCLE);
        } else {
            result = CMoudleParse.getInstance().bindMapData(blogData, CIRCLE_TYPE_NEWS);
        }
        dealWithModule(result);
    }

    /**
     * 处理模板显示数据
     *
     * @param showData 需要显示对象的JsonObject
     */
    private void handleTemplateShowData(JsonObject showData) {
        Map<String, String> blogData = new HashMap<>();
        String jsonString = showData.toString();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator it = jsonObject.keys();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object objValue = jsonObject.get(key);
                if (objValue instanceof String) {
                    String value = (String) objValue;
                    if (value.length() == 0) value = "";
                    blogData.put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String result = null;
        if (mCircleType == CIRCLE_TYPE_CIRCLE) {
            result = CMoudleParse.getInstance().bindMapData(blogData, CIRCLE_TYPE_CIRCLE);
        } else if (mCircleType == CIRCLE_TYPE_NEWS) {
            result = CMoudleParse.getInstance().bindMapData(blogData, CIRCLE_TYPE_NEWS);
        }
        dealWithModule(result);
    }

    /**
     * 处理模板显示数据
     *
     * @param keyData
     */
    private void handleTemplateKeyData(TemplateKeyData keyData) {
        if (keyData == null) return;

        String signId = keyData.getSignId();
        if (signId != null) {
            mSignIdSuccess = true;
            handleSignId(signId);  //处理signId
        }

        mKeyword = keyData.getKeyword();
        mSrpId = keyData.getSrpId();
        mDetailType = keyData.getKeywordType();
        mInterestId = keyData.getInterestId();
        showAdminMoreOper = keyData.getShowAdminMoreOper();        //是否显示圈主工具“更多操作”
        interestType = String.valueOf(keyData.getInterestType());
        blogShareUrl = keyData.getShortUrl();
        subStatusFromJSON = keyData.getIsSubscribe();   //服务端返回的订阅状态

        mActionBarTitle.setText(mKeyword);

        if (postAdapter != null) {
            postAdapter.setKeyWord(mKeyword);
            postAdapter.setSrp_id(mSrpId);
//            adapter.setmDeatilType(mDetailType);
            postAdapter.setinterest_id(mInterestId);
        }
    }

    /**
     * 处理模板底部相关推荐等部分数据
     *
     * @param body
     */
    private void handleTemplateFootData(JsonObject body) {
        Gson gson = new Gson();
        TemplateFootData footInfo = gson.fromJson(body, TemplateFootData.class);
        TemplateCommentsInfo commentsInfo = footInfo.getCommentsInfo(); //评论接口内容

        String footData = footInfo.getHtmlString();
//        footData = "<div class=\"quan_boxbg\" data-iscircle=\"1\" data-interestid=\"272\" data-srpid=\"f479751542203f3e5f4d300ddacd1127\" data-interestlogo=\"http://souyue-xqq.b0.upaiyun.com/circlelogo/1509/0619/33/11441539228.jpg\" data-keyword=\"heyl公开圈\"><img src=\"images/quan_bg.jpg\" class=\"quan_bg\"><div class=\"quan_nr\"><ul class=\"quan_info\"><li class=\"quan_logo\"><img src=\"http://souyue-xqq.b0.upaiyun.com/circlelogo/1509/0619/33/11441539228.jpg\"></li><li class=\"quan_title\"><h3>heyl公开圈</h3> <p>heyl公开圈欢迎加入，哇哈哈哈。。。。</p></li></ul></div></div>";
//        footData = "<div class=\"share_redpack\"><div class=\"redpack_bt\"><span>分享得红包</span></div><div class=\"redpack_nr\"><div class=\"pack_an\" onclick=\"shareToWeixin()\"><a href=\"javascript:void(0);\" class=\"an1\" id=\"an1\"><i></i><span>微信好友</span></a></div><div class=\"pack_an\" onclick=\"shareToWXFriend()\"><a href=\"javascript:void(0);\" class=\"an2\" id=\"an2\"><i></i><span>微信朋友圈</span></a></div><div id=\"redbackInsert\" class=\"pack_an\" onclick=\"getShareRed()\"><a href=\"javascript:void(0);\" class=\"an3\" id=\"an3\"><i></i><span>领取红包</span></a></div></div></div>";
//        footData = "<section class=\"sy_sct_a\"><h3 class=\"div_lst_bt\">相关推荐</h3><div class=\"div_lst\"><ul><li onclick=\"javascript:openBlog('115660783');\"><a>好数据啊</a></li><li onclick=\"javascript:openBlog('115660769');\"><a>悦天下：假如捡到35万美元你就拍拍手</a></li><li onclick=\"javascript:openBlog('115660732');\"><a>老师为了向学生证明吸烟的害处，特意把从香烟中提取的尼古丁放在...</a></li></ul></div></section>";
        String requestJs = "javascript:render(\"" + footData + "\")";
        mWebView.loadUrl(requestJs);

        handleCommentCount(commentsInfo);

//        TemplateUtils.writeHtmlToSD(footData, "foot.txt");// 输出到sd 用于联调
    }

    private void dealWithModule(String result) {
        progress.goneLoading();
        mDoneKvo.doDone();

        mWebView.loadDataWithBaseURL(
                "file://" + TemplateUtils.getTemplatePath(this) + "/" + templatePath + "/#"
                        + MakeCookie.getDetailCookie(this),
                result, "text/html", "utf-8", null);

//        TemplateUtils.writeHtmlToSD(result, "content.html");    // 输出到sd 用于联调
    }

    @Override
    public void onHttpError(IRequest _request) {
        IHttpError error = _request.getVolleyError();
        int id = _request.getmId();
        switch (id) {
            case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID:
                if (pno > 1) {
                    lvPost.removeFooterView(footerView);
                    SouYueToast.makeText(DetailModuleActivity.this, "网络异常，请重试！",
                            Toast.LENGTH_SHORT).show();
                    needLoad = true;
                } else {
                    list_progress.showNetError();
                }
                break;
//            case CDetailHttp.HTTP_GET_SECOND_LIST:
//                list_progress.showNetError();
//                break;
            case CDetailHttp.HTTP_COMPLAIN:
                SouYueToast.makeText(DetailModuleActivity.this, "举报失败",
                        Toast.LENGTH_SHORT).show();
                break;
            case HttpCommon.CIRCLE_DETAILBLOGPUSH_REQUESTID: // 帖子推送请求
                boolean result = false;
                try {
                    //还是原来的逻辑，如果返回码不是200,就弹吐司
                    HttpJsonResponse response = _request.<HttpJsonResponse>getResponse();
                    if (response != null) {
                        result = response.getCode() != 200;
                    }
                } catch (Exception e) {
                    result = false;
                } finally {
                    if (result) {
                        SouYueToast.makeText(DetailModuleActivity.this, "网络异常，请重试！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                joinCircleFail(_request.getVolleyError());
                break;
            case HttpCommon.SUB_ADDDEL_REQUEST:
            case HttpCommon.SUB_DELETE_REQUEST:
                subIsRunning = false;
                break;
            case HttpCommon.CIRLCE_BLOG_PRIME_ID:
            case HttpCommon.CIRLCE_BLOG_TOP_ID:     //置顶
            case HttpCommon.CIRLCE_BLOF_TOP_CANCLE_ID:
            case HttpCommon.CIRLCE_BLOG_PRIME_CANCLE_ID:    //加精
                HttpJsonResponse response = _request.<HttpJsonResponse>getResponse();
                if (response != null) {
                    doFaild(response.getCode());
                }
                break;
            case HttpCommon.CIRLCE_BLOG_DELETE_ID:
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                break;
            case HttpCommon.SUB_ADD_REQUEST:
                joinNewsFail(_request.getVolleyError());
                break;
            case HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID:
                subIsRunning = false;
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                if (circleFollowDialog != null) {
                    circleFollowDialog.dismissProcessDialog();
                }
                if (error.getErrorCode() < 700) {
                    Toast.makeText(this, "评论失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                if (error.getErrorCode() < 700) {
                    UIHelper.ToastMessage(this, "收藏失败");
                }
                break;
            case HttpCommon.DETAIL_ADDDOWN_ID:
            case HttpCommon.DETAIL_ADDUP_ID:
                mUpDowning = false;
                SouYueToast.makeText(this, R.string.networkerror, Toast.LENGTH_LONG).show();
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                if (error.getErrorCode() == 600) {
                    HttpJsonResponse json = error.getJson();
                    SouYueToast.makeText(this, json.getBodyString(), Toast.LENGTH_LONG).show();
                }
                break;
            case HttpCommon.DETAIL_MODULE_CIRCLE:
            case HttpCommon.DETAIL_MODULE_NEWS:     //-1
                progress.showNetError();
                break;
            default:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void joinNewsFail(IHttpError error) {
        if (error.getErrorType() == CSouyueHttpError.TYPE_SERVER_ERROR) {
            //通知js
            if (mSrpId.equals(jsSrpId)) {
                JsonObject obj = new JsonObject();
                obj.addProperty("result", 0);
                obj.addProperty("srp_id", jsSrpId);
                mWebView.loadUrl("javascript:addSrpSubCallback(" + obj + ")");
                jsSrpId = null;
                jsKeyword = null;
            }

        }
        subIsRunning = false;
    }

    private void joinCircleFail(IHttpError error) {
        if (error.getErrorType() == CSouyueHttpError.TYPE_SERVER_ERROR) {
            Toast.makeText(DetailModuleActivity.this, "加入圈子失败", Toast.LENGTH_SHORT)
                    .show();
            //通知js
            if (String.valueOf(mInterestId).equals(jsInsterestId)) {
                JsonObject obj = new JsonObject();
                obj.addProperty("result", 0);
                obj.addProperty("interest_id", jsInsterestId);
                mWebView.loadUrl("javascript:addCircleSubCallback(" + obj + ")");
                jsInsterestId = null;
                jsimageLogo = null;
                jsKeyword = null;
            }
        }
        subIsRunning = false;
    }

    /**
     * 处理失败返回码
     *
     * @param code
     */
    private void doFaild(int code) {
        if (code != 200) {
            Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressClick(null);
    }

    @Override
    public void onBackPressClick(View view) {
        finishWithResult();
    }

    public void commentDetailSuccess(HttpJsonResponse res) {
        UIHelper.ToastMessage(this, R.string.comment_detail_success);
        // 成功后清空保存的数据
        SYSharedPreferences.getInstance().putString(mUrl + "_text", "");
        SYSharedPreferences.getInstance().putString(mUrl + "_img", "");
        CommentsForCircleAndNews published = circleFollowDialog.getPublishPosts();
        long comment_id = 0;
        try {
            comment_id = res.getBody().get("comment_id").getAsLong();
        } catch (Exception e) {
            //错误处理
            UIHelper.ToastMessage(this, "评论失败");
            circleFollowDialog.dismissProcessDialog();
            return;
        }

        published.setContent(circleFollowDialog.getPublishPosts().getContent());
        published.setCreate_time(circleFollowDialog.getPublishPosts().getCreate_time());
        published.setComment_id(comment_id);
        follow_post_count.setText(Integer.parseInt(follow_post_count.getText()
                .toString()) + 1 + "");
        hasChanged = true;
        follow = true;

        if (mCircleType == CIRCLE_TYPE_NEWS) {
            published.setImage_url(SYUserManager.getInstance().getImage());
            if (AnoyomousUtils.getAnoyomouState(mInterestId + "")) {
                published.setNickname("匿名用户");
                published.setIs_anonymity(1);
                published.setImage_url("");
            } else if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) {
                published.setNickname(getResources().getString(R.string.user_guest));
            } else if (is_private) { // isprivate 存在，表示是圈子。走圈子匿名流程
                published.setImage_url(image);
                published.setNickname(nickname);
                published.setIs_private(1);
            } else {
                published.setNickname(SYUserManager.getInstance().getName());
            }
        } else {
            if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) { //如果是游客显示默认头像和"游客"
                published.setNickname(StringUtils.isNotEmpty(nickname) ? nickname : getResources().getString(R.string.user_guest));
                published.setImage_url("");
                //如果是匿名状态
            } else if (AnoyomousUtils.getAnoyomouState(mInterestId + "")) {
                published.setNickname("匿名用户");
                published.setIs_anonymity(1);
            } else if (mOptionRoleType == Constant.ROLE_NONE) {  //登录的非圈成员，显示搜悦昵称和搜悦头像
                published.setImage_url(StringUtils.isNotEmpty(image) ? image : SYUserManager.getInstance().getImage());
                published.setNickname(StringUtils.isNotEmpty(nickname) ? nickname : SYUserManager.getInstance().getName());
            } else if (is_private) {
                published.setImage_url(image);
                published.setNickname(nickname);
                published.setIs_private(1);
            } else {
                published.setImage_url(StringUtils.isNotEmpty(image) ? image : "");
                published.setNickname(StringUtils.isNotEmpty(nickname) ? nickname : "匿名用户");
            }

        }
        published.setGood_num("0");
        published.setSrp_id(mSrpId);
        published.setType(mDetailType);
        published.setRole(mOptionRoleType);
        published.setIs_current_comment(1);
        circleFollowDialog.dismissProcessDialog();
        postsList.add(postsListHot.size(), published);
        postAdapter.notifyDataSetChanged();
        mHaveNoComment.setVisibility(View.INVISIBLE);
        // 统计
        switch (mCircleType) {
            case CIRCLE_TYPE_NEWS:
                UpEventAgent.onNewsComment(this, mChannel, mKeyword, mSrpId,
                        mTitle, mSourceUrl);
                break;
            case CIRCLE_TYPE_CIRCLE:
                UpEventAgent.onGroupComment(this, mInterestId + "." + "", "",
                        mainPosts.getBlog_id() + "");
                break;
            default:
        }
    }

    private void jumpTakePhoto() {
        if (circleFollowDialog.getImgLen() > 9) {
            Toast.makeText(this, "最多选择9张图片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            imageFileUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
            if (imageFileUri != null) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                if (Utils.isIntentSafe(DetailModuleActivity.this, i)) {
                    startActivityForResult(i, 2);
                } else {
                    SouYueToast.makeText(DetailModuleActivity.this,
                            getString(R.string.dont_have_camera_app),
                            SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(DetailModuleActivity.this,
                        getString(R.string.cant_insert_album),
                        SouYueToast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            SouYueToast.makeText(DetailModuleActivity.this,
                    getString(R.string.cant_insert_album),
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

    private void showJoinInterest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您还不是该圈的成员，是否立即加入？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        // 判断是私密圈还是公开圈，进行跳转
                        if (mOptionRoleType == Constant.ROLE_NONE
                                && interestType.equals("1")) {
                            IntentUtil.gotoSecretCricleCard(
                                    DetailModuleActivity.this, mInterestId, 0);
                            return;
                        } else {
                            loadSaveRecomentCircles(
                                    SYUserManager.getInstance().getToken(),
                                    mInterestId + "",
                                    ZSSdkUtil.OTHER_SUBSCRIBE_MENU);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void saveRecomentCirclesSuccess(HttpJsonResponse res) {
        if (res.getBody().get("state").getAsInt() == 1) {
            // 加圈后获取在这个圈子的昵称和头像
            Toast.makeText(DetailModuleActivity.this, "加入圈子成功", Toast.LENGTH_SHORT)
                    .show();
            sendSubStateBroadcast(this);
            if (jsInsterestId != null && jsInsterestId.equals(mInterestId + "") || jsInsterestId == null) {
                btn_subscribe.setImageResource(R.drawable.srp_no_subscribe_selector);
            }
            isSubscribeSuccess = true;
            isUpdateSuccess = true;
            isSubcribe = true;

            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_DETAIL_CLICK);
            mOptionRoleType = Constant.ROLE_NORMAL;
            SYSharedPreferences.getInstance().putBoolean(
                    SYSharedPreferences.KEY_UPDATE, true);
            //数据库操作
            if (jsInsterestId == null || jsInsterestId != null && jsInsterestId.equals(mInterestId + "")) {
                SuberedItemInfo info = new SuberedItemInfo();
                info.setId(res.getBody().get("interest_id").getAsInt());
                info.setTitle(mKeyword);
                info.setCategory("interest");
                info.setSrpId(res.getBody().get("new_srpid").getAsString());
                info.setKeyword(mKeyword);
                info.setType("0");
                suberDao.addOne(info);
                // 统计
                UpEventAgent.onGroupJoin(this, mInterestId + "." + "", "");
            } else {
                SuberedItemInfo info = new SuberedItemInfo();
                // info.setId(Long.parseLong(jsInsterestId));
                info.setId(res.getBody().get("interest_id").getAsInt());
                info.setTitle(jsKeyword);
                info.setCategory("interest");
                info.setImage(jsimageLogo);
                // info.setSrpId(jsSrpId);
                info.setSrpId(res.getBody().get("new_srpid").getAsString());
                info.setKeyword(jsKeyword);
                info.setType("0");
                suberDao.addOne(info);
            }

            // sendSubStateBroadcast(mContext);
            //通知js
            JsonObject obj = new JsonObject();
            obj.addProperty("result", 1);
            if (jsInsterestId == null) {
                obj.addProperty("interest_id", mInterestId);
            } else {
                obj.addProperty("interest_id", jsInsterestId);
            }
            mWebView.loadUrl("javascript:addCircleSubCallback(" + obj + ")");
            jsInsterestId = null;
            jsimageLogo = null;
            jsSrpId = null;
            jsKeyword = null;
        }

        subIsRunning = false;
    }

    public void clickAd(String json) {
        try {
            final ADInfo adinfo = new Gson().fromJson(json, ADInfo.class);
            if (adinfo == null || StringUtils.isEmpty(adinfo.url()))
                return;

            doDownload(adinfo);

        } catch (JsonParseException jpe) {
        }
    }

    private void doDownload(final ADInfo adinfo) {
        final Intent intent = new Intent();
        if (AdListItem.DOWNLOAD_AD.equals(adinfo.type())) {
            if (!CMainHttp.getInstance().isWifi(MainApplication.getInstance())) {
                new DownloadAlert(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.setClass(MainApplication.getInstance(),
                                DownloadService.class);
                        intent.putExtra("url", adinfo.url());
                        intent.putExtra("event", adinfo.event());
                        intent.putExtra("keyword", adinfo.keyword());
                        intent.putExtra("srpId", adinfo.srpId());
                        intent.putExtra("page", ADInfo.SRP_DETAIL_PAGE_LOCATION);
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
                intent.putExtra("page", ADInfo.SRP_DETAIL_PAGE_LOCATION);
                MainApplication.getInstance().startService(intent);
            }

        } else {
            String url = adinfo.event();
            if (url != null) {
                // 广告点击调用接口 广告位置==>1：首页 2：SRP首页 3：SRP详情页面
//                http.adClick(adinfo.keyword(), adinfo.srpId(),
//                        ADInfo.SRP_DETAIL_PAGE_LOCATION, "", url);
                AdClickRequest adClickRequest = new AdClickRequest(HttpCommon.SRP_AD_CLICK_REQUEST, this);
                adClickRequest.addParams(adinfo.keyword(), adinfo.srpId(), ADInfo.SPR_PAGE_LOCATION, "", url);
                mMainHttp.doRequest(adClickRequest);
            }
            intent.setClass(this, WebSrcViewActivity.class);
            intent.putExtra(WebSrcViewActivity.PAGE_URL, adinfo.url());
            startActivity(intent);
        }
    }

    public void getAdListSuccess(final AdList list) {
        if (list != null) {
            mWebView.loadUrl("javascript:ad_data(" + list.list() + ")");
        }
    }

    /**
     * * 初始化屏幕亮度值      
     */
    private void getBrightness() {
        brightness = BrightnessUtil.getScreenBrightness(this);
    }

    private void stopAutoBright() {
        // 如果开启了自动亮度调节，则关闭之 
        BrightnessUtil.stopAutoBrightness(this);
    }

    private boolean isAutoBright() {
        return BrightnessUtil.isAutoBrightness(this);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float angle = Math.abs(velocityY / velocityX);
        if (velocityX > 1000 && angle < 0.27) {
            Intent intent = new Intent();
            intent.putExtra("isSubscribeSuccess", isSubscribeSuccess);
            intent.putExtra("isLogin", isLogin);
            intent.putExtra("isQuite", isQuite);
            setResult(RESULT_OK, intent);
            this.finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }
        if (velocityX < -500 && angle < 0.27) {
            if (mHsvDatas != null) {
                if (mHsvWidgets == null) {                 //mHsvWidgets的初始化时放在二级导航回来的，所以如果二级导航没回来是偶滑动，会出现空指针
                    return true;
                }
                int curPos = mHsvWidgets.getCurrentItem();
                if (curPos == mHsvDatas.size() - 1) {
                    return true;
                }
                TemplateWidgetNavData navigationBar = mHsvDatas.get(curPos + 1);
                if (mDetailType == DETAIL_TYPE_NEWS || mDetailType == KEYWORD_CATE_SPECIAL || mDetailType == DETAIL_TYPE_RSS) {
                    startActivityToSrp(navigationBar.getUrl(),
                            navigationBar.getTitle(), navigationBar.getMd5());
                } else {
                    showCircleIndex(DetailModuleActivity.this, mSrpId, mKeyword,
                            mKeyword, interest_logo, navigationBar.getTitle(),
                            navigationBar.getMd5(), 0x3);
                }
                return true;
            }
            UpEventAgent.onDetailWidget(DetailModuleActivity.this);
        }
        return false;
    }

    /**
     * 发送订阅广播，改变订阅图标状态
     *
     * @param activity
     */
    public static void sendSubStateBroadcast(Activity activity) {
        if (null != activity) {
            Intent myIntent = new Intent();
            myIntent.setAction("ChangeSubscribeState");
            activity.sendBroadcast(myIntent);
        }
    }

    /**
     * @param newTemplateVersion 比如：module_1
     * @return
     */
    private void checkTemplateVersion(String newTemplateVersion) {
        templateVersion = newTemplateVersion;
        final int newVersionNum = Integer.parseInt(newTemplateVersion.charAt(newTemplateVersion.length() - 1) + "");    //得到最后一位的数字
        int oldVersionNum = CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, "templateVersion", 1);

        if (newVersionNum > oldVersionNum) {
            // 下载接口：http://103.29.134.224/d3api2/detail/template.util.data.groovy?type=templatePath
            // 下载路径：http://103.29.134.224/static/template/zip/ + 模板版本.zip
//           downloadTemplate();
            TemplateDownRequest.send(HttpCommon.TEMPLATE_DOWN_REQUEST, this);
        } else {
            // do noting
        }
    }

    static class UnZipTemplateHandler extends Handler {
        private DetailModuleActivity activity;
        private String newTemplateName;
        private int newVersionNum;

        public UnZipTemplateHandler(DetailModuleActivity activity, String newTemplateName, int newVersionNum) {
            this.activity = activity;
            this.newTemplateName = newTemplateName;
            this.newVersionNum = newVersionNum;
        }

        @Override
        public void handleMessage(Message msg) {
            TemplateUtils.unZipTemplate(
                    TemplateUtils.getTemplatePath(activity) + File.separator + newTemplateName,
                    TemplateUtils.getTemplatePath(activity));
            CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, "templateVersion", newVersionNum);
        }
    }

    /**
     * @param downloadDir        服务端返回的url前缀 例：http://61.135.210.239:8888/d3api2/template/zip/
     * @param newTemplateVersion
     */
    private void downloadTemplate(String downloadDir, String newTemplateVersion) {
        final String newTemplateName = newTemplateVersion + ".zip";
        final int newVersionNum = Integer.parseInt(newTemplateVersion.charAt(newTemplateVersion.length() - 1) + "");

        final String downloadPath = downloadDir + newTemplateName;
        //测试链接： http://61.135.210.239:8888/d3api2/template/zip/module_2.zip

        final UnZipTemplateHandler handler = new UnZipTemplateHandler(DetailModuleActivity.this, newTemplateName, newVersionNum);

        final IVolleyLoadResponse iVolleyLoadResponse = new IVolleyLoadResponse() { //下载进度监听接口
            @Override
            public void onHttpProcess(long _totle_length, long _cur_length) {
                //计算百分比
                long percent = (long) (100.0 * _cur_length / _totle_length);
                String strPercent = percent + "%";
                if (percent >= 100L) {    //下载完成
                    handler.sendEmptyMessageDelayed(0, 3000);
                } else {  //下载未完成
                    // do noting
                }
            }
        };

        IVolleyResponse iVolleyResponse = new IVolleyResponse() {   //下载状态监听接口
            @Override
            public void onHttpResponse(IRequest _request) {

            }

            @Override
            public void onHttpError(IRequest _request) {
                CMainHttp.getInstance().doDownload(0,
                        TemplateUtils.getTemplatePath(DetailModuleActivity.this),
                        downloadPath,
                        iVolleyLoadResponse,
                        this);
            }

            @Override
            public void onHttpStart(IRequest _request) {

            }
        };

        CMainHttp.getInstance().doDownload(0,
                TemplateUtils.getTemplatePath(this),
                downloadPath,
                iVolleyLoadResponse,
                iVolleyResponse);
    }

    /**
     * 更改订阅状态,
     *
     * @param isSubscribe 0: 未订阅; 1: 订阅; -1:数据非法,隐藏订阅按钮
     */
    private void changeSubState(int isSubscribe) {
        //查询数据库 设置详情页的订阅状态
        SuberedItemInfo infoFromDb = null;
        try {
            infoFromDb = suberDao.queryOne(mSrpId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infoFromDb != null) {
            btn_subscribe.setImageResource(R.drawable.srp_no_subscribe_selector);
            isSubcribe = true;
        } else {    //返回为空时 根据接口数据处理
            if (isSubscribe == 1) {   //已订阅
                btn_subscribe.setImageResource(R.drawable.srp_no_subscribe_selector);
                isSubcribe = true;
            } else if (isSubscribe == 0) {     //未订阅
                btn_subscribe.setImageResource(R.drawable.srp_subscribe_selector);
                isSubcribe = false;
            } else if (isSubscribe == -1) {  // -1 : 数据非法 则隐藏订阅按钮
                btn_subscribe.setVisibility(View.GONE);
            }
        }
    }

    /**
     * WebView 调试 使用chrome可以直接调试webView
     */
    private void debugWebView() {
        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }
}
