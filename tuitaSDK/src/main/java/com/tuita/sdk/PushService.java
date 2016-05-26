package com.tuita.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;

import android.text.TextUtils;
import com.andruby.alive.daemon.Daemon;
import com.google.gson.Gson;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.helper.MessageRecentDaoHelper;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.log.Logger;

import cn.jpush.android.service.DaemonService;
import so.raw.daemon.DaemonManager;

public class PushService extends Service {
    private static final String LOGTAG = PushService.class.getSimpleName();
    private static final String ACTION_PREFIX = "com.tuita.sdk";
    private static final String ACTION_START = ACTION_PREFIX + ".START";
    private static final String ACTION_STOP = ACTION_PREFIX + ".STOP";
    private static final String ACTION_KEEPALIVE = ACTION_PREFIX + ".KEEPALIVE";
    private static final String ACTION_RECONNECT = ACTION_PREFIX + ".RECONNECT";
    private static final String ACTION_SET_SOUND = ACTION_PREFIX + ".SET_SOUND";
    private static final String ACTION_SET_VIBRATE = ACTION_PREFIX + ".SET_VIBRATE";
    private static final String ACTION_SET_TEST = ACTION_PREFIX + ".SET_TEST";
    private static final String ACTION_SET_MSGNUM = ACTION_PREFIX + ".SET_MSGNUM";
    private static final String ACTION_SET_IMUSER = ACTION_PREFIX + ".SET_IMUSER";
    private static final String ACTION_SET_INCHAT = ACTION_PREFIX + ".SET_INCHAT";
    public final static String mAction = "com.tuita.sdk.messagesendreceiver";
    protected static final String PREF_SOUND = "Tuita_SOUND";
    protected static final String PREF_VIBRATE = "Tuita_VIBRATE";
    protected static final String PREF_INCHAT = "Tuita_CURRENT";
    protected static final String PREF_CAN_PUSH_MESSAGE = "pushSwitch";
    protected static final String IM_USER_IDENTITY = "Tuita_USER_IDENTITY";
    private static final long KEEP_ALIVE_INTERVAL = 3 * 60 * 1000;
    private static final long RECONNECT_INTERVAL = KEEP_ALIVE_INTERVAL / 2;
    private static AlarmManager alarmManager;
    private static final Map<String, PendingIntent> intentMap = new HashMap<String, PendingIntent>();
    public static int msgNum = 0;
    public static final int RETRY_COUNT = 2;    //重试固定次数

    MessageSendReceiver receiver = null;
    final Map<Long, Future> mFutureMap = new HashMap<Long, Future>();   //为了取消发送任务的

    private static SharedPreferences getPreferences(Context ctx) {
        return ctx.getSharedPreferences(TuitaSDKManager.TAG, Context.MODE_PRIVATE);
    }

    @TargetApi(11)
    private static SharedPreferences getPreferencess(Context ctx) {
        if (Build.VERSION.SDK_INT >= 11) {
            return ctx.getSharedPreferences(TuitaSDKManager.SOUYUE_TAG, 0x0004);
        } else {
            return getPreferences(ctx);
        }
    }

    private static void putBoolean(Context ctx, String key, boolean val) {
        try {
            SharedPreferences sp = getPreferences(ctx);
            if (sp != null) {
                sp.edit().putBoolean(key, val).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void putLong(Context ctx, String key, long val) {
        try {
            SharedPreferences sp = getPreferences(ctx);
            if (sp != null) {
                sp.edit().putLong(key, val).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getEnableSound(Context ctx) {
        return getPreferences(ctx).getBoolean(PREF_SOUND, true);
    }

    protected static boolean canPushMessage(Context ctx) {

        boolean b = getPreferencess(ctx).getBoolean(PREF_CAN_PUSH_MESSAGE, true);

        Log.i("canPushMessage", "pushswitch--------->" + b);
        return b;
    }

    public static void setEnableSound(Context ctx, boolean ifEnable) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_SET_SOUND);
        i.putExtra("enable", ifEnable);
        ctx.startService(i);
    }

    public static boolean getEnableVibrate(Context Context) {
        return getPreferences(Context).getBoolean(PREF_VIBRATE, true);
    }

    public static void setEnableVibrate(Context ctx, boolean ifEnable) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_SET_VIBRATE);
        i.putExtra("enable", ifEnable);
        ctx.startService(i);
    }

    public static void setTest(Context ctx, int ifTest) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_SET_TEST);
        i.putExtra("test", ifTest);
        ctx.startService(i);
    }

    public static void startService(Context ctx) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    public static void stopService(Context ctx) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    public static void setMsgNum(Context ctx) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_SET_MSGNUM);
        ctx.startService(i);
    }

    public static void setImUserIdentity(Context ctx, boolean isLogin) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_SET_IMUSER);
        i.putExtra(IM_USER_IDENTITY, isLogin);
        ctx.startService(i);
    }

    public static boolean getImUserIdentity(Context Context) {
        return getPreferences(Context).getBoolean(IM_USER_IDENTITY, false);
    }

    public static void setIsInChat(Context ctx, long currentId) {
        Intent i = new Intent(ctx, PushService.class);
        i.setAction(ACTION_SET_INCHAT);
        i.putExtra(PREF_INCHAT, currentId);
        ctx.startService(i);
    }

    public static Long getIsInChat(Context Context) {
        return getPreferences(Context).getLong(PREF_INCHAT, 0);
    }

    private TuitaSDKManager tuitaMgr = null;
    private boolean hasStartKeepAlives;

    private void closeConnection() {
        Logger.i("tuita", "PushService.closeConnection", "PushService closeConnection()");
//		getAlarmManager().cancel(getPendingIntent(ACTION_RECONNECT));
        getAlarmManager().cancel(getPendingIntent(ACTION_KEEPALIVE));
        hasStartKeepAlives = false;
        stopSDK();
    }

    private AlarmManager getAlarmManager() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        return alarmManager;
    }

    private PendingIntent getPendingIntent(String action) {
        PendingIntent iGet = intentMap.get(action);
        if (iGet != null) {
            return iGet;
        }
        Intent i = new Intent();
        i.setClass(this, PushService.class);
        i.setAction(action);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        intentMap.put(action, pi);
        return pi;
    }

    private void keepAlive() {
        Log.i(LOGTAG, "keepAlive");
        try {
            new Thread("keepAlive") {
                public void run() {
                    Log.i(LOGTAG, "keepAlive,thread run");
                    boolean b = tuitaMgr.ping();
                    Log.i(LOGTAG, "keepAlive,tuitaMgr.ping()=" + b);
                    if (!b) {
                        openOrCloseConnection();
                    }
                }
            }.start();
        } catch (Throwable e) {
            Logger.e("tuita", "PushService.keepAlive", "keepAlive fail", e);
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
        tuitaMgr = TuitaSDKManager.getInstance(getApplicationContext());
        Daemon.run(this, PushService.class, Daemon.INTERVAL_ONE_MINUTE * 3, makeFeedBackUrl(getApplicationContext()));
        DaemonManager.Action action = new DaemonManager.Action(DaemonManager.TYPE_SERVICE,new ComponentName(this,PushService.class));
        DaemonManager.getManager().startDaemon(this,60 * 1000,action);
        receiver = new MessageSendReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(mAction);
        this.registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.i(LOGTAG, "onDestroy");
        if (receiver != null) {
            try {
                this.unregisterReceiver(receiver);
            } catch (Exception e) {
                Logger.e("tuita", "PushService.onDestroy", "Receiver unregister error", e);
                e.printStackTrace();
            }
        }
        stopForeground(true);
        stop();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null) {
            String action = intent.getAction();
            Logger.i("tuita", "PushService.onStart", action);
            if (action != null) {
                if (action.equals(ACTION_STOP) == true) {
                    putBoolean(this, PREF_CAN_PUSH_MESSAGE, false);

                    Logger.i("tuita", "PushService.onStart", "PushService stop");
                    if (!isNetworkAvailable())
                        stop();
                } else if (action.equals(ACTION_START) == true) {
                    putBoolean(this, PREF_CAN_PUSH_MESSAGE, true);
                    Logger.i("tuita", "PushService.onStart", "PushService start");
                    start();
                } else if (action.equals(ACTION_KEEPALIVE) == true) {
                    keepAlive();
                } else if (action.equals(ACTION_RECONNECT) == true) {
                    openConnection();
                } else if (action.equals(ACTION_SET_SOUND)) {
                    putBoolean(this, PREF_SOUND, intent.getBooleanExtra("enable", true));
                } else if (action.equals(ACTION_SET_VIBRATE)) {
                    putBoolean(this, PREF_VIBRATE, intent.getBooleanExtra("enable", true));
                } else if (action.equals(ACTION_SET_TEST)) {
                    // 2 线上 1 预上线 0 测试
                    int test = intent.getIntExtra("test", 2);
                    if (test == 0) {       //搜悦的224和239对应im  测试环境
                        TuitaSDKManager.TUITA_CENTER_HOST = TuitaSDKManager.TUITA_CENTER_HOST_TEST_INSIDE;
                    } else if (test == 3) {  //搜悦的开发环境
                        TuitaSDKManager.TUITA_CENTER_HOST = TuitaSDKManager.TUITA_CENTER_HOST_TEST_INSIDE;
                    } else if (test == 1 || test == 4) {       //搜悦的225和另一个环境对应im 预上线
                        TuitaSDKManager.TUITA_CENTER_HOST = TuitaSDKManager.TUITA_CENTER_HOST_TEST;
                    } else {    //正式环境
                        TuitaSDKManager.TUITA_CENTER_HOST = TuitaSDKManager.TUITA_CENTER_HOST_ONLINE;
                    }
                } else if (action.equals(ACTION_SET_MSGNUM)) {
                    msgNum = 0;
                } else if (action.equals(ACTION_SET_IMUSER)) {
                    putBoolean(this, IM_USER_IDENTITY, intent.getBooleanExtra(IM_USER_IDENTITY, false));
                } else if (action.equals(ACTION_SET_INCHAT)) {
                    putLong(this, PREF_INCHAT, intent.getLongExtra(PREF_INCHAT, 0));
//                    putBoolean(this, PREF_INCHAT, intent.getBooleanExtra(PREF_INCHAT, false));
                } else if (Daemon.DAEMON_ACTION.equals(action)) {
                    Daemon.writeSucessFile(this);
                    putBoolean(this, PREF_CAN_PUSH_MESSAGE, true);
                    start();
                } else if (Daemon.DAEMON_ACTION.equals(action)) {
                    Daemon.writeSucessFile(this);
                    putBoolean(this, PREF_CAN_PUSH_MESSAGE, true);
                    start();
                }
            }
        } else {
            Logger.i("tuita", "PushService.onStart", "this action == null");
            putBoolean(this, PREF_CAN_PUSH_MESSAGE, true);
            start();
        }
    }

    private void openConnection() {
        try {
            new Thread("openConnection") {
                public void run() {
                    //检查网络或tuita服务是否可用
                    boolean b = tuitaMgr.start();
                    Logger.i("tuita", "PushService.openConnection", "openConnection,thread run,tuitaMgr.start()=" + b);
                    Log.i(LOGTAG, "openConnection,thread run,tuitaMgr.start()=" + b);
                    if (b) {
                        startKeepAlives();
                    } else {
                        openOrCloseConnection();
                    }
                }
            }.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void openOrCloseConnection() {
        Logger.i("tuita", "PushService.openOrCloseConnection", "openOrCloseConnection ----> " + isNetworkAvailable());
        if (isNetworkAvailable()) {
            scheduleReconnect();
        } else {
            closeConnection();
        }
    }

    private void scheduleReconnect() {
        Log.i(LOGTAG, "scheduleReconnect");
        Logger.i("tuita", "PushService.scheduleReconnect", "scheduleReconnect method running");
        Intent i = new Intent(this, PushService.class);
        i.setAction(ACTION_RECONNECT);
        startService(i);
//		getAlarmManager().set(AlarmManager.RTC, System.currentTimeMillis() + RECONNECT_INTERVAL, getPendingIntent(ACTION_RECONNECT));
    }

    private void start() {
        Logger.i("tuita", "PushService.start", "start()  注册网络广播");
        //5.0新改 以前网络改为静态注册
        openConnection();
    }


    private void startForeground() {
        Notification note = new Notification(0, null, System.currentTimeMillis());
        note.flags |= Notification.FLAG_NO_CLEAR;
        if (android.os.Build.VERSION.SDK_INT < 18) startForeground(134138, note);
    }

    private void startKeepAlives() {
        if (hasStartKeepAlives) {
            return;
        }
        Logger.i("tuita", "PushService.startKeepAlives", "startKeepAlives");
        getAlarmManager().setRepeating(AlarmManager.RTC, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, getPendingIntent(ACTION_KEEPALIVE));
        hasStartKeepAlives = true;
    }

    private void stop() {
        Log.i(LOGTAG, "stop");
        Logger.i("tuita", "PushService.stop", "stopKeepAlives");
        closeConnection();
    }

    private void stopSDK() {
        Log.i(LOGTAG, "---->tuitaMgr.stop()");
        try {
            tuitaMgr.stop();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public class MessageSendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {//action为空，则记录日志
                Logger.i("tuita", "MessageSendReceiver.onReceive", "action can't be null");
                return;
            } else if (action.equals(mAction)) {//action为当前想要的action按照逻辑顺序处理
                final int type = intent.getIntExtra("type", 0);
                final long uidorgid = intent.getLongExtra("uidorgid", 0);
                final int contentType = intent.getIntExtra("contentType", 0);
                final String content = intent.getStringExtra("content");
                final String uuid = intent.getStringExtra("uuid");
                int retry = intent.getIntExtra("retry", 0);
                final ImCommand firstCmd = ImCommand.newSendMessageCmd(type, uidorgid, contentType, content, uuid, retry, 1);
                tuitaMgr.getConnection().write(firstCmd.getPacket());
                tuitaMgr.getImmanager().getRunningCmd().put(firstCmd.getTid(), firstCmd);
                final long tid = firstCmd.getTid();
                Future future = tuitaMgr.getScheduler().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        ImCommand cmd = tuitaMgr.getImmanager().getRunningCmd().get(tid);
                        Future runFuture = mFutureMap.get(tid);
                        if (cmd != null) {      //发送消息超时
                            if (cmd.getRetryCount() > RETRY_COUNT) {       //重发超过2次,直接返回错误通知界面
                                android.util.Log.i("sendMsgLog","超过次数---->"+cmd.getRetryCount());
                                String uuid = cmd.getText();
                                MessageHistoryDaoHelper.getInstance(context).update(uuid, tuitaMgr.getImmanager().getOwner().getUid(), IMessageConst.STATUS_SENT_FAIL, -1);
                                MessageRecentDaoHelper.getInstance(context).update(uuid, tuitaMgr.getImmanager().getOwner().getUid(), IMessageConst.STATUS_SENT_FAIL);
                                MessageHistory msg = new MessageHistory();
                                msg.setUuid(uuid);
                                msg.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                                tuitaMgr.getImmanager().getRunningCmd().remove(tid);
                                BroadcastUtil.sendBroadcastToUI(context, BroadcastUtil.ACTION_MSG, new Gson().toJson(msg), false);
                                if (runFuture != null) {
                                    runFuture.cancel(true);
                                    mFutureMap.remove(runFuture);
                                }
                            } else {    //没有超过2次再次发送
                                android.util.Log.i("sendMsgLog","没有超过---->"+cmd.getRetryCount());
                                //记录重试的次数
                                cmd.setRetryCount(cmd.getRetryCount() + 1);
                                if (tuitaMgr.getImmanager().getTuitaIMState() == TuitaIMManager.TUITA_IM_STATE_CONNECT) {  //连接上
                                    //重新组包
                                    cmd.setPacket(TuitaPacket.createIMSendMessagePacket(cmd.getTid(), type, uidorgid, contentType, content, uuid, 1));
                                    tuitaMgr.getConnection().write(cmd.getPacket());
                                    android.util.Log.i("sendMsgLog","put之前---->"+cmd.getTid());
                                    tuitaMgr.getImmanager().getRunningCmd().put(cmd.getTid(), cmd);
                                    android.util.Log.i("sendMsgLog","增加次数---->"+cmd.getRetryCount());
                                }else {     //没有连接上
                                    android.util.Log.i("sendMsgLog","无网---->");
                                    //可用ip清除
                                    tuitaMgr.saveInfo(TuitaSDKManager.TUITA_HOST,"");
                                    //重连
                                    tuitaMgr.reConnectIM();
                                }

                            }
                            Logger.i("tuita", "MessageSendReceiver.onReceive", "send --------- timeout");
                        } else {
                            if (runFuture != null) {
                                runFuture.cancel(true);
                                mFutureMap.remove(runFuture);
                            }
                        }
                    }

                }, TuitaIMManager.TUITA_IM_COMMAND_DELAY_SECOND, TuitaIMManager.TUITA_IM_COMMAND_DELAY_SECOND, TimeUnit.SECONDS);
                mFutureMap.put(tid, future);
            } else {//action为其他action则记录日志以备差错
                Logger.i("tuita", "MessageSendReceiver.onReceive", "action error,this action are not we expected");
            }

        }
    }

    /**
     * 判断是否有网络
     *
     * @return
     */
    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable();
        } catch (Exception e) {
            android.util.Log.v("connectivity", e.toString());
        }
        return false;
    }

    /**
     * 获取卸载反馈url
     * @return
     */
    private String makeFeedBackUrl(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(ContextUtil.getSouyueHost(context) + "html/uninstall/index.html");
        String uid = null;
        String channel = null;
        try {
            uid = ContextUtil.getOwner(context).getUid()+"";
        } catch (Exception e) {

        }
        try {
            channel = URLEncoder.encode(ContextUtil.getMetaData(context).getString("UMENG_CHANNEL"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            if (TextUtils.isEmpty(uid)) {
                sb.append("?vc=" + TuitaSDKManager.getAppVersionName(tuitaMgr));
                if (DeviceUtil.getIMEI(context) != null)
                    sb.append("&imei=" + DeviceUtil.getIMEI(context));
            } else {
                sb.append("?userId=" + uid + "&vc=" + TuitaSDKManager.getAppVersionName(tuitaMgr));
                if (DeviceUtil.getIMEI(context) != null)
                    sb.append("&imei=" + DeviceUtil.getIMEI(context));
            }
            if (!TextUtils.isEmpty(channel)) {
                sb.append("&channel=" + channel);
            }
        } catch (Exception e) {

        }
        return sb.toString();
    }

}