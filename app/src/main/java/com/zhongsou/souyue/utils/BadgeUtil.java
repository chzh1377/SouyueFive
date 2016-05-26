package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.Intent;

/**
 * 桌面红点
 * 适配机型：
 * 小米、三星、索尼
 *
 * @author qubian
 * @data 2015年10月14日
 * @email naibbian@163.com
 */
public class BadgeUtil {


    /**
     *  发送静态广播
     * @param context
     * @param count  count＝ 0 为重置 ，count > 0 为显示数据
     */
    public static void sendBadgeBraodcast(Context context,int count)
    {
        Intent intent = new Intent(BadgeBroadcastReceiver.Action);
        intent.putExtra(BadgeBroadcastReceiver.BadgeCount, count);
        context.sendBroadcast(intent);
    }


}