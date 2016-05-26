package com.tuita.sdk;

import android.app.*;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.os.Vibrator;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.helper.*;
import com.tuita.sdk.im.db.module.*;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 发送IM广播
 *
 * @author iamzl
 */
public class BroadcastUtil {
    private static final String LOGTAG = "Tuita_im";
    private static int CHANNEL = 1;//通知的ID 私聊群聊默认为1，服务号则为服务号ID
    private static HashMap<Integer, HashMap<Integer, Integer>> notifyMap = new HashMap<Integer, HashMap<Integer, Integer>>();//此map维护了一个关于ID和通知次数的队列
    private static final String DESC = "您有%s条未读新消息";
    private static NotificationManager notificationManager;
    private static final Random RANDOM = new Random();

    private static Intent intent;
    private static String ACTION = "com.zhongsou.im";
    public static String ACTION_SEARCH = ACTION + ".search";// 查找用户
    public static String ACTION_RECENT_ADD = ACTION + ".recent.add";// 最近聊天
    public static String ACTION_NEWFRIEND = ACTION + ".newfriend";// 收到新朋友请求
    public static String ACTION_CONTACT = ACTION + ".contact";// 通讯录
    public static String ACTION_CONTACT_AND_MSG = ACTION + ".contactandmsg";// 通讯录
    public static String ACTION_MOBILE_CONTACT = ACTION + ".mobile.contact";// 手机联系人
    public static String ACTION_MOBILE_CONTACT_UPLOAD_SUCCESS = ACTION + ".upload.contact.success";// 手机联系人上传成功
    public static String ACTION_MOBILE_CONTACT_UPLOAD_ERROR = ACTION + ".upload.contacterror";// 手机联系人上传失败
    public static String ACTION_MOBILE_CONTACT_STATUS = ACTION
            + ".contactstatus";// 通讯录
    public static String ACTION_SYS_MSG = ACTION + ".sysmsg";
    public static String ACTION_GIFT_ZSB = ACTION + ".giftzsb";
    public static String ACTION_CHARGE_ZSB = ACTION + ".chargezsb";

    public static String ACTION_SOUYUE_MSG = ACTION + ".souyuemsg"; //搜悦调用广播
    public static String ACTION_SOUYUE_KICKEDOUT = ACTION + ".souyuekickedout"; //搜悦退出，单点登录


    // 聊天页面
    public static String ACTION_MSG_ADD = ACTION + ".msg.add";// 聊天信息
    public static String ACTION_MSG_ADD_ONLINE = ACTION + ".msg.addonline";// online聊天信息
    public static String ACTION_MSG = ACTION + ".msg";// 用于监听广播
    public static String ACTION_MSG_SEND_SUCCESS = ACTION + ".msg.success";// 发送成功
    public static String ACTION_MSG_SEND_FAIL = ACTION + ".msg.fail";// 发送失败
    public static String ACTION_MSG_SEND_ERROR_NOTFRIEND = ACTION
            + ".msg.errornotfriend";// 发送失败
    public static String ACTION_CONNECT_FAIL = ACTION + ".connect.fail";
    public static String ACTION_CONNECT_SUCCESS = ACTION + ".connect.success";
    public static String ACTION_CLRAR_MESSAGE_BUBBLE = ACTION
            + ".clear.msgbubble";

    //群聊相关
    public static String ACTION_GROUP_CREATE_SUCCESS = ACTION + ".msg.success";// 创建群成功
    public static String ACTION_GROUP_CREATE_FAIL = ACTION + ".msg.fail";// 创建群失败
    public static String ACTION_GROUP_EXIT = ACTION + "msg.exit";//退群
    public static String ACTION_GROUP_INFO = ACTION + "msg.info";//群信息
    public static String ACTION_ADD_GROUP = ACTION + "msg.addgroup";//加入群
    public static String ACTION_MEMBER_INFO = ACTION + "msg.memberinfo";//群友信息

    //消息加载广播
    public static String ACTION_TAB_LOADING = ACTION + "msg.loading";//消息加载广播

    //im错误码，请求广播
    public static String ACTION_ERROR_TIP = ACTION + "msg.errortip";//IM 整体错误提示
    public static String ACTION_SUCCESS_DIALOG = ACTION + "msg.success.dialog";//IM成功广播（回调）

    //登陆表情标新标志
    public static String ACTION_EXPRESSION_NEW = ACTION + "msg.expression.new";
    private static Vibrator mVibrator;

    private static SharedPreferences timeSp;
    public static String TIME_SHAREDPREFERENCE = "TuitaTimeSp";//通知栏显示时间存储
    public static long TIME_GAP = 5000;//两条消息间隔时间
    public static long TIME_RESET = 10 * 1000;//time重置时间

    private static AudioManager am;//声音管理器，主要为了解决当系统铃声音量为0时搜悦控制提示音开启变为震动问题
    private static int current;        //系统声音音量
    private static Gson gson = new Gson();
    private static String notice_show_jpush = "notice_show_jpush";  //Umeng
    private static String notice_show = "notice_show";    //Umeng

    /**
     * 显示通知栏
     *
     * @param context
     * @param action
     * @param data
     * @param isNotify
     */
    public static void sendBroadcastToUI(Context context, String action,
                                         String data, boolean isNotify) {
        timeSp = context.getSharedPreferences(TIME_SHAREDPREFERENCE, context.MODE_PRIVATE);
        long timeOld = timeSp.getLong("timeOld", 0);
        long timeNew = System.currentTimeMillis();
        intent = new Intent();
        intent.setAction(action);
        JSONObject dataJson = null;
        long senderId = 0;

        if (data != null) {
            intent.putExtra("data", data);
            try {
                dataJson = new JSONObject(data);
                senderId = dataJson.has("chat_id") ? dataJson.getLong("chat_id") : 0;
                isNotify = dataJson.getInt("content_type") == IMessageConst.CONTENT_TYPE_NEW_SYSTEM_MSG ? false : isNotify;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (context != null) {
            if (!isNotify(context, senderId) && isNotify) {
                if (PushService.canPushMessage(context) && !TuitaIMManager.mIsShowNfBar) {//判断如果设置消息免打扰或者搜悦内部设置消息推送不打开则不显示通知栏
                    notify(context, data, timeNew - timeOld < TIME_GAP ? false : true); //打开通知栏
                    saveTime(timeNew);
                }
            } else {
                context.sendBroadcast(intent);
            }
        }
        Log.i(TuitaSDKManager.TAG, "sendBroadcast success " + action
                + "------------->" + data);
    }

    public static void saveTime(long timeNew) {
        if (timeSp != null) {
            Editor editor = timeSp.edit();
            editor.putLong("timeOld", timeNew);
            editor.commit();
        }

    }

    /**
     * 来了通知时先判断有没有对应的ID 如果有则证明以前已经发过此类通知，则增加此通知的条数，如果没有则值赋为1
     * 这里因为需要区分不同用户发送的不同消息数、所以需要多个userId参数
     *
     * @param
     */
    public synchronized static void putNotifyNum(int notifyId, int userId, Context context) {
        if (notifyMap.containsKey(notifyId) && notifyMap.get(notifyId).containsKey(userId)) {
            notifyMap.get(notifyId).put(userId, notifyMap.get(notifyId).get(userId) + 1);
        } else if (notifyMap.containsKey(notifyId) && !notifyMap.get(notifyId).containsKey(userId)) {
            notifyMap.get(notifyId).put(userId, 1);
        } else {
//            notifyMap.get(notifyId).put(userId, 1);
            HashMap hashMap = new HashMap<Integer, Integer>();
            hashMap.put(userId, 1);
            notifyMap.put(notifyId, hashMap);
        }
        TuitaSDKManager.getInstance(context).preferences.edit().putString(NOTIFY_MAP, gson.toJson(notifyMap)).commit();
    }

    /**
     * 获取所有联系人发送消息的总数量 专指服务号
     *
     * @param notifyId 通知栏id
     * @param userId   用户ID
     * @return
     */
    public synchronized static int getNotifyNum(int notifyId, int userId) {
        return notifyMap.get(notifyId).get(userId);
    }

    /**
     * 获取所有联系人发送消息的总数量 专指私聊和群聊和群聊
     *
     * @param notifyId 通知栏id
     * @return
     */
    public synchronized static int getNotifyAllNum(int notifyId) {
        int i = 0;
        if (notifyMap.get(notifyId) != null) {
            for (int v : notifyMap.get(notifyId).values()) {
                System.out.println("value= " + v);
                i += v;
            }
        }
        return i;
    }

    /**
     * 获取有多少个联系人
     *
     * @param notifyId
     * @return
     */
    public synchronized static int getAllContacts(int notifyId) {
        return notifyMap.get(notifyId).size();
    }


    /**
     * 1.重置通知的数量 当用户点击进入相关的界面时，需要清除掉指定ID的通知数量
     * <p/>
     * 2.取消通知栏通知，为了支持取消全部通知栏通知功能，（例如用户退出登录） 当 notifyId 为 -1时，
     * 设置清空所有Map中维护的ID
     *
     * @param id
     * @return
     */
    public synchronized static void resetSpecifyId(int id) {
        Log.i("notifycation", "resetID" + id);
        if (id == -1) {
            notifyMap.clear();
        }else if (id == -2){
            //不做操作
        }else {
            notifyMap.remove(id);//Id不存在也没关系，所以不用判断
        }
    }

    private static boolean isNotify(Context context, long sendId) {

        return PushService.getIsInChat(context) == sendId || sendId == 0 ? true : false;
    }

    /**
     * 判断前后台运行
     *
     * @return
     */
    private static boolean isAppOnForeground(Context context) {
        String packageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否锁屏
     *
     * @return
     */
    private static boolean isScreenLocker(Context context) {
        KeyguardManager km = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    private static final String NOTIFY_MAP = "NOTIFY_MAP";

    private static void notify(Context context, String data, boolean soundFlag) {
        Log.i(LOGTAG, "notify,data=" + data);
        Notification n = null;
        initNotifyMap(context);
        try {
            JSONObject json = new JSONObject(data);
            if (json.getInt("chat_type") == IConst.CHAT_TYPE_PRIVATE && json.getLong("myid") != 0 && json.getLong("sender") != 0) {

                Contact contact = ContactDaoHelper.getInstance(context).find(json.getLong("myid"), json.getLong("sender"));
                MessageHistory his = MessageHistoryDaoHelper.getInstance(context).find(json.getString("uuid"), json.getLong("myid"));
                String showContent = showContent(his.getContent_type(), his.getContent(), context, json);
                String showName = TextUtils.isEmpty(contact.getComment_name()) ? contact.getNick_name() : contact.getComment_name();
                String showTitle = context.getString(R.string.notify_title);

                CHANNEL = 1;
                putNotifyNum(CHANNEL, (int) json.getLong("chat_id"), context);
                int contantNum = getAllContacts(CHANNEL);
                Intent singleIntent = new Intent(getBroadcastAction(context));
                if (contantNum > 1) {//判断是几个联系人，1，控制通知栏显示文字   2，判断点击后跳到哪个界面
                    singleIntent.putExtra(Constants.TYPE, Constants.TYPE_LIST);
                    showContent = String.format(context.getString(R.string.notify_contant_multi), contantNum, getNotifyAllNum(CHANNEL));
                } else {
                    singleIntent.putExtra(Constants.TYPE, Constants.TYPE_CHAT);
//                    showContent = showName+":发来" + getNotifyNum(CHANNEL,(int)json.getLong("chat_id"))+"条消息";
                    showContent = String.format(context.getString(R.string.notify_contant_single), showName, getNotifyNum(CHANNEL, (int) json.getLong("chat_id")));
                    singleIntent.putExtra(Constants.TARGET_TYPE, IConst.CHAT_TYPE_PRIVATE);
                    singleIntent.putExtra(Constants.TARGET_ID, json.getLong("chat_id"));
                    singleIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
                }
                //获取Notification
                n = getNotification(context, soundFlag, singleIntent, showContent, showTitle);
            } else if (json.getInt("chat_type") == IConst.CHAT_TYPE_GROUP && json.getLong("myid") != 0 && json.getLong("sender") != 0) {
                Group group = GroupDaoHelper.getInstance(context).find(json.getLong("myid"), json.getLong("chat_id"));
                GroupMembers members = GroupMembersDaoHelper.getInstance(context).find(json.getLong("myid"), json.getLong("chat_id"), json.getLong("sender"));
                MessageHistory his_gr = MessageHistoryDaoHelper.getInstance(context).find(json.getString("uuid"), json.getLong("myid"));
                String showContent = showContent(his_gr.getContent_type(), his_gr.getContent(), context, json);
                String showName = null;
                showName = group.getGroup_nick_name();
                String showTitle = context.getString(R.string.notify_title);
                CHANNEL = 1;
                putNotifyNum(CHANNEL, (int) json.getLong("chat_id"), context);
                int contantNum = getAllContacts(CHANNEL);
                Intent groupIntent = new Intent(getBroadcastAction(context));
                if (contantNum > 1) {//判断是几个联系人，1，控制通知栏显示文字   2，判断点击后跳到哪个界面
                    groupIntent.putExtra(Constants.TYPE, Constants.TYPE_LIST);
                    showContent = String.format(context.getString(R.string.notify_contant_multi), contantNum, getNotifyAllNum(CHANNEL));
                } else {
                    groupIntent.putExtra(Constants.TYPE, Constants.TYPE_CHAT);
                    groupIntent.putExtra(Constants.TARGET_TYPE, IConst.CHAT_TYPE_GROUP);
                    groupIntent.putExtra(Constants.TARGET_ID, json.getLong("chat_id"));
                    groupIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
                    showContent = String.format(context.getString(R.string.notify_contant_single), showName, getNotifyNum(CHANNEL, (int) json.getLong("chat_id")));
                }
                //获取Notification
                n = getNotification(context, soundFlag, groupIntent, showContent, showTitle);
            } else if (json.getInt("chat_type") == IConst.CHAT_TYPE_SERVICE_MESSAGE && json.getLong("myid") != 0) {
                MobclickAgent.onEvent(context, notice_show);    //Umeng 统计通知栏显示（除了聊天消息）
                String serviceJumpData = null;
                String pushFrom = "";
                String mid = "";
                if (json.has("pushFrom")) {
                    pushFrom = json.getString("pushFrom");
                }

                if (IConst.PUSH_TYPE_JPUSH.equals(pushFrom)) {   //此时是JPush
                    MobclickAgent.onEvent(context, notice_show_jpush);    //Umeng 统计
                }

                if (json.has("by4")) {
                    mid = json.getString("by4");
                }
                if (json.has("serviceJumpData")) {
                    serviceJumpData = json.getString("serviceJumpData");
                }
                long chatId = json.getLong("chat_id");//赋值为服务号ID
                if (chatId == getServiceId(context)) {
                    CHANNEL = RANDOM.nextInt(Integer.MAX_VALUE - 100) + 101;
                } else {
                    CHANNEL = (int) chatId;
                }
                putNotifyNum(CHANNEL, CHANNEL, context);
                Intent serviceMsgIntent = sendNotifyBroadCast(context, serviceJumpData, pushFrom, mid);

                ServiceMessage seMsgRe1 = ServiceMessageDaoHelper
                        .getInstance(context)
                        .findByMuid(json.getString("uuid"));
                MessageHistory his = MessageHistoryDaoHelper.getInstance(context).find(json.getString("uuid"), json.getLong("myid"));

                //如果是CT1或者CT2  就用digst 不是用content
                his.setContent(his.getContent_type() == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST ||
                                his.getContent_type() == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND ?
                                seMsgRe1.getDigst() : his.getContent()
                );
                String showContent = showContent(his.getContent_type(), his.getContent(), context, json);
                showContent = (getNotifyNum(CHANNEL, CHANNEL) > 1 ? String.format(context.getString(R.string.notify_msg_num), getNotifyNum(CHANNEL, CHANNEL)) : "") + showContent;//如果未读消息大于1则显示未读消息数量
                String showName = CHANNEL == 25 ? context.getString(R.string.notify_service_sy) : seMsgRe1.getTitle();
                //获取Notification
                n = getNotification(context, soundFlag, serviceMsgIntent, showContent, showName);
            } else if (json.getInt("chat_type") == IConst.CHAT_TYPE_SYSTEM && json.getLong("myid") != 0) {
                String serviceJumpData = null;
                long chatId = json.getLong("chat_id");//赋值为服务号ID
                String content = json.getString("content");
                if (json.has("serviceJumpData")) {
                    serviceJumpData = json.getString("serviceJumpData");
                }
                CHANNEL = (int)chatId;
                putNotifyNum(CHANNEL, -2, context);
                Intent sysMsgIntent = sendNotifyBroadCast(context, serviceJumpData, "", "");

                //如果是CT1或者CT2  就用digst 不是用content
                String showContent = context.getString(R.string.ask_add_friend);
                String showName = content;
                //获取Notification
                n = getNotification(context, soundFlag, sysMsgIntent, showContent, showName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PushService.msgNum++;
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.notify(CHANNEL, n);
    }


    private static String getAppLabel(Context context) {
        String label = getAppInfo(context).loadLabel(
                context.getPackageManager()).toString();
        // Log.d(LOGTAG, "getAppLabel," + label);
        return label;
    }

    /**
     * 抽取出初始化notifyMap的方法
     *
     * @param context
     */
    public static void initNotifyMap(Context context) {
        if (notifyMap != null && notifyMap.size() < 1) {
            if (!"{}".equals(TuitaSDKManager.getInstance(context).preferences.getString(NOTIFY_MAP, "{}"))) {
                notifyMap = gson.fromJson(TuitaSDKManager.getInstance(context).preferences.getString(NOTIFY_MAP, "{}"), new TypeToken<HashMap<Integer, HashMap<Integer, Integer>>>() {
                }.getType());
            }
        }
    }

    private static String getBroadcastAction(Context context) {
        String action = "com.tuita.sdk.action.souyue.im";
        Log.i(LOGTAG, "getBroadcastAction=" + action);
        return action;
    }

    private static int getPushIconId(Context context) {
        int id = context.getApplicationInfo().icon;// ;context.getResources().getIdentifier("push",
        // "drawable",
        // context.getPackageName());
        if (id <= 0) {
            id = android.R.drawable.sym_def_app_icon;
        }
        Log.i(LOGTAG, "getPushIconId," + id);
        return id;
    }

    private static ApplicationInfo getAppInfo(Context context) {
        return context.getApplicationInfo();
    }

    private static String showContent(int type, String contentSource, Context context, JSONObject json) {
        String content = null;
        switch (type) {
            case MessageHistory.CONTENT_TYPE_TEXT:     //0文字
                content = contentSource;
                break;
            case MessageHistory.CONTENT_TYPE_VOICE:                      //1
                content = "语音";
                break;
            case MessageHistory.CONTENT_TYPE_IMAGE:                      //2
                content = "图片";
                break;
            case MessageHistory.CONTENT_TYPE_VCARD:                      //3
                content = "[名片]";
                break;
            case MessageHistory.CONTENT_TYPE_TIGER:                      //4
                content = "发来一条消息";
                break;
            case MessageHistory.CONTENT_TYPE_SHARE_TIGER:                //5
                content = "发来一条消息";
                break;
            case MessageHistory.CONTENT_TYPE_INTEREST_SHARE:             //7
                content = "[分享]圈吧";
                break;
            case MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND:        //8
                content = "[兴趣圈邀请]";
                break;
            case MessageHistory.CONTENT_TYPE_SOUYUE_NEWS_SHARE:          //9
                content = "[分享]新闻";
                break;
            case MessageHistory.CONTENT_TYPE_SECRET_MSG:                 //10
                content = "[消息]";
                break;
            case MessageHistory.CONTENT_TYPE_NEW_VOICE:                  //12
                content = "[语音]";  //语音
                break;
            case MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD:           //13
                content = "[名片]兴趣圈";
                break;
            case MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE:    //14添加好友入私密圈吧
                content = "[私密圈邀请]";
                break;
            case MessageHistory.CONTENT_TYPE_NEW_IMAGE:                  //15
                content = "[图片]"; //图片
                break;
            case MessageHistory.CONTENT_TYPE_SRP_SHARE:       //20
                content = "[分享]";
                break;
            case MessageHistory.CONTENT_TYPE_AT_FRIEND:      //21@好友
                content = getAtContent(contentSource, context, json);
                break;
            case MessageHistory.CONTENT_TYPE_GROUP_CARD:     //19群名片
                content = "[名片]";
                break;
            case MessageHistory.CONTENT_TYPE_GIF:     //22 动画表情
                content = "[动画表情]";
                break;
            case MessageHistory.CONTENT_TYPE_SERVICE_MESSAGE_FIRST:     //16 服务号第一种类型
                content = contentSource;
                break;
            case MessageHistory.CONTENT_TYPE_SERVICE_MESSAGE_SECOND:     //17 服务号第二种类型
                content = contentSource;
                break;
            case MessageHistory.CONTENT_TYPE_WEB:     //23 web类型
                content = "[分享]";
                break;
            case MessageHistory.CONTENT_TYPE_RED_PAKETS:     //26 红包
                content = "[搜悦红包]";
                break;
            default:         //默认
                content = "发来一条消息";
                break;
        }

        return content;
    }

    /**
     * 取消通知栏通知，为了支持取消全部通知栏通知功能，（例如用户退出登录） 当 notifyId 为 -1时，
     * 设置清空所有Map中维护的ID
     *
     * @param context
     * @param notifyId
     */
    public static void notificationCancel(Context context, int notifyId) {
        if (notifyId == -1) {
            if (notificationManager == null) {
                notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notificationManager.cancelAll();
        }else if (notifyId == -2){
            for (Integer integer : notifyMap.keySet()){
                HashMap<Integer, Integer> map = notifyMap.get(integer);
                if (map.containsKey(-2)){
                    if (notificationManager != null) {
                        notificationManager.cancel(integer);
                    } else {
                        notificationManager = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(integer);
                    }
                    resetSpecifyId(integer);
                }
            }

        }else {
            if (notificationManager != null) {
                notificationManager.cancel(notifyId);
            } else {
                notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notifyId);
            }
        }

        if (notifyMap != null) {
            resetSpecifyId(notifyId);
        }
        TuitaSDKManager.getInstance(context).preferences.edit().putString(NOTIFY_MAP, gson.toJson(notifyMap)).commit();
    }

    private static String getAtContent(String contentSource, Context context, JSONObject json) {
        AtFriend atFriend = new Gson().fromJson(contentSource, AtFriend.class);
        String newc = "";
        for (int i = 0; i < atFriend.getUsers().size(); i++) {

            try {
                long uid = atFriend.getUsers().get(i).getUid();
                String nickname = atFriend.getUsers().get(i).getNick();
                Contact contact = null;
                contact = ContactDaoHelper.getInstance(context).find(json.getLong("myid"), uid);
                GroupMembers groupMembers = TuitaIMManager.db_findMemberListByGroupidandUid(context, json.getLong("myid"), json.getLong("chat_id"), uid);
                String newname = "";
                if (contact != null && groupMembers != null) {
                    newname = TextUtils.isEmpty(contact.getComment_name()) ? TextUtils.isEmpty(groupMembers.getMember_name()) ?
                            groupMembers.getNick_name() : groupMembers.getMember_name() : contact.getComment_name();
                } else if (groupMembers != null) {
                    newname = TextUtils.isEmpty(groupMembers.getMember_name()) ?
                            groupMembers.getNick_name() : groupMembers.getMember_name();
                }

                newc = atFriend.getC().replaceAll(nickname, newname);
            } catch (JSONException e) {
                e.printStackTrace();
                return "[消息]";
            }
        }
        return newc;
    }


    /**
     * 获取可用的Notification
     *
     * @param context
     * @param soundFlag
     * @param jumpIntent
     * @param showContent
     * @param showName
     * @return
     */
    private static Notification getNotification(Context context, boolean soundFlag, Intent jumpIntent, String showContent, String showName) {
        Notification n;
        if (am == null) {
            am = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);//获取系统声音服务，主要用来获得当前系统声音的设置
        }
        if (am != null) {
            current = am.getStreamVolume(AudioManager.STREAM_RING);
        }
        n = new Notification(getPushIconId(context), showName + ":" + showContent, System.currentTimeMillis());
        n.defaults = Notification.DEFAULT_LIGHTS;
        if (PushService.getEnableSound(context) && soundFlag && current != 0) {
            n.defaults |= Notification.DEFAULT_SOUND;//1
        }
        if (PushService.getEnableVibrate(context) && soundFlag) {
            n.defaults |= Notification.DEFAULT_VIBRATE;//2
        }

        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.setLatestEventInfo(context, showName, showContent, PendingIntent.getBroadcast(context, RANDOM.nextInt(), jumpIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        return n;
    }


    /**
     * 返回所有跳转到IM界面的通知的Intent   、、处理所有从小米点击后走向IM的通知
     * **/
    public static Intent getGotoImNotifyIntent(Context context,long chatId,int chatType){
        Intent serviceMsgIntent = null;
        serviceMsgIntent = new Intent(getBroadcastAction(context));
        serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_CHAT);
        serviceMsgIntent.putExtra(Constants.TARGET_TYPE, chatType);
        serviceMsgIntent.putExtra(Constants.TARGET_ID, chatId);     //取nc某个字段值
        serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
        return serviceMsgIntent;
    }

    /**
     * 返回新朋友界面的Intent
     * @param context
     * @return
     */
    public static Intent getGotoImNewFriendIntent(Context context){
        Intent serviceMsgIntent = null;
        serviceMsgIntent = new Intent(getBroadcastAction(context));
        serviceMsgIntent.putExtra(Constants.TYPE,  Constants.TYPE_NEW_FRIEND);
        serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
        return serviceMsgIntent;
    }

    /**
     * 得到跳到详情页的Intent
     * @param context
     * @param serviceJumpData
     * @param pushFrom
     * @param mid
     * @return
     */
//    public static Intent getGotoDetailPageIntent(Context context,String serviceJumpData,String pushFrom, String mid){
//        Intent serviceMsgIntent = null;
//        serviceMsgIntent = new Intent(ContextUtil.getBroadcastAction(context));
//        serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_GET_DATA);
//        serviceMsgIntent.putExtra(Constants.DATA, serviceJumpData);
//        serviceMsgIntent.putExtra(Constants.PUSH_FROM, pushFrom);
//        serviceMsgIntent.putExtra(Constants.MID, mid);
//        serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
//        return serviceMsgIntent;
//    }

    /**
     * 得到跳转到图集页面的Intent
     * @return
     */
//    public static Intent getGotoImageSetIntent(Context context,String serviceJumpData,String pushFrom, String mid){
//        Intent serviceMsgIntent = null;
//        serviceMsgIntent = new Intent(ContextUtil.getBroadcastAction(context));
//        serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_GET_DATA);
//        serviceMsgIntent.putExtra(Constants.DATA, serviceJumpData);
//        serviceMsgIntent.putExtra(Constants.PUSH_FROM, pushFrom);
//        serviceMsgIntent.putExtra(Constants.MID, mid);
//        serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
//        return serviceMsgIntent;
//    }

    /**
     * 通知栏跳转  逻辑处理
     *
     * @param context
     * @param serviceJumpData
     * @param pushFrom
     * @param mid
     * @return
     * @throws JSONException
     */
    public static Intent sendNotifyBroadCast(Context context, String serviceJumpData, String pushFrom, String mid) throws JSONException {
        String[] serviceJumpDataArr = null;
        if (!TextUtils.isEmpty(serviceJumpData)) {
            serviceJumpDataArr = serviceJumpData.trim().split(",");
        }
        Intent serviceMsgIntent = null;
        if (serviceJumpDataArr != null && serviceJumpDataArr.length > 0) {     //有nc跳转数据处理
            if ("1".equals(serviceJumpDataArr[0])) {    //跳详情页
                serviceMsgIntent = new Intent(ContextUtil.getBroadcastAction(context));
                serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_GET_DATA);
                serviceMsgIntent.putExtra(Constants.DATA, serviceJumpData);
                serviceMsgIntent.putExtra(Constants.PUSH_FROM, pushFrom);
                serviceMsgIntent.putExtra(Constants.MID, mid);
                serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
            } else if ("2".equals(serviceJumpDataArr[0])) {  //跳到会话页
                serviceMsgIntent = new Intent(getBroadcastAction(context));
                serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_CHAT);
                serviceMsgIntent.putExtra(Constants.TARGET_TYPE, IConst.CHAT_TYPE_SERVICE_MESSAGE);
                serviceMsgIntent.putExtra(Constants.TARGET_ID, Long.parseLong(serviceJumpDataArr[1]));     //取nc某个字段值
                serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
            } else if ("3".equals(serviceJumpDataArr[0])) {
                serviceMsgIntent = new Intent(ContextUtil.getBroadcastAction(context));
                serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_GET_DATA);
                serviceMsgIntent.putExtra(Constants.DATA, serviceJumpData);
                serviceMsgIntent.putExtra(Constants.PUSH_FROM, pushFrom);
                serviceMsgIntent.putExtra(Constants.MID, mid);
                serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
            } else if ("5".equals(serviceJumpDataArr[0])) {  //跳到新朋友
                serviceMsgIntent = new Intent(getBroadcastAction(context));
                serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_NEW_FRIEND);
                serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
            } else {     //非1或2 走默认跳转到列表页
                serviceMsgIntent = new Intent(getBroadcastAction(context));
                serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_DEFAULT_LIST);
                serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
            }

        } else {     //无nc跳转逻辑 跳转到列表页
            serviceMsgIntent = new Intent(getBroadcastAction(context));
            serviceMsgIntent.putExtra(Constants.TYPE, Constants.TYPE_SERVICE_LIST);
            serviceMsgIntent.putExtra(Constants.NOTIFY_ID, CHANNEL);
        }
        return serviceMsgIntent;
    }


    /**
     * 获取要闻id
     *
     * @param context
     * @return
     */
    private static int getServiceId(Context context) {
        if (ContextUtil.isSyApp(context)) {
            return 501;
        } else {
            if (ContextUtil.isOnline(context)) {
                return 2071;
            } else {
                return 2650;
            }
        }
    }

}
