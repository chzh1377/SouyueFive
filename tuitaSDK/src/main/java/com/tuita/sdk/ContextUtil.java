package com.tuita.sdk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.log.Logger;
import org.json.JSONObject;

import java.util.Random;

/**
 * @author wanglong@zhongsou.com
 */
public class ContextUtil {
    private static final String LOGTAG = ContextUtil.class.getSimpleName();
    //	private static String getAppSecret(Context context) {
    //		String appSecret = getMetaData(context).getString("appSecret");
    //		return appSecret;
    //	}
    private static final Random RANDOM = new Random();
    private static NotificationManager notificationManager;
    private static ConnectivityManager connMan;
    private static SharedPreferences timeSp;
    private static String notice_show = "notice_show";  //Umeng

    public static String getAppId(Context context) {
        if (isSyApp(context)) {
//            if (isOnline(context))
            return "souyue";
//            else
//                return "souyue_1_0";
        } else {
            return getMetaData(context).getString("appId");
        }
    }

    private static ApplicationInfo getAppInfo(Context context) {
        return context.getApplicationInfo();
    }

    protected static String getAppKey(Context context) {
        if (isSyApp(context)) {
            if (isOnline(context))
                return "^frnUjKYAj%cY1-9";
            else
                return "souyue";
        } else {
            return getMetaData(context).getString("appKey");
        }
    }

    private static String getAppLabel(Context context) {
        String label = getAppInfo(context).loadLabel(context.getPackageManager()).toString();
        // Log.d(LOGTAG, "getAppLabel," + label);
        return label;
    }

    private static String getSubStringTitle(String desc) {
        if (desc != null && !"".equals(desc) && desc.length() > 15) {
            return desc.substring(0, 15);
        } else {
            return desc;
        }
    }

    public static String getBroadcastAction(Context context) {
        String action = "com.tuita.sdk.action." + getAppId(context);
        Log.i(LOGTAG, "getBroadcastAction=" + action);
        return action;
    }

    public static Bundle getMetaData(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getPushIconId(Context context) {
        int id = context.getApplicationInfo().icon;//;context.getResources().getIdentifier("push", "drawable", context.getPackageName());
        if (id <= 0) {
            id = android.R.drawable.sym_def_app_icon;
        }
        Log.i(LOGTAG, "getPushIconId," + id);
        return id;
    }

    protected static boolean isNetworkAvailable(Context context) {
        if (connMan == null) {
            connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo info = connMan.getActiveNetworkInfo();
        boolean isConn = info != null && info.isAvailable();
        if (info != null) {
            Logger.i("NetInfo", "info.isAvai-------->" + info.isAvailable(), "info.isAvai-------->" + info.isAvailable());
        } else {
            Logger.i("NetInfo", "info.isAvai-------->info == null", "info.isAvai-------->info == null");
        }
        return isConn;
    }

    //	private static void notify2(Context context, int type, String message, String data) {
    //		Notification n = new Notification();
    //		n.flags |= Notification.FLAG_SHOW_LIGHTS;
    //		n.flags |= Notification.FLAG_AUTO_CANCEL;
    //		n.defaults = Notification.DEFAULT_ALL;
    //		n.icon = getPushIconId(context);
    //		n.when = System.currentTimeMillis();
    //		Intent intent = new Intent("com.tuita.sdk.acton." + getAppId(context));
    //		intent.putExtra(Constants.TYPE, type);
    //		intent.putExtra(Constants.DATA, data);
    //		n.setLatestEventInfo(context, getAppLabel(context), message, PendingIntent.getBroadcast(context, 0, intent, 0));
    //		if (notificationManager == null) {
    //			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    //		}
    //		notificationManager.notify(0, n);
    //	}
    protected static void notify(Context context, int type, String data) {
        notify(context, type, -1, null, null, data);
    }

    protected static void notify(Context context, int type, int channel, String title, String description, String data) {
        MobclickAgent.onEvent(context, notice_show);     //Umeng
        Log.i(LOGTAG, "notify,type=" + type + ",channel=" + channel + ",title=" + title + ",description=" + description + ",data=" + data);
        Intent intent = new Intent(getBroadcastAction(context));
        intent.putExtra(Constants.TYPE, type);
        intent.putExtra(Constants.DATA, data);
        if (type == Constants.TYPE_GET_CLIENTID) {
            context.sendBroadcast(intent);
        } else if (PushService.canPushMessage(context)) {
            Intent log = new Intent("com.tuita.sdk.log." + getAppId(context));
            log.putExtra(Constants.TITLE, description);
            log.putExtra(Constants.DATA, data);
            context.sendBroadcast(log);
            //for demo begin
            //			Intent i = new Intent("com.tuita.sdk.notify." + getAppId(context));
            //			i.putExtra("title", title);
            //			i.putExtra("description", description);
            //			i.putExtra("channel", channel);
            //			i.putExtra("body", data);
            //			context.sendBroadcast(i);
            //for demo end
//		    boolean isIm = isIMMsg(data);
//		    if (isIm){
//		        PushService.msgNum ++;
//		        description = String.format(description, PushService.msgNum);
//		    }
            timeSp = context.getSharedPreferences(BroadcastUtil.TIME_SHAREDPREFERENCE, context.MODE_PRIVATE);
            long timeOld = timeSp.getLong("timeOld", 0);
            long timeNew = System.currentTimeMillis();
            BroadcastUtil.saveTime(timeNew - timeOld > BroadcastUtil.TIME_RESET ? 0 : timeNew);
            showNotification(context, channel, title, description, data, intent, timeNew - timeOld < BroadcastUtil.TIME_GAP || timeOld == 0 ? false : true);
        }
    }

    private static void showNotification(Context context, int channel, String title, String description, String data, Intent intent, boolean soundFlag) {
        Notification n = new Notification(getPushIconId(context), description, System.currentTimeMillis());
        n.defaults = 0;
        if (PushService.getEnableSound(context) && soundFlag) {
            n.defaults |= Notification.DEFAULT_SOUND;//1
        }
        if (PushService.getEnableVibrate(context) && soundFlag) {
            n.defaults |= Notification.DEFAULT_VIBRATE;//2
        }
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        if (isSyApp(context)) {
            n.setLatestEventInfo(context, title == null ? getSubStringTitle(description) : title, description, PendingIntent.getBroadcast(context, RANDOM.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            n.setLatestEventInfo(context, getAppLabel(context), title, PendingIntent.getBroadcast(context, RANDOM.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
//			n.setLatestEventInfo(context, getAppLabel(context), description, PendingIntent.getBroadcast(context, RANDOM.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (isIMMsg(data)) {
//        			if (!PushService.getImUserIdentity(context))
            return;
        }
        notificationManager.notify(channel >= 1 && channel <= 100 ? channel : RANDOM.nextInt(Integer.MAX_VALUE - 100) + 101, n);
    }
    //	private static void notify3(Context context, int type, String message, String data) {
    //		Log.i(LOGTAG, "notify,type=" + type + ",message=" + message + ",data=" + data);
    //		String title = getAppLabel(context);
    //		Notification notification = new Notification();
    //		notification.icon = getPushIconId(context);
    //		notification.defaults = Notification.DEFAULT_ALL;
    //		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    //		notification.when = System.currentTimeMillis();
    //		notification.tickerText = message;
    //		Intent intent = new Intent("com.tuita.sdk.acton." + getAppId(context));
    //		intent.putExtra(Constants.TYPE, type);
    //		intent.putExtra(Constants.DATA, data);
    //		notification.setLatestEventInfo(context, title, message, PendingIntent.getBroadcast(context, 0, intent, 0));
    //		if (notificationManager == null) {
    //			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    //		}
    //		notificationManager.notify(random.nextInt(), notification);
    //	}

    private static boolean isIMMsg(String data) {
        try {
            JSONObject json = new JSONObject(data);
            return "im".equals(json.getString("t"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通知搜悦下线
     *
     * @param
     */
    protected static void notify2Souyue(Context context, String token, String msg) {
        Intent i = new Intent(BroadcastUtil.ACTION_SOUYUE_KICKEDOUT);
        i.putExtra("token", token);
        i.putExtra("msg", msg);
        context.sendBroadcast(i);
    }

    /**
     * 获取用户相关
     *
     * @param context
     * @return
     */
    public static TuitaIMManager.Owner getOwner(Context context) {
        UserDBHelper userTableDBHelper = new UserDBHelper(context);
        userTableDBHelper.openReadable();
        SYUserBean user = userTableDBHelper.select(TuitaIMManager.USER_ADMIN);
        if (user == null || "".equals(user.token()))
            user = userTableDBHelper.select(TuitaIMManager.USER_GUEST);
        userTableDBHelper.close();

        TuitaIMManager.Owner owner = new TuitaIMManager.Owner();
        if (user != null) {
            owner.setUid(user.userId());
            owner.setNick(user.name());
            owner.setAvatar(user.image());
            owner.setPass(user.token());
        }
        return owner;
    }

    //add by trade

    /**
     * 当前app是否是搜悦. <br/>
     *
     * @param context
     * @return
     * @author liudl
     * @date 2014-8-18 上午10:40:38
     */
    public static boolean isSyApp(Context context) {
        if ("com.zhongsou.souyue".equals(context.getPackageName())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取搜悦环境配置，判断是否上线环境
     *
     * @return
     */
    public static boolean isOnline(Context context) {
        int souyue_interface_env = Integer.parseInt(context.getResources().getString(context.getResources().getIdentifier("souyue_interface_env", "string", context.getPackageName())));
        // 2 线上 1 预上线 0 测试
        if (souyue_interface_env == 0 || souyue_interface_env == 3) {       //搜悦的224和239对应im  测试环境
            return false;
        } else if (souyue_interface_env == 1 || souyue_interface_env == 4) {       //搜悦的225和另一个环境对应im 预上线
            return true;
        } else {    //正式环境
            return true;
        }
    }

    /**
     * 获取搜悦环境
     * @param context
     * @return
     */
    public static String getSouyueHost(Context context) {
        int souyueService = Integer.parseInt(context.getResources().getString(context.getResources().getIdentifier("souyue_interface_env", "string", context.getPackageName())));
        // 搜悦环境
        switch (souyueService) {
            case 0:
                return "http://103.29.134.224/d3api2/";
            case 1:
                return "http://103.29.134.225/d3api2/";
            case 4:
                return "http://111.206.69.38:8111/d3api2/";
            case 2:
                return "http://api2.souyue.mobi/d3api2/";

            case 3:
                return "http://61.135.210.239:8888/d3api2/";
            default:
                return "http://api2.souyue.mobi/d3api2/";
        }
    }
}
