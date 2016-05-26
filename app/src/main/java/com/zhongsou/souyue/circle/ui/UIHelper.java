package com.zhongsou.souyue.circle.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.CircleCommentNewActivity;
import com.zhongsou.souyue.circle.activity.CircleFriendActivity;
import com.zhongsou.souyue.circle.activity.CircleIMGroupActivity;
import com.zhongsou.souyue.circle.activity.CircleInboxActivity;
import com.zhongsou.souyue.circle.activity.CircleInboxSettingActivity;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.CircleMemberListActivity;
import com.zhongsou.souyue.circle.activity.CircleMemberSettingActivity;
import com.zhongsou.souyue.circle.activity.CircleMyListActivity;
import com.zhongsou.souyue.circle.activity.CircleRecommendInfoActivity;
import com.zhongsou.souyue.circle.activity.EssencePostActivity;
import com.zhongsou.souyue.circle.activity.ImFriendActivity;
import com.zhongsou.souyue.circle.activity.IncludingMePostsActivity;
import com.zhongsou.souyue.circle.activity.InviteNoticeActivity;
import com.zhongsou.souyue.circle.activity.MyPostActivity;
import com.zhongsou.souyue.circle.activity.NewSignatureActivity;
import com.zhongsou.souyue.circle.activity.PersonalCenterActivity;
import com.zhongsou.souyue.circle.activity.PostsForMeActivity;
import com.zhongsou.souyue.circle.activity.PublishActivity;
import com.zhongsou.souyue.circle.activity.RewardsWebViewActivity;
import com.zhongsou.souyue.circle.activity.ShareTofriendActivity;
import com.zhongsou.souyue.circle.model.CircleMemberItem;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.InviteNoticeItem;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.model.ShareContent;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONObject;
//import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 */
public class UIHelper {

    public final static String TAG = "UIHelper";

    public final static int RESULT_OK = 0x700;
    public final static int REQUEST_CODE = 0x01;
    public final static int REQUEST_CODE_GOTO_CARD = 2;
    public final static int REQUEST_CODE_GOTO_CARD_MAIN = 3;
    public final static int REQUEST_CODE_GOTO_COUPON = 4; // 现金券支付
    public final static int REQUEST_CODE_GOTO_SETTING_PAY_PSW = 5; // 安全设置支付密码
    public static final int REQUEST_CODE_GOTO_CITY = 200;
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    public static final int RESULT_CODE_SUBSCRIBE = 100;
    private static final int QR_CODE_SIZE = 300;
    public final static int RESULT_CODE_REPLY = 400;
    public final static int RESULT_CODE_DELETE_BLOG = 401;
    public final static int RESULT_CODE_POSTS = 402;
    public final static int RESULT_CODE_DELETE_MAIN_BLOG = 403;
    public final static int REQUEST_CODE_IMFRIEND = 0x500;
    public final static int RESULT_CODE_IMFRIEND = 0x600;
    public final static int RESULT_CODE_INVITE_FRIEND_OK = 0x900;
    public final static int RESULT_CODE_INVITE_FRIEND_CANCLE = 0x901;
    public final static int REQUEST_CODE_INVITE_FRIEND = 0x902;

    public final static String BOOL_NAME = "/nreaderOffline.zip";

    /**
     * 弹出Toast消息 *
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        if (cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        if (cont == null || msg <= 0) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        if (cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, time).show();
    }

    //跳转到新的回复页
    public static void showCommentNewPage(Activity context, CommentsForCircleAndNews posts,
                                          long interest_id, String nickName, String image,
                                          long mblog_userId, int isBantank, int operType, String srpid,
                                          String srpword, String url, boolean isAdmin, int mCircleType,
                                          String maiTitle, String mainName, String mainTime, long mianBlogId, boolean isFromPush) {
        Intent intent = new Intent(context, CircleCommentNewActivity.class);
        intent.putExtra("post", posts);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("nickName", nickName);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("image", image);
        intent.putExtra("mblog_userId", mblog_userId);
        intent.putExtra("isBantank", isBantank);
        intent.putExtra("operType", operType);
        intent.putExtra("srpid", srpid);
        intent.putExtra("srpword", srpword);
        intent.putExtra("url", url);
        intent.putExtra("mCircleType", mCircleType);
        intent.putExtra(CircleCommentNewActivity.REPLY_MAIN_TITLE, maiTitle);
        intent.putExtra(CircleCommentNewActivity.REPLY_MAIN_NAME, mainName);
        intent.putExtra(CircleCommentNewActivity.REPLY_MAIN_TIME, mainTime);
        intent.putExtra(CircleCommentNewActivity.REPLY_MAIN_BLOG_ID, mianBlogId);
        intent.putExtra(CircleCommentNewActivity.REPLY_IS_FROM_PUSH, isFromPush);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 跳转打赏页面
     *
     * @param context
     * @param mblog_userId
     */
    public static void showRewardsPage(Activity context, long posts_id, long interest_id, long mblog_userId) {
        Intent intent = new Intent(context, RewardsWebViewActivity.class);
        intent.putExtra("blog_id", posts_id);    // posts_id 就是blog_id
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("mblog_userId", mblog_userId);
//    	context.startActivityForResult(intent, REQUEST_CODE);
        context.startActivity(intent);
    }


    /**
     * @param context
     * @param posts
     * @param interest_id
     * @param publish_type 1发布主贴，2编辑主题，3发布跟帖，4编辑跟帖
     */
    public static void showPublish(Activity context, Posts posts, long interest_id, String srp_id, int publish_type, String nickName) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("publish_type", publish_type);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("posts", posts);
        intent.putExtra("nickName", nickName);
        intent.putExtra("is_from_list_publish", true);
        context.startActivityForResult(intent, UIHelper.REQUEST_CODE);
    }

    public static void showPublish(Activity context, Posts posts, long interest_id, String srp_id, int publish_type, String tag_id, String nickName) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("publish_type", publish_type);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("posts", posts);
        intent.putExtra("tag_id", tag_id);
        intent.putExtra("nickName", nickName);
        intent.putExtra("is_from_list_publish", true);
        context.startActivityForResult(intent, UIHelper.REQUEST_CODE);
    }

    public static void showPublish(Activity context, Posts posts, long interest_id, String srp_id, String srpWord, int publish_type, String nickName) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("publish_type", publish_type);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("posts", posts);
        intent.putExtra("srpWord", srpWord);
        intent.putExtra("nickName", nickName);
        intent.putExtra("is_from_list_publish", true);
        context.startActivityForResult(intent, UIHelper.REQUEST_CODE);
    }

    /**
     * 显示精华区列表页
     *
     * @param context
     * @param interest_id
     */
    public static void showEssencePost(Context context, long interest_id) {
        Intent intent = new Intent(context, EssencePostActivity.class);
        intent.putExtra("interest_id", interest_id);
        context.startActivity(intent);
    }

    public static void showInboxPage(Context context, long interest_id) {
        Intent intent = new Intent(context, CircleInboxActivity.class);
        intent.putExtra("interest_id", interest_id);
        context.startActivity(intent);
    }

    public static void showInboxSettingPage(Context context, long interest_id) {
        Intent intent = new Intent(context, CircleInboxSettingActivity.class);
        intent.putExtra("interest_id", interest_id);
        context.startActivity(intent);
    }


    /**
     * 搜悦IM好友列表
     *
     * @param context
     */
    public static void circleInviteFriend(Activity context, long interest_id, boolean isSYFriend, String interest_logo, String interest_name, Posts mainPosts, int type, boolean isFromBlog, String shareUrl, String srpId) {
        Intent intent = new Intent(context, ImFriendActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("isSYFriend", isSYFriend);
        intent.putExtra("interest_logo", interest_logo);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("Posts", mainPosts);
        intent.putExtra("type", type);
        intent.putExtra("isFromBlog", isFromBlog);
        intent.putExtra("shareUrl", shareUrl);
        intent.putExtra("srpId", srpId);
        context.startActivityForResult(intent, REQUEST_CODE_IMFRIEND);
    }

    /**
     * 搜悦IM好友列表（由兴趣圈进入）
     *
     * @param context
     */
    public static void showImFriend(Activity context, long interest_id, boolean isSYFriend, String interest_logo, String interest_name, Posts mainPosts, int type, boolean isFromBlog, String shareUrl, String srpId) {
//        Intent intent = new Intent(context, ImFriendActivity.class);
        Intent intent = new Intent(context, IMShareActivity.class); //所有分享统一走一个界面
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("isSYFriend", isSYFriend);
        intent.putExtra("interest_logo", interest_logo);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("Posts", mainPosts);
        intent.putExtra("type", type);
        intent.putExtra("isFromBlog", isFromBlog);
        intent.putExtra("shareUrl", shareUrl);
        intent.putExtra("srpId", srpId);
        intent.putExtra("fromWhere", IMShareActivity.IM_SHARE_FROM_INSTREST);
        context.startActivityForResult(intent, REQUEST_CODE_IMFRIEND);
    }

    /**
     * 显示收搞箱审核页面
     */
    public static void showRecommendInfo(Activity context, long[] recommendIds, int showPostion, int recommendType) {
        Intent intent = new Intent(context, CircleRecommendInfoActivity.class);
        intent.putExtra("recommendIdList", recommendIds);
        intent.putExtra("showPosition", showPostion);
        intent.putExtra("recommendType", recommendType);
        context.startActivityForResult(intent, 0);
    }

    /**
     * 发帖@圈成员列表
     *
     * @param context
     * @param interest_id
     */
    public static void showCircleFriend(Activity context, long interest_id, ArrayList<CircleMemberItem> selMembers) {
        Intent intent = new Intent(context, CircleFriendActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("selMembers", selMembers);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 从个人中心我发布的帖子
     *
     * @param context
     * @param interest_id
     */
    public static void showCirclePostsForMe(Activity context, String new_srpid, long interest_id, int imStatus, long user_id) {
        Intent intent = new Intent(context, PostsForMeActivity.class);
        intent.putExtra("new_srpid", new_srpid);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("imStatus", imStatus);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到圈成员列表
     */

    public static void showCircleMemberList(Activity context, long interest_id, String interest_logo, String interest_name, int type, String new_srpId) {
        Intent intent = new Intent(context, CircleMemberListActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("interest_logo", interest_logo);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("new_srpId", new_srpId);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 分享帖子给好友
     * <p/>
     * 统计新增，interest_name  因为圈贴分享成功后统计需要发送此字段
     */
    public static void showPostsFriend(Activity context, long posts_id, String token, String interest_name, long interest_id, ShareContent shareContent, boolean isFromPosts) {
        Intent intent = new Intent(context, ShareTofriendActivity.class);
        intent.putExtra("posts_id", posts_id);
        intent.putExtra("token", token);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("isFromPosts", isFromPosts);
        intent.putExtra("ShareContent", shareContent);
        context.startActivity(intent);
    }


    /**
     * 邀请好友留言界面
     */

    public static void showInviteNotice(Activity context, ArrayList<InviteNoticeItem> item, boolean isAdmin, long interest_id) {
        Intent intent = new Intent(context, InviteNoticeActivity.class);
        intent.putExtra("InviteNoticeItem", item);
        intent.putExtra("isAdmin", isAdmin);
        intent.putExtra("interest_id", interest_id);
        context.startActivityForResult(intent, REQUEST_CODE_INVITE_FRIEND);
    }

    /**
     * 兴趣圈进入个人设置
     */
    public static void showCircleMemberSetting(Activity context, long interest_id, int interestType, int requestCode) {
        Intent intent = new Intent(context, CircleMemberSettingActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("interestType", interestType);
        context.startActivityForResult(intent, requestCode);
    }


    /**
     * 进入我发布的帖子
     */
    public static void showMyPost(Activity context, long interest_id) {
        Intent intent = new Intent(context, MyPostActivity.class);
        intent.putExtra("user_id", Long.parseLong(SYUserManager.getInstance().getUserId()));
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("token", SYUserManager.getInstance().getToken());
        context.startActivity(intent);
    }

    /**
     * 进入@我的页面
     */
    public static void showAtMeSetting(Activity context, long interest_id) {
        Intent intent = new Intent(context, IncludingMePostsActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("token", SYUserManager.getInstance().getToken());
        intent.putExtra("oper_type", 2);
        context.startActivity(intent);
    }


    /**
     * 新闻或原创分享到兴趣圈
     *
     * @param context
     * @param shareContent
     */
    public static void shareToInterest(Activity context, com.zhongsou.souyue.circle.model.ShareContent shareContent) {
        Intent intent = new Intent(context, ShareTofriendActivity.class);
        intent.putExtra("ShareContent", shareContent);
        context.startActivity(intent);
    }

    /**
     * 新闻或原创分享到兴趣圈(重载上面的方法)
     *
     * @param context      ctx
     * @param shareContent 分享的内容
     * @param interest_id  兴趣圈id
     */
    public static void shareToInterest(Activity context, com.zhongsou.souyue.circle.model.ShareContent shareContent, long interest_id) {
        Intent intent = new Intent(context, ShareTofriendActivity.class);
        intent.putExtra("ShareContent", shareContent);
        intent.putExtra("interest_id", interest_id);
        context.startActivity(intent);
    }


    /**
     * 进入兴趣圈
     *
     * @param context
     * @param interest_id
     */
//    public static void showInterestCircle(Activity context, long interest_id) {
//        Intent intent = new Intent(context, CircleListPagerActivity.class);
//        intent.putExtra("interest_id", interest_id);
//        context.startActivity(intent);
//    }


    /**
     * 从个人中心进入我订阅的兴趣圈/主题
     *
     * @param context
     * @type 0 主题，1兴趣圈
     */
    public static void showInterestOrSRP(Activity context, int type, long looked_user_id) {
        Intent intent = new Intent(context, CircleMyListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("looked_user_id", looked_user_id);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 进入兴趣圈首页 by bob zhou
     * @param context
     * @param interest_id   兴趣圈Id
     * @param title         兴趣圈标题
     * @param imgUrl        兴趣圈logo
     *//*
    public static void showCircleIndex(Activity context, long interest_id,String title,String imgUrl){
        Intent intent = new Intent(context,CircleIndexActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("title", title);
        intent.putExtra("imgUrl", imgUrl);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_, R.anim.left_out);
    }*/

    /**
     * 进入兴趣圈首页 by bob zhou
     *
     * @param context
     * @param srp_id
     * @param keyword
     * @param interest_name 兴趣圈名称
     * @param interest_logo 兴趣圈logo
     */
    public static void showCircleIndex(Activity context, String srp_id, String keyword, String interest_name, String interest_logo) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        setCircleData(intent, srp_id, keyword, interest_name, interest_logo, null);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void showCircleIndex(Activity context, String srp_id, String keyword, String interest_name, String interest_logo, int requestCode) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        setCircleData(intent, srp_id, keyword, interest_name, interest_logo, null);
        context.startActivityForResult(intent, requestCode);    //带返回结果
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void showCircleIndex(Activity context, String srp_id, String keyword, String interest_name, String interest_logo, String from) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        setCircleData(intent, srp_id, keyword, interest_name, interest_logo, from);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private static void setCircleData(Intent intent, String srp_id, String keyword, String interest_name, String interest_logo, String from){
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("keyword", keyword);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("interest_logo", interest_logo);
        if (from != null) {
            intent.putExtra("from", from);
        }
    }

    /**
     * 进入兴趣圈心情签名页面
     *
     * @param context
     * @param userId  被查看人的user_id
     */
    public static void showMoodSignature(Activity context, long userId, int requestCode) {
        Intent intent = new Intent(context, NewSignatureActivity.class);
        intent.putExtra("userId", userId);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    public interface ChangeDateCallback {
        public void changeDate();
    }

    //获取值通过关键字
    public static String getStr(HttpJsonResponse res, String key) {
        String str = "";
        try {
            str = res.getBody().get(key).getAsString();
        } catch (Exception e) {
            str = "";
        }
        return str;
    }

    public static boolean isBlog(String data) {
        try {
//            JSONObject json = JSON.parseObject(data);
//            return "blog".equals(json.getString("t"));
            JSONObject jo = new JSONObject(data);
            return "blog".equals(jo.getString("t"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获得网络连接是否可用
     *
     * @param context workstate -1，无网络,0，wifi,1，移动网络
     * @return
     */
    public static int hasNetwork(Context context) {
        int workState = -1;

        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if (workinfo == null || !workinfo.isAvailable()) {
            return workState;
        }

        boolean wifiConnected = workinfo.getType() == ConnectivityManager.TYPE_WIFI;
        boolean mobileConnected = workinfo.getType() == ConnectivityManager.TYPE_MOBILE;

        if (mobileConnected && !wifiConnected) {
            workState = 1;
        }

        if (wifiConnected) {
            workState = 0;
        }

        return workState;
    }


    /**
     * 进入个人中心
     */
    public static void showPersonPage(Activity context, PersonPageParam param) {
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.putExtra("param", param);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 进入个人中心
     */
    public static void showPersonPageFromContext(Context context, PersonPageParam param) {
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.putExtra("param", param);
        context.startActivity(intent);
    }
    /**
     * 进入兴趣圈讨论群
     *
     * @param context       ctx
     * @param srp_id        兴趣圈srp_id
     * @param interest_name 兴趣圈名称
     */
    public static void showCircleIMGroup(Activity context, String srp_id, String interest_name) {
        Intent intent = new Intent(context, CircleIMGroupActivity.class);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("interest_name", interest_name);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
