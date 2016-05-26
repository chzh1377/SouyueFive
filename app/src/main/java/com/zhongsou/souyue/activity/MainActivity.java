package com.zhongsou.souyue.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.google.gson.Gson;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.PushService;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.MessageRecent;
//import com.zhongsou.juli.factory.JuliConnect;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.component.TabIndicator;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.fragment.BaseTabFragment;
import com.zhongsou.souyue.fragment.DiscoverTabFragment;
import com.zhongsou.souyue.fragment.MineFragment;
import com.zhongsou.souyue.fragment.MsgTabFragment;
import com.zhongsou.souyue.fragment.MsgTabFragment.NotifyMainListener;
import com.zhongsou.souyue.fragment.MyFragmentTabHost;
import com.zhongsou.souyue.fragment.MyFragmentTabHost.OnTabClickListener;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.aidl.ImAidlService;
import com.zhongsou.souyue.im.dialog.ImContactDialog;
import com.zhongsou.souyue.im.dialog.ImProgressDialog;
import com.zhongsou.souyue.im.services.Imservice;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.im.view.ImUIHelpr;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.common.ClientConfig;
import com.zhongsou.souyue.net.common.ClientConfigReq;
import com.zhongsou.souyue.net.common.UpdateClientId;
import com.zhongsou.souyue.net.other.UploadPushRegIDRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.service.StartActivityServiceTwo;
import com.zhongsou.souyue.utils.BadgeUtil;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.PushUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2014/8/20. mailto:wzyax@qq.com
 */
public class MainActivity extends BaseActivity implements OnTabChangeListener,
        OnTabClickListener, NotifyMainListener {
    public static final String TAB_TAG_EXTRA = "TAB_TAG_EXTRA";
    public static final String TAB_INDEX_EXTRA = "TAB_INDEX_EXTRA";
    public static final String PUSH_INFO_EXTRA = "push_info";
    public static final String SHORTCUT_EXTRA = "shortcut_info";
    public static final String SOUYUE_SPEC = "SOUYUE";
    public static final String MSG_SPEC = "MSG";
    public static final String DISCOVER_SPEC = "DISCOVER";
    public static final String ME_SPEC = "ME";
    public static String SEARCH_PATH_MEMORY_DIR; // 搜索索引在内存中的路径

    private MyFragmentTabHost mTabHost;
    private FragmentManager fm;
    private TabIndicator souyueTabView;
    private TabIndicator msgTabView;
    private TabIndicator discoverTabView;
    private TabIndicator meTabView;
    private MainApplication instance;
    private SharedPreferences bubbleSp;
    private LocationManagerProxy mAMapLocManager;
    private BubbleReceiver getbubblereceiver;

    /**
     * 上传手机通讯录相关
     */
    private ImProgressDialog dialog;
    private boolean isIMContactsUpload;
    SYSharedPreferences sysp;
    private SharedPreferences sPreferences;
    private ContentResolver resolver;
    private long mUploadTime;
    private static final long IM_CONTACT_UPLOAD_GAP_TIME = 1000 * 60 * 60 * 12;
    private UploadContactReceiver uploadContactReceiver;
    private List<Contact> contactList = new ArrayList<Contact>();
    private boolean firstUpload = false;    //第一次启动是否上传过通讯录

    // CHomePopWindow mPopView;
    String mCurTab = SOUYUE_SPEC;
    /**
     * 应用宝典action
     */
    public static String ACTION_APPBIBLE = "com.zhongsou.appbible.ACTION_APPBIBLE";
//    private Http http;
    private TabWidget widget;
    private int mWidgetHeight;
    private NewReceiver newReceiver;
    private UpdateClientId updateClientid;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showBubble(msg.arg1);
                    break;
                case 2:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PingYinUtil.getPingYin("张");//为了初始化一下联系人列表，否则联系人列表第一次进入，缓慢
                        }
                    }).start();
                    break;
                case 1001:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    static class InitPushHandler extends Handler{
        WeakReference<Context> mReference;

        public InitPushHandler(Context context) {
            mReference= new WeakReference<Context>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                PushUtils.initJPush(MainApplication.getInstance());  //初始化JPush
                PushUtils.initMiPush(MainApplication.getInstance());  //初始化MiPush
//                PushUtils.initHwPush(MainApplication.getInstance());  //初始化huawei Push
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        instance = MainApplication.getInstance();
        initService();
        initTab();
        setReciever();
        uploadPhoneContacts();
        handleIntent(getIntent());
        // 统计 启动，激活用户，后台自己算是不是第一次
        UpEventAgent.onLaunch(this);
        UpEventAgent.onZSDevMainView(this,"","");
        // UpEventAgent.onDaemon(this, Daemon.checkSuccess(this));
        bubbleSp = getSharedPreferences("BUBBLESP", MODE_PRIVATE);
        loadBubleData(bubbleSp.getInt("bubblenum", 0));

        String isFirst = SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.IS_FIRST_ENTER, "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String str = formatter.format(new Date());
//        if (UIHelper.hasNetwork(this) == 0 && !isFirst.equals(str)) { //离线小说
//            // 是每天的第一次进入并且是wifi状态，进行下载
////			http.getDownloadFictionVersion();
//            DownloadFictionVersionRequest request =
//                    new DownloadFictionVersionRequest(HttpCommon.DOWNLOAD_FICTION_VERSION_REQUEST_ID, this);
//            request.setRefresh(true);
//            request.setParams();
//            CMainHttp.getInstance().doRequest(request);
//        }
        SYSharedPreferences.getInstance().putString(
                SYSharedPreferences.IS_FIRST_ENTER, str);
        /*
         * Intent tabRedIntent = new Intent();
		 * tabRedIntent.setAction("com.zhongsou.im.souyuemsg");
		 * tabRedIntent.putExtra("data",
		 * "{\"type\":\"discover\",\"category\":\"relogin\",\"title\":\"来自任务中心\",\"msg\":\"你完成一项任务，奖励5中搜币\",\"highlight\":\"5中搜币\",\"url\":\"http://\"}"
		 * ); sendBroadcast(tabRedIntent);
		 */
        // testRSA();

        handler.sendEmptyMessageDelayed(2, 1000);

//		MiPushClient.clearNotification(this);	//清楚小米推送弹出的通知栏
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ClientConfigReq req = new ClientConfigReq(HttpCommon.CLIENT_CONGIG, MainActivity.this);
                req.setParams(SYUserManager.getInstance().getUserId());
                CMainHttp.getInstance().doRequest(req);
//                http.getClientConfig();
            }
        }, 1000);
//        //启动推荐弹窗.延迟一秒，否则用户的token很可能拿不到。,拿不到token就不算弹出来了。
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                boolean firstInstallRecommend = sysp.getBoolean(SYSharedPreferences.FIRST_INSTALL_RECOMMEND, true);
//                if (firstInstallRecommend && SYUserManager.getInstance().getUser() != null && CMainHttp.getInstance().isNetworkAvailable(MainActivity.this)) {
//                    //第一次启动并且网络是通的。
//                    SubRecommendDialog.showDialog(MainActivity.this, true, 0L, 0L, false);
//                    sysp.putBoolean(SYSharedPreferences.FIRST_INSTALL_RECOMMEND, false);
//                }
//            }
//        }, 2000);

        //向IM发送，推送的注册ID
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String pushRegID = PushUtils.getPushRegID(MainActivity.this);
                if (StringUtils.isNotEmpty(pushRegID)
                        && CMainHttp.getInstance().isNetworkAvailable(MainActivity.this)) {
                    int pushChannel = PushUtils.getPushChannel();
                    UploadPushRegIDRequest request = new UploadPushRegIDRequest(HttpCommon.UPLOAD_PUSH_REGID_REQUEST, MainActivity.this);
                    request.setParams(pushRegID, pushChannel);
                    CMainHttp.getInstance().doRequest(request);
                }
            }
        }, 5000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HomeListManager.SHARE_TO_SSO_REQUEST_CODE) {
            if (fm != null) {
                List<Fragment> fragment = fm.getFragments();
                if (fragment != null) {
                    for (Fragment frag : fragment) {
                        if (frag != null) {
                            frag.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传通讯录
     */
    private void uploadPhoneContacts() {
        sysp = SYSharedPreferences.getInstance();
        dialog = new ImProgressDialog.Builder(MainActivity.this).create();
        sPreferences = getSharedPreferences("contect", MODE_PRIVATE);
        resolver = getContentResolver();

        isIMContactsUpload = sysp.getBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, false);
        mUploadTime = System.currentTimeMillis();
        upload();
    }

    /**
     * 开始上传
     */
    private void upload() {
        if (IntentUtil.isLogin()) {
            firstUpload = true;
            if (isIMContactsUpload) {
                long oldTime = sPreferences.getLong("time", 0);
                if (mUploadTime - oldTime >= IM_CONTACT_UPLOAD_GAP_TIME) {
                    ThreadPoolUtil.getInstance().execute(mUploadContactChangeRunnable);
                } else {
                    //24小时以内暂不做处理，直接跳转
                }
            } else {
                Map<Long, Integer> map = getContactVersion();
                SharedPreferences.Editor editor = sPreferences.edit();
                editor.putLong("time", mUploadTime);
                editor.putString("key", Arrays.toString(map.keySet().toArray()));
                editor.putString("value", Arrays.toString(map.values().toArray()));
                editor.commit();
                ThreadPoolUtil.getInstance().execute(mUploadContactsRunnable);
                sysp.putBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, true);
            }
        } else {
            firstUpload = false;
        }
    }

    /**
     * 获取联系人版本
     */
    public Map<Long, Integer> getContactVersion() {
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        Cursor c = null;
        try {
            c = resolver
                    .query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.VERSION}, null, null, null);
            while (c.moveToNext()) {
                long cid = c.getLong(c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
                int ver = c.getInt(c.getColumnIndex(ContactsContract.RawContacts.VERSION));
                map.put(cid, ver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    protected boolean meiZuHideSB() {
        return true;
    }

    /**
     * 处理来自外部的跳转 Intent 中须携带指定参数 tag or pos help
     * :IntentUtil.openMainActivity(...);
     */
    public static final int DEFAULT_NUM = -1;
    public static final int POSITION_NUM = 1;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    IMChatActivity.invoke(MainActivity.this, msg.getData().getInt(ImUIHelpr.TARGET_TYPE), msg.getData().getLong(ImUIHelpr.CHAT_ID));
                    break;
            }
            return false;
        }
    });

    private void handleIntent(Intent intent) {
        String tabTag = intent.getStringExtra(TAB_TAG_EXTRA);
        int tabIndex = intent.getIntExtra(TAB_INDEX_EXTRA, 0);
        boolean isJumpToChat = intent.getBooleanExtra(ImUIHelpr.IS_JUMP_CHAT, false);
        int targetType = intent.getIntExtra(ImUIHelpr.TARGET_TYPE, DEFAULT_NUM);
        long chatId = intent.getLongExtra(ImUIHelpr.CHAT_ID, DEFAULT_NUM);
        Serializable serializable = intent
                .getSerializableExtra(PUSH_INFO_EXTRA);
        Serializable shortcutInfo = intent.getSerializableExtra(SHORTCUT_EXTRA);
        Serializable adSerializable = intent.getSerializableExtra("ad_info");
        // int fromkickedout = 0;
        // if (intent.hasExtra("fromkickedout")) {
        // fromkickedout = intent.getIntExtra("fromkickedout", 0);
        // }
        if (serializable != null) {// 此分支为之前遗留的逻辑,我只负责把你送到这里,未来看你的造化了^_^
            if (isJumpToChat && targetType != DEFAULT_NUM && chatId != DEFAULT_NUM) {//如果三个条件都符合则直接走逻辑，先跳到聊天列表页Fragment 然后启动聊天界面
                setCurrentTabByIndex(POSITION_NUM);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt(ImUIHelpr.TARGET_TYPE, targetType);
                bundle.putLong(ImUIHelpr.CHAT_ID, chatId);
                msg.what = 1;
                msg.setData(bundle);
                mHandler.sendMessageDelayed(msg, 500);
            } else {
                StartActivityServiceTwo.onStart(
                        intent.getSerializableExtra("push_info"), this);
            }
        } else if (tabTag != null) {
            setCurrentTabByTag(tabTag);
            // if (fromkickedout == 1) {
            // if (null != fm.findFragmentByTag(tabTag) && null !=
            // ((SouyueTabFragment) fm.findFragmentByTag(tabTag)).mTitleView)
            // ((SouyueTabFragment)
            // fm.findFragmentByTag(tabTag)).mTitleView.updataUserName();
            // }
        } else {
            setCurrentTabByIndex(tabIndex);
        }

        if (shortcutInfo != null) {
            StartActivityServiceTwo.onStart(shortcutInfo, this);
        }

        if (adSerializable != null) {
            StartActivityServiceTwo.onStart(adSerializable, this);
        }
    }

    /**
     * 初始化底部选项卡
     */
    private void initTab() {
        Utils.mStutas = getStatusHeight(this);
        Utils.mMenuHeight = getMenuHeight();
        fm = getSupportFragmentManager();
//		if (android.os.Build.VERSION.SDK_INT> android.os.Build.VERSION_CODES.HONEYCOMB) {
//			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
//			getActionBar().setDisplayOptions(0);
//		}
        mTabHost = (MyFragmentTabHost) findViewById(android.R.id.tabhost);
        widget = (TabWidget) findViewById(android.R.id.tabs);
        mTabHost.setup(this, fm, R.id.main_content);

        mTabHost.getTabWidget().setStripEnabled(false);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setOnTabClickListener(this);
        mTabHost.getTabWidget().setDividerDrawable(null);

        // 搜悦
        TabSpec souYueSpec = mTabHost.newTabSpec(SOUYUE_SPEC);
        souyueTabView = new TabIndicator(this);
        souyueTabView.setView(R.string.tab_souyue,
                R.drawable.tabitem_home_souyue_img_sel, true);
        souyueTabView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN
                        && mCurTab.equals(SOUYUE_SPEC)) {
                    ((SouyueTabFragment) fm.findFragmentByTag(SOUYUE_SPEC))
                            .PullListRefresh();
                }
                return false;
            }
        });
        souYueSpec.setIndicator(souyueTabView);

        // 消息
        TabSpec msgSpec = mTabHost.newTabSpec(MSG_SPEC);
        msgTabView = new TabIndicator(this);
        msgTabView.setView(R.string.tab_msg,
                R.drawable.tabitem_home_msg_img_sel, false);
        msgSpec.setIndicator(msgTabView);

        // 发现
        TabSpec discoverSpec = mTabHost.newTabSpec(DISCOVER_SPEC);
        discoverTabView = new TabIndicator(this);
        discoverTabView.setView(R.string.tab_disvover,
                R.drawable.tabitem_home_discover_img_sel, false);
        discoverSpec.setIndicator(discoverTabView);

        // 我
        TabSpec meSpec = mTabHost.newTabSpec(ME_SPEC);
        meTabView = new TabIndicator(this);
        meTabView.setView(R.string.tab_my, R.drawable.tabitem_home_my_img_sel,
                false);
        meSpec.setIndicator(meTabView);

        Bundle b = new Bundle();
        b.putString("key", "1");

        mTabHost.addTab(souYueSpec, SouyueTabFragment.class, b);
        mTabHost.addTab(msgSpec, MsgTabFragment.class, b);
        mTabHost.addTab(discoverSpec, DiscoverTabFragment.class, b);
        mTabHost.addTab(meSpec, MineFragment.class, b);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!firstUpload) {
            upload();
        }

        if (((MainApplication) getApplication()).isShowingBottomTab()) {
            checkIfShowMsg();
            checkShowRed();
        }
        updateVersionNew();
        IntentUtil.checkSysRecommendMsg(this); // 检测并弹出专题推荐
//        IntentUtil.checkAndShowSubRecommendDialog(this);//检测并弹出订阅推荐

    }

    private void checkShowRed() {
        if (SYSharedPreferences.getInstance().getBoolean(
                SYSharedPreferences.KEY_TASKCENTER_REDSHOW, false)) {
            setTabViewBageTips(2, -1);
            // 设置发现页强刷标记
            ((MainApplication) getApplication())
                    .setNeedForceRefreshDiscover(true);
            SYSharedPreferences.getInstance().remove(
                    SYSharedPreferences.KEY_TASKCENTER_REDSHOW);
        }
    }

    private void checkIfShowMsg() {
        String jsonStr = SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.KEY_TASKCENTER_DISCOVERMSG, "");
        if (jsonStr.length() > 0) {// 有缓存的消息，通知提醒
            Intent intent = new Intent();
            intent.setAction("com.zhongsou.im.souyuemsg");
            intent.putExtra("data", jsonStr);
            sendBroadcast(intent);
        }
    }

    public int updateTabWidget(float persent) {

        // add by yinguanping 当前底部状态栏显示状态
        if (persent > -0.01f) {// 底部tab栏显示
            ((MainApplication) getApplication()).setShowingBottomTab(true);
            checkIfShowMsg();
        } else if (persent < -0.99f) {// 隐藏
            ((MainApplication) getApplication()).setShowingBottomTab(false);
        }
        // ---end

        if (mWidgetHeight == 0) {
            mWidgetHeight = widget.getHeight();
        }
        int sh = (int) (mWidgetHeight * persent);
//         widget.scrollTo(0,sh);
        widget.setPadding(0, 0, 0, sh);
        widget.requestLayout();
//        int[] location = new int[2];
//        widget.getLocationOnScreen(location);
        return sh;
    }

    public int getmWidgetHeight() {
        return mWidgetHeight;
    }

    // public int getTabHostBottom() {
    // int[] location = new int[2];
    // widget.getLocationOnScreen(location);
    // return location[1];
    // }

    /**
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public int getMenuHeight() {
        Resources resources = this.getResources();

        int rid = resources.getIdentifier("config_showNavigationBar", "bool",
                "android");
        if (rid > 0) {
            Log.d("sam test", resources.getBoolean(rid) + ""); // 获取导航栏是否显示true
            // or false
        }

        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            Log.d("sam test", resources.getDimensionPixelSize(resourceId) + ""); // 获取高度
        }
        int height = 0;
        if (rid > 0 && resourceId > 0 && resources.getBoolean(rid)) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    // public boolean getMenuShow() {
    // Resources resources = this.getResources();
    //
    // int rid = resources.getIdentifier("config_showNavigationBar", "bool",
    // "android");
    // if (rid > 0) {
    // Log.d("sam test", resources.getBoolean(rid) + ""); //获取导航栏是否显示true or
    // false
    // return resources.getBoolean(rid);
    // }
    // return false;
    // }
    //
    //
    // public int getTabHostLocation() {
    // int[] location = new int[2];
    // widget.getLocationOnScreen(location);
    // return location[1];
    // }

    @Override
    public void onTabChanged(String tabId) {
        mCurTab = tabId;

        if (SOUYUE_SPEC.equals(tabId)) {
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.TAB_HOME);    //Umeng
            UpEventAgent.onZSDevTabItem(this,getString(R.string.tab_souyue));
//			ToastUtil.show(this, "tab_home");
        } else if (MSG_SPEC.equals(tabId)) {
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.TAB_IM);    //Umeng
            UpEventAgent.onZSDevTabItem(this,getString(R.string.tab_msg));
//			ToastUtil.show(this, "tab_im");
        } else if (DISCOVER_SPEC.equals(tabId)) {
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.TAB_DISCOVER);    //Umeng
            UpEventAgent.onZSDevTabItem(this,getString(R.string.tab_disvover));
//			ToastUtil.show(this, "tab_discover");
        } else if (ME_SPEC.equals(tabId)) {
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.TAB_MY);    //Umeng
            UpEventAgent.onZSDevTabItem(this,getString(R.string.tab_my));
//			ToastUtil.show(this, "tab_my");
        } else {
//			ToastUtil.show(this, "default");
        }

        if (fm.findFragmentByTag(SOUYUE_SPEC) != null)
            ((BaseTabFragment) fm.findFragmentByTag(SOUYUE_SPEC)).refresh();
        if (fm.findFragmentByTag(tabId) != null)
            ((BaseTabFragment) fm.findFragmentByTag(tabId)).refresh();

        if (MSG_SPEC.equals(tabId) && ImserviceHelp.getInstance().getNotifyNum(1) > 1) {//用于当界面fragment切换到聊天界面时，清除通知栏大于两个、联系人的通知
            ImserviceHelp.getInstance().cancelNotify(1);
        }
    }

    public void setEnable(boolean enable){
        TabWidget wid = mTabHost.getTabWidget();
        if (enable){
            wid.setEnabled(true);
            wid.setClickable(true);
            wid.setLongClickable(true);
        }else{
            wid.setEnabled(false);
            wid.setClickable(false);
            wid.setLongClickable(false);
        }
    }

    /**
     * @param tabId 检查用户是否登陆 否返回false
     * @return 返回是否登录
     */
    @Override
    public boolean checkLogin(String tabId) {
        return true;
    }

    @Override
    public void setCurrentTabByTag(String tabId) {
        mTabHost.setCurrentTabByTag(tabId);
    }

    @Override
    public void setTabViewBageTips(int pos, int newMsgCount) {
        switch (pos) {
            case 0:
                souyueTabView.setNewMsgCountBadge(newMsgCount);
                break;
            case 1:
                msgTabView.setNewMsgCountBadge(newMsgCount);
                break;
            case 2:
                discoverTabView.setNewMsgCountBadge(newMsgCount);
                break;
            case 3:
                meTabView.setNewMsgCountBadge(newMsgCount);
                break;
        }
    }

    @Override
    public void setCurrentTabByIndex(int pos) {
        mTabHost.setCurrentTab(pos);
    }

    /**
     * 弹出友盟升级提示 升级已经修改为从自己服务器上升级
     */
    // private void updateVersion() {
    // UmengUpdateAgent.update(this);
    // }

    /**
     * 新版本更新，自己服务器
     */
    private void updateVersionNew() {
        // 请求版本更新信息
        if ("".equals(SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.UPDATE, ""))
                || !new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                .equals(SYSharedPreferences.getInstance().getString(
                        SYSharedPreferences.UPDATE, ""))) {
            ((MainApplication) this.getApplication()).checkVersion(0);
        }
    }

    /**
     * 初始化一些恶心的服务的操作请大家放这里
     */
    private void initService() {
        initPushService();
//        http = new Http(this);
        // updateVersion();//废除掉
        initImServices();
        SEARCH_PATH_MEMORY_DIR = getFilesDir().getAbsolutePath() + "/im_index";
//        initLocationManager();
        updateClientid = new UpdateClientId(HttpCommon.UPDATECLIENT_REQUEST_ID1, null);
        updateClientid.setParams("add");
        mMainHttp.doRequest(updateClientid);

        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();
        sysp.putString(SYSharedPreferences.KEY_SHOW_GUIDE_DY_NEW,
                SYSharedPreferences.FIRSTGUIDE);
        sysp.putString(SYSharedPreferences.KEY_VERSION,
                DeviceInfo.getAppVersion());

        new InitPushHandler(this).sendEmptyMessageDelayed(0, 1000);  //使用Handler初始化推送服务
        //定位  延时60s
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initLocationManager();
            }
        }, 60 * 1000);
    }

    private class BubbleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UrlConfig.HIDE_TABRED_ACTION)) {// 隐藏红点气泡
                int tag = intent.getIntExtra("tag", -2);// 隐藏或显示标记
                setTabViewBageTips(2, tag);
            } else {
                sendBubbleNum();
            }
        }
    }

    public void initLocationManager() {
        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
            return;
        }
        try {
            mAMapLocManager = LocationManagerProxy.getInstance(this);
//			mAMapLocManager.requestLocationUpdates(
//					LocationProviderProxy.AMapNetwork, 5 * 60 * 1000, 5000,
//					locationListener);
            mAMapLocManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 5 * 60 * 1000, 5000,
                    locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AMapLocationListener locationListener = new AMapLocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onLocationChanged(AMapLocation arg0) {
            if (setLocation(arg0)) {
                detective();
                updateClientid = new UpdateClientId(HttpCommon.UPDATECLIENT_REQUEST_ID, null);
                updateClientid.setParams("add");
                mMainHttp.doRequest(updateClientid);
            }
        }
    };

    protected void detective() {
        if (mAMapLocManager != null) {
            mAMapLocManager.removeUpdates(locationListener);
            mAMapLocManager.destory();
        }
        mAMapLocManager = null;
    }

    protected boolean setLocation(AMapLocation location) {
        if (location != null) {
            String p = location.getProvince();
            if (TextUtils.isEmpty(p)) {
                p = location.getCity();
            }
            SYSharedPreferences.getInstance().putString(
                    SYSharedPreferences.KEY_CITY, location.getCity());
            SYSharedPreferences.getInstance().putString(
                    SYSharedPreferences.KEY_PROVINCE, p);
            SYSharedPreferences.getInstance().putString(
                    SYSharedPreferences.KEY_LAT, location.getLatitude() + "");
            SYSharedPreferences.getInstance().putString(
                    SYSharedPreferences.KEY_LNG, location.getLongitude() + "");
            return true;
        }
        return false;
    }

    // im气泡广播
    private void setReciever() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(BroadcastUtil.ACTION_MSG_ADD);
        inf.addAction(BroadcastUtil.ACTION_CONTACT_AND_MSG);
        inf.addAction(BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE);
        inf.addAction(BroadcastUtil.ACTION_MSG_SEND_SUCCESS);
        inf.addAction(UrlConfig.HIDE_TABRED_ACTION);
        getbubblereceiver = new BubbleReceiver();
        registerReceiver(getbubblereceiver, inf);

        IntentFilter newFileter = new IntentFilter();
        newFileter.addAction(BroadcastUtil.ACTION_EXPRESSION_NEW);
        newReceiver = new NewReceiver();
        newReceiver.register(this, newFileter);

        /**
         * im上传通讯录相关
         */
        IntentFilter uploadContactFilter = new IntentFilter();
        uploadContactFilter.addAction(BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_SUCCESS);
        uploadContactFilter.addAction(BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR);

        uploadContactReceiver = new UploadContactReceiver();
        registerReceiver(uploadContactReceiver, uploadContactFilter);
    }

    private class NewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BroadcastUtil.ACTION_EXPRESSION_NEW.equals(action)) {
                boolean newExpression = intent.getExtras().getBoolean(
                        "newExpression");
                Slog.d("callback", "登陆 标新广播");
                sysp.setNewExpression(newExpression);
            }

        }

        public void register(Context context, IntentFilter filter) {

            context.registerReceiver(this, filter);

        }

    }

    public class UploadContactReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_SUCCESS)) {     //上传成功
                Log.i("uploadContact", "----->success");
                Map<Long, Integer> newMap = getContactVersion();
                SharedPreferences.Editor editor = sPreferences.edit();
                editor.putLong("time", mUploadTime);
                editor.putString("key", Arrays.toString(newMap.keySet().toArray()));
                editor.putString("value",
                        Arrays.toString(newMap.values().toArray()));
                editor.commit();

            } else if (intent.getAction().equals(BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR)) { //上传失败
                Log.i("uploadContact", "----->error");
                sysp.putBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, true);
            }
        }

    }

    // 显示未读消息气泡数
    public void loadBubleData(int num) {
        if (num != 0) {
            setTabViewBageTips(1, num);
        } else {
            setTabViewBageTips(1, 0);
        }
        BadgeUtil.sendBadgeBraodcast(this, num);
    }

    /**
     * 启动im消息服务为微信服务
     */
    private void initImServices() {
        Intent intent = new Intent(this, Imservice.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Tuita", "service conn onServiceDisconnected " + name);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            instance.imService = ImAidlService.Stub.asInterface(service);
            if (instance.imService != null) {
                ThreadPoolUtil.getInstance().execute(new Runnable() {

                    @Override
                    public void run() {
                        // User user = SYUserManager.getInstance().getUser();
                        // if (user != null
                        // && user.userType().equals(
                        // SYUserManager.USER_ADMIN)) {
//						ImserviceHelp.getInstance().im_connect(
//								DeviceInfo.getAppVersion());
                        ImserviceHelp.getInstance().initImService();

//						Log.i("notifyCation", "ID =" + getIntent().getIntExtra(TAB_INDEX_EXTRA, 0));
                        if (getIntent().getIntExtra(TAB_INDEX_EXTRA, 0) == 1) {//当进入聊天列表页就清空一下通知栏
                            ImserviceHelp.getInstance().cancelNotify(1);
                        }
                        sendBubbleNum();
                        // }
                    }
                });
            }
            Log.i("Tuita", "service conn onServiceConnected"
                    + instance.imService.toString());
        }
    };

    private void sendBubbleNum() {
        int sum = 0;
        List<MessageRecent> messageRecents = ImserviceHelp.getInstance()
                .db_getMessageRecent();
        Config c = ImserviceHelp.getInstance().db_getConfig();
        long unread = 0;

        if (messageRecents != null)
            for (MessageRecent recent : messageRecents) {
                sum += recent.isNotify() && recent.getJumpType() == MessageRecent.ITEM_JUMP_IMCHAT ? recent.getBubble_num() : 0;
            }
        if (c != null) {
            unread = c.getFriend_bubble();
            sum += unread;
        }
        Message message = new Message();
        message.what = 1;
        message.arg1 = sum;
        handler.sendMessage(message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        JuliConnect.disConnectJuli();
        // if (conn != null)
        // unbindService(conn);
        // MainApplication.getInstance().imService = null;
        //移除handler，的callback，防止发生诡异的问题。
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (uploadContactReceiver != null) {
            unregisterReceiver(uploadContactReceiver);
        }

        unregisterReceiver(getbubblereceiver);
        unregisterReceiver(newReceiver);
//        stopService(new Intent(this, DownloadBookZipService.class));
        if (conn != null)
            unbindService(conn);
    }
////
////    //
////    //	弹框测试代码
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_MENU){
//         Intent intent = new Intent(this,SubGroupActivity.class);
////         Intent intent = new Intent(this,FirstInActivity.class);
//            startActivity(intent);
//		}
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            /*
             * long secondTime = System.currentTimeMillis(); if (secondTime -
			 * firstTime > 2000) { Toast.makeText(this,
			 * getString(R.string.exit_app), Toast.LENGTH_SHORT).show();
			 * firstTime = secondTime;//firstTime return true; } else {
			 * ZhongSouActivityMgr.getInstance().exit(); System.exit(0); }
			 */
                // ViewGroup mRootView = (ViewGroup)
                // (getWindow().getDecorView().findViewById(android.R.id.content));
                // if (mPopView != null) {
                // mPopView.dismiss();
                // return true;
                // }
                // ZhongSouActivityMgr.getInstance().exit();
                moveTaskToBack(true);
                break;
        }

        // return super.onKeyUp(keyCode, event);
        return true;
    }

    @Override
    public void showRedNum(int num) {
        SharedPreferences.Editor edit = bubbleSp.edit();
        edit.putInt("bubblenum", num);
        edit.commit();
        loadBubleData(num);
    }

    private void showBubble(int num) {
        loadBubleData(num);
    }

//    /**
//     * 离线小说下载回调
//     * @param response
//     */
//    public void getDownloadFictionVersionSuccess(HttpJsonResponse response) {
//        long version = response.getBody().get("version").getAsLong();
//        if (version > SYSharedPreferences.getInstance().getLong(
//                SYSharedPreferences.FICTION_VERSION, -1)) {
//            SYSharedPreferences.getInstance().putLong(
//                    SYSharedPreferences.FICTION_VERSION, version);
//            startService(new Intent(this, DownloadBookZipService.class));
//        }
//    }

//    /**
//     * 离线小说下载回调
//     * 原有逻辑处理与成功返回逻辑相同
//     * @param response
//     */
//    public void getDownloadFictionVersionFail(HttpJsonResponse response) {
//        getDownloadFictionVersionSuccess(response);
//    }

    /**
     * 开启服务操作，从MainApplication搬过来
     */
    private void initPushService() {
        if (ConfigApi.isSouyue()) {
            PushService.setTest(MainApplication.getInstance(),
                    ConstantsUtils.PUSH_TEST);
            //启动卸载推荐
            PushService.startService(MainApplication.getInstance());
        } else {
            boolean pushSwitchState = sysp.getBoolean(
                    SYSharedPreferences.KEY_PUSHSWITCH,
                    ConstantsUtils.PUSH_DEFAULT_OPEN);// 获取消息推送服务开关状态-- YanBin
            if (pushSwitchState) {
                PushService.setTest(MainApplication.getInstance(),
                        ConstantsUtils.PUSH_TEST);
                PushService.startService(MainApplication.getInstance());
            } else {
                PushService.stopService(MainApplication.getInstance());
            }
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
//            case HttpCommon.DOWNLOAD_FICTION_VERSION_REQUEST_ID:
//                getDownloadFictionVersionSuccess(request.<HttpJsonResponse>getResponse());
//                break;
            case HttpCommon.CLIENT_CONGIG:
                getClientConfigSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.UPLOAD_PUSH_REGID_REQUEST:  //上传Push 的 regID
                long userID = Long.parseLong(SYUserManager.getInstance().getUserId());
                Log.d(TAG, "UPLOAD_PUSH_REGID_REQUEST success");
                JSONObject json = request.getResponse();
                int status = 0;
                String msg = null;
                try {
                    status = json.getInt("status");
                    msg = json.getString("msg");    //暂不使用
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(status == 200){
                    CommSharePreference.getInstance().putValue(userID, ConstantsUtils.PUSH_REGID_NAME, false);
                }else{
                    Log.d(TAG, "UPLOAD_PUSH_REGID_REQUEST error");
                }
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()) {
//            case HttpCommon.DOWNLOAD_FICTION_VERSION_REQUEST_ID:
//                getDownloadFictionVersionFail(request.<HttpJsonResponse>getResponse());
            //                break;
//            case HttpCommon.UPLOAD_PUSH_REGID_REQUEST:  //上传Push 的 regID  自定义格式  不再走此处
//                long userID = Long.parseLong(SYUserManager.getInstance().getUserId());
//                Log.d(TAG, "UPLOAD_PUSH_REGID_REQUEST error");
        }
    }

    /**
     * 控制客户端数据及开关 ，返回数据
     *
     * @param res
     */
    public void getClientConfigSuccess(HttpJsonResponse res) {
        new ClientConfig(this, res);
    }

    /**
     * 上传通讯录变化
     */
    private Runnable mUploadContactChangeRunnable = new Runnable() {

        @Override
        public void run() {
//            Cursor cur = null;
            try {
                ArrayList<Long> idList = new ArrayList<Long>();// 接收变化的联系人id
                Map<Long, Integer> newMap = getContactVersion();
                Map<Long, Integer> oldMap = stringToMap(
                        sPreferences.getString("key", ""),
                        sPreferences.getString("value", ""));
                Set<Long> set = newMap.keySet();
                for (Long id : set) {
                    if (oldMap.containsKey(id)) {// 存在
                        if (oldMap.get(id) != newMap.get(id)) {// 老版本不等于新版本
                            idList.add(id);
                        }
                    } else {// 不存在
                        idList.add(id);
                    }
                }
                if (idList.size() >= 0) {
                    for (Long id : idList) {
                        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{"display_name", "data1"},
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
                        cursor.moveToFirst();
                        String number = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (number.length() < 11) {
                            cursor.moveToNext();
                            continue;
                        }
                        if (number.startsWith("0")) {
                            cursor.moveToNext();
                            continue;
                        }
                        number = number.replaceAll("\\D", "");
                        if (number.length() > 11) {
                            number = number.substring(number.length() - 11,
                                    number.length());
                        }
                        String name = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        Contact person = new Contact();
                        person.setNick_name(name);
                        person.setPhone(number);
                        contactList.add(person);

                        try {
                            if (cursor != null && !cursor.isClosed())
                                cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
            } finally {

            }
            try {
                if (contactList != null && contactList.size() > 0) {
                    ImserviceHelp.getInstance().im_contacts_upload(
                            new Gson().toJson(contactList));
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                } else {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                }
            } catch (Exception e) {
            }

        }
    };

    private Map<Long, Integer> stringToMap(String str, String str2) {
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        String[] keys = str.replace("[", "").replace("]", "")
                .replaceAll(" ", "").split(",");
        String[] values = str2.replace("[", "").replace("]", "")
                .replaceAll(" ", "").split(",");
        for (int i = 0; i < values.length; i++) {
            map.put(Long.valueOf(keys[i]), Integer.valueOf(values[i]));
        }
        return map;
    }

    /**
     * 上传全部通讯录
     */
    private Runnable mUploadContactsRunnable = new Runnable() {

        @Override
        public void run() {
            Cursor cur = null;
            try {
                cur = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{"display_name", "data1"}, null, null,
                        null);
                cur.moveToFirst();
                while (cur.getCount() > cur.getPosition()) {

                    String number = cur.getString(cur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (number.length() < 11) {
                        cur.moveToNext();
                        continue;
                    }
                    if (number.startsWith("0")) {
                        cur.moveToNext();
                        continue;
                    }
                    number = number.replaceAll("\\D", "");
                    if (number.length() > 11) {
                        number = number.substring(number.length() - 11,
                                number.length());
                    }

                    String name = cur.getString(cur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Contact person = new Contact();
                    person.setNick_name(name);
                    person.setPhone(number);
                    contactList.add(person);
                    cur.moveToNext();
                }
            } catch (Exception e) {
            } finally {
                try {
                    if (cur != null && !cur.isClosed())
                        cur.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            try {
                if (contactList != null && contactList.size() > 0) {
                    ImserviceHelp.getInstance().im_contacts_upload(
                            new Gson().toJson(contactList));
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                } else {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    ImContactDialog.Builder build = new ImContactDialog.Builder(
                            MainActivity.this);
                    build.setPositiveButton(new ImContactDialog.Builder.ImContactDialogInterface() {

                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            // finish();
                        }
                    });
                    build.create().show();
                }
            } catch (Exception e) {
            }

        }

    };
}
