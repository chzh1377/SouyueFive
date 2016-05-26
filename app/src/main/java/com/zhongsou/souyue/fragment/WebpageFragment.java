package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

/**
 * 网页订阅类型
 */
@SuppressLint("ValidFragment")
public class WebpageFragment extends SRPFragment implements OnJSClickListener {

    public static final int layoutId = R.layout.webpage_search;
    private CustomWebView photo_webview;
    private RelativeLayout webview_parent;

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

    public WebpageFragment(Context context, NavigationBar nav) {
        super(context, nav);
    }

    public WebpageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState
                    .getSerializable("nav");
        View view = View.inflate(activity, layoutId, null);
        webview_parent = (RelativeLayout) view
                .findViewById(R.id.webview_parent);
        inits(view);
        return view;
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (photo_webview != null) {
                photo_webview.setVisibility(View.GONE);
                if (webview_parent != null)
                    webview_parent.removeView(photo_webview);
                photo_webview.destroy();
                photo_webview = null;
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void loadData() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (photo_webview != null)
            LogDebugUtil.i("WebpageFragment url=======================>>",
                    nav.url());
        photo_webview.loadUrl(this.nav.url());
    }

    @Override
    protected void inits(View view) {
        photo_webview = (CustomWebView) view.findViewById(R.id.webpage_webview);
        photo_webview.setOnJSClickListener(this);
        initWebView();
        createPBHelper(view.findViewById(R.id.ll_data_loading), nav);
    }

//    public void searchResultSuccess(SearchResult searchResult, AjaxStatus as) {
//        // pbHelper.goneLoading();
//    }

    @Override
    public void onHttpError(IRequest request) {
        pbHelper.showNetError();
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        pbHelper.showNetError();
//    }

    @Override
    public void onJSClick(JSClick jsc) {
        if (!StringUtils.isEmpty(jsc)) {
            try {
                //未处理                
                if (jsc.isPasePage()) {
                    IntentUtil.skipDetailPage(context, jsc, 0, null, 0, null, null, null);
                    return;
                }
                //与KunlunJueFragment一致，                
                if (jsc.isInterest()) {
                    toInterest(jsc);
                    return;
                }
                ImJump2SouyueUtil.IMAndWebJump(getActivity(),jsc,null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    public void initWebView() {
        WebSettings settings = photo_webview.getSettings();
        settings.setAppCacheEnabled(true);
        if (CMainHttp.getInstance().isNetworkAvailable(getActivity()))
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        else
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        settings.setPluginsEnabled(true);
        settings.setPluginState(PluginState.ON);
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        Utils.hideBuiltInZoomControls(photo_webview);
        photo_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.stopLoading();
                view.clearView();
                if(pbHelper!=null){
                    pbHelper.goneLoading();
                    pbHelper.showNetError();
                    pbHelper.setFromH5(true);
                }
            }
            
        });
        photo_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        photo_webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 70) {
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

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != getActivity().RESULT_OK ? null
                    : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

}