package com.zhongsou.souyue.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
//import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.activity.FirstInActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.share.NotificationDialog;
import com.zhongsou.souyue.share.SpecialRecommendDialog;
import com.zhongsou.souyue.ui.highlight.Highlight;
import com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.UserInfoUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.TaskCenterToast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * k
 * Created by yinguanping on 14-11-11.
 */
public class TaskCenterReceiver extends BroadcastReceiver {

    public static final int             DIALOG_TIME_STEP = 60 * 5 * 1000;// 两次相同弹窗中间间隔的时间
    private             Timer           mTimer           = null;
    private             closeTimerTask  clTask           = null;
    private static      TaskCenterToast taskCenterToast  = null;
    private             String          jsonStr          = "";
    private             TaskCenterInfo  taskCenterInfo   = null;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                if (taskCenterToast != null) {
                    taskCenterToast.dissPopWindow();
                    taskCenterToast.setOpenPop(false);
                }
            } else if (msg.what == 1) {// 踢人
                Activity activity = null;
                activity = UserInfoUtils.getActivity();
                if (activity == null) {
                    return false;
                }

                if (taskCenterToast != null) {
                    if (taskCenterToast.isOpenPop()) {// 如果当前有正在打开，则关闭当前，显示最新
                        taskCenterToast.dissPopWindow();
                    }
                }
                taskCenterToast = new TaskCenterToast(activity, taskCenterInfo);

                if (!activity.isFinishing()) {
                    taskCenterToast.showPopUpWindow();
                    // 事件处理,更改pop状态
                    taskCenterToast.setOpenPop(true);
                } else {
                    return false;
                }
            } else if (msg.what == 2) {// 任务中心
                Activity activity = null;
                activity = UserInfoUtils.getActivity();
                if (activity == null) {
                    return false;
                }

                if (taskCenterToast != null) {
                    if (taskCenterToast.isOpenPop()) {// 如果当前有正在打开，则关闭当前，显示最新
                        taskCenterToast.dissPopWindow();
                    }
                }

                taskCenterToast = new TaskCenterToast(activity, taskCenterInfo);
                if (taskCenterInfo.getType() != null) {

                    MainApplication mainApplication = (MainApplication) activity.getApplication();
                    if (!mainApplication.isShowingBottomTab() || !(activity instanceof MainActivity)) {// 判断如果当前底部tab栏是否正在隐藏状态，则缓存消息
                        if (taskCenterInfo.getType().equals("discover")) {
                            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_TASKCENTER_DISCOVERMSG, jsonStr);
                            return false;
                        } else {
                            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_TASKCENTER_REDSHOW, true);
                        }
                    } else {
                        notifyShowRed(activity, mainApplication);
                    }
                }

                if (!activity.isFinishing()) {
                    taskCenterToast.showPopUpWindow();
                    // 事件处理,更改pop状态
                    taskCenterToast.setOpenPop(true);
                } else {
                    return false;
                }

                mTimer = new Timer();
                autoClosePopWindow(5000);// 开启5秒自动关闭功能

                // 弹出提示框成功，尝试remove掉已经缓存的msg。以防下次再弹出提示
                SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_TASKCENTER_DISCOVERMSG);
            } else if (msg.what == 3) {
                // 强制跳转
                UserInfoUtils.jumpToFillUser(taskCenterInfo);

            } else if (msg.what == 4) {
                SpecialRecommendDialog specialdialog = SpecialRecommendDialog.getInstance();
                // 如果当前弹框正在显示的话，来消息不请求新数据
                if (!specialdialog.isShowing()) {
                    specialdialog.getData();
                }

            } else if (msg.what == 5) {
                // 不管过来多少次消息只改变一次
                SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
            } else if (msg.what == 6) {
                if (MainApplication.getInstance().isRunning() && Utils.isAppOnForeground(UserInfoUtils.getActivity())) {
                    int statusHeight = Utils.getStatusHeight(UserInfoUtils.getFirstActivity());
                    Activity activity = UserInfoUtils.getActivity();
                    if (activity != null) {
                        NotificationDialog dialog = new NotificationDialog(activity, statusHeight);
                        if (!activity.isFinishing()) {
                            dialog.showTopDialog();
                            dialog.setTextString(taskCenterInfo.getMsg());
                        }
                    }

                }
            }
            return false;
        }
    });

    private void notifyShowRed(Activity activity, MainApplication mainApplication) {
        // 发送广播通知首页tab发现处添加红点提醒
        Intent tabRedIntent = new Intent();
        tabRedIntent.setAction(UrlConfig.HIDE_TABRED_ACTION);
        tabRedIntent.putExtra("tag", -1);
        activity.sendBroadcast(tabRedIntent);
        // 设置发现页强刷标记
        mainApplication.setNeedForceRefreshDiscover(true);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("com.zhongsou.im.souyuekickedout")) {
                taskCenterInfo = new TaskCenterInfo();
                taskCenterInfo.setCategory("relogin");
                taskCenterInfo.setToken(intent.getStringExtra("token"));
                taskCenterInfo.setMsg(intent.getStringExtra("msg"));
                if (MainApplication.getInstance().isRunning() && Utils.isAppOnForeground(UserInfoUtils.getActivity()) && !(UserInfoUtils.getActivity() instanceof FirstInActivity)) {
                    Activity activity = UserInfoUtils.getActivity();
                    User user = SYUserManager.getInstance().getUserAdmin();
                    if (null != taskCenterInfo.getToken() && user.token() != null && taskCenterInfo.getToken().equals(user.token())) {// 执行退出操作
                        if (activity == null) {
                            return;
                        }
                        if (activity != null) {
                            SYUserManager.userExitSouYue(context);
                            IntentUtil.goHomeSouyue(activity);
                        }
                        mTimer = new Timer();
                        WaitForShowPop waitForShowPop = null;
                        if (waitForShowPop != null) {
                            waitForShowPop.cancel(); // 将原任务从队列中移除
                        }
                        waitForShowPop = new WaitForShowPop(); // 新建一个任务
                        mTimer.schedule(waitForShowPop, 1000);
                    }
                } else {
                    SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_TASKCENTER_KICKUSER_TOKEN, intent.getStringExtra("token"), SYSharedPreferences.KEY_TASKCENTER_KICKUSER_MSG,
                            intent.getStringExtra("msg"));

                }
                return;
            } else {
                jsonStr = intent.getStringExtra("data");
//                taskCenterInfo = JSON.parseObject(jsonStr, TaskCenterInfo.class);
                taskCenterInfo = new Gson().fromJson(jsonStr, TaskCenterInfo.class);
                if (taskCenterInfo != null && StringUtils.isNotEmpty(taskCenterInfo.getCategory()) && taskCenterInfo.getCategory().equals("user")) {
                    handler.sendEmptyMessage(3);
                } else if (taskCenterInfo != null && StringUtils.isNotEmpty(taskCenterInfo.getCategory()) && taskCenterInfo.getCategory().equals("SysRecommend")) {// 如果是系统推荐
                    if (taskCenterInfo.getType().equals("popWindow")) {
                        if (Highlight.hasShowTip() && !SubRecommendDialog.getIsShowingMe() && MainApplication.getInstance().isRunning()
                                && Utils.isAppOnForeground(UserInfoUtils.getActivity()) && !(UserInfoUtils.getActivity() instanceof FirstInActivity)) {
                            String special_timestamp = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SPECIAL, "");
                            if (StringUtils.isEmpty(special_timestamp)) {
                                handler.sendEmptyMessage(4);
                            } else {
                                // 同一用户两条消息之间的间隔超过5分钟的话客户端才处理
                                String[] special_timestamps = special_timestamp.split(",");
                                if (special_timestamps != null && special_timestamps.length > 1) {
                                    User u = SYUserManager.getInstance().getUser();
                                    if (u != null) {
                                        if (special_timestamps[0].equals(u.userId() + "")) {
                                            long currentTemp = System.currentTimeMillis();
                                            long lastTemp = Long.parseLong(special_timestamps[1]);
                                            // 5*60*1000
                                            if (currentTemp - lastTemp > TaskCenterReceiver.DIALOG_TIME_STEP) {
                                                handler.sendEmptyMessage(4);
                                            }
                                        } else {
                                            handler.sendEmptyMessage(4);
                                        }
                                    } else {
                                        handler.sendEmptyMessage(4);
                                    }
                                } else {
                                    handler.sendEmptyMessage(4);
                                }

                            }
                        } else {
                            if (SubRecommendDialog.getIsShowingMe()) {
                                SubRecommendDialog instance = SubRecommendDialog.getInstance();
                                if (instance != null) {
                                    instance.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            //消失的时候，要显示推荐
                                            handler.sendEmptyMessage(4);
                                        }
                                    });
                                }
                            } else {
                                SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_SYSTEM_SPECIAL, taskCenterInfo.getType());
                            }
                        }

                    } else if (taskCenterInfo.getType().equals("spcUpdate")) {
                        if (!SYSharedPreferences.getInstance().getBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, false)) {
                            handler.sendEmptyMessage(5);
                        }
                    } else if (taskCenterInfo.getType().equals("subPopWindow")) {
                        //如果可弹，就弹
                        if (Highlight.hasShowTip() && !SpecialRecommendDialog.getInstance().isShowing()   // 另一个弹窗不显示
                                && MainApplication.getInstance().isRunning()    // 程序正在运行
                                && Utils.isAppOnForeground(UserInfoUtils.getActivity())  // 正在前台
                                && (UserInfoUtils.getActivity() instanceof MainActivity)) // 前台是MainActivity
                        {
                            if (Utils.checkOverTime(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SUBRECOMMEND, DIALOG_TIME_STEP)) {
                                //检测超时才能弹出来。
                                SubRecommendDialog.showDialog(UserInfoUtils.getActivity(), false, taskCenterInfo.getIsPre(), taskCenterInfo.getListId(), false);
                            }
                        } else {
                            //缓存起来这次需要弹的弹框
                            SubRecommendDialog.addToQueue(false, taskCenterInfo.getIsPre(), taskCenterInfo.getListId());
                            //如果推荐正在显示，则设置dismiss时显示订阅
                            if (SpecialRecommendDialog.getInstance().isShowing()) {
                                SpecialRecommendDialog.getInstance().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        SubRecommendDialog.showDialog(UserInfoUtils.getActivity(), false, null, null, false);
                                    }
                                });
                            } else {
                                SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND, taskCenterInfo.getType());
                            }
                        }
                    }
                } else if (taskCenterInfo != null && StringUtils.isNotEmpty(taskCenterInfo.getCategory()) && taskCenterInfo.getCategory().equals("notification")
                        && taskCenterInfo.getType().equals("dismsg")) {
                    handler.sendEmptyMessage(6);
                } else if (taskCenterInfo != null && StringUtils.isNotEmpty(taskCenterInfo.getCategory()) && taskCenterInfo.getCategory().equals("task")) {
                    mTimer = new Timer();
                    ShowTaskCenter showTaskCenter = null;
                    if (showTaskCenter != null) {
                        showTaskCenter.cancel(); // 将原任务从队列中移除
                    }
                    showTaskCenter = new ShowTaskCenter(); // 新建一个任务
                    mTimer.schedule(showTaskCenter, 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    /**
     * 自动关闭方法
     */

    private void autoClosePopWindow(long delay) {
        if (mTimer != null) {
            if (clTask != null) {
                clTask.cancel(); // 将原任务从队列中移除
            }

            clTask = new closeTimerTask(); // 新建一个任务
            mTimer.schedule(clTask, delay);
        }
    }

    class closeTimerTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }

    }

    class ShowTaskCenter extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(2);
        }
    }

    class WaitForShowPop extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    }
}
