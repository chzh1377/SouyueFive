package com.zhongsou.souyue.circle.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.SelfCreateActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.countUtils.AppInfoUtils;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GetLocalCookieListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoShareListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.GotoSrpListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ReadNovelContentListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.ReadNovelDictionaryListener;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.SetLocalCookieListener;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class RewardsWebViewActivity extends BaseActivity implements
        GotoSrpListener,
        GotoShareListener,//
        OnJSClickListener, SetLocalCookieListener,
        GetLocalCookieListener, ReadNovelDictionaryListener,
        ReadNovelContentListener {
	
	private CustomWebView mWebView;
	private long blog_id;
	private long mblog_user_id;
	private long interest_id ;
	private String uid;
	private String token;
	private String versioncode; 
	private String webUrl;

    private SearchResultItem sri;
	
	// http://mtest.zhongsou.com/circlerewards/index?uid_a=发帖人uid(搜悦库的用户id)&tid=主帖id&souyue_version=搜悦客户端版本号&uid_b=当前登陆用户uid&token=当前登陆用户的token
	//	发帖人uid(搜悦库的用户id)	mblog_user_id
	//	主帖id 					// posts_id 就是blog_id
	//	搜悦客户端版本号
	//	当前登陆用户uid
	//	当前登陆用户的token
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_activity_webview_rewards);
		Intent intent = getIntent();
		blog_id = intent.getLongExtra("blog_id",0);
		interest_id = intent.getLongExtra("interest_id",0);
		mblog_user_id = intent.getLongExtra("mblog_userId", 0);
		uid = SYUserManager.getInstance().getUserId();
		token =SYUserManager.getInstance().getToken();
		versioncode = AppInfoUtils.getAppVersion(RewardsWebViewActivity.this);
		webUrl = UrlConfig.HOST_ZHONGSOU_REWARDS
				+"index?uid_a="+mblog_user_id
				+"&circle_id=" + interest_id
				+"&tid="+blog_id
				+"&souyue_version="+versioncode
				+"&uid_b="+uid
				+"&token="+token;
		Log.i("webUrl", "webUrl:" + webUrl);
		
		mWebView = (CustomWebView) this.findViewById(R.id.rewards_webview);
        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT <= 9) {
//            settings.setPluginsEnabled(true);
        } else if (Build.VERSION.SDK_INT <= 10) {
            settings.setPluginState(WebSettings.PluginState.ON);
        } else {
            settings.setPluginState(WebSettings.PluginState.ON);
        }
//        mWebView.addJavascriptInterface(new JsInterface(), "ent");
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

        MakeCookie.synCookies(this, webUrl);
		mWebView.loadUrl(webUrl);// 加载需要显示的网页
		
		mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
	}

	/*
	 * 如果不做任何处理点击系统Back， Browser会调用finish()而结束自身，
	 * 如果希望浏览的网页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish(); // 返回WebView的上一页面
			return true;
		}
		return false;
	}

    /*
     * @Override public boolean isLogin() { return IntentUtil.isLogin(); }
     *
     * @Override public void login() { if(!IntentUtil.isLogin()) {
     * IntentUtil.goLoginForResult(this, 100); } }
     */

    /*
     * @Override public void redirectCouponDetail(long coupon_zsb) {
     * UIHelper.showCouponDetailByZsb(WebSrcViewActivity.this, coupon_zsb); }
     */

//    class JsInterface implements DontObfuscateInterface {
//        /**
//         * 中搜币商城现金券JS回调，跳转到现金券详情
//         *
//         * @param coupon_zsb 现金券面额兑换的中搜币个数
//         */
//        @JavascriptInterface
//        public void redirectCouponDetail(long coupon_zsb) {
//            UIHelper.showCouponDetailByZsb(RewardsWebViewActivity.this, coupon_zsb);
//        }
//
//        /**
//         * 跳转到商家首页接口 mall_id: 商家ID mall_name: 商家名称 mall_type: 商家类型 0-普通商家 1-合作商家
//         * city: 如果是普通商家，需要传入商家所在城市
//         */
//        @JavascriptInterface
//        public void redirectShopHome(String mall_idStr, String mall_name,
//                                     int mall_type, String city) {
//            long mall_id = Long.parseLong(mall_idStr);
//            if (mall_type == SearchShop.TYPE_INSIDE) {
//                UIHelper.showEntHomeFromSouyue(RewardsWebViewActivity.this, mall_name, mall_id, false);
//            } else {
//                SearchShop shop = new SearchShop();
//                shop.setSid(mall_id);
//                shop.setName(mall_name);
//                UIHelper.showCommonShopFromSouyue(RewardsWebViewActivity.this, city, shop);
//            }
//        }
//
//        /**
//         * 跳转到普通商家的分店列表接口 mall_id: 商家ID lng: 经度(当前位置) lat: 纬度（当前位置） city:
//         * 商家的所在城市
//         */
//        @JavascriptInterface
//        public void redirectShopList(String mall_idStr, Double lng, Double lat, String city) {
//            long mall_id = Long.parseLong(mall_idStr);
//            SearchParam searchParam = new SearchParam();
//            searchParam.setCity(city);
//            searchParam.setSid((int) mall_id);
//            searchParam.setLat(lat);
//            searchParam.setLng(lng);
//            UIHelper.showEntSearchSubbranch(RewardsWebViewActivity.this, searchParam);
//        }
//
//        /**
//         * 跳转到地图导航接口 mall_name: 商家名称 lng: 经度(商家位置) lat: 纬度(商家位置) address: 商家地址
//         */
//        @JavascriptInterface
//        public void redirectShopMap(String mall_name, String lng, String lat, String address) {
//            UIHelper.goToMapLocation(RewardsWebViewActivity.this, "", mall_name, Double.parseDouble(lng), Double.parseDouble(lat), address);
//        }
//
//
//    }

    class SouYueLoginInterface implements DontObfuscateInterface {

        public void login() {
            if (!IntentUtil.isLogin()) {
                IntentUtil.goLoginForResult(RewardsWebViewActivity.this, 100);
            }
        }

        public boolean isLogin() {
            return IntentUtil.isLogin();
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
        if (SYUserManager.USER_ADMIN.equals(SYUserManager.getInstance().getUserType())) {
            i.setClass(this, SelfCreateActivity.class);
        } else {
            i.setClass(this, LoginActivity.class);
        }
        startActivity(i);
    }


    @Override
    public void onJSClick(JSClick jsc) {
        try {
            toSRIObj(jsc);
            //未抽取
            if (jsc.isCheckappinstalled()) {
                checkAppInstalled(jsc.appname());
                return;
            }
            ImJump2SouyueUtil.IMAndWebJump(RewardsWebViewActivity.this, jsc, sri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    @Override
//    public void downloadFiction(String id, String name, String img, String length, String url, String version) {
//        ImJump2SouyueUtil.downloadFiction(RewardsWebViewActivity.this, id, name, img, length, url, version);
//    }

//    @Override
//    public void downloadVideo(String id, String name, String img, String length, String urls) {
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
            return WebSrcViewActivity.INSTALL;
        try {
            getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return WebSrcViewActivity.INSTALL;
        } catch (PackageManager.NameNotFoundException e)

        {
            return WebSrcViewActivity.UNINSTALL;
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
}
