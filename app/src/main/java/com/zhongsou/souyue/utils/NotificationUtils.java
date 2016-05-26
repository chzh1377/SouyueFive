package com.zhongsou.souyue.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import com.tuita.sdk.PushService;

import java.util.Random;

/**
 * 通知栏工具类
 * @author qubian
 * @data 2015年10月26日
 * @email naibbian@163.com
 */
public class NotificationUtils {

    private static final int NotificationNumber = 1;
    private static final int RequestCode = 0;
    private static NotificationManager mManager;
    private static NotificationCompat.Builder mBuilder;
    private static final Random RANDOM = new Random();
    private static boolean isShowTime= false;//显示 时间
    private static boolean isShowNum= false;// 显示右侧 的 数量
    private static AudioManager audioManager;
    private static int current;
    private static int InboxStyleNum=3;
    /**
     * 普通通知栏
     * @param context
     * @param soundFlag
     * @param jumpIntent
     * @param showContent
     * @param showName
     * @return
     */
    public static Notification getNotification(Context context, boolean soundFlag, Intent jumpIntent, String showContent, String showName)
    {
        Notification n = new Notification(getPushIconId(context), showName + ":" + showContent, System.currentTimeMillis());
        initNotification(n,context,soundFlag);
        n.setLatestEventInfo(context, showName, showContent, PendingIntent.getBroadcast(context, RANDOM.nextInt(), jumpIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        return n;
    }
    /**
     * 段落样式
     * @param context
     * @param soundFlag
     * @param jumpIntent
     * @param showContent
     * @param showName
     * @param lins
     * @return
     */
    public static Notification getInboxStyleNotification(Context context, boolean soundFlag, Intent jumpIntent, String showContent, String showName,String[] lins,String summary) {
        String triker=showName + ":" + showContent;
        PendingIntent pendingIntent =PendingIntent.getBroadcast(context, RANDOM.nextInt(), jumpIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n= getInboxStyleNotification(context, showName, showContent, triker, showName, summary, lins, pendingIntent);
        initNotification(n,context,soundFlag);
        return n;
    }



    /**
     * 大文本样式
     * @param context
     * @param soundFlag
     * @param jumpIntent
     * @param showContent
     * @param showName
     * @param detailContent
     * @param summary
     * @return
     */
    public static Notification getBigTextStyleNotification(Context context, boolean soundFlag, Intent jumpIntent, String showContent, String showName,String detailContent,String summary) {
        String triker=showName + ":" + showContent;
        PendingIntent pendingIntent =PendingIntent.getBroadcast(context, RANDOM.nextInt(), jumpIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n= getBigTextStyleNotification(context, showName, showContent, triker, showName, summary, detailContent, pendingIntent);
        initNotification(n,context,soundFlag);
        return n;
    }

    /**
     * 大图样式
     * @param context
     * @param soundFlag
     * @param jumpIntent
     * @param showContent
     * @param showName
     * @param bitmap
     * @return
     */
    public static Notification getBigPictureStyleNotification(Context context, boolean soundFlag, Intent jumpIntent, String showContent, String showName,Bitmap bitmap) {
        String triker=showName + ":" + showContent;
        PendingIntent pendingIntent =PendingIntent.getBroadcast(context, RANDOM.nextInt(), jumpIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n= getBigPictureStyleNotification(context, showName, showContent, triker, showName,"", bitmap, pendingIntent);
        initNotification(n, context, soundFlag);
        return n;
    }



    /**
     * 获取Builder
     *
     * @param context
     * @param showTitleName
     *@param showContent
     * @param ticker
     * @param pushIconId
     * @param style
     * @param pendingIntent @return
     */
    private static NotificationCompat.Builder getBuilder(Context context, String showTitleName, String showContent, String ticker, int pushIconId, NotificationCompat.Style style, PendingIntent pendingIntent) {

        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE);
        if(isShowTime)
        {
            mBuilder.setWhen(System.currentTimeMillis());
        }
        if(isShowNum)
        {
            mBuilder.setNumber(NotificationNumber);
        }
        mBuilder .setContentTitle(showTitleName)
                .setContentText(dealStringTitle(showContent))
                .setPriority(Notification.PRIORITY_MAX)// 优先级  最高
                .setTicker(ticker)
                .setSmallIcon(pushIconId)
                .setStyle(style)
                .setContentIntent(pendingIntent);
        return mBuilder;
    }

    /**
     * 获取NotificationManager
     *
     * @param context
     * @return
     */
    private static NotificationManager getManager(Context context) {

        if (mManager == null) {
            synchronized (NotificationUtils.class) {
                if (mManager == null) {
                    mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                }
            }
        }
        return mManager;
    }

    /**
     * 设置通知的声音
     * @param n
     * @param context
     * @param soundFlag
     */
    private  static void initNotification(Notification n,Context context, boolean soundFlag)
    {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);//获取系统声音服务，主要用来获得当前系统声音的设置
        }
        if(audioManager!=null){
            current = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        }
        n.defaults = Notification.DEFAULT_LIGHTS;
        if (PushService.getEnableSound(context) && soundFlag && current != 0) {
            n.defaults |= Notification.DEFAULT_SOUND;//1
        }
        if (PushService.getEnableVibrate(context) && soundFlag) {
            n.defaults |= Notification.DEFAULT_VIBRATE;//2
        }

        n.flags |= Notification.FLAG_AUTO_CANCEL;
    }


    /**
     * 段落样式的大视图通知栏
     * 细节区域只有256dp高度的内容
     * 需要最高的优先级 ，否则二级滑动滑不动
     *
     * @param context
     * @param showTitleName 未展开时的 标题；
     * @param showContent 未展开时的 内容
     * @param ticker 通知来到时，状态栏的提示
     * @param bigContentTitle 展开时的标题
     * @param summaryText 展开时的小标题（）
     * @param lins 展开时的每行内容（）
     * @param pendingIntent intent
     */
    private static Notification getInboxStyleNotification(Context context, String showTitleName, String showContent, String ticker,
                                                          String bigContentTitle, String summaryText, String[] lins,
                                                          PendingIntent pendingIntent) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(bigContentTitle);
        style.setSummaryText(dealStringTitle(summaryText));
        int lineLength = lins.length >InboxStyleNum?InboxStyleNum:lins.length;
        for (int i = 0; i < lineLength; i++) {
            style.addLine(lins[i]);
        }
        mBuilder = getBuilder(context,showTitleName,showContent,ticker,getPushIconId(context),style,pendingIntent);
        Notification mNotification = mBuilder.build();
        mNotification.icon = getPushIconId(context);//默认的显示图标
        return mNotification;

    }

    /**
     * 大图样式的大视图通知栏
     * 细节区域只有256dp高度的内容
     * 需要最高的优先级 ，否则二级滑动滑不动
     *
     * @param context
     * @param showTitleName 未展开时的 标题；
     * @param showContent 未展开时的 内容
     * @param ticker 通知来到时，状态栏的提示
     * @param bigContentTitle 展开时的标题
     * @param summary 展开时的副标题(没用)
     * @param bitmap 展开的大图
     * @param pendingIntent intent
     */
    private static Notification getBigPictureStyleNotification(Context context, String showTitleName, String showContent, String ticker,
                                                               String bigContentTitle, String summary,Bitmap bitmap,PendingIntent pendingIntent) {
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(bigContentTitle);
        if(!isEmpty(summary))
        {
            style.setSummaryText(summary);
        }
        style.bigPicture(bitmap);
        mBuilder = getBuilder(context, showTitleName, showContent, ticker, getPushIconId(context), style, pendingIntent);
        Notification mNotification = mBuilder.build();
        mNotification.icon = getPushIconId(context);
        return  mNotification;
    }

    /**
     * 大文本样式的大视图通知栏
     * 细节区域只有256dp高度的内容
     * 需要最高的优先级 ，否则二级滑动滑不动
     *
     * @param context
     * @param showTitleName 未展开时的 标题；
     * @param showContent 未展开时的 内容
     * @param ticker 通知来到时，状态栏的提示
     * @param bigContentTitle 展开时的标题
     * @param summary 展开时的副标题（最底下）
     * @param bigtext 展开的文本
     * @param pendingIntent intent
     */
    private static Notification getBigTextStyleNotification(Context context, String showTitleName, String showContent, String ticker,
                                                            String bigContentTitle,String summary, String bigtext,PendingIntent pendingIntent
    ) {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(bigContentTitle);
        if(!isEmpty(summary))
        {
            style.setSummaryText(summary);
        }
        style.bigText(bigtext);
        mBuilder = getBuilder(context, showTitleName, showContent, ticker, getPushIconId(context), style, pendingIntent);
        Notification mNotification = mBuilder.build();
        mNotification.icon = getPushIconId(context);
        return  mNotification;
    }


    private static int dealWithId(int channel) {
        return channel >= 1 && channel <= 100 ? channel : RANDOM.nextInt(Integer.MAX_VALUE - 100) + 101;
    }

    private static int iconId = 0;

    private void setIconId(int iconId)
    {
        this.iconId=iconId;
    }
    /**
     * 默认图标
     * @param context
     * @return
     */
    private static int getPushIconId(Context context) {
        if(iconId==0)
        {
            iconId = context.getApplicationInfo().icon;
        }
        if (iconId < 0) {
            iconId = android.R.drawable.sym_def_app_icon;
        }
        return iconId;
    }

    private  static String dealStringTitle(String desc) {
        if (desc != null && !"".equals(desc) && desc.length() > 15) {
            return desc.substring(0, 15);
        } else {
            return desc;
        }
    }
    public static boolean isEmpty(Object str) {
        return str == null || str.toString().length() == 0;
    }

}
