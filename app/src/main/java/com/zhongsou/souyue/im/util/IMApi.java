package com.zhongsou.souyue.im.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.NewFriend;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.ac.CreateGroupInviteActivity;
import com.zhongsou.souyue.im.ac.GroupInfomationActivity;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.ac.ImModifyNoteName;
import com.zhongsou.souyue.im.ac.NewFriendsActivity;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ImRequestDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.Serializable;

/**
 * Created by zhangwb on 14-11-5.
 *
 * Description:外部调用IM工具类
 */
public class IMApi {

    //类型为个人中心跳转类型
    private static final int IM_SELF = 1;//自己
    private static final int IM_CHAT = 2;//已经是好友,直接聊天
    private static final int IM_ADD = 3;//非好友，加好友
    private static final int IM_BACK = 4;//通知个人中心是好友且finish界面

    private static final int CODE_ALTER_COMMENT_NAME = 1;//备注名返回码

    /**
     * 兴趣圈个人页获取button状态
     * @param viewerUid
     */
    public static int getPersonStatus(long viewerUid){
        if (viewerUid != 0) {
            Contact c = ImserviceHelp.getInstance().db_getContactById(viewerUid);
            if(SYUserManager.getInstance().getUserId().equals(viewerUid+"")){
                return IM_SELF;
            }
            if (c != null){
                return IM_CHAT;
            } else{
                return IM_ADD;
            }
        }
        return IM_ADD;
    }

    /**
     * 兴趣圈个人页跳转IM私聊页
     * @param activity
     * @param viewerUid
     */
    public static void personGotoIMChat(Activity activity,long viewerUid){
        if (viewerUid != 0) {
            IMChatActivity.invoke(activity, IConst.CHAT_TYPE_PRIVATE, viewerUid);
        }
    }

    /**
     * 兴趣圈个人页跳转IM私聊页
     * @param activity
     * @param viewerUid
     */
    public static void testPersonGotoIMChat(Activity activity,long viewerUid){
        if (viewerUid != 0) {
            Contact c = ImserviceHelp.getInstance().db_getContactById(viewerUid);
            //获取最近聊天记录
            MessageRecent messageRecent = ImserviceHelp.getInstance().db_findMessageRecent(viewerUid);
            Intent intent = new Intent();
            intent.setClass(activity, IMChatActivity.class);
            intent.putExtra(IMChatActivity.KEY_CONTACT, c);
            if(null != messageRecent) {
                intent.putExtra("draftContent", messageRecent.getDrafttext());
                intent.putExtra("draftForAtContent", messageRecent.getDraftforat());
            }
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
//            activity.finish();
        }
    }

    /**
     * 兴趣圈个人页加好友
     * @param activity
     * @param viewerUid
     */
    public static void addFriend(final Activity activity, final long viewerUid){
        ImDialog.Builder build = new ImDialog.Builder(activity);
//        build.setEditMsg(R.string.im_dialog_txt_num);
        build.setEditShowMsg("我是"+ SYUserManager.getInstance().getName());
        build.setTitle(R.string.im_dialog_title);
        build.setPositiveButton(R.string.im_dialog_send, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, final View v) {
                //ImserviceHelp.getInstance().im_userOp(1, contactItem.getChat_id(), contactItem.getNick_name(), contactItem.getAvatar(), v.getTag().toString());
                //更新好友状态：添加-->等待验证
                boolean opFlag = false;

                ImRequestDialog mDialog = new ImRequestDialog(activity);
                mDialog.show();
                if(CMainHttp.getInstance().isNetworkAvailable(activity)){
                    opFlag = ImserviceHelp.getInstance().im_userOp(1, viewerUid, "", "", v.getTag().toString(),3);
                    if(opFlag){
                        mDialog.mDismissDialog();
                        SouYueToast.makeText(activity, activity.getString(R.string.im_send_success), Toast.LENGTH_SHORT).show(); //发送成功不提示
                    }else{
                        mDialog.mDismissDialog();
                        SouYueToast.makeText(activity, activity.getString(R.string.im_server_busy), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mDialog.mDismissDialog();
                    SouYueToast.makeText(activity, activity.getString(R.string.im_net_unvisiable), Toast.LENGTH_SHORT).show();
                }
                //更新新朋友状态
                if(opFlag) {
                    Contact contact = new Contact();
                    contact.setChat_id(viewerUid);
                    Intent data = new Intent();
                    data.setAction(NewFriendsActivity.IM_ALLOW_ADD_ME_ACTION);
                    data.putExtra(NewFriendsActivity.IM_ALLOW_ADD_ME_DATA, contact);
                    data.putExtra(NewFriendsActivity.IM_ALLOW_ADD_ME_STATUS, 1);
                    activity.sendBroadcast(data);
                }
            }
        }).create().show();
    }

    /**
     * 兴趣圈个人页发送个人名片
     * @param activity
     * @param viewerUid
     */
    public static void sendCard(Activity activity,long viewerUid){
       Contact c = ImserviceHelp.getInstance().db_getContactById(viewerUid);
        Intent intent = new Intent(activity, IMShareActivity.class);
        Bundle bundle=new Bundle();
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putSerializable("contact", c);
        bundle.putBoolean(ContactsListActivity.FROMFRIENDINFOACTIVITY, true);
        intent.putExtras(bundle);
        activity.startActivity(intent);

    }

    /**
     * 兴趣圈个人页更改好友名称
     * @param activity
     * @param viewerUid
     */
    public static void modifyNoteName(Activity activity,long viewerUid){
        Contact c = ImserviceHelp.getInstance().db_getContactById(viewerUid);
        String name = TextUtils.isEmpty(c.getComment_name()) ? c.getNick_name() : c.getComment_name();
        Intent intent = new Intent();
        intent.setClass(activity, ImModifyNoteName.class);
        intent.putExtra(ImModifyNoteName.TAG, name);
        intent.putExtra(ImModifyNoteName.UID, c.getChat_id());
        activity.startActivityForResult(intent, CODE_ALTER_COMMENT_NAME);
    }

    /**
     * 兴趣圈个人页删除好友
     * @param activity
     * @param viewerUid
     */
    public static void deleteFriend(final Activity activity,long viewerUid, final int from){
        final Contact c = ImserviceHelp.getInstance().db_getContactById(viewerUid);
        ImDialog.Builder build = new ImDialog.Builder(activity);
        String digName =c.getNick_name();
        build.setMessage("确认与 " + digName + " 一刀两断吗？");
        build.setPositiveButton(R.string.im_dialog_ok, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                if(CMainHttp.getInstance().isNetworkAvailable(activity)) {
                    ImserviceHelp.getInstance().im_userOp(4, c.getChat_id(), c.getNick_name(), c.getAvatar(), "");
                    c.setStatus(Contact.STATUS_IS_SOUYUE_USER_NOT_FRIEND);
                    c.setComment_name(null);
                    activity.finish();
                    if (from == PersonPageParam.FROM_IM || from == PersonPageParam.FROM_SINGLE_CHAT) {
                        IMIntentUtil.gotoMainActivity(activity);
                    }
                }else {
                    SouYueToast.makeText(activity, activity.getResources().getString(R.string.im_chat_delete_failed), SouYueToast.LENGTH_SHORT).show();
                }
            }
        }).create().show();
    }

    /**
     * 搜悦私聊界面跳转到兴趣圈 个人中心界面
     * @param activity
     * @param contactInfo
     */
//    public static void IMChatGotoShowPersonPage(Activity activity,Contact contactInfo){
//        PersonPageParam param = new PersonPageParam();
//        param.setViewerUid(contactInfo.getChat_id());
//        param.setFrom(PersonPageParam.FROM_IM);
//        param.setShowName(contactInfo.getNick_name());
//        param.setSubName1(contactInfo.getComment_name());
//        param.setSubName2("");
//        UIHelper.showPersonPage(activity,param);
//    }

    /**
     * 跳转兴趣圈个人界面
     *
     * @param activity
     * @param obj
     */
    public static void IMGotoShowPersonPage(Activity activity,Serializable obj,int fromType){

        Contact contactItem = null;
        NewFriend friendItem = null;
        PersonPageParam param = null;
        if (obj instanceof Contact) {
            contactItem = (Contact) obj;
        } else if (obj instanceof NewFriend) {
            friendItem = (NewFriend) obj;
        }

        if (friendItem != null) {
            param = new PersonPageParam();
            param.setViewerUid(friendItem.getChat_id());
            param.setFrom(fromType);
            param.setShowName(friendItem.getNick_name());
            param.setSubName1("");
            param.setSubName2("");
        }

        if (contactItem != null){
            param = new PersonPageParam();
            param.setViewerUid(contactItem.getChat_id());
            param.setFrom(fromType);
            param.setShowName(contactItem.getNick_name());
            param.setSubName1(contactItem.getComment_name());
            param.setSubName2("");
        }

        UIHelper.showPersonPage(activity,param);
    }

    /**
     * 个人中心调用IM获取好友备注名
     * @param viewerUid
     * @return
     */
    public static String getIMCommentName(long viewerUid){
        Contact c = ImserviceHelp.getInstance().db_getContactById(viewerUid);
        if (c == null){
            return "";
        }else{
            return c.getComment_name();
        }
    }


    /**
     * 搜悦调用跳转服务号界面获取数据接口（现用于意见反馈跳转搜小悦）
     * @param serviceId
     * @param cateId
     * @return
     */
    public static ServiceMessageRecent getServiceMessageRecent(long serviceId,long cateId){

        ServiceMessageRecent serviceMessageRecent = ImserviceHelp.getInstance().db_getServiceMessageRecent(serviceId,cateId);
        return serviceMessageRecent;

    }

    /**
     * 兴趣圈点击群聊列表 ----》跳转到群信息页面
     * @param cx
     */
    public static void gotoGroupInfoActivity(Context cx , long group_id,String source){
        Intent appIntent = new Intent(cx, GroupInfomationActivity.class);
        appIntent.putExtra("groupid", group_id);
//        appIntent.putExtra("inviteid", inviteid);
        appIntent.putExtra("mode", 3);
        appIntent.putExtra("source", source);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 兴趣圈点加号  -----《跳转到创建群聊信息界面
     */
    public static void gotoCreateImGroup(Context context) {
        Intent intent = new Intent(context, CreateGroupInviteActivity.class);
        context.startActivity(intent);
    }

}
