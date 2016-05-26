package com.zhongsou.souyue.ui.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.gson.Gson;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.RSAUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * 首页专题webview
 * Created by liuyihong on 15/6/5.
 */
public class CBaseWebView extends CustomWebView implements JavascriptInterface.ImagesListener{
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static String BLANK_URL = "about:blank";

    private ProgressBarHelper mProcess;
    private List<String> mImageUrls;
    private Activity mActivity;
    private Object mObj;
    private SearchResultItem sri;
    protected boolean mWebSuccess = false;
    private boolean mDestory = false;
    private String mUrl;

    private String result="";

    public CBaseWebView(Context context) {
        super(context);
        init(context);
    }

    public CBaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CBaseWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context _context){
        if (_context instanceof Activity){
            mActivity = (Activity) _context;
        }
        getSettings().setUseWideViewPort(false);

        if (ConfigApi.isUseWebViewImageBlock) {
            getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级
            getSettings().setBlockNetworkImage(true);//设置图片最后加载渲染
        }
        //优化先加载速度html
        if(Build.VERSION.SDK_INT >= 19) {
            getSettings().setLoadsImagesAutomatically(true);
        } else {
            getSettings().setLoadsImagesAutomatically(false);
        }
        setImagesListener(this);
        setWebViewClient();

        getmWebViewInterface().setJSClickListener(new JavascriptInterface.OnJSClickListener() {
            @Override
            public void onJSClick(JSClick json) {
                try {
                    final JSClick jsc = json;
                    if (jsc == null)
                        return;
                    jsc.init();
                    if (jsc.isEncrypt()) {
                        final String en = Utils.encryptJs(jsc.getContent());
                        post(new Runnable() {
                            @Override
                            public void run() {
                                loadUrl("javascript:" + jsc.getCallback() + "('"
                                        + en + "')");
                            }
                        });
                        return;
                    }
                    if (jsc.isGetUser()) {
                        final String en = Utils.encryptJs(ImJump2SouyueUtil.getUserInfo(context));
                        post(new Runnable() {
                            @Override
                            public void run() {
                                loadUrl("javascript:" + jsc.getCallback() + "('"
                                        + en + "')");
                            }
                        });
                        return;
                    }
                    if (jsc.isRSAEncrypt()) {
                        User user = SYUserManager.getInstance().getUser();
//                String param = "{'openid':'" + user.getOpenid() + "','opid':'" + user.getOpid()
//                        + "','appid':'" + user.getAppId() + " ','encryptiontype':'js','data':'" + jsc.getData() + "'}";

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("openid", user.getOpenid());
                        jsonObject.put("opid", user.getOpid());
                        jsonObject.put("appid", user.getAppId());
                        jsonObject.put("encryptiontype", "js");
                        jsonObject.put("data", jsc.getData());

                        try {
                            result = RSAUtils.privateEncrypt(jsonObject.toString(), user.getPrivate_key());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        post(new Runnable() {
                            @Override
                            public void run() {
                                loadUrl("javascript:" + jsc.getCallback() + "('"
                                        + result + "')");
                            }
                        });
                        return;
                    }
                    if (jsc.isGetRSAParam()) {
                        User user = SYUserManager.getInstance().getUser();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("openid", user.getOpenid());
                        jsonObject.put("opid", user.getOpid());
                        jsonObject.put("appid", user.getAppId());
                        jsonObject.put("auth_token", user.getAuth_token());

//                final String RSAparam = "{'openid':'" + user.getOpenid() + "','opid':'" + user.getOpid()
//                        + "','appid':'" + user.getAppId() + " ','auth_token':'" + user.getAuth_token() + "'}";

                        final String RSAparam = jsonObject.toString();

                        post(new Runnable() {
                            @Override
                            public void run() {
                                loadUrl("javascript:" + jsc.getCallback() + "('"
                                        + RSAparam + "')");
                            }
                        });
                        return;
                    }
                    click(jsc);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public boolean isDestory() {
        return mDestory;
    }

    public void setDestory(boolean mDestory) {
        this.mDestory = mDestory;
    }

    public void setActivity(Activity _ac){
        mActivity = _ac;
    }
    public Object getData() {
        return mObj;
    }

    public void setData(Object mObj) {
        this.mObj = mObj;
    }

    public ProgressBarHelper getmProcess() {
        return mProcess;
    }

    public void setmProcess(ProgressBarHelper mProcess) {
        this.mProcess = mProcess;
    }

    public boolean ismWebSuccess() {
        return mWebSuccess;
    }

    public void setmWebSuccess(boolean mWebSuccess) {
        this.mWebSuccess = mWebSuccess;
    }

    @Override
    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url)){
            Log.e(this.getClass().getName(),"url 为空了！");
            return;
        }
        mUrl = url;
        mWebSuccess=true;
        MakeCookie.synCookies(context, url);
        super.loadUrl(url);
    }

    // 设置WebView拦截里面的图片链接和查看原文
    private void setWebViewClient() {
        setWebViewClient(new WebViewClient() {

            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                if (mDestory){
                    return;
                }
                handler.proceed();
            }



            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mDestory){
                    return;
                }
                if(!getSettings().getLoadsImagesAutomatically()) {
                    getSettings().setLoadsImagesAutomatically(true);
                }
                if (ConfigApi.isUseWebViewImageBlock)
                    getSettings().setBlockNetworkImage(false);
                if(mWebSuccess&&!url.equals(BLANK_URL)) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProcess.goneLoading();
                        }
                    },200);
//                    post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mProcess.goneLoading();
//                        }
//                    });
                    setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mDestory){
                    return true;
                }
                if (!TextUtils.isEmpty(url)) {
                    if (url.toLowerCase().startsWith("showimage")) {
                        int imagepos = 0;
                        try {
                            imagepos = Integer.parseInt(url.substring(url.lastIndexOf("//") + 2, url.length()));

                            if (mImageUrls != null && mImageUrls.size() > 0 && imagepos < mImageUrls.size()) {
                                Intent intent = new Intent();
                                intent.setClass(getContext(), TouchGalleryActivity.class);
                                TouchGallerySerializable tg = new TouchGallerySerializable();
                                tg.setItems(mImageUrls);
                                tg.setClickIndex(imagepos);
                                Bundle extras = new Bundle();
                                extras.putSerializable("touchGalleryItems", tg);
                                intent.putExtras(extras);
                                getContext().startActivity(intent);
                            }
                        } catch (Exception e) {

                        }
                    } else if (containsUGC(url)) {
                        Intent webViewIntent = new Intent();
                        webViewIntent.setClass(getContext(), WebSrcViewActivity.class);
                        webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
                        getContext().startActivity(webViewIntent);
                    }else{
                        toStartSrcPage(url, false);
                    }
                }
                // 处理内部点击url拦截 在自定义webview中打开，指向jianxing－fan
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d("callback","---------- webview erro"+description+"error url:"+failingUrl);
                if (mDestory){
                    return;
                }
                mProcess.showNetError();
                mWebSuccess=false;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


        setWebChromeClient(new CBaseWebChromeClient(mActivity) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
//                if (newProgress > 70 && !mIsShowLoading && CMainHttp.getInstance().isNetworkAvailable()) {
//                    pbHelper.goneLoading();
//                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                mActivity.startActivityForResult(
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
                mActivity.startActivityForResult(
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
                mActivity.startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);

            }
        });
    }

    public boolean containsUGC(String url) {
        if (!TextUtils.isEmpty(url))
            return url.toLowerCase().contains("ugc.groovy") || url.toLowerCase().contains("interest.content.groovy");
        return false;
    }

    public void toStartSrcPage(String url, boolean isClose) {
        Intent intent = new Intent(getContext(), WebSrcViewActivity.class);
        Bundle bundle = new Bundle();

        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.url_$eq(url);
        bundle.putSerializable(WebSrcViewActivity.ITEM_INFO, searchResultItem);

        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    @Override
    public void setImages(String iags) {

        if (null != iags) {
            mImageUrls = Arrays.asList(iags.trim().split(" "));
            com.tencent.mm.sdk.platformtools.Log.i("", "imageUrls size: " + mImageUrls.size());
        }
    }

//    @Override
//    public void onJSClick(String json) {
////        Toast.makeText(getContext(),json,Toast.LENGTH_LONG).show();
//
//
//    }

    public void click(JSClick jsc){
        //没处理，与公共方法不一致
        if (jsc.isInterest()) {//线上都截取了，这里也截取
            toInterest(jsc);
            return;
        }
        if (mObj instanceof SearchResultItem) {
            ImJump2SouyueUtil.IMAndWebJump(getContext(), jsc, (SearchResultItem) mObj);
        }else{
            ImJump2SouyueUtil.IMAndWebJump(getContext(), jsc, null);
        }
    }

    public void toInterest(JSClick jsc) {
        IntentUtil.gotoSecretCricleCard(getContext(),
                Long.parseLong(jsc.getInterest_id()));
    }

    public void updateWebViewFont(){
        initFontSize(DeviceInfo.getSize());
    }
}
