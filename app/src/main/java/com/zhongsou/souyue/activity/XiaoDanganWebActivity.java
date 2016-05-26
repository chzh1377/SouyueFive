package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ZoomButtonsController;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.webview.CustomWebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XiaoDanganWebActivity extends RightSwipeActivity{
	private String sourcePageUrl;
	private CustomWebView mWebView;
	private String keyword;
	private FrameLayout webview_parent;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.xiaodangan_web_view);
		setCanRightSwipe(true);
		initView();
		pbHelp = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
		initFromIntent();
        ((TextView)findViewById(R.id.activity_bar_title)).setText(keyword);
		mWebView.loadUrl(sourcePageUrl);
	}

 


	private void initView() {
		webview_parent = findView(R.id.webview_parent);
		mWebView = (CustomWebView) findViewById(R.id.webView);
		
		WebSettings settings = mWebView.getSettings();
		settings.setAppCacheEnabled(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		hideBuiltInZoomControls(mWebView);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
//		settings.setPluginsEnabled(true);
		settings.setPluginState(PluginState.ON);

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!TextUtils.isEmpty(url) && !url.startsWith("about:blank")) {// 跳过空白页
					view.loadUrl(url);// 在内部处理url跳转
				}
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				view.stopLoading();
				view.clearView();
				pbHelp.showNetError();
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);// 进度条
				if (newProgress > 70) {
					pbHelp.goneLoading();
				}
			}
		});
	}

	private void initFromIntent() {
		Intent i = this.getIntent();
		sourcePageUrl = i.getStringExtra("url");
		keyword = i.getStringExtra("keyword");
	}

	public void onCloseClick(View v) {
		onCloseActivityClick(v);
	}

 

	public void onCloseActivityClick(View v) {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


	private void hideBuiltInZoomControls(WebView view) {
		if (Build.VERSION.SDK_INT < 11) {
			try {
				Field field = WebView.class
						.getDeclaredField("mZoomButtonsController");
				field.setAccessible(true);
				ZoomButtonsController zoomCtrl = new ZoomButtonsController(view);
				zoomCtrl.getZoomControls().setVisibility(View.GONE);
				field.set(view, zoomCtrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				WebSettings settings = view.getSettings();
				Method method = WebSettings.class.getMethod(
						"setDisplayZoomControls", boolean.class);
				method.invoke(settings, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		onCloseActivityClick(null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (mWebView != null){
				mWebView.setVisibility(View.GONE);
				if (webview_parent != null)
					webview_parent.removeView(mWebView);
		        mWebView.destroy();
		        mWebView = null;
			}
		} catch (Exception ex){
			
		}
	}
}
