package com.zhongsou.souyue.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.util.StringUtils;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.webview.CBaseWebView;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import java.io.File;

/**
 * 专题页面（5.0.5 补充需求）
 *
 * @author qubian
 * @data 2015年9月9日
 * @email naibbian@163.com
 */
public class SpecialTopicActivity extends BaseActivity implements View.OnClickListener, ProgressBarHelper.ProgressBarClickListener, PickerMethod, IShareContentProvider {

    private CBaseWebView mWebView;
    private String mChannel;
    private String mUrl;
    private String hasPic;
    private String wifi;
    private String mTitle;
    private String mSrpId;
    private String mKeyword;
    private String mContent;
    private String srpShareUrl;
    private String imgUrl;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.home_specialtopic_activity);
        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        mChannel = getIntent().getStringExtra("channel");
        mSrpId = getIntent().getStringExtra("Srpid");
        mContent = getIntent().getStringExtra("descreption");
        mKeyword = getIntent().getStringExtra("title");
//        imgUrl= getIntent().getStringExtra("imgUrl");
        imgUrl = CommonStringsApi.getSrpIconUrl(this, mSrpId);
        initView();
        setData();
    }

    private void initView() {
        findViewById(R.id.goBack).setOnClickListener(this);
        findViewById(R.id.special_share_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.activity_bar_title))
                .setText(mTitle == null ? "" : mTitle);
        mWebView = (CBaseWebView) findViewById(R.id.cover_webview);
        pbHelp = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);
        pbHelp.setFromH5(true);
    }


    private void setData() {
        mWebView.setmProcess(pbHelp);
        pbHelp.showLoadingUI();
        cleanWebviewData();
        wifi = CMainHttp.getInstance().isWifi(this) ? "1" : "0";
        hasPic = SYSharedPreferences.getInstance().getLoadWifi(this) ? "0" : "1";
        mWebView.setVisibility(View.GONE);
        if (mUrl != null) {
            mUrl = mUrl + "&hasPic=" + hasPic + "&wifi=" + wifi;
            if (StringUtils.isNotEmpty(mChannel)) {
                mUrl = mUrl + "&specilChannel=" + mChannel;
            }
            Log.i(this.getClass().getName(), "home load url:" + mUrl);
            MakeCookie.synCookies(this, mUrl);
            mWebView.loadUrl(mUrl);
        }
//        http = new Http(this);
        imageLoader = ImageLoader.getInstance();

    }

//    private Http http;

//    private void getNewsShortUrl() {
//        http.shortURL(mUrl);
//    }

    public void shortURLSuccess(String url) {
        blogShareUrl = url;
    }

    private void cleanWebviewData() {
        mWebView.clearFormData();
        mWebView.clearHistory();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goBack:
                super.onBackPressed();
                break;
            case R.id.special_share_btn:    //专题分享按钮
                if (pbHelp.isLoading) {
//                    SouYueToast.makeText(this, R.string.tg_alert_connect, Toast.LENGTH_SHORT).show();
                    return;
                }
                UpEventAgent.onZSZhuantiShare(this);    //ZSSDK
                this.srpShareUrl = UrlConfig.srp + com.zhongsou.souyue.utils.StringUtils.enCodeRUL(mKeyword)
                        + "&srpId=" + mSrpId + CommonStringsApi.getUrlAppendIgId();
                if (blogShareUrl == null && !com.zhongsou.souyue.utils.StringUtils.isEmpty(srpShareUrl)) {
//                    http.shortURL(srpShareUrl);
                    ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
                    req.setParams(srpShareUrl);
                    CMainHttp.getInstance().doRequest(req);
                }
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    SouYueToast.makeText(this, R.string.neterror,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mTitle == null || mTitle.equals("")) {
                    SouYueToast.makeText(this, R.string.wait_data, Toast.LENGTH_SHORT).show();
                } else {
                    showShareWindow();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()) {
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId()) {
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortURLSuccess(request.<HttpJsonResponse>getResponse().getBodyString());
                break;
        }
    }

    private ShareMenuDialog mCircleShareMenuDialog;

    private void showShareWindow() {
        mCircleShareMenuDialog = new ShareMenuDialog(this, this,
                ShareConstantsUtils.SRP);
        mCircleShareMenuDialog.showBottonDialog();
    }

    @Override
    public void clickRefresh() {
        pbHelp.showLoadingUI();
        if (mUrl != null) {
            MakeCookie.synCookies(this, mUrl);
            Log.i(this.getClass().getName(), "home load url:" + mUrl);
            mWebView.loadUrl(mUrl);
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
            ShareContent content = null;
            content = getShareContent();
            doShareNews(position, content);

        } else {
            SouYueToast.makeText(this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    private Bitmap imageBitmap;
    private String blogShareUrl;
//    private AQuery a = new AQuery(this);

    @Override
    public ShareContent getShareContent() {

        if (!TextUtils.isEmpty(imgUrl)) {
//            imageBitmap = a.getCachedImage(imgUrl);
            File file =PhotoUtils.getImageLoader().getDiskCache().get(imgUrl);
            if(file!=null)
            {
                imageBitmap = BitmapUtil.decodeFile(file.getAbsolutePath());
            }
            if (imageBitmap == null) {
                File cache = imageLoader.getDiscCache().get(imgUrl);
                if (cache != null) {
                    imageBitmap = BitmapFactory.decodeFile(cache.getAbsolutePath());
                }
            }
        } else {
            imageBitmap = null;
        }
        String contentStr = getString(R.string.srp_share_content);
        contentStr = String.format(contentStr, CommonStringsApi.APP_NAME,
                mKeyword);

        String url = blogShareUrl;
        if (com.zhongsou.souyue.utils.StringUtils.isEmpty(url)) {
            url = ZSEncode.encodeURI(com.zhongsou.souyue.utils.StringUtils.enCodeKeyword(srpShareUrl));
        }
        ShareContent result = new ShareContent(mKeyword, url, imageBitmap, contentStr,
                imgUrl);
        result.setSharePointUrl(srpShareUrl != null ? srpShareUrl : "");
        result.setKeyword(mKeyword);
        result.setSrpId(mSrpId);
        result.setTitle(mKeyword);
        result.setContent(contentStr);
        return result;
    }

    private SsoHandler mSsoHandler;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void doShareNews(int position, final ShareContent content) {
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
                        UpEventAgent.onZSTopicShare(this, "syfriend", mSrpId, content.getUrl());
                        ImShareNews imsharenews = new ImShareNews(
                                content.getKeyword(), content.getSrpId(),
                                content.getContent(), null, content.getPicUrl());
                        IMShareActivity.startSYIMFriendAct(this, imsharenews);
                    } else {
                        toLogin();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_SINA:
                    UpEventAgent.onZSTopicShare(this, "sina", mSrpId, content.getUrl());
                    mSsoHandler = ShareByWeibo.getInstance().share(this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    UpEventAgent.onZSTopicShare(this, "wx", mSrpId, content.getUrl());
                    ShareByWeixin.getInstance().share(content, false);
                    break;
                case ShareMenuDialog.SHARE_TO_FRIENDS:
                    String wxFriendUrl = content.getUrl();
                    if (null != wxFriendUrl
                            && wxFriendUrl.contains("urlContent.groovy?")) {
                        wxFriendUrl = wxFriendUrl.replace(
                                "urlContent.groovy?",
                                "urlContent.groovy?keyword="
                                        + com.zhongsou.souyue.utils.StringUtils.enCodeRUL(mKeyword)
                                        + "&srpId=" + mSrpId + "&");
                    }
                    content.setTitle(content.getContent());
                    content.setUrl(wxFriendUrl);
                    UpEventAgent.onZSTopicShare(this, "friend", mSrpId, content.getUrl());
                    ShareByWeixin.getInstance().share(content, true);
                    break;
                case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                    UpEventAgent.onZSTopicShare(this, "qfriend", mSrpId, content.getUrl());
                    ShareByTencentQQ.getInstance().share(this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                    UpEventAgent.onZSTopicShare(this, "qzone", mSrpId, content.getUrl());
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

    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        startActivity(intent);
    }
}
