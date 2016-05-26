package com.zhongsou.souyue.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
//import com.huawei.android.pushagent.PushManager;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.CMainHttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 所有推送平台的工具类
 * @date 2015/11/26
 */
public class PushUtils {

    public static final String TAG = "PushUtils";
    public static final String MI_TAG = "SOUYUE_MiPush";
    public static final String JPUSH_TAG = "SOUYUE_JPush";
    public static final String MIPUSH_APP_ID = "2882303761517127449";
    public static final String MIPUSH_APP_KEY = "5321712760449";
    private static final int SET_JPUSH_TAGS = 0;
    private static final int SET_PUSH_ALIGN = 1;

    private static Set<String> jpushTags = new HashSet<String>();   //用于存放成功设置的JPush  Tag

    private static class PushCallbackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_JPUSH_TAGS:   //设置极光tags
                    PushUtils.setSingleJPushTag((Context) msg.obj, UrlConfig.getJPushTag());
                    break;
                case SET_PUSH_ALIGN:   //设置推送别名（极光和小米）
                    try {
                        PushUtils.setPushAlias((Context) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * get an object of LoggerInterface
     *
     * @return LoggerInterface
     */
    public static LoggerInterface getMiPushLoggerInterface() {
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(MI_TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(MI_TAG, content);
            }
        };
        return newLogger;
    }

    /**
     * 针对MiPush
     * 因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，
     * 这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化
     *
     * @return 是否需要初始化
     */
    private static boolean shouldInitMiPush(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * MiPush 初始化方法，并设置Tag
     *
     * @param context
     */
    public static void initMiPush(Context context) {
        if (!isXiaoMi()) { //此时是不是小米设备，返回，不进行初始化
            MiPushClient.unregisterPush(context);
            return;
        }
        if (!readIsOpenXiaoMiPush(context)) {   //升级之后 regid不变
            MiPushClient.unregisterPush(context);
            Log.d(TAG, "readIsOpenXiaoMiPush(context) ====" + readIsOpenXiaoMiPush(context));
            return;
        }
        if (shouldInitMiPush(context)) {
            List<String> topics = MiPushClient.getAllTopic(context);
            for(String topic : topics){ //取消之前所有无效topic
                if(!topic.equals(UrlConfig.getMiPushTopic())){
                    MiPushClient.unsubscribe(context, topic, null);
                }
            }
            MiPushClient.registerPush(context, MIPUSH_APP_ID, MIPUSH_APP_KEY);
            Log.d(TAG, "initMiPush(context) ====");
        }
        PushUtils.miPushSubscribeTopic(context, UrlConfig.getMiPushTopic());   //设置标签
//        PushUtils.miPushSubscribeTopic(context, "xiaomi");   //设置标签 测试用！！！
        com.xiaomi.mipush.sdk.Logger.setLogger(context, PushUtils.getMiPushLoggerInterface());  //enable log
    }

    /**
     * MiPush初始化
     *
     * @param context
     */
    public static void miPushInit(Context context) {
        if (shouldInitMiPush(context)) {
            MiPushClient.registerPush(context, MIPUSH_APP_ID, MIPUSH_APP_KEY);
        }
    }

    /**
     * MiPush订阅标签
     *
     * @param context 上下文
     * @param topic   标签名
     */
    public static void miPushSubscribeTopic(Context context, String topic) {
        MiPushClient.subscribe(context, topic, null);
    }


    //=============================

//    public static void initHwPush(Context context) {
//        if (!isHuaWei()) {
////            PushManager.deregisterToken();  //客服不推荐使用
////            PushManager.enableReceiveNotifyMsg();   //通知控制方法
////            PushManager.enableReceiveNormalMsg();   //透传控制方法
//
//            PushManager.enableReceiveNotifyMsg(context, false);      //禁用通知，会到达终端，但不会显示
//            return;
//        }
//
//        PushManager.requestToken(context);
//        setHwPushTag(context);
//    }

    /**
     * 华为推送  设置tag
     *
     * @param context 上下文
     */
//    public static void setHwPushTag(Context context) {
//        String hwtag = "hwtag";
////        List<String> listTagKey = new ArrayList<String>();
////        listTagKey.add(hwtag);
////        PushManager.deleteTags(context, listTagKey);    //删除tag
//
//        Map<String, String> hwTags = new HashMap<String, String>();
//        hwTags.put(hwtag, UrlConfig.getHwPushTopic());
//        PushManager.setTags(context, hwTags);
//    }

    //=============================


    //=============================

    /**
     * JPush 初始化方法，并设置Tag
     *
     * @param context
     */
    public static void initJPush(Context context) {
//        final Context finalContext = context;
        if (isXiaoMi() || isHuaWei()) { //此时是小米设备或者华为，返回，不进行初始化
            JPushInterface.stopPush(context);
            return;
        }
        if (!readIsOpenJPush(context)) {  //升级之后 regid不变
//        if (true) {
            JPushInterface.stopPush(context);
            Log.d(TAG, "readIsOpenJPush(context) ====" + readIsOpenJPush(context));
            return;
        }

        JPushInterface.setDebugMode(UrlConfig.isTest());    //根据搜悦环境判断是否设置为Debug模式
        JPushInterface.init(context);
        if (JPushInterface.isPushStopped(context)) {
            JPushInterface.resumePush(context);
        }
        Log.d(TAG, "initJPush(context) ====");
        PushUtils.setSingleJPushTag(context, UrlConfig.getJPushTag());
    }

    /**
     * JPush 设置多个Tag 该方法是覆盖Tag，不是增加Tag
     *
     * @param context 上下文
     * @param tags    tags为空时，是清空tags
     */
    public static void setJPushTag(final Context context, Set<String> tags) {

        tags = JPushInterface.filterValidTags(tags);    //过滤无效tag
        TagAliasCallback tagAliasCallback = new TagAliasCallback() {  //回调接口
            /**
             * 回调方法
             * @param responseCode 0表示成功，其他为错误码
             * @param alias 获得设置成功的别名
             * @param tags 获得设置成功的TAG
             */
            @Override
            public void gotResult(int responseCode, String alias, Set<String> tags) {
                if (responseCode == 0) {
                    LogDebugUtil.d(JPUSH_TAG, "souyue setTags successful");
                    jpushTags = tags;   //存放成功的Tag
                } else {
                    LogDebugUtil.d(JPUSH_TAG, "souyue setTags error code : " + responseCode);
                    if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
//                        new Handler() {
//                            @Override
//                            public void handleMessage(Message msg) {
//                                super.handleMessage(msg);
//                                PushUtils.setSingleJPushTag(finalContext, UrlConfig.getJPushTag());
//                            }
//                        }.sendEmptyMessageAtTime(0, 60 * 1000);

                        PushCallbackHandler pushHandler = new PushCallbackHandler();

                        Message message = pushHandler.obtainMessage();
                        message.obj = context;
                        message.what = SET_JPUSH_TAGS;

                        pushHandler.sendMessageDelayed(message, 60 * 1000);
                    }
                }
                LogDebugUtil.d(JPUSH_TAG, "souyue tags : " + tags.toString());
            }
        };
        // 设置JPush标签
        JPushInterface.setTags(context, tags, tagAliasCallback);
    }


    /**
     * JPush 设置Tag 覆盖之前的tag，不是添加！！
     *
     * @param context 上下文
     */
    public static void setSingleJPushTag(Context context, String string) {

        Set<String> tags = new HashSet<String>();   //JPush Tags set
        tags.add(string);

        setJPushTag(context, tags);
    }

    /**
     * JPush 在原来tag基础上再添加一个tag 需要在setJPushTags成功返回之后才能调用 未测试
     *
     * @param context 上下文
     */
    public static void addJPushTag(Context context, String string) {

        jpushTags.add(string);

        setJPushTag(context, jpushTags);
    }

    /**
     * JPush 设置Tag
     *
     * @param context 上下文
     */
    public static void setDefaultJPushTag(Context context) {

        Set<String> tags = new HashSet<String>();   //JPush Tags set
        tags.add("souyue_jpush");
        tags.add("souyue_jpush_test");

        setJPushTag(context, tags);
    }


    /**
     * 判断是否是小米设备
     *
     * @return true
     * false
     */
    private static boolean isXiaoMi() {
        String xiaomi = "xiaomi";
        boolean ret = DeviceInfo.fingerprint.toLowerCase().contains(xiaomi)
                || DeviceInfo.brand.toLowerCase().contains(xiaomi)
                || DeviceInfo.manufacturer.toLowerCase().contains(xiaomi);  // 该条件考虑删除
        return ret;
    }


    /**
     * 判断是否是华为设备
     *
     * @return true
     * false
     */
    private static boolean isHuaWei() {
        String huawei = "huawei";
        boolean ret = DeviceInfo.fingerprint.toLowerCase().contains(huawei)
                || DeviceInfo.brand.toLowerCase().contains(huawei);
        return ret;
    }

    /**
     * 根据设备设置别名
     *
     * @param context 上下文
     */
    public static void setPushAlias(final Context context) throws Exception {
        String userId = SYUserManager.getInstance().getUserId();    //获得UserID
        String md5UserId = Utils.get2MD5(userId);   //生成MD5
        Log.d(TAG, "before md5 alias : " + SYUserManager.getInstance().getUserId());
        Log.d(TAG, "md5 alias : " + md5UserId);
//        Log.d(TAG, "userType alias : " + userType);
        if (!isXiaoMi() && readIsOpenJPush(context)) { //不是小米设备，并且极光打开
            JPushInterface.setAlias(context, md5UserId, new TagAliasCallback() {
                @Override
                public void gotResult(int responseCode, String alias, Set<String> tags) {
                    if (responseCode == 0) {  //设置成功
                        Log.d(TAG, "Jpush souyue setAlias successful : " + alias);
                    } else {  //设置失败
                        if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
                            PushCallbackHandler pushHandler = new PushCallbackHandler();

                            Message message = pushHandler.obtainMessage();
                            message.obj = context;
                            message.what = SET_PUSH_ALIGN;

                            pushHandler.sendMessageDelayed(message, 60 * 1000);
                        }
                    }
                }
            });
        } else if (isXiaoMi() && readIsOpenXiaoMiPush(context)) { //是小米设备，并且小米打开
            String userType = SYUserManager.getInstance().getUserType();    //获得用户类型
            if (userType.equals(SYUserManager.USER_ADMIN)) {  //当用户为注册用户时
                String md5GuestId = Utils.get2MD5(SYUserManager.getInstance().getGuestId());    //游客ID
                MiPushClient.unsetAlias(context, md5GuestId, null); //用户登录后，取消用游客ID注册的Alias
                Log.d(TAG, "mipush disable guest alias mi : " + md5GuestId);
            }
            MiPushClient.setAlias(context, md5UserId, null);
            Log.d(TAG, "mipush md5 alias mi : " + md5UserId);
        }
    }

    /**
     * 根据手机平台获得推送的注册ID
     *
     * @param context 上下文
     * @return regID
     */
    public static String getPushRegID(Context context) {
        String regID = null;
        try {
            if (isXiaoMi()) {
                regID = MiPushClient.getRegId(context);
            } else {
                regID = JPushInterface.getRegistrationID(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "PushUtil getPushRegID Exception !!!!");
        }
        return regID;
    }


    /**
     * 是否需要上传regID
     *
     * @return true 是   false 否
     */
    public static boolean isUploadPushRegID() {
        boolean flag = true;
        try {
            String strUserID = SYUserManager.getInstance().getUserId();
            if (StringUtils.isEmpty(strUserID)) {
                flag = false;
            } else {
                long userID = Long.parseLong(strUserID);
                flag = CommSharePreference.getInstance().getValue(userID, ConstantsUtils.PUSH_REGID_NAME, true);
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            Log.e(TAG, "isUploadPushRegID exception");
        }
        return flag;
    }

    public static int getPushChannel() {
        int pushChannel = 0;
        if (isXiaoMi()) {
            pushChannel = 1;
        } else {
            pushChannel = 2;
        }
        return pushChannel;
    }

    public static void stopPush(Context context){
        if(isXiaoMi()){
            MiPushClient.pausePush(context, null);
        }else{
            JPushInterface.stopPush(context);
        }
    }

    public static void resumePush(Context context){
        if(isXiaoMi()){
            MiPushClient.resumePush(context, null);
        }else{
            if(JPushInterface.isPushStopped(context)){
                JPushInterface.resumePush(context);
            }
        }
    }

    //=========服务端接口===============

    private static final String IsOpenXiaoMiPush = "IsOpenXiaoMiPush";
    private static final String IsOpenJPush = "IsOpenJPush";

    /**
     * 保存 是否打开小米推送
     *
     * @param context
     * @param flag
     */
    public static void saveIsOpenXiaoMiPush(Context context, boolean flag) {
        CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, IsOpenXiaoMiPush, flag);
    }

    /**
     * 读取  是否打开小米推送
     * 默认为打开
     *
     * @param context
     * @return
     */
    private static boolean readIsOpenXiaoMiPush(Context context) {
        return CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, IsOpenXiaoMiPush, true);
    }

    /**
     * 保存 是否打开极光推送
     *
     * @param context
     * @param flag
     */
    public static void saveIsOpenJPush(Context context, boolean flag) {
        CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, IsOpenJPush, flag);
    }

    /**
     * 读取  是否打开极光推送
     * 默认为打开
     *
     * @param context
     * @return
     */
    private static boolean readIsOpenJPush(Context context) {
        return CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, IsOpenJPush, true);
    }
}
