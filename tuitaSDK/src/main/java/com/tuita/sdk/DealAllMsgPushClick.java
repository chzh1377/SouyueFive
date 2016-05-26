package com.tuita.sdk;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

/**
 * Created by zhou on 2016/2/29 0029.
 * 处理通知栏跳转逻辑
 */
public class DealAllMsgPushClick {
    public static final String JUMPACTION = "com.zhongsou.souyue.pushReceiver";//可以发送到这个广播接收者内的action

    public static final int INVOKE_TYPE_IM_CHAT = 90;//跳转到会话页面
    public static final int INVOKE_TYPE_IM_NEW_FRIEND = 91;//跳转到IM新朋友界面

    //处理所有通知栏点击后的跳转 逻辑 5.2.0新增
    //http://192.168.31.188:8082/Home/Article/detail/id/22.html
    public static void dealAllMsg(Context context, JSONObject jsonData) throws Exception {
        int type = jsonData.getInt("type");
        switch (type) {
            case INVOKE_TYPE_IM_CHAT://
                context.sendBroadcast(BroadcastUtil.getGotoImNotifyIntent(context,
                                jsonData.getLong("chatId"),
                                jsonData.getInt("chatType"))
                );//由 ImOfflineReceiver  接收
                break;
            case INVOKE_TYPE_IM_NEW_FRIEND:
                context.sendBroadcast(BroadcastUtil.getGotoImNewFriendIntent(context));//由 ImOfflineReceiver  接收
                break;
            default://IM不识别的话则以广播的形式发送到搜悦 NotificationMsgJumpReceiver
                Intent intent = new Intent(JUMPACTION);
                intent.putExtra("data", jsonData.toString());
                context.sendBroadcast(intent);
        }
    }
}
