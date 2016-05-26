/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.zhongsou.souyue.ui.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import com.google.gson.Gson;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.ac.SrpWebViewActivity;
import com.zhongsou.souyue.ui.WebViewInterface;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.RSAUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * A convenient extension of WebView.
 */
public class CustomWebView extends WebView implements
		Serializable {
	private static final long serialVersionUID = 4450282837406464795L;

	private static final String APP_CACAHE_DIRNAME = "/webcache";// Add By YinGp

	Context context;

	private int mProgress = 100;

	private boolean mIsLoading = false;

	public String mLoadedUrl;

	private static boolean mBoMethodsLoaded = false;

	private static Method mOnPauseMethod = null;
	private static Method mOnResumeMethod = null;
	private static Method mSetFindIsUp = null;
	private static Method mNotifyFindDialogDismissed = null;

	// add by yinguanping js加密
	private String result = "";
	private JavascriptInterface mWebViewInterface;

	/**
	 * Constructor.
	 *
	 * @param context
	 *            The current context.
	 */
	public CustomWebView(Context context) {
		super(context);
		this.context = context;
		initializeOptions();
		loadMethods();
	}

	/**
	 * Constructor.
	 *
	 * @param context
	 *            The current context.
	 * @param attrs
	 *            The attribute set.
	 */
	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initializeOptions();
		loadMethods();
	}

	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initializeOptions();
		loadMethods();
	}

	/**
	 * Initialize the WebView with the options set by the user through
	 * preferences.
	 */
	public void initializeOptions() {
		WebSettings settings = getSettings();
		// User settings
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(false);
		settings.setSaveFormData(true);
		settings.setSavePassword(true);
		settings.setDefaultZoom(ZoomDensity.MEDIUM);
		String str = "";
		try {
			str = settings.getUserAgentString()
					+ (ConfigApi.isSouyue() ? "-souyue5.0" : "-superapp-"
							+ URLEncoder.encode(CommonStringsApi.APP_NAME,
									"utf-8") + "-souyue4.0");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		settings.setUserAgentString(str);

		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		// Technical settings
		settings.setSupportMultipleWindows(true);
		setLongClickable(true);
		setScrollbarFadingEnabled(true);
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		setDrawingCacheEnabled(true);

		CookieManager.getInstance().setAcceptCookie(true);
		// CookieManager.setAcceptFileSchemeCookies(true);

		/** Add By YinGuanPing webview Cache */

		// LOAD_DEFAULT
		// 默认加载方式，使用这种方式，会实现快速前进后退，在同一个标签打开几个网页后，关闭网络时，可以通过前进后退来切换已经访问过的数据，同时新建网页需要网络
		// - LOAD_NO_CACHE
		// - LOAD_NORMAL
		// * 这个方式跟LOAD_NO_CACHE方式相同，不使用缓存，如果没有网络，即使以前打开过此网页也不会使用以前的网页。
		// - LOAD_CACHE_ELSE_NETWORK
		// *
		// 这个方式不论如何都会从缓存中加载，除非缓存中的网页过期，出现的问题就是打开动态网页时，不能时时更新，会出现上次打开过的状态，除非清除缓存。
		// - LOAD_CACHE_ONLY
		// * 这个方式只是会使用缓存中的数据，不会使用网络。

		// 开启 DOM storage API 功能
		settings.setDomStorageEnabled(true);
		// 开启 database storage API 功能
		// settings.setDatabaseEnabled(true);
		// String cacheDirPath = context.getFilesDir().getAbsolutePath()
		// + APP_CACAHE_DIRNAME;
		// Log.i("CustomWebView", "cacheDirPath=" + cacheDirPath);

		// 应用可以有数据库
		settings.setDatabaseEnabled(true);
		String dbPath = context.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		Slog.d("callback", "db 缓存路径" + dbPath);
		settings.setDatabasePath(dbPath);
		// 应用可以有缓存
		settings.setAppCacheMaxSize(1024 * 1024 * 100);
		settings.setAppCacheEnabled(true);
		settings.setAllowFileAccess(true);
		if (CMainHttp.getInstance().isNetworkAvailable(context))
			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		else {
			settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 设置
																		// 缓存模式
		}
		if (!CMainHttp.getInstance().isNetworkAvailable(context)) {
			// clearCache(true);
			// clearHistory();
			// clearFormData();
			// context.deleteDatabase("webview.db");
			// context.deleteDatabase("webviewCache.db");
		}
		String appCaceDir = context.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		if (appCaceDir != null) {
			Slog.d("callback", "缓存路径" + appCaceDir);
			settings.setAppCachePath(appCaceDir);
		}

		/** Add by YinGP 提速webview */
		// settings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级
		// settings.setBlockNetworkImage(true);//设置图片最后加载渲染
		// this.setLayerType(View.LAYER_TYPE_HARDWARE, null);//给webview 开启硬件加速

		// // 设置数据库缓存路径
		// settings.setDatabasePath(cacheDirPath);
		// // 开启 Application Caches 功能
		// settings.setAppCacheEnabled(true);
		// // 设置 Application Caches 缓存目录
		// settings.setAppCachePath(cacheDirPath);

		// if (Build.VERSION.SDK_INT <= 7) {
		// settings.setPluginsEnabled(true);
		// } else {
		// settings.setPluginState(PluginState.ON);
		// }

		mWebViewInterface = new WebViewInterface(this,context);
		Log.e("mwebview","add js interface");
		this.addJavascriptInterface(mWebViewInterface,
				"JavascriptInterface");
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mScrollChanged != null) {
			mScrollChanged.scrollChanged(l, t, oldl, oldt);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (mMeasureChanged != null) {
			mMeasureChanged.measureChanged(0, 0);
		}
	}

	@Override
	public void loadUrl(String url) {
		if (url.contains("javascrpt")) {// 不明白？
			return;
		}
		if (isWebEncrypt(url)) {
			url = Utils.encryptWebUrl(url);
		}
		mLoadedUrl = url;
		super.loadUrl(url);
	}

	public void initFontSize(JSONObject json) {
		loadUrl("javascript:changeFontSize(" + json + ")");
	}

    public boolean isWebEncrypt(String url) {

        // if(url.contains("sy_c=")){
        // return false;
        // }
        if (url.contains("isEncryption=1")) {
            return true;
        }
        if (Utils.isExtraEncrypt(url)) {
            return true;
        }
        return false;
    }

	/**
	 * Inject the AdSweep javascript.
	 */
	public void loadAdSweep() {
		// super.loadUrl(ApplicationUtils.getAdSweepString(mContext));
	}

	/**
	 * Set the current loading progress of this view.
	 *
	 * @param progress
	 *            The current loading progress.
	 */
	public void setProgress(int progress) {
		mProgress = progress;
	}

	/**
	 * Get the current loading progress of the view.
	 *
	 * @return The current loading progress of the view.
	 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * Triggered when a new page loading is requested.
	 */
	public void notifyPageStarted() {
		mIsLoading = true;
	}

	/**
	 * Triggered when the page has finished loading.
	 */
	public void notifyPageFinished() {
		mProgress = 100;
		mIsLoading = false;
	}

	/**
	 * Check if the view is currently loading.
	 *
	 * @return True if the view is currently loading.
	 */
	public boolean isLoading() {
		return mIsLoading;
	}

	/**
	 * Get the loaded url, e.g. the one asked by the user, without redirections.
	 *
	 * @return The loaded url.
	 */
	public String getLoadedUrl() {
		return mLoadedUrl;
	}

	/**
	 * Reset the loaded url.
	 */
	public void resetLoadedUrl() {
		mLoadedUrl = null;
	}

	public boolean isSameUrl(String url) {
		if (url != null) {
			return url.equalsIgnoreCase(this.getUrl());
		}

		return false;
	}

	/**
	 * Perform an 'onPause' on this WebView through reflexion.
	 */
	public void doOnPause() {
		if (mOnPauseMethod != null) {
			try {
				mOnPauseMethod.invoke(this);
			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			}
		}
	}

	/**
	 * Perform an 'onResume' on this WebView through reflexion.
	 */
	public void doOnResume() {
		if (mOnResumeMethod != null) {
			try {

				mOnResumeMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			}
		}
	}

	public void doSetFindIsUp(boolean value) {
		if (mSetFindIsUp != null) {
			try {

				mSetFindIsUp.invoke(this, value);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			}
		}
	}

	public void doNotifyFindDialogDismissed() {
		if (mNotifyFindDialogDismissed != null) {
			try {
				mNotifyFindDialogDismissed.invoke(this);
			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void disableHardwareAcc() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	/**
	 * Load static reflected methods.
	 */
	private void loadMethods() {
		if (!mBoMethodsLoaded) {

			try {

				mOnPauseMethod = WebView.class.getMethod("onPause");
				mOnResumeMethod = WebView.class.getMethod("onResume");

			} catch (SecurityException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			} catch (NoSuchMethodException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			}

			try {

				mSetFindIsUp = WebView.class.getMethod("setFindIsUp",
						Boolean.TYPE);
				mNotifyFindDialogDismissed = WebView.class
						.getMethod("notifyFindDialogDismissed");

			} catch (SecurityException e) {
				mSetFindIsUp = null;
				mNotifyFindDialogDismissed = null;
			} catch (NoSuchMethodException e) {
				mSetFindIsUp = null;
				mNotifyFindDialogDismissed = null;
			}

			mBoMethodsLoaded = true;
		}
	}

//	private ButtonListener bListener;
//	private JavascriptInterface.ImagesListener    imgListenter;
	private JavascriptInterface.GotoSrpListener   goSrpListenter;
//	private OpenAdListener openAdListener;
	private JavascriptInterface.GotoShareListener gotoShareListener;
//	private JavascriptInterface.OnJSClickListener JSClickListener;
//	private GotoInterestListener gotoInterestListener;
//	private ReadNovelDictionaryListener readNovelDictionaryListener;
//	private ReadNovelContentListener readNovelContentListener;
//	private DownloadRadioListener downloadRadioListener;
//	private DownloadNovelListener downloadNovelListener;
//	private SetLocalCookieListener cookieListener;
//	private GetLocalCookieListener cookieListener2;
	private onScrollChangedListener               mScrollChanged;
	private onMeasureChangedListener              mMeasureChanged;

	public void setmMeasureChanged(onMeasureChangedListener mLayoutChanged) {
		this.mMeasureChanged = mLayoutChanged;
	}

	public void setmScrollChanged(onScrollChangedListener mScrollChanged) {
		this.mScrollChanged = mScrollChanged;
	}

	// private RedirectCouponDetail redirectCouponDetail;
	/*
	 * private LoginListener loginListener; private IsLoginListener
	 * isLoginListener;
	 */

	/*
	 * public void setLoginListener(LoginListener loginListener) {
	 * this.loginListener = loginListener; }
	 *
	 * public void setIsLoginListener(IsLoginListener isLoginListener) {
	 * this.isLoginListener = isLoginListener; }
	 */

	/*
	 * public void setRedirectCouponDetail(RedirectCouponDetail
	 * redirectCouponDetail) { this.redirectCouponDetail = redirectCouponDetail;
	 * }
	 */

//	public void setButtonListener(ButtonListener listener) {
//		this.bListener = listener;
//	}
//
	public void setImagesListener(JavascriptInterface.ImagesListener listener) {
		((WebViewInterface)this.mWebViewInterface).setImgListenter(listener);
	}

	public void setOnJSClickListener(JavascriptInterface.OnJSClickListener listener) {
		((WebViewInterface)this.mWebViewInterface).setJSClickListener(listener);
	}

	public void setGotoSrpListener(JavascriptInterface.GotoSrpListener listener) {
		((WebViewInterface)this.mWebViewInterface).setGoSrpListenter(listener);
	}

	public void setGotoShareListener(JavascriptInterface.GotoShareListener listener) {
		((WebViewInterface)this.mWebViewInterface).setGotoShareListener(listener);
	}

	public void setGotoInterestListener(JavascriptInterface.GotoInterestListener listener) {
		((WebViewInterface)this.mWebViewInterface).setGotoInterestListener(listener);
	}

	public void getCookeiListener(JavascriptInterface.GetLocalCookieListener listener) {
		((WebViewInterface)this.mWebViewInterface).setCookieListener2(listener);
	}

	public void setCookeiListener(JavascriptInterface.SetLocalCookieListener listener) {
		((WebViewInterface)this.mWebViewInterface).setCookieListener(listener);
	}

	public void setReadNovelDictionaryListener(JavascriptInterface.ReadNovelDictionaryListener listener) {
		((WebViewInterface)this.mWebViewInterface).setReadNovelDictionaryListener(listener);
	}

	public void setReadNovelContentListener(JavascriptInterface.ReadNovelContentListener listener) {
		((WebViewInterface)this.mWebViewInterface).setReadNovelContentListener(listener);
	}

	public WebViewInterface getmWebViewInterface() {
		return ((WebViewInterface)this.mWebViewInterface);
	}


	//
//	public void setGotoInterestListener(
//			GotoInterestListener gotoInterestListener) {
//		this.gotoInterestListener = gotoInterestListener;
//	}
//
//	public void setReadNovelDictionaryListener(
//			ReadNovelDictionaryListener readNovelDictionaryListener) {
//		this.readNovelDictionaryListener = readNovelDictionaryListener;
//	}
//
//	public void setReadNovelContentListener(
//			ReadNovelContentListener readNovelContentListener) {
//		this.readNovelContentListener = readNovelContentListener;
//	}
//
//	public void setDownLoadRadioListener(
//			DownloadRadioListener downloadRadioListener) {
//		this.downloadRadioListener = downloadRadioListener;
//	}
//
//	public void setDownLoadNoverListener(
//			DownloadNovelListener downloadNovelListener) {
//		this.downloadNovelListener = downloadNovelListener;
//	}
//
//	public void setCookeiListener(SetLocalCookieListener listener) {
//		this.cookieListener = listener;
//	}
//
//	public void getCookeiListener(GetLocalCookieListener listener) {
//		this.cookieListener2 = listener;
//	}
//
//	@Override
//	public void setButtonDisable() {
//		if (bListener != null) {
//			bListener.setButtonDisable();
//		}
//	}
//
//	@Override
//	public void setImages(String images) {
//		if (imgListenter != null)
//			imgListenter.setImages(images);
//	}
//
//	@Override
//	public void gotoSRP(String keyword, String srpId) {
//		if (goSrpListenter != null)
//			goSrpListenter.gotoSRP(keyword, srpId);
//	}
//
//	@Override
//	public void openAd2(String json) {
//		if (openAdListener != null)
//			openAdListener.openAd2(json);
//	}
//
//	@Override
//	public String getSouyueInfo() {
//		User user = SYUserManager.getInstance().getUser();
//		String token = (user != null
//				&& !user.userType().equals(SYUserManager.USER_ADMIN) ? user
//				.token() : "");
//		return "{\"imei\":" + "\"" + DeviceInfo.getDeviceId() + "\"" + ","
//				+ "\"token\":" + "\"" + token + "\"}";
//	}
//
//	@Override
//	public String getNetworkType() {
//		return DeviceInfo.getNetworkType().toLowerCase();
//	}
//
//	@Override
//	public void gotoShare() {
//		if (gotoShareListener != null)
//			gotoShareListener.gotoShare();
//	}
//
//	@Override
//	public void onJSClick(String json) {
//		try {
//			final JSClick jsc = new Gson().fromJson(json, JSClick.class);
//			if (jsc == null)
//				return;
//			jsc.init();
//			if (jsc.isEncrypt()) {
//				final String en = Utils.encryptJs(jsc.getContent());
//				post(new Runnable() {
//					@Override
//					public void run() {
//						loadUrl("javascript:" + jsc.getCallback() + "('" + en
//								+ "')");
//					}
//				});
//				return;
//			}
//			if (jsc.isGetUser()) {
//				final String en = Utils.encryptJs(ImJump2SouyueUtil
//						.getUserInfo(context));
//				post(new Runnable() {
//					@Override
//					public void run() {
//						loadUrl("javascript:" + jsc.getCallback() + "('" + en
//								+ "')");
//					}
//				});
//				return;
//			}
//			if (jsc.isRSAEncrypt()) {
//				User user = SYUserManager.getInstance().getUser();
//				// String param = "{'openid':'" + user.getOpenid() +
//				// "','opid':'" + user.getOpid()
//				// + "','appid':'" + user.getAppId() +
//				// " ','encryptiontype':'js','data':'" + jsc.getData() + "'}";
//
//				JSONObject jsonObject = new JSONObject();
//				jsonObject.put("openid", user.getOpenid());
//				jsonObject.put("opid", user.getOpid());
//				jsonObject.put("appid", user.getAppId());
//				jsonObject.put("encryptiontype", "js");
//				jsonObject.put("data", jsc.getData());
//
//				try {
//					result = RSAUtils.privateEncrypt(jsonObject.toString(),
//							user.getPrivate_key());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				post(new Runnable() {
//					@Override
//					public void run() {
//						loadUrl("javascript:" + jsc.getCallback() + "('"
//								+ result + "')");
//					}
//				});
//				return;
//			}
//			if (jsc.isGetRSAParam()) {
//				User user = SYUserManager.getInstance().getUser();
//				JSONObject jsonObject = new JSONObject();
//				jsonObject.put("openid", user.getOpenid());
//				jsonObject.put("opid", user.getOpid());
//				jsonObject.put("appid", user.getAppId());
//				jsonObject.put("auth_token", user.getAuth_token());
//
//				// final String RSAparam = "{'openid':'" + user.getOpenid() +
//				// "','opid':'" + user.getOpid()
//				// + "','appid':'" + user.getAppId() + " ','auth_token':'" +
//				// user.getAuth_token() + "'}";
//
//				final String RSAparam = jsonObject.toString();
//
//				post(new Runnable() {
//					@Override
//					public void run() {
//						loadUrl("javascript:" + jsc.getCallback() + "('"
//								+ RSAparam + "')");
//					}
//				});
//				return;
//			}
//			if (JSClickListener != null) {
//				JSClickListener.onJSClick(jsc);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	@Override
//	public void gotoInterest(long interest_id) {
//		if (gotoInterestListener != null)
//			gotoInterestListener.gotoInterest(interest_id);
//	}
//
//	@Override
//	public String getFictionIndex(String novelId) {
//		String indexStr = "";
//		if (readNovelDictionaryListener != null) {
//			indexStr = readNovelDictionaryListener.getFictionIndex(novelId);
//		}
//		return indexStr;
//	}
//
//	@Override
//	public String getFictionContent(String novelId, int begin, int offet) {
//		String content = "";
//		if (readNovelContentListener != null) {
//			content = readNovelContentListener.getFictionContent(novelId,
//					begin, offet);
//		}
//		return content;
//	}
//
//	@Override
//	public void downloadVideo(String id, String name, String img,
//			String length, String urls) {
//		if (downloadRadioListener != null) {
//			downloadRadioListener.downloadVideo(id, name, img, length, urls);
//		}
//	}
//
//	@Override
//	public void downloadFiction(String id, String name, String img,
//			String length, String url, String version) {
//		if (downloadNovelListener != null) {
//			downloadNovelListener.downloadFiction(id, name, img, length, url,
//					version);
//		}
//	}
//
//	@Override
//	public void setLocalCookie(String key, String value) {
//		if (cookieListener != null) {
//			cookieListener.setLocalCookie(key, value);
//		}
//	}
//
//	@Override
//	public void getLocalCookie(String key) {
//		if (cookieListener2 != null) {
//			cookieListener2.getLocalCookie(key);
//		}
//	}
//
//	@Override
//	public String getSouyueVersion() {
//		return DeviceInfo.getAppVersion();
//	}

	/*
	 * @Override public void redirectCouponDetail(long coupon_zsb) {
	 * if(redirectCouponDetail != null)
	 * redirectCouponDetail.redirectCouponDetail(coupon_zsb); }
	 */

	/*
	 * @Override public void login() { if (loginListener != null)
	 * loginListener.login(); }
	 * 
	 * @Override public boolean isLogin() { if (isLoginListener != null) return
	 * isLoginListener.isLogin(); return false; }
	 */

}
