package com.zhongsou.souyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.tuita.sdk.PushService;
import com.zhongsou.souyue.utils.ConstantsUtils;

/**
 * User: zhangliang01@zhongsou.com
 * Date: 12/4/13
 * Time: 2:44 PM
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        SYSharedPreferences sysp = SYSharedPreferences.getInstance();
//        if (sysp != null && sysp.getBoolean(SYSharedPreferences.KEY_PUSHSWITCH, true)) {
    		Log.i("", "system broadcast be get");
            PushService.setTest(context, ConstantsUtils.PUSH_TEST);
            PushService.startService(context);
//        }
    }
}
