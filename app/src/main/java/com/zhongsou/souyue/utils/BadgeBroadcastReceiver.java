package com.zhongsou.souyue.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * 桌面红点的广播，当数字count > 0时显示数字，count ＝ 0时，重置，
 * @author qubian
 * @data 2015年10月14日
 * @email naibbian@163.com
 */
public class BadgeBroadcastReceiver extends BroadcastReceiver {

    public static final String Action="com.zhongsou.souyue.utils.BadgeBroadcastReceiver";
    public static final String IMSMG="com.tuita.sdk.action.souyue.im";
    public static final String BadgeCount="BadgeCount";
    private static final Random RANDOM = new Random();
    private int count;


    @Override
    public void onReceive(Context context, Intent intent) {
        count = intent.getIntExtra(BadgeCount, 0);
        if (count <=0)
        {
            resetBadgeCount(context);
        }else
        {

            setBadgeCount(context, count);
        }
    }



    /**
     * 设置红点数
     * @param context
     * @param count
     */
    private static void setBadgeCount(Context context, int count) {
        if (count <= 0) {
            count = 0;
        } else {
            count = Math.max(0, Math.min(count, 99));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(context, count);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            sendToSony(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
            sendToSamsumg(context, count);
        } else {
            Log.i("","Not support");
        }
    }


    /**
     * 小米
     * @param count
     */
    private static void sendToXiaoMi(Context context, int count) {
        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, String.valueOf(count == 0 ? "" : count));  // 设置信息数-->这种发送必须是miui 6才行
        } catch (Exception e) {
//            e.printStackTrace();
            // miui 6之前的版本
            Intent localIntent = new Intent(
                    "android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra(
                    "android.intent.extra.update_application_component_name",
                    context.getPackageName() + "/" + getLauncherClassName(context));
            localIntent.putExtra(
                    "android.intent.extra.update_application_message_text", String.valueOf(count == 0 ? "" : count));
            context.sendBroadcast(localIntent);
        }
    }


//    /**
//     * 此方式不行
//     * @param context
//     * @param number
//     */
//    private static void sendToXiaoMi(Context context,int number) {
//        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = null;
//        boolean isMiUIV6 = true;
//        try {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//            builder.setContentTitle("您有" + number + "未读消息");
//            builder.setTicker("您有" + number + "未读消息");
//            builder.setAutoCancel(true);
//            builder.setSmallIcon(R.drawable.logo);
//            builder.setDefaults(Notification.DEFAULT_LIGHTS);
//            Intent intent = new Intent(context,MainActivity.class);
//            intent.putExtra(MainActivity.TAB_TAG_EXTRA,"1");
//            builder.setContentIntent(PendingIntent.getActivity(context, RANDOM.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
//            notification = builder.getNotification();
//            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
//            Object miuiNotification = miuiNotificationClass.newInstance();
//            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
//            field.setAccessible(true);
//            field.set(miuiNotification, number);// 设置信息数
//            field = notification.getClass().getField("extraNotification");
//            field.setAccessible(true);
//            field.set(notification, miuiNotification);
//        }catch (Exception e) {
//            e.printStackTrace();
//            //miui 6之前的版本
//            isMiUIV6 = false;
//            Intent localIntent = new Intent(
//                    "android.intent.action.APPLICATION_MESSAGE_UPDATE");
//            localIntent.putExtra(
//                    "android.intent.extra.update_application_component_name",
//                    context.getPackageName() + "/" + getLauncherClassName(context));
//            localIntent.putExtra(
//                    "android.intent.extra.update_application_message_text", String.valueOf(number == 0 ? "" : number));
//            context.sendBroadcast(localIntent);
//        }
//        finally
//        {
//            if(notification!=null && isMiUIV6 )
//            {
//                //miui6以上版本需要使用通知发送
//                nm.notify(101010, notification);
//            }
//        }
//
//    }



    /**
     * 索尼
     *
     * @param count
     */
    private static void sendToSony(Context context, int count){
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }

        boolean isShow = true;
        if (count == 0) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);//是否显示
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",launcherClassName );//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
        context.sendBroadcast(localIntent);
    }


    /**
     * 三星
     * @param count
     */
    private static void sendToSamsumg(Context context, int count){
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }


    /**
     * 重置、清除Badge未读显示数
     * @param context
     */
    private static void resetBadgeCount(Context context) {
        setBadgeCount(context, 0);
    }



    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }





}
