package com.zhongsou.souyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.ListTypeRender;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.view.ZSVideoConst;

/**
 * wangqiang
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
    private static final  String TAG ="NetBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Slog.d("callback", "网络广播发生改变");
            PhotoUtils.retainWifi();
            ListTypeRender.resetNet();
            dealNetChange(context);
        }
    }

    public static void dealNetChange(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifiState != null && mobileState != null
                && NetworkInfo.State.CONNECTED != wifiState
                && NetworkInfo.State.CONNECTED == mobileState) {
            if(!sendBroadCastController())
            {
                Intent intent = new Intent();
                intent.setAction(ZSVideoConst.VIDEO_NET_ACTION);
                intent.putExtra(ZSVideoConst.VIDEO_NET_STATUS, ZSVideoConst.VIDEO_NET_STATUS_PHONE);
                context.sendOrderedBroadcast(intent, null);
            }
        } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {

        } else if (wifiState != null && mobileState != null
                && NetworkInfo.State.CONNECTED != wifiState
                && NetworkInfo.State.CONNECTED != mobileState) {
            SouYueToast.makeText(context, R.string.nonetworkerror, Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(ZSVideoConst.VIDEO_NET_ACTION);
            intent.putExtra(ZSVideoConst.VIDEO_NET_STATUS, ZSVideoConst.VIDEO_NET_STATUS_NO);
            context.sendOrderedBroadcast(intent, null);
        }
    }


    private static long lastSendTime;

    /**
     * 时间 控制
     *  秒内 不发出两次广播
     *
     * @return
     */
    public static boolean sendBroadCastController() {
        long time = System.currentTimeMillis();

        if ( time - lastSendTime < 1000*1) {
            return true;
        }
        lastSendTime = time;
        return false;
    }

}
