package com.zhongsou.souyue.im.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.ScaningActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.ac.ChatDetailActivity;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.ac.EditGroupChatNickName;
import com.zhongsou.souyue.im.ac.GroupInfomationActivity;
import com.zhongsou.souyue.im.ac.GroupListActivity;
import com.zhongsou.souyue.im.ac.GroupQRCodeActivity;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.IMNoLoginActivity;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.ac.IMSouYueMessageActivity;
import com.zhongsou.souyue.im.ac.InviteGroupFriendActivity;
import com.zhongsou.souyue.im.ac.NewGroupDetailsActivity;
import com.zhongsou.souyue.im.ac.ShareGroupActivity;
import com.zhongsou.souyue.im.ac.ShowServiceMessageActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoulu
 * on 14-8-22
 * Description:IM Activity跳转工具类
 */
public class IMIntentUtil {
    public final static int REQUEST_CODE = 0x01;
    public final static int MYGROUP_NICKNAME = 0x04;
    public final static int MYGROUP_EDITMSG = 0x03;
    public final static int MYCHAT_EDITMSG = 0x02;
    public final static String NEWS = "news";
    public final static String CIRCLE = "circle";
    public final static String WHERECOMEFROM = "fromwhere";
    public final static int SHAREGROUP = 0x05;
    public final static int SCARD = 0x06;//分享名片到群聊私聊
    public static final int CIRCLESELECTPIC = 0x200;
    public static final int SEND_RED_PACKET = 0x07;

    /**
     * 跳转到邀请群好友界面
     *
     * @param cx
     */
    public static void gotoInviteGroupFriendActivity(Activity cx, long groupId) {
        Intent appIntent = new Intent(cx, InviteGroupFriendActivity.class);
        appIntent.putExtra("groupId", groupId);
        cx.startActivityForResult(appIntent, REQUEST_CODE);
        cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到群二维码
     *
     * @param cx
     */
    public static void gotoGroupQRCode(Context cx, Serializable obj) {
        Intent appIntent = new Intent(cx, GroupQRCodeActivity.class);
        appIntent.putExtra("qrcode", obj);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    /**
     * 跳转到修改群昵称
     *
     * @param cx
     * @param obj
     */
    public static void gotoEditGroupNickName(Activity cx, Serializable obj, String groupname) {
        Intent appIntent = new Intent(cx, EditGroupChatNickName.class);
        appIntent.putExtra("group", obj);
        appIntent.putExtra("groupname", groupname);
        cx.startActivityForResult(appIntent, REQUEST_CODE);
        cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到修改我的群昵称
     *
     * @param cx
     * @param obj
     */
    public static void gotoMyGroupNickName(Activity cx, Serializable obj, String mynickname) {
        Intent appIntent = new Intent(cx, EditGroupChatNickName.class);
        appIntent.putExtra("group", obj);
        appIntent.putExtra("mynickname", mynickname);
        appIntent.putExtra("myGroupName", 1);
        cx.startActivityForResult(appIntent, MYGROUP_NICKNAME);
        cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到群聊页表页面
     *
     * @param cx
     */
    public static void gotoGroupList(Context cx) {
        Intent appIntent = new Intent(cx, GroupListActivity.class);
        ((Activity) cx).startActivityForResult(appIntent, IMIntentUtil.SHAREGROUP);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 扫一扫未登录跳转到该页面
     *
     * @param cx
     */
    public static void gotoNologinActivity(Context cx) {
        Intent appIntent = new Intent(cx, IMNoLoginActivity.class);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到群信息页面
     *
     * @param cx
     */
    public static void gotoGroupInfoActivity(Context cx, long group_id, long inviteid) {
        Intent appIntent = new Intent(cx, GroupInfomationActivity.class);
        appIntent.putExtra("groupid", group_id);
        appIntent.putExtra("inviteid", inviteid);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到用户信息页
     *
     * @param cx
     * @param uid
     */
    public static void gotoIMFriendInfo(Context cx, long uid, long gid, int fromType) {
        GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(gid, uid);

        if (groupMembers == null) {
            Contact c = ImserviceHelp.getInstance().db_getContactById(uid);
            IMApi.IMGotoShowPersonPage((Activity) cx, c, fromType);
        } else {
            Contact c = ImserviceHelp.getInstance().db_getContactById(uid);
            if (c == null) {
                PersonPageParam param = new PersonPageParam();
                param.setViewerUid(groupMembers.getMember_id());
                param.setFrom(PersonPageParam.FROM_IM);
                param.setShowName(groupMembers.getNick_name());
                param.setSubName1("");
                param.setSubName2(groupMembers.getMember_name());
                UIHelper.showPersonPage((Activity) cx, param);
            } else {
                PersonPageParam param = new PersonPageParam();
                param.setViewerUid(groupMembers.getMember_id());
                param.setFrom(PersonPageParam.FROM_IM);
                param.setShowName(c.getNick_name());
                param.setSubName1(c.getComment_name());
                param.setSubName2(groupMembers.getMember_name());
                UIHelper.showPersonPage((Activity) cx, param);

            }
        }
    }

    /**
     * 跳转到群聊界面
     *
     * @param activity
     * @param obj
     */
    public static void gotoGroupChatActivity(Context activity, Group obj, int num) {
        IMChatActivity.invoke((Activity) activity, IConst.CHAT_TYPE_GROUP, obj.getGroup_id());

    }

    /**
     * 跳转到群聊需要finish当前Activity
     *
     * @param activity
     * @param obj
     * @param num
     */
    public static void gotoGroupChatActivity1(Context activity, Group obj, int num) {

        IMChatActivity.invoke((Activity) activity, IConst.CHAT_TYPE_GROUP, obj.getGroup_id());
        ((Activity) activity).finish();

    }

    /**
     * 跳转到服务号界面
     *
     * @param activity
     * @param obj
     */
    public static void gotoServiceMessageActivity(Context activity, Serializable obj) {
        Intent finfo = new Intent(activity, IMSouYueMessageActivity.class);
        finfo.putExtra("cate", obj);
        activity.startActivity(finfo);
        if (activity instanceof Activity) {
            ((Activity) activity).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }


    /**
     * 跳转到群详情页
     *
     * @param activity
     * @param obj
     */
    public static void GroupDetailsActivity(Activity activity, Serializable obj) {
        Intent finfo = new Intent(activity, NewGroupDetailsActivity.class);
        finfo.putExtra(NewGroupDetailsActivity.GROUP_DETAIL, obj);
        activity.startActivityForResult(finfo, MYGROUP_NICKNAME);
        if (activity instanceof Activity) {
            ((Activity) activity).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    /**
     * 跳转到私聊详情页
     *
     * @param activity
     * @param mTargetId
     */
    public static void ChatDetailActivity(Activity activity, long mTargetId) {
        Intent finfo = new Intent(activity, ChatDetailActivity.class);
        finfo.putExtra(ChatDetailActivity.CHAT_DETAIL, mTargetId);

        activity.startActivityForResult(finfo, MYCHAT_EDITMSG);
        if (activity instanceof Activity) {
            ((Activity) activity).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    /**
     * 跳转到服务号cate界面
     *
     * @param activity
     * @param obj
     */
    public static void gotoServiceMessageCateActivity(Context activity, ServiceMessageRecent obj, MessageRecent item) {
        IMChatActivity.invoke((Activity) activity, IConst.CHAT_TYPE_SERVICE_MESSAGE, obj.getService_id());
    }

    /**
     * 　回到主页
     */
    public static void gotoMainActivity(Context cx) {
        Intent appIntent = new Intent(cx, CommonStringsApi.getHomeClass());
        appIntent.putExtra(MainActivity.TAB_TAG_EXTRA, "msg");
        cx.startActivity(appIntent);
        ((Activity) cx).finish();
    }

    /**
     * 跳转到分享内容到群界面
     *
     * @param cx
     */
    public static void gotoShareGroupActivity(Context cx, ImShareNews imShareNews, String flag) {
        Intent appIntent = new Intent(cx, ShareGroupActivity.class);
        appIntent.putExtra(WHERECOMEFROM, flag);
        appIntent.putExtra(ImShareNews.NEWSCONTENT, imShareNews);
        appIntent.putExtra(ContactsListActivity.START_TYPE, false);
        appIntent.putExtra(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SHARE_IMFRIEND);
        appIntent.putExtra(ContactsListActivity.START_FROM, false);
        ((Activity) cx).startActivityForResult(appIntent, SHAREGROUP);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 搜悦IM好友列表
     *
     * @param context
     */
    public static void showImFriend(Activity context, long interest_id, boolean isSYFriend, String interest_logo, String interest_name, Posts mainPosts, int type, boolean isFromBlog, String shareUrl, String srpId, String flag, int from_type) {
        Intent intent = new Intent(context, ShareGroupActivity.class);
        intent.putExtra("interest_id", interest_id);
        intent.putExtra("isSYFriend", isSYFriend);
        intent.putExtra("interest_logo", interest_logo);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("Posts", mainPosts);
        intent.putExtra("fromType", from_type);
        intent.putExtra("type", type);
        intent.putExtra("isFromBlog", isFromBlog);
        intent.putExtra("shareUrl", shareUrl);
        intent.putExtra("srpId", srpId);
        intent.putExtra(WHERECOMEFROM, flag);
        context.startActivityForResult(intent, UIHelper.REQUEST_CODE_IMFRIEND);
    }

    //转发消息多条
    public static void startForwardAct(Activity act, Object editList, String flag) {
        Intent intent = new Intent(act, ShareGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatMsgEntity.FORWARD, (Serializable) editList);
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_FORWARD);
        bundle.putString(WHERECOMEFROM, flag);
        bundle.putBoolean(ContactsListActivity.START_FROM, false);
        intent.putExtras(bundle);
        if (editList instanceof ChatMsgEntity) {
            act.startActivityForResult(intent, IMIntentUtil.SHAREGROUP);
        } else {
            act.startActivityForResult(intent, IMChatActivity.CODE_FORWARD);
        }

    }

    public static void shareIMFriendInfoToGroup(Activity act, Contact contactItem, String flag) {
        Intent intent = new Intent(act, ShareGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SEND_VCARD);
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putSerializable("contact", contactItem);
        bundle.putString(WHERECOMEFROM, flag);
        intent.putExtras(bundle);
        act.startActivityForResult(intent, IMIntentUtil.SHAREGROUP);
        act.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 通讯录跳转服务号列表
     *
     * @param activity
     */
    public static void gotoShowServiceMessageAc(Activity activity) {
        Intent appIntent = new Intent(activity, ShowServiceMessageActivity.class);
        activity.startActivity(appIntent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 分享群名片到私聊群聊
     *
     * @param ac
     * @param group
     */
    public static void gotoShowGroupCardToContactList(Activity ac, Group group) {
//        Intent intent = new Intent(ac, ContactsListActivity.class);
        Intent intent = new Intent(ac, IMShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putSerializable("group_card", group);
        intent.putExtras(bundle);
        ac.startActivity(intent);
    }

    /**
     * 分享个人名片到私聊群聊
     *
     * @param ac
     */
    public static void gotoShowPersionalCardToContactList(Activity ac) {
        Intent intent = new Intent(ac, IMShareActivity.class);
        User user = SouyueAPIManager.getInstance().getUserInfo();
        Contact c = new Contact();
        c.setChat_id(user.userId());
        c.setAvatar(user.image());
        c.setNick_name(user.name());
        c.setMyid(user.userId());
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", c);
        bundle.putBoolean("fromfriendactivity", true);
        intent.putExtras(bundle);
        ac.startActivity(intent);
    }

    public static void gotoShowGroupCardToShareGroup(Activity ac, Group group) {
        Intent intent = new Intent(ac, ShareGroupActivity.class);
        intent.putExtra(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SEND_GROUPCARD);
        intent.putExtra(IMChatActivity.KEY_GET_GROUPCARD_ID, group);
        intent.putExtra(ContactsListActivity.START_TYPE, false);
        intent.putExtra(WHERECOMEFROM, NEWS);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        ac.finish();
    }

    public static void gotoShowGroupCardToShareGroup(Activity ac, Contact contactItem, Group group) {
        Intent intent = new Intent(ac, ShareGroupActivity.class);
        intent.putExtra(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SEND_GROUPCARD);
        intent.putExtra(IMChatActivity.KEY_CONTACT, contactItem);
        intent.putExtra(IMChatActivity.KEY_GET_GROUPCARD_ID, group);
        intent.putExtra(ContactsListActivity.START_TYPE, false);
        intent.putExtra(WHERECOMEFROM, NEWS);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        ac.finish();
    }

    public static void gotoShowCardShareGroup(Activity ac) {
        Intent intent = new Intent(ac, ShareGroupActivity.class);
        intent.putExtra(ContactsListActivity.START_TYPE, false);
        intent.putExtra(ShareGroupActivity.ISSHOWCARD, true);
        intent.putExtra(WHERECOMEFROM, NEWS);
        ac.startActivityForResult(intent, SCARD);
        ac.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到通讯录
     *
     * @param ac
     */
    public static void gotoContactListActivity(Activity ac) {
        Intent intent = new Intent(ac, ContactsListActivity.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    /**
     * 搜悦新闻的点击事件
     */
    public static void souyueNewsClick(Context context, ChatMsgEntity entity) {
        List<String> imageList = new ArrayList<String>();
        imageList.add(entity.getImsharenews().getImgurl());

        SearchResultItem item = new SearchResultItem();
        String shareUrl = entity.getImsharenews().getUrl();
        String keyword = entity.getImsharenews().getKeyword();
        String srpid = entity.getImsharenews().getSrpid();
        String title = entity.getImsharenews().getTitle();

        Intent intent = null;
        // 跳转到新闻详情页
        if (shareUrl != null && !"".equals(shareUrl) && entity.isShareNewsType()) {
            if (shareUrl.contains(context.getResources().getString(R.string.trade_zae_domain))) {
                intent = new Intent(context, WebSrcViewActivity.class);
                intent.putExtra(WebSrcViewActivity.PAGE_URL, shareUrl);
                intent.putExtra(WebSrcViewActivity.PAGE_KEYWORD, keyword);
                context.startActivity(intent);
            } else {
                item.url_$eq(shareUrl);
                item.keyword_$eq(keyword);
                item.srpId_$eq(srpid);
                item.image_$eq(imageList);
                item.title_$eq(title);
                IntentUtil.startskipDetailPage(context, item);
            }
        } else {
            intent = new Intent(context,
                    SRPActivity.class);
            intent.putExtra("keyword", keyword);
            intent.putExtra("srpId", srpid);
            context.startActivity(intent);
        }

    }

    /**
     * 发送红包close
     *
     * @param context
     * @param jsClick
     */
    public static void redPacketCloseWeb(Context context, JSClick jsClick) {
        Intent mIntent = new Intent();
        mIntent.putExtra("content", jsClick.getContent());
        mIntent.putExtra("url", jsClick.url());
        ((Activity) context).setResult(IMIntentUtil.SEND_RED_PACKET, mIntent);
        ((Activity) context).finish();
    }

    /**
     * 跳转到webview  带requestCode
     *
     * @param cx
     * @param url
     * @param type
     */
    public static void gotoWebForResult(Context cx, String url, String type, int requestCode) {
        Intent appIntent = new Intent(cx, WebSrcViewActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, type);
        ((Activity) cx).startActivityForResult(appIntent, requestCode);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 发送红包url跳转
     *
     * @param context
     * @param targetType
     * @param targetId
     */
    public static void gotoSendRedPacketWeb(Context context, int targetType, long targetId) {

        IMIntentUtil.gotoWebForResult(context,
                UrlConfig.getSendRedPacketUrl() +
                        "?sendname=" + SYUserManager.getInstance().getUser().userName() +
                        "&sendid=" + SYUserManager.getInstance().getUserId() +
                        "&redPacketType=" + targetType +
                        "&receiveid=" + targetId
                , "interactWeb", IMIntentUtil.SEND_RED_PACKET);
    }

}
