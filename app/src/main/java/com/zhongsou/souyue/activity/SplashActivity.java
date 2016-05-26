package com.zhongsou.souyue.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.im.render.MsgSerMsgFirstRender;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.PushInfo;
import com.zhongsou.souyue.module.SplashAd;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.UserAction;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.common.GuestToken;
import com.zhongsou.souyue.net.personal.UserCallBack;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.LayoutApi;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.utils.ActivityUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.VersionUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

//import com.zhongsou.juli.factory.Advert;
//import com.zhongsou.juli.factory.AdvertFactory;

/**
 * 闪屏
 *
 * @author zhangliang01@zhongsou.com
 */
public class SplashActivity extends BaseActivity implements IVolleyResponse, OnClickListener {
    private static       String LOG_TAG                = "SplashActivity";
    private static final String JUMP_TYPE_SLOT         = "slot";
    private static final String JUMP_TYPE_ZERO         = "zero";
    private static final String JUMP_TYPE_INTEREST     = "interest";  //兴趣圈帖子
    public static final  String JUMP_TYPE_LINGPAI      = "lingpai";
    public static final  String JUMP_TYPE_INTERESTCARD = "circleCard";//兴趣圈名片
    public static final  String JUMP_TYPE_GALLERYNEWS  = "galleryNews";//图集

    public static final int HTTP_GET_GET_SPLASH_IMAGE        = 501;
    public static final int HTTP_GET_GET_GUIDE_RECOMMEND_SRP = 502;
    public static final int HTTP_UPLOAD_APPDATA              = 506;
    public static final String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    private boolean isFirstIn;
    //    private CGuideHttp mCguideHttp;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        JPushInterface.onResume(this);  //JPush
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        JPushInterface.onPause(this);  //JPush
    }

    private final  int     SPLASH_DISPLAY_LENGHT = 500;
    private        int     mSplashDisplayAD      = 1500;
    private static boolean isInit                = false;
    private boolean createShortCut;
    private int b = 0;
    private String md5;
    private String keyword;
    private String pushId;
    private String g;
    private String pushFrom;
    private String clickFrom;
    private String mid;
    //    private String ifextract;
    private PushInfo pushInfo = new PushInfo();
    private ImageView mSplashAdImg;
    //    private boolean isNeedOpenSlot;   //是否跳转到老虎机
    private String    jumpType;
    // 微信返回
    private String    srpId;
    private String    url;

    //=======super app start=========
    public static final String  SATRT_SUF       = ".png";
    //是否只有第一次显示引导图，true 只第一次显示，false 每次都显示
    private             boolean isFirstShowGuid = true;
    private String firstGuide;
    private String version_name;
    //是否第一次打开app
    //5.0
    private ImageLoader imageLoader;
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(false).build();

    private        String                   lastSplashUrl;
    private        int                      mIsJump;
    private        Bitmap                   bm;
    private static LruCache<String, Bitmap> mMemoryCache;
    private static final String DEFAULT_SPLASH_IMAGE_KEY = "defaultSplashImage";

    private Button   mJumpBtn;//跳过按钮
    private int      mJumpType;//跳转类型
    private Runnable mAdDisplayThread;
    private String   mJumpUrl;//跳转url
    private int      displayTime;//后台设置广告展示时间
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ZSSdkUtil.initZSSDK(this);  //根据环境初始化ZSSDK
        mHandler = new Handler(Looper.getMainLooper());
//        NBSAppAgent.setLicenseKey("aafac50748844123a8737b0242a781f7").withLocationServiceEnabled(true).start(this);
//        http = new Http(this);
        imageLoader = ImageLoader.getInstance();
        if (sysp == null) sysp = SYSharedPreferences.getInstance();
        //初始化新的启动页
        //------------

        //--------------
        b = sysp.getInt(SYSharedPreferences.KEY_SHOW_GUIDE_DY, 0);//以后就不用这个int的值了
        firstGuide = sysp.getString(SYSharedPreferences.KEY_SHOW_GUIDE_DY_NEW, "");
        version_name = sysp.getString(SYSharedPreferences.KEY_VERSION, "");

        //======= super app end =========
        createShortCut = sysp.getBoolean(SYSharedPreferences.KEY_CREATESHORTCUT, true);

        isFirstIn = checkFirstIn();
        // 获取游客的token
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getToken();
            }
        }, 0);
        LogDebugUtil.v(LOG_TAG, "isFromWX()=" + isFromWX());
        if (isFromWX())
            initFromWX();
        else
            initPush();
        setPushInfo();
        MobclickAgent.onError(this);
        initSplash();

        if (createShortCut && !(null != android.os.Build.BOARD && (android.os.Build.BOARD.contains("mi") || android.os.Build.BOARD.contains("MI")))) {
            createShortCut();
            sysp.putBoolean(SYSharedPreferences.KEY_CREATESHORTCUT, false);
        }
        //调用户回访接口
        if (UserAction.isLogin()) {
            UserCallBack call = new UserCallBack(HttpCommon.USER_CALLBACK_REQUEST, this);
            call.setParams();
            mMainHttp.doRequest(call);
//            http.souyueBack();
        }


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAD();
            }
        }, SPLASH_DISPLAY_LENGHT);
//        AdvertFactory factory = new AdvertFactory();
//        Advert advert  = factory.getSplashAd(SplashActivity.this);
//        advert.showAdvert();
//        AdvertFactory.getInstance().getSplashAd(this).show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gogogo();
//            }
//        }, 48 * 100);
    }

    /**
     * 跳转到引导页
     */
    private void goToLeaderPage() {
        Intent intent = new Intent(this, FirstInActivity.class);
        startActivity(intent);
    }

    /**
     * 判断是否第一次进入搜悦
     *
     * @return
     */

    private boolean checkFirstIn() {
        //如果是空的话，表示还未安装。
        if (StringUtils.isEmpty(firstGuide) || StringUtils.isEmpty(version_name)) {
            return true;
        } else {
            //如果保存的版本号，等于当前的版本号，表示已经进入了
            if (version_name.equals(DeviceInfo.getAppVersion())) {
                return false;
            } else if (VersionUtils.isVersionBig(DeviceInfo.getAppVersion(), version_name)) {
                //如果大于当前版本号，也不弹
                return false;
            } else {
                if (firstGuide.equals(SYSharedPreferences.FIRSTGUIDE)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private void checkAD() {
        if ((!StringUtils.isEmpty(lastSplashUrl))) {
            mAdDisplayThread = new SplashAdDisplay();
            mHandler.postDelayed(mAdDisplayThread, mSplashDisplayAD);
        } else {
            gogogo();
        }
    }

    /**
     * 获得用户touken成功
     */
    public void tokenSuccess(HttpJsonResponse response) {
        User u = new Gson().fromJson(response.getBody(), User.class);
        u.userType_$eq(SYUserManager.USER_GUEST);
        SYUserManager.getInstance().setUser(u);
        if (StringUtils.isNotEmpty(response.getHead().get("cpmRecommend"))) {
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_GUEST_SPECIAL, u.userId() + "," + response.getHead().get("cpmRecommend").getAsString());
        }
    }

    public void gogogo() {
        initApplication();
        //如果不进入引导页
        if (!isFirstIn) {
            if (isFromWX() || (!TextUtils.isEmpty(md5) && !md5.equals("null"))) {// 有消息推送到达客户端，则开启服务启动应用，完成压栈效果
//              走这的话认为已经进去过搜悦
                startActivityToService();
                SplashActivity.this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                startActivity(MainActivity.class);
            }
        }else{
            goToLeaderPage();
        }
        finish();
    }

    private void setPushInfo() {
        if (pushInfo != null) {
            pushInfo.url_$eq(url);
            pushInfo.g_$eq(g);
            pushInfo.keyword_$eq(keyword);
            pushInfo.pushId_$eq(pushId);
            pushInfo.srpId_$eq(srpId);
            pushInfo.setJumpType(jumpType);
            pushInfo.setStatisticsJumpPosition(MsgSerMsgFirstRender.STATISTICS_JUMP_POSITION_NOTIFICATION_BAR);
            pushInfo.setPushFrom(pushFrom);
            pushInfo.setClickFrom(clickFrom);
            pushInfo.setMid(mid);
//			pushInfo.setIfextract(ifextract);
        }
//        pushInfo.setSlot(isNeedOpenSlot?"slot":"");
    }

    public void startActivityToService() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, CommonStringsApi.getHomeClass());
        if (pushInfo != null) {
            LogDebugUtil.v(LOG_TAG, "startActivityToService push_info=" + pushInfo);
            intent.putExtra("push_info", pushInfo);
        }
        startActivity(intent);
    }

    private static void initApplication() {
        if (!isInit) {
            ImageUtil.delTempShareImages();
            isInit = true;
        }
    }

    /**
     * 初始消息推送
     */
    private void initPush() {
        sysp.putBoolean(SYSharedPreferences.KEY_ISRUNNING, true);
        /**
         * 消息推送推来的参数(md5,keyword,pushId)
         */

        md5 = this.getIntent().getStringExtra("md5");
        keyword = this.getIntent().getStringExtra("keyword");
        pushId = this.getIntent().getStringExtra("pushId");
        g = this.getIntent().getStringExtra("g");
        pushFrom = this.getIntent().getStringExtra("pushFrom");
        clickFrom = this.getIntent().getStringExtra("clickFrom");
        mid = this.getIntent().getStringExtra("mid");
    }

    private void initSplash() {
        setContentView(LayoutApi.getLayoutResourceId(R.layout.new_splash));

        //5.0动态图片
        mSplashAdImg = findView(R.id.splash_ad_image);
        mJumpBtn = findView(R.id.btn_splash_skipe);
        if(isFirstIn){
            mSplashAdImg.setImageResource(R.drawable.splash_default);
            mJumpBtn.setVisibility(View.GONE);
        }else{
            mSplashAdImg.setOnClickListener(this);
            mJumpBtn.setOnClickListener(this);
            initSplashAdConfigure();
            if (ConfigApi.isSouyue()) {
                setUpCacheSplashImg();
            }
        }
    }

    private void createShortCut() {
        ActivityUtils.addShortCut(this);
    }

    /**
     * 分解微信返回数据
     */
    private void initFromWX() {
        Intent intent = this.getIntent();
        if (null == intent) return;
        String str = intent.getDataString() == null ? "" : intent.getDataString();
        try {
            if (ConfigApi.isSouyue()) {
                str = URLDecoder.decode(str, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("", "splash : " + str);
        if ((ShareApi.WEIXIN_APP_ID.concat("://slot")).equals(str)) {
            jumpType = JUMP_TYPE_SLOT;
            Log.i("slot", "initFromWX");
            return;
        } else if ((ShareApi.WEIXIN_APP_ID.concat("://zero")).equals(str)) {
            jumpType = JUMP_TYPE_ZERO;
            return;
        } else if (str.startsWith(ShareApi.WEIXIN_APP_ID + "://" + JUMP_TYPE_INTERESTCARD)) {
            jumpType = JUMP_TYPE_INTERESTCARD;
            CircleResponseResultItem item = new CircleResponseResultItem();
            if (!ConfigApi.isSouyue()) {
                try {
                    str = URLDecoder.decode(str, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            Uri uri = Uri.parse(str);
            if (uri != null) {
                String interest_id = uri.getQueryParameter("circleId");
                try {
                    item.setInterest_id(Long.valueOf(interest_id));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                pushInfo.setInterestBlog(item);
            }
            return;
        } else if (str.startsWith(ShareApi.WEIXIN_APP_ID.concat("://lingpai"))) {
            jumpType = JUMP_TYPE_LINGPAI;
            url = str.substring(str.indexOf("http"), str.length());
            return;
        } else if (str.startsWith((ShareApi.WEIXIN_APP_ID.concat("://interest")))) {
            jumpType = JUMP_TYPE_INTEREST;
            if (!ConfigApi.isSouyue()) {
                try {
                    str = URLDecoder.decode(str, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            wrapInterest(str);
            return;
        } else if (str.startsWith((ShareApi.WEIXIN_APP_ID.concat("://galleryNews")))) {//微信分享，打开跳转到图集
            jumpType = JUMP_TYPE_GALLERYNEWS;
            String wxStr = ShareApi.WEIXIN_APP_ID.concat("://galleryNews");
            str = str.substring(0, wxStr.length()) + "?" + str.substring(wxStr.length() + 1);
            wrapGalleryNews(str);
            return;
        }

        String[] st = str.split("//");// 分割wx360a9785675a8653://
        Log.i(LOG_TAG, str);
        if (st.length >= 2) {

            if (st.length == 3
                    && isMatchUrl(st[2], "opentype=(\\w{8})", "emptyWeb")
                    && isMatchUrl(st[2], "source=(\\w{6})", "search")) {//解析从搜索分享到微信的跳转类型
                String[] resultStr = st[2].split("\\?");
                String[] param = resultStr[1].split("&");
                for (int i = 0; i < param.length; i++) {
                    String[] s = param[i].split("=");
                    if (s.length >= 2) {
                        if ("opentype".equals(s[0]) && "emptyWeb".equals(s[1])) {
                            keyword = s[1];//用keyword暂存打开类型的值
                        }
                        if ("source".equals(s[0]) && "search".equals(s[1])) {
                            srpId = s[1];//用srpId暂存分享来源的值
                        }
                    }
                }
                if (str.contains("url=")) {
                    if (str.indexOf("url=") + 4 < str.length()) {
                        url = str.substring(str.indexOf("url=") + 4, str.length());
                    }
                    Log.i("", "url : " + url);
                }
                return;
            }
            if (!ConfigApi.isSouyue()) {
                try {
                    st[1] = URLDecoder.decode(st[1], "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String[] dataStr = st[1].split("&");// 分割keyword=ddd&srpId=sfsfsf&url=
            if (dataStr.length >= 3) {
                String[] s = dataStr[0].split("=");
                if (s.length >= 2) keyword = s[1];
                s = dataStr[1].split("=");
                if (s.length >= 2) srpId = s[1];
                if (!ConfigApi.isSouyue()) {
                    try {
                        str = URLDecoder.decode(str, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (str.contains("&url=")) {
                    if (str.indexOf("&url=") + 5 < str.length()) {
                        url = str.substring(str.indexOf("&url=") + 5, str.length());
                        if (!ConfigApi.isSouyue()) {
                            try {
                                url = URLDecoder.decode(url, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        // add by trade
                        if (url.contains("opentype=src")) {
                            g = "0";
                            if (url.contains("md5")) {
                                md5 = (url.split("md5=")[1]).split("&")[0];
                            }
                        }
                    }
                    Log.i("", "url : " + url);
                }
            } else if (dataStr.length >= 2) {
                String[] s = dataStr[0].split("=");
                if (s.length >= 2) keyword = s[1];
                s = dataStr[1].split("=");
                if (s.length >= 2) srpId = s[1];

            } else {
                String[] s = dataStr[0].split("=");
                if (s.length >= 2) keyword = s[1];
            }
        } else {
            pushInfo = null;
        }
    }

    private void wrapInterest(String str) {
        CircleResponseResultItem item = new CircleResponseResultItem();
        Uri                      uri  = Uri.parse(str);
        if (uri != null) {
            String interest_id = uri.getQueryParameter("interestId");
            String blog_id = uri.getQueryParameter("blogId");
            String user_id = uri.getQueryParameter("userId");
            String isPrime = uri.getQueryParameter("isPrime");
            String top_status = uri.getQueryParameter("isTop");
            try {
                item.setBlog_id(Long.valueOf(blog_id));
                item.setType(Integer.valueOf(uri.getQueryParameter("type")));
                item.setInterest_id(Long.valueOf(interest_id));
                item.setIs_prime(Integer.valueOf(isPrime));
                item.setTop_status(Integer.valueOf(top_status));
                item.setUser_id(Long.valueOf(user_id));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            pushInfo.setInterestBlog(item);
        }
    }


    private void wrapGalleryNews(String str) {
        GalleryNewsHomeBean item = new GalleryNewsHomeBean();
        Uri                 uri  = Uri.parse(str);
        if (uri != null) {
            String srpId = uri.getQueryParameter("srpid");
            String title = uri.getQueryParameter("title");
            String url = uri.getQueryParameter("url");
            String images = uri.getQueryParameter("img");
            String source = uri.getQueryParameter("source");
            String keyword = uri.getQueryParameter("keyword");
            String pubTime = uri.getQueryParameter("newstime");
            try {
                item.setSrpId(srpId);
                item.setTitle(title);
                item.setUrl(url);
                if (!StringUtils.isEmpty(images)) {
                    List<String> imgList = new ArrayList<String>();
                    String[] imgs = images.split(",");
                    for (String img : imgs) {
                        imgList.add(img);
                    }
                    item.setImage(imgList);
                }
                item.setSource(source);
                item.setKeyword(keyword);
                item.setPubTime(pubTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            pushInfo.setGalleryNews(item);
        }
    }

    /**
     * 判断是否来自微信
     *
     * @return
     */
    private boolean isFromWX() {
        boolean is     = false;
        Intent  intent = this.getIntent();
        if (null == intent) is = false;
        String str = intent.getDataString() == null ? "" : intent.getDataString();
        LogDebugUtil.v(LOG_TAG, "isFromWX.datastring=" + str);
        if (StringUtils.isNotEmpty(ShareApi.WEIXIN_APP_ID) && str.contains(ShareApi.WEIXIN_APP_ID)) {
            is = true;
        }
        return is;
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }

/*    public void splashAdSuccess(SplashAd sad) {
        adaddress = sad.getAdImage();
        if (splashIv != null && !StringUtils.isEmpty(adaddress)) {
            if (isHighSpeed()) {//快网速
//                new AQuery(this).id(splashIv).image(sad.getAdImage(), false, false, 0, 0, null, AQuery.FADE_IN);
                new AQuery(this).id(splashIv).image(sad.getAdImage(), true, true, 0, 0, new BitmapAjaxCallback() {
                    @Override
                    protected void callback(String url, ImageView iv, Bitmap bitmap, AjaxStatus status) {
                        if (bitmap != null && status.getCode() == 200) {
                            splashIv.setImageBitmap(bitmap);
                            animationShow(splashIv);
                        }
                    }
                });
            } else {//慢网速
                File f;
                try {
                    f = new AQuery(this).getCachedFile(sad.getAdImage());
                    if (f != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                        if (bitmap != null) {
                            splashIv.setImageBitmap(bitmap);
                            animationShow(splashIv);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            animationHide(logo);
        }
    }*/

    /**
     * 判断网速
     *
     * @return
     */
    protected boolean isHighSpeed() {
        return checkConnectionStatus();
    }

    protected Set<Integer> highSpeed = new HashSet<Integer>();

    {
        highSpeed.add(TelephonyManager.NETWORK_TYPE_UMTS);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_1xRTT);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_EDGE);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_EVDO_0);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_EVDO_A);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_HSDPA);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_HSPA);
        highSpeed.add(TelephonyManager.NETWORK_TYPE_HSUPA);
    }

    protected boolean parseNetStatus(NetworkInfo ni) {
        if (ni == null)
            throw new RuntimeException("ni can not be null");
        if (highSpeed.contains(ni.getSubtype()) || ni.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        return false;
    }

    protected boolean checkConnectionStatus() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return parseNetStatus(networkInfo);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    private void animationHide(ImageView iv) {
        try {
            AlphaAnimation ai = new AlphaAnimation(1.0f, 0.0f);
            ai.setDuration(500);
            ai.setFillAfter(true);
            iv.startAnimation(ai);
        } catch (Exception ex) {

        }
    }

    private void startActivity(Class activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        startActivity(intent);
        SplashActivity.this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void animationShow(ImageView iv) {
        try {
            AlphaAnimation ai = new AlphaAnimation(0.0f, 1.0f);
            ai.setDuration(500);
            ai.setFillAfter(true);
            iv.startAnimation(ai);
        } catch (Exception ex) {

        }
    }

    /**
     * 获取游客token
     */
    public void getToken() {
        User user = SYUserManager.getInstance().getUser();

//        //统计SDK获得userId
//        if (user != null && user.userId() != 0) {
//            try{
//                Log.d("SplashActivity", "ZSSDK userId :" + String.valueOf(user.userId()));
//                ZSclickAgent.setUser(MainApplication.getInstance().getApplicationContext(), String.valueOf(user.userId()));   //ZSSDK 设置用户
//            }catch (Exception e){
//                e.printStackTrace();
//                Log.e("SYUserManager","ZSSDK-setUser Exception");
//            }
//        }
        if (user != null && user.userId() == 0) {
            SYUserManager.getInstance().delUser(user);
            user = null;
        }
        if (user == null || StringUtils.isEmpty(user.token())) {
            GuestToken guest = new GuestToken(HttpCommon.SELFCREATELIST_REQUEST_ID, SplashActivity.this);
            guest.setParams(SplashActivity.this);
            mMainHttp.doRequest(guest);
        }
    }


    /**
     * 使用缓存或默认图
     */
    private void setUpCacheSplashImg() {

        allocMaxMemory();

        if (mSplashAdImg != null) {
            if (StringUtils.isNotEmpty(lastSplashUrl)) {

                File cacheFile = imageLoader.getDiskCache().get(lastSplashUrl);
                if (cacheFile != null) {
                    setImage(lastSplashUrl, cacheFile.getAbsolutePath());
                } else {
                    setImage(DEFAULT_SPLASH_IMAGE_KEY, "");
                    sysp.remove(SplashAd.LAST_SPLASH_IMAGE_URL);
                    sysp.remove(SplashAd.SPLASH_DISPLAY_TIME);
                    sysp.remove(SplashAd.SPLASH_IS_DISPLAY_JUMP);
                    sysp.remove(SplashAd.SPLASH_JUMP_TYPE);
                    sysp.remove(SplashAd.SPLASH_JUMP_URL);
                    sysp.remove(SplashAd.SPLASH_LASTID);
                }
            } else {
                setImage(DEFAULT_SPLASH_IMAGE_KEY, "");
            }
        }
    }

    /**
     * 为启动图片分配最大内存
     */
    private void allocMaxMemory() {
        if (mMemoryCache == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
            int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @SuppressLint("NewApi")
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024;
                }
            };
        }
    }

    private void setImage(String key, String filePath) {
        Bitmap cacheBitmap = getBitmapFromMemCache(key);
        if (cacheBitmap == null) {
            try {
                if (StringUtils.isEmpty(filePath)) {
                    //TODO 修改默认图
                    cacheBitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.splash_default);
                } else {
                    cacheBitmap = BitmapFactory.decodeFile(filePath);
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            addBitmapToMemoryCache(key, cacheBitmap);
        }
        if (cacheBitmap != null) {
            mSplashAdImg.setImageBitmap(cacheBitmap);
        }
    }

    /**
     * 判断url包含参数
     *
     * @param url
     * @param patttern
     * @param field
     * @return
     */
    public static boolean isMatchUrl(String url, String patttern, String field) {
        Pattern pm = Pattern.compile(patttern);
        Matcher m  = pm.matcher(url);
        if (m.find()) {
            if (!StringUtils.isEmpty(m.group(1)) && !StringUtils.isEmpty(field)) {
                if (field.equals(m.group(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null){
            try {
                mHandler.removeCallbacksAndMessages(null);
            }catch (Exception e){

            }
        }
    }

    /**
     * 添加图片到缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 根据key取出图片
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return key == null ? null : mMemoryCache.get(key);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        int id = request.getmId();
        switch (id) {
            case HttpCommon.SELFCREATELIST_REQUEST_ID:
                Object obj = request.getResponse();
                if (obj != null) {
                    tokenSuccess((HttpJsonResponse) obj);
                }
                break;
        }
    }

    /**
     * 广告展示
     */
    class SplashAdDisplay implements Runnable {
        @Override
        public void run() {
            gogogo();
        }
    }

    /**
     * 初始化广告图配置
     */
    private void initSplashAdConfigure() {
        lastSplashUrl = sysp.getString(SplashAd.LAST_SPLASH_IMAGE_URL, "");
        mIsJump = sysp.getInt(SplashAd.SPLASH_IS_DISPLAY_JUMP, 0);
        mJumpType = sysp.getInt(SplashAd.SPLASH_JUMP_TYPE, 0);
        mJumpUrl = sysp.getString(SplashAd.SPLASH_JUMP_URL, "");
        //启动图展示时间
        displayTime = sysp.getInt(SplashAd.SPLASH_DISPLAY_TIME, 0);
        //设置启动图时间
        if (displayTime != 0) {
            mSplashDisplayAD = displayTime;
        }
        //设置是否显示跳过
        if (mIsJump == 1) {
            mJumpBtn.setVisibility(View.VISIBLE);
        } else {
            mJumpBtn.setVisibility(View.GONE);
        }

        if (isFromWX() || (!TextUtils.isEmpty(md5) && !md5.equals("null")) || mJumpType == 0) {
            mSplashAdImg.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {

        String id = sysp.getString(SplashAd.SPLASH_ID, "");

        if (mHandler != null) {
            if (mAdDisplayThread != null) {
                mHandler.removeCallbacks(mAdDisplayThread);
            }
        }

        switch (v.getId()) {
            case R.id.btn_splash_skipe:
                gogogo();
                UpEventAgent.onZSAdskip(SplashActivity.this, id);
                break;
            case R.id.splash_ad_image:
                //Toast.makeText(this,"url = "+mJumpUrl + " type = " + mJumpType,Toast.LENGTH_SHORT).show();
                //0是不跳，1是h5，2是srp ，3是圈子
                if (StringUtils.isNotEmpty(mJumpUrl)) {
                    switch (mJumpType) {
                        case 0:
                            UpEventAgent.onZSAdclick(SplashActivity.this, id);
                            break;
                        case 1:
                            adStartActivityServiceTwo();
                            UpEventAgent.onZSAdstart(SplashActivity.this, id);
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 启动页点击跳转
     */
    private void adStartActivityServiceTwo() {
        SplashAd splashAd = new SplashAd();
        splashAd.setJumpType(mJumpType);
        splashAd.setJumpUrl(mJumpUrl);
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this,
                CommonStringsApi.getHomeClass());
        intent.putExtra("ad_info", splashAd);
        startActivity(intent);
    }
}
