package com.zhongsou.souyue.im.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.tuita.sdk.Constants;
import com.tuita.sdk.PushService;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.NewFriendsActivity;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.view.ImUIHelpr;

import java.util.List;

public class ImOfflineReceiver extends BroadcastReceiver {
    private final static String CHAT_ACTIVITY="IMChatActivity";//定义聊天界面类名
    ActivityManager am;
    ComponentName cn;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(Constants.TYPE)) {
            case Constants.TYPE_LIST:
                ImserviceHelp.getInstance().cancelNotify(1);
                PushService.setMsgNum(MainApplication.getInstance());
                ImUIHelpr.startIm(context);
                break;
            case Constants.TYPE_CHAT:
                if(am == null){
                    am = (ActivityManager) context
                            .getSystemService(Context.ACTIVITY_SERVICE);
                }
                if(cn==null || "".equals(cn)){//获取组件名称
                    cn = am.getRunningTasks(1).get(0).topActivity;
                }
                long targetId = bundle.getLong(Constants.TARGET_ID);
//                Log.d("", "cls:" + cn.getClassName());

                //判断搜悦当前是否正在运行
                if(isAppOnForeground(context,am) && MainApplication.getInstance().isRunning()){//如果
                    //如果当前栈顶是聊天界面并且targetID等于当前聊天界面mTargetId则不重复打开此界面
                    if(cn.getClassName().contains(CHAT_ACTIVITY) && targetId == IMChatActivity.mTargetId){//解决从详情页返回聊天的情况
                    }else if(cn.getClassName().contains(CHAT_ACTIVITY)){//当前界面是聊天界面 但是不是通知栏点击应进的聊天界面的情况
                        PushService.setMsgNum(MainApplication.getInstance());
                        IMChatActivity.invoke(context, bundle.getInt(Constants.TARGET_TYPE), targetId);
                        ImserviceHelp.getInstance().cancelNotify(bundle.getInt(Constants.NOTIFY_ID));
                    }else{//当前界面不是聊天界面
                        PushService.setMsgNum(MainApplication.getInstance());
                        IMChatActivity.invoke(context, bundle.getInt(Constants.TARGET_TYPE), targetId);
                        ImserviceHelp.getInstance().cancelNotify(bundle.getInt(Constants.NOTIFY_ID));
                        System.out.println("else 11");
                    }
                }else{//当前程序没在运行则调用下面方法  先走MainActivity
                    System.out.println("else 12" + context.getPackageName() + "   " + cn.getPackageName());
                    ImUIHelpr.startIMChat(context, bundle.getInt(Constants.TARGET_TYPE), targetId);
                }

                break;
            case Constants.TYPE_SERVICE_LIST:
                ImserviceHelp.getInstance().cancelNotify(bundle.getInt(Constants.NOTIFY_ID));
                PushService.setMsgNum(MainApplication.getInstance());
                ImUIHelpr.startIm(context);
                break;
            case Constants.TYPE_NEW_FRIEND:     //跳转到新朋友
                ImserviceHelp.getInstance().cancelNotify(bundle.getInt(Constants.NOTIFY_ID));
                PushService.setMsgNum(MainApplication.getInstance());
                NewFriendsActivity.invokeNewTask(context);
                break;
            default:
                ImserviceHelp.getInstance().cancelNotify(bundle.getInt(Constants.NOTIFY_ID));
                PushService.setMsgNum(MainApplication.getInstance());
                ImUIHelpr.startIm(context);
                break;
        }
    }

    /**
     * 判断前后台运行
     *
     * @return
     */
    private static boolean isAppOnForeground(Context context,ActivityManager am) {
        String packageName = context.getPackageName();
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(Integer.MAX_VALUE);
        if (taskInfos == null)
            return false;
        for (int i=0;i<taskInfos.size();i++) {
//            System.out.println("else 1allTs i = "+ i + taskInfos.get(i).topActivity.getPackageName()+ " aa " +taskInfos.get(i).topActivity.getClassName());
        }
        for (int i=0;i<taskInfos.size();i++) {
//            System.out.println("else 123 i = "+ i + taskInfos.get(i).topActivity.getPackageName()+ " aa " +taskInfos.get(i).topActivity.getClassName());
            if(i==0){
                if(packageName.equals(taskInfos.get(i).topActivity.getPackageName())){
                    return true;
                }
            }else{
                if (packageName.equals(taskInfos.get(i).topActivity.getPackageName())){//如果程序已经运行在后台，则把程序调回前台  此方法支持最低的api 为11
                    am.moveTaskToFront(taskInfos.get(i).id,ActivityManager.MOVE_TASK_WITH_HOME);
                    return true;
                }
            }
        }
        return false;
    }
}
