package com.tuita.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.*;
import com.zhongsou.souyue.log.Logger;

import java.lang.reflect.Field;

/**
 * tuita 网络监听网络的广播接收
 * Created by zhangwenbin on 15/6/3.
 */
public class TuitaServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                || action.equals("android.intent.action.BOOT_COMPLETED")
                || action.equals("android.intent.action.MEDIA_MOUNTED")
                || action.equals("android.intent.action.USER_PRESENT")
                || action.equals("android.intent.action.ACTION_POWER_CONNECTED")
                || action.equals("android.intent.action.ACTION_POWER_DISCONNECTED")
                || action.equals("android.intent.action.PACKAGE_ADDED")
                || action.equals("android.intent.action.PACKAGE_REMOVED")
                ) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {

              //通过资源名称 key取值搜悦环境配置
                int resId = context.getResources().getIdentifier("souyue_interface_env",
                        "string", context.getPackageName());
                PushService.setTest(context,Integer.parseInt(context.getResources().getString(resId)));
                PushService.startService(context);
                Logger.i("tuita", "TuitaNetChangeReceiver.onReceive", "start PushService, open ");
            } else {
                PushService.stopService(context);
                Logger.i("tuita", "TuitaNetChangeReceiver.onReceive", "stop PushService, close ");
            }
        }
    }
}
