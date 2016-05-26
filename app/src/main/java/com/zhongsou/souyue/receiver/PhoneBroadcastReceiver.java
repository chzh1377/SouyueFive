package com.zhongsou.souyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhongsou.souyue.view.ZSVideoConst;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/4/13.
 */
public class PhoneBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            //去电（拨出）
        }else{ //来电
            ZSVideoConst.sendStopBroadcast(context);
        }
    }

}
