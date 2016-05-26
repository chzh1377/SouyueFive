package com.zhongsou.souyue;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.andruby.cbug.CrashHandler;
import com.facebook.drawee.view.FrescoConfig;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.speex.encode.AudioLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;
import com.umeng.update.UmengUpdateAgent;
import com.zhongsou.souyue.common.CommManager;
import com.zhongsou.souyue.countUtils.AppInfoUtils;
import com.zhongsou.souyue.im.aidl.ImAidlService;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.emoji.IMEmojiPattern;
import com.zhongsou.souyue.log.DataProcess;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.log.LoggerConfig;
import com.zhongsou.souyue.module.UpdateBean;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.moduleparse.CMoudleParse;
import com.zhongsou.souyue.net.other.SettingCheckVersion;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.service.StartActivityServiceTwo;
import com.zhongsou.souyue.service.UmengParamsTask;
import com.zhongsou.souyue.service.download.DownloadService;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.UpdateNewVersion;
import com.zhongsou.souyue.utils.VersionUtils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainApplication extends Application implements IVolleyResponse{
    public static final String TAG = "souyue";
    private static final long FREQUENCY = 5000;// 1000 * 60 * 30; //
    public static MainApplication INSTANCE;
    public static final String ACTION_DOWNLOAD_BACKGROUND = "DOWNLOAD_NEW_VERSION_BACKGROUND";
    private boolean flag = false;
    public static String inputContents = "";
    private static SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    public static boolean sysRecAd = true;
    public static boolean srpRecAd = true;
    public static float mDesityX;
    public static float mDesityY;
    public ImAidlService imService;
    private boolean isShowingBottomTab = true;
    private boolean isNeedForceRefreshDiscover = false;
    private boolean isFromGameToLogin = false;
    private boolean isWifiSetting = false;
    CMainHttp mMainHttp;
    private int fromType;// 版本更新标识，0：来自首页 1：来自IM聊天 2：来自设置界面手动点击



    // public static boolean isHeadLine;
    // public static boolean isHeadRead;

    public boolean isFlag() {
        return flag;
    }

    public boolean isShowingBottomTab() {
        return isShowingBottomTab;
    }

    public void setShowingBottomTab(boolean isShowingBottomTab) {
        this.isShowingBottomTab = isShowingBottomTab;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isNeedForceRefreshDiscover() {
        return isNeedForceRefreshDiscover;
    }

    public void setNeedForceRefreshDiscover(boolean isNeedForceRefreshDiscover) {
        this.isNeedForceRefreshDiscover = isNeedForceRefreshDiscover;
    }

    public boolean isFromGameToLogin() {
        return isFromGameToLogin;
    }


    public void setFromGameToLogin(boolean isFromGameToLogin) {
        this.isFromGameToLogin = isFromGameToLogin;
    }

    /**
     * 提供系统调用的构造函数，
     */
    public MainApplication() {
        INSTANCE = this;
    }

    /**
     * 获得application实例
     *
     * @return
     */
    public static MainApplication getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainApplication();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Stetho.newInitializerBuilder(this)
//                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                .build();
        FrescoConfig.initFresco(getApplicationContext());
        initLog(getApplicationContext());
        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();
        MobclickAgent.updateOnlineConfig(this);// 更新在线参数的本地缓存
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(true);
        // if (!ConfigApi.isSouyue()) {
        // PushService.startService(MainApplication.getInstance());
        // }
        MobclickAgent
                .setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);
        setAdState(false);
        // initPush();
        UmengParamsTask utk = new UmengParamsTask();
        utk.execute(UmengDefParams.COINS_PRESENT_FOR_REG, //
                UmengDefParams.INVITE_SMS, //
                UmengDefParams.SUPERSEARCHURL, //
                UmengDefParams.LAOHUJI_REPLY_NO, UmengDefParams.ENABLE_UPLOAD_PUSH_LOG, UmengDefParams.FIRST_LOGIN_MSG);
        android.os.Process.myPid();

        /*
         * 初始化崩溃时收集信息的handler内部会有phoneInfo上传操作，这里为了不上传两次，故判断只有搜悦进程(com.zhongsou.souyue)才去上传 但是崩溃后此handler仍会执行两次
         */
        if ("com.zhongsou.souyue".equals(getCurProcessName())) {
            initErrorHandler();
        }
        CommManager.getInstance().init(this);
        AudioLoader.init(getApplicationContext(), null);
        initImageLoader();
        mDesityX = getResources().getDisplayMetrics().xdpi / 160;
        mDesityY = getResources().getDisplayMetrics().ydpi / 160;
        setEmojiInit();
        CMoudleParse.getInstance().initModule(this);
        mMainHttp = new CMainHttp(this);
//        JuliConnect.getInstance().connectJuli(this, "JL_81581460451218", "HxlDpX");
    }

    private String getCurProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 初始化tuita中的log系统  此处有一风险，当user刚刚登录时，userID作为的文件名称不能及时改变
     *
     * @param context
     */
    public void initLog(Context context) {
        String fileName = SYUserManager.getInstance().getUserId()
                + DataProcess.millisToStringDate(System.currentTimeMillis(),
                "_yyyyMMdd0000") + ".log";
        LoggerConfig logConfig = new LoggerConfig(true, false, fileName);
        Logger.initLogger(context, logConfig);

//		if(Logger.getmSaveFile()!=null){   暂时放在这
//			Logger.getmSaveFile().reSetDebugHandler(SYUserManager.getInstance().getUserId()
//					+ DataProcess.millisToStringDate(System.currentTimeMillis(), "_yyyyMMdd0000") + ".log");
//		}

    }

    public void checkVersion(int fromType) {
        this.fromType = fromType;
        SettingCheckVersion check = new SettingCheckVersion(HttpCommon.OTHER_CHECK_OTHER_REQUEST,this);
        check.setParams("souyue", "android",
                AppInfoUtils.getChannel(getInstance()));
        mMainHttp.doRequest(check);
//        http.getUpdateInfo("souyue", "android",
//                AppInfoUtils.getChannel(getInstance()),
//                DeviceInfo.getAppVersion());
    }

    // private void initPush() {
    // if (ConfigApi.isSouyue()) {
    // PushService.setTest(MainApplication.getInstance(),
    // ConstantsUtils.PUSH_TEST);
    // PushService.startService(MainApplication.getInstance());
    // } else {
    // boolean pushSwitchState = sysp.getBoolean(
    // SYSharedPreferences.KEY_PUSHSWITCH,
    // TradeUrlConfig.PUSH_DEFAULT_OPEN);// 获取消息推送服务开关状态
    // if (pushSwitchState) {
    // PushService.setTest(MainApplication.getInstance(),
    // ConstantsUtils.PUSH_TEST);
    // PushService.startService(MainApplication.getInstance());
    // } else {
    // PushService.stopService(MainApplication.getInstance());
    // }
    // }
    // }
    
    public void initImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileCount(500)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(
                        new UsingFreqLimitedMemoryCache((int) (Runtime
                                .getRuntime().maxMemory() / 8)))
                .build();
        Log.v(this.getClass().getName(), "运行时内存大小："
                + Runtime.getRuntime().maxMemory());
        ImageLoader.getInstance().init(config);
        L.writeDebugLogs(false);
    }

    private void initErrorHandler() {
        /*
		 * if (BuildConfig.DEBUG) { StrictMode.setThreadPolicy(new
		 * StrictMode.ThreadPolicy.Builder() .detectDiskReads()
		 * .detectDiskWrites() .detectNetwork() // or .detectAll() for all
		 * detectable problems .penaltyLog() .build());
		 * StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		 * .detectLeakedSqlLiteObjects() .detectLeakedClosableObjects()
		 * .penaltyLog() .penaltyDeath() .build()); return; }
		 */
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext(), new CrashHandler.CrashListener() {

            @Override
            public void onCrashListener() {
//                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
//                PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                        intent, PendingIntent.FLAG_ONE_SHOT);
//                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
                ZhongSouActivityMgr.getInstance().errorExit();
            }
        });
    }

    // 将广告状态设置成未关闭
    public static void setAdState(boolean closed) {
        sysp.putBoolean(SYSharedPreferences.KEY_AD_CLOSED, closed);
    }

    public void killService() {
        Intent intent = new Intent(getApplicationContext(),
                StartActivityServiceTwo.class);
        stopService(intent);
    }

    public void exitActivity() {
        sysp.putBoolean(SYSharedPreferences.KEY_ISRUNNING, false);
    }

    // 获取应用是否运行
    public boolean isRunning() {
        return sysp.getBoolean(SYSharedPreferences.KEY_ISRUNNING, false);
    }

    private void setEmojiInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiPattern.getInstace().getFileText(getApplicationContext());
                IMEmojiPattern.getInstace().getFileText(getApplicationContext());
            }
        }).start();
    }

    public void getUpdateInfoSuccess(HttpJsonResponse res) {
        // System.out.println(" MainApplication getUpdateInfoSuccess ");
        Gson gson = new Gson();
        UpdateBean updateBean = gson.fromJson(res.getBody(), UpdateBean.class);
        // 判断是否更新和是否强制更新，然后用以下语句更新 UpdateNewVersion.checkIfUpDate(context);
        if (!updateBean.getVersion().equals(DeviceInfo.getAppVersion())
                && VersionUtils.isVersionBig(updateBean.getVersion(),
                DeviceInfo.getAppVersion())) {// 先判不等，再比大小，都满足则更新
            // 更新 1,强制更新。2,非强制更新
            boolean isMustUpdate = false;
            if (VersionUtils.isVersionBig(updateBean.getMinVersion(), DeviceInfo.getAppVersion())) {
                isMustUpdate = true;
            }
            for (int i = 0; i < updateBean.getDisable().length; i++) {
                if (updateBean.getDisable()[i].equals(DeviceInfo.getAppVersion())) {
                    isMustUpdate = true;
                }
            }

            UpdateNewVersion updateNewVersion = new UpdateNewVersion(getInstance(), isMustUpdate, updateBean);
            if (!updateNewVersion.isHaveLocalApk()      //最新版本apk不存在
                    && CMainHttp.getInstance().isWifi(MainApplication.getInstance())   //当前网络环境是wifi
                    && ("".equals(SYSharedPreferences.getInstance().getString(SYSharedPreferences.UPDATE, ""))   //每日首次进入此方法时
                    || !new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                    .equals(SYSharedPreferences.getInstance().getString(SYSharedPreferences.UPDATE, "")))) {

                //调用DownloadService进行下载
//                ToastUtil.show(this, "Download apk - Y");
                Intent intent = new Intent();
                intent.setClass(MainApplication.getInstance(), DownloadService.class);
                intent.setAction(ACTION_DOWNLOAD_BACKGROUND);
                intent.putExtra("url", updateBean.getUrl());
                intent.putExtra("title", MainApplication.getInstance().getString(R.string.app_name));
                MainApplication.getInstance().startService(intent);

                SYSharedPreferences.getInstance().putString(SYSharedPreferences.UPDATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            } else {  //apk已经存在
                updateNewVersion.update();
            }
        } else {
            // 不更新
            if (fromType == 0) {// 如果是从首页检测版本更新则不显示toast
            } else if (fromType == 1) { // 类型1 IM进来的
                Toast.makeText(this, "新版本尚在内测中，敬请期待", Toast.LENGTH_LONG).show();
            } else if (fromType == 2) { // 类型2 设置中点击进来的
                Toast.makeText(this, "已经是最新版本，无需更新", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.OTHER_CHECK_OTHER_REQUEST:
                getUpdateInfoSuccess(request.<HttpJsonResponse>getResponse());
        }
    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // 开启分包
    }
}
