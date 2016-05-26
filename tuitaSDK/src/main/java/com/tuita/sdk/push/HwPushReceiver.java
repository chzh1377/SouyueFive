//package com.tuita.sdk.push;
//
//import android.app.NotificationManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import cn.jpush.android.api.JPushInterface;
//import com.huawei.android.pushagent.PushManager;
//import com.huawei.android.pushagent.PushReceiver;
//import com.huawei.android.pushagent.PushService;
//import com.huawei.android.pushagent.api.PushEventReceiver;
//import com.tuita.sdk.ContextUtil;
//import com.tuita.sdk.TuitaIMManager;
//import com.tuita.sdk.TuitaSDKManager;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Iterator;
//
///**
// * @author YanBin yanbin@zhongsou.com
// * @version V1.0
// * @Copyright (c) 2015 zhongsou
// * @Description HwPush receiver
// * @date 2016/01/25
// */
//public class HwPushReceiver extends PushEventReceiver {
//    private static final String TAG = "HwPush-Souyue";
//
//    @Override
//    public void onToken(Context context, String token, Bundle extras){
//        String belongId = extras.getString("belongId");
//        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
//        Log.d(TAG, content);
//    }
//
//
//    @Override
//    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {  //透传
//        try {
//            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
//            Log.d(TAG, content);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public void onEvent(Context context, PushReceiver.Event event, Bundle extras) {
//        if (PushReceiver.Event.NOTIFICATION_OPENED.equals(event)
//                || PushReceiver.Event.NOTIFICATION_CLICK_BTN.equals(event)) {   // 打开通知栏
//            int notifyId = extras.getInt(PushReceiver.BOUND_KEY.pushNotifyId, 0);
//            Log.d(TAG, " notifyId = "+ notifyId);
//            Log.d(TAG, " extras === "+ extras.toString());
//
//            if (0 != notifyId) {
//                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.cancel(notifyId);
//            }
//            String extraData = extras.getString(PushReceiver.BOUND_KEY.pushMsgKey);
////            String content = "收到通知附加消息： " + extraData;
////            Log.d(TAG, content);
//
//            if(extraData == null)return;
//
//            int pushType = -1;
//            JSONObject data = null;
//            try {
//                JSONArray jsonArray = new JSONArray(extraData);
//                JSONObject type = jsonArray.getJSONObject(0);
//                if(type != null){
//                    pushType = type.getInt("type");
//                    Log.d(TAG,"pushType = " + type.getString("type")); //正常
//                }
//
//                data = new JSONObject(jsonArray.getJSONObject(1).getString("data"));
//                Log.d(TAG, " data = " + data.toString());    //正常
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            TuitaSDKManager mgr = TuitaSDKManager.getInstance(context);
//            mgr.loadIM();
//            TuitaIMManager tmmgr = mgr.getImmanager();
//            tmmgr.setOwner(ContextUtil.getOwner(context));
//            try{
//                tmmgr.dealJPushMsg(data,  pushType);
//            }catch (Exception e){
//                Log.e(TAG, "HwPush onNotificationMessageClicked JumpTo Exception");
//            }
//        } else if (PushReceiver.Event.PLUGINRSP.equals(event)) {
//            final int TYPE_LBS = 1;
//            final int TYPE_TAG = 2;
//            int reportType = extras.getInt(PushReceiver.BOUND_KEY.PLUGINREPORTTYPE, -1);
//            boolean isSuccess = extras.getBoolean(PushReceiver.BOUND_KEY.PLUGINREPORTRESULT, false);
//            String message = "";
//            if (TYPE_LBS == reportType) {       // LBS
//                message = "LBS report result :";
//            } else if(TYPE_TAG == reportType) {     // TAG
//                message = "TAG report result :";
//            }
//            Log.d(TAG, message + isSuccess + "---tags : " +PushManager.getTags(context).toString());
//        }
//        super.onEvent(context, event, extras);
//    }
//}
