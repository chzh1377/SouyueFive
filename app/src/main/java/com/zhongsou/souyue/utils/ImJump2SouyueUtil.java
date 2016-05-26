package com.zhongsou.souyue.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tuita.sdk.im.db.module.IConst;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SearchActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.download.DownloadFileServiceV2;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.render.MsgRedPacketRender;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.ac.SrpWebViewActivity;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog;

/**
 * IM跳转搜悦工具类
 *
 * @author yinguanping
 */
public class ImJump2SouyueUtil {

    private static ImJump2SouyueUtil imJump2SouyueUtil;
    public static final int JSNOBACK = 0;
    public static final int JSLOGINBACK = 1;
    public static final int JSRECHARGEBACK = 2;
    public static final int IM_UPDATE= 3;
    private SearchResultItem mSearchResultItem;
    private static DisplayImageOptions options;
    private static ImageLoader imageLoader;

    static {
        options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(false).build();
        imageLoader = ImageLoader.getInstance();
    }

    public synchronized static ImJump2SouyueUtil getInstance() {
        if (imJump2SouyueUtil == null) {
            imJump2SouyueUtil = new ImJump2SouyueUtil();
        }
        return imJump2SouyueUtil;
    }

    public void jump(final Context context, String str, String statisticsJumpPosition, String pushInfoData,String clickFrom,String mid) {
        UmengStatisticUtil.onEvent(context, UmengStatisticEvent.IM_SERVICE_ITEM_CLICK);   //Umeng
//        JSClick jsClick = JSON.parseObject(str, JSClick.class);
        JSClick jsClick = new Gson().fromJson(str, JSClick.class);
        jsClick.init();
        if (jsClick.isPasePage()) {
            mSearchResultItem = new SearchResultItem();
            String pushId = "";
            if (pushInfoData != null) {
                String[] content = pushInfoData.trim().split(",");

                //当nc的type等于1的情况
                if (StringUtils.isNotEmpty(content[0]) && content[0].equals("1")) {
                    if (content.length >= 4) {
                        pushId = content[3];
                    }
                }
                //当nc的type等于2的情况
                if (StringUtils.isNotEmpty(content[0]) && content[0].equals("2")) {
                    if (content.length >= 3) {
                        pushId = content[2];
                    }
                }
                //为了融合老版本推送   第一个是m的情况
                if (StringUtils.isNotEmpty(content[0]) && content[0].equals("m")) {
                    if (content.length >= 3) {
                        pushId = content[2];
                    }
                }
            }
            mSearchResultItem.setClickFrom(clickFrom);
            mSearchResultItem.setPushFrom(IConst.PUSH_TYPE_DEFAULT);
            mSearchResultItem.setMsgId(mid);
            mSearchResultItem.setStatisticsJumpPosition(statisticsJumpPosition);
            if (!pushId.equals("")) {
                try {
                    mSearchResultItem.pushId_$eq(Long.parseLong(pushId));
                } catch (Exception e) {   //容错
                    e.printStackTrace();
                }
            }
        } else if (jsClick.isReply()) {
            JSONObject clickJson = null;
            try {
                clickJson = new JSONObject(str);
                CommentsForCircleAndNews commentsForCircleAndNews = new Gson().fromJson(clickJson.getJSONObject("post").toString(), CommentsForCircleAndNews.class);
                String t = clickJson.getString("t");
                String nickName = "";
                boolean isAdmin = false;
                if (t.equals("reply")) {
                    nickName = commentsForCircleAndNews.getNickname();
                    if (commentsForCircleAndNews.getRole() == Constant.ROLE_ADMIN) {
                        isAdmin = true;
                    } else {
                        isAdmin = false;
                    }
                } else {
                    nickName = commentsForCircleAndNews.getBlog_author();
                    isAdmin = clickJson.getBoolean("isAdmin");
                }
                UIHelper.showCommentNewPage((Activity) context,
                        commentsForCircleAndNews,
                        clickJson.getLong("interest_id"),
                        nickName,
                        clickJson.getString("image"),
                        clickJson.getLong("mblog_userId"),
                        clickJson.getInt("isBantank"),
                        clickJson.getInt("operType"),
                        clickJson.getString("srpid"),
                        clickJson.getString("srpword"),
                        clickJson.getString("url"),
                        isAdmin,
                        clickJson.getInt("mCircleType"),
                        commentsForCircleAndNews.getBlog_title(),
                        commentsForCircleAndNews.getBlog_author(),
                        commentsForCircleAndNews.getCreate_time(),
                        commentsForCircleAndNews.getBlog_id(), true

                );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (jsClick.isAtlas()){
            mSearchResultItem = new SearchResultItem();
            mSearchResultItem.setPushFrom(IConst.PUSH_TYPE_DEFAULT);
            mSearchResultItem.setClickFrom(clickFrom);
            mSearchResultItem.setMsgId(mid);
            mSearchResultItem.setStatisticsJumpPosition(statisticsJumpPosition);
        }
        int serviceReturnCode = IMAndWebJump(context, jsClick, mSearchResultItem);
        if (serviceReturnCode == IM_UPDATE){
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
            aBuilder.setMessage("您的搜悦版本过低，需要升级使用该功能");
            aBuilder.setPositiveButton("暂不", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            aBuilder.setNegativeButton("升级", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainApplication) ((Activity) context).getApplication()).checkVersion(2);
                }
            });
            aBuilder.create();
            aBuilder.show();
        }

    }

    public static int IMAndWebJump(final Context context, JSClick jsClick, SearchResultItem sri) {
        if (jsClick.isInterest()) {// 圈子
            try {
                processInterest(context, jsClick);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return JSNOBACK;
        } else if (jsClick.isSrp()) {// srp
            IntentUtil.gotoSrp(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isAlt()) {// @我的列表
            UIHelper.showAtMeSetting((Activity) context, Long.parseLong(jsClick.getInterest_id()));
            return JSNOBACK;
        } else if (jsClick.isCommentType()) {// 回复我的列表
            IntentUtil.gotoReplyMe(context, jsClick.getInterest_id());
            return JSNOBACK;
//        }else if (jsClick.isSlotMachine()) {// 老虎机
//            IntentUtil.toSlotMachine(context);
//            return JSNOBACK;
        } else if (jsClick.isInteractWeb()) {// 中搜币商城
            IntentUtil.gotoWeb(context, jsClick.url(), "interactWeb");
            return JSNOBACK;
        } else if (jsClick.isClose()) {
            ((Activity) context).finish();
            ((Activity) context).overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return JSNOBACK;
        } else if (jsClick.isGoLogin()) {
            if (!jsClick.isAutoReturn()) {
                ZhongSouActivityMgr.getInstance().goHome();//退回到首页
            }
            return IntentUtil.toLogin(context);
        } else if (jsClick.isRecharge()) {
            return toRecharge(context, jsClick);
        } else if (jsClick.isAskfor()) {
            return toAskfor(context, jsClick);
        } else if (jsClick.isExchange()) {
            return toExchange(context);
        } else if (jsClick.isApp()) {// 移动应用宝典
            boolean isInstall = ActivityUtils.isIntentAvailable(context,
                    ConstantsUtils.ACTION_APPBIBLE);
            if (isInstall) {
                // 跳转到应用宝典
                IntentUtil.toAPPbible(context);
            } else {
                IntentUtil.gotoWeb(context, UrlConfig.bible, "");
            }
            return JSNOBACK;
        } else if (jsClick.isDiscount()) {// 移动商街首页
//            IntentUtil.gotoDiscount(context);
            return JSNOBACK;
        } else if (jsClick.isBlog()) {// 兴趣圈帖子
//            UIHelper.showPostsDetail(context, Long.valueOf(jsClick.getBlog_id()), Long.valueOf(jsClick.getInterest_id()));
            SearchResultItem item1 = new SearchResultItem();
            item1.setBlog_id(Long.valueOf(jsClick.getBlog_id()));
            item1.setInterest_id(Long.valueOf(jsClick.getInterest_id()));
            IntentUtil.startskipDetailPage(context, item1);
            return JSNOBACK;
        } else if (jsClick.isShowimage()) { //点击查看图片 查看大图
            IntentUtil.toShowImage(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isZSBclose()) {
            IntentUtil.ZSBClose(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isOpenapp()) {
            IntentUtil.openAPP(context, jsClick.appname());
            return JSNOBACK;
        } else if (jsClick.isBrowser()) {
            IntentUtil.toBrowser(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isTel()) {
            IntentUtil.toTel(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isOriginal()) {
            IntentUtil.toStartSrcPage(context, jsClick, sri);
            return JSNOBACK;
        } else if (jsClick.isWebView()) {
            IntentUtil.skipDetailPage(context, jsClick, 0, null, 0,null,null,null);
            return JSNOBACK;
        } else if (jsClick.isPasePage()) {
            if (sri != null && sri.getmOptionRoleType() == Constant.ROLE_NONE
                    && sri.getInterestType() != null && sri.getInterestType().equals("1")) {
                IntentUtil.gotoSecretCricleCard(context, sri.getInterest_id(), 0);
            } else if (sri != null && sri.getStatisticsJumpPosition() != null) {
                IntentUtil.skipDetailPage(context, jsClick, 0, sri.getStatisticsJumpPosition(),
                        sri.pushId(),sri.getMsgId(),sri.getClickFrom(),sri.getPushFrom());
            } else {
                IntentUtil.skipDetailPage(context, jsClick, 1002, null, 0,null,null,null);
            }
            return JSNOBACK;
        } else if (jsClick.isLocation()) {
            return JSNOBACK;
        } else if (jsClick.isMySubscribe()) {
            if (jsClick.getType().equals("interest")) {//我的订阅-兴趣圈
                IntentUtil.toMySubscribe(context, R.string.manager_grid_insterest);
            } else if (jsClick.getType().equals("theme")) {//我的订阅-主题
                IntentUtil.toMySubscribe(context, R.string.manager_grid_subject);
            } else if (jsClick.getType().equals("press")) {//我的订阅-报刊
                IntentUtil.toMySubscribe(context, R.string.manager_grid_rss);
            }
            return JSNOBACK;
        } else if (jsClick.isSubscribe()) {
            if (jsClick.getType().equals("interest")) {//订阅-兴趣圈
                IntentUtil.toSubscribe(context, R.string.manager_grid_insterest);
            } else if (jsClick.getType().equals("theme")) {//订阅-主题
                IntentUtil.toSubscribe(context, R.string.manager_grid_subject);
            } else if (jsClick.getType().equals("press")) {//订阅-报刊
                IntentUtil.toSubscribe(context, R.string.manager_grid_rss);
            }
            return JSNOBACK;
        } else if (jsClick.isSearch()) {//搜索
            IntentUtil.openSearchActivity((Activity) context);
            return JSNOBACK;
        } else if (jsClick.isScan()) {//扫一扫
            IntentUtil.toScaning(context);
            return JSNOBACK;
        } else if (jsClick.isSouyueUserCenter()) {//个人中心
            PersonPageParam personPageParam = new PersonPageParam();
            personPageParam.setViewerUid(Long.parseLong(SYUserManager.getInstance().getUserId().trim()));
            personPageParam.setFrom(0);
            UIHelper.showPersonPage((Activity) context, personPageParam);
            return JSNOBACK;
        } else if (jsClick.isRegister()) {//注册
            IntentUtil.toRegister((Activity) context);
            return JSNOBACK;
        } else if (jsClick.isImAddressBook()) {//IM通讯录AddressBook
            IMIntentUtil.gotoContactListActivity((Activity) context);
            return JSNOBACK;
        } else if (jsClick.isCommentPage()) {//详情评论
            IntentUtil.toComment(context, sri);
            return JSNOBACK;
        } else if (jsClick.isEmptyWeb()) {
            IntentUtil.toOpenNoTitle(context, jsClick, sri);
            return JSNOBACK;
        } else if (jsClick.isGoHome()) {
            IntentUtil.toSyHome(context, jsClick);//caoyl112755
            return JSNOBACK;

        } else if (jsClick.isRelogin_CommonRegister()) {//普通注册直接登录
            registerSuccess(context, jsClick.getUserInfo(), jsClick.getGuideInfo());
            return JSNOBACK;
        } else if (jsClick.isRelogin_ModifyPwd()) {//修改登录密码成功之后，需要客户端强制退出，重新登录
            SYUserManager.userExitSouYue((Activity) context);
            if (ConfigApi.isSouyue()) {
                IntentUtil.gotoLogin(context);
            } else {
                IntentUtil.gotoLoginIF(context);
            }

            return JSNOBACK;
        }
        /*else if (jsClick.isRelogin_ForgetPwd()) {//忘记密码重新登录
            ZhongSouActivityMgr.getInstance().goHome();//退回到首页
            IntentUtil.gotoLogin(context);
            return JSNOBACK;
        } */
        else if (jsClick.isNavigationWeb()) {//打开新详情
            SouYueToast.makeText(context, R.string.notsupported, SouYueToast.LENGTH_SHORT);
            return JSNOBACK;
        } else if (jsClick.isUpdateSouyue()) {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
            aBuilder.setMessage("您的搜悦版本过低，需要升级使用该功能");
            aBuilder.setPositiveButton("暂不", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            aBuilder.setNegativeButton("升级", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainApplication) ((Activity) context).getApplication()).checkVersion(2);
                }
            });
            aBuilder.create();
            aBuilder.show();
            return JSNOBACK;
        } else if (jsClick.isOpenSearchDialog()) {//
            IntentUtil.openSearchDialog(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isOpenYaowen()) {//
            IntentUtil.gotoSouYueYaoWen(context,"");
            return JSNOBACK;
        } else if (jsClick.isOpenQRCode()) {//
            IntentUtil.openQRCode(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isCopy()) {
            copyToBoard(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isCreateShortcut()) {
            createShortcut(context, jsClick);
            return JSNOBACK;
        } else if (jsClick.isReward()) {     //打赏跳转
            IntentUtil.skipDetailPage(context, jsClick, 0, null, 0,null,null,null);
            return JSNOBACK;
        } else if (jsClick.isSetWallPaper()) { //设置壁纸
            setScreenPaper(context, jsClick);
            return JSNOBACK;
        }else if (jsClick.isReply()){
            return JSNOBACK;
        }else if (jsClick.isAtlas()) {
            IntentUtil.StartGalleryNewsActivity(context, getAtlasBean(jsClick, sri));
            return JSNOBACK;
        }else if (jsClick.isRecommendSub()) {       //推荐订阅
            SubRecommendDialog.showDialog(context,false,jsClick.isPre(),jsClick.getListId());
            return JSNOBACK;
        }else if (jsClick.isSendRedPacket()) {       //发送红包
            IMIntentUtil.redPacketCloseWeb(context,jsClick);
            return JSNOBACK;
        }else if (jsClick.isOpenRedPacket()) {       //打开红包
            MsgRedPacketRender.changeRedPacketStutas(MsgRedPacketRender.mOpenRedPacket);
            //改变该红包的状态
            return JSNOBACK;
        }else {
            return IM_UPDATE;
        }
    }

//
//    public static int WebJump(final Context context, JSONObject _json) throws JSONException {
//        String category = _json.getString("category");
//        if (category.equals("interest")) {// 圈子
//            toInterest(context, _json);
//            return JSNOBACK;
//        } else if (category.equals("srp")) {// srp
//            IntentUtilWeb.gotoSrp(context, _json);
//            return JSNOBACK;
//        } else if (category.equals("alt")) {// @我的列表
//            UIHelper.showAtMeSetting((Activity) context, _json.getLong("interest_id"));
//            return JSNOBACK;
//        }  else if (category.equals("CommentType")) {// 回复我的列表
//            IntentUtil.gotoReplyMe(context, jsClick.getInterest_id());
//            return JSNOBACK;
//        }else if (category.equals("slotMachine")) {// 老虎机
//            IntentUtil.toSlotMachine(context);
//            return JSNOBACK;
//        } else if (category.equals("interactWeb")) {// 中搜币商城
//            IntentUtil.gotoWeb(context, jsClick.url(), "interactWeb");
//            return JSNOBACK;
//        } else if (category.equals("close")) {
//            ((Activity) context).finish();
//            return JSNOBACK;
//        } else if (category.equals("login")) {
//            if (!jsClick.isAutoReturn()) {
//                ZhongSouActivityMgr.getInstance().goHome();//退回到首页
//            }
//            return IntentUtil.toLogin(context);
//        } else if (category.equals("recharge")) {
//            return toRecharge(context, jsClick);
////        } else if (jsClick.isInteractWeb()) {
////            IntentUtil.gotoWeb(context, jsClick.url(), "interactWeb");
////            return JSNOBACK;
//        } else if (jsClick.isAskfor("askfor")) {
//            return toAskfor(context, jsClick);
//        } else if (jsClick.isExchange("exchange")) {
//            return toExchange(context);
//        } else if (jsClick.isApp("app")) {// 移动应用宝典
//            boolean isInstall = ActivityUtils.isIntentAvailable(context,
//                    NewHomeActivity.ACTION_APPBIBLE);
//            if (isInstall) {
//                // 跳转到应用宝典
//                IntentUtil.toAPPbible(context);
//            } else {
//                IntentUtil.gotoWeb(context, UrlConfig.bible, "");
//            }
//            return JSNOBACK;
//        } else if (jsClick.isDiscount("discount")) {// 移动商街首页
//            IntentUtil.gotoDiscount(context);
//            return JSNOBACK;
//        } else if (jsClick.isBlog("blog")) {// 兴趣圈帖子
//            UIHelper.showPostsDetail(context, Long.valueOf(jsClick.getBlog_id()), Long.valueOf(jsClick.getInterest_id()));
//            return JSNOBACK;
//        } else if (jsClick.isShowimage("showimage")) {
//            IntentUtil.toShowImage(context, jsClick);
//            return JSNOBACK;
//        } else if (jsClick.isZSBclose("ZSBclose")) {
//            IntentUtil.ZSBClose(context, jsClick);
//            return JSNOBACK;
//        } else if (jsClick.isOpenapp("openapp")) {
//            IntentUtil.openAPP(context, jsClick.appname());
//            return JSNOBACK;
//        } else if (jsClick.isBrowser("browser")) {
//            IntentUtil.toBrowser(context, jsClick);
//            return JSNOBACK;
//        } else if (jsClick.isTel("tel")) {
//            IntentUtil.toTel(context, jsClick);
//            return JSNOBACK;
////        } else if (jsClick.isOriginal()) {
////            IntentUtil.toStartSrcPage(context, jsClick, sri);
////            return JSNOBACK;
////        } else if (jsClick.isWebView()) {
////            IntentUtil.toWebView(context, jsClick, sri);
////            return JSNOBACK;
////        } else if (jsClick.isPasePage()) {
////            IntentUtil.toWebView(context, jsClick, sri);
////            return JSNOBACK;
//        } else if (jsClick.isLocation("location")) {
//            IntentUtil.toLocation(context, jsClick);
//            return JSNOBACK;
//        } else if (jsClick.isMySubscribe("mySubscribe")) {
//            if (jsClick.getType().equals("interest")) {//我的订阅-兴趣圈
//                IntentUtil.toMySubscribe(context, R.string.manager_grid_insterest);
//            } else if (jsClick.getType().equals("theme")) {//我的订阅-主题
//                IntentUtil.toMySubscribe(context, R.string.manager_grid_subject);
//            } else if (jsClick.getType().equals("press")) {//我的订阅-报刊
//                IntentUtil.toMySubscribe(context, R.string.manager_grid_rss);
//            }
//            return JSNOBACK;
//        } else if (jsClick.isSubscribe("subscribe")) {
//            if (jsClick.getType().equals("interest")) {//订阅-兴趣圈
//                IntentUtil.toSubscribe(context, R.string.manager_grid_insterest);
//            } else if (jsClick.getType().equals("theme")) {//订阅-主题
//                IntentUtil.toSubscribe(context, R.string.manager_grid_subject);
//            } else if (jsClick.getType().equals("press")) {//订阅-报刊
//                IntentUtil.toSubscribe(context, R.string.manager_grid_rss);
//            }
//            return JSNOBACK;
//        } else if (jsClick.isSearch("search")) {//搜索
//            IntentUtil.openSearchActivity((Activity) context);
//            return JSNOBACK;
//        } else if (jsClick.isScan("scan")) {//扫一扫
//            IntentUtil.toScaning(context);
//            return JSNOBACK;
//        } else if (jsClick.isSouyueUserCenter("souyueUserCenter")) {//个人中心
//            PersonPageParam personPageParam = new PersonPageParam();
//            personPageParam.setViewerUid(Long.parseLong(SYUserManager.getInstance().getUserId().trim()));
//            personPageParam.setFrom(0);
//            UIHelper.showPersonPage((Activity) context, personPageParam);
//            return JSNOBACK;
//        } else if (jsClick.isRegister("register")) {//注册
//            IntentUtil.toRegister((Activity) context);
//            return JSNOBACK;
//        } else if (jsClick.isImAddressBook("imAddressBook")) {//IM通讯录AddressBook
//            IMIntentUtil.gotoContactListActivity((Activity) context);
//            return JSNOBACK;
//        } else if (jsClick.isCommentPage("commentPage")) {//详情评论
//            IntentUtil.toComment(context, sri);
//            return JSNOBACK;
//        } else if (jsClick.isEmptyWeb("emptyWeb")) {
//            IntentUtil.toOpenNoTitle(context, jsClick, sri);
//            return JSNOBACK;
//        } else if (jsClick.isGoHome("goHome")) {
//            IntentUtil.toSyHome(context, jsClick);//caoyl112755
//            return JSNOBACK;
//
//        } else if (jsClick.isRelogin_CommonRegister("registerSucceed")) {//普通注册直接登录
//            registerSuccess(context, jsClick.getUserInfo());
//            return JSNOBACK;
//        } else if (jsClick.isRelogin_ModifyPwd("relogin")) {//修改登录密码成功之后，需要客户端强制退出，重新登录
//            SYUserManager.userExitSouYue((Activity) context);
//            if(ConfigApi.isSouyue()){
//                IntentUtil.gotoLogin(context);
//            }else {
//                IntentUtil.gotoLoginIF(context);
//            }
//
//            return JSNOBACK;
//        }
//        /*else if (jsClick.isRelogin_ForgetPwd()) {//忘记密码重新登录
//            ZhongSouActivityMgr.getInstance().goHome();//退回到首页
//            IntentUtil.gotoLogin(context);
//            return JSNOBACK;
//        } */
//        else if (jsClick.isNavigationWeb("navigationWeb")) {//打开新详情
//            IntentUtil.toOpenTitle(context, jsClick);
//            return JSNOBACK;
//        } else if (jsClick.isUpdateSouyue("updatenote")) {
//            AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
//            aBuilder.setMessage("您的搜悦版本过低，需要升级使用该功能");
//            aBuilder.setPositiveButton("暂不", new OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            aBuilder.setNegativeButton("升级", new OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    ((MainApplication) ((Activity) context).getApplication()).checkVersion(2);
//                }
//            });
//            aBuilder.create();
//            aBuilder.show();
//            return JSNOBACK;
//        }
//        return JSNOBACK;
//    }

//    private static void toInterest(Context context, JSClick jsClick) {
//        if (!TutorialBuilder.isFirstTutorial(
//                SYSharedPreferences.CIRCLE_TUTORIAL_PREFS_NAME, true)) {
//            com.zhongsou.souyue.circle.ui.UIHelper.showCircleIndex(
//                    (Activity) context,
//                    jsClick.srpId(), jsClick.keyword(),
//                    jsClick.getInterest_name(), jsClick.getInterest_logo());
//        } else {
//            IntentUtil.openCircleTitorialActivity(context,
//                    jsClick.srpId(), jsClick.keyword(),
//                    jsClick.getInterest_name(), jsClick.getInterest_logo());
//
//        }
//    }
//    private static void toInterest(Context context, JSONObject _json) throws JSONException {
//        if (!TutorialBuilder.isFirstTutorial(
//                SYSharedPreferences.CIRCLE_TUTORIAL_PREFS_NAME, true)) {
//            com.zhongsou.souyue.circle.ui.UIHelper.showCircleIndex(
//                    (Activity) context,
//                    _json.getString("srpId"), _json.getString("keyword"),_json.getString("interest_name")
//                    , _json.getString("interest_logo"));
//        } else {
//            IntentUtil.openCircleTitorialActivity(context,
//                    _json.getString("srpId"), _json.getString("keyword"),_json.getString("interest_name")
//                    , _json.getString("interest_logo"));
//
//        }
//    }

    private static int toRecharge(Context context, JSClick jsClick) {
        try {
            if (SYUserManager.getInstance().getUser().userType()
                    .equals(SYUserManager.USER_ADMIN)) {
                IntentUtil.gotoPay((Activity) context, EnvConfig.RESULT_CHONGZHI_BACK_SUCCESS);
                return JSRECHARGEBACK;
            } else {
                IntentUtil.toLogin(context);
                return JSLOGINBACK;
            }
        } catch (Exception ex) {
            return JSNOBACK;
        }
    }

    private static int toAskfor(Context context, JSClick jsClick) {
        try {
            if (SYUserManager.getInstance().getUser().userType().equals(SYUserManager.USER_ADMIN)) {
                ContactsListActivity.startSuoyaoAct((Activity) context);
                return JSNOBACK;
            } else {
                IntentUtil.toLogin(context);
                return JSLOGINBACK;
            }
        } catch (Exception ex) {
            return JSNOBACK;
        }
    }

    private static int toExchange(Context context) {
        if (SYUserManager.getInstance().getUser().userType().equals(SYUserManager.USER_ADMIN)) {
            IntentUtil.startExchangeAct(context);
            return JSNOBACK;
        } else {
            IntentUtil.toLogin(context);
            return JSLOGINBACK;
        }
    }


    public static String getIndex(String name) {
        String str = DownloadFileServiceV2.getBookIndexPath(name);
        String str1 = "";
        try {
            RandomAccessFile file = new RandomAccessFile(str, "r");
            byte[] bytes = new byte[(int) file.length()]; // 要读取的个数
            file.read(bytes, 0, (int) file.length());
            str1 = new String(bytes, "gbk");
            Log.e("小说目录", str1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str1;
    }

    public static String getContent(String name, int begin, int offset) {
        String str = DownloadFileServiceV2.getBookContentPath(name);
        String str1 = "";
        try {
            RandomAccessFile file = new RandomAccessFile(str, "r");
            byte[] bytes = new byte[offset]; // 要读取的个数
            file.skipBytes(begin);
            file.read(bytes, 0, offset);
            str1 = new String(bytes, "gbk");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str1;
    }


//    public static void downloadFiction(Context context, String id, String name, String img, String length, String url, String version) {
//        Log.e("小说离线信息", id + "   " + name + "    " + img + "   " + length + "   " + url + "   " + version);
//        int state = 0;
//        //判断version，进行更新
//        if (UIHelper.hasNetwork(context) == 0 && Long.parseLong(version) > SYSharedPreferences.getInstance().getLong(SYSharedPreferences.FICTION_VERSION, -1)) {
//            SYSharedPreferences.getInstance().putLong(SYSharedPreferences.FICTION_VERSION, Long.parseLong(version));
//            context.startService(new Intent(context, DownloadBookZipService.class));
//        }
//        if (!DownloadDao.getInstance(context).isHasInfors(id)) {
//
//            // 删除数据库信息
//            DownloadDao.getInstance(context).delete(id);
//            DownloadFileServiceV2.delFile(DownloadFileServiceV2.getBookIndexPath(id));
//            DownloadFileServiceV2.delFile(DownloadFileServiceV2.getBookContentPath(id));
//
//            DownloadInfo info = new DownloadInfo();
//            info.setName(name);
//            info.setLength(Integer.valueOf(length));
//            info.setOnlyId(id);
//            info.setState(DownloadInfo.STATE_INIT);
//            info.setType(DownloadInfo.DOWNLOAD_TYPE_BOOK);
//            info.setImgUrl(img);
//            info.setUrls(url);  //需要解析？？？
//            DownloadDao.getInstance(context).insertInfos(info);
//        } else {
//            state = DownloadDao.getInstance(context).getInfo(id).getState();
//        }
//        Intent intent = new Intent(context, DownloadActivity.class);
//        intent.putExtra("fileType", DownloadInfo.DOWNLOAD_TYPE_BOOK);
//        intent.putExtra("from", 1);
////        //判断是不是已经下载完成
//        if (state == DownloadInfo.STATE_COMPLETE) {
//            intent.putExtra("downloadstate", 1);
//        } else {
//            intent.putExtra("downloadstate", 0);
//        }
//        context.startActivity(intent);
//    }
//
//    public static void downloadVideo(Context context, String id, String name, String img, String length, String urls) {
//        Log.e("视频离线信息", id + "   " + name + "    " + img + "   " + length + "   " + urls);
//        if (!DownloadDao.getInstance(context).isHasInfors(id)) {
//
//            List<UrlConsume> urlList = null;
//            try {
//                urlList = JSON.parseArray(urls, UrlConsume.class);
//                if (urlList == null || urlList.size() == 0) {
//                    ToastUtil.show(context, "数据格式错误，无法下载");
//                    return;
//                }
//            } catch (Exception e) {
//                return;
//            }
//
//            // 删除数据库信息
//            DownloadDao.getInstance(context).delete(id);
//            for (UrlConsume url : urlList) {
////              DownloadFileServiceV2.delFile(DownloadFileServiceV2.getVideoUrlPath(id, url.getUrl()));
//            }
//
//            DownloadInfo info = new DownloadInfo();
//            info.setName(name);
//            info.setLength(Integer.valueOf(length));
//            info.setOnlyId(id);
//            info.setState(DownloadInfo.STATE_INIT);
//            info.setType(DownloadInfo.DOWNLOAD_TYPE_VIDEO);
//            info.setImgUrl(img);
//            info.setUrls(urls);  //需要解析？？？
//            DownloadDao.getInstance(context).insertInfos(info);
//        }
//        Intent intent = new Intent(context, DownloadActivity.class);
//        intent.putExtra("fileType", DownloadInfo.DOWNLOAD_TYPE_VIDEO);
//        intent.putExtra("from", 1);
//        context.startActivity(intent);
//    }

    /**
     * 注册回调方法
     */
    public static void registerSuccess(Context context, User u, TaskCenterInfo taskCenterInfo) {
        if (u != null) {
            try {
                u.userType_$eq("1");
//            if (SYUserManager.getInstance().getUser() != null) {
//                SYUserManager.getInstance().delUser(SYUserManager.getInstance().getUser());
//            }
//            CookieSyncManager.getInstance().resetSync();
                SYUserManager.getInstance().setUser(u);
//                ImserviceHelp.getInstance().im_logout();
                ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
                new SYInputMethodManager(((Activity) context)).hideSoftInput();
                SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, true);
                Intent i = new Intent(ConstantsUtils.LINK);
                i.putExtra(com.tuita.sdk.Constants.TYPE, 40);
                context.sendBroadcast(i);
                //判断是否赠送饮料
                
                    regSuccess(context);
                UpEventAgent.onReg(context, "其他");
                UpEventAgent.onLogin(context);
            } catch (Exception e) {
                Log.e("registerSuccess", e.getMessage());
            }
        }
        if (taskCenterInfo != null) {
            UserInfoUtils.jumpToFillUser(taskCenterInfo);
        }
    }


    private static void regSuccess(Context context) {
        if (((Activity) context) instanceof WebSrcViewActivity) {
            ((Activity) context).finish();
        }
        if (((MainApplication) ((Activity) context).getApplication()).isFromGameToLogin()) {//区分是否来自老虎机
            Intent mIntent = new Intent("subscribeState");
            context.sendBroadcast(mIntent);
            ((Activity) context).setResult(((Activity) context).RESULT_OK);
        } else {
            IntentUtil.goHomeMine(context);
        }
    }

    public static String getUserInfo(Context context) {
        JSONObject userInfo = new JSONObject();
        User user = SYUserManager.getInstance().getUser();
        boolean islogin = (user != null && user.userType().equals(
                SYUserManager.USER_ADMIN));
        try {
            if (user != null) {
                userInfo.put("userid", user.userId());
                userInfo.put("username", user.userName());
                userInfo.put("sid", user.token());
            }
            userInfo.put("wifi", CMainHttp.getInstance().isWifi(context) ? "1" : "0");
            userInfo.put("anonymous", islogin ? 1 : 0);
            userInfo.put("imei", DeviceUtil.getDeviceId(context));
            userInfo.put("appname", CommonStringsApi.APP_NAME_SHORT);
            userInfo.put("v", DeviceInfo.getAppVersion());
            userInfo.put("type", DeviceInfo.osName);
            userInfo.put("lat", SYSharedPreferences.getInstance().getString(
                    SYSharedPreferences.KEY_LAT, ""));
            userInfo.put("long", SYSharedPreferences.getInstance().getString(
                    SYSharedPreferences.KEY_LNG, ""));
            return userInfo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void processInterest(Context context, JSClick obj) throws JSONException {
        long interestid = Long.valueOf(obj.getInterest_id());
        String interestlogo = obj.getInterest_logo();
        String keyword = obj.keyword();
        String srpid = obj.srpId();
        String type = obj.getType();
        if (("home").equals(type)) {
//            UIHelper.showCircleIndex((Activity)context,srpid,keyword,keyword,interestlogo);
            IntentUtil.gotoCircleIndex(context, srpid, keyword, keyword, interestlogo);
        } else if (("card").equals(type)) {
            IntentUtil.gotoSecretCricleCard(context, interestid, 0);
        } else {
            IntentUtil.gotoSecretCricleCard(context, interestid, 0);
        }
    }

    /**
     * 创建桌面快捷方式
     *
     * @param context
     * @param jsClick
     */
    public static void createShortcut(final Context context, final JSClick jsClick) {
        final Map<String, Object> extras = new HashMap<String, Object>();
        String imgUrl = jsClick.image();
        extras.put("from", "shortcut");
        extras.put("source_url", jsClick.url());

        if (StringUtils.isNotEmpty(imgUrl)) {
            MyImageLoader.imageLoader.displayImage(imgUrl, new ImageView(
                            context), MyImageLoader.mCreateShort,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            loadedImage = ImageUtil.getRoundCornerRect(
                                    loadedImage, 18, true); // 魔数18，圆角半径，采用18比较合适
                            loadedImage = ImageUtil.zoomImg(loadedImage,
                                    CircleUtils.dip2px(context, 45),
                                    CircleUtils.dip2px(context, 45)); // 魔数45,经过测试，比较合适，且对各个大小的屏幕兼容性较好
                            loadShortcutIcon(context, jsClick, extras, loadedImage);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            loadShortcutIcon(context, jsClick, extras, null);

                        }

                    });
        } else {
            loadShortcutIcon(context, jsClick, extras, null);
        }

    }

    private static void loadShortcutIcon(Context context,
                                         JSClick jsClick,
                                         Map<String, Object> extras,
                                         Bitmap loadedImage) {
        Bitmap shortcutIcon = null;
        if (loadedImage == null) {
            shortcutIcon = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.search_shortcut_default_logo);
        } else {
            shortcutIcon = loadedImage;
        }
        ActivityUtils
                .addShortCut(
                        context,
                        "com.zhongsou.souyue.platform.ac.SrpWebViewActivity",
                        shortcutIcon, jsClick.title(),
                        extras);

        callbackJS(context, jsClick);
    }

    private static void callbackJS(Context context, JSClick jsClick) {
        WebView callbackWebView = getCallbackWebview(context);
        if (callbackWebView != null) {
            callbackWebView.loadUrl("javascript:" + jsClick.getCallback() + "()");
        }
    }

    /**
     * 获取回调JS的webview对象
     *
     * @param context
     * @return
     */
    private static WebView getCallbackWebview(Context context) {
        WebView callbackWebView = null;
        if (context != null) {
            if (context instanceof SearchActivity) {
                callbackWebView = ((SearchActivity) context).getmWebView();
            } else if (context instanceof SrpWebViewActivity) {
                callbackWebView = ((SrpWebViewActivity) context).getmWebView();
            }
        }
        return callbackWebView;
    }

    /**
     * 带参数JS回调
     *
     * @param context
     * @param jsClick
     * @param callbackParam
     */
    private static void callbackJS(Context context, JSClick jsClick, String callbackParam) {
        WebView callbackWebView = getCallbackWebview(context);
        if (callbackWebView != null) {
            callbackWebView.loadUrl("javascript:" + jsClick.getCallback() + "('" + callbackParam + "')");
        }
    }

    /**
     * 复制到剪贴板
     *
     * @param context
     * @param jsClick
     */
    @SuppressLint("NewApi")
    public static void copyToBoard(Context context, JSClick jsClick) {
        if (android.os.Build.VERSION.SDK_INT > 11) {
            android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            c.setPrimaryClip(ClipData.newPlainText("", jsClick.getContent()));

        } else {
            android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            c.setText(jsClick.getContent());
        }
        callbackJS(context, jsClick);
    }

    /**
     * 设置壁纸
     *
     * @param activity
     * @param jsClick
     */
    public static void setScreenPaper(final Context activity, final JSClick jsClick) {

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
        if (jsClick != null) {
            String url = jsClick.image();
            if (StringUtils.isEmpty(url)) {
                setWallPaperCallback(activity, jsClick, false);
                return;
            }
            imageLoader.loadImage(url, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              final Bitmap loadedImage) {
                    if (activity != null) {
                        ((Activity) activity).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (null != loadedImage) {
                                        wallpaperManager.setBitmap(loadedImage);
                                        setWallPaperCallback(activity, jsClick, true);
                                    }
                                } catch (Exception e) {
                                    setWallPaperCallback(activity, jsClick, false);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {
                    setWallPaperCallback(activity, jsClick, false);
                }
            });
        }
    }

    public static void setWallPaperCallback(Context activity, JSClick jsClick, boolean state) {
        JSONObject callbackObject = new JSONObject();
        try {
            if (!state) {
                callbackJS(activity, jsClick, callbackObject.put("state", "fail").put("msg", "").toString());
                return;
            }
            callbackJS(activity, jsClick, callbackObject.put("state", "success").put("msg", "").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装 图集信息
     * @param _jsClick
     * @param sri
     * @return
     */
    private static GalleryNewsHomeBean getAtlasBean(JSClick _jsClick,SearchResultItem sri) {
        GalleryNewsHomeBean galleryNewsHomeBean = new GalleryNewsHomeBean();
        galleryNewsHomeBean.setSrpId(_jsClick.srpId());
        galleryNewsHomeBean.setTitle(_jsClick.title());
        galleryNewsHomeBean.setDescription(_jsClick.description());
        galleryNewsHomeBean.setUrl(_jsClick.url());
        String images = _jsClick.image();
        if (!StringUtils.isEmpty(images)) {
            List<String> imgList = new ArrayList<String>();
            String[] imgs = images.split(",");
            for (String img : imgs) {
                imgList.add(img);
            }
            galleryNewsHomeBean.setImage(imgList);
        }
        galleryNewsHomeBean.setSource(_jsClick.getSource());
        galleryNewsHomeBean.setKeyword(_jsClick.keyword());
        galleryNewsHomeBean.setPubTime(_jsClick.getPubTime() + "");
        galleryNewsHomeBean.setMsgId(sri.getMsgId());
        galleryNewsHomeBean.setClickFrom(sri.getClickFrom());
        galleryNewsHomeBean.setPushFrom(sri.getPushFrom());

        return galleryNewsHomeBean;
    }
}
