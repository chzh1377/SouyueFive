package com.tuita.sdk.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tuita.sdk.ContextUtil;
import com.tuita.sdk.DealAllMsgPushClick;
import com.tuita.sdk.PushService;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.TuitaSDKManager;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YanBin yanbin@.com
 * @version V
 * @Copyright (c) 2015
 * @Description mipush receiver
 * @date 2015/11/26
 */
public class MiPushReceiver extends PushMessageReceiver {
    public static final String TAG = "MiPushReceiver";
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mStartTime;
    private String mEndTime;
    private String notice_show_click_mipush = "notice_show_click_mipush";  //Umeng

    /**
     * 接收透传消息（不显示通知）
     * @param context 上下文
     * @param message 消息内容
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();    //得到内容
        if(!TextUtils.isEmpty(message.getTopic())) {    //标签不为空
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {    //别名不为空
            mAlias=message.getAlias();
        } else if(!TextUtils.isEmpty(message.getCategory())){    //类型不为空

        } else if(!TextUtils.isEmpty(message.getDescription())){    //描述不为空

        }else if(!TextUtils.isEmpty(message.getMessageId())){

        } else if(!TextUtils.isEmpty(message.getContent())){

        }
//        Toast.makeText(context, "Through : " + message.toString(), Toast.LENGTH_LONG).show();
//        Log.d(TAG, "Through : " + message.toString());
    }

    /**
     * 通知被点击
     * @param context 上下文
     * @param message 消息内容
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {

        MobclickAgent.onEvent(context, notice_show_click_mipush);  //Umeng
        mMessage = message.getContent();
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        }
        PushService.startService(context);  //启动IM推送服务

        //得到扩展信息
        Map<String, String> extra = new HashMap<String, String>();
        if(message.getExtra() != null){
            extra = message.getExtra();
        }
        String type = extra.get("type");
        int pushType = -1;
        if(type != null){
             pushType = Integer.parseInt(type);
        }
        String content = message.getContent();
        JSONObject data = null;
        try {
            data = new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TuitaSDKManager mgr = TuitaSDKManager.getInstance(context);
        mgr.loadIM();
        TuitaIMManager tmmgr = mgr.getImmanager();
        tmmgr.setOwner(ContextUtil.getOwner(context));
        message.getContent();
        try{
//            Log.i("xxx","小米通知被点击");
            DealAllMsgPushClick.dealAllMsg(context,data);
        }catch (Exception e){
            Log.e(TAG, "MiPush onNotificationMessageClicked JumpTo Exception");
        }

//        Log.d(TAG, "Clicked : " + message.toString());
//        Log.d(TAG, "--------=======data : " + data);      //正常
//        Log.d(TAG, "--------=======pushType : " + pushType);      //正常
    }

    /**
     * 通知到达
     * @param context 上下文
     * @param message 消息内容
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        }
        Map<String,String> extra = message.getExtra();

//        Toast.makeText(context, "Arrived : " + message.toString(), Toast.LENGTH_LONG).show();
//        Log.d(TAG, "Arrived : " + message.toString());
    }

    /**
     * 客户端向服务端发送命令返回结果接收
     * @param context 上下文
     * @param message 返回信息
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {    //注册
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {    //设置别名
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {  //取消别名
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {  //设置Topic（标签）
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {//取消设置Topic（标签）
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }

    /**
     * 接收注册结果
     * @param context 上下文
     * @param message 返回结果
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
//        Toast.makeText(context, "mRegId : " + mRegId, Toast.LENGTH_LONG).show();
        Log.d(TAG, "mRegId : " + mRegId);

    }
}
