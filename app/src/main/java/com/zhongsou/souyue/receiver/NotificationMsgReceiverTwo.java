package com.zhongsou.souyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.google.gson.JsonObject;
import com.google.gson.JsonObject;
import com.tuita.sdk.Constants;
import com.tuita.sdk.ContextUtil;
import com.tuita.sdk.PushService;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.activity.GalleryNewsActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.SplashActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.im.render.MsgSerMsgFirstRender;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.view.ImUIHelpr;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.PushInfo;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.common.UpdateClientId;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationMsgReceiverTwo extends BroadcastReceiver {
	/**
	 * 客户端id
	 */
    private static String LOG_TAG = "NotificationMsgReceiverTwo";
    private static String ACTION_NAME_TUITA_HEAD = "com.tuita.sdk.action.";
    private static String ACTION_NAME_TUITA_TEST = "com.tuita.sdk.action.souyue_1_0";
    private static String ACTION_NAME_TUITA = "com.tuita.sdk.action.souyue";
    private static String CLIENT_ID;

    public static String getClientId() {
        if (StringUtils.isNotEmpty(CLIENT_ID)) {
            return CLIENT_ID;
        } else {
            SharedPreferences sp = MainApplication.getInstance().getSharedPreferences("TuitaSDK", Context.MODE_PRIVATE);
            if (sp != null)
                return sp.getString("tuita_clientID", "");
            else 
                return "";
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean isForeground = Utils.isAppOnForeground(context);// 检查应用是否前台显示运行

        if (ACTION_NAME_TUITA_TEST.equals(action)
                || ACTION_NAME_TUITA.equals(action)
                || (ACTION_NAME_TUITA_HEAD+ ContextUtil.getAppId(context)).equals(action)) {
            LogDebugUtil.v(LOG_TAG, "onReceive come");
            Bundle bundle = intent.getExtras();
            switch (bundle.getInt(Constants.TYPE)) {
                case Constants.TYPE_GET_DATA:
                    Log.d(LOG_TAG,"NotificationMsgReceiver !===! onReceiver" + action);   //test
                    UmengStatisticUtil.onEvent(MainApplication.getInstance(), UmengStatisticEvent.NOTICE_CLICK);  //Umeng 包括极光点击 后期扩展可能问题
                    // 获取透传（payload）数据
                    String data = bundle.getString(Constants.DATA);
                    if (data == null) {
                        break;
                    }
//
//                    if (UIHelper.isEntMsg(data)) {
//                        UIHelper.showPushMsg(context, data);
//                        break;
//                    }
                    if (ImUIHelpr.isIMMsg(data)) {
                        PushService.setMsgNum(MainApplication.getInstance());
                        ImUIHelpr.startIm(context);
                        break;
                    }

                    if("1".equals(bundle.getString(Constants.PUSH_FROM))){    //此时是JPush
                        MobclickAgent.onEvent(context, UmengStatisticEvent.NOTICE_SHOW_CLICK_JPUSH);     //Umeng 统计
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // 帖子推送
                    if (com.zhongsou.souyue.circle.ui.UIHelper.isBlog(data)) {
//                        JSONObject json = JSON.parseObject(data);
//                        JsonObject json = new JsonObject(data);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(data);
                            if (MainApplication.getInstance().isRunning() && isForeground) {
                                ZhongSouActivityMgr.getInstance().goHome();
                                // intent.putExtra("blog_id", json.getLong("blog_id"));
                                // intent.putExtra("interest_id",json.getLong("interest_id"));
                                // intent.setClass(context, PostsActivity.class);
                                // context.startActivity(intent);
                                SearchResultItem item1 = new SearchResultItem();
                                item1.setBlog_id(json.getLong("blog_id"));
                                item1.setInterest_id(json.getLong("interest_id"));
                                IntentUtil.startskipDetailPushPage(context, item1);
                            } else {
                                CircleResponseResultItem interestBlog = new CircleResponseResultItem();
                                interestBlog.setBlog_id(json.getLong("blog_id"));
                                interestBlog.setInterest_id(json.getLong("interest_id"));
                                PushInfo pushInfo = new PushInfo();
                                pushInfo.setJumpType("postsdetail");
                                pushInfo.setInterestBlog(interestBlog);
                                intent.setClass(context, MainActivity.class);
                                intent.putExtra("push_info", pushInfo);
                                context.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    // TODO:接收处理透传（payload）数据
                    String[] content = data.trim().split(",");
                    boolean isGoSrp = false;
                    String md5 = "";
                    String keyword = "";
                    String pushId = "";
                    String g = "";
                    if (StringUtils.isNotEmpty(content[0]) && content[0].equals("1")) { //1跳详情页
                        if (content.length < 3) {
                            break;
                        }
                        isGoSrp = content.length == 3;
                        if (content.length >= 3) {
                            md5 = content[1];
                            keyword = content[2];
                            LogDebugUtil.v(LOG_TAG, "md5=" + md5);
                            LogDebugUtil.v(LOG_TAG, "keyword=" + keyword);
                        }
                        if (content.length >= 4) {
                            pushId = content[3];
                            LogDebugUtil.v(LOG_TAG, "pushId=" + pushId);
                        }
                        if (content.length >= 5) {
                            g = content[4];
                            LogDebugUtil.v(LOG_TAG, "g=" + g);
                        }
                        ImserviceHelp.getInstance().cancelNotify(bundle.getInt(Constants.NOTIFY_ID));//取消通知栏计数
                    }

                    if(StringUtils.isNotEmpty(content[0]) && content[0].equals("m")){
                        if (content.length < 2) {
                            break;
                        }
                        isGoSrp = content.length == 2;
                        if (content.length >= 2) {
                            md5 = content[0];
                            keyword = content[1];
                            LogDebugUtil.v(LOG_TAG, "md5=" + md5);
                            LogDebugUtil.v(LOG_TAG, "keyword=" + keyword);
                        }
                        if (content.length >= 3) {
                            pushId = content[2];
                            LogDebugUtil.v(LOG_TAG, "pushId=" + pushId);
                        }
                        if (content.length >= 4) {
                            g = content[3];
                            LogDebugUtil.v(LOG_TAG, "g=" + g);
                        }
                    }
                    //图集跳转
                    if (StringUtils.isNotEmpty(content[0]) && content[0].equals("3")){
                        BaseInvoke pushPhotosInvoke = new BaseInvoke();
                        pushPhotosInvoke.setType(NotificationMsgJumpReceiver.INVOKDE_TYPE_PUSH_PHOTOS);
                        pushPhotosInvoke.setSrpId(content[1]);
                        HomePagerSkipUtils.skip(context, pushPhotosInvoke);
//                        if(MainApplication.getInstance().isRunning() && isForeground){
//                            intent.putExtra("item", galleryNewsHomeBean);
//                            intent.setClass(context, GalleryNewsActivity.class);
//                            context.startActivity(intent);
//                            break;
//                        }else {
//                            ImUIHelpr.startAtlas(context,galleryNewsHomeBean);
//                            break;
//                        }
                        break;
                    }
                    LogDebugUtil.v(LOG_TAG, "MainApplication.getInstance().isRunning()=" + MainApplication.getInstance().isRunning());
                    if (MainApplication.getInstance().isRunning() && isForeground) {
                        LogDebugUtil.v(LOG_TAG, "isGoSrp=" + isGoSrp);
                        intent.putExtra("isfrompush", true);
                        if (isGoSrp) {// 跳srp页
                            intent.setClass(context, SRPActivity.class);
                            intent.putExtra("keyword", keyword);
                            context.startActivity(intent);
                        } else {
                            SearchResultItem resultItem = new SearchResultItem();
                            Bundle bundleRes = new Bundle();
                            resultItem.keyword_$eq(keyword);
                            try {
                                resultItem.pushId_$eq(Long.parseLong(pushId));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            resultItem.setStatisticsJumpPosition(MsgSerMsgFirstRender.STATISTICS_JUMP_POSITION_NOTIFICATION_BAR);
                            resultItem.setPushFrom(bundle.getString(Constants.PUSH_FROM));
                            resultItem.setClickFrom(MsgSerMsgFirstRender.CLICK_FROM_NOTICE);
                            resultItem.setMsgId(bundle.getString(Constants.MID));
                            bundleRes.putSerializable("searchResultItem", resultItem);
                            intent.putExtras(bundleRes);
                            if (!StringUtils.isEmpty(g) && g.equals("0")) {// 原网页中打开
                                intent.setClass(context, WebSrcViewActivity.class);
                                context.startActivity(intent);
                            } else {
                                IntentUtil.startskipDetailPushPage(context, resultItem);
                            }
                        }
                    } else {
                        intent.setClass(context, SplashActivity.class);
                        intent.putExtra("g", g);
                        intent.putExtra("md5", md5);
                        intent.putExtra("keyword", keyword);
                        intent.putExtra("pushId", pushId);
                        intent.putExtra("pushFrom",bundle.getString(Constants.PUSH_FROM));
                        intent.putExtra("clickFrom",MsgSerMsgFirstRender.CLICK_FROM_NOTICE);
                        intent.putExtra("mid",bundle.getString(Constants.MID));
                        context.startActivity(intent);
                    }
                    break;
                // 个推下行的设备号，将设备号和登录用户uid进行关联
                case Constants.TYPE_GET_CLIENTID:
                    String clientId = bundle.getString(Constants.DATA);
                    if (StringUtils.isNotEmpty(clientId)) {
                        CLIENT_ID = clientId;
                    }
                    new UpdateClientId(HttpCommon.UPDATECLIENT_REQUEST_ID,null).setParams("add");
                    UpdateClientId updateClientid = new UpdateClientId(HttpCommon.UPDATECLIENT_REQUEST_ID, null);
                    updateClientid.setParams("add");
                    CMainHttp.getInstance().doRequest(updateClientid);
                    Log.d("GETUI", clientId);
                    break;
                default:
                    break;
            }
        }
    }
}