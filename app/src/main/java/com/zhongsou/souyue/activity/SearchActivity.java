package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.db.ReadHistoryHelper;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NewsCount;
import com.zhongsou.souyue.module.NewsDetail;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.detail.NewsCountReq;
import com.zhongsou.souyue.net.detail.NewsDetailReq;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.share.SharePvRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.service.LogService;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoInterestListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoShareListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoSrpListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.ui.webview.SearchWebViewClient;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.SaveBitmap;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import java.util.ArrayList;


public class SearchActivity extends BaseActivity implements
        IShareContentProvider,//
        OnClickListener,//
        GotoSrpListener, GotoShareListener,//
        OnJSClickListener, PickerMethod, GotoInterestListener {
    public static final String DEFAULT_PAGE = "http://www.zhongsou.com/";
    public static final String PAGE_URL = "source_url";
    public static final String HASFAVORITED = "hasFavorited";
    public static final String ITEM_INFO = "searchResultItem";
    public static final String RESULT_HASFAVORITED = "hasFavorited";
    public static final String RESULT_HASUP = "hasUp";
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private String sourcePageUrl;
    private CustomWebView mWebView;
    private ProgressBar web_src_loadProgress;
    private View button_forword, button_back;
    private RelativeLayout webView_parent;
    SearchResultItem sri;
    //    private Http htp;
    private ImageButton share;
    private RelativeLayout up;
    private boolean hasFavorited, hasUp;
    private String token = SYUserManager.getInstance().getToken();
    private long newsId = 0;
    private String utype;
    private SsoHandler mSsoHandler;
    private boolean shareIsOk = false;
    private boolean webLoadOk = false;
    private Handler hr;
    private int upCount;
    private TextView upC;
    private ShareMenuDialog mShareMenuDialog;
    private ShareContent content;
    private String shortUrl;
    private Bitmap msharebitmap = null;
    private int position;
    private ProgressDialog progressDialog;
    private Object object = new Object();
    private Dialog mDialog = null;
    private String homePageUrl = "";
    private String currentUrl = "";
    private boolean isSelectImg = false;
    private String callBack = "";
    private CMainHttp mCMainHttp;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dismissProgress();
                    if (msg.obj != null) {
                        if ((Boolean) msg.obj) {
                            toShare();
                        }
                    }
                    break;
                case 101://加载javascript返回图片给后台
                    String json = String.valueOf(msg.obj);
                    mWebView.loadUrl("javascript:" + callBack + "('" + json + "')");//调用网页js接口将base64文件和图片参数发送给网页
                    break;
                default:
                    break;
            }
        }

    };

//    AQuery query = new AQuery(SearchActivity.this);

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.search_activity);

        if (ConfigApi.isPrintWebViewLogToSDCard) {
            //Add by ygp 收集log日志服务
            startService(new Intent(this, LogService.class));
        }
        init();
    }

    private void init() {
        hr = new Handler();
        pbHelp = new ProgressBarHelper(this, null);
        mCMainHttp = CMainHttp.getInstance();
        initTitleBar();// 所有按钮置灰
        initFromIntent();// 初始化数据
        initView();

        // 私密圈JS接口测试临时地址
        // sourcePageUrl = "http://tianshaojie.github.io/interest/";

        if (sourcePageUrl != null) {
            MakeCookie.synCookies(this, sourcePageUrl);
            mWebView.loadUrl(sourcePageUrl);
            pbHelp.showLoading();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_share:
                toShare();
                break;
            default:
                break;
        }
    }

    private void setButtonState() {
        changetUpState();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_HASFAVORITED, hasFavorited);
        intent.putExtra(RESULT_HASUP, hasUp);
        setResult(0, intent);
    }

    private void changetUpState() {
        if (upC != null)
            upC.setText("" + upCount);
        if (hasUp) {
            up.setEnabled(false);
        } else {
            up.setEnabled(true);
        }
    }

    private void initTitleBar() {
        button_forword = findViewById(R.id.button_forword);
        button_back = findViewById(R.id.button_back);
        share = (ImageButton) findViewById(R.id.button_share);
        share.setOnClickListener(this);
        button_forword.setEnabled(false);
    }

    private void initView() {
        web_src_loadProgress = (ProgressBar) findViewById(R.id.web_src_loadProgress);
        webView_parent = (RelativeLayout) findViewById(R.id.webView_parent);
        mWebView = (CustomWebView) findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
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

        mWebView.setGotoSrpListener(this);
        mWebView.setGotoShareListener(this);
        mWebView.setOnJSClickListener(this);
        mWebView.setGotoInterestListener(this);
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
        mWebView.setWebViewClient(new SearchWebViewClient(this));
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 70) {
                    pbHelp.goneLoading();
                }
                if (newProgress == 100) {
//                    mWebView.setVisibility(View.VISIBLE);
                    webLoadOk = true;
                    shareIsEnable();
                }
//                Log.i("", "the webview onProgressChanged newProgress"
//                        + newProgress);
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
            Log.i(LogService.TAG, "category--搜索页--onPageStart--网络:" + operator + "--机型:" + DeviceInfo.deviceName + "--版本:" + DeviceInfo.osVersion);
        }

        updataUI(view);
        if (web_src_loadProgress.getVisibility() == View.GONE) {
            web_src_loadProgress.setVisibility(View.VISIBLE);
        }
    }

    public void onPageFinished(WebView view, String url) {
        if (ConfigApi.isPrintWebViewLogToSDCard)
            Log.i(LogService.TAG, "category--搜索页--onPageFinish");
        if (ConfigApi.isUseWebViewImageBlock)
            mWebView.getSettings().setBlockNetworkImage(false);
        updataUI(view);
        if (web_src_loadProgress.getVisibility() == View.VISIBLE) {
            web_src_loadProgress.setVisibility(View.GONE);
        }
    }

    public void onLoadResource(WebView view, String url) {
        if (ConfigApi.isPrintWebViewLogToSDCard)
            Log.i(LogService.TAG, "category--搜索页--url=" + url);
    }


    public void shouldOverrideUrlLoading(WebView view, String url) {
        currentUrl = url;
        // updataUI(view);
        if (!TextUtils.isEmpty(url) && !url.startsWith("about:blank")) {// 跳过空白页
            share.setEnabled(false);
        }
    }

    private void updataUI(WebView view) {
        button_back.setEnabled(view.canGoBack());
        button_forword.setEnabled(view.canGoForward());
    }

    public void onReceivedError() {
//        mWebView.stopLoading();
        pbHelp.goneLoading();
        if (web_src_loadProgress.getVisibility() == View.VISIBLE) {
            web_src_loadProgress.setVisibility(View.GONE);
        }
//        mWebView.setVisibility(View.INVISIBLE);
        if (null != showErrorDialog())
            showErrorDialog().show();
    }

    public void onReceivedHttpAuthRequest(WebView view,
                                          HttpAuthHandler handler, String host, String realm) {
        if (null != showErrorDialog())
            showErrorDialog().show();
    }

    private Dialog showErrorDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                return null;
            }
            return mDialog;
        }

        mDialog = new Dialog(mContext, R.style.dialogfullscreen);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.webview_errordialog, null);
        mView.findViewById(R.id.webview_errordialog_btnRefresh)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mWebView.reload();
                    }
                });
        mView.findViewById(R.id.webview_errordialog_btnClose)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mContext.finish();
                    }
                });
        mDialog.setContentView(mView);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    mContext.finish();
                }
                return false;
            }
        });
        return mDialog;
    }

    /**
     * 初始数据
     */
    private void initFromIntent() {
        Intent i = this.getIntent();
        sri = (SearchResultItem) i.getSerializableExtra(ITEM_INFO);
        if (sri == null) {
            sourcePageUrl = i.getStringExtra(PAGE_URL);// 无按钮的url
        } else
            initFromIntentObj(sri, i);

        homePageUrl = sourcePageUrl;
        currentUrl = homePageUrl;
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
                    setButtonState();
                }
            }
        } else {
//            htp.newsDetail(sri.keyword(), sri.srpId(), sri.pushId());
            loadNewsDetail(sri.keyword(), sri.srpId(), sri.pushId());
        }
    }

    /**
     * 加载网络  新闻详情
     *
     * @param keyword
     * @param srpId
     * @param pushId
     */
    public void loadNewsDetail(String keyword, String srpId, Long pushId) {
        NewsDetailReq req = new NewsDetailReq(HttpCommon.DETAIL_NEWS_DETAIL_ID, this);
        req.setParams(keyword, srpId, pushId);
        mCMainHttp.doRequest(req);
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
        mCMainHttp.doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId()) {
            case HttpCommon.DETAIL_NEWS_COUNT_ID:
                newsCountSuccess(new Gson().fromJson(request.<HttpJsonResponse>getResponse().getBody(), NewsCount.class));
                break;
            case HttpCommon.DETAIL_NEWS_DETAIL_ID:
                newsDetailSuccess(new Gson().fromJson(request.<HttpJsonResponse>getResponse().getBody(), NewsDetail.class));
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse response = request.getResponse();
                shareSuccess(response.getBodyLong("newsId"));
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()) {
            case HttpCommon.DETAIL_NEWS_COUNT_ID:
                break;
            case HttpCommon.DETAIL_NEWS_DETAIL_ID:
                SouYueToast.makeText(this, "连接不可用", SouYueToast.LENGTH_SHORT);
                finish();
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                SouYueToast.makeText(this, R.string.share_fail,
                        SouYueToast.LENGTH_SHORT).show();
        }
    }

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

    public void onCloseClick(View v) {
        onCloseActivityClick(v);
    }

    public void onReloadUrlClick(View v) {
        if (CMainHttp.getInstance().isNetworkAvailable(mContext))
            mWebView.reload();
        else
            SouYueToast.makeText(getApplicationContext(),
                    getResources().getString(R.string.nonetworkerror), 0)
                    .show();
    }

    public void onBackwordUrlClick(View v) {
        LogDebugUtil.v("FAN", "onBack");
        mWebView.stopLoading();
        if (mWebView.canGoBack()) {
            LogDebugUtil.v("FAN", "web---3");
            mWebView.goBack();
        }
    }

    public void onForwordUrlClick(View v) {
        LogDebugUtil.v("FAN", "onForword");
        mWebView.stopLoading();
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    public void onCloseActivityClick(View v) {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ConfigApi.isPrintWebViewLogToSDCard) {
            //Add by ygp 收集log日志服务
            stopService(new Intent(this, LogService.class));
        }

        try {
            webView_parent.removeView(mWebView);
            mWebView.setVisibility(View.GONE);
            mWebView.destroy();
            mWebView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long pubDate() {
        long pubDate = 0;
        try {
            pubDate = Long.parseLong(sri.date());
        } catch (Exception e) {
        }
        return pubDate;
    }

    @Override
    public ShareContent getShareContent() {
        String str = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";
        if (msharebitmap == null)
            msharebitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.logo);
        ShareContent shareContent = null;
        String shareurl;
        if (StringUtils.isNotEmpty(shortUrl)) {
            shareurl = shortUrl;
        } else {
            // 解决首次打开分享不跳转详情页
            shareurl = ZSEncode.encodeURI(sri.url());
        }
        shareContent = new ShareContent(StringUtils.shareTitle(sri.title(),
                sri.description()), shareurl, msharebitmap,
                StringUtils.shareDesc(sri.description()), str);

        return shareContent;
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        if ("newsDetail".equals(methodName)) {
//            SouYueToast.makeText(this, "连接不可用", SouYueToast.LENGTH_SHORT);
//            finish();
//        }
//    }

    // 添加订阅回调
    public void favoriteAddSuccess(Long l) {
        SouYueToast.makeText(this, R.string.favorite_add,
                SouYueToast.LENGTH_SHORT).show();
        hasFavorited = true;
        newsId = l;
        setResult();
    }

//    public void favoriteDeleteSuccess(AjaxStatus as) {
//        SouYueToast.makeText(this, R.string.favorite_del,
//                SouYueToast.LENGTH_SHORT).show();
//        hasFavorited = false;
//        setResult();
//    }

    public void upSuccess(Long id) {
        hasUp = true;
        newsId = id;
        changetUpState();
        setResult();
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
        setButtonState();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            onCloseActivityClick(null);
        }
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
        intent.setClass(SearchActivity.this, LoginActivity.class);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // 分享到精华区
    private void shareToDigest() {
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
        } else {

            SharePvRequest.send("souyue", sourcePageUrl, SharePvRequest.EMPTY_RESPONSE);
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(sourcePageUrl, StringUtils.shareTitle(sri.title(),
                    sri.description()), null != sri.image()
                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
                            .description(), pubDate() + "", sri.source(),
                    sri.keyword(), sri.srpId());
            mMainHttp.doRequest(share);
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

        /**
         * 登陆后刷新cookies
         */
        boolean isBackSearchRefresh = true;//此变量只用来表示从详情页返回搜索页面屏蔽刷新页面
        if (data != null) {
            isBackSearchRefresh = data.getBooleanExtra("isBackSearchRefresh", true);
        }
        if (resultCode == -1 && isBackSearchRefresh) {
            String url = mWebView.getUrl();
            MakeCookie.synCookies(this, url);
            mWebView.loadUrl(url);
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data
                    .getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

        if (resultCode == 0xde) {
            currentUrl = homePageUrl;
        }

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

    @Override
    public void gotoInterest(long interest_id) {
        IntentUtil.gotoSecretCricleCard(this, interest_id);
    }

    @Override
    public void onJSClick(JSClick jsc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.stopLoading();
                }
            }
        });

        try {
            //未抽取            
            toSRIObj(jsc);
            // 打开新页（抽取）{"category":"pasePage","description":"","image":"","url":"http://sports.syd.com.cn/system/2014/08/29/010473947.shtml","title":"新页抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":true,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}
            // 打开M内新页（不抽取）{"category":"pasePage","description":"","image":"","url":"http://mtest.zhongsou.com/index#page_summary_detail?url\u003dhttp://sports.syd.com.cn/system/2014/08/29/010473947.shtml\u0026k\u003d李娜\u0026kid\u003d337842","title":"新页不抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":true,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}
            // 打开M内新页（不抽取）{"category":"pasePage","description":"","image":"","url":"http://mtest.zhongsou.com","title":"新页不抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":true,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}
            // 三方新页（不抽取）{"category":"original","description":"","image":"","url":"http://wapbaike.baidu.com/view/24211.htm?adapt\u003d1","title":"第三方页不抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":false,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}

//            文件上传js测试
//            if ("selectImg".equals(jsc.category())) {
//                callBack = jsc.getCallback();
//                isSelectImg = true;
//                SelectImg.getInstance(this).selectImage();
//            }

            //未抽取，与WebSrcViewActivity页跳转不一样            
            if (jsc.isGoLogin()) {
                toLogin();
                return;
            }

            if (!jsc.isShare()) {
                ImJump2SouyueUtil.IMAndWebJump(SearchActivity.this, jsc, sri);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void toSRIObj(final JSClick jsc) {// 转换成SearchResultItem对象
        if (null == sri)
            sri = new SearchResultItem();
        sri.title_$eq(jsc.title());
        sri.keyword_$eq(jsc.keyword());
        sri.srpId_$eq(jsc.srpId());
        sri.url_$eq(jsc.url());
        sri.md5_$eq(jsc.md5());
        ArrayList<String> t = new ArrayList<String>();
        t.add(jsc.image());
        sri.image_$eq(t);
        sri.description_$eq(jsc.description());
        //设置壁纸时，下载图片不要弹出对话框提示,自行处理
        if (jsc.isSetWallPaper()) {
            return;
        }
        if (jsc.image() != null) {
            if (sri.image().get(0) != null && !sri.image().get(0).equals("")
                    && sri.image().get(0).length() > 1) {
                showProgress("加载中,请稍候...");
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            msharebitmap = null;
                            msharebitmap = SaveBitmap.getImage(sri.image().get(
                                    0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 1;
                        message.obj = jsc.isShare();
                        mHandler.sendMessage(message);
                    }
                });
            } else {
                msharebitmap = null;
                if (jsc.isShare()) {
                    toShare();
                    return;
                }
            }
        }
    }

    private void toShare() {// 调用分享菜单
        if (StringUtils.isEmpty(sri.url())) {
            return;
        }
        if (StringUtils.isNotEmpty(sri.url())) {
//            htp.shortURL(sri.url());
            ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
            req.setParams(sri.url());
            CMainHttp.getInstance().doRequest(req);
        }
    }

    public void shortURLSuccess(String url) {
        shortUrl = url;
        content = getShareContent();
        showShareWindow();
    }

    @Override
    public void loadData(int position) {
        this.position = position;
        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(this)) {
            switch (position) {
                case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                    boolean islogin = (SYUserManager.getInstance().getUser()
                            .userType().equals(SYUserManager.USER_ADMIN));
                    if (islogin) {
                        ImShareNews imsharenews = new ImShareNews(
                                content.getKeyword(), content.getSrpId(),
                                content.getTitle(), content.getSharePointUrl(),
                                content.getPicUrl());
                        IMShareActivity.startSYIMFriendAct(
                                SearchActivity.this, imsharenews);
                    } else {
                        toLogin();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_DIGEST:
                    // 判断用户是否登陆
                    if (null != utype && !utype.equals("1")) {

                        LoginAlert loginDialog = new LoginAlert(
                                SearchActivity.this,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        shareToDigest();
                                    }
                                }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                        loginDialog.show();
                    } else {
                        // 登陆用户直接分享到精华区
                        shareToDigest();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_SINA:
                    mSsoHandler = ShareByWeibo.getInstance().share(
                            SearchActivity.this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    ShareByWeixin.getInstance().share(content, false);
                    break;
                case ShareMenuDialog.SHARE_TO_FRIENDS:
                    String wxFriendUrl = content.getUrl();
                    if (null != wxFriendUrl
                            && wxFriendUrl.contains("urlContent.groovy?")) {
                        wxFriendUrl = wxFriendUrl.replace(
                                "urlContent.groovy?",
                                "urlContent.groovy?keyword="
                                        + StringUtils.enCodeRUL(sri.keyword())
                                        + "&srpId=" + sri.srpId() + "&");
                    }
                    content.setUrl(wxFriendUrl);
                    ShareByWeixin.getInstance().share(content, true);
                    break;
                case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                    if (null != utype && !utype.equals("1")) {
                        LoginAlert loginDialog = new LoginAlert(
                                SearchActivity.this,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        share2SYFriends(content);
                                    }
                                }, CommonStringsApi.SHARE_JHQ_WARNING, 1);
                        loginDialog.show();
                    } else {
                        share2SYFriends(content);
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_QQFRIEND://4.1.1新增分享qq好友
                    ShareByTencentQQ.getInstance().share(SearchActivity.this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE://4.1.1新增分享qq空间
                    ShareByTencentQQZone.getInstance().share(SearchActivity.this, content);
                    break;
                default:
                    break;
            }
            content = null;
        } else {
            SouYueToast.makeText(SearchActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

    }

    private void showShareWindow() {
        hr.post(new Runnable() {
            @Override
            public void run() {
                if (mShareMenuDialog == null) {
                    mShareMenuDialog = new ShareMenuDialog(SearchActivity.this,
                            SearchActivity.this, ShareConstantsUtils.SEARCH);
                }
                mShareMenuDialog.showBottonDialog(0);
            }
        });
    }

    private void share2SYFriends(ShareContent content) {
        Bundle b = new Bundle();
        Intent i = new Intent();
        b.putSerializable("searchResultItem", sri);
        i.setClass(this, ShareToSouyueFriendsDialog.class);
        i.putExtras(b);
        i.putExtra("content", content.getContent());
        i.putExtra("shareUrl", content.getSharePointUrl());
        startActivity(i);
    }

    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        startActivityForResult(intent, 1001);
    }

    /**
     * @author xujun 显示进度条
     */
    public void showProgress(String message) {
        synchronized (object) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    /**
     * @author 取消等待框
     */
    public void dismissProgress() {

        synchronized (object) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                    progressDialog = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public CustomWebView getmWebView() {
        return mWebView;
    }

    public void setmWebView(CustomWebView mWebView) {
        this.mWebView = mWebView;
    }

}
