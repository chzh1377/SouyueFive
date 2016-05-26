package com.zhongsou.souyue.im.services;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.Constants;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.TuitaSDKManager;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.NewFriend;
import com.tuita.sdk.im.db.module.ServiceMessage;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.im.aidl.ImAidlService;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

public class Imservice extends Service {
    private TuitaIMManager tmmgr = null;
    private TuitaSDKManager mgr = null;
//    private static final int TRY_CONN_TIME = 5000;
    private IntentFilter linkFilter;
//    private IntentFilter homeFilter;
//    private IntentFilter screenFilter;
//    public boolean appstatus;
    

    
//    private BroadcastReceiver homeReceiver = new  BroadcastReceiver(){  
//        static final String SYSTEM_REASON = "reason";  
//        static final String SYSTEM_HOME_KEY = "homekey";//home key  
//        static final String SYSTEM_RECENT_APPS = "recentapps";//long home key  
//          
//        @Override  
//        public void onReceive(Context context, Intent intent) {  
//            String action = intent.getAction();  
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
//                String reason = intent.getStringExtra(SYSTEM_REASON);  
//                if (reason != null) {  
//                    if (reason.equals(SYSTEM_HOME_KEY)) {  
//                        Log.i("Tuita", "home key click goto im_logout"); 
//                        //调用logout
//                        //记录状态为logout
//                        try {
//                            if (mBinder != null)
//                                mBinder.im_logout();
//                            } catch (Exception e) {
//                        }
//                    } else if (reason.equals(SYSTEM_RECENT_APPS)) {  
//                        Log.i("Tuita", "home key long click");  
//                    }  
//                }  
//            }  
//        }  
//    };
    /**
     * 登陆成功广播
     */
    private BroadcastReceiver linkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                int f = bundle.getInt(Constants.TYPE);
                Log.i("Tuita", "TuitaConnSuccess goto im_connect and f = " + f);
                if (f == Constants.TYPE_GET_CLIENTID || f == Constants.TYPE_LOGIN) {
                    // tuita连接成功im上线
                    if(mBinder != null) {
                        try {
                            if (f == Constants.TYPE_LOGIN) {
                                mBinder.cancelNotify(-1);//登录成功后取消通知栏通知，及map计数
                                mBinder.im_connect(DeviceInfo.getAppVersion());
                            }
                            if (SYUserManager.getInstance().getUserId() != null) {
                                TuitaIMManager.createSouxiaoyue(getApplication(), Long.parseLong(SYUserManager.getInstance().getUserId()));
                            }

                        } catch (RemoteException e) {
                            Logger.e("im", "Imservice.BroadcastReceiver()onReceive", "BroadcastReceiver  mBinder.im_connect Exception ", e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };
//    private BroadcastReceiver screenOffReceiver = new BroadcastReceiver(){
//        public void onReceive(Context context, Intent intent) {
//            Log.i("Tuita", "screenOffReceiver goto im_logout");
//            if(mBinder != null) {
//                try {
//                    if (mBinder != null)
//                        mBinder.im_logout();
//                    } catch (Exception e) {
//                }
//            }
//        };
//    };
    
    public interface ImserviceInterface{
        public void im_connect(String version);//上线 通
//        public boolean appOnline();//程序前台运行
//        public void setAppOnline(boolean status);//程序运行状态。前台，后
        public void im_logout();//IM下线
        public boolean im_search(String keyword);//查找好友 通
        public boolean newGroupOp(int op,List uids);//创建群
        public boolean addOrDeleteGroupMembersOp(int op,String gid,List uids);//加好友入群，或者删除群好友
        public boolean retreatGroupMembersOp(int op,String gid,String nextOwnerId);//退出群
        public boolean saveGroupConfigOp(int op,String gid,boolean isGroupSaved,boolean isNewsNotifyShielded);//保存配置信息
        public boolean updateGroupNickNameOp(int op,String gid,String nick);//修改群昵称或者修改群成员昵称
        public boolean getGroupDetailsOp(int op,String gid);//获取群详细信息
        public boolean getUserOp(int op,long uid);//获取用户并查看是否是好友
//        public boolean im_userOp(int o, long uid, String nick, String avatar, String text);//加好友 通
        public boolean im_userOp(int o, long uid, String nick, String avatar, String text,int originType);
        public boolean im_info(int op, String text);//获得分组，好友列表 调试中
        public boolean im_contacts_status(String contactJson);//手机联系人状态匹配
        public boolean im_contacts_upload(String contactJson);//手机联系人上传
//        public String im_sendMessage(int type, long uidorgid, int contentType, String content, String retry);//发送消息
        public String im_saveMessage(int type, long uidorgid, int contentType, String content, String retry);//发送消息(针对语音，图片等，只存不发)
        public boolean im_update(int op, long uid, String text);//更新各种信息 op 1更新昵称 2更新群里的昵称 3更新好友名称
        public boolean im_giftzsb(long uid, int num);//赠送中搜币 
        
        public List<NewFriend> db_getNewFriend();//本地新朋友
        public List<ServiceMessage> db_getServiceMsg(long cateId);//本地同一个cateId服务号
        public List<ServiceMessageRecent> db_getServiceMsgRe(long cateId);//本地同一个cateId服务号(最近表)
        public List<ServiceMessage> db_getServiceMsgByServiceid(long cateId,long serviceMsgId,int queryType,long session_order);//本地同一个serviceId服务号
        public void db_delNewFriend(long chat_id);//删除新朋友
        public void db_clearNewFriend();//删除全部新朋友
        public List<Contact> db_getContact();
        public List<MessageRecent> db_getMessageRecent();//最近聊天
        public ServiceMessage db_getServiceMessageMessage(String uuid);////by uuid获得服务号信息
        public void db_delMessageRecent(long chat_id);//根据id最近聊天删除
        public List<MessageHistory> db_getMessage(long chat_id, long session_order, int queryType);//获取消息历史
        public void db_clearMessageHistory(long chat_id,int chat_type); //单个会话删除全部记录
        public void db_deleteSelectedMessageHistory(long chat_id,String uuid); //删除选中记录
        public void db_clearFriendBubble();//清除好友气泡
        public void db_clearMessageRecentBubble(long chat_id);//清除最近联系人气泡
        public void db_updateSouyueMessageRecentBubble(long chat_id,long service_id);//更改最近联系人气泡数
        public void db_clearSouyueMessageRecentBubble(long serviceId,long cateId);//清除服务号最近气泡
        public Config db_getConfig();//获取泡得个数
        public Contact db_getContactById(long chat_id);//获得联系人
//        public boolean im_chargezsb();//系统赠送中搜币
        public void db_updateMessageHistoryTime(String uuid,int content_type, long chat_id, long currentTime);//密信插入当前时间
        public void updateStatus(String uuid, int content_type, long chat_id, int isRead);//语音插入无语未读标识
        public GroupMembers db_findMemberListByGroupidandUid(long groupid,long userid);//根据groupid和uerid查询member
        public List<GroupMembers> db_findMemberListByGroupid(long group_id);
        public String db_findGroupListByGroupidAndIsSaved(long group_id,int is_saved);//根据groupid和is_saved查显示在群聊中的群
        public String db_findGroupListByUserid(long group_id);//根据userid查找所有的群
        public Group db_updateGroup(long group_id);//根据groupid更新Group
//        public String db_findGroupMemberByMemberid(long groupid); //根据memberid查询群成员信息
        public boolean addGroupMemberOp(int op, String gid,String inviterId,int mode,List uids,String source);//加人进群
        public void db_deleteGroupSelectedMessageHistory(long chat_id,String uuid);//删除群聊历史
        public void db_deleteMemberById(long member_id,long gid);//根据member_id 删除对应成员
        public void db_updateCommentName(long chat_id,String commentName);//更新备注名
        public Group db_findGourp(long group_id);//根据群id查群信息
        public void db_updateRecentBy1(long chatid,long myid,String num);//update最近联系人的by1状态
        public List<ServiceMessageRecent> db_getServiceMessageByMyid();//查找服务号列表
        public long db_findMemberCountByGroupid(long group_id);//根据groupid查找群人数
        public boolean findGroupInfo(int op,long group_id,List memberIds);//查询群信息 (走协议)
        public boolean getMemberDetail(int op,long group_id,long memberId); //获取群成员详细信息 op 9
        public void db_insertDraft(long chatid,String draftConent);//插入抄稿
        public void db_insertDraftForAt(long chatid, String draftContent, String draftForAtContent);//插入带有at的草稿
        public MessageRecent db_findMessageRecent(long chatid);//获取某人的最近聊天记录
        public boolean updateNewsNotify(int op,long uid,boolean is_news_notify);//私聊消息提醒
        public boolean saveServiceMsgNotify(long srvId,boolean isNewsNotifyShielded);//服务号消息免打扰
        public void cancelNotify(int id);
        public int getNotifyNum(int id);
        public void initImService();//初始化tuita用户，以后可能去掉
    }

    @Override
    public IBinder onBind(Intent intent) {
//        im_init(intent);
        linkFilter = new IntentFilter(ConstantsUtils.LINK);
//        homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        screenFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(homeReceiver, homeFilter);
        registerReceiver(linkReceiver, linkFilter);
//        registerReceiver(screenOffReceiver, screenFilter);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        if (tmmgr != null)
//            tmmgr.im_logout();
        if(linkReceiver != null)
            unregisterReceiver(linkReceiver);
//        if(homeReceiver != null)
//            unregisterReceiver(homeReceiver);
//        if(screenOffReceiver != null)
//            unregisterReceiver(screenOffReceiver);
        return super.onUnbind(intent);
    }


    private ImAidlService.Stub mBinder = new ImAidlService.Stub() {

        /**
         * 上线
         */
        @Override
        public void im_connect(String version) throws RemoteException {
            Log.i("Tuita", "im_connect()");

            initImService();
            if (tmmgr != null) {
                tmmgr.im_connect(version);
            }

        }

        /**
         * 私聊好友的操作
         * @param op 1加好友并发验证内容 2确认成为好友 3拒绝成为好友 4删除好友关系
         * @param uid 对应的好友id
         * @param text 附加信息
         */
        @Override
        public boolean im_userOp(int op, long uid, String nick, String avatar, String text,int originType) throws RemoteException {
            if (tmmgr != null)
                return tmmgr.im_userOp(op, uid, nick, avatar, text, originType);
            return false;
        };

        /**
         * 根据条件取网络信息
         * @param op 1所有群和所有好友 2所有群 3单个群 4所有好友 5指定好友
         * @param text 当取单个群或好友时，为对应的群id或好友id
         */
        @Override
        public boolean im_info(int op, String text) throws RemoteException {
            boolean b = false;
            if (text == null)
                text = "";
            if (tmmgr != null)
                b = tmmgr.im_info(op, text);
            return b;
        }

        /**
         * 搜索用户
         */
        @Override
        public boolean im_search(String keyword) throws RemoteException {
            if (tmmgr != null)
                return tmmgr.im_search(keyword);
            return false;
        }

        /**
         * 手机联系人状态
         */
        @Override
        public boolean im_contacts_status(String contactJson) throws RemoteException {
            if(tmmgr != null)
                return tmmgr.im_contacts_status(contactJson);
            return false;
        }

        /**
         * IM下线
         */
        @Override
        public void im_logout() throws RemoteException {
//            setAppOnline(false);
            if(tmmgr != null) tmmgr.im_logout();
        }

        /**
         * 赠送中搜币
         */
        @Override
        public boolean im_giftzsb(long uid, int num) throws RemoteException {
            if(tmmgr != null)
                return tmmgr.im_giftzsb(uid, num);
            return false;
        }

        /**
         * 更新各种信息
         * @param op 1更新昵称 2更新群里的昵称 3更新好友名称
         * @param text
         */
        @Override
        public boolean im_update(int op, long uid, String text) throws RemoteException {
            if(tmmgr != null)
                return tmmgr.im_update(op, uid, text);
            return false;
        }

        /**
         * 手机通讯录上传
         */
		@Override
		public boolean im_contacts_upload(String contactJson)
				throws RemoteException {
			if(tmmgr != null)
                return tmmgr.im_contacts_upload(contactJson);
            return false;
		}

		@Override
		public String im_saveMessage(int type, long uidorgid, int contentType,
				String content, String retry) throws RemoteException {
			if (tmmgr != null)
                return tmmgr.im_saveMessage(type, uidorgid, contentType, content, retry);
            return null;
		}

		/**
		 * 建群
		 */
		@Override
		public boolean newGroupOp(int op, List uids) throws RemoteException {
			if(tmmgr != null){
				return tmmgr.newGroupOp(op, uids);
			}

			return false;
		}

		/**
		 * 加好友入群或者删除好友
		 */
		@Override
		public boolean addOrDeleteGroupMembersOp(int op, String gid, List uids)
				throws RemoteException {
			if(tmmgr != null){
				return tmmgr.addOrDeleteGroupMembersOp(op, gid, uids);
			}
			return false;
		}

		/**
		 * 退群
		 */
		@Override
		public boolean retreatGroupMembersOp(int op, String gid,
				String nextOwnerId) throws RemoteException {
			if(tmmgr != null){
				return tmmgr.retreatGroupMembersOp(op, gid, nextOwnerId);
			}
			return false;
		}

		/**
		 * 保存设置信息
		 */
		@Override
		public boolean saveGroupConfigOp(int op, String gid,
				boolean isGroupSaved, boolean isNewsNotifyShielded)
				throws RemoteException {
			if(tmmgr != null){
				return tmmgr.saveGroupConfigOp(op, gid, isGroupSaved, isNewsNotifyShielded);
			}
			return false;
		}

		/**
		 * 修改群昵称或者修改群成员昵称
		 */
		@Override
		public boolean updateGroupNickNameOp(int op, String gid, String nick)
				throws RemoteException {
			if(tmmgr != null){
				return tmmgr.updateGroupNickNameOp(op, gid, nick);
			}
			return false;
		}

		/**
		 * 获取群详细信息
		 */
		@Override
		public boolean getGroupDetailsOp(int op, String gid)
				throws RemoteException {
			if(tmmgr != null){
				return tmmgr.getGroupDetailsOp(op, gid);
			}
			return false;
		}


        @Override
        public boolean addGroupMemberOp(int op, String gid, String inviterId, int mode, List uids,String source) throws RemoteException {
            if(tmmgr != null){
                return tmmgr.addGroupMemberOp(op, gid, inviterId, mode, uids,source);
            }
            return false;
        }


        @Override
        public boolean findGroupInfo(int op, long group_id, List memberIds) throws RemoteException {
            if(tmmgr != null){
                return tmmgr.findGroupInfo(op, group_id, memberIds);
            }
            return false;
        }

        @Override
        public boolean getMemberDetail(int op, long group_id, long memberId) throws RemoteException {
            if(tmmgr != null){
                return tmmgr.getMemberDetail(op, group_id, memberId);
            }
            return false;
        }

        /**
         * 私聊消息提醒
         * @param op
         * @param uid
         * @param is_news_notify
         * @return
         * @throws RemoteException
         */
        @Override
        public boolean updateNewsNotify(int op, long uid, boolean is_news_notify) throws RemoteException {
            if(tmmgr != null){
                return tmmgr.updateNewsNotify(op, uid, is_news_notify);
            }
            return false;
        }

        /**
         * 服务号消息免打扰
         * @param srvId
         * @param isNewsNotifyShielded
         * @return
         * @throws RemoteException
         */
        @Override
        public boolean saveServiceMsgNotify(long srvId, boolean isNewsNotifyShielded) throws RemoteException {
            if(tmmgr != null){
                return tmmgr.saveServiceMsgNotify(srvId, isNewsNotifyShielded);
            }
            return false;
        }


        /**
         * 初始化imservice
         */
        @Override
        public void initImService() throws RemoteException {
            User u = SYUserManager.getInstance().getDBUser();
            TuitaIMManager.Owner owner = new TuitaIMManager.Owner();
            if (u != null && owner != null) {
                owner.setUid(u.userId());
                owner.setNick(u.name());
                owner.setAvatar(u.image());
                owner.setPass(u.token());
                mgr = TuitaSDKManager.getInstance(getApplicationContext());
                mgr.loadIM();
                tmmgr = mgr.getImmanager();
                tmmgr.setOwner(owner);
            }else{
                Logger.i("im","Imservice.im_connect","imconnect 时 u == "+u+"  owner == "+owner);
            }
        }

        @Override
        public void cancelNotify(int id) throws RemoteException {
            BroadcastUtil.notificationCancel(Imservice.this,id);
        }

        @Override
        public int getNotifyNum(int id) throws RemoteException {
            BroadcastUtil.initNotifyMap(Imservice.this);
            return BroadcastUtil.getNotifyAllNum(id);
        }

        @Override
        public boolean setUserMessageNotify(int op, long uid, boolean isNewsNotifyShielded, boolean hasSound, boolean isVibrate) throws RemoteException {
            if(tmmgr != null)
                return tmmgr.setUserMessageNotify(op,uid, isNewsNotifyShielded, hasSound, isVibrate);
            return false;
        }


        @Override
		public boolean getUserOp(int op, long uid) throws RemoteException {
			if(tmmgr != null){
				return tmmgr.getUserOp(op, uid);
			}
			return false;
		}

    };

}
