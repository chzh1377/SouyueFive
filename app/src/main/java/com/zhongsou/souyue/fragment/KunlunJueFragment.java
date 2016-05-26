package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.platformtools.Log;
import com.tuita.sdk.Constants;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.NewsCount;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.detail.NewsCountReq;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
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
import com.zhongsou.souyue.ui.webview.CustomWebChromeClient;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoSrpListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片搜索
 *
 * @author iamzl
 */
@SuppressLint("ValidFragment")
public class KunlunJueFragment extends SRPFragment implements GotoSrpListener,
        OnJSClickListener, PickerMethod, IShareContentProvider,IVolleyResponse
{
    public static final int layoutId = R.layout.photo_search;
    private CustomWebView webview;
    private RelativeLayout webview_parent;
    public List<String> imageUrls;
    private int isJsBack;
    private User user;
    private CustomWebChromeClient customWebChromeClient;

    /**
     * 2014-10-24 web表单微件添加资源上传 by zhaobo
     */
    private ValueCallback<Uri> mUploadMessage;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public String shareUrl, webViewUrl, shortUrl; // 短链 zhaobo 2014-10-31
    public static final int START_FOR_RESULT = 1010;
    public SearchResultItem searchResultItem = new SearchResultItem();
    public boolean isBottomMenuShow = false;
    public boolean mIsShowLoading = false;


    private ShareMenuDialog mShareMenuDialog;
    private ImageView img = null;
    private String utype;
    private ShareContent content;
    private String shareType;
    private SearchResultItem sri;
    private Bitmap imageBitmap = null;
    private int position;
    private long newsId = 0;
    private String token = SYUserManager.getInstance().getToken();
    private SsoHandler mSsoHandler;
    private StringBuffer stringBuffer;
    private LoginReceiver loginreceiver;
    private CMainHttp mHttp;
    public KunlunJueFragment() {
    }

    public KunlunJueFragment(Context context, NavigationBar nav) {
        super(context, nav);
    }

    public void setType(String type) {
        super.type = type;
    }

    public void setKeyWord(String keyWord) {
        super.keyWord = keyWord;
    }

    public void setSrpid(String srpId) {
        super.srpId = srpId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState
                    .getSerializable("nav");
        View view = View.inflate(activity, layoutId, null);
        mHttp= CMainHttp.getInstance();
        inits(view);
        loginreceiver = new LoginReceiver();
        IntentFilter inf = new IntentFilter();
        inf.addAction(ConstantsUtils.LINK);
        getActivity().registerReceiver(loginreceiver, inf);
        return view;
    }

    private class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int f = bundle.getInt(Constants.TYPE);
            if(f==40){
                loadWebViewUrl();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (webview != null) {
                webview.setVisibility(View.GONE);
                if (webview_parent != null)
                    webview_parent.removeView(webview);
                webview.destroy();
                webview = null;
                getActivity().unregisterReceiver(loginreceiver);
                loginreceiver=null;
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadWebViewUrl();
    }

    private void loadWebViewUrl() {
        // 2.1 uid：登录或匿名用户id，推送通知使用uid
        // 2.2 anonymous=1|0：1为匿名用户，否则为真实注册用户
        // 2.3 wifi=1|0：是否是wifi环境。1是无线
        stringBuffer = new StringBuffer(this.nav.url());
        if (this.nav.url().contains("?"))
            stringBuffer.append("&anonymous=").append(
                    SYUserManager.getInstance().getUserType());//
        else {
            stringBuffer.append("?anonymous=").append(
                    SYUserManager.getInstance().getUserType());
        }
        stringBuffer
                .append("&uid=")
                .append(SYUserManager.getInstance().getUserId())
                .append("&wifi=")
                .append((CMainHttp.getInstance().isWifi(MainApplication.getInstance()) ? "1" : "0"))
                .append("&imei=")
                .append(DeviceUtil.getDeviceId(MainApplication.getInstance()))
                .append("&hasPic=")
                .append(SettingsManager.getInstance().isLoadImage() ? "1" : "0");
        MakeCookie.synCookies(getActivity(), stringBuffer.toString());
        if (webview != null) {
            webview.loadUrl(stringBuffer.toString());
        }
    }

    @Override
    protected void inits(View view) {
        webview_parent = (RelativeLayout) view
                .findViewById(R.id.webview_parent);
        webview = (CustomWebView) view.findViewById(R.id.photo_webview);
        webview.setOnJSClickListener(this);
        webview.setGotoSrpListener(this);
        initWebView();
        createPBHelper(view.findViewById(R.id.ll_data_loading), nav);
        pbHelper.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                webview.reload();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isJsBack == ImJump2SouyueUtil.JSLOGINBACK) {
            loadWebViewUrl();
            isJsBack = ImJump2SouyueUtil.JSNOBACK;
        }else{  //SYFX-1924
            MakeCookie.synCookies(getActivity(), stringBuffer.toString());
        }

    }

    @Override
    public void gotoSRP(String keyword, String srpId) {
        try {
            if (!TextUtils.isEmpty(keyword) /* && !TextUtils.isEmpty(srpId) */) {
                Intent intent = new Intent();
                intent.setClass(this.activity, SRPActivity.class);
                intent.putExtra("keyword", keyword);
                intent.putExtra("srpId", srpId);
                startActivity(intent);
                this.activity.overridePendingTransition(R.anim.left_in,
                        R.anim.left_out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJSClick(JSClick jsc) {
//        jsc.setType(type);          //貌似没用吧，这里给type赋值后会导致category为interest时候没法判断原有的type

        toSRIObj(jsc);
        try {
            user = SYUserManager.getInstance().getUser();

            //分享
            if (jsc.isShare()) {
                toShare();
                return;
            }

            //未处理            
//            if (jsc.isPasePage()) {
//                toReadablite(jsc);
//                return;
//            }
            //没处理，与公共方法不一致            
//            if (jsc.isInterest()) {                      //走ImJump2SouyueUtil.IMAndWebJump,需要判断是home类型还是card类型
//                toInterest(jsc);
//                return;
//            }
            //超级app需求：活动微件显示底部评论条
            if (jsc.isShowbottommenu()) {
                if (!ConfigApi.isSouyue()) {
                    toShowBottomMenu(jsc);
                    return;
                }
                return;
            }
    		// 项目展示微件 从详情页跳转到列表页新增类别 add by zhaobo 2015-03-23
			if (jsc.isRefreshBrowser()) {
				isJsBack = ImJump2SouyueUtil.JSLOGINBACK;
				IntentUtil.toBrowser(getActivity(), jsc);
				return;
			}
            isJsBack = ImJump2SouyueUtil.IMAndWebJump(getActivity(), jsc, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void toShare() {// 调用分享菜单
        if (StringUtils.isEmpty(sri.url())) {
            return;
        }

        loadShareImage();//预加载分享图片

        if (StringUtils.isNotEmpty(sri.url())) {
//            http.shortURL(sri.url());
            ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID,this);
            req.setParams(sri.url());
            CMainHttp.getInstance().doRequest(req);
        }

        showShareWindow(null);
    }

    private void loadShareImage() {
        //获取要分享的图片
        String str = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";
        if (!StringUtils.isEmpty(str)) {
            if (img == null) {
                img = new ImageView(activity);
            }
//            if (query != null) {
//                query.id(img).image(str, false, true);
//            }
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,str,img, MyDisplayImageOption.defaultOption);
        }
    }

    private void showShareWindow(String keyword) {
        if (mShareMenuDialog == null) {
            if (StringUtils.isEmpty(keyword)) {
                mShareMenuDialog = new ShareMenuDialog(activity, this,
                        ShareConstantsUtils.WEBSRCVIEWKEYWORD);
            } else if (StringUtils.isSuperSrp(keyword, null) != 0) {
                mShareMenuDialog = new ShareMenuDialog(activity, this,
                        ShareConstantsUtils.SUPERSRP);
            } else {
                mShareMenuDialog = new ShareMenuDialog(activity, this,
                        ShareConstantsUtils.WEBSRCVIEW);
            }
        }
        if (StringUtils.isEmpty(keyword)) {
            mShareMenuDialog.showBottonDialog();
        }
    }

    public void initWebView() {
        WebSettings settings = webview.getSettings();
        settings.setAppCacheEnabled(true);
        if (CMainHttp.getInstance().isNetworkAvailable(getActivity()))
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        else
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        settings.setPluginsEnabled(true);
        settings.setPluginState(PluginState.ON);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

        if (ConfigApi.isUseWebViewImageBlock) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级
            settings.setBlockNetworkImage(true);//设置图片最后加载渲染
        }

        Utils.hideBuiltInZoomControls(webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.stopLoading();
                view.clearView();
                if (pbHelper != null) {
                    pbHelper.goneLoading();
                    pbHelper.showNetError();
                    pbHelper.setFromH5(true);
                    mIsShowLoading = true;
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (ConfigApi.isPrintWebViewLogToSDCard)
                    android.util.Log.i(LogService.TAG, "category--SRP--KunlunJueFragment--url=" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (ConfigApi.isPrintWebViewLogToSDCard)
                    android.util.Log.i(LogService.TAG, "category--SRP--KunlunJueFragment--onPageFinish");
                if (ConfigApi.isUseWebViewImageBlock){
                    if(webview!=null){
                        webview.getSettings().setBlockNetworkImage(false);
                    }
                }
                super.onPageFinished(view, url);
                mIsShowLoading = false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (ConfigApi.isPrintWebViewLogToSDCard) {
                    int CURRENT_NETWORK_STATUS = DeviceInfo.getNetWorkType(getActivity());
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
                    android.util.Log.i(LogService.TAG, "category--SRP--KunlunJueFragment--onPageStart--网络:" + operator + "--机型:" + DeviceInfo.deviceName + "--版本:" + DeviceInfo.osVersion);
                }
//                super.onPageStarted(view, url, favicon);
//                mIsShowLoading = false;
            }
        });

        webview.setWebChromeClient(customWebChromeClient = new CustomWebChromeClient(
                this) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 70 && !mIsShowLoading && CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
                    pbHelper.goneLoading();
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getActivity().startActivityForResult(
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
                getActivity().startActivityForResult(
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
                getActivity().startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);

            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        if (customWebChromeClient != null)
            customWebChromeClient.onActivityResult(requestCode, resultCode,
                    data);
        super.onActivityResult(requestCode, resultCode, data);

        // 超级APP表单微件 提交 by zhaobo 2014-10-24
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != getActivity().RESULT_OK ? null
                    : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    //add by trade

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!ConfigApi.isSouyue()) {
            if (isVisibleToUser) {
                loadNewsCount();
                if (isBottomMenuShow) {
                    if (getActivity() instanceof SRPActivity) {
                        ((SRPActivity) getActivity()).setBottomMenu(View.VISIBLE);
                    }
                }

            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    // 查询当前新闻的评论数目、赞和收藏的状态
    public void loadNewsCount() {
//        http.newsCount(getToken(), webViewUrl);
        NewsCountReq req = new NewsCountReq(HttpCommon.DETAIL_NEWS_COUNT_ID,this);
        req.setParams(getToken(),webViewUrl);
        mHttp.doRequest(req);

    }
    @Override
    public void onHttpResponse(IRequest _request) {
        switch (_request.getmId())
        {
            case HttpCommon.DETAIL_NEWS_COUNT_ID:
                newsCountSuccess(new Gson().fromJson(_request.<HttpJsonResponse>getResponse().getBody(), NewsCount.class));
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortURLSuccess(_request.<HttpJsonResponse>getResponse().getBodyString());
                break;
        }

    }

    @Override
    public void onHttpError(IRequest _request) {

    }
    @Override
    public void onHttpStart(IRequest _request) {

    }

    /**
     * 查询当前新闻的评论数目、赞和收藏的状态回调
     *
     * @param newsCount
     */
    public void newsCountSuccess(NewsCount newsCount) {
        ((SRPActivity) getActivity()).changeNewsCount(newsCount);
    }

    private String getToken() {
        return SYUserManager.getInstance().getUser() == null ? ""
                : SYUserManager.getInstance().getUser().token();
    }

    public void shortURLSuccess(String url) {
        this.shortUrl = url;
    }

    /**
     * toShowBottomMenu:是否显示底部导航. <br/>
     *
     * @author fm
     * @date 2014年11月3日 上午10:16:36
     */
    public void toShowBottomMenu(JSClick jsc) {
        if (jsc != null) {
            shareUrl = jsc.url();
            webViewUrl = jsc.getZurl();
            searchResultItem.keyword_$eq(jsc.keyword());
            searchResultItem.srpId_$eq(jsc.srpId());
            searchResultItem.url_$eq(jsc.url());
            searchResultItem.image_$eq(jsc.imgs());
            searchResultItem.title_$eq(jsc.title());
            searchResultItem.description_$eq(jsc.description());
            searchResultItem.callback_$eq(jsc.getCallback());
            isBottomMenuShow = jsc.isShowbottommenu();
            if (!StringUtils.isEmpty(searchResultItem.url())) {
//                http.shortURL(appShareParams(searchResultItem.url()));
                ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID,this);
                req.setParams(appShareParams(searchResultItem.url()));
                CMainHttp.getInstance().doRequest(req);
            }
            if (((SRPActivity) getActivity()).getCurrentFragment().nav.url()
                    .equals(this.nav.url())) {
                loadNewsCount();
                if (getActivity() instanceof SRPActivity) {
                    ((SRPActivity) getActivity()).setBottomMenu(View.VISIBLE);
                }
            }
        }
    }


    public String appShareParams(String url) {
        try {
            if (!StringUtils.isEmpty(url))
                return UrlConfig.urlContent.replace("client=souyue", "")
                        + "keyword="
                        + searchResultItem.keyword()
                        + "&appname="
                        + MainApplication.getInstance().getResources()
                        .getString(R.string.IGID) + "&srpId="
                        + searchResultItem.srpId() + "&url="
                        + URLEncoder.encode(url, "utf-8");
        } catch (Exception uee) {
            Log.e("appShareParams", uee.getMessage());
        }
        return url;
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
        if (CMainHttp.getInstance().isNetworkAvailable(getActivity())) {

            content = getShareContent();

            switch (position) {
                case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                    boolean islogin = (SYUserManager.getInstance().getUser()
                            .userType().equals(SYUserManager.USER_ADMIN));
                    if (islogin) {
                        ImShareNews imsharenews = new ImShareNews(
                                content.getKeyword(), content.getSrpId(),
                                content.getTitle(), content.getSharePointUrl(),
                                content.getPicUrl());
                        ContactsListActivity.startSYIMFriendAct(
                                activity, imsharenews);
                    } else {
                        gotoLogin();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_DIGEST:
                    // 判断用户是否登陆
                    if (null != utype && !utype.equals("1")) {

                        LoginAlert loginDialog = new LoginAlert(
                                activity,
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
                            activity, content);
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
                                activity,
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
                    ShareByTencentQQ.getInstance().share(activity, content);
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                    if (!StringUtils.isEmpty(sri.description())) {
                        content.setContent(sri.description());
                    }
                    ShareByTencentQQZone.getInstance().share(activity, content);
                    break;
                default:
                    break;
            }
            content = null;
        } else {
            SouYueToast.makeText(activity,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    private void gotoLogin() {
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        startActivityForResult(intent, 0);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // 分享到精华区
    private void shareToDigest() {
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
//            http.share(token, newsId);
        }else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
            share.setParams(stringBuffer.toString(), StringUtils.shareTitle(sri.title(),
                            sri.description()), null != sri.image()
                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
                            .description(), pubDate() + "", sri.source(),
                    sri.keyword(), sri.srpId());
            mMainHttp.doRequest(share);
//            http.share(token, stringBuffer.toString(), StringUtils.shareTitle(sri.title(),
//                            sri.description()), null != sri.image()
//                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
//                            .description(), pubDate() + "", sri.source(),
//                    sri.keyword(), sri.srpId());
        }
    }

    private void share2SYFriends(ShareContent content) {
        Bundle b = new Bundle();
        Intent i = new Intent();
        b.putSerializable("searchResultItem", sri);
        i.setClass(getActivity(), ShareToSouyueFriendsDialog.class);
        i.putExtras(b);
        i.putExtra("content", content.getContent());
        i.putExtra("shareUrl", content.getSharePointUrl());
        startActivity(i);
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
//            imageBitmap = query.getCachedImage(sri.image().get(0));
            File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(sri.image().get(0));
            if(fileImage != null){
                imageBitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
            }
        }
        if (imageBitmap == null)
            imageBitmap = BitmapFactory.decodeResource(getResources(),
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
                sri.description()), shareurl, imageBitmap,
                StringUtils.shareDesc(sri.description()), str);

        return shareContent;
    }

    private void toSRIObj(JSClick jsc) {// 转换成SearchResultItem对象
        if (null == sri)
            sri = new SearchResultItem();
        sri.title_$eq(jsc.title());
        sri.keyword_$eq(jsc.keyword());
        sri.srpId_$eq(jsc.srpId());
        sri.md5_$eq(jsc.md5());
        sri.url_$eq(jsc.url());
        ArrayList<String> t = new ArrayList<String>();
        t.add(jsc.image());
        sri.image_$eq(new ArrayList<String>(t));
        sri.description_$eq(jsc.description());
        sri.callback_$eq(jsc.getCallback());
    }

}