package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ImagesListener;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * 图片搜索
 *
 * @author iamzl
 */
@SuppressLint("ValidFragment")
public class PhotoSearchFragment extends SRPFragment implements ImagesListener {

    public static final int layoutId = R.layout.photo_search;
    private CustomWebView photo_webview;
    public List<String> imageUrls;
    private RelativeLayout webview_parent;

    public PhotoSearchFragment(Context context, NavigationBar nav) {
        super(context, nav);
    }

    public PhotoSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");
        View view = View.inflate(activity, layoutId, null);
        inits(view);
        return view;
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
        if (photo_webview != null) {
            LogDebugUtil.i("PhotoSearchFragment url=======================>>", nav.url());
            photo_webview.loadUrl(this.nav.url());
        }
    }

    @Override
    protected void inits(View view) {
        webview_parent = (RelativeLayout) view.findViewById(R.id.webview_parent);
        photo_webview = (CustomWebView) view.findViewById(R.id.photo_webview);
        photo_webview.setImagesListener(this);
        initWebView();
        createPBHelper(view.findViewById(R.id.ll_data_loading), nav);
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        pbHelper.showNetError();
//    }

    @Override
    public void setImages(String images) {
        imageUrls = Arrays.asList(images.trim().split(" "));
    }

    @Override
    public void createPBHelper(View view, final NavigationBar nav) {
        pbHelper = new ProgressBarHelper(activity, view);
        pbHelper.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            public void clickRefresh() {
                photo_webview.reload();
            }
        });
    }

    public void initWebView() {
        WebSettings settings = photo_webview.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT <= 9) {
//            settings.setPluginsEnabled(true);
        } else if (Build.VERSION.SDK_INT <= 10) {
            settings.setPluginState(PluginState.ON);
        } else {
            settings.setPluginState(PluginState.ON);
        }
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        Utils.hideBuiltInZoomControls(photo_webview);
        photo_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().startsWith("showimage")) {
                    int imagepos = 0;
                    try {
                        imagepos = Integer.parseInt(url.substring(url.lastIndexOf("//") + 2, url.length()));
                        if (imageUrls != null && imageUrls.size() > 0 && imagepos < imageUrls.size()) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), TouchGalleryActivity.class);
                            TouchGallerySerializable tg = new TouchGallerySerializable();
                            tg.setItems(imageUrls);
                            tg.setClickIndex(imagepos);
                            Bundle extras = new Bundle();
                            extras.putSerializable("touchGalleryItems", tg);
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                    } catch (Exception e) {

                    }

                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                boolean loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Do something
                view.stopLoading();
                view.clearView();
                pbHelper.showNetError();
            }
        });
        photo_webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 70) {
                    if (CMainHttp.getInstance().isNetworkAvailable(getActivity()))
                        pbHelper.goneLoading();
                }
            }

        });
    }
}