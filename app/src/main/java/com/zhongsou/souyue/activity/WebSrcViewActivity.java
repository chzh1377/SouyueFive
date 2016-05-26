package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.activity.ImFriendActivity;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.db.ReadHistoryHelper;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NewsCount;
import com.zhongsou.souyue.module.NewsDetail;
import com.zhongsou.souyue.module.PackageInfoMap;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.TitleBarSource;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.detail.AddFavoriteReq;
import com.zhongsou.souyue.net.detail.DetailUpReq;
import com.zhongsou.souyue.net.detail.NewsCountReq;
import com.zhongsou.souyue.net.detail.NewsDetailReq;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.TradeBusinessApi;
import com.zhongsou.souyue.service.LogService;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.CustomWebViewClient;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GetLocalCookieListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoShareListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoSrpListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ReadNovelContentListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ReadNovelDictionaryListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.SetLocalCookieListener;
import com.zhongsou.souyue.uikit.FavoriteAlert;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.Base64Utils;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.FastDoubleCliceUtils;
import com.zhongsou.souyue.utils.HttpUtil;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;
import com.zhongsou.souyue.view.MapPopuWindow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author fan 查看原文
 */
public class WebSrcViewActivity extends RightSwipeActivity
        implements
        IShareContentProvider,//
        OnClickListener,
        GotoSrpListener,
        GotoShareListener,//
        OnJSClickListener, PickerMethod, SetLocalCookieListener,
        GetLocalCookieListener, ReadNovelDictionaryListener,
        ReadNovelContentListener,
        ProgressBarHelper.ProgressBarClickListener {
    public static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).build();
    public static final String DEFAULT_PAGE = "http://www.zhongsou.com/";
    public static final String PAGE_URL = "source_url";
    public static final String PAGE_TYPE = "page_type";
    public static final String CALLBACK = "callback";

    public static final String HASFAVORITED = "hasFavorited";
    public static final String ITEM_INFO = "searchResultItem";
    public static final String RESULT_HASFAVORITED = "hasFavorited";
    public static final String RESULT_HASUP = "hasUp";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String GOTOIFRAGMENT = "gotoIfragment";

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private final static int JS_FILE_CHOOSER_REQUEST_CODE = 2;
    private final static int JS_FILE_CHOOSER_AND_CROP_REQUEST_CODE = 3;
    private final static int CROP_IMAGE_REQUEST_CODE = 4;

    public static final String SHARE_TYPE = "web";

    private String SUPPER_SHARE_URL;
    private String sourcePageUrl;
    private String page_type;
    private CustomWebView mWebView;
    private ProgressBar web_src_loadProgress;
    private ImageButton button_close;
    private ImageButton button_back;
    private RelativeLayout webView_parent;
    SearchResultItem sri;
    private ImageButton collect, iv_up, share;
    private RelativeLayout up;
    private boolean hasFavorited, hasUp;
    private String token = SYUserManager.getInstance().getToken();
    private long newsId = 0;
    private FavoriteAlert favoriteDialog;
    private String utype;
    private SsoHandler mSsoHandler;
    private boolean shareIsOk = false;
    private boolean webLoadOk = false;
    private Handler hr;
    private int upCount;
    private TextView upC;
    private ImageView img = null;
    private Bitmap imageBitmap;
    private ImageLoader imageLoader;
    private boolean isShare = false;

    private boolean isfreeTrial;

    // 是否从企业流程进入
    private boolean fromEnt;
    private int isJsBack;
    private ShareContent content;
    private ShareMenuDialog mShareMenuDialog;
    // 是否跳转到我界面
    private boolean isgotoIfragment;
    private String params;
    public static final int START_FOR_RESULT = 1010;
    private RelativeLayout comment_layout;
    private TextView tv_read_commentcount;
    private ImageView comment_icon;
    private int commentsCount;
    // private boolean toRecharge;
    public static final int ZSBRESULT = 0x22;
    private String currentURL = "";
    public static final int INSTALL = 1;
    public static final int UNINSTALL = 0;

    // add by trade
    private String shortUrl;
    private boolean isFromTradeHome;
    private String tradeSourcePageUrl;
    private boolean isShareFromJs = false; // 当前分享是否来自活动微件
    private String shareType; // 活动微件分享类型
    private String isWidget; // 预约微件 iswidget=1 时 详情页登陆成功后 拼入参数uid和anonymous add
    // by zhaobo
    private boolean isFirstLoadFromJs = true; // 第一次加载JS时重新load 针对预约微件 iswidget
    private int replaceBg;
    private String zutiSrp;

    private Bitmap bitmapNormal = null;
    private Bitmap bitmapSelected = null;
    private Bitmap refreshSelected = null;
    private Bitmap refreshNormal = null;
    private boolean bitmapLoaded = false;
    private boolean refreshBitmapLoaded = false;
    public static final String PAGE_KEYWORD = "page_keyword";
    private String keyword;
    public final static String TRADE_COMMENT_TYPE = "2"; // 评论类型
    public final static String TRADE_UP_TYPE = "1"; // 点赞
    private int tradeCommentCount; // 超A记录评论数量
    private ImageButton button_reload;
    private boolean isZAEwidget = false; // 标记是否是ZAE h5微件 此页面底部分享 评论 点赞 收藏 按钮
    // 搜悦也再使用
    private Bitmap h5ShareBitmap = null; // h5 分享时bitMap 在调用底部导航时异步获取 add by
    // zhaobo 2015-03-27
    private String h5ShareImageUrl = ""; // h5 分享图标地址 add by zhaobo 2015-03-27
    private String h5CallBackUrl;
    private String zaeMd5;

    //add by yinguanping 重定向
    private boolean isFistAdd = true;
    //    private boolean isClickGoBack = false;
    private boolean isStarted = false;
    private List<String> hostUrls = new ArrayList<String>();

    private String mImagePath = "";
    private String mImageUrl;
    // 请求获取图片路径的url
    private String mRequestUrl = "";

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static final int DEFAULT_OUTPUT_X = 480;
    private static final int DEFAULT_OUTPUT_Y = 480;

    // 裁剪图片宽高默认值
    private int mCropWidth = DEFAULT_OUTPUT_X;
    private int mCropLength = DEFAULT_OUTPUT_Y;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.web_src_view);
        if (ConfigApi.isPrintWebViewLogToSDCard) {
            //Add by ygp 收集log日志服务
            startService(new Intent(this, LogService.class));
        }
        hr = new Handler();
        View view = findView(R.id.webSrcLoadingView);
        pbHelp = new ProgressBarHelper(this, view);
        pbHelp.setProgressBarClickListener(this);
        imageLoader = ImageLoader.getInstance();
        isShareFromJs = false; // 活动微件分享
        initTitleBar();// 所有按钮置灰
        initFromIntent();// 初始化数据
        initView();
        changeSkin(); // 换头 换肤
        if (sourcePageUrl != null) {
            // if(special){//把导航挪到底部
            // View naviRoot = findViewById(R.id.ll_webNavi_root);
            // ViewGroup parent = (ViewGroup) naviRoot.getParent();
            // parent.removeView(naviRoot);
            // parent.addView(naviRoot);
            // }

            loadWebViewUrl();
        }
        favoriteDialog = new FavoriteAlert(this,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        htp.favoriteAdd(
//                                token,
//                                sourcePageUrl,
//                                sri.title(),
//                                null != sri.image() && sri.image().size() > 0 ? sri
//                                        .image().get(0) : "",
//                                sri.description(), pubDate(), sri.source(), sri
//                                        .keyword(), sri.srpId());
                        AddFavoriteReq req = new AddFavoriteReq(HttpCommon.CIRLCE_ADD_FAVORATE_ID, WebSrcViewActivity.this);
                        req.setParams(token, sourcePageUrl, sri.title(),
                                null != sri.image() && sri.image().size() > 0 ? sri.image().get(0) : "",
                                sri.description(), pubDate(), sri.source(), sri.keyword(), sri.srpId());
                        CMainHttp.getInstance().doRequest(req);

                        collect.setEnabled(false);
                    }
                });

        fromEnt = getIntent().getBooleanExtra("fromEnt", false);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mWebView.doOnPause();
        if (mWebView != null) {
            hideKeyboard();
            mWebView.doOnPause();
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mWebView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 取消收藏
     *
     * @param token
     * @param url
     * @param dataType
     * @param blogId
     */
    private void dofavoriteDelete(String token, String url, int dataType, long blogId) {
        CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID, this);

        req.setParams(token, url, dataType, blogId);
        CMainHttp.getInstance().doRequest(req);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ib_read_collect:
                if (hasFavorited) {
//                    htp.favoriteDelete(token, sourcePageUrl, 0, 0l);
                    dofavoriteDelete(token, sourcePageUrl, 0, 0l);
                    collect.setEnabled(false);
                } else {
                    favoriteDialog.show();
                }
                break;
            case R.id.up_layout:
                tradeUp();
                break;
            case R.id.ib_read_share:
                if (hr != null) {
                    hr.post(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(sri.url())) {
                                return;
                            }
                            loadShareImage();
                            showShareWindow(sri.keyword());
                        }
                    });

                }
                break;
            case R.id.comment_icon:
                if (FastDoubleCliceUtils.isFastDoubleClick())
                    return;
                // 超级APP针对活动微件 点赞 评论 收藏统一url 由h5 js提供
                if (isShareFromJs) {
                    sri.url_$eq(sourcePageUrl);
                }
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("searchResultItem", sri);
                i.setClass(this, CommentaryActivity.class);
                i.putExtras(bundle);
                i.putExtra("isH5Widget", isZAEwidget);  //传入Key值  add by zhaobo
                i.putExtra("h5CallBackUrl", h5CallBackUrl);
                startActivityForResult(i, START_FOR_RESULT);
                break;
            default:
                break;
        }
    }

    private void resetButtonState() {
        share.setEnabled(false);
        up.setEnabled(false);
        collect.setEnabled(false);
    }

    private void setButtonState() {
        if (tv_read_commentcount != null) {
            tv_read_commentcount.setText(commentsCount + "");
        }
        changeCollectState();
        changetUpState();
    }

    private void changeCollectState() {
        if (hasFavorited) {
            collect.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_collect_unnormal));
        } else {
            collect.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_collect_normal));
        }
        collect.setEnabled(true);
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_HASFAVORITED, hasFavorited);
        intent.putExtra(RESULT_HASUP, hasUp);
        intent.putExtra(COMMENT_COUNT, commentsCount);
        setResult(0, intent);
    }

    private void changetUpState() {
        if (upC != null)
            upC.setText("" + upCount);
        if (hasUp) {
            up.setEnabled(false);
            iv_up.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_up_unnormal));
        } else {
            up.setEnabled(true);
            iv_up.setImageDrawable(getResources().getDrawable(
                    R.drawable.circle_up_normal));
        }

    }

    private void initTitleBar() {
        upC = findView(R.id.up_count);
        // button_forword = findViewById(R.id.button_forword);
        button_back = (ImageButton) findViewById(R.id.button_back);
        button_close = (ImageButton) findViewById(R.id.button_close);
        share = (ImageButton) findViewById(R.id.ib_read_share);
        share.setOnClickListener(this);
        collect = (ImageButton) findViewById(R.id.ib_read_collect);
        iv_up = (ImageButton) findViewById(R.id.web_sre_up);
        up = (RelativeLayout) findViewById(R.id.up_layout);
        collect.setOnClickListener(this);
        up.setOnClickListener(this);
        comment_layout = (RelativeLayout) findViewById(R.id.comment_layout);
        comment_layout.setOnClickListener(this);
        tv_read_commentcount = (TextView) findViewById(R.id.tv_read_commentcount);
        comment_icon = (ImageView) findViewById(R.id.comment_icon);
        comment_icon.setOnClickListener(this);
        resetButtonState();
        // button_forword.setEnabled(false);
        button_reload = (ImageButton) findViewById(R.id.button_reload);
    }

    private void initView() {
        web_src_loadProgress = (ProgressBar) findViewById(R.id.web_src_loadProgress);
        webView_parent = (RelativeLayout) findViewById(R.id.webView_parent);
        mWebView = (CustomWebView) findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT <= 9) {
//            settings.setPluginsEnabled(true);
        } else if (Build.VERSION.SDK_INT <= 10) {
            settings.setPluginState(PluginState.ON);
        } else {
            settings.setPluginState(PluginState.ON);
        }
        if (ConfigApi.isUseWebViewImageBlock) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级
            settings.setBlockNetworkImage(true);//设置图片最后加载渲染
        }
        mWebView.addJavascriptInterface(new JsInterface(), "ent");
        mWebView.addJavascriptInterface(new SouYueLoginInterface(), "souyue");
        mWebView.setGotoSrpListener(this);
        mWebView.setGotoShareListener(this);
        mWebView.setOnJSClickListener(this);
        mWebView.getCookeiListener(this);
        mWebView.setCookeiListener(this);
//        mWebView.setDownLoadNoverListener(this);
//        mWebView.setDownLoadRadioListener(this);
        mWebView.setReadNovelDictionaryListener(this);
        mWebView.setReadNovelContentListener(this);
        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
        mWebView.setWebViewClient(new CustomWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /**
                 * fix bug SYFX-1306 ,if the phone MANUFACTURER is
                 * XIAOMI or MEIZU ,
                 * the browser will open a default application to open the url
                 */
                if (url.toLowerCase().startsWith("sogoumsesdk://") || url.toLowerCase().startsWith("sinanews://")) {
                    return false;
                }
                /**
                 *  以 huiduobao://  为前缀的url_scheme，打开惠多宝客户端
                 */
                if (url.toLowerCase().startsWith("huiduobao://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {


            /**
             * fix bug SYFX-364 when target="_blank",
             * cannot open a new window in this webview
             */
            private String innerAnchor = "";    //h5 中内部锚点

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String url = null;
                if(result != null) {
                	url = result.getExtra();
                }
                if (url != null && !url.equals(innerAnchor)) {
                    view.loadUrl(url);
                    innerAnchor = url;
                }
                return false;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 90) {
                }
                if (newProgress == 100) {
                    pbHelp.goneLoading();
                    webLoadOk = true;
                    shareIsEnable();
                }
                web_src_loadProgress.setProgress(newProgress);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                mContext.startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg,
                                        String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                mContext.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                // i.setType("*/*");
                i.setType("*/*");
                mContext.startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);

            }

        });
    }

    public void onPageStarted(WebView view, String url) {
        if (isFistAdd) {
            hostUrls.add(url);
        }

        isStarted = true;
        if (ConfigApi.isPrintWebViewLogToSDCard) {
            int CURRENT_NETWORK_STATUS = DeviceInfo.getNetWorkType(this);
            String operator = "";
            if (CURRENT_NETWORK_STATUS == 4) {// wifi
                operator = "wifi";
            } else if (CURRENT_NETWORK_STATUS == 1) {   //2G
                operator = "2G";
            } else if (CURRENT_NETWORK_STATUS == 2) {   //3G
                operator = "3g";
            } else if (CURRENT_NETWORK_STATUS == 3) {   //4G
                operator = "4G";
            }
            android.util.Log.i(LogService.TAG, "category：original--onPageStart--网络:" + operator + "--机型:" + DeviceInfo.deviceName + "--版本:" + DeviceInfo.osVersion);
        }
        updataUI(view);
        if (web_src_loadProgress.getVisibility() == View.GONE) {
            web_src_loadProgress.setVisibility(View.VISIBLE);
        }
    }

    public void onLoadResource(WebView view, String url) {
        if (ConfigApi.isPrintWebViewLogToSDCard)
            android.util.Log.i(LogService.TAG, "category：original--url=" + url);
    }

    public void onPageFinished(WebView view, String url) {
        isFistAdd = false;
        isStarted = false;
        if (ConfigApi.isPrintWebViewLogToSDCard)
            android.util.Log.i(LogService.TAG, "category：original--onPageFinish");
        if (ConfigApi.isUseWebViewImageBlock && mWebView != null)
            mWebView.getSettings().setBlockNetworkImage(false);
        updataUI(view);
        if (web_src_loadProgress.getVisibility() == View.VISIBLE) {
            web_src_loadProgress.setVisibility(View.GONE);
        }
    }

    public void shouldOverrideUrlLoading(WebView view, String url) {
//        if (!isClickGoBack) {
        if (isStarted) {//重定向url
//                if (isFistAdd) {
//                    hostUrls.clear();
//                }
//                hostUrls.add("");
            if (hostUrls.size() > 0) {
                hostUrls.remove(hostUrls.size() - 1);
            }
        }
        if (!hostUrls.contains(url))
            hostUrls.add(url);
            /*else {
                if (!hostUrls.contains(url))
                    hostUrls.add(url);
            }*/
//        }
//        System.out.println("orginURl:"+mWebView.getOriginalUrl());
//        System.out.println("url:"+mWebView.getUrl());
//        isClickGoBack = false;
        currentURL = url;
        share.setEnabled(false);
        // updataUI(view);
        if (!TextUtils.isEmpty(url) && !url.startsWith("about:blank")) {// 跳过空白页
        }
    }

    // 换肤操作
    private void changeSkin() {

        if ((sri != null && TradeBusinessApi.getInstance()
                .isActivitySrpDownloadOver(sri.srpId()))
                || TradeBusinessApi.getInstance().isActivitySrpDownloadOver(
                zutiSrp)
                || SYSharedPreferences.getInstance().getBoolean(
                ConstantsUtils.ACTION_SRPID_DOWNLOAD_OVER_PRE
                        + keyword, false)) {
            replaceBg = 3;
        }
        if (ConstantsUtils.MEGAGAME_SEARCH_KEYWORD.equals(keyword)) {
            replaceBg = 4;
        }

        if (replaceBg == 3) {

            // StateListDrawable goBackDrawable =
            // TradeCommonUtil.addStateDrawable(this,TradeCommonUtil.getImageByReflect("ac_super_btn_goback_selector"),TradeCommonUtil.getImageByReflect("ac_super_btn_goback_selector_h"),TradeCommonUtil.getImageByReflect("ac_super_btn_goback_selector_h"));
            // button_back.setImageDrawable(goBackDrawable);
            LinearLayout web_src_navibar_ll = (LinearLayout) findViewById(R.id.web_src_navibar_ll);
            web_src_navibar_ll.setPadding(0, 0, 0, 0);
            TextView textView = (TextView) findViewById(R.id.activity_bar_title);
            textView.setVisibility(View.GONE);
            LinearLayout naviRoot = (LinearLayout) findViewById(R.id.web_src_navibar1);
            ImageView ac_titlebar_bg = (ImageView) findViewById(R.id.trade_iv_ac_titlebar_bg);
            button_reload = (ImageButton) findViewById(R.id.button_reload);
            ac_titlebar_bg.setVisibility(View.VISIBLE);
            // TradeCommonUtil.getImageByReflect(naviRoot.getChildAt(0),
            // "ac_cm_all_title_bg_red", 1);
            // TradeCommonUtil.getImageByReflect(textView,"ac_title_szuper_image",1);

            for (TitleBarSource titleBarSource : SRPActivity.titleBarSourceList) {
//                Bitmap bitmap = a.getCachedImage(titleBarSource.getPicurl());
                Bitmap bitmap = null;
                File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(titleBarSource.getPicurl());
                if (fileImage != null) {
                    bitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
                }
                Matrix matrix = new Matrix();
                matrix.postScale(1.5f, 1.5f); // 长和宽放大缩小的比例
                if (bitmap != null) {
                    bitmap = Bitmap
                            .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrix, true);
                }
                if ("zh_ac_all_title_bg_red.png".equals(titleBarSource
                        .getTitle())) {
                    // naviRoot.setBackgroundDrawable(new
                    // BitmapDrawable(getResources(),bitmap));
                    naviRoot.getChildAt(0).setBackgroundDrawable(
                            new BitmapDrawable(getResources(), bitmap));
                    continue;
                } else if ("trade_ac_btn_goback_selector.9.png"
                        .equals(titleBarSource.getTitle())) {
                    bitmapNormal = bitmap;
                } else if ("trade_ac_btn_goback_selector_h.9.png"
                        .equals(titleBarSource.getTitle())) {
                    bitmapSelected = bitmap;
                } else if ("trade_ac_title_super_image.png"
                        .equals(titleBarSource.getTitle())) { // 导航栏的掌上超模大赛
                    // textView.setBackgroundDrawable(new
                    // BitmapDrawable(getResources(),bitmap));
                    // ac_titlebar_bg.setBackgroundDrawable(new
                    // BitmapDrawable(getResources(),bitmap));
                    continue;
                } else if ("trade_ac_btn_refresh_selected.9.png"
                        .equals(titleBarSource.getTitle())) {
                    refreshNormal = bitmap;
                } else if ("trade_ac_btn_refresh_unenabled.9.png"
                        .equals(titleBarSource.getTitle())) {
                    refreshSelected = bitmap;
                }

                if (bitmapNormal != null && bitmapSelected != null
                        && !bitmapLoaded) {
                    bitmapLoaded = true;
                    StateListDrawable drawable = Utils
                            .addStateDrawable(mContext, bitmapNormal,
                                    bitmapSelected, -1);
                    button_back.setImageDrawable(drawable);
                }
                if (refreshNormal != null && refreshSelected != null
                        && !refreshBitmapLoaded) {
                    refreshBitmapLoaded = true;
                    StateListDrawable drawable = Utils
                            .addStateDrawable(mContext, refreshNormal,
                                    refreshSelected, -1);
                    button_reload.setImageDrawable(drawable);
                }
            }

        } else if (replaceBg == 4) {
            LinearLayout ll_web_src_navi_root = (LinearLayout) findViewById(R.id.web_src_navibar1);
            ll_web_src_navi_root.getChildAt(0).setBackgroundResource(
                    R.drawable.zh_ac_all_title_bg_red);
            button_back.setImageResource(R.drawable.megagame_btn_goback2);
            button_reload
                    .setImageResource(R.drawable.megagame_btn_refresh_selector2);
            button_close
                    .setImageResource(R.drawable.megagame_btn_close_selector);
        }

    }

    private void updataUI(WebView view) {
        if (view.canGoForward()) {
            button_close.setVisibility(View.VISIBLE);
        }
    }

    public void onReceivedError() {
        pbHelp.goneLoading();
        pbHelp.showNetError();
        pbHelp.setFromH5(true);
        if (web_src_loadProgress.getVisibility() == View.VISIBLE) {
            web_src_loadProgress.setVisibility(View.GONE);
        }
    }

    private void loadWebViewUrl() {
        isFistAdd = true;
        if (hostUrls != null) {
            hostUrls.clear();
        }

        User user = SYUserManager.getInstance().getUser();
        boolean islogin = (user != null && user.userType().equals(
                SYUserManager.USER_ADMIN));
        StringBuilder sb = null;
        if (!StringUtils.isEmpty(page_type)) {
            if (page_type.equals("url")) {
                sb = new StringBuilder();
                sb.append("?userid=").append(
                        islogin ? ZSEncode.encodeURI(String.valueOf(user
                                .userId())) : "");
            } else if (page_type.equals("interactWeb")) {
                sb = new StringBuilder();
                sb = setParams(sb, user, islogin);
            } else if (page_type.equals("youbao")) {
                sb = new StringBuilder();
                sb.append("&token=").append(
                        SYUserManager.getInstance().getToken());
            } else if (page_type.equals("nopara")) {

            }
        }
        // 预约微件 活动微件
        if (("1").equals(isWidget) && islogin) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            if (sourcePageUrl.contains("?"))
                sb.append("&anonymous=").append(
                        SYUserManager.getInstance().getUserType());//
            else {
                sb.append("?anonymous=").append(
                        SYUserManager.getInstance().getUserType());
            }

            sb.append("&uid=")
                    .append(SYUserManager.getInstance().getUserId())
                    .append("&wifi=")
                    .append((CMainHttp.getInstance().isWifi(MainApplication.getInstance()) ? "1"
                            : "0"))
                    .append("&imei=")
                    .append(DeviceUtil.getDeviceId(MainApplication
                            .getInstance()));
        }
        MakeCookie.synCookies(this, sourcePageUrl);


        if (sb != null) {
            params = sb.toString();
//            if (mWebView.isWebEncrypt(sourcePageUrl + params)) {
//                htp.shortURL(Utils.encryptWebUrl(sourcePageUrl + params));// TODO:请求短链服务需要加密
//            } else {
//                htp.shortURL(sourcePageUrl + params);// TODO:请求短链服务需要加密
//            }
            mWebView.loadUrl(sourcePageUrl + params);
        } else {
            if (!ConfigApi.isSouyue() && isFromTradeHome) {// TODO:是否是搜悦
                mWebView.loadUrl(tradeSourcePageUrl);// 超a
            } else {
                mWebView.loadUrl(sourcePageUrl);// 搜悦
            }
        }
        if (!ConfigApi.isSouyue() && isFromTradeHome) {// TODO:是否是搜悦
            currentURL = tradeSourcePageUrl;
        } else {
            currentURL = sourcePageUrl;
        }
        pbHelp.showLoading();

        /**
         * 加次方法是为了刷新页面后去除历史记录，注：webivew只会清除当前页之前的历史记录。 add by yinguanping
         */
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //退出了还有可能会调用
                if (mWebView != null) {
                    mWebView.clearHistory();
                }
            }
        }, 1000);
    }

    public void shortURLSuccess(String url) {// 短链返回不加密
        isShare = false;
        SUPPER_SHARE_URL = url;
    }

    /**
     * 初始数据
     */
    private void initFromIntent() {
        Intent i = this.getIntent();

        sri = (SearchResultItem) i.getSerializableExtra(ITEM_INFO);
        isgotoIfragment = getIntent().getBooleanExtra(GOTOIFRAGMENT, false);
        if (!ConfigApi.isSouyue()) {
            isFromTradeHome = i.getBooleanExtra("isFromTradeHome", false);
            tradeSourcePageUrl = i.getStringExtra("sourceUrl");
        }
        // 底部bottombar 显示与否
        if (ConfigApi.isSouyue()) {
            findViewById(R.id.web_src_bottom_bar).setVisibility(View.GONE);
        }
        if (sri == null) {
            findViewById(R.id.web_src_bottom_bar).setVisibility(View.GONE);
            sourcePageUrl = i.getStringExtra(PAGE_URL);// 无按钮的url
            page_type = i.getStringExtra(PAGE_TYPE);
            if (!StringUtils.isEmpty(page_type) && page_type.equals("url")
                    && sourcePageUrl == null) {
                sourcePageUrl = UrlConfig.web_nav;
            }
        } else
            initFromIntentObj(sri, i);
        keyword = i.getStringExtra(PAGE_KEYWORD);
        if (null == keyword) {
            keyword = i.getStringExtra("keyword");
        }
    }

    /**
     * 拼参数
     */
    private StringBuilder setParams(StringBuilder sb, User user, boolean islogin) {
        if (sourcePageUrl.contains("?")) {
            if (sourcePageUrl.endsWith("&")) {
                sb.append("userid=");
            } else {
                sb.append("&userid=");
            }

        } else {
            sb.append("?userid=");
        }
        sb.append(user != null ? user.userId() : "")
                .append("&version=")
                .append(DeviceInfo.getFloatVersion())
                .append("&anonymous=")
                .append(islogin ? 1 : 0)
                .append("&wifi=")
                .append(CMainHttp.getInstance().isWifi(this) ? "1" : "0")
                .append("&imei=")
                .append(DeviceUtil.getDeviceId(this))
                .append("&appname=")
                .append(CommonStringsApi.APP_NAME_SHORT)
                .append("&api_appname=")
                .append(com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()))
                .append("&v=")
                .append(DeviceInfo.getAppVersion())
                .append("&type=")
                .append(DeviceInfo.osName)
                .append("&lat=")
                .append(SYSharedPreferences.getInstance().getString(
                        SYSharedPreferences.KEY_LAT, ""))
                .append("&long=")
                .append(SYSharedPreferences.getInstance().getString(
                        SYSharedPreferences.KEY_LNG, ""))
                .append("&province=")
                .append(SYSharedPreferences.getInstance().getString(
                        SYSharedPreferences.KEY_PROVINCE, ""))
                .append("&city=")
                .append(SYSharedPreferences.getInstance().getString(
                        SYSharedPreferences.KEY_CITY, ""));
        if (user != null) {
            sb.append("&token=").append(user.token()).append("&username=")
                    .append(user.userName()).append("&sid=")
                    .append(user.token());
        }
        return sb;
    }

    /**
     * 加密
     */
    private List<String> getS4String(User user, boolean islogin) {
        List<String> param = new ArrayList<String>();
        param.add("userid");
        param.add(user != null && islogin ? user.userId() + "" : "");
        param.add("token");
        param.add(user.token());
        param.add("version");
        param.add(DeviceInfo.getFloatVersion());
        param.add("anonymous");
        param.add(islogin ? 1 + "" : 0 + "");
        param.add("wifi");
        param.add(CMainHttp.getInstance().isWifi(this) ? "1" : "0");
        param.add("imei");
        param.add(DeviceUtil.getDeviceId(this));
        param.add("username");
        param.add(user.userName());
        param.add("sid");
        param.add(user.token());
        param.add("appname");
        param.add(CommonStringsApi.APP_NAME_SHORT);
        param.add("v");
        param.add(DeviceInfo.getAppVersion());
        param.add("type");
        param.add(DeviceInfo.osName);
        param.add("lat");
        param.add(SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.KEY_LAT, ""));
        param.add("long");
        param.add(SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.KEY_LNG, ""));
        return param;
    }

    /**
     * 有底部按钮的数据
     */
    private void initFromIntentObj(SearchResultItem sri, Intent i) {
        if (sri.pushId() == 0) {// >0 视为推送消息
            if (TextUtils.isEmpty(sri.url())) {
                sri.url_$eq(DEFAULT_PAGE);
            }
            sourcePageUrl = sri.url();
            // if (!ConfigApi.isSouyue() && isFromTradeHome
            // && !StringUtils.isEmpty(sourcePageUrl)) {
            // htp.shortURL(appShareParams(sourcePageUrl));
            // }
            if (sourcePageUrl != null) {
                shareIsOk = true;
                if (sourcePageUrl.endsWith("#extractnone"))
                    sourcePageUrl = sourcePageUrl
                            .replaceAll("#extractnone", "");
                ReadHistoryHelper.getInstance().insert(sourcePageUrl);
                NewsCount newsCount = (NewsCount) i
                        .getSerializableExtra("newsCount");
                if (newsCount == null) {
//                    htp.newsCount(token, sourcePageUrl);
                    loadNewsCount(token, sourcePageUrl);
                } else {
                    hasFavorited = newsCount.hasFavorited();
                    hasUp = newsCount.hasUp();
                    upCount = newsCount.upCount();
                    commentsCount = newsCount.commentsCount();
                    setButtonState();
                }
            }
        } else {
//            htp.newsDetail(sri.keyword(), sri.srpId(), sri.pushId());
            loadNewsDetail(sri.keyword(), sri.srpId(), sri.pushId());
        }
    }

    /**
     * 加载网络 新闻详情
     *
     * @param keyword
     * @param srpId
     * @param pushId
     */
    public void loadNewsDetail(String keyword, String srpId, Long pushId) {
        NewsDetailReq req = new NewsDetailReq(HttpCommon.DETAIL_NEWS_DETAIL_ID, this);
        req.setParams(keyword, srpId, pushId);
        CMainHttp.getInstance().doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId()) {
            case HttpCommon.CIRLCE_ADD_FAVORATE_ID:
                favoriteAddSuccess(request.<HttpJsonResponse>getResponse().getBodyLong("newsId"));
                break;
            case HttpCommon.DETAIL_NEWS_COUNT_ID:
                newsCountSuccess(new Gson().fromJson(request.<HttpJsonResponse>getResponse().getBody(), NewsCount.class));
                break;
            case HttpCommon.DETAIL_NEWS_DETAIL_ID:
                newsDetailSuccess(new Gson().fromJson(request.<HttpJsonResponse>getResponse().getBody(), NewsDetail.class));
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                favoriteDeleteSuccess();
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortURLSuccess(request.<HttpJsonResponse>getResponse().getBodyString());
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse response = request.getResponse();
                shareSuccess(response.getBodyLong("newsId"));
                break;
            case HttpCommon.DETAIL_UP:
                HttpJsonResponse response1 = request.getResponse();
                upSuccess(response1.getBodyLong("newsId"));
        }

    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()) {
            case HttpCommon.CIRLCE_ADD_FAVORATE_ID:
                if (collect != null)
                    collect.setEnabled(true);
                break;
            case HttpCommon.DETAIL_NEWS_COUNT_ID:
                resetButtonState();
                break;
            case HttpCommon.DETAIL_NEWS_DETAIL_ID:
                SouYueToast.makeText(this, "连接不可用", SouYueToast.LENGTH_SHORT);
                finish();
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                SouYueToast.makeText(this, R.string.share_fail,
                        SouYueToast.LENGTH_SHORT).show();
        }

    }


    /**
     * 查看原文，应该不需要加密的
     *
     * @param detail
     */
    public void newsDetailSuccess(NewsDetail detail) {
        sri.title_$eq(detail.title());
        sri.image_$eq(detail.image());
        sri.description_$eq(detail.title());
        sri.srpId_$eq(detail.srpId());
        sri.source_$eq(detail.source());
        sri.date_$eq(detail.date());
        sri.url_$eq(detail.urlOrig());
        shareIsOk = true;
        mWebView.loadUrl(sourcePageUrl = detail.urlOrig());
//        htp.newsCount(token, sourcePageUrl = detail.urlOrig());
        loadNewsCount(token, sourcePageUrl = detail.urlOrig());
    }


    /**
     * 加载网络 新闻详情顶数，评论数
     *
     * @param token
     * @param url
     */
    public void loadNewsCount(String token, String url) {
        NewsCountReq req = new NewsCountReq(HttpCommon.DETAIL_NEWS_COUNT_ID, this);
        req.setParams(token, url);
        CMainHttp.getInstance().doRequest(req);
    }

    public void onCloseClick(View v) {
        onCloseActivityClick(v);
    }

    public void onReloadUrlClick(View v) {
        if (CMainHttp.getInstance().isNetworkAvailable(this))
            mWebView.reload();
        else
            SouYueToast.makeText(getApplicationContext(),
                    getResources().getString(R.string.nonetworkerror), 0)
                    .show();
    }

    public void onBackwordUrlClick(View v) {
        mWebView.stopLoading();

//      isClickGoBack = true;
        int step = -1;//goback step

        WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();
        int cForwardIndex = webBackForwardList.getCurrentIndex();
        String currentUrl = null;
        if (webBackForwardList.getItemAtIndex(cForwardIndex) != null) {
            currentUrl = webBackForwardList.getItemAtIndex(cForwardIndex).getUrl();
        }
        String previousUrl = null;
        if (cForwardIndex - 1 >= 0) {
            previousUrl = webBackForwardList.getItemAtIndex(cForwardIndex - 1).getUrl();
        }
        if (cForwardIndex == 0) {
            step = -100;
        } else {
            while (cForwardIndex + step >= 0 && !hostUrls.contains(webBackForwardList.getItemAtIndex(cForwardIndex + step).getUrl())) {
                step--;
            }
            if (cForwardIndex + step < 0) {
                step = -100;
            }
//        	Log.d(TAG, "hostUrls size="+hostUrls.size());
//            if (hostUrls.size() > 0) {
//            	if (cForwardIndex + step >= 0) {
//            		hostUrls.remove(hostUrls.size() - 1);
////            		step = -cForwardIndex;
//            		Log.d(TAG, "remove step="+step);
//            		Log.d(TAG, "after hostUrls size="+hostUrls.size());
//            	}
//            	// last step
//            	if (hostUrls.size() == 0) {
//            		step = -100;
//            	}
//            }

        }
        if (StringUtils.isNotEmpty(previousUrl) && StringUtils.isNotEmpty(currentUrl) && previousUrl.equals(currentUrl)) {
            --step;
            if (cForwardIndex + step < 0) {
                step = -100;
            }
        }
        if (step == -100) {
            onCloseClick(v);
        } else {
            mWebView.goBackOrForward(step);
        }

      /*if (cForwardIndex == 0) {//当前在首页，并且首页没有重定向
          super.onBackPressed();
          return;
      }

      int step = -1;//goback step
      while (webBackForwardList.getItemAtIndex(cForwardIndex + step) != null &&
              hostUrls.get(cForwardIndex + step) != null &&
              !webBackForwardList.getItemAtIndex(cForwardIndex + step).getUrl().equals(hostUrls.get(cForwardIndex + step))) {
          step--;
      }
      if (!webBackForwardList.getItemAtIndex(cForwardIndex).getUrl().equals(hostUrls.get(cForwardIndex))) {//当前重定向页返回上一页
          if (cForwardIndex + step == 0) {//当前为重定向首页点击返回，直行关闭操作
              super.onBackPressed();
              return;
          } else {
              int tempStep = -1;//回到当前重定向的页时再向前走一次
              while (webBackForwardList.getItemAtIndex(cForwardIndex + step + tempStep).getUrl() != null &&
                      hostUrls.get(cForwardIndex + step + tempStep) != null &&
                      !webBackForwardList.getItemAtIndex(cForwardIndex + step + tempStep).getUrl().equals(hostUrls.get(cForwardIndex + step + tempStep))) {
                  tempStep--;
              }
              step += tempStep;
          }
      } else {
          if (cForwardIndex + step == 0) {//返回到首页清除除首页外的浏览记录
              int clearIndex = 1;
              if (webBackForwardList.getItemAtIndex(0) != null &&
                      hostUrls.get(0) != null && webBackForwardList.getItemAtIndex(0).getUrl().equals(hostUrls.get(0))) {//首页主地址相同
                  while (hostUrls.get(clearIndex) != null && "".equals(hostUrls.get(clearIndex))) {
                      clearIndex++;
                  }
                  for (int i = 0; i < hostUrls.size() - 1; i++) {
                      if (i >= clearIndex) {
                          hostUrls.remove(i);
                      }
                  }
              }
          }
      }
      mWebView.goBackOrForward(step);*/


//      LogDebugUtil.v("FAN", "onBack");
//      mWebView.stopLoading();
      /*if (mWebView.canGoBack()) {
          LogDebugUtil.v("FAN", "web---3");
          mWebView.goBack();
      } else {
          onCloseClick(v);
      }*/
    }

    public void onForwordUrlClick(View v) {
        LogDebugUtil.v("FAN", "onForword");
        mWebView.stopLoading();
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    public void onCloseActivityClick(View v) {
        LogDebugUtil.v("FAN", "onClose");
        if (fromEnt) {
//            UIHelper.startIFragment(this);
//            setResult(RESULT_OK);
//            finish();
        } else if (isgotoIfragment) {
//            IntentUtil.gotoIfragment(this);
            finish();
        } else {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    @Override
    protected void onDestroy() {
        LogDebugUtil.v("FAN", "onDestroy");
        if (ConfigApi.isPrintWebViewLogToSDCard) {
            //Add by ygp 收集log日志服务
            stopService(new Intent(this, LogService.class));
        }
        super.onDestroy();
        mWebView.setVisibility(View.GONE);
        if (webView_parent != null)
            webView_parent.removeView(mWebView);
        mWebView.destroy();
        mWebView = null;
    }

    private Long pubDate() {
        long pubDate = 0;
        try {
            pubDate = Long.parseLong(sri.date());
        } catch (Exception e) {
        }
        return pubDate;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ShareContent content;
            switch (msg.what) {
                case ShareMenuDialog.SHARE_TO_SINA:
                    content = (ShareContent) msg.obj;
                    mSsoHandler = ShareByWeibo.getInstance().share(
                            WebSrcViewActivity.this, content);
                    break;
//                case ShareMenuDialog.SHARE_TO_TWEIBO:
//                    content = (ShareContent) msg.obj;
//                    ShareByTencentWeiboSSO.getInstance().share(
//                            WebSrcViewActivity.this, content);
//                    break;
                case 7878:
                    LinearLayout title_layout = (LinearLayout) findViewById(R.id.title_layout);
                    title_layout.setVisibility(View.VISIBLE);
                    //显示头部标题 TextView
                    TextView textView = (TextView) title_layout.findViewById(R.id.activity_bar_title);
                    textView.setText(msg.obj.toString());
                    textView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void showShareWindow(String keyword) {
        if (mShareMenuDialog == null) {
            if (StringUtils.isEmpty(keyword)) {
                if (SHARE_TYPE.equals(sri.getType())) {
                    mShareMenuDialog = new ShareMenuDialog(this, this,
                            ShareConstantsUtils.WEBSRCVIEWWEBTYPE);
                } else {
                    mShareMenuDialog = new ShareMenuDialog(this, this,
                            ShareConstantsUtils.WEBSRCVIEWKEYWORD);
                }
            } else if (StringUtils.isSuperSrp(keyword, null) != 0) {
                mShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.SUPERSRP);
            } else {
                mShareMenuDialog = new ShareMenuDialog(this, this,
                        ShareConstantsUtils.WEBSRCVIEW);
            }
        }
        if (StringUtils.isEmpty(keyword)) {
            mShareMenuDialog.showBottonDialog();
        } else {
            int bottomHeight = findViewById(R.id.web_src_bottom_bar)
                    .getHeight();
            if (bottomHeight > 0) {
                mShareMenuDialog.showBottonDialog(bottomHeight);
            }
        }
    }

    @Override
    public ShareContent getShareContent() {
//        Bitmap imageBitmap = null;
        String str = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";
        if (!TextUtils.isEmpty(str) && imageLoader != null) {
            if (imageBitmap == null) {
                File file = imageLoader.getDiskCache().get(str);
                if (file != null && file.exists()) {
                    imageBitmap = BitmapFactory.decodeFile(imageLoader.getDiscCache().get(str).getAbsolutePath());
                }
            }
        }
        ShareContent shareContent = null;
        if (!isZAEwidget) {
            shareContent = new ShareContent(StringUtils.shareTitle(sri.title(),
                    sri.description()), SUPPER_SHARE_URL != null ? SUPPER_SHARE_URL
                    : ZSEncode.encodeURI(sourcePageUrl), imageBitmap,
                    StringUtils.shareDesc(sri.description()), str);
        } else {
            shareContent = new ShareContent(StringUtils.shareTitle(sri.title(),
                    sri.description()),
                    SUPPER_SHARE_URL != null ? appShareParams(SUPPER_SHARE_URL)
                            : ZSEncode.encodeURI(sourcePageUrl), h5ShareBitmap,
                    StringUtils.shareDesc(sri.description()),
                    h5ShareImageUrl);
        }


        return shareContent;
    }
//
//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        if ("newsCount".equals(methodName))
//            resetButtonState();
//        if ("favoriteAdd".equals(methodName))
//            collect.setEnabled(true);
//        if ("up".equals(methodName))
//            up.setEnabled(true);
//        if ("newsDetail".equals(methodName)) {
//            SouYueToast.makeText(this, "连接不可用", SouYueToast.LENGTH_SHORT);
//            finish();
//        }
//
//        if ("shortURL".equals(methodName)) {
//            if (pbHelp != null) {
//                pbHelp.setFromH5(true);
//                pbHelp.goneLoading();
//            }
////            pbHelp.showNetError();
//        }
//    }

    // 添加订阅回调
    public void favoriteAddSuccess(Long l) {
        SouYueToast.makeText(this, R.string.favorite_add,
                SouYueToast.LENGTH_SHORT).show();
        hasFavorited = true;
        newsId = l;
        setResult();
        changeCollectState();
    }

    public void favoriteDeleteSuccess() {
        SouYueToast.makeText(this, R.string.favorite_del,
                SouYueToast.LENGTH_SHORT).show();
        hasFavorited = false;
        changeCollectState();
        setResult();
    }

    public void upSuccess(Long id) {
        hasUp = true;
        newsId = id;
        upCount++;
        changetUpState();
        setResult();
        callBackPHPInterface("", upCount);
    }

    public void shareSuccess(Long id) {
        newsId = id;
        SouYueToast.makeText(this, R.string.share_success,
                SouYueToast.LENGTH_SHORT).show();
    }

    public void newsCountSuccess(NewsCount newsCount) {
        hasFavorited = newsCount.hasFavorited();
        hasUp = newsCount.hasUp();
        upCount = newsCount.upCount();
        commentsCount = newsCount.commentsCount();
        setButtonState();
        if (isZAEwidget) {
            callBackPHPInterface(hasUp ? "1" : "0", upCount); // 调用接口传入点赞数量 add by
            // zhaobo
        }
    }

    @Override
    public void onBackPressed() {
        onCloseActivityClick(null);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        if (args != null) {
            String warningInfo = args.getString("warningInfo");
            AlertDialog gotoLogin = null;
            switch (id) {
                case 0:
                    gotoLogin = new AlertDialog.Builder(this)
                            .setMessage(warningInfo)
                            .setPositiveButton(R.string.go_login,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // 登陆
                                            gotoLogin();
                                        }
                                    })
                            .setNegativeButton(R.string.dialog_continue,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // 继续分享到精华区
                                            shareToDigest();
                                        }
                                    }).create();
                    break;
                case 1:
                    gotoLogin = new AlertDialog.Builder(this)
                            .setMessage(warningInfo)
                            .setPositiveButton(R.string.go_login,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // 登陆
                                            gotoLogin();
                                        }
                                    })
                            .setNegativeButton(R.string.dialog_cancel,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    }).create();
                    break;
            }
            return gotoLogin;
        } else {
            return super.onCreateDialog(id, args);
        }
    }

    private void shareIsEnable() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (shareIsOk && webLoadOk && share != null)
                    share.setEnabled(true);
            }

        }, 1000);
    }

    private void gotoLogin() {
        Intent intent = new Intent();
        intent.setClass(WebSrcViewActivity.this, LoginActivity.class);
        if (!ConfigApi.isSouyue()) {
            intent.putExtra(LoginActivity.Only_Login, true);
        }
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // 分享到精华区
    private void shareToDigest() {
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
//            htp.share(token, newsId);
        } else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(sourcePageUrl, StringUtils.shareTitle(sri.title(),
                            sri.description()), null != sri.image()
                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
                            .description(), pubDate() + "", sri.source(),
                    sri.keyword(), sri.srpId());
            mMainHttp.doRequest(share);
//            htp.share(token, sourcePageUrl, StringUtils.shareTitle(sri.title(),
//                            sri.description()), null != sri.image()
//                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
//                            .description(), pubDate() + "", sri.source(),
//                    sri.keyword(), sri.srpId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == 100) {
            if (SouyueAPIManager.isLogin()) {
                User user = SYUserManager.getInstance().getUser();
//                String userStr = JSON.toJSONString(user);
                String userStr = new Gson().toJson(user);
                mWebView.loadUrl("javascript:onLoginSuccess('" + userStr + "')");
            }
        }
        if (requestCode == START_FOR_RESULT) {
            commentsCount = data.getExtras().getInt("comment_count")
                    + Integer.parseInt(tv_read_commentcount.getText()
                    .toString());

            if (commentsCount > 0) {
                if (tv_read_commentcount != null) {
                    tv_read_commentcount.setText("" + commentsCount);
                    setResult();
                }

            }
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data
                    .getData();
            if(result != null){
                ContentResolver resolver = getContentResolver();
                String fileType = resolver.getType(result); //如果是图片，为image/jpeg或image/png等
                if(!(fileType.startsWith("image"))) {//判断用户选择的是否为图片
                    Toast.makeText(this, getResources().getString(R.string.is_not_image),Toast.LENGTH_SHORT).show();
                    result = null;
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == JS_FILE_CHOOSER_REQUEST_CODE) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                return;
            }
            if (android.os.Build.VERSION.SDK_INT < 19) {    //版本小于4.4
                mImagePath = ImageUtil.getImageRealPath(this, result);
            } else {
                // android.os.Build.VERSION_CODES.KITKAT
                // return result as /document/image%3A157215
                mImagePath = ImageUtil.getRealPathFromUri4Kitkat(this, result);
            }

            Bitmap bitmap = null;
            if (mImagePath != null && mImagePath.length() > 0) {
                bitmap = BitmapFactory.decodeFile(mImagePath);
            }
            if (bitmap != null) {
                uploadImage(bitmap);
            }
        } else if (requestCode == JS_FILE_CHOOSER_AND_CROP_REQUEST_CODE) {
            // 选择并按指定大小裁剪图片
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                return;
            }

            cropImage(result);
        } else if (requestCode == CROP_IMAGE_REQUEST_CODE) {
            if (data != null) {
                Bitmap bitmap = null;
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = extras.getParcelable("data");
                }

                if (bitmap != null) {
                    uploadImage(bitmap);
                }
            }
        }

    }

    /**
     * webview上传图片
     *
     * @param bitmap
     */
    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            // compress bitmap
            bitmap = ImageUtil.compressImage(bitmap);

            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            mImageUrl = Base64Utils.encode(bytes);

            try {
                mImageUrl = URLEncoder.encode(mImageUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Thread() {

            @Override
            public void run() {
                super.run();
                if (mRequestUrl.equals("") || mRequestUrl == null) {
                    return;
                }
                String jsonStr = HttpUtil.getUploadImageJsonByPost(mRequestUrl, mImageUrl);
                // parse json and get return image url
                final String img_url = HttpUtil.getImageUrlByParseJson(jsonStr);
                if (img_url != null && !img_url.equals("")) {
                    myHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            String call = "javascript:fileToUpload('" + img_url + "')";
                            mWebView.loadUrl(call);
                        }
                    });
                }
            }

        }.start();
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例 mCropWidth / mCropLength
        intent.putExtra("aspectX", mCropWidth);
        intent.putExtra("aspectY", mCropLength);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", mCropWidth);
        intent.putExtra("outputY", mCropLength);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
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

    @Override
    public void gotoShare() {
        Intent i = new Intent();
        if (SYUserManager.USER_ADMIN.equals(utype)) {
            i.setClass(this, SelfCreateActivity.class);
        } else {
            i.setClass(this, LoginActivity.class);
        }
        startActivity(i);
    }

    // private void toStartSrcPage() {
    // Intent intent = new Intent(this, WebSrcViewActivity.class);
    // Bundle bundle = new Bundle();
    // // sri.url_$eq(url);
    // bundle.putSerializable(WebSrcViewActivity.ITEM_INFO, sri);
    // // intent.putExtra("newsCount", "0");
    // intent.putExtras(bundle);
    //
    // startActivityForResult(intent, 18);
    // overridePendingTransition(R.anim.left_in, R.anim.left_out);
    // }

    @Override
    public void onJSClick(JSClick jsc) {
        try {
            // 每个页处理逻辑不一样
            toSRIObj(jsc);
            // 未抽取
			if (jsc.isShare()) {
				this.isShare = jsc.isShare();
				mWebView.post(new Runnable() {

					@Override
					public void run() {
						toShare();
					}
				});
				return;
			}
            // 未抽取
            if (jsc.isCheckappinstalled()) {
                checkAppInstalled(jsc.appname());
                return;
            }

            if (jsc.isShowbottommenu()) {
                toShowBottomMenu(jsc);
                return;
            }
            if (jsc.isStartUpVote()) {
                myHandler.sendEmptyMessage(2);
                // tradeUp();
                return;
            }
            if (jsc.isSetTilte()) {//设置标题
                handler.obtainMessage(7878, jsc.title()).sendToTarget();
                return;
            }

            if (jsc.isOpenMaps()) { //打开地图
                gotoMap(jsc);
                ;
            }

            isJsBack = ImJump2SouyueUtil.IMAndWebJump(WebSrcViewActivity.this,
                    jsc, sri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void gotoMap(JSClick jsc) {
        //高德地图
        String dat;
        Intent intent = null;
        List<PackageInfoMap> maps = getSystemMap();

        if (maps == null || maps.size() == 0) {
            Toast.makeText(this, "您还没有下载地图", Toast.LENGTH_LONG).show();
            return;
        }

        if (maps.size() == 1) {
            startup(maps.get(0), jsc);
        }
        if (maps.size() > 1) {
            //弹窗
            showWindow(maps, jsc);
        }

    }


    private void showWindow(List<PackageInfoMap> maps, JSClick jsc) {
        MapPopuWindow window = new MapPopuWindow(this, maps, jsc);
        window.showAtLocation(this.findViewById(R.id.web_parent), Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    }

    private void shadowWindow() {

    }

    private void startup(PackageInfoMap infoMap, JSClick jsc) {
        String dat;
        Intent intent = null;

        if ("com.autonavi.minimap.custom".equals(infoMap.getPackageName())) {
            dat = "androidamap://route?sourceApplication=softname&slat=" + jsc.getSlat() + "&slon=" + jsc.getSlng() + "&sname=" + jsc.getSname() + "&dlat=" + jsc.getDlat() + "&dlon=" + jsc.getDlng() + "&dname=" + jsc.getDname() + "&dev=" + jsc.getDev() + "&m=" + jsc.getM() + "&t=" + jsc.getType();
            intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse(dat));
            intent.setPackage("com.autonavi.minimap.custom");
            startActivity(intent);
        } else if ("com.baidu.BaiduMap".equals(infoMap.getPackageName())) {
            try {
                dat = "intent://map/direction?origin=latlng:" + jsc.getSlat() + "," + jsc.getSlng() + "|name:" + jsc.getSname() + "&destination=latlng:" + jsc.getDlat() + "," + jsc.getDlng() + "|name:" + jsc.getDname() +
                        "&mode=driving" + "&region=" + jsc.getRegion() + "&src=souyue#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                intent = Intent.getIntent(dat);
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
            startActivity(intent); //启动调用
        }
    }

    private List<PackageInfoMap> getSystemMap() {
        List<PackageInfoMap> maps = new ArrayList<PackageInfoMap>();
        List<PackageInfo> infos = this.getPackageManager().getInstalledPackages(0);
        PackageInfoMap map;

        for (PackageInfo info : infos) {
            String packageName = info.packageName;
            String applicationName = (String) this.getPackageManager().getApplicationLabel(info.applicationInfo);
            if ("com.autonavi.minimap.custom".equals(packageName) || "com.baidu.BaiduMap".equals(packageName)) {
                map = new PackageInfoMap(applicationName, packageName);
                maps.add(map);
            }
        }

        return maps;
    }

    /**
     * 检查应用是否安装，并且把检查之后的结果通知H5页面
     *
     * @param appNames
     */
    private void checkAppInstalled(String appNames) {
        if (appNames != null) {
            String[] appName = appNames.split(",");
            JSONArray contactArr = new JSONArray();
            for (int i = 0; i < appName.length; i++) {
                contactArr.put(getJson(appName[i], checkPackage(appName[i])));
            }
            // checkAppInstalledCallback 页面提供的方法
            mWebView.loadUrl("javascript:checkAppInstalledCallback('"
                    + contactArr.toString() + "')");
        }
    }

    /**
     * 检查应用是否安装
     *
     * @param packageName
     * @return
     */
    public int checkPackage(String packageName) {
        if (packageName == null || "".equals(packageName))
            return UNINSTALL;
        try {
            getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return INSTALL;
        } catch (NameNotFoundException e)

        {
            return UNINSTALL;
        }

    }

    /**
     * 获取json
     *
     * @param packName
     * @param isInstalled
     * @return
     */
    private JSONObject getJson(String packName, int isInstalled) {
        JSONObject j = new JSONObject();
        if (TextUtils.isEmpty(packName)) {
            return null;
        }
        try {
            j.put("k", packName);
            j.put("v", isInstalled);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j;
    }

    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        startActivity(intent);
        isJsBack = ImJump2SouyueUtil.JSLOGINBACK;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isJsBack == ImJump2SouyueUtil.JSLOGINBACK) {
            loadWebViewUrl();
            isJsBack = ImJump2SouyueUtil.JSNOBACK;
        }
        if (isJsBack == ImJump2SouyueUtil.JSRECHARGEBACK) {
            if (mWebView != null) {
                mWebView.reload();
            }
            isJsBack = ImJump2SouyueUtil.JSNOBACK;
        }
        if (mWebView != null) {
            mWebView.doOnResume();
        }
    }

    private void toSRIObj(JSClick jsc) {// 转换成SearchResultItem对象
        if (null == sri)
            sri = new SearchResultItem();
        sri.title_$eq(jsc.title());
        sri.keyword_$eq(jsc.keyword());
        sri.srpId_$eq(jsc.srpId());
        sri.md5_$eq(jsc.md5());
        sri.url_$eq(SUPPER_SHARE_URL = jsc.url());
        ArrayList<String> t = new ArrayList<String>();
        t.add(jsc.image());
        sri.image_$eq(new ArrayList<String>(t));
        sri.description_$eq(jsc.description());
        sri.callback_$eq(jsc.getCallback());
        sri.setType(jsc.getType());
//        sri.setType("web");
    }

    private void toShare() {// 调用分享菜单
        if (StringUtils.isEmpty(sri.url())) {
            return;
        }
        //分享链接设置
        if (ConfigApi.isSouyue() && !isFromTradeHome) {
//            htp.shortURL(sri.url());
            ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
            req.setParams(sri.url());
            CMainHttp.getInstance().doRequest(req);
        }

        loadShareImage();//预加载分享图片

        showShareWindow(null);
    }

    private void loadShareImage() {
        // 获取要分享的图片
        String str = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";
        if (!StringUtils.isEmpty(str)) {
            if (img == null) {
                img = new ImageView(WebSrcViewActivity.this);
            }
            // query.id(img).image(str, false, true);
            if (imageLoader != null && StringUtils.isNotEmpty(str)) {
                File file = imageLoader.getDiskCache().get(str);
                if (file != null) {
                    imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                } else {
                    imageLoader.displayImage(str, img, options, new ImageLoadingListener() {

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            if (mShareMenuDialog != null) {
                                mShareMenuDialog
                                        .setImageDownloadSucc(false);
                            }

                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                            imageBitmap = arg2;
                            if (mShareMenuDialog != null) {
                                mShareMenuDialog
                                        .setImageDownloadSucc(false);
                            }
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                            if (mShareMenuDialog != null) {
                                mShareMenuDialog
                                        .setImageDownloadSucc(false);
                            }
                        }

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            if (mShareMenuDialog != null) {
                                mShareMenuDialog
                                        .setImageDownloadSucc(true);
                            }
                        }

                    });
                }
            }
        }
    }

    /**
     * 刷新页面
     */
    @Override
    public void clickRefresh() {
        mWebView.loadUrl(currentURL);// TODO:刷新页面判断是否加密
    }

    /*
     * @Override public boolean isLogin() { return SouyueAPIManager.isLogin(); }
     * 
     * @Override public void login() { if(!SouyueAPIManager.isLogin()) {
     * SouyueAPIManager.goLoginForResult(this, 100); } }
     */

    /*
     * @Override public void redirectCouponDetail(long coupon_zsb) {
     * UIHelper.showCouponDetailByZsb(WebSrcViewActivity.this, coupon_zsb); }
     */

    class JsInterface implements DontObfuscateInterface {
        /**
         * 中搜币商城现金券JS回调，跳转到现金券详情
         *
         * @param coupon_zsb 现金券面额兑换的中搜币个数
         */
        @JavascriptInterface
        public void redirectCouponDetail(long coupon_zsb) {
//            UIHelper.showCouponDetailByZsb(WebSrcViewActivity.this, coupon_zsb);
            SouYueToast.makeText(WebSrcViewActivity.this, R.string.notsupported, SouYueToast.LENGTH_SHORT);
        }

        /**
         * 跳转到商家首页接口 mall_id: 商家ID mall_name: 商家名称 mall_type: 商家类型 0-普通商家 1-合作商家
         * city: 如果是普通商家，需要传入商家所在城市
         */
        @JavascriptInterface
        public void redirectShopHome(String mall_idStr, String mall_name,
                                     int mall_type, String city) {
//            long mall_id = Long.parseLong(mall_idStr);
//            if (mall_type == SearchShop.TYPE_INSIDE) {
//                UIHelper.showEntHomeFromSouyue(WebSrcViewActivity.this,
//                        mall_name, mall_id, false);
//            } else {
//                SearchShop shop = new SearchShop();
//                shop.setSid(mall_id);
//                shop.setName(mall_name);
//                UIHelper.showCommonShopFromSouyue(WebSrcViewActivity.this,
//                        city, shop);
//            }
            SouYueToast.makeText(WebSrcViewActivity.this, R.string.notsupported, SouYueToast.LENGTH_SHORT);
        }

        /**
         * 跳转到普通商家的分店列表接口 mall_id: 商家ID lng: 经度(当前位置) lat: 纬度（当前位置） city:
         * 商家的所在城市
         */
        @JavascriptInterface
        public void redirectShopList(String mall_idStr, Double lng, Double lat,
                                     String city) {
//            long mall_id = Long.parseLong(mall_idStr);
//            SearchParam searchParam = new SearchParam();
//            searchParam.setCity(city);
//            searchParam.setSid((int) mall_id);
//            searchParam.setLat(lat);
//            searchParam.setLng(lng);
//            UIHelper.showEntSearchSubbranch(WebSrcViewActivity.this,
//                    searchParam);
            SouYueToast.makeText(WebSrcViewActivity.this, R.string.notsupported, SouYueToast.LENGTH_SHORT);
        }

        /**
         * 跳转到地图导航接口 mall_name: 商家名称 lng: 经度(商家位置) lat: 纬度(商家位置) address: 商家地址
         */
        @JavascriptInterface
        public void redirectShopMap(String mall_name, String lng, String lat,
                                    String address) {
//            UIHelper.goToMapLocation(WebSrcViewActivity.this, "", mall_name,
//                    Double.parseDouble(lng), Double.parseDouble(lat), address);
            SouYueToast.makeText(WebSrcViewActivity.this, R.string.notsupported, SouYueToast.LENGTH_SHORT);
        }

        /**
         * js端调用android进行webview图片上传
         */
        @JavascriptInterface
        public void selectPhoto(String requestUrl) {
            mRequestUrl = requestUrl;
            Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT, null);
            intentFromGallery.setType("image/*");
            startActivityForResult(intentFromGallery, JS_FILE_CHOOSER_REQUEST_CODE);
        }

        /**
         * js端调用android进行webview图片上传
         */
        @JavascriptInterface
        public void selectAndCropPhoto(String requestUrl, int width, int length) {
            mRequestUrl = requestUrl;
            mCropWidth = width;
            mCropLength = length;
            Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
            intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intentFromGallery, JS_FILE_CHOOSER_AND_CROP_REQUEST_CODE);

        }


    }

    class SouYueLoginInterface implements DontObfuscateInterface {

        public void login() {
            if (!SouyueAPIManager.isLogin()) {
                SouyueAPIManager.goLoginForResult(WebSrcViewActivity.this, 100);
            }
        }

        public boolean isLogin() {
            return SouyueAPIManager.isLogin();
        }
    }

    @Override
    public void loadData(int args) {

        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(this)) {
            utype = SYUserManager.getInstance().getUserType();
            content = getShareContent();
            content.setKeyword(sri.keyword());
            content.setCallback(sri.callback());
            content.setSrpId(sri.srpId());
            Message msg = new Message();
            msg.obj = content;
            boolean islogin = (SYUserManager.getInstance().getUser().userType()
                    .equals(SYUserManager.USER_ADMIN));
            String shareUrl = sri.callback();
            switch (args) {
                case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                    if (islogin) {
                        if (isZAEwidget) {
                            //分享到搜悦好友 拼接md5 add by zhaobo
                            sourcePageUrl = Utils.linkUrlWidget(sourcePageUrl, "md5", zaeMd5);
                            content.setUrl(ZSEncode.encodeURI(sourcePageUrl));
                        }
                        if (SHARE_TYPE.equals(sri.getType())) {
                            shareToSouyueFriend(content);
                        } else {
                            ImShareNews imsharenews = new ImShareNews(
                                    content.getKeyword(), content.getSrpId(),
                                    content.getTitle(), content.getUrl(),
                                    content.getPicUrl());
//                            ContactsListActivity.startSYIMFriendAct(
//                                    WebSrcViewActivity.this, imsharenews);
                            IMShareActivity.startSYIMFriendAct(
                                    WebSrcViewActivity.this, imsharenews);
                        }
                        shareType = "1";
                    } else {
                        toLogin();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_DIGEST:
                    if (null != utype && !utype.equals("1")) {
                        Bundle b = new Bundle();
                        b.putString("warningInfo", String.format(
                                getString(R.string.share_syfriend_warning),
                                CommonStringsApi.APP_NAME_SHORT));
                        showDialog(0, b);
                    } else {
                        // 登陆用户直接分享到精华区
                        shareToDigest();
                        shareType = "4";
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_SINA:
                    if (!ConfigApi.isSouyue() && isFromTradeHome
                            && !StringUtils.isEmpty(shortUrl)) {
                        content.setUrl(shortUrl);
                    }
                    msg.what = ShareMenuDialog.SHARE_TO_SINA;
                    handler.sendMessage(msg);
                    shareType = "5";
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    if (!ConfigApi.isSouyue() && isFromTradeHome
                            && !StringUtils.isEmpty(shortUrl)) {
                        content.setUrl(shortUrl);
                    }
                    ShareByWeixin.getInstance().share(content, false);
                    shareType = "6";
                    break;
                case ShareMenuDialog.SHARE_TO_FRIENDS:
                    ShareByWeixin.getInstance().share(content, true);
                    shareType = "7";
                    break;
                case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                    isfreeTrial = SYUserManager.getInstance().getUser().freeTrial();
                    if (isfreeTrial) {
                        Dialog alertDialog = new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.share_mianshen))
                                .setPositiveButton(
                                        getString(R.string.alert_assent),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                share2SYwangyou();
                                                shareType = "3";
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.alert_cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        alertDialog.show();
                    } else {
                        share2SYwangyou();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_INTEREST:
                    LoginAlert loginDialog = new LoginAlert(
                            WebSrcViewActivity.this,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    shareToInterest();
                                    shareType = "2";
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                    loginDialog.show();
                    break;
                case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                    if (!StringUtils.isEmpty(sri.description())) {
                        content.setContent(sri.description());
                    }
                    ShareByTencentQQ.getInstance().share(this, content);
                    shareType = "11";
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                    if (!StringUtils.isEmpty(sri.description())) {
                        content.setContent(sri.description());
                    }
                    ShareByTencentQQZone.getInstance().share(this, content);
                    shareType = "12";
                    break;
                default:
                    break;
            }
//            if (!ConfigApi.isSouyue() && isShareFromJs) {
//                htp.shareSucCallback(shareUrl, shareType);
//            }
        } else {
            SouYueToast.makeText(WebSrcViewActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

    }

    private void share2SYwangyou() {
        Bundle b = new Bundle();
        if (null != utype && !utype.equals("1")) {
            b.putString("warningInfo", String.format(
                    getString(R.string.share_syfriend_warning),
                    CommonStringsApi.APP_NAME_SHORT));
            showDialog(1, b);
        } else {
            Intent i = new Intent();
            b.putSerializable("searchResultItem", sri);
            i.setClass(WebSrcViewActivity.this,
                    ShareToSouyueFriendsDialog.class);
            i.putExtras(b);
            i.putExtra("content", content.getContent());
            startActivity(i);
        }
    }

    private void shareToInterest() {
        com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
        interestmodel.setTitle(sri.title());
        interestmodel.setImages(sri.image());
        interestmodel.setKeyword(sri.keyword());
        interestmodel.setSrpId(sri.srpId());
        interestmodel.setBrief(sri.description());
        interestmodel
                .setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPESOURCE);
        interestmodel.setNewsUrl(SUPPER_SHARE_URL != null ? SUPPER_SHARE_URL
                : ZSEncode.encodeURI(sourcePageUrl));
        com.zhongsou.souyue.circle.ui.UIHelper.shareToInterest(
                WebSrcViewActivity.this, interestmodel);
    }

    private void shareToSouyueFriend(ShareContent content) {
        Posts mPost = new Posts();
        mPost.setTitle(content.getTitle());
        mPost.setContent(content.getContent());
        mPost.setImage_url(sri.image() == null ? "" : sri.image().get(0));
        mPost.setKeyword(content.getKeyword());
        mPost.setSrpId(content.getSrpId());
        mPost.setUrl(content.getUrl());
//        Intent intent = new Intent(WebSrcViewActivity.this, ImFriendActivity.class);
        Intent intent = new Intent(WebSrcViewActivity.this, IMShareActivity.class);
        intent.putExtra("Posts", mPost);
        intent.putExtra("isSYFriend", true);
        intent.putExtra("fromType", ImFriendActivity.FROM_TYPE_WEB);
        intent.putExtra("interest_logo", sri.image() == null ? "" : sri.image().get(0));
        intent.putExtra("isFromBlog", true);
        intent.putExtra("fromWhere", IMShareActivity.IM_SHARE_FROM_WEB);
        startActivity(intent);
    }

//    @Override
//    public void downloadFiction(String id, String name, String img,
//                                String length, String url, String version) {
//        ImJump2SouyueUtil.downloadFiction(WebSrcViewActivity.this, id, name,
//                img, length, url, version);
//    }
//
//    @Override
//    public void downloadVideo(String id, String name, String img,
//                              String length, String urls) {
//        ImJump2SouyueUtil.downloadVideo(this, id, name, img, length, urls);
//    }

    @Override
    public void getLocalCookie(String key) {
        String str = SYSharedPreferences.getInstance().getString("srp_" + key,
                "");
        try {
            mWebView.loadUrl("javascript:getLocalCookieCallback('"
                    + URLEncoder.encode(str, "utf-8").replace("+", "%20")
                    + "')");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLocalCookie(String key, String value) {
        SYSharedPreferences.getInstance().putString("srp_" + key, value);
    }

    @Override
    public String getFictionIndex(String novelId) {
        String str1 = ImJump2SouyueUtil.getIndex(novelId);
        Log.e("长度是：", str1.length() + "");
        return str1;
    }

    @Override
    public String getFictionContent(String novelId, int begin, int offset) {
        String str = ImJump2SouyueUtil.getContent(novelId, begin, offset);
        return str;
    }

    // add by trade
    private String appShareParams(String url) {
        try {
            if (!StringUtils.isEmpty(url))
                if (isFromTradeHome) {
                    return UrlConfig.urlContent.replace("client=souyue", "")
                            + "keyword="
                            + sysp.getString(SYSharedPreferences.CAROUSEL_SRP,
                            getString(R.string.SRP_KW))
                            + "&appname="
                            + MainApplication.getInstance().getResources()
                            .getString(R.string.IGID)
                            + "&srpId="
                            + sysp.getString(
                            SYSharedPreferences.CAROUSEL_SRPID,
                            getString(R.string.SRP_ID)) + "&url="
                            + URLEncoder.encode(url, "utf-8");
                } else {
                    return UrlConfig.urlContent.replace("client=souyue", "") + "keyword=" + URLEncoder.encode(sri.keyword(), "utf-8") + "&srpId=" + sri.srpId()
                            + "&url=" + URLEncoder.encode(url + "&opentype=src", "utf-8") + "&userId=" + SYUserManager.getInstance().getUser().userId() + CommonStringsApi.getUrlAppendIgId() + "opentype=src";
                }

        } catch (Exception uee) {
            Log.e("appShareParams", uee.getMessage());
        }
        return url;
    }

    /**
     * 有底部按钮的数据(活动微件特殊处理) zhaobo
     */
    private void initFromJSClick(SearchResultItem sri) {
        if (sri.pushId() == 0) {// >0 视为推送消息
            if (TextUtils.isEmpty(sri.url())) {
                sri.url_$eq(DEFAULT_PAGE);
            }
            if (isFromTradeHome && !StringUtils.isEmpty(sourcePageUrl)) {
//                htp.shortURL(appShareParams(sourcePageUrl));
                ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
                req.setParams(appShareParams(sourcePageUrl));
                CMainHttp.getInstance().doRequest(req);
            }
            if (sourcePageUrl != null) {
                shareIsOk = true;
                if (sourcePageUrl.endsWith("#extractnone"))
                    sourcePageUrl = sourcePageUrl
                            .replaceAll("#extractnone", "");
                if (sourcePageUrl.contains(getResources().getString(
                        R.string.trade_zae_domain))) {
                    isZAEwidget = true;
                }
                ReadHistoryHelper.getInstance().insert(sourcePageUrl);
                NewsCount newsCount = null;
                if (newsCount == null) {
//                    htp.newsCount(token, sourcePageUrl);
                    loadNewsCount(token, sourcePageUrl);
                }
            }
        } else {
//            htp.newsDetail(sri.keyword(), sri.srpId(), sri.pushId());
            loadNewsDetail(sri.keyword(), sri.srpId(), sri.pushId());
        }
    }

    /**
     * getShareBitmap:获取分享时的BitMap图片. <br/>
     *
     * @author zhaobo
     * @date 2015-3-30 下午3:57:36
     */
    private void getShareBitmap() {
        h5ShareImageUrl = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";

//        AQuery aQuery = new AQuery(WebSrcViewActivity.this);
//        aQuery.ajax(h5ShareImageUrl, File.class, new AjaxCallback<File>() {
//            @Override
//            public void callback(String url, File file, AjaxStatus status) {
//                if (file != null) {
//                    h5ShareBitmap = BitmapFactory.decodeFile(file
//                            .getAbsolutePath());
//                }
//            }
//
//            ;
//        });
        CMainHttp.getInstance().doDownload(new Random().nextInt(),
                getCacheDir().getAbsolutePath(),
                h5ShareImageUrl,
                null,
                new IVolleyResponse() {
                    @Override
                    public void onHttpResponse(IRequest request) {
                        File file = new File(request.getResponse().toString());
                        if (file != null) {
                            h5ShareBitmap = BitmapFactory.decodeFile(file
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

    // 活动微件处理底部菜单显示状态 by zhaobo 2014-11-6
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getShareBitmap();
                findViewById(R.id.web_src_bottom_bar).setVisibility(
                        View.VISIBLE);
                initFromJSClick(sri);
            } else if (msg.what == 2) {
                tradeUp();
            }
        }

        ;
    };

    /**
     * toShowBottomMenu:活动微件调用底部菜单显示. <br/>
     *
     * @param jsc
     * @author zhaobo
     * @date 2014-11-6 下午4:33:53
     */
    private void toShowBottomMenu(JSClick jsc) {
        boolean isShowBottomMenu = !(("").equals(jsc.keyword())
                || ("").equals(jsc.srpId()) || ("").equals(jsc.url())
                || ("").equals(jsc.image()) || ("").equals(jsc.title()));

        if (isShowBottomMenu) {
            sri.keyword_$eq(jsc.keyword());
            sri.srpId_$eq(jsc.srpId());
            sri.url_$eq(jsc.url());
            List<String> imgs = new ArrayList<String>();
            imgs.add(jsc.image());
            sri.image_$eq(imgs);
            sri.title_$eq(jsc.title());
            sri.description_$eq(jsc.description());
            sri.callback_$eq(jsc.getCallback());
            h5CallBackUrl = jsc.getCallback();
            sourcePageUrl = jsc.getZurl();
            isShareFromJs = true;
            zaeMd5 = jsc.md5();
            myHandler.sendEmptyMessage(1);
        }
    }

    /**
     * toTel:活动微件 打电话 <br/>
     *
     * @param num
     * @author zhaobo
     * @date 2014-11-3 上午11:17:15
     */
    private void toTel(String num) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * callBackPHPInterface:h5微件详情页调用PHP接口，点赞处理逻辑 <br/>
     *
     * @param state
     * @param num
     * @author zhaobo
     * @date 2015-3-30 下午1:55:56
     */
    private void callBackPHPInterface(String state, int num) {
        if (("").equals(state)) {
            mWebView.loadUrl("javascript:ShowVoteNum('" + num + "')");
        } else {
            mWebView.loadUrl("javascript:ShowPop('" + state + "','" + num
                    + "')");
        }
    }

    /**
     * tradeUp:点赞处理逻辑.
     *
     * @author zhaobo
     * @date 2015-3-31 下午2:40:35
     */
    private void tradeUp() {

        if (newsId > 0) {
            DetailUpReq up = new DetailUpReq(HttpCommon.DETAIL_UP, this);
            up.setParams(newsId);
            mMainHttp.doRequest(up);
//            htp.up(token, newsId);

        } else {
            DetailUpReq up = new DetailUpReq(HttpCommon.DETAIL_UP, this);
            up.setParams(sourcePageUrl, sri.title(), null != sri.image()
                            && sri.image().size() > 0 ? sri.image().get(0) : "",
                    sri.description(), pubDate(), sri.source(), sri.keyword(),
                    sri.srpId());
            mMainHttp.doRequest(up);
//            htp.up(token, sourcePageUrl, sri.title(), null != sri.image()
//                            && sri.image().size() > 0 ? sri.image().get(0) : "",
//                    sri.description(), pubDate(), sri.source(), sri.keyword(),
//                    sri.srpId());
        }
    }
}
