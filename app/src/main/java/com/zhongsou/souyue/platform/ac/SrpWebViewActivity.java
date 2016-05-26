package com.zhongsou.souyue.platform.ac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.FirstInActivity;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.SelfCreateActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.ShortCutInfo;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.share.ShareAllPlat;
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
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GetLocalCookieListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoInterestListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoShareListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoSrpListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ReadNovelContentListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ReadNovelDictionaryListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.SetLocalCookieListener;
import com.zhongsou.souyue.ui.webview.SrpWebViewClient;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SrpWebViewActivity extends BaseActivity
        implements
        IShareContentProvider,//
        OnClickListener,
        //
        GotoSrpListener,
        GotoShareListener,//
        OnJSClickListener, PickerMethod, GotoInterestListener,
        SetLocalCookieListener, GetLocalCookieListener,
        ReadNovelDictionaryListener, ReadNovelContentListener{
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
    private ImageView img = null;

//    AQuery query = new AQuery(SrpWebViewActivity.this);
    private String from;
    private LoginRefreshBC receiver ;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.srp_webview_activity);
        if (ConfigApi.isPrintWebViewLogToSDCard) {
            //Add by ygp 收集log日志服务
            startService(new Intent(this, LogService.class));
        }
        init();

    }

    private void init() {
        hr = new Handler();
        pbHelp = new ProgressBarHelper(this, null);
//        htp = new Http(this);
        initTitleBar();// 所有按钮置灰
        initFromIntent();// 初始化数据
        initView();
        //注册登录后或者私密圈订阅刷新广播

        if(receiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("subscribeState");
            filter.addAction(ConstantsUtils.LINK);
            receiver = new LoginRefreshBC();
            registerReceiver(receiver, filter);
        }
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


        mWebView.addJavascriptInterface(new JsInterface(), "ent");
        mWebView.addJavascriptInterface(new SouYueLoginInterface(), "souyue");
        mWebView.setGotoSrpListener(this);
        mWebView.setGotoShareListener(this);
        mWebView.setOnJSClickListener(this);
        mWebView.setGotoInterestListener(this);
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
        mWebView.setWebViewClient(new SrpWebViewClient(this));
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 70) {
                    pbHelp.goneLoading();
                }
                if (newProgress == 100) {
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
                i.setType("*/*");
                mContext.startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);

            }

        });
    }

    /**
     * ==============WebView方法定义===================
     */
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
            android.util.Log.i(LogService.TAG, "category：emptyWeb--onPageStart--网络:" + operator + "--机型:" + DeviceInfo.deviceName + "--版本:" + DeviceInfo.osVersion);
        }
        updataUI(view);
        if (web_src_loadProgress.getVisibility() == View.GONE) {
            web_src_loadProgress.setVisibility(View.VISIBLE);
        }
    }

    public void onLoadResource(WebView view, String url) {
        if (ConfigApi.isPrintWebViewLogToSDCard)
            android.util.Log.i(LogService.TAG, "category：emptyWeb--url=" + url);
    }


    public void onPageFinished(WebView view, String url) {
        if (ConfigApi.isPrintWebViewLogToSDCard)
            android.util.Log.i(LogService.TAG, "category：emptyWeb--onPageFinish");
        if (ConfigApi.isUseWebViewImageBlock) {
            if(mWebView != null) {
                mWebView.getSettings().setBlockNetworkImage(false);
            }
        }
        if(view != null) {
            updataUI(view);
            if (web_src_loadProgress.getVisibility() == View.VISIBLE) {
                web_src_loadProgress.setVisibility(View.GONE);
            }
        }
    }

    public void shouldOverrideUrlLoading(WebView view, String url) {
        currentUrl = url;
        if (!TextUtils.isEmpty(url) && !url.startsWith("about:blank")) {// 跳过空白页
            share.setEnabled(false);
        }
    }

    public void onReceivedError() {
        pbHelp.goneLoading();
        if (web_src_loadProgress.getVisibility() == View.VISIBLE) {
            web_src_loadProgress.setVisibility(View.GONE);
        }
        if (null != showErrorDialog())
            showErrorDialog().show();
    }

    public void onReceivedHttpAuthRequest(WebView view,
                                          HttpAuthHandler handler, String host, String realm) {
        if (null != showErrorDialog())
            showErrorDialog().show();
    }

    private void updataUI(WebView view) {
        button_back.setEnabled(view.canGoBack());
        button_forword.setEnabled(view.canGoForward());
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
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
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
        getShareData();
        sourcePageUrl = i.getStringExtra(PAGE_URL);// 无按钮的url

        homePageUrl = sourcePageUrl;
        currentUrl = homePageUrl;

        from = getIntent().getStringExtra("from");
        if (ShortCutInfo.FROM_SHORTCUT.equals(from)) {
            if (doFromShortcut()) {
                return;
            }
        }
    }

    private void getShareData() {
        if (sri == null){
            sri = new SearchResultItem();
            BaseInvoke invoke = (BaseInvoke) getIntent().getSerializableExtra("invoke");
            if (invoke==null){
                return;
            }
            sri.url_$eq(invoke.getUrl());
            List<String> lists = new ArrayList<String>();
            lists.add(invoke.getIconUrl());
            sri.image_$eq(lists);
            sri.title_$eq(invoke.getTitle());
            sri.description_$eq(invoke.getDesc());
            sri.keyword_$eq(invoke.getKeyword());
            sri.srpId_$eq(invoke.getSrpId());
        }
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
        this.unregisterReceiver(receiver);
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
        if (!TextUtils.isEmpty(str)) {
//			AQuery query = new AQuery(SrpWebViewActivity.this);
//            msharebitmap = query.getCachedImage(sri.image().get(0));
            try {
                msharebitmap =  BitmapUtil.decodeFile(PhotoUtils.getImageLoader().getDiskCache().get(sri.image().get(0)).getAbsolutePath());
            }catch (Exception e){

            }
        }
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
//    }


    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse response = request.getResponse();
                shareSuccess(response.getBodyLong("newsId"));
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortURLSuccess(request.<HttpJsonResponse>getResponse().getBodyString());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.SHARE_TO_PLATOM:
                SouYueToast.makeText(this, R.string.share_fail,
                        SouYueToast.LENGTH_SHORT).show();
        }
    }

    public void shareSuccess(Long id) {
        newsId = id;
        SouYueToast.makeText(this, R.string.share_success,
                SouYueToast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        if (homePageUrl.equals(currentUrl)) {
            onCloseActivityClick(null);
        } else {
            mWebView.loadUrl(homePageUrl);
            currentUrl = homePageUrl;
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
        intent.setClass(SrpWebViewActivity.this, LoginActivity.class);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // 分享到精华区
    private void shareToDigest() {
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
//            htp.share(token, newsId);
        }else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
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
        super.onActivityResult(requestCode,resultCode,data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        //登录后的刷新靠广播接收器来处理  
//        if (resultCode == -1 && !(requestCode == FILECHOOSER_RESULTCODE)) {
//            String url = mWebView.getUrl();
//            MakeCookie.synCookies(this, url);
//            mWebView.loadUrl(url);
//        }

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
        try {
            // 每个页处理逻辑不一样
            toSRIObj(jsc);

            // 未抽取
            if (jsc.isShare()) {
                toShare();
                return;
            }
            // 未抽取
            if (jsc.isCheckappinstalled()) {
                checkAppInstalled(jsc.appname());
                return;
            }
            ImJump2SouyueUtil.IMAndWebJump(SrpWebViewActivity.this, jsc, sri);
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
        sri.callback_$eq(jsc.getCallback());
    }

    private void toShare() {// 调用分享菜单
        if (StringUtils.isEmpty(sri.url())) {
            return;
        }

        loadShareImage();//预加载分享图片

        if (StringUtils.isNotEmpty(sri.url())) {
//            htp.shortURL(sri.url());
            getShortUrl(sri.url());
        }

    }

    private void getShortUrl(String url)
    {
        ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID,this);
        req.setParams(url);
        CMainHttp.getInstance().doRequest(req);
    }
    private void loadShareImage() {
        //获取要分享的图片
        String str = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";
        if (!StringUtils.isEmpty(str)) {
            if (img == null) {
                img = new ImageView(SrpWebViewActivity.this);
            }
            //query.id(img).image(str, false, true);
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,str,img, MyDisplayImageOption.defaultOption);
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
        if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
            switch (position) {
                case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                    boolean islogin = (SYUserManager.getInstance().getUser()
                            .userType().equals(SYUserManager.USER_ADMIN));
                    if (islogin) {
                        ImShareNews imsharenews = new ImShareNews(
                                content.getKeyword(), content.getSrpId(),
                                content.getTitle(), content.getSharePointUrl(),
                                content.getPicUrl());
//                        ContactsListActivity.startSYIMFriendAct(
//                                SrpWebViewActivity.this, imsharenews);
                        IMShareActivity.startSYIMFriendAct(
                                SrpWebViewActivity.this, imsharenews);
                    } else {
                        toLogin();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_DIGEST:
                    // 判断用户是否登陆
                    if (null != utype && !utype.equals("1")) {

                        LoginAlert loginDialog = new LoginAlert(
                                SrpWebViewActivity.this,
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
                            SrpWebViewActivity.this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    Log.i("SrpWebViewActivity Url", content.getUrl());
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
                                SrpWebViewActivity.this,
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
                case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                    if (!StringUtils.isEmpty(sri.description())) {
                        content.setContent(sri.description());
                    }
                    ShareByTencentQQ.getInstance().share(SrpWebViewActivity.this,
                            content);
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                    if (!StringUtils.isEmpty(sri.description())) {
                        content.setContent(sri.description());
                    }
                    ShareByTencentQQZone.getInstance().share(
                            SrpWebViewActivity.this, content);
                    break;
                default:
                    break;
            }
            content = null;
        } else {
            SouYueToast.makeText(SrpWebViewActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

    }

    private void showShareWindow() {
        hr.post(new Runnable() {
            @Override
            public void run() {
                if(!SrpWebViewActivity.this.isFinishing()) {
                    if (mShareMenuDialog == null) {
                        mShareMenuDialog = new ShareMenuDialog(
                                SrpWebViewActivity.this, SrpWebViewActivity.this,
                                ShareConstantsUtils.SEARCH);
                    }
                    mShareMenuDialog.showBottonDialog(0);
                }
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
            return WebSrcViewActivity.UNINSTALL;
        try {
            getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return WebSrcViewActivity.INSTALL;
        } catch (NameNotFoundException e)

        {
            return WebSrcViewActivity.UNINSTALL;
        }
    }

    class JsInterface implements DontObfuscateInterface {
        /**
         * 中搜币商城现金券JS回调，跳转到现金券详情
         *
         * @param coupon_zsb 现金券面额兑换的中搜币个数
         */
        @JavascriptInterface
        public void redirectCouponDetail(long coupon_zsb) {
//            UIHelper.showCouponDetailByZsb(SrpWebViewActivity.this, coupon_zsb);
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
//                UIHelper.showEntHomeFromSouyue(SrpWebViewActivity.this,
//                        mall_name, mall_id, false);
//            } else {
//                SearchShop shop = new SearchShop();
//                shop.setSid(mall_id);
//                shop.setName(mall_name);
//                UIHelper.showCommonShopFromSouyue(SrpWebViewActivity.this,
//                        city, shop);
//            }
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
//            UIHelper.showEntSearchSubbranch(SrpWebViewActivity.this,
//                    searchParam);
        }

        /**
         * 跳转到地图导航接口 mall_name: 商家名称 lng: 经度(商家位置) lat: 纬度(商家位置) address: 商家地址
         */
        @JavascriptInterface
        public void redirectShopMap(String mall_name, String lng, String lat,
                                    String address) {
//            UIHelper.goToMapLocation(SrpWebViewActivity.this, "", mall_name,
//                    Double.parseDouble(lng), Double.parseDouble(lat), address);
        }
    }

    class SouYueLoginInterface implements DontObfuscateInterface {

        public void login() {
            if (!IntentUtil.isLogin()) {
                IntentUtil.goLoginForResult(SrpWebViewActivity.this, 100);
            }
        }

        public boolean isLogin() {
            return IntentUtil.isLogin();
        }
    }

//    @Override
//    public void downloadFiction(String id, String name, String img,
//                                String length, String url, String version) {
//        ImJump2SouyueUtil.downloadFiction(SrpWebViewActivity.this, id, name,
//                img, length, url, version);
//    }

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

    private boolean doFromShortcut() {
        if (StringUtils.isNotEmpty(SYUserManager.getInstance().getToken())) {
            if (!MainApplication.getInstance().isRunning()) {
                Intent mainIntent = new Intent(this, CommonStringsApi.getHomeClass());
                mainIntent.putExtra("from", ShortCutInfo.FROM_SHORTCUT);
                ShortCutInfo info = new ShortCutInfo();
                info.setUrl(sourcePageUrl);
                info.setGoTo(ShortCutInfo.GO_TO_M_SEARCH);
                mainIntent.putExtra(MainActivity.SHORTCUT_EXTRA, info);
                startActivity(mainIntent);
                finish();
                return true;
            }
        } else { //极其特殊的情况，即当用户清空所有搜悦数据，包括用户信息后，点击快捷方式直接走引导页
            Intent i = new Intent();
            i.setClass(SrpWebViewActivity.this, FirstInActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    public CustomWebView getmWebView() {
        return mWebView;
    }

    public void setmWebView(CustomWebView mWebView) {
        this.mWebView = mWebView;
    }
    /**
     * 登录或者订阅
     *
     */
    class LoginRefreshBC extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int f = intent.getIntExtra(com.tuita.sdk.Constants.TYPE, 0);
            if((action != null && "subscribeState".equals(action))
                    ||(action.equals(ConstantsUtils.LINK)&&f==40)) {
                String url = mWebView.getUrl();
                MakeCookie.synCookies(context, url);
                mWebView.loadUrl(url);
            }
        }

    }
}
