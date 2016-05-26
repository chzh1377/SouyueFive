package com.tuita.sdk.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tuita.sdk.DealAllMsgPushClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description JPush receiver
 * @date 2015/11/24
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush-Souyue";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
//        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {     //注册成功后，收到regID
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) { //收到透传消息
            Log.d(TAG, "[MyReceiver] EXTRA_MESSAGE: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            Log.d(TAG, "[MyReceiver] EXTRA_EXTRA: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

            //原有透传推送要闻逻辑，现在注释掉-20160324-v5.2.0
//            if (extras != null) {
//                try {
//                    JSONObject jsonObject = new JSONObject(extras);
//                    String strData = jsonObject.getString("data");
//                    JSONObject data = new JSONObject(strData);
//                    int pushType = jsonObject.getInt("type");
//
//                    TuitaSDKManager mgr = TuitaSDKManager.getInstance(context);
//                    mgr.loadIM();
//                    TuitaIMManager tmmgr = mgr.getImmanager();
//                    tmmgr.setOwner(ContextUtil.getOwner(context));
//
//                    tmmgr.dealJPushMsg(data,pushType);    //传入IM进行后续弹通知栏等处理
//                    Log.d(TAG, "==== receive data ====: " + data);      //正常
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) { //收到通知栏消息
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
//            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            Log.d(TAG, "JPush Nofification Received == " + bundle.toString());

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {  //点开通知栏

            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                String jsonObj= new JSONObject(extras).getString("jumpTo");
                Log.i("jsonObj",jsonObj);
                DealAllMsgPushClick.dealAllMsg(context, new JSONObject(jsonObj));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity
//            Intent i = new Intent(context, JPushNotificationShowActivity.class);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            context.startActivity(i);
            Log.d(TAG, "JPush Nofification Opened == " + extras);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
//            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
//                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
