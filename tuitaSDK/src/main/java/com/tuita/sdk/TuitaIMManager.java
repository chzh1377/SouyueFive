package com.tuita.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.helper.CateDaoHelper;
import com.tuita.sdk.im.db.helper.ConfigDaoHelper;
import com.tuita.sdk.im.db.helper.ContactDaoHelper;
import com.tuita.sdk.im.db.helper.GroupDaoHelper;
import com.tuita.sdk.im.db.helper.GroupMembersDaoHelper;
import com.tuita.sdk.im.db.helper.HelperUtils;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.helper.MessageMidDaoHelper;
import com.tuita.sdk.im.db.helper.MessageRecentDaoHelper;
import com.tuita.sdk.im.db.helper.NewFriendDaoHelper;
import com.tuita.sdk.im.db.helper.ServiceMessageDaoHelper;
import com.tuita.sdk.im.db.helper.ServiceMessageRecentDaoHelper;
import com.tuita.sdk.im.db.module.AtFriend;
import com.tuita.sdk.im.db.module.Cate;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupExtendInfo;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.ImToCricle;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.NewFriend;
import com.tuita.sdk.im.db.module.SearchMsgResult;
import com.tuita.sdk.im.db.module.ServiceMessage;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.tuita.sdk.im.db.module.TimeOutBean;
import com.tuita.sdk.im.db.module.UserBean;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TuitaIMManager {
    protected static final String TUITA_IM_SDK_VERSION = "1";
    protected static final int TUITA_IM_RETURN_CODE_SUCCESS = 200;  //正确
    protected static final int TUITA_IM_RETURN_CODE_ERROR_RECONNECT = 401;  //服务器通知重连
    protected static final int TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN = 403; //出错误 互踢  权限失败
    protected static final int TUITA_IM_RETURN_CODE_ERROR_SERVERERROE = 500;//服务器错误
    protected static final int TUITA_IM_RETURN_CODE_ERROR_NOTFRIEND = 601;//104消息下业务不是好友
    protected static final int TUITA_IM_RETURN_CODE_ERROR_PERMISSION = 603;//权限错误
    protected static final int TUITA_IM_MESSAGE_HISTORY_LENGTH = 20;
    protected static final int TUITA_IM_COMMAND_RUNNING_LENGTH = 20;
    protected static final int TUITA_IM_COMMAND_DELAY_SECOND = 10;
    protected static final int NEW_TUITA_IM_COMMAND_DELAY_SECOND = 20;
    protected static final int TUITA_IM_RUNNING_CMD_LENGTH = 50;

    protected static final int TUITA_IM_SYSMSG_TYPE_ADDME = 1;
    protected static final int TUITA_IM_SYSMSG_TYPE_AGREE = 2;
    protected static final int TUITA_IM_SYSMSG_TYPE_REFUSE = 3;
    protected static final int TUITA_IM_SYSMSG_TYPE_DELME = 4;
    protected static final int TUITA_IM_SYSMSG_TYPE_PHONE = 9;// 系统推送手机联系人
    protected static final int TUITA_IM_SYSMSG_TYPE_UPDATE_CONTACT = 10;// 更新好友相关信息
    protected static final int TUITA_IM_SYSMSG_TYPE_CREATE_GROUP = 101;// 建群，加好友返回,
    // 发送给其它被加入群的人员的

    protected static final int TUITA_IM_SYSMSG_TYPE_DELETE_GROUP = 102;// 删除群好友，发送给所有成员
    protected static final int TUITA_IM_SYSMSG_TYPE_RETREAT_GROUP = 103;// 退出群
    protected static final int TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_NAME = 104;// 修改群昵称，发送给其它群的成员的，不一定实时发
    protected static final int TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_MEMBER_NAME = 105;// 修改群成员自己的昵称，发送给其它群的成员的，不一定实时发
    protected static final int TUITA_IM_SYSMSG_TYPE_GET_GROUP_MEMBERS_DETAIL = 106;// 获取群成员详细信息
    protected static final int TUITA_IM_SYSMSG_TYPE_GET_GROUP_AVATAR_AND_NAME = 107;// 群头像和群名称
    protected static final int TUITA_IM_SYSMSG_TYPE_GET_GROUP_ALL = 108;//获取某个群的所有信息


    protected static final int TUITA_IM_SYSMSG_TYPE_UPDATE_SERVICE_MSG = 201;//更新服务号  5.0新增（以后是更新所有消息）
    // im值
    public static final String TUITA_IM_SYSMSG_TYPE_IM = "1";

    protected static final int TUITA_IM_USEROP_ADD = 1;
    protected static final int TUITA_IM_USEROP_AGREE = 2;
    protected static final int TUITA_IM_USEROP_REFUSE = 3;
    protected static final int TUITA_IM_USEROP_DEL = 4;
    protected static final int TUITA_IM_USEROP_ISNEWSNOTIFY = 5;    //私聊消息提醒
    protected static final int TUITA_IM_CREATE_GROUP = 1;// 创建群
    protected static final int TUITA_IM_ADD_FRIENDS_GROUP = 2;// 加好友入群
    protected static final int TUITA_IM_DELETE_GROUP_MEMBER = 3;// 删除群好友
    protected static final int TUITA_IM_EXIT_GROUP = 4;// 退群
    protected static final int TUITA_IM_GROUP_CONFIG = 5;
    protected static final int TUITA_IM_UPDATE_GROUP_NAME = 6;// 修改群昵称
    protected static final int TUITA_IM_UPDATE_GROUP_MEMBER_NAME = 7;// 修改群成员自己的昵称
    protected static final int TUITA_IM_GROUP_DETAIL = 8;// 获取群详细信息
    protected static final int TUITA_IM_GROUP_MEMBERS_DETAIL = 9;// 获取群成员详细信息
    protected static final int TUITA_IM_IS_CONTACT = 10;// 获取用户并查看是否是好友
    protected static final int TUITA_IM_IS_IN_GROUP = 11; // 是否在该群中
    protected static final int TUITA_IM_ADD_IN_GROUP = 0; // 用户加入群

    protected static final int TUITA_IM_UPDATEOP_EDITALIAS = 3;

    public static final int HAS_READ = 1;
    public static final int NO_READ = 0;
    public static final int TUITA_IM_STATE_DISCONNECT = 0;
    public static final int TUITA_IM_STATE_CONNECT = 1;
    protected static final int CONN_STATE_NOTCONNECT = 0;
    protected static final int CONN_STATE_CONNECTING = 1;
    public static final String ISINVITED = "isinvited";//是当前用户邀请的人
    public static final String NOTINVITED = "noinvited";//非当前用户邀请的人

    private static TuitaIMManager immanager;
    private TuitaSDKManager manager;
    private SmallMap<Long, ImCommand> runningCmd = new SmallMap<Long, ImCommand>(
            TUITA_IM_RUNNING_CMD_LENGTH);
    private Owner owner;
    private volatile int imConnState; // 0 not connect 1 connecting
    private volatile int tuitaIMState; // 0 disconnect 1 connect

    public SmallMap<Long, Long> getGroupOwnerId() {
        return groupOwnerId;
    }

    private SmallMap<Long, Long> groupOwnerId = new SmallMap<Long, Long>(TUITA_IM_RUNNING_CMD_LENGTH);
    //    private String oldMessage_muid = "";//旧的记录消息的muid，防止消息重复
//    private String newMessage_muid = "";//新的记录消息的muid，防止消息重复
    public static final String USER_GUEST = "0";
    public static final String USER_ADMIN = "1";

    private TuitaIMManager(TuitaSDKManager manager) {
        this.manager = manager;
    }

    public static TuitaIMManager getInstance(TuitaSDKManager manager) {
        if (immanager == null) {
            synchronized (TuitaIMManager.class) {
                if (immanager == null) {
                    immanager = new TuitaIMManager(manager);
                }
            }
        }
        return immanager;
    }

    /**
     * IM上线，需传入自己的id
     */
    public void im_connect(String version) {
        putString("version", version);
        Logger.i("tuita", "TuitaIMManager.im_connect", "version = " + version);
        im_innerconnect();
    }

    public void putString(String key, String val) {
        //
        try {
            SharedPreferences sp = this
                    .getManager()
                    .getContext()
                    .getSharedPreferences(TuitaSDKManager.TAG,
                            Context.MODE_PRIVATE);

            if (sp != null) {
                if (getString(key) != null && !"".equals(getString(key))) {
                    sp.edit().remove(key);
                }
                sp.edit().putString(key, val).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return this
                .getManager()
                .getContext()
                .getSharedPreferences(TuitaSDKManager.TAG, Context.MODE_PRIVATE)
                .getString(key, "");
    }

    /**
     * 上线及重新上线
     */
    public void im_innerconnect() {
        SYUserBean user = null;
        Log.i(TuitaSDKManager.TAG,
                "im_connect,status:" + this.getTuitaIMState() + ",tuitastate:"
                        + this.getManager().getTuitaState());
//        if (this.getManager().getTuitaState() == TuitaSDKManager.TUITA_STATE_DISCONNECT) {
//            Logger.i("tuita","TuitaIMManager.im_innerconnect","im_innerconnect() tuita state disconnect");
//            return;
//        }
//        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
//            Logger.i("tuita","TuitaIMManager.im_innerconnect","im_innerconnect() already im connect");
//            return;
//        }
//        if (this.getImConnState() == TuitaIMManager.CONN_STATE_CONNECTING) {
//            Logger.i("tuita","TuitaIMManager.im_innerconnect","im_innerconnect() im connecting return");
//            return;
//        }
        if (this.getOwner() == null) {
            Logger.i("tuita", "TuitaIMManager.im_innerconnect", "im_innerconnect()   this.getOwner()" + this.getOwner());
            UserDBHelper userTableDBHelper = new UserDBHelper(this.getManager().getContext());
            userTableDBHelper.openReadable();
            user = userTableDBHelper.select(USER_ADMIN);
            if (user == null || "".equals(user.token()))
                user = userTableDBHelper.select(USER_GUEST);
            userTableDBHelper.close();
        }
        synchronized (TuitaIMManager.class) {
            if (this.getImConnState() == TuitaIMManager.CONN_STATE_CONNECTING) {
                Log.i(TuitaSDKManager.TAG, "im connecting 2 return");
                Logger.i("tuita", "TuitaIMManager.im_innerconnect", "im connecting return");
                return;
            }
            if (this.getOwner() == null && user != null) {
                Log.i(TuitaSDKManager.TAG, "version  is" + getString("version"));
                if (getString("version") != null && !"".equals(getString("version"))) {
                    this.setImConnState(TuitaIMManager.CONN_STATE_CONNECTING);
                    owner = new Owner();
                    owner.setUid(user.userId());
                    owner.setNick(user.name());
                    owner.setAvatar(user.image());
                    owner.setPass(user.token());
                    Config config = ConfigDaoHelper.getInstance(this.getManager().getContext()).find(user.userId());
                    ImCommand cmd = ImCommand.newConnectCmd(this, config != null ? config.getContact_last_update() : 0, getString("version"));
                    manager.getConnection().write(cmd.getPacket());
                }
            } else if (this.getOwner() != null) {
                this.setImConnState(TuitaIMManager.CONN_STATE_CONNECTING);
                Config config = ConfigDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid());
                ImCommand cmd = ImCommand.newConnectCmd(this, config != null ? config.getContact_last_update() : 0, getString("version"));
                manager.getConnection().write(cmd.getPacket());
            }

            manager.getScheduler().schedule(new Runnable() {
                @Override
                public void run() {
                    if (TuitaIMManager.this.getImConnState() == TuitaIMManager.CONN_STATE_CONNECTING) {
                        TuitaIMManager.this
                                .setImConnState(TuitaIMManager.CONN_STATE_NOTCONNECT);
                    }
                    // if (TuitaIMManager.this.getTuitaIMState() ==
                    // TuitaIMManager.TUITA_IM_STATE_DISCONNECT) {
                    // BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(),
                    // BroadcastUtil.ACTION_CONNECT_FAIL, null, false);
                    // }
                }
            }, TUITA_IM_COMMAND_DELAY_SECOND, TimeUnit.SECONDS);
        }

    }

    /**
     * IM下线
     */
    public void im_logout() {
        if (this.getTuitaIMState() == TUITA_IM_STATE_DISCONNECT) {
            Log.i(TuitaSDKManager.TAG, "already logout");
            return;
        }
        this.setTuitaIMState(TUITA_IM_STATE_DISCONNECT);
        ImCommand cmd = ImCommand.newLogoutCmd();
        manager.getConnection().write(cmd.getPacket());
        this.setOwner(null);

    }

    /**
     * 给服务器的确认消息，确认收到的最大消息id
     *
     * @param tid
     */
    private void ack(long tid) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newAckCmd(tid);
            manager.getConnection().write(cmd.getPacket());
        }
    }

    private String genUuid(long uid, long t) {
        return "" + uid + "_" + t;
    }

//    private long t;


    /**
     * 发送消息前入库操作
     *
     * @param context
     * @param myid
     * @param type
     * @param uidorgid
     * @param contentType
     * @param content
     * @param retryUuid
     * @return
     */
    public static String sendInsertMsg(Context context, long myid, int type, long uidorgid, int contentType,
                                       String content, String retryUuid) {
//        int retry = 0;
        String uuid = null;
        final long uid = myid;
        long t = System.currentTimeMillis();
        if (retryUuid != null && retryUuid.length() > 0) {
            // 消息已经存在
            uuid = retryUuid;
            Logger.i("tuita", "TuitaIMManager.im_sendMessage", "旧消息UUID= " + uuid);
            if (contentType == MessageHistory.CONTENT_TYPE_AT_FRIEND) {//如果是at类型重发
                AtFriend atFriend = new Gson().fromJson(content, AtFriend.class);
                String c = atFriend.getC();
                List<UserBean> userBean = atFriend.getUsers();
                for (int k = 0; k < userBean.size(); k++) {
                    long memberid = userBean.get(k).getUid();
                    String nickname = userBean.get(k).getNick();
                    Contact newcontact = ContactDaoHelper.getInstance(context).find(myid, memberid);
                    String mem = new Gson().toJson(GroupMembersDaoHelper.getInstance(context).find(myid, uidorgid, memberid));
                    GroupMembers groupMembers = new Gson().fromJson(mem, GroupMembers.class);
                    if (newcontact != null && !TextUtils.isEmpty(newcontact.getComment_name())) {
                        c = c.replace(nickname, newcontact.getComment_name());
                    } else if (groupMembers != null && !TextUtils.isEmpty(groupMembers.getMember_name())) {
                        c = c.replace(nickname, groupMembers.getMember_name());
                    }
                }
                MessageHistoryDaoHelper.getInstance(
                        context).update(uuid,
                        uid, c, -1, IMessageConst.STATUS_SENTING);
            } else {//普通文本消息重发
                MessageHistoryDaoHelper.getInstance(
                        context).update(uuid,
                        uid, content, -1, IMessageConst.STATUS_SENTING);
            }
//            retry = 1;
        } else {
            // 新消息
//            uuid = genUuid(myid, t);
            uuid = "" + myid + "_" + t;
            Logger.i("tuita", "TuitaIMManager.im_sendMessage", "新消息 uuid = " + uuid);
            MessageHistory msg = new MessageHistory();
            msg.setMyid(myid);
            msg.setChat_id(uidorgid);
            msg.setSender(myid);
            msg.setChat_type(type);
            msg.setContent(content);
            msg.setContent_type(contentType);
            long session_order = MessageHistoryDaoHelper.getInstance(
                    context).getMaxSessionOrder(
                    myid, uidorgid);
            msg.setSession_order(session_order);
            msg.setDate(t);
            msg.setUuid(uuid);
//            if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            msg.setStatus(IMessageConst.STATUS_SENTING);
//            } else {
//                msg.setStatus(IMessageConst.STATUS_SENT_FAIL);
//            }
            if (contentType == MessageHistory.CONTENT_TYPE_AT_FRIEND) {
                msg.setContentforat(content);
                msg.setContent_type(MessageHistory.CONTENT_TYPE_TEXT);

                AtFriend atFriend = new Gson().fromJson(content, AtFriend.class);
                String c = atFriend.getC();
                List<UserBean> userBean = atFriend.getUsers();
                for (int k = 0; k < userBean.size(); k++) {
                    long memberid = userBean.get(k).getUid();
                    String nickname = userBean.get(k).getNick();
                    Contact newcontact = ContactDaoHelper.getInstance(context).find(myid, memberid);
                    String mem = new Gson().toJson(GroupMembersDaoHelper.getInstance(context).find(myid, uidorgid, memberid));
                    GroupMembers groupMembers = new Gson().fromJson(mem, GroupMembers.class);
                    if (newcontact != null && !TextUtils.isEmpty(newcontact.getComment_name())) {
                        c = c.replace(nickname, newcontact.getComment_name());
                    } else if (groupMembers != null && !TextUtils.isEmpty(groupMembers.getMember_name())) {
                        c = c.replace(nickname, groupMembers.getMember_name());
                    }
                }
                msg.setContent(c);
            }
            MessageHistoryDaoHelper.getInstance(context)
                    .save(msg);
            MessageRecent chat = new MessageRecent();
            chat.setMyid(myid);
            chat.setChat_id(msg.getChat_id());
            chat.setSender(myid);
            chat.setChat_type(msg.getChat_type());
            chat.setContent(msg.getContent());
            chat.setContent_type(msg.getContent_type());
            chat.setDate(msg.getDate());
            chat.setUuid(msg.getUuid());
            chat.setBy1("0");
            chat.setStatus(msg.getStatus());
            MessageRecentDaoHelper.getInstance(context)
                    .save(chat);
            //如果是服务号还需要入一个库
            if (type == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                ServiceMessageRecent serviceMessageRecent = ServiceMessageRecentDaoHelper.getInstance(context).find(myid, msg.getChat_id());
                serviceMessageRecent.setDigst(msg.getContent());
                serviceMessageRecent.setMyid(myid);
                serviceMessageRecent.setService_id(msg.getChat_id());
                serviceMessageRecent.setBy1(msg.getUuid());
                serviceMessageRecent.setDetail_type(msg.getContent_type());
                ServiceMessageRecentDaoHelper.getInstance(context).save(serviceMessageRecent);
            }
        }
        return uuid;
    }


    /**
     * 存储聊天消息(针对语音，图片等，只存数据库，不发送)
     *
     * @param type        消息类型 0私聊 1群聊
     * @param uidorgid    好友id或群id
     * @param contentType 消息内容类型 1文字 2语音 3图片
     * @param content     内容
     */
    public String im_saveMessage(int type, long uidorgid, int contentType,
                                 String content, String retryUuid) {
        Log.i(TuitaSDKManager.TAG,
                "im_saveMessage,status:" + this.getTuitaIMState()
                        + ",retryUuid:" + retryUuid);
        int retry = 0;
        String uuid = null;
        final long uid = this.getOwner().getUid();
        long t = System.currentTimeMillis();
        if (retryUuid != null && retryUuid.length() > 0) {
            // 消息已经存在
            uuid = retryUuid;
            Logger.i("tuita", "TuitaIMManager.im_sendMessage", "旧消息UUID= " + uuid);
            retry = 1;
            if (this.getTuitaIMState() != TUITA_IM_STATE_CONNECT) {
                MessageHistory ret = new MessageHistory();
                ret.setUuid(uuid);
                ret.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this
                                .getManager().getContext(), BroadcastUtil.ACTION_MSG,
                        new Gson().toJson(ret), false);
            }
        } else {
            // 新消息
            uuid = genUuid(this.getOwner().getUid(), t);
            Logger.i("tuita", "TuitaIMManager.im_sendMessage", "新消息 uuid = " + uuid);
            MessageHistory msg = new MessageHistory();
            msg.setMyid(this.getOwner().getUid());
            msg.setChat_id(uidorgid);
            msg.setSender(this.getOwner().getUid());
            msg.setChat_type(type);
            msg.setContent(content);
            msg.setContent_type(contentType);
            long session_order = MessageHistoryDaoHelper.getInstance(
                    this.getManager().getContext()).getMaxSessionOrder(
                    this.getOwner().getUid(), uidorgid);
            msg.setSession_order(session_order);
            msg.setDate(t);
            msg.setUuid(uuid);

            if (contentType == IMessageConst.CONTENT_TYPE_RED_PAKETS){
                msg.setStatus(IMessageConst.STATUS_HAS_SENT);
            }else {
                if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
                    msg.setStatus(IMessageConst.STATUS_SENTING);
                } else {
                    MessageHistory ret = new MessageHistory();
                    ret.setUuid(uuid);
                    ret.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                    BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this
                                    .getManager().getContext(), BroadcastUtil.ACTION_MSG,
                            new Gson().toJson(ret), false);
                    msg.setStatus(IMessageConst.STATUS_SENT_FAIL);
                }
            }
            MessageHistoryDaoHelper.getInstance(this.getManager().getContext())
                    .save(msg);

            MessageRecent chat = new MessageRecent();
            chat.setMyid(this.getOwner().getUid());
            chat.setChat_id(msg.getChat_id());
            chat.setSender(this.getOwner().getUid());
            chat.setChat_type(msg.getChat_type());
            chat.setContent(msg.getContent());
            chat.setContent_type(msg.getContent_type());
            chat.setDate(msg.getDate());
            chat.setUuid(msg.getUuid());
            chat.setStatus(msg.getStatus());
            MessageRecentDaoHelper.getInstance(this.getManager().getContext())
                    .save(chat);
        }
        return uuid;
    }

    /**
     * 私聊好友的操作
     *
     * @param op   1加好友并发验证内容 2确认成为好友 3拒绝成为好友 4删除好友关系
     * @param uid  对应的好友id
     * @param text 附加信息
     */
    public boolean im_userOp(int op, long uid, String nick, String avatar,
                             String text, int originType) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            Contact contact = new Contact();
            contact.setChat_id(uid);
            contact.setNick_name(nick);
            contact.setAvatar(avatar);
            ImCommand cmd = ImCommand.newUserOpCmd(this, op, contact, text, originType);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 获取服务号消息（只在本地没有的情况下）
     */
    public boolean getServiceMsgDetail(long srvId) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.getServiceMsgDetailCommand(srvId);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 私聊消息提醒
     *
     * @param op
     * @param uid
     * @param is_news_notify
     * @return
     */
    public boolean updateNewsNotify(int op, long uid, boolean is_news_notify) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newIMnotifyCmd(op, uid, is_news_notify);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 更新各种信息
     *
     * @param op   1更新昵称 2更新群里的昵称 3更新好友名称 4更新头像
     * @param text
     */
    public boolean im_update(int op, long uid, String text) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newUpdateCmd(this, op, uid, text);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 根据条件取各种信息
     *
     * @param op   1所有群和所有好友 2所有群 3单个群 4所有好友 5指定好友
     * @param text 当取单个群或好友时，为对应的群id或好友id
     */
    public boolean im_info(int op, String text) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            Config config = ConfigDaoHelper.getInstance(
                    this.getManager().getContext()).find(this.getOwner().getUid());
            ImCommand cmd = ImCommand.newInfoCmd(this, op,
                    config != null ? config.getContact_last_update() : 0, text);
            manager.getConnection().write(cmd.getPacket());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 创建群
     *
     * @param op   1
     * @param uids 群成员ID列表
     */
    public boolean newGroupOp(int op, List<Long> uids) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newGroupOpCmd(this, op, uids);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 删除群好友
     *
     * @param op   删除3
     * @param gid  群id
     * @param uids 群成员ID列表
     */
    public boolean addOrDeleteGroupMembersOp(int op, String gid, List<Long> uids) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.addGroupMembersOpCmd(this, op, gid, uids);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 加人进群
     *
     * @param op
     * @param gid
     * @param inviterId 邀请人id
     * @param mode      1 正常加入 2通过群二维码加入
     * @param uids
     * @return
     */
    public boolean addGroupMemberOp(int op, String gid, String inviterId,
                                    int mode, List uids,String source) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.addGroupMembers(this, op, gid, inviterId, mode, uids,source);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 退群
     *
     * @param op          4
     * @param gid         群主id
     * @param nextOwnerId 下一个群主id
     */
    public boolean retreatGroupMembersOp(int op, String gid, String nextOwnerId) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.retreatGroupOpCmd(this, op, gid, nextOwnerId);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 保存群配置信息
     *
     * @param op                   5
     * @param gid                  群主id
     * @param isGroupSaved         是否保存到通信录
     * @param isNewsNotifyShielded 是否屏蔽消息提醒
     */
    public boolean saveGroupConfigOp(int op, String gid, boolean isGroupSaved,
                                     boolean isNewsNotifyShielded) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.saveGroupConfigOpCmd(this, op, gid, isGroupSaved, isNewsNotifyShielded);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 修改群昵称或者修改群成员昵称
     *
     * @param op   群6 群成员7
     * @param gid  群主id
     * @param nick 昵称nick
     */
    public boolean updateGroupNickNameOp(int op, String gid, String nick) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.updateGroupNickNameOpCmd(this, op, gid, nick);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 获取群详细信息和是否在该群中
     *
     * @param op  8获取群详细信息，11是否在该群中
     * @param gid 群主id
     */
    public boolean getGroupDetailsOp(int op, String gid) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.getGroupDetailsOpCmd(this, op, gid);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
//            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 获取群成员详细信息
     *
     * @param op  9
     * @param gid 群主id
     */
    public boolean getMemberDetail(int op, long gid, long memberId) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.getMemberDetailOpCmd(this, op, gid, memberId);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
//            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 获取用户并查看是否是好友
     *
     * @param op  10
     * @param uid 用户id
     */
    public boolean getUserOp(int op, long uid) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.getUserOpCmd(this, op, uid);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
//            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 搜索好友
     *
     * @param keyword 搜索关键词
     */
    public boolean im_search(String keyword) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newSearchCmd(keyword);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 赠送中搜币
     */
    public boolean im_giftzsb(long uid, int num) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newGiftzsbCmd(uid, num);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            final long tid = cmd.getTid();
            manager.getScheduler().schedule(new Runnable() {
                @Override
                public void run() {
                    ImCommand cmd = runningCmd.get(tid);
                    if (cmd != null) {
                        BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_GIFT_ZSB, IConst.GIFT_ZSB_TIMEOUT, false);
                        runningCmd.remove(tid);
                    }
                }
            }, TUITA_IM_COMMAND_DELAY_SECOND, TimeUnit.SECONDS);
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 充值中搜币
     */
    public boolean im_chargezsb() {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newChargezsbCmd();
            manager.getConnection().write(cmd.getPacket());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 手机通讯录的联系人状态
     */
    public boolean im_contacts_status(String contactJson) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            List<Contact> contacts = new Gson().fromJson(contactJson,
                    new TypeToken<List<Contact>>() {
                    }.getType());
            ImCommand cmd = ImCommand.newContactStatusCmd(contacts);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
//            requestTimeOut(manager,cmd.getTid());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 上传手机通讯录
     */
    public boolean im_contacts_upload(String contactJson) {
        Log.i(TuitaSDKManager.TAG,
                "im_contacts_upload,status:" + this.getTuitaIMState());
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            List<Contact> contacts = new Gson().fromJson(contactJson,
                    new TypeToken<List<Contact>>() {
                    }.getType());
            ImCommand cmd = ImCommand.newContactUploadCmd(contacts);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
//            requestTimeOut(manager,cmd.getTid());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 保存服务号消息免打扰接口
     *
     * @param srvId
     * @param isNewsNotifyShielded
     * @return
     */
    public boolean saveServiceMsgNotify(long srvId, boolean isNewsNotifyShielded) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.saveServiceMsgNotify(srvId, isNewsNotifyShielded);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setFailedBean(), false);
            //重连
            manager.reConnectIM();
            return false;
        }
    }

    /**
     * 获取聊天记录，返回小于maxMid的一定条数的消息列表
     *
     * @param chatid 会话标识 两人的id组合，格式uid1_uid2
     * @param maxMid 最大的消息id
     */
    public boolean im_getMessage(String chatid, long maxMid) {
        Log.i(TuitaSDKManager.TAG, "im_getMessage");
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newGetMessageCmd(this,
                    IConst.CHAT_TYPE_PRIVATE, chatid, maxMid);
            manager.getConnection().write(cmd.getPacket());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取某个群的所有信息
     *
     * @param op      12
     * @param groupid 群id
     * @return
     */
    public boolean findGroupInfo(int op, long groupid, List<Long> memberIds) {
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.findGroupInfoCmd(this,
                    IConst.CHAT_TYPE_GROUP, op, groupid, memberIds);
            manager.getConnection().write(cmd.getPacket());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }

    }

    public static List<MessageHistory> db_getMessage(Context cx, long myid, long chat_id, long session_order, int queryType) {
        if (queryType == IConst.QUERY_MSG_FIRST) {
            return MessageHistoryDaoHelper.getInstance(
                    cx).findWhileFirstIn(
                    myid, chat_id);
        } else if (queryType == IConst.QUERY_MSG_UP) {
            return MessageHistoryDaoHelper.getInstance(
                    cx).findUp(
                    myid, chat_id, session_order);
        } else if (queryType == IConst.QUERY_MSG_ALL) {
            return MessageHistoryDaoHelper.getInstance(
                    cx).findAll(
                    myid, chat_id);
        } else {
            return MessageHistoryDaoHelper.getInstance(
                    cx).findWhileNewMessageCome(
                    myid, chat_id, session_order);
        }
    }

    /**
     * 通过bubblenumber 查找数据
     *
     * @param cx
     * @param myid
     * @param chat_id
     * @param limitCount
     * @return
     */
    public static List<MessageHistory> getMessageByLimitCount(Context cx, long myid, long chat_id, int limitCount) {
        return MessageHistoryDaoHelper.getInstance(
                cx).findByLimitCount(
                myid, chat_id, limitCount);
    }

    /**
     * @param uuid
     * @return by  zhangwb
     */
    public static ServiceMessage db_getServiceMessageMessage(Context context, String uuid) {
        return ServiceMessageDaoHelper.getInstance(context)
                .findByMuid(uuid);
    }

    /**
     * 查找服务号列表
     *
     * @param myid
     * @return by zhangwb
     */
    public static String db_getServiceMessageByMyid(Context context, long myid) {
        return new Gson().toJson(ServiceMessageRecentDaoHelper.getInstance(context).findByMyid(myid));
    }


    public static void db_clearMessageHistory(Context cx, long myid, long chat_id, int chat_type) {
        MessageHistoryDaoHelper.getInstance(cx)
                .deleteAll(myid, chat_id, chat_type);
    }

    public static void db_deleteMemberById(Context context, long myid, long member_id, long gid) {
        GroupMembersDaoHelper.getInstance(context).deleteBygr(myid, member_id, gid);
    }

//    public static String db_findGroupMemberByMemberid(Context context, long myid , long memberid) {
//        return new Gson().toJson(GroupMembersDaoHelper.getInstance(context).find(myid,memberid));
//    }

    public static void db_deleteSelectedMessageHistory(Context cx, long myid, long chat_id, String uuid) {
        MessageHistoryDaoHelper.getInstance(cx)
                .deleteSelected(myid, chat_id, uuid);
    }

    public static void db_deleteGroupSelectedMessageHistory(Context context, long myid, long chat_id, String uuid) {
        MessageHistoryDaoHelper.getInstance(context).deleteGroupSelected(myid, chat_id, uuid);
    }

    /**
     * 新增删除所有 chat_type类型方法 zcz
     *
     * @param cx
     * @param myid
     * @param chat_id
     * @param uuid
     * @param chat_type
     */
    public static void db_deleteSelectedAllTypeMessageHistory(Context cx, long myid, long chat_id, String uuid, int chat_type) {
        MessageHistoryDaoHelper.getInstance(cx)
                .deleteSelectedItem(myid, chat_id, uuid, chat_type);
    }

    public static List<Contact> db_findLike(Context cx, long myid, String localOrder) {
        List<Contact> contacts = ContactDaoHelper.getInstance(cx).findLike(myid, localOrder);
        List<Group> groupList = GroupDaoHelper.getInstance(cx).findLike(myid, localOrder);

        for (Group group : groupList) {
            Contact contact = new Contact();
            contact.setChat_id(group.getGroup_id());
            contact.setChat_type(1);
            contact.setComment_name(group.getGroup_nick_name());
            contact.setNick_name(group.getGroup_nick_name());
            contact.setAvatar(group.getGroup_avatar());

            contacts.add(contact);
        }
        if (groupList.size() > 0) {
            Contact contact2 = new Contact();
            contact2.setComment_name("群聊");
            contacts.add(contacts.size() - groupList.size(), contact2);
        }
        if (contacts.size() - groupList.size() > 1) {
            Contact contact3 = new Contact();
            contact3.setComment_name("好友");
            contacts.add(0, contact3);
        }

        return contacts;

    }

    public static List<Contact> db_findLikeOnlyContact(Context cx, long myid, String localOrder) {
        List<Contact> contacts = ContactDaoHelper.getInstance(cx).findLike(myid, localOrder);
        return contacts;
    }


    /**
     * 搜索结果数据拼装
     *
     * @param cx
     * @param myid
     * @param localOrder
     * @return
     */
    public static List<SearchMsgResult> db_find_search_all(Context cx, long myid, String localOrder) {
        List<SearchMsgResult> results = new ArrayList<SearchMsgResult>();
        List<Contact> myContacts = ContactDaoHelper.getInstance(cx).findLike(myid, localOrder);
        //取前三条
        List<Contact> contacts = new ArrayList();
        if (myContacts.size() > 3) {
            for (int i = 0; i < 3; i++) {
                contacts.add(myContacts.get(i));
            }
        } else {
            for (int i = 0; i < myContacts.size(); i++) {
                contacts.add(myContacts.get(i));
            }
        }

        List<Group> myGroupList = GroupDaoHelper.getInstance(cx).findLike(myid, localOrder);
        //取前三条
        List<Group> groupList = new ArrayList();
        if (myGroupList.size() > 3) {
            for (int i = 0; i < 3; i++) {
                groupList.add(myGroupList.get(i));
            }
        } else {
            for (int i = 0; i < myGroupList.size(); i++) {
                groupList.add(myGroupList.get(i));
            }
        }

        //填充单聊（联系人）数据
        for (Contact contact : contacts) {
            SearchMsgResult result1 = new SearchMsgResult();
            result1.setLayoutType(1);
            result1.setGroupType(0);
            result1.setChat_id(contact.getChat_id());
            result1.setChat_type(contact.getChat_type());
            String commentName = contact.getComment_name();
            if (commentName != null && !"".equals(commentName)) {
                result1.setTitle(contact.getComment_name());
                result1.setContent(contact.getNick_name());
            } else {
                result1.setTitle(contact.getNick_name());
            }
            result1.setUserImage(contact.getAvatar());
            results.add(result1);
        }
        if (contacts.size() > 0) {
            SearchMsgResult result2 = new SearchMsgResult();
            result2.setLayoutType(0);
            result2.setGroupType(0);
            result2.setGroupName("联系人");
            results.add(results.size() - contacts.size(), result2);
        }
        if (myContacts.size() > 3) {
            SearchMsgResult addBottomResult = new SearchMsgResult();
            addBottomResult.setLayoutType(2);
            addBottomResult.setGroupType(0);
            results.add(addBottomResult);
        }

        //填充群聊数据
        if (groupList.size() > 0) {
            SearchMsgResult addresult = new SearchMsgResult();
            addresult.setLayoutType(0);
            addresult.setGroupType(1);
            addresult.setGroupName("群聊");
            results.add(addresult);
        }
        for (Group group : groupList) {
            SearchMsgResult result = new SearchMsgResult();
            result.setLayoutType(1);
            result.setGroupType(1);
            result.setChat_id(group.getGroup_id());
            result.setChat_type(1);
            result.setTitle(group.getGroup_nick_name());
            result.setUserImage(group.getGroup_avatar());
            results.add(result);
        }
        if (myGroupList.size() > 3) {
            SearchMsgResult addBottomResult = new SearchMsgResult();
            addBottomResult.setLayoutType(2);
            addBottomResult.setGroupType(1);
            results.add(addBottomResult);
        }

        return results;

    }

    /**
     * 二级页面 搜索结果联系人详情
     *
     * @param cx
     * @param myid
     * @param localOrder
     * @return
     */
    public static List<SearchMsgResult> db_find_search_contact_detail(Context cx, long myid, String localOrder) {
        List<SearchMsgResult> results = new ArrayList<SearchMsgResult>();
        List<Contact> contacts = ContactDaoHelper.getInstance(cx).findLike(myid, localOrder);

        for (Contact contact : contacts) {
            SearchMsgResult result1 = new SearchMsgResult();
            result1.setLayoutType(1);
            result1.setGroupType(0);
            result1.setChat_id(contact.getChat_id());
            result1.setChat_type(contact.getChat_type());
            String commentName = contact.getComment_name();
            if (commentName != null && !"".equals(commentName)) {
                result1.setTitle(contact.getComment_name());
                result1.setContent(contact.getNick_name());
            } else {
                result1.setTitle(contact.getNick_name());
            }
            result1.setUserImage(contact.getAvatar());
            results.add(result1);
        }

        return results;

    }


    /**
     * 搜索群聊结果数据拼装
     *
     * @param cx
     * @param myid
     * @param localOrder
     * @return
     */
    public static List<SearchMsgResult> db_find_search_group_detail(Context cx, long myid, String localOrder) {
        List<SearchMsgResult> results = new ArrayList<SearchMsgResult>();
        List<Group> groupList = GroupDaoHelper.getInstance(cx).findLike(myid, localOrder);

        for (Group group : groupList) {
            SearchMsgResult result = new SearchMsgResult();
            result.setLayoutType(1);
            result.setGroupType(1);
            result.setChat_id(group.getGroup_id());
            result.setChat_type(1);
            result.setTitle(group.getGroup_nick_name());
            result.setUserImage(group.getGroup_avatar());
            results.add(result);
        }

        return results;

    }


    public static String db_getNewFriend(Context context, long myid) {
        return new Gson().toJson(NewFriendDaoHelper.getInstance(
                context).findAll(myid));
    }

    /**
     * 通过cateId找服务号
     *
     * @param cateId
     * @return zhangwb
     */
    public static String db_getServiceMsg(Context context, long myid, long cateId) {
        return new Gson().toJson(ServiceMessageDaoHelper.getInstance(
                context).findAll(myid, cateId));
    }

    /**
     * 通过cateId找服务号最近
     *
     * @param cateId
     * @return zhangwb
     */
    public static List<ServiceMessageRecent> db_getServiceMsgRe(Context context, long myid, long cateId) {
        return ServiceMessageRecentDaoHelper.getInstance(
                context).findAll(myid, cateId);
    }

    /**
     * 通过serId和cateId找服务号
     *
     * @param cateId
     * @return zhangwb
     */
    public static String db_getServiceMsgByServiceid(Context context, long myid, long cateId, long serviceMsgId) {
        return new Gson().toJson(ServiceMessageDaoHelper.getInstance(
                context).findAll(myid, cateId, serviceMsgId));
        // if (queryType == IConst.QUERY_MSG_FIRST) {
        // return new Gson().toJson(ServiceMessageDaoHelper.getInstance(
        // this.getManager().getContext()).findWhileFirstIn(
        // cateId, serviceMsgId));
        // } else if (queryType == IConst.QUERY_MSG_UP) {
        // return new Gson().toJson(ServiceMessageDaoHelper.getInstance(
        // this.getManager().getContext()).findUp(
        // cateId, serviceMsgId, session_order));
        // } else {
        // return new Gson().toJson(ServiceMessageDaoHelper.getInstance(
        // this.getManager().getContext()).findWhileNewMessageCome(
        // cateId, serviceMsgId, session_order));
        // }
    }

    /**
     * 删除全部新朋友
     * by  zhangwb
     *
     * @param context
     * @param myid
     */
    public static void db_clearNewFriend(Context context, long myid) {
        NewFriendDaoHelper.getInstance(context)
                .deleteAll(myid);
    }

    /**
     * 逐条删除新朋友
     * by zhangwb
     *
     * @param context
     * @param myid
     * @param chat_id
     */
    public static void db_delNewFriend(Context context, long myid, long chat_id) {
        NewFriend nf = NewFriendDaoHelper.getInstance(
                context).find(myid, chat_id);
        NewFriendDaoHelper.getInstance(context).delete(nf.getId());
    }

    /**
     * 获取联系人
     * by zhangwb
     *
     * @param context
     * @param myid
     * @return
     */
    public static String db_getContact(Context context, long myid) {
        return new Gson().toJson(ContactDaoHelper.getInstance(
                context).findAll(myid));
    }

    /**
     * 获取联系人
     * 直接返list，不再来回转化倒数据
     */
    public static List<Contact> db_getContacts(Context context, long myid) {
        return ContactDaoHelper.getInstance(context).findAll(myid);
    }

    public static Contact db_getContactByid(Context cx, long myid, long chat_id) {
        return ContactDaoHelper.getInstance(cx).find(myid, chat_id);
    }

    /**
     * 获取最近联系人
     * by zhangwb
     *
     * @param context
     * @param myid
     * @return
     */
    public static List<MessageRecent> db_getMessageRecent(Context context, long myid) {
        return MessageRecentDaoHelper.getInstance(
                context).findWithUser(context, myid);
    }

    /**
     * 获取最近联系人(除去服务号)
     *
     * @param context
     * @param myid
     * @return
     */
    public static String db_getMsgRecent(Context context, long myid) {
        return new Gson().toJson(MessageRecentDaoHelper.getInstance(
                context).findRecentWithUser(context, myid));
    }

    /**
     * 获取最近联系人(除去服务号)
     *
     * @param context
     * @param myid
     * @return
     */
    public static JSONArray db_getMsgRecentList(Context context, long myid) {
        return MessageRecentDaoHelper.getInstance(
                context).findRecentWithUserList(context, myid);
    }


    /**
     * 获取最近聊天记录
     *
     * @param context
     * @param myid
     * @param chatid
     */
    public static MessageRecent db_getMessageRecentByMyid(Context context, long myid, long chatid) {
        return MessageRecentDaoHelper.getInstance(context).find(myid, chatid);
    }


    public static void db_clearFriendBubble(Context cx, long myid) {
        ConfigDaoHelper.getInstance(cx).cleanFriendBubble(myid);
    }

    public static void dbClearCateBubble(Context cx, long myid, long cateId) {
        CateDaoHelper.getInstance(cx).clearCateBubble(myid, cateId);
    }

    /**
     * 密信插入当前时间
     */
    public static void db_updateMessageHistoryTime(Context cx, long myid, String uuid, int content_type,
                                                   long chat_id, long currentTime) {
        MessageHistoryDaoHelper.getInstance(cx)
                .updateCurrentTime(uuid, content_type,
                        myid, chat_id, currentTime);
    }

    /**
     * 改变消息状态
     */
    public static void updateStatus(Context cx, long myid, String uuid,
                                                           int content_type, long chat_id, int isRead) {
        MessageHistoryDaoHelper.getInstance(cx)
                .updateStatus(uuid, content_type, myid, chat_id, isRead);
    }

    /**
     * 根据group_id, user_id 查询member
     *
     * @param group_id
     * @param user_id
     */
    public static GroupMembers db_findMemberListByGroupidandUid(Context cx, long myid, long group_id, long user_id) {
        return GroupMembersDaoHelper.getInstance(cx).find(myid, group_id, user_id);

    }

    public static String[] db_getFinalName(Context cx, long myid, long gid, long memberId) {
        return GroupMembersDaoHelper.getInstance(cx).getFinalName(myid, gid, memberId);
    }

    /**
     * 插入草稿内容
     *
     * @param cx
     * @param myid
     * @param chat_id
     * @param content
     */
    public static void db_insertDraftContent(Context cx, long myid, long chat_id, String content) {
        MessageRecent mMessageRecent = MessageRecentDaoHelper.getInstance(cx).find(myid, chat_id);
        Contact contact = ContactDaoHelper.getInstance(cx).find(myid, chat_id);
        Group group = GroupDaoHelper.getInstance(cx).find(myid, chat_id);
        if (mMessageRecent != null) {
            mMessageRecent.setDrafttext(content);
            MessageRecentDaoHelper.getInstance(cx).save(mMessageRecent);
        }

        if (null != contact || null != group) {
            if (!TextUtils.isEmpty(content)) {
                MessageRecent bean = new MessageRecent();
                bean.setDrafttext(content);
                bean.setChat_id(chat_id);
                bean.setChat_type(contact != null ? contact.getChat_type() : 1);
                //bean.setContent_type(content);
                bean.setDate(System.currentTimeMillis());
                //bean.setStatus(contact.getStatus());
                bean.setMyid(myid);
                MessageRecentDaoHelper.getInstance(cx).save(bean);
            }
        }
        //解决搜小悦删除之后，从通讯录进入不出现草稿问题
        if (!TextUtils.isEmpty(content) && null == mMessageRecent && contact == null && null == group) {
            MessageRecent messageRecent = new MessageRecent();
            messageRecent.setDate(System.currentTimeMillis());
            messageRecent.setChat_id(chat_id);
            messageRecent.setMyid(myid);
            messageRecent.setDrafttext(content);
            MessageRecentDaoHelper.getInstance(cx).save(messageRecent);
        }

    }

    /**
     * 插入带有at的草稿内容
     *
     * @param cx
     * @param myid
     * @param chat_id
     * @param draftForAtcontent
     */
    public static void db_insertDraftForAtContent(Context cx, long myid, long chat_id, String draftContent, String draftForAtcontent) {
        MessageRecent mMessageRecent = MessageRecentDaoHelper.getInstance(cx).find(myid, chat_id);
        if (mMessageRecent != null) {
            mMessageRecent.setDrafttext(draftContent);
            mMessageRecent.setDraftforat(draftForAtcontent);
            MessageRecentDaoHelper.getInstance(cx).save(mMessageRecent);
        }

    }

    /**
     * 每次im登陆会检验版本号，如果版本号不同，lastupdate清0
     * 4.1max增加 本地数据库自动生成搜小悦
     *
     * @param cx
     * @param myid
     * @param newVersion
     */
    public static void checkVersion(Context cx, long myid, String newVersion) {
        Config config = ConfigDaoHelper.getInstance(cx).find(myid);
        if (config != null) {
            String oldVersion = config.getBy1();
            if (oldVersion == null || !newVersion.equals(oldVersion)) {
                config.setContact_last_update(0);
                config.setBy1(newVersion);
                ConfigDaoHelper.getInstance(cx).update(config);
            }
        }
        createSouxiaoyue(cx, myid);
    }


    /**
     * 4.1max增加 本地数据库自动生成搜小悦
     *
     * @param cx
     * @param myid
     */
    public static void createSouxiaoyue(Context cx, long myid) {

        ServiceMessageRecent serviceMessageRe = ServiceMessageRecentDaoHelper.getInstance(cx).findWithCate(myid, IConst.SOUXIAOYUE_ID, IConst.SOUXIAOYUE_ID);
        if (serviceMessageRe == null) {
            ServiceMessageRecent serviceMessageRecent = new ServiceMessageRecent();
            serviceMessageRecent.setMyid(myid);
            serviceMessageRecent.setService_id(IConst.SOUXIAOYUE_ID);
            serviceMessageRecent.setCate_id(IConst.SOUXIAOYUE_ID);
            serviceMessageRecent.setService_name(IConst.SOUYXIAOYUE_NAME);
            serviceMessageRecent.setService_avatar(IConst.SOUXIAOYUE_URL);
            serviceMessageRecent.setCate_name(IConst.SOUYXIAOYUE_NAME);
            serviceMessageRecent.setCate_avatar(IConst.SOUXIAOYUE_URL);
            serviceMessageRecent.setBy2("1");
            serviceMessageRecent.setBubble_num("0");
            ServiceMessageRecentDaoHelper.getInstance(cx).save(serviceMessageRecent);
        }

    }

    /**
     * 根据group_id 查询member
     *
     * @param context
     * @param myid
     * @param group_id
     * @return
     */
    public static List<GroupMembers> db_findMemberListByGroupid(Context context, long myid, long group_id) {
        return GroupMembersDaoHelper.getInstance(context).findMemebers(myid, group_id);
    }

    /**
     * 查询保存到通讯录的群
     *
     * @param userid
     * @param is_saved 是否保存到通讯录
     * @return
     */
    public static String db_findGroupListByGroupidAndIsSaved(Context context, long userid, int is_saved) {
        return new Gson().toJson(GroupDaoHelper.getInstance(context)
                .db_findGroupListByGroupidAndIsSaved(userid, is_saved));
    }

    public static String db_findGroupListByUserid(Context context, long userid) {
        return new Gson().toJson(GroupDaoHelper.getInstance(context)
                .db_findGroupListByUserid(userid));
    }

    public static void db_updateCommentName(Context context, long myid, long chat_id, String commentName) {
        ContactDaoHelper.getInstance(context).updateCommentName(myid, chat_id, commentName);
    }

    public static Group db_findGourp(Context context, long myid, long group_id) {
        return GroupDaoHelper.getInstance(context).find(myid, group_id);
    }

    public static Group db_updateGroup(Context context, long myid, long group_id) {
        return GroupDaoHelper.getInstance(context).find(myid, group_id);
    }

    public static void db_clearMessageRecentBubble(Context cx, long myid, long chat_id) {
        MessageRecent recent = MessageRecentDaoHelper.getInstance(cx).find(myid,
                chat_id);
        if (recent != null && recent.getBubble_num() > 0) {
            MessageRecentDaoHelper.getInstance(cx)
                    .cleanBubble(myid, chat_id);
            int count = MessageRecentDaoHelper.getInstance(
                    cx).countBubble(
                    myid);
            ConfigDaoHelper.getInstance(cx)
                    .updateMessageBubble(myid, count);
            BroadcastUtil.sendBroadcastToUI(cx,
                    BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE, null, false);
        }
    }

    public static void db_updateSouyueMessageRecentBubble(Context cx, long myid, long chat_id, long service_id) {
        MessageRecent recent = MessageRecentDaoHelper.getInstance(
                cx).find(myid, chat_id);
        ServiceMessageRecent serviceMsgRe = ServiceMessageRecentDaoHelper
                .getInstance(cx).findWithCate(myid, service_id, chat_id);
        if (recent != null && recent.getBubble_num() > 0) {
            int bubble_count = recent.getBubble_num()
                    - Integer.parseInt(serviceMsgRe.getBubble_num());
            MessageRecentDaoHelper.getInstance(cx)
                    .addBubble(myid, chat_id, bubble_count);
            int count = MessageRecentDaoHelper.getInstance(cx).countBubble(myid);
            ConfigDaoHelper.getInstance(cx)
                    .updateMessageBubble(myid, count);
            BroadcastUtil.sendBroadcastToUI(cx,
                    BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE, null, false);
        }
    }

    public static ServiceMessageRecent db_getServiceMessageRecent(Context cx, long myid, long serviceId, long cateId) {
        ServiceMessageRecent serviceMsgRe = ServiceMessageRecentDaoHelper
                .getInstance(cx).findWithCate(myid, serviceId, cateId);
        return serviceMsgRe;
    }

    /**
     * 查找目标用户的服务号
     *
     * @param cx
     * @param myid
     * @param serviceId
     * @return
     */
    public static ServiceMessageRecent db_getTargetServiceMsgRe(Context cx, long myid, long serviceId) {
        ServiceMessageRecent serviceMsgRe = ServiceMessageRecentDaoHelper
                .getInstance(cx).find(myid, serviceId);
        return serviceMsgRe;
    }

    /**
     * 清除服务号最近气泡
     * by zhangwb
     *
     * @param context
     * @param myid
     * @param serviceId
     * @param cateId
     */
    public static void db_clearSouyueMessageRecentBubble(Context context, long myid, long serviceId, long cateId) {
        ServiceMessageRecent recent = ServiceMessageRecentDaoHelper
                .getInstance(context).findWithCate(
                        myid, serviceId, cateId);
        if (recent != null && Integer.parseInt(recent.getBubble_num()) > 0) {
            ServiceMessageRecentDaoHelper.getInstance(
                    context).cleanBubble(
                    myid, serviceId);
//			int count = MessageRecentDaoHelper.getInstance(
//					this.getManager().getContext()).countBubble(
//					this.getOwner().getUid());
//			ConfigDaoHelper.getInstance(this.getManager().getContext())
//					.updateMessageBubble(this.getOwner().getUid(), count);
            BroadcastUtil.sendBroadcastToUI(context,
                    BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE, null, false);
        }
    }

    /**
     * 根据id最近聊天删除
     * by zhangwb
     *
     * @param context
     * @param myid
     * @param chat_id
     */
    public static void db_delMessageRecent(Context context, long myid, long chat_id) {
        MessageRecent recent = MessageRecentDaoHelper.getInstance(
                context).find(myid,
                chat_id);
        if (recent != null) {
            MessageRecentDaoHelper.getInstance(context)
                    .delete(recent.getId());
            MessageHistoryDaoHelper.getInstance(context)
                    .deleteAll(myid, chat_id, recent.getChat_type());
            if (recent.getBubble_num() > 0) {
                int count = MessageRecentDaoHelper.getInstance(
                        context).countBubble(
                        myid);
                ConfigDaoHelper.getInstance(context)
                        .updateMessageBubble(myid, count);
                BroadcastUtil.sendBroadcastToUI(context,
                        BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE, null, false);
            }
        }
    }

    /**
     * 置顶相关操作
     */
    public static void db_ToTopMessageRecent(Context context, long myid, long chat_id, String by3) {
        MessageRecentDaoHelper.getInstance(context).updateBy3(myid, chat_id, by3);
    }

    public static String db_getConfig(Context cx, long myid) {
        Config config = ConfigDaoHelper.getInstance(
                cx).find(myid);
        if (config == null) {
            config = new Config();
            config.setMyid(myid);
            ConfigDaoHelper.getInstance(cx).insert(
                    config);
        }
        return new Gson().toJson(config);
    }

    public static void db_updateRecent(Context context, MessageRecent msg) {
        MessageRecentDaoHelper.getInstance(context).save(msg);
    }

    public static void db_updateRecentBy1(Context context, long chatid, long myid, String num) {
        MessageRecentDaoHelper.getInstance(context).updateBy1(myid, chatid, num);
    }

    public static void db_updateRecentTime(Context context, long chatid, long myid, long time) {
        MessageRecentDaoHelper.getInstance(context).updateTime(myid, chatid, time);
    }

    public static long db_findMemberCountByGroupid(Context context, long group_id, long myid) {
        return GroupMembersDaoHelper.getInstance(context).findMemberCountByGroupid(group_id, myid);
    }

    /**
     * 更新各种信息
     *
     * @param op   6 更新基本信息，消息免打扰的三个数据
     *
     * @param
     */
    public boolean setUserMessageNotify(int op,long uid, boolean isNewsNotifyShielded, boolean hasSound, boolean isVibrate){
        if (this.getTuitaIMState() == TUITA_IM_STATE_CONNECT) {
            ImCommand cmd = ImCommand.newUpdateCmd(this, op,uid, isNewsNotifyShielded,hasSound,isVibrate);
            manager.getConnection().write(cmd.getPacket());
            runningCmd.put(cmd.getTid(), cmd);
            requestTimeOut(manager, cmd.getTid());
            return true;
        } else {
            //重连
            manager.reConnectIM();
            return false;
        }
    }
    /**
     * protected static final int TUITA_MSG_TYPE_IM_CONNECT = 100; protected
     * static final int TUITA_MSG_TYPE_IM_MESSAGE_ACK = 101; protected static
     * final int TUITA_MSG_TYPE_IM_MESSAGE_OFFLINE = 102; protected static final
     * int TUITA_MSG_TYPE_IM_MESSAGE_ONLINE = 103; protected static final int
     * TUITA_MSG_TYPE_IM_MESSAGE = 104; protected static final int
     * TUITA_MSG_TYPE_IM_MESSAGE_HISTORY = 105; protected static final int
     * TUITA_MSG_TYPE_IM_RELATION_USER = 106; protected static final int
     * TUITA_MSG_TYPE_IM_RELATION_GROUP = 107; protected static final int
     * TUITA_MSG_TYPE_IM_UPDATE = 108; protected static final int
     * TUITA_MSG_TYPE_IM_INFO = 109; protected static final int
     * TUITA_MSG_TYPE_IM_LOGOUT = 110; protected static final int
     * TUITA_MSG_TYPE_IM_CONTACTS_STATUS = 111; protected static final int
     * TUITA_MSG_TYPE_IM_USER_SEARCH = 112; protected static final int
     * TUITA_MSG_TYPE_IM_ZSB_GIFT = 113;
     *
     * @param type
     * @param json
     */
    @SuppressWarnings("rawtypes")
//    long time = 0;

    public void dealRead(int type, JSONObject json) throws Exception {
        int codeFirst = getJsonInt(json, "code");
        if (codeFirst == TUITA_IM_RETURN_CODE_ERROR_RECONNECT) {      //4.1最后新增服务器通知重连
            //错误码及重连
            manager.reConnectIM();
        }
        if (type == TuitaPacket.TUITA_MSG_TYPE_CONNECT) {         //t=1
            //========================================================//
            this.setImConnState(TuitaIMManager.CONN_STATE_NOTCONNECT);
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                // this.token = getJson(json, "msg");
                if (this.getTuitaIMState() != TuitaIMManager.TUITA_IM_STATE_CONNECT) {
                    this.setTuitaIMState(TuitaIMManager.TUITA_IM_STATE_CONNECT);
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(),
                            BroadcastUtil.ACTION_CONNECT_SUCCESS, null, false);
                }
//                tid = getJsonLong(json, "tid");
//                long tid = getJsonLong(json, "tid");
                if (tid > 0) {
                    ack(tid);
                }
                getJsonInt(json, "maxGroupMembers");
                JSONObject contacts = getJsonObject(json, "contacts");
                if (contacts != null) {
                    parseInfo(contacts, false);
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(),
                            BroadcastUtil.ACTION_CONTACT_AND_MSG, null, false);
                }
                JSONArray data = json.getJSONArray("data");
                if (data != null && data.length() > 0) {
                    parseChatMessage(data, false);

                    MessageHistory msg = new MessageHistory();
                    msg.setAction(BroadcastUtil.ACTION_MSG_ADD);
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(), BroadcastUtil.ACTION_MSG_ADD, null,
                            false);
                    Config config = ConfigDaoHelper.getInstance(
                            this.getManager().getContext()).find(
                            this.getOwner().getUid());
                    if (config != null && config.getTotal_message_bubble() > 0) {
                        BroadcastUtil.sendBroadcastToUI(this.getManager()
                                        .getContext(), BroadcastUtil.ACTION_MSG,
                                new Gson().toJson(msg), true);
                    }
                }
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
            } else {         //错误码及重连
                manager.reConnectIM();
            }
            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_CONNECT) {            //100  im上线

            /*
             * this.setImConnState(TuitaIMManager.CONN_STATE_NOTCONNECT); int
			 * code = getJsonInt(json, "code"); if (code ==
			 * TUITA_IM_RETURN_CODE_SUCCESS) { //this.token = getJson(json,
			 * "msg");
			 * this.setTuitaIMState(TuitaIMManager.TUITA_IM_STATE_CONNECT);
			 * Log.i(TuitaSDKManager.TAG, "----------------im connect success");
			 * BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
			 * BroadcastUtil.ACTION_CONNECT_SUCCESS, null, false); } else {
			 * BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
			 * BroadcastUtil.ACTION_CONNECT_FAIL, null, false); }
			 */
            this.setImConnState(TuitaIMManager.CONN_STATE_NOTCONNECT);
            int code = getJsonInt(json, "code");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                // this.token = getJson(json, "msg");
                if (this.getTuitaIMState() != TuitaIMManager.TUITA_IM_STATE_CONNECT) {
                    this.setTuitaIMState(TuitaIMManager.TUITA_IM_STATE_CONNECT);
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(),
                            BroadcastUtil.ACTION_CONNECT_SUCCESS, null, false);
                }
                long tid = getJsonLong(json, "tid");
                if (tid > 0) {
                    ack(tid);
                }
                getJsonInt(json, "maxGroupMembers");
                JSONObject contacts = getJsonObject(json, "contacts");
                if (contacts != null) {
                    parseInfo(contacts, false);
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(),
                            BroadcastUtil.ACTION_CONTACT_AND_MSG, null, false);
                }
                JSONArray data = json.getJSONArray("data");
                if (data != null && data.length() > 0) {
                    parseChatMessage(data, false);

                    MessageHistory msg = new MessageHistory();
                    msg.setAction(BroadcastUtil.ACTION_MSG_ADD);
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(), BroadcastUtil.ACTION_MSG_ADD, null,
                            false);
                    Config config = ConfigDaoHelper.getInstance(
                            this.getManager().getContext()).find(
                            this.getOwner().getUid());
                    if (config != null && config.getTotal_message_bubble() > 0) {
                        BroadcastUtil.sendBroadcastToUI(this.getManager()
                                        .getContext(), BroadcastUtil.ACTION_MSG,
                                new Gson().toJson(msg), true);
                    }
                }
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
//                manager.setTuitaState(0);
//                manager.start();
//                TuitaIMManager.this
//                        .setImConnState(TuitaIMManager.CONN_STATE_NOTCONNECT);
                // BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                // BroadcastUtil.ACTION_CONNECT_FAIL, null, false);
            } else {         //错误码及重连
                manager.reConnectIM();
            }

        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_MESSAGE_OFFLINE) {         // 102 离线消息（已废弃）

        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_MESSAGE_ONLINE) {      //103接收各种消息
            if (this.getTuitaIMState() != TuitaIMManager.TUITA_IM_STATE_CONNECT) {
                this.setTuitaIMState(TuitaIMManager.TUITA_IM_STATE_CONNECT);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_CONNECT_SUCCESS, null, false);
            }
            ack(getJsonLong(json, "tid"));
            // 把nickname和头像加到广播里
            JSONArray data = json.getJSONArray("data");
            JSONObject obj = data.getJSONObject(0);
            int mt = obj.getInt("mt");
            JSONArray msgsObj1 = obj.getJSONArray("msgs");
            boolean isExist = MessageMidDaoHelper.getInstance(this.getManager().getContext()).isContraintAndinsert(msgsObj1.getJSONObject(0).getLong("mid") + this.getOwner().getUid() + "", msgsObj1.getJSONObject(0).getLong("t"));
            if (!isExist) {//判断mid是否重复
                MessageHistory msg = parseChatMessage(data, true);
                if (msg == null) {
                    msg = new MessageHistory();
                }
                msg.setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                if (mt == IConst.CHAT_TYPE_GROUP) {
                    GroupMembers groupMembers = db_findMemberListByGroupidandUid(this.getManager().getContext(), this.getOwner().getUid(), obj.getLong("id"),
                            msgsObj1.getJSONObject(0).getLong("sid"));
                    if (groupMembers != null) {
                        String comment_name = null;
                        Contact contact_group = ContactDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), msgsObj1.getJSONObject(0).getLong("sid"));
                        if (contact_group != null) {
                            comment_name = contact_group.getComment_name();
                        }
                        JSONObject object = new JSONObject();
                        object.put(
                                "nickname",
                                TextUtils.isEmpty(comment_name) ? (TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers
                                        .getNick_name() : groupMembers.getMember_name()) : comment_name);
                        object.put("userImage", groupMembers.getMember_avatar());
                        msg.setBy2(object.toString());

                        //判断是否提醒消息
                        Group group = GroupDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), obj.getLong("id"));
                        if (group != null) {
                            mIsShowNfBar = group.getIs_news_notify() == 1 ? true : false;
                        }
                    }
                    if (msgsObj1.getJSONObject(0).getLong("sid") == this.getOwner().getUid()) {
                        mIsShowNfBar = true;
                    }

                } else if (mt == IConst.CHAT_TYPE_PRIVATE) {
                    Contact contact = ContactDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), msgsObj1.getJSONObject(0).getLong("sid"));
                    if (contact != null) {
                        mIsShowNfBar = contact.getIs_news_notify() == 1 ? true : false;
                    }
                    if (msgsObj1.getJSONObject(0).getLong("sid") == this.getOwner().getUid()) {
                        mIsShowNfBar = true;
                    }
                }
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MSG_ADD, null, false);
                msg.setDate(msgsObj1.getJSONObject(0).getLong("t"));
                if(DeviceUtil.getPushChannel()==1){
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(msg), false);
                }else{
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(msg), true);
                }

            }
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_MESSAGE) {     //104 发消息
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            long myid = this.getOwner().getUid();
            MessageHistory msg = new MessageHistory();
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
//                runningCmd.remove(tid);
                String uuid = getJsonString(json, "muid");
                long session_order = 0;
                if (!TextUtils.isEmpty(getJsonString(json, "msg"))) {
                    session_order = Long.parseLong(getJsonString(json, "msg"));
                }
                MessageHistoryDaoHelper.getInstance(
                        this.getManager().getContext()).update(uuid, myid,
                        IMessageConst.STATUS_HAS_SENT, session_order);
                MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).update(uuid, myid,
                        IMessageConst.STATUS_HAS_SENT);
                msg.setUuid(uuid);
                msg.setSession_order(session_order);
                msg.setAction(BroadcastUtil.ACTION_MSG_SEND_SUCCESS);
                BroadcastUtil
                        .sendBroadcastToUI(this.getManager().getContext(),
                                BroadcastUtil.ACTION_MSG,
                                new Gson().toJson(msg), false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_NOTFRIEND) {          //104下601code不是好友
                Logger.i("tuita", "TuitaIMManager.dealRead", "you are not friend----->" + TUITA_IM_RETURN_CODE_ERROR_NOTFRIEND);
                String uuid = getJsonString(json, "muid");
                long session_order = Long.parseLong(getJsonString(json, "msg"));
                MessageHistory preMsg = MessageHistoryDaoHelper.getInstance(
                        this.getManager().getContext()).update(uuid, myid,
                        IMessageConst.STATUS_SENT_FAIL, session_order);
                MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).update(uuid, myid,
                        IMessageConst.STATUS_SENT_FAIL);

                if (preMsg.getChat_type() == IConst.CHAT_TYPE_PRIVATE) {
                    msg.setUuid(uuid);
                    msg.setSession_order(session_order);
                    msg.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                    BroadcastUtil
                            .sendBroadcastToUI(this.getManager().getContext(),
                                    BroadcastUtil.ACTION_MSG,
                                    new Gson().toJson(msg), false);

                    MessageHistory newmsg = new MessageHistory();
                    newmsg.setMyid(this.getOwner().getUid());
                    newmsg.setChat_id(preMsg.getChat_id());
                    newmsg.setSender(this.getOwner().getUid());
                    newmsg.setChat_type(preMsg.getChat_type());
                    newmsg.setContent_type(IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND);
                    newmsg.setDate(System.currentTimeMillis());
                    newmsg.setSession_order(session_order);
                    newmsg.setStatus(IMessageConst.STATUS_HAS_SENT);
                    newmsg.setUuid(genUuid(this.getOwner().getUid(), System.currentTimeMillis()));
                    MessageHistoryDaoHelper.getInstance(this.getManager().getContext())
                            .save(newmsg);
                    newmsg.setAction(BroadcastUtil.ACTION_MSG_SEND_ERROR_NOTFRIEND);
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(newmsg),
                            false);
                }

            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {      //104下403code被踢
                Logger.i("tuita", "TuitaIMManager.dealRead", "kicked out error----->" + code);
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
                ImCommand cmd = runningCmd.get(tid);
                if (cmd != null) {
                    String uuid = cmd.getText();
                    MessageHistoryDaoHelper.getInstance(
                            TuitaIMManager.this.getManager().getContext())
                            .update(uuid, myid, IMessageConst.STATUS_SENT_FAIL,
                                    -1);
                    MessageRecentDaoHelper.getInstance(
                            TuitaIMManager.this.getManager().getContext())
                            .update(uuid, myid, IMessageConst.STATUS_SENT_FAIL);
                    msg.setUuid(uuid);
                    msg.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                    BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this
                                    .getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(msg),
                            false);
                }

            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {          //104下500code服务器错误
                Logger.i("tuita", "TuitaIMManager.dealRead", "server error----->" + code);
                ImCommand cmd = runningCmd.get(tid);
                if (cmd != null) {
                    String uuid = cmd.getText();
                    MessageHistoryDaoHelper.getInstance(
                            TuitaIMManager.this.getManager().getContext())
                            .update(uuid, myid, IMessageConst.STATUS_SENT_FAIL,
                                    -1);
                    MessageRecentDaoHelper.getInstance(
                            TuitaIMManager.this.getManager().getContext())
                            .update(uuid, myid, IMessageConst.STATUS_SENT_FAIL);
                    msg.setUuid(uuid);
                    msg.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                    BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this
                                    .getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(msg),
                            false);
                }
                //错误码及重连
                manager.reConnectIM();
            } else {
                Logger.i("tuita", "TuitaIMManager.dealRead", "other error----->" + code);
                ImCommand cmd = runningCmd.get(tid);
                if (cmd != null) {
                    String uuid = cmd.getText();
                    MessageHistoryDaoHelper.getInstance(
                            TuitaIMManager.this.getManager().getContext())
                            .update(uuid, myid, IMessageConst.STATUS_SENT_FAIL,
                                    -1);
                    MessageRecentDaoHelper.getInstance(
                            TuitaIMManager.this.getManager().getContext())
                            .update(uuid, myid, IMessageConst.STATUS_SENT_FAIL);
                    msg.setUuid(uuid);
                    msg.setAction(BroadcastUtil.ACTION_MSG_SEND_FAIL);
                    BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this
                                    .getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(msg),
                            false);
                }
            }
            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_RELATION_USER) {       //106私聊好友关系
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                ImCommand cmd = runningCmd.get(tid);
                if (cmd.getType() == TuitaPacket.TUITA_MSG_TYPE_IM_RELATION_USER) {
                    NewFriend nf = null;
                    Contact contact = null;
                    switch (cmd.getOp()) {
                        case TUITA_IM_USEROP_ADD:
                            nf = existFriend(cmd.getContact().getChat_id());
                            if (nf != null) {
//                                nf.setMyid(this.getOwner().getUid());
//                                nf.setChat_id(cmd.getContact().getChat_id());
//                                nf.setChat_type(IConst.CHAT_TYPE_PRIVATE);
//                                nf.setNick_name(cmd.getContact().getNick_name());
//                                nf.setAvatar(cmd.getContact().getAvatar());
                                nf.setStatus(NewFriend.STATUS_WAITING_ALLOW);
//                                nf.setAllow_text(cmd.getText());
                                NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).save(nf);
                            }
                            break;
                        case TUITA_IM_USEROP_AGREE:
                            nf = existFriend(cmd.getContact().getChat_id());
                            if (nf != null) {
                                nf.setStatus(NewFriend.STATUS_HAS_ADD);
                                if (!"".equals(getJsonString(json, "pn"))) {
                                    nf.setBy1(getJsonString(json, "pn"));
                                }
                                NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).save(nf);
                                contact = new Contact();
                                contact.setMyid(this.getOwner().getUid());
                                contact.setChat_id(cmd.getContact().getChat_id());
                                contact.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                                contact.setNick_name(cmd.getContact()
                                        .getNick_name());
                                contact.setAvatar(cmd.getContact().getAvatar());
                                if (!"".equals(getJsonString(json, "pn"))) {
                                    contact.setPhone(getJsonString(json, "pn"));
                                    if (!"".equals(getJsonString(json, "am"))
                                            && getJsonString(json, "am").equals(
                                            TUITA_IM_SYSMSG_TYPE_IM)) {
                                        contact.setBy2(IConst.CONTACT_PHONE_RECOMMEND);
                                    } else {
                                        contact.setBy2(IConst.CONTACT_PHONE_MATCHING);
                                    }
                                }

                                ContactDaoHelper.getInstance(
                                        this.getManager().getContext()).save(
                                        contact);

                                MessageRecent recent = MessageRecentDaoHelper
                                        .getInstance(this.getManager().getContext())
                                        .find(this.getOwner().getUid(),
                                                contact.getChat_id());
                                if (recent == null) {
                                    MessageHistory msg = new MessageHistory();
                                    msg.setMyid(this.getOwner().getUid());
                                    msg.setChat_id(contact.getChat_id());
                                    msg.setSender(this.getOwner().getUid());
                                    msg.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                                    msg.setContent(IConst.FIRST_MESSAGE);
                                    msg.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                                    msg.setDate(System.currentTimeMillis());
                                    msg.setSession_order(IConst.FIRST_SESSION_ORDER);
                                    msg.setUuid(genUuid(this.getOwner().getUid(),
                                            msg.getDate()));
                                    msg.setStatus(IMessageConst.STATUS_HAS_SENT);
                                    MessageHistoryDaoHelper.getInstance(
                                            this.getManager().getContext()).save(
                                            msg);
                                    MessageRecent chat = new MessageRecent();
                                    chat.setMyid(this.getOwner().getUid());
                                    chat.setChat_id(contact.getChat_id());
                                    chat.setSender(msg.getSender());
                                    chat.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                                    chat.setContent(msg.getContent());
                                    chat.setChat_type(msg.getContent_type());
                                    chat.setDate(msg.getDate());
                                    chat.setUuid(msg.getUuid());
                                    chat.setStatus(msg.getStatus());
                                    MessageRecentDaoHelper.getInstance(
                                            this.getManager().getContext()).save(
                                            chat);
                                }
                            }
                            BroadcastUtil.sendBroadcastToUI(this.getManager()
                                            .getContext(),
                                    BroadcastUtil.ACTION_CONTACT_AND_MSG, null,
                                    false);
                            break;
                        case TUITA_IM_USEROP_REFUSE:
                            nf = existFriend(cmd.getContact().getChat_id());
                            if (nf != null) {
                                NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).delete(
                                        nf.getId());
                            }
                            break;
                        case TUITA_IM_USEROP_DEL:
                            nf = NewFriendDaoHelper.getInstance(
                                    this.getManager().getContext()).find(
                                    this.getOwner().getUid(),
                                    cmd.getContact().getChat_id());
                            if (nf != null) {
                                // NewFriendDaoHelper.getInstance(this.getManager().getContext()).delete(nf.getId());
                                nf.setMyid(this.getOwner().getUid());
                                nf.setChat_id(cmd.getContact().getChat_id());
                                nf.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                                nf.setNick_name(cmd.getContact().getNick_name());
                                nf.setAvatar(cmd.getContact().getAvatar());
                                nf.setStatus(NewFriend.STATUS_ALLOW);
                                nf.setAllow_text(cmd.getText());
                                NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).save(nf);
                            }
                            contact = ContactDaoHelper.getInstance(
                                    this.getManager().getContext()).find(
                                    this.getOwner().getUid(),
                                    cmd.getContact().getChat_id());
                            if (contact != null) {
                                ContactDaoHelper.getInstance(
                                        this.getManager().getContext()).delete(
                                        contact.getId());
                            }
                            MessageRecent recent = MessageRecentDaoHelper
                                    .getInstance(this.getManager().getContext())
                                    .find(this.getOwner().getUid(),
                                            cmd.getContact().getChat_id());
                            if (recent != null) {
                                MessageRecentDaoHelper.getInstance(
                                        this.getManager().getContext()).delete(
                                        recent.getId());
                                MessageHistoryDaoHelper.getInstance(
                                        this.getManager().getContext()).deleteAll(
                                        this.getOwner().getUid(),
                                        cmd.getContact().getChat_id(), recent.getChat_type());
                                int count = MessageRecentDaoHelper.getInstance(
                                        this.getManager().getContext())
                                        .countBubble(this.getOwner().getUid());
                                ConfigDaoHelper.getInstance(
                                        this.getManager().getContext())
                                        .updateMessageBubble(
                                                this.getOwner().getUid(), count);
                            }
                            BroadcastUtil.sendBroadcastToUI(this.getManager()
                                            .getContext(),
                                    BroadcastUtil.ACTION_CONTACT_AND_MSG, null,
                                    false);
                            break;

                        case TUITA_IM_USEROP_ISNEWSNOTIFY:      //私聊消息提醒
                            ContactDaoHelper.getInstance(this.getManager().getContext()).updateNewsNotify(this.getOwner().getUid(), cmd.getUid(), cmd.isNewsNotifyShielded() ? 1 : 0);
                            BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_SUCCESS_DIALOG, null, false);
                            break;
                    }
                }
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
            } else {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }

            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_RELATION_GROUP) { // 107群聊返回
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                Group group = null;
                GroupMembers groupMembers = null;
                ImCommand cmd = runningCmd.get(tid);
                JSONObject dataJson = getJsonObject(json, "data");
                if (cmd.getType() == TuitaPacket.TUITA_MSG_TYPE_IM_RELATION_GROUP) {
                    switch (cmd.getOp()) {
                        case TUITA_IM_CREATE_GROUP: // 创建群

                            break;
                        case TUITA_IM_ADD_FRIENDS_GROUP: // 加好友入群
                            break;
                        case TUITA_IM_DELETE_GROUP_MEMBER: // 删除群好友

                            break;
                        case TUITA_IM_EXIT_GROUP: // 退群
                            BroadcastUtil.sendBroadcastToUI(this.getManager()
                                            .getContext(), BroadcastUtil.ACTION_GROUP_EXIT,
                                    null, false);
                            break;
                        case TUITA_IM_GROUP_CONFIG: // 保存配置信息
                            group = GroupDaoHelper.getInstance(
                                    this.getManager().getContext()).find(this.getOwner().getUid(),
                                    cmd.getUid());
                            if (cmd.isGroupSaved()) {
                                group.setIs_group_saved(1);
                            } else {
                                group.setIs_group_saved(0);
                            }

                            if (cmd.isNewsNotifyShielded()) {
                                group.setIs_news_notify(1);
                            } else {
                                group.setIs_news_notify(0);
                            }

                            GroupDaoHelper.getInstance(
                                    this.getManager().getContext()).save(group);

                            break;
                        case TUITA_IM_UPDATE_GROUP_NAME: // 修改群昵称
                            break;
                        case TUITA_IM_UPDATE_GROUP_MEMBER_NAME: // 修改群成员自己的昵称
                            break;
                        case TUITA_IM_GROUP_DETAIL: // 获取群详细信息
                            group = new Group();
                            group.setGroup_id(dataJson.getLong("gid"));
                            group.setGroup_nick_name(dataJson.getString("nick"));
                            group.setMaxGroupMembers(dataJson
                                    .getInt("maxGroupMembers"));
                            BroadcastUtil.sendBroadcastToUI(this.getManager()
                                            .getContext(), BroadcastUtil.ACTION_GROUP_INFO,
                                    new Gson().toJson(group), false);
                            break;
                        case TUITA_IM_GROUP_MEMBERS_DETAIL: // 获取群成员详细信息
                            groupMembers = GroupMembersDaoHelper.getInstance(
                                    this.getManager().getContext()).find(this.getOwner().getUid(),
                                    dataJson.getLong("gid"),
                                    dataJson.getLong("uid"));
                            if (groupMembers != null) {
                                groupMembers.setGroup_id(dataJson.getLong("gid"));
                                groupMembers.setMember_id(dataJson.getLong("uid"));
                                groupMembers.setSelf_id(this.getOwner().getUid());
                                groupMembers.setNick_name(dataJson.getString("nick"));
                                groupMembers.setMember_name(dataJson
                                        .getString("memberNick"));
                                groupMembers.setMember_avatar(dataJson
                                        .getString("avatar"));

                                GroupMembersDaoHelper.getInstance(
                                        this.getManager().getContext()).save(
                                        groupMembers);
                            }
                            GroupMembersDaoHelper.getInstance(this.getManager().getContext()).updateAvatar(this.getOwner().getUid(), dataJson.getLong("uid"), getJsonString(dataJson, "avatar"), getJsonString(dataJson, "nick"));

                            ContactDaoHelper.getInstance(this.getManager().getContext()).updateNickandAvatar(this.getOwner().getUid(), dataJson.getLong("uid"), getJsonString(dataJson, "nick"), getJsonString(dataJson, "avatar"));
                            break;
                        case TUITA_IM_IS_CONTACT: // 获取用户并查看是否是好友
                            BroadcastUtil.sendBroadcastToUI(this.getManager()
                                            .getContext(),
                                    BroadcastUtil.ACTION_MEMBER_INFO, dataJson
                                            .toString(), false);
                            break;
                        case TUITA_IM_IS_IN_GROUP: // 是否在该群中
                            group = new Group();
                            group.setGroup_id(dataJson.getLong("gid"));
                            group.setGroup_nick_name(dataJson.getString("nick"));
                            group.setMemberCount(dataJson.getInt("memberCount"));
                            group.setOwner_id(dataJson.getLong("ownerId"));
                            if (dataJson.has("avatar")) {
                                group.setAvatar(dataJson.getString("avatar"));
                            }
                            group.setMember(dataJson.getBoolean("isMember"));
                            BroadcastUtil.sendBroadcastToUI(this.getManager()
                                            .getContext(), BroadcastUtil.ACTION_GROUP_INFO,
                                    new Gson().toJson(group), false);
                            break;
                        default:
                            break;
                    }

                }
                // parseInfo(getJsonObject(json, "data"), false);

                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_SUCCESS_DIALOG, null, false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GROUP_CREATE_FAIL, json.toString(), false);
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GROUP_CREATE_FAIL, json.toString(), false);
            } else {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GROUP_CREATE_FAIL, json.toString(), false);
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }

            runningCmd.remove(tid);

        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_UPDATE) {          //108  更新基本信息
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                ImCommand cmd = runningCmd.get(tid);
                if (cmd.getType() == TuitaPacket.TUITA_MSG_TYPE_IM_UPDATE) {
                    Contact contact = null;
                    NewFriend nf = null;
                    switch (cmd.getOp()) {
                        case TUITA_IM_UPDATEOP_EDITALIAS:
                            nf = NewFriendDaoHelper.getInstance(
                                    this.getManager().getContext()).find(
                                    this.getOwner().getUid(), cmd.getUid());
                            if (nf != null) {
                                nf.setComment_name(cmd.getText());
                                NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).save(nf);
                            }
                            contact = ContactDaoHelper.getInstance(
                                    this.getManager().getContext()).find(
                                    this.getOwner().getUid(), cmd.getUid());
                            if (contact != null) {
                                contact.setComment_name(cmd.getText());
                                contact.setBy1(IConst.IM_UPDATEOP_EDITALIAS);
                                ContactDaoHelper.getInstance(
                                        this.getManager().getContext()).save(
                                        contact);
                                BroadcastUtil.sendBroadcastToUI(this.getManager()
                                                .getContext(),
                                        BroadcastUtil.ACTION_CONTACT_AND_MSG, null,
                                        false);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
            } else {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }

            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_INFO) {           //109通讯录及基本信息
            int code = getJsonInt(json, "code");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                parseInfo(getJsonObject(json, "data"), true);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
            } else {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_CONTACTS_STATUS) {    //111通讯录联系人状态
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                ImCommand cmd = runningCmd.get(tid);
                List<Contact> contacts = cmd.getContacts();
                parseContactStatus(json.getJSONObject("data"), contacts);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_SUCCESS, null, false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR, null, false);
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);

                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR, null, false);
            } else {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR, null, false);
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }

            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_USER_SEARCH) {         //112 搜索搜悦用户
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                JSONObject data = json.getJSONObject("data");
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_SEARCH, data.toString(), false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);

                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
            } else {
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }
            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_ZSB_GIFT) {        //113赠送中搜币
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GIFT_ZSB, IConst.GIFT_ZSB_SUCCESS,
                        false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_NOTFRIEND) {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GIFT_ZSB,
                        IConst.GIFT_ZSB_ERROR_NOTFRIEND, false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GIFT_ZSB, IConst.GIFT_ZSB_FAIL,
                        false);
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);

                //错误码及重连
                manager.reConnectIM();
            } else {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_GIFT_ZSB, IConst.GIFT_ZSB_FAIL,
                        false);
                //错误提示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, json.toString(), false);
            }

            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_ZSB_CHARGE) {          //114 短信充值已废弃
            int code = getJsonInt(json, "code");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_CHARGE_ZSB,
                        IConst.CHARGE_ZSB_SUCCESS, false);
            } else {
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_CHARGE_ZSB,
                        IConst.CHARGE_ZSB_FAIL, false);
            }
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_CONTACTS_UPLOAD) {    //115上传通讯录，只上传不反回
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                // ImCommand cmd = runningCmd.get(tid);
                // List<Contact> contacts = cmd.getContacts();
                // parseContactStatus(json.getJSONObject("data"), contacts);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_SUCCESS, null, false);
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_SERVERERROE) {
                //错误提示
//                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),BroadcastUtil.ACTION_ERROR_TIP,json.toString(),false);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR, null, false);

                //错误码及重连
                manager.reConnectIM();
            } else if (code == TUITA_IM_RETURN_CODE_ERROR_OTHERLOGIN || code == TUITA_IM_RETURN_CODE_ERROR_PERMISSION) {
                String token = json.getString("token");
                String msg_kicked = json.getString("msg");
                ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR, null, false);
            } else {
                //错误提示
//                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),BroadcastUtil.ACTION_ERROR_TIP,json.toString(),false);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR, null, false);
            }

            runningCmd.remove(tid);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_KICKED_OUT) {     //116被踢
            JSONObject dataObj = getJsonObject(json, "data");
            String token = dataObj.getString("token");
            String msg_kicked = dataObj.getString("msg");
            ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
        } else if (type == TuitaPacket.TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG) { //118服务号消息免打扰
            int code = getJsonInt(json, "code");
            long tid = getJsonLong(json, "tid");
            if (code == TUITA_IM_RETURN_CODE_SUCCESS) {
                ImCommand cmd = runningCmd.get(tid);
                JSONObject dataJson = getJsonObject(json, "data");
                if (cmd.getType() == TuitaPacket.TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG) {
                    switch (cmd.getOp()) {
                        case 1:     //保存免打扰
                            ServiceMessageRecentDaoHelper.getInstance(this.getManager().getContext()).updateNewsNotify(this.getOwner().getUid(), cmd.getUid(), cmd.isNewsNotifyShielded() ? 1 : 0);
                            BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_SUCCESS_DIALOG, null, false);
                            break;

                        case 2:     //获取服务号详细信息
                            if (dataJson.has("srvCategory")) {
                                JSONObject srvcateJsonObj = getJsonObject(dataJson, "srvCategory");
                                ServiceMessageRecentDaoHelper.getInstance(this.getManager().getContext()).createCate(this.getManager().getContext(), this.getOwner().getUid(), srvcateJsonObj.getLong("cateId"), srvcateJsonObj.getString("cateTitle"), srvcateJsonObj.getString("cateAvatar"), Cate.CATE_HAS_CATEID, 0);
                            }

                            if (dataJson.has("srvAccount")) {
                                JSONObject srvJsonObj = getJsonObject(dataJson, "srvAccount");
                                ServiceMessageRecentDaoHelper.getInstance(this.getManager().getContext()).createServiceRe(this.getManager().getContext(), this.getOwner().getUid(), srvJsonObj);
                            }
                            break;
                    }
                }
            }
            runningCmd.remove(tid);
        }
    }

	/*
     * private void mergeAndDealChats(List<Chat> newChats) { for (Chat chat :
	 * newChats) { Chat oldChat = findChat(chat.getChatid()); if (oldChat ==
	 * null) { oldChat = new Chat(); oldChat.setChatid(chat.getChatid());
	 * oldChat.setType(chat.getType()); }
	 * oldChat.setUnreadNum(oldChat.getUnreadNum() + chat.getUnreadNum());
	 * oldChat.setLastMessageTime(chat.getLastMessageTime()); OrderHashMap<Long,
	 * Long, Message> lastMessages = oldChat.getLastMessages(); if (lastMessages
	 * == null) { lastMessages = new OrderHashMap<Long, Long,
	 * Message>(TUITA_IM_MESSAGE_HISTORY_LENGTH); } //Iterator<Entry<Long,
	 * Message>> it = chat.getLastMessages().entrySet().iterator(); Object[]
	 * msgArr = chat.getLastMessages().toArray(); boolean isNew = true; for
	 * (Object obj : msgArr) { Message msg = (Message) obj; isNew =
	 * lastMessages.add(msg.getSequence(), msg.getMid(), msg); if (isNew) { if
	 * (chat.getType() == Chat.TUITA_IM_MSGTYPE_SYS) {
	 * dealSysMessage(msg.getContent()); } } } while (it.hasNext()) {
	 * Entry<Long, Message> entry = it.next(); lastMessages.put(entry.getKey(),
	 * entry.getValue()); } } }
	 */

    private void dealSysMessage(long myid, JSONObject json, int opType, long t) {
        NewFriend nf = null;
        Contact contact = null;
        Group group = null;
        GroupMembers groupMembers = null;
        // String jsonStr = new Gson().toJson(json);
        try {
            switch (opType) {
                case TUITA_IM_SYSMSG_TYPE_ADDME:
                    nf = NewFriendDaoHelper.getInstance(
                            this.getManager().getContext()).find(myid,
                            json.getLong("uid"));
                    MessageHistory addhistory = new MessageHistory();
                    addhistory.setMyid(this.getOwner().getUid());
                    addhistory.setChat_id(json.getLong("uid"));
                    addhistory.setContent(getJsonString(json, "nick"));
                    addhistory.setChat_type(IConst.CHAT_TYPE_SYSTEM);
                    addhistory.setServiceJumpData("5");
                    if (nf == null) {
                        nf = new NewFriend();
                        nf.setMyid(myid);
                        nf.setChat_id(json.getLong("uid"));
                        nf.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                        nf.setNick_name(getJsonString(json, "nick"));
                        nf.setAvatar(getJsonString(json, "avatar"));
                        nf.setStatus(NewFriend.STATUS_ALLOW);
                        nf.setAllow_text(getJsonString(json, "text"));
                        nf.setOrigin(getJsonString(json, "origin"));

                        NewFriendDaoHelper.getInstance(
                                this.getManager().getContext()).save(nf);

                        mIsShowNfBar = false;
                        BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                                BroadcastUtil.ACTION_MSG, new Gson().toJson(addhistory), true);
                    } else {
                        if (!"".equals(getJsonString(json, "origin"))) {
                            nf.setOrigin(getJsonString(json, "origin"));
                        }
                        if (nf.getStatus() != NewFriend.STATUS_ALLOW) {
                            nf.setAllow_text(getJsonString(json, "text"));
                            if (!"".equals(getJsonString(json, "pn"))) {
                                nf.setBy1(getJsonString(json, "pn"));
                            }
                            nf.setStatus(NewFriend.STATUS_ALLOW);
                            if (nf.getStatus() == NewFriend.STATUS_WAITING_ADD){
                                mIsShowNfBar = false;
                                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                                        BroadcastUtil.ACTION_MSG, new Gson().toJson(addhistory), true);
                            }
                            NewFriendDaoHelper.getInstance(
                                    this.getManager().getContext()).save(nf);

                        } else {
                            nf.setAllow_text(getJsonString(json, "text"));
                            NewFriendDaoHelper.getInstance(
                                    this.getManager().getContext()).save(nf);
                        }
                    }
                    break;
                case TUITA_IM_SYSMSG_TYPE_AGREE:
                    // nf = NewFriendDaoHelper.getInstance(
                    // this.getManager().getContext()).find(myid,
                    // json.getLong("uid"));
                    nf = new NewFriend();
                    // if (nf != null) {
                    nf.setMyid(myid);
                    nf.setChat_id(json.getLong("uid"));
                    nf.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                    nf.setNick_name(getJsonString(json, "nick"));
                    nf.setAvatar(getJsonString(json, "avatar"));
                    nf.setStatus(NewFriend.STATUS_HAS_ADD);
                    nf.setAllow_text(getJsonString(json, "text"));
                    nf.setComment_name(getJsonString(json, "alias"));
                    nf.setOrigin(getJsonString(json, "origin"));

                    if (!"".equals(getJsonString(json, "pn"))) {
                        nf.setBy1(getJsonString(json, "pn"));
                    }
                    if (!"".equals(getJsonString(json, "am"))
                            && getJsonString(json, "am").equals(
                            TUITA_IM_SYSMSG_TYPE_IM)) {
                        nf.setBy2(IConst.NEWFRIEND_PHONE_MATCHING);
                    }
                    NewFriendDaoHelper.getInstance(this.getManager().getContext())
                            .save(nf);
                    // }
                    contact = new Contact();
                    contact.setMyid(myid);
                    contact.setChat_id(json.getLong("uid"));
                    contact.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                    contact.setNick_name(getJsonString(json, "nick"));
                    contact.setAvatar(getJsonString(json, "avatar"));
                    if (!"".equals(getJsonString(json, "alias"))) {
                        contact.setComment_name(getJsonString(json, "alias"));
                    }

                    if (!"".equals(getJsonString(json, "pn"))) {
                        contact.setPhone(getJsonString(json, "pn"));
                        if (!"".equals(getJsonString(json, "am"))
                                && getJsonString(json, "am").equals(
                                TUITA_IM_SYSMSG_TYPE_IM)) {
                            contact.setBy2(IConst.CONTACT_PHONE_RECOMMEND);
                        } else {
                            contact.setBy2(IConst.CONTACT_PHONE_MATCHING);
                        }
                    }

                    ContactDaoHelper.getInstance(this.getManager().getContext())
                            .save(contact);
                    MessageRecent recent = MessageRecentDaoHelper.getInstance(
                            this.getManager().getContext()).find(
                            this.getOwner().getUid(), contact.getChat_id());
                    if (recent == null) {
                        MessageHistory msg = new MessageHistory();
                        msg.setMyid(this.getOwner().getUid());
                        msg.setChat_id(contact.getChat_id());
                        msg.setSender(contact.getChat_id());
                        msg.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                        msg.setContent(IConst.FIRST_MESSAGE);
                        msg.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                        // msg.setDate(System.currentTimeMillis());
                        msg.setDate(t);
                        msg.setSession_order(IConst.FIRST_SESSION_ORDER);
                        msg.setUuid(genUuid(msg.getChat_id(), msg.getDate()));
                        msg.setStatus(IMessageConst.STATUS_HAS_SENT);
                        MessageHistoryDaoHelper.getInstance(
                                this.getManager().getContext()).save(msg);
                        mIsShowNfBar = false;
                        BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                                BroadcastUtil.ACTION_MSG, new Gson().toJson(msg), true);

                        MessageRecent chat = new MessageRecent();
                        chat.setMyid(this.getOwner().getUid());
                        chat.setChat_id(contact.getChat_id());
                        chat.setSender(msg.getSender());
                        chat.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                        chat.setContent(msg.getContent());
                        chat.setChat_type(msg.getContent_type());
                        chat.setDate(msg.getDate());
                        chat.setUuid(msg.getUuid());
                        chat.setStatus(msg.getStatus());
                        chat.setBubble_num(IConst.FIRST_BUBBLE);
                        MessageRecentDaoHelper.getInstance(
                                this.getManager().getContext()).save(chat);
                    }
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_CONTACT, null, false);
                    break;
                case TUITA_IM_SYSMSG_TYPE_REFUSE:
                    nf = new NewFriend();
                    nf.setMyid(myid);
                    nf.setChat_id(json.getLong("uid"));
                    nf.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                    nf.setNick_name(getJsonString(json, "nick"));
                    nf.setAvatar(getJsonString(json, "avatar"));
                    nf.setStatus(NewFriend.STATUS_WAITING_ADD);
                    nf.setAllow_text(getJsonString(json, "text"));

                    /**
                     * save db
                     */
                    NewFriendDaoHelper.getInstance(this.getManager().getContext())
                            .save(nf);
                    break;
                case TUITA_IM_SYSMSG_TYPE_PHONE:
                    JSONArray msgsObj = null;
                    JSONObject obj = null;
                    msgsObj = json.getJSONArray("users");
                    if (msgsObj != null) {
                        for (int i = 0; i < msgsObj.length(); i++) {
                            obj = msgsObj.getJSONObject(i);
                            nf = existFriend(obj.getLong("uid"));
                            if (nf == null) {
                                nf = new NewFriend();
                                nf.setMyid(myid);
                                nf.setChat_id(obj.getLong("uid"));
                                nf.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                                nf.setNick_name(getJsonString(obj, "nick"));
                                nf.setAvatar(getJsonString(obj, "avatar"));
                                nf.setStatus(NewFriend.STATUS_WAITING_ADD);
                                if (obj.has("alias") && !"".equals(getJsonString(obj, "alias"))) {
                                    nf.setComment_name(getJsonString(obj, "alias"));
                                }
                                nf.setBy1(getJsonString(obj, "pn"));
                                nf.setAllow_text("");
                                nf.setBy2(IConst.NEWFRIEND_PHONE_RECOMMEND);
                                /**
                                 * save db
                                 */
                                NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).save(nf);
                            }

                        }
                    }

                    break;
                case TUITA_IM_SYSMSG_TYPE_UPDATE_CONTACT:     // 10更新好友相关信息

                    /**
                     * {
                     s: 10,          	// 修改好友信息
                     uid: long,			// 好友的用户ID
                     nick: "好友搜悦昵称"	// 如果没有变化，该字段不存在  <<判断>>
                     avatar: "好友头像"	// 如果没有变化，该字段不存在  <<判断>>
                     }
                     */


                    contact = new Contact();
                    contact.setMyid(myid);
                    contact.setChat_id(json.getLong("uid"));
                    if (!"".equals(getJsonString(json, "nick"))) {
                        contact.setNick_name(getJsonString(json, "nick"));
                    }
                    if (!"".equals(getJsonString(json, "avatar"))) {
                        contact.setAvatar(getJsonString(json, "avatar"));
                    }

                    ContactDaoHelper.getInstance(this.getManager().getContext()).updateNickandAvatar(myid, contact.getChat_id(), contact.getNick_name(), contact.getAvatar());

                    GroupMembersDaoHelper.getInstance(this.getManager().getContext()).updateAvatar(this.getOwner().getUid(), json.getLong("uid"), getJsonString(json, "avatar"), getJsonString(json, "nick"));

                    break;
                case TUITA_IM_SYSMSG_TYPE_CREATE_GROUP:
//                    Log.i("TUITA_IM_SYSMSG_TYPE_CREATE_GROUP", "101");
                    JSONObject dataJson = getJsonObject(json, "users");
                    long invitedId = getJsonLong(json, "inviterId");
                    JSONArray usersObj = null;
                    JSONObject membersObj = null;
                    List<String> sysMessageList = null;

                    group = GroupDaoHelper.getInstance(
                            this.getManager().getContext()).find(this.getOwner().getUid(),
                            json.getLong("gid"));
                    if (group != null) {
                        // group = new Gson().fromJson(jsonStr,
                        // new TypeToken<Group>() {}.getType());
                        group.setGroup_id(json.getLong("gid"));
                        group.setGroup_nick_name(json.getString("nick"));
                        group.setSelf_id(this.getOwner().getUid());
                        if (json.has("isNickSet")) {
                            if (json.getBoolean("isNickSet")) {
                                group.setIs_nick_set(1);
                            } else {
                                group.setIs_nick_set(0);
                            }
                        } else {
                            group.setIs_nick_set(0);
                        }
                        if (json.has("avatar")) {
                            group.setGroup_avatar(json.getString("avatar"));
                        }
//                        group.setIs_news_notify(0);
//                        group.setIs_group_saved(1);
                        group.setOwner_id(json.getLong("ownerId"));
                        if (json.has("maxGroupMembers")) {
                            group.setMax_numbers(json.getInt("maxGroupMembers"));
                        }
                    } else {
                        group = new Group();
                        // group = new Gson().fromJson(jsonStr,
                        // new TypeToken<Group>() {}.getType());
                        group.setGroup_id(json.getLong("gid"));
                        group.setGroup_nick_name(json.getString("nick"));
                        group.setSelf_id(this.getOwner().getUid());
                        if (json.has("isNickSet")) {
                            if (json.getBoolean("isNickSet")) {
                                group.setIs_nick_set(1);
                            } else {
                                group.setIs_nick_set(0);
                            }
                        } else {
                            group.setIs_nick_set(0);
                        }

                        if (json.has("isNewsNotifyShielded")) {
                            if (json.getBoolean("isNewsNotifyShielded")) {
                                group.setIs_news_notify(1);
                            } else {
                                group.setIs_news_notify(0);
                            }
                        } else {
                            group.setIs_news_notify(0);
                        }

                        if (json.has("isGroupSaved")) {
                            if (json.getBoolean("isGroupSaved")) {
                                group.setIs_group_saved(1);
                            } else {
                                group.setIs_group_saved(0);
                            }
                        } else {
                            group.setIs_group_saved(1);
                        }
                        group.setOwner_id(json.getLong("ownerId"));
                        if (json.has("maxGroupMembers")) {
                            group.setMax_numbers(json.getInt("maxGroupMembers"));
                        }
                        if (json.has("avatar")) {
                            group.setGroup_avatar(json.getString("avatar"));
                        }
                        if(json.has("extendInfo"))
                        {
                            JSONObject extendObj = getJsonObject(json, "extendInfo");
                            group.setExtendInfo(dealWithExtendInfo(extendObj));
                        }
                    }
                    group.setOwner_id(json.getLong("ownerId"));
                    GroupDaoHelper.getInstance(this.getManager().getContext())
                            .save(group);
                    usersObj = json.getJSONArray("users");
                    sysMessageList = new ArrayList<String>();
                    if (usersObj != null) {
                        List<GroupMembers> list = new ArrayList<GroupMembers>();
                        for (int i = 0; i < usersObj.length(); i++) {
                            membersObj = usersObj.getJSONObject(i);
                            groupMembers = new GroupMembers();
                            groupMembers.setGroup_id(json.getLong("gid"));
                            if (json.getLong("ownerId") == membersObj
                                    .getLong("uid")) {
                                groupMembers.setIs_owner(1);
                            } else {
                                groupMembers.setIs_owner(0);
                            }
                            groupMembers.setSelf_id(this.getOwner().getUid());
                            groupMembers.setMember_id(membersObj.getLong("uid"));
                            groupMembers.setNick_name(membersObj.getString("nick"));
                            if (membersObj.has("memberNick"))
                                groupMembers.setMember_name(membersObj.getString("memberNick"));
                            if (this.getOwner().getUid() == invitedId)
                                groupMembers.setBy1(ISINVITED);
                            else
                                groupMembers.setBy1(NOTINVITED);
                            if (json.has("avatar")) {
                                group.setGroup_avatar(json.getString("avatar"));
                            }
                            groupMembers.setMember_avatar(membersObj
                                    .getString("avatar"));
                            sysMessageList.add(membersObj.getString("nick"));
                            /**
                             * save db
                             */
                            GroupMembersDaoHelper.getInstance(
                                    this.getManager().getContext()).save(
                                    groupMembers);

                            list.add(groupMembers);
                        }
                        BroadcastUtil.sendBroadcastToUI(this.getManager()
                                        .getContext(), BroadcastUtil.ACTION_ADD_GROUP,
                                new Gson().toJson(list), false);
                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    MessageRecent mesRe = new MessageRecent();
                    mesRe.setChat_id(json.getLong("gid"));
                    mesRe.setChat_type(IConst.CHAT_TYPE_GROUP);
                    mesRe.setMyid(this.getOwner().getUid());
                    mesRe.setSender(this.getOwner().getUid());
                    mesRe.setDate(currentTimeMillis);
                    mesRe.setBubble_num(0);
                    mesRe.setUuid(genUuid(this.getOwner().getUid(), t));
                    MessageRecentDaoHelper.getInstance(
                            this.getManager().getContext()).save(mesRe);

                    groupMembers = GroupMembersDaoHelper.getInstance(
                            this.getManager().getContext()).find(this.getOwner().getUid(),
                            json.getLong("gid"), json.getLong("inviterId"));

                    MessageHistory history = new MessageHistory();
                    history.setMyid(this.getOwner().getUid());
                    history.setUuid(genUuid(this.getOwner().getUid(), t));
                    history.setChat_id(json.getLong("gid"));
                    history.setChat_type(IConst.CHAT_TYPE_GROUP);
                    history.setContent_type(IConst.CONTENT_TYPE_SYSMSG);
                    history.setDate(t);
                    history.setStatus(IMessageConst.STATUS_HAS_SENT);
                    // 5.2 废弃的拼接 进入群时的消息体  使用tip 字段
                    if(json.has("tip"))
                    {
                        history.setContent(json.getString("tip"));
                    }else // 万一服务端没有给， 走以前的逻辑，fuck
                    {
                        if (groupMembers.getMember_id() == this.getOwner().getUid()) {
                        for (int i = 0; i < sysMessageList.size(); i++) {
                            if (sysMessageList.get(i).equals(this.getOwner().getNick())) {
                                sysMessageList.remove(i);
                                break;
                            }
                        }
                        history.setContent("你邀请" + sysMessageList.toString().replace("[", "").replace("]", "") + "加入群聊");
                        } else {
                            for (int i = 0; i < sysMessageList.size(); i++) {
                                if (sysMessageList.get(i).equals(groupMembers.getNick_name())) {
                                    sysMessageList.remove(i);
                                    break;
                                }
                            }
                            history.setContent(groupMembers.getNick_name() + "邀请" + sysMessageList.toString().replace("[", "").replace("]", "") + "加入群聊");
                        }
                    }
                    MessageHistoryDaoHelper.getInstance(
                            this.getManager().getContext()).save(history);
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_GROUP_CREATE_SUCCESS,
                            new Gson().toJson(group), false);

                    history.setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_MSG, new Gson().toJson(history),
                            false);
                    break;
                case TUITA_IM_SYSMSG_TYPE_DELETE_GROUP:
//                    Log.i("TUITA_IM_SYSMSG_TYPE_CREATE_GROUP", "102");
                    JSONArray userObj = null;
                    Long uid = null;

                    userObj = json.getJSONArray("users");
                    if (userObj != null) {
                        for (int i = 0; i < userObj.length(); i++) {
                            uid = userObj.getLong(i);
                            if (this.getOwner().getUid() == uid) {      //删自己
                                GroupDaoHelper.getInstance(
                                        this.getManager().getContext()).deleteAll(
                                        json.getLong("gid"),
                                        this.getOwner().getUid());
                                GroupMembersDaoHelper.getInstance(
                                        this.getManager().getContext()).deleteAll(
                                        json.getLong("gid"),
                                        this.getOwner().getUid());
                                MessageRecentDaoHelper.getInstance(
                                        this.getManager().getContext()).delete(
                                        this.getOwner().getUid(),
                                        json.getLong("gid"),
                                        IConst.CHAT_TYPE_GROUP
                                );

                                MessageHistory historyExit = new MessageHistory();
                                historyExit.setMyid(this.getOwner().getUid());
                                historyExit.setUuid(genUuid(this.getOwner()
                                        .getUid(), t));
                                historyExit.setDate(System.currentTimeMillis());
                                historyExit.setChat_id(json.getLong("gid"));
                                historyExit.setChat_type(IConst.CHAT_TYPE_GROUP);
                                historyExit
                                        .setContent_type(IConst.CONTENT_TYPE_SYSMSG);
                                historyExit
                                        .setStatus(IMessageConst.STATUS_HAS_SENT);
                                historyExit.setContent("您已经不在群组了.");
                                MessageHistoryDaoHelper.getInstance(
                                        this.getManager().getContext()).save(
                                        historyExit);
                                // 发广播
                                historyExit
                                        .setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                                BroadcastUtil.sendBroadcastToUI(this.getManager()
                                                .getContext(), BroadcastUtil.ACTION_MSG,
                                        new Gson().toJson(historyExit), false);
                            } else {
                                group = GroupDaoHelper.getInstance(
                                        this.getManager().getContext()).find(this.getOwner().getUid(),
                                        json.getLong("gid"));
                                if (group != null) {
                                    group.setGroup_id(json.getLong("gid"));
                                    group.setGroup_nick_name(json.getString("nick"));
                                    group.setSelf_id(this.getOwner().getUid());
                                    if (json.getBoolean("isNickSet")) {
                                        group.setIs_nick_set(1);
                                    } else {
                                        group.setIs_nick_set(0);
                                    }
                                    if (json.has("avatar")) {
                                        group.setGroup_avatar(json
                                                .getString("avatar"));
                                    }

                                    GroupDaoHelper.getInstance(
                                            this.getManager().getContext()).save(
                                            group);
                                }
                                /**
                                 * save db
                                 */
                                groupMembers = GroupMembersDaoHelper.getInstance(
                                        this.getManager().getContext()).find(this.getOwner().getUid(),
                                        json.getLong("gid"), uid);
                                if (group.getOwner_id() == this.getOwner().getUid()) {
                                    MessageHistory historyExit = new MessageHistory();
                                    historyExit.setMyid(this.getOwner().getUid());
                                    historyExit.setUuid(genUuid(this.getOwner()
                                            .getUid(), t));
                                    historyExit.setChat_id(json.getLong("gid"));
                                    historyExit.setDate(System.currentTimeMillis());
                                    historyExit.setChat_type(IConst.CHAT_TYPE_GROUP);
                                    historyExit
                                            .setContent_type(IConst.CONTENT_TYPE_SYSMSG);
                                    historyExit
                                            .setStatus(IMessageConst.STATUS_HAS_SENT);
                                    if (null != groupMembers) {
                                        historyExit.setContent(groupMembers
                                                .getNick_name() + "退出了群聊");
                                    } else {
                                        historyExit.setContent(groupMembers
                                                .getNick_name() + "退出了群聊");
                                    }

                                    MessageHistoryDaoHelper.getInstance(
                                            this.getManager().getContext()).save(
                                            historyExit);
                                    historyExit
                                            .setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                                    .getContext(), BroadcastUtil.ACTION_MSG,
                                            new Gson().toJson(historyExit), false);
                                }

                                GroupMembersDaoHelper.getInstance(
                                        this.getManager().getContext()).deleteBygr(this.getOwner().getUid(),
                                        uid, json.getLong("gid"));
                            }

                        }

                    }

                    break;
                case TUITA_IM_SYSMSG_TYPE_RETREAT_GROUP:
//                    Log.i("TUITA_IM_SYSMSG_TYPE_RETREAT_GROUP", "103");
                    long currentTimeMillis2 = System.currentTimeMillis();
                    groupMembers = GroupMembersDaoHelper.getInstance(
                            this.getManager().getContext()).find(this.getOwner().getUid(),
                            json.getLong("gid"), json.getLong("uid"));
                    group = GroupDaoHelper.getInstance(
                            this.getManager().getContext()).find(this.getOwner().getUid(),
                            json.getLong("gid"));

                    if (json.getLong("ownerId") == this.getOwner().getUid()) {
                        MessageHistory rehistory = new MessageHistory();
                        rehistory.setMyid(this.getOwner().getUid());
                        rehistory.setChat_id(json.getLong("gid"));
                        rehistory.setChat_type(IConst.CHAT_TYPE_GROUP);
                        rehistory.setContent_type(IConst.CONTENT_TYPE_SYSMSG);
                        rehistory.setStatus(IMessageConst.STATUS_HAS_SENT);
                        rehistory.setUuid(genUuid(json.getLong("gid"),
                                currentTimeMillis2));
                        rehistory.setDate(currentTimeMillis2);
                        rehistory.setContent(groupMembers.getNick_name() + "退出了群聊");

                        MessageHistoryDaoHelper.getInstance(
                                this.getManager().getContext()).save(rehistory);
                    }
                    if (this.getOwner().getUid() == json.getLong("uid")) {  //自己退了
                        GroupDaoHelper.getInstance(this.getManager().getContext())
                                .deleteAll(json.getLong("gid"),
                                        this.getOwner().getUid());
                        GroupMembersDaoHelper.getInstance(
                                this.getManager().getContext()).deleteAll(
                                json.getLong("gid"), this.getOwner().getUid());
                        MessageRecentDaoHelper.getInstance(
                                this.getManager().getContext()).delete(
                                this.getOwner().getUid(), json.getLong("gid"), IConst.CHAT_TYPE_GROUP);
                    } else {    //别人退了
                        MessageRecentDaoHelper.getInstance(this.getManager().getContext()).updateTime(this.getOwner().getUid(), json.getLong("gid"), currentTimeMillis2);
                        if (group != null) {
                            if (json.has("nick")) {
                                group.setGroup_nick_name(json.getString("nick"));
                            }
                            if (json.has("isNickSet")) {
                                if (json.getBoolean("isNickSet")) {
                                    group.setIs_nick_set(1);
                                } else {
                                    group.setIs_nick_set(0);
                                }
                            }
                            if (json.has("avatar")) {
                                group.setGroup_avatar(json.getString("avatar"));
                            }
                            group.setOwner_id(json.getLong("ownerId"));
                            GroupDaoHelper.getInstance(
                                    this.getManager().getContext()).save(group);
                        }
                        GroupMembersDaoHelper.getInstance(
                                this.getManager().getContext()).updateOwner(this.getOwner().getUid(),
                                json.getLong("ownerId"), json.getLong("gid"));
                        GroupMembersDaoHelper.getInstance(
                                this.getManager().getContext()).deleteBygr(this.getOwner().getUid(),
                                json.getLong("uid"), json.getLong("gid"));
                    }
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_GROUP_EXIT, json.getLong("uid") + "", false);
                    break;
                case TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_NAME:
//                    Log.i("TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_NAME", "104");
                    GroupDaoHelper.getInstance(this.getManager().getContext())
                            .updateCommentName(this.getOwner().getUid(), json.getLong("gid"),
                                    json.getString("nick"));

                    groupMembers = GroupMembersDaoHelper.getInstance(
                            this.getManager().getContext()).find(this.getOwner().getUid(),
                            json.getLong("gid"), json.getLong("modiferId"));

                    MessageHistory historyExit = new MessageHistory();
                    historyExit.setMyid(this.getOwner().getUid());
                    historyExit.setUuid(genUuid(this.getOwner().getUid(), t));
                    historyExit.setChat_id(json.getLong("gid"));
                    historyExit.setChat_type(IConst.CHAT_TYPE_GROUP);
                    historyExit.setContent_type(IConst.CONTENT_TYPE_SYSMSG);
                    historyExit.setStatus(IMessageConst.STATUS_HAS_SENT);
                    historyExit.setDate(System.currentTimeMillis());
                    if (null != groupMembers) {
                        historyExit.setContent(groupMembers.getNick_name()
                                + "将群名称修改为：" + json.getString("nick"));
                    }
                    MessageHistoryDaoHelper.getInstance(
                            this.getManager().getContext()).save(historyExit);
                    historyExit.setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                            BroadcastUtil.ACTION_MSG,
                            new Gson().toJson(historyExit), false);
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_SUCCESS_DIALOG, null, false);

                    break;
                case TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_MEMBER_NAME:
//                    Log.i("TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_NAME", "105");
                    GroupMembersDaoHelper.getInstance(
                            this.getManager().getContext()).updateCommentName(this.getOwner().getUid(),
                            json.getLong("uid"), json.getLong("gid"),
                            json.getString("memberNick"));
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_SUCCESS_DIALOG, null, false);
                    break;
                case TUITA_IM_SYSMSG_TYPE_GET_GROUP_MEMBERS_DETAIL:
//                    Log.i("TUITA_IM_SYSMSG_TYPE_UPDATE_GROUP_NAME", "106");
//                    groupMembers = GroupMembersDaoHelper.getInstance(
//                            this.getManager().getContext()).find(this.getOwner().getUid(),
//                            json.getLong("gid"),
//                            json.getLong("uid"));
//                    groupMembers.setGroup_id(json.getLong("gid"));
//                    groupMembers.setMember_id(json.getLong("uid"));
//                    groupMembers.setSelf_id(this.getOwner().getUid());
//                    groupMembers.setNick_name(json.getString("nick"));
//                    groupMembers.setMember_name(json
//                            .getString("memberNick"));
//                    groupMembers.setMember_avatar(json
//                            .getString("avatar"));
//
//                    GroupMembersDaoHelper.getInstance(
//                            this.getManager().getContext()).save(
//                            groupMembers);
                    break;
                case TUITA_IM_SYSMSG_TYPE_GET_GROUP_AVATAR_AND_NAME:        //补救系统消息
//                    Log.i("TUITA_IM_SYSMSG_TYPE_GET_GROUP_AVATAR_AND_NAME", "107");
                    Group group_update = GroupDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), json.getLong("gid"));
                    if (group_update != null) {
                    group_update.setGroup_nick_name(json.getString("nick"));
                    group_update.setGroup_avatar(json.getString("avatar"));
                    if (json.has("maxGroupMembers")) {
                        group_update.setMax_numbers(json.getInt("maxGroupMembers"));
                    }
                    if (json.has("ownerId")) {
                        group_update.setOwner_id(json.getLong("ownerId"));
                    }
                    if(json.has("extendInfo"))
                    {
                        JSONObject extendObj = getJsonObject(json, "extendInfo");
                        group_update.setExtendInfo(dealWithExtendInfo(extendObj));
                    }
                }

                    GroupDaoHelper.getInstance(this.getManager().getContext()).save(group_update);
                    break;
                case TUITA_IM_SYSMSG_TYPE_GET_GROUP_ALL:        //获取某个群的所有信息
//                    Log.i("TUITA_IM_SYSMSG_TYPE_GET_GROUP_ALL", "108");
                    JSONArray memberArray = null;
                    JSONObject memberObjs = null;
                    if (json.getBoolean("isDeleted")) {       //删除本人群相关信息
                        GroupDaoHelper.getInstance(this.getManager().getContext()).deleteAll(json.getLong("gid"), this.getOwner().getUid());
                        GroupMembersDaoHelper.getInstance(this.getManager().getContext()).deleteAll(json.getLong("gid"), this.getOwner().getUid());
                        MessageRecentDaoHelper.getInstance(this.getManager().getContext()).delete(this.getOwner().getUid(), json.getLong("gid"), IConst.CHAT_TYPE_GROUP);
                    } else {      //做新增和删除
                        //群入库
                        Group groupAll = GroupDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), json.getLong("gid"));

                        if (groupAll == null) {
                            groupAll = new Group();
                        }
                        groupAll.setGroup_id(json.getLong("gid"));
                        groupAll.setSelf_id(this.getOwner().getUid());
                        groupAll.setGroup_nick_name(json.getString("nick"));
                        if (json.has("isNickSet")) {        //是否设置群昵称
                            if (json.getBoolean("isNickSet")) {
                                groupAll.setIs_nick_set(1);
                            } else {
                                groupAll.setIs_nick_set(0);
                            }
                        }

                        if (json.getBoolean("isGroupSaved")) {      //是否保存通讯录
                            groupAll.setIs_group_saved(1);
                        } else {
                            groupAll.setIs_group_saved(0);
                        }

                        if (json.getBoolean("isNewsNotifyShielded")) {      //是否消息提醒
                            groupAll.setIs_news_notify(1);
                        } else {
                            groupAll.setIs_news_notify(0);
                        }

                        groupAll.setGroup_avatar(json.getString("avatar"));
                        groupAll.setOwner_id(json.getLong("ownerId"));
                        groupOwnerId.put(json.getLong("gid"), json.getLong("ownerId"));
                        groupAll.setMax_numbers(json.getInt("maxGroupMembers"));
                        if(json.has("extendInfo"))
                        {
                            JSONObject extendObj = getJsonObject(json, "extendInfo");
                            groupAll.setExtendInfo(dealWithExtendInfo(extendObj));
                        }
                        GroupDaoHelper.getInstance(this.getManager().getContext()).save(groupAll);
                        //群成员入库
                        memberArray = json.getJSONArray("members");

                        for (int i = 0; i < memberArray.length(); i++) {
                            memberObjs = memberArray.getJSONObject(i);
                            if (memberObjs.getInt("s") == 1) {        //新增的
                                GroupMembers groupMembersAdd = new GroupMembers();
                                groupMembersAdd.setGroup_id(memberObjs.getLong("gid"));
                                groupMembersAdd.setMember_id(memberObjs.getLong("uid"));
                                groupMembersAdd.setNick_name(memberObjs.getString("nick"));
                                groupMembersAdd.setMember_avatar(memberObjs.getString("avatar"));
                                groupMembersAdd.setSelf_id(this.getOwner().getUid());
                                groupMembersAdd.setMember_name(memberObjs.getString("memberNick"));
                                if (groupOwnerId.containsKey(memberObjs.getLong("gid"))) {
                                    if (memberObjs.getLong("uid") == groupOwnerId.get(memberObjs.getLong("gid"))) {
                                        groupMembersAdd.setIs_owner(1);
                                    } else {
                                        groupMembersAdd.setIs_owner(0);
                                    }
                                }

                                GroupMembersDaoHelper.getInstance(this.getManager().getContext()).save(groupMembersAdd);
                                groupOwnerId.clear();
                            } else if (memberObjs.getInt("s") == 3) {      //删除的
                                GroupMembersDaoHelper.getInstance(this.getManager().getContext()).deleteBygr(this.getOwner().getUid(), memberObjs.getLong("uid"), memberObjs.getLong("gid"));
                            }
                        }
                        groupOwnerId.clear();
                    }
                    break;

                case TUITA_IM_SYSMSG_TYPE_UPDATE_SERVICE_MSG:   //201更新服务号消息
//                    Log.i("TUITA_IM_SYSMSG_TYPE_UPDATE_SERVICE_MSG", "201");
                    JSONObject commonJsonObj = getJsonObject(json, "common");
                    JSONObject deltaJsonObj = getJsonObject(commonJsonObj, "delta");
                    //TODO 讲下面这两个字段入库和发广播给界面显示系统消息

                    String msg = commonJsonObj.getString("msg");
                    long id = commonJsonObj.getLong("id");  //当pos为1，是好友uid；pos为2，是群gid。当pos为3，是服务号id

                    //解析入库增量信息
                    if (commonJsonObj.getInt("pos") == IConst.SYSTEM_POS_SERVICE_MSG) {     //如果pos是服务号
                        ServiceMessageRecentDaoHelper.getInstance(this.getManager().getContext()).updateServiceMsg(deltaJsonObj, this);
                    }

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理组中的 添加的额外字段
     * @param extendObj
     * @return
     * @throws JSONException
     */
    private GroupExtendInfo dealWithExtendInfo(JSONObject extendObj)
    {
        GroupExtendInfo extendInfo = new GroupExtendInfo();
        try {
            if(extendObj!=null && extendObj.has("circle_boundCircleList"))
            {
                JSONArray circleJsonList =getJsonArray(extendObj,"circle_boundCircleList");
                if(circleJsonList!=null)
                {
                    JSONObject circleJsonObj;
                    ImToCricle imToCricle;
                    List<ImToCricle>  imToCricleList = new ArrayList<ImToCricle>();
                    for (int i=0;i<circleJsonList.length();i++)
                    {
                        circleJsonObj = circleJsonList.getJSONObject(i);
                        imToCricle = new ImToCricle();
                        imToCricle.setKeyword(circleJsonObj.getString("keyword"));
                        imToCricle.setSrpId(circleJsonObj.getString("srpId"));
                        imToCricle.setInterestId(circleJsonObj.getLong("interestId"));
                        imToCricle.setInterestLogo(circleJsonObj.getString("interestLogo"));
                        imToCricle.setInterestName(circleJsonObj.getString("interestName"));
                        imToCricle.setType(circleJsonObj.getInt("type"));
                        imToCricleList.add(imToCricle);
                    }
                    if(imToCricleList.size()>0)
                    {
                        extendInfo.setCircle_boundCircleList(imToCricleList);
                    }
                }
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return  extendInfo;
    }
	/*
     * private boolean containUid(String uids, long uid) { boolean res = false;
	 * if (uids != null) { String[] uidArr = uids.split(","); for (String uidStr
	 * : uidArr) { if (uidStr.equals(String.valueOf(uid))) { return true; } } }
	 * return res; }
	 */

    private void parseContactStatus(JSONObject data, List<Contact> contacts) {
        try {
            JSONArray usersObj = data.getJSONArray("users");
            // HashMap<String, Contact> contactMap = new HashMap<String,
            // Contact>(usersObj.length());
            List<Contact> result = new ArrayList<Contact>(usersObj.length());
            JSONObject userObj = null;
            Contact contact = null;
            for (int i = 0, m = usersObj.length(); i < m; i++) {
                userObj = usersObj.getJSONObject(i);
                contact = new Contact();
                contact.setPhone(getJsonString(userObj, "pn"));
                contact.setChat_id(userObj.getLong("uid"));
                contact.setNick_name(getJsonString(userObj, "nick"));
                contact.setComment_name(getJsonString(userObj, "alias"));
                contact.setAvatar(getJsonString(userObj, "avatar"));
                contact.setStatus(userObj.getInt("s"));
                // contactMap.put(contact.getPhone(), contact);
                result.add(contact);
            }
			/*
			 * for (Contact c : contacts) { contact =
			 * contactMap.get(c.getPhone()); if (contact != null) {
			 * c.setStatus(contact.getStatus());
			 * c.setAction(BroadcastUtil.ACTION_MOBILE_CONTACT_STATUS); } }
			 */
            BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                    BroadcastUtil.ACTION_MOBILE_CONTACT,
                    new Gson().toJson(result), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseInfo(JSONObject data, boolean notify) {
        try {

            //更新初始化的消息  用于100  109
            HelperUtils.updateInitMsg(data, this);
            boolean hasDel = false;

//            if (usersObj.length() > 0 ) {
            long lastUpdate = data.getLong("lastUpdate");
            ConfigDaoHelper.getInstance(this.getManager().getContext())
                    .updateContactLastUpdate(this.getOwner().getUid(),
                            lastUpdate);
            if (hasDel) {
                int count = MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).countBubble(
                        this.getOwner().getUid());
                ConfigDaoHelper.getInstance(this.getManager().getContext())
                        .updateMessageBubble(this.getOwner().getUid(),
                                count);
            }
            if (notify) {
                BroadcastUtil.sendBroadcastToUI(this.getManager()
                                .getContext(), BroadcastUtil.ACTION_CONTACT, null,
                        false);
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean mIsShowNfBar;     //是否显示通知栏变量  false显示，true不显示

    private MessageHistory parseChatMessage(JSONArray data, boolean isOnline) {
        MessageHistory result = null;
        Contact contact = null;
        String content = null;
        String muid = null;
        JSONArray msgsObj = null;
        JSONObject obj = null;
        try {
            for (int i = 0, n = data.length(); i < n; i++) {
                obj = data.getJSONObject(i);

                int mt = obj.getInt("mt");
                String id = obj.getString("id");
                msgsObj = obj.getJSONArray("msgs");
                if (mt == IConst.CHAT_TYPE_SYSTEM) {
                    mIsShowNfBar = true;
                    JSONObject msgObj = null;
                    JSONObject tmp = null;
                    long t = 0;
                    int op = 0;
                    int friendBubble = 0;
                    for (int j = 0, m = msgsObj.length(); j < m; j++) {
                        msgObj = msgsObj.getJSONObject(j);
                        content = getJsonString(msgObj, "c");
                        t = msgObj.getLong("t");
                        tmp = new JSONObject(content);
                        op = tmp.getInt("s");
                        if (op == TUITA_IM_SYSMSG_TYPE_ADDME) {
                            NewFriend nf = NewFriendDaoHelper.getInstance(
                                    this.getManager().getContext()).find(
                                    this.getOwner().getUid(),
                                    tmp.getLong("uid"));
                            if (nf == null) {
                                friendBubble++;
                            }else {
                                if (nf.getStatus() == NewFriend.STATUS_WAITING_ADD){
                                    friendBubble++;
                                }
                            }
                        }
                        // 系统推荐联系人添加气泡
                        if (op == TUITA_IM_SYSMSG_TYPE_PHONE) {
                            JSONArray sysRecommdContacts = tmp
                                    .getJSONArray("users");
                            for (int k = 0; k < sysRecommdContacts.length(); k++) {
                                JSONObject sysRecommdContact = sysRecommdContacts
                                        .getJSONObject(k);
                                NewFriend nf = NewFriendDaoHelper.getInstance(
                                        this.getManager().getContext()).find(
                                        this.getOwner().getUid(),
                                        sysRecommdContact.getLong("uid"));
                                if (nf == null) {
                                    friendBubble++;
                                }
                            }
                        }

                        dealSysMessage(this.getOwner().getUid(), tmp, op, t);
                    }
                    if (friendBubble > 0) {
                        // update friend bubble
                        ConfigDaoHelper.getInstance(
                                this.getManager().getContext())
                                .addFriendBubble(this.getOwner().getUid(),
                                        friendBubble);
                    }
                    BroadcastUtil.sendBroadcastToUI(this.getManager()
                                    .getContext(), BroadcastUtil.ACTION_SYS_MSG, null,
                            false);
                    continue;
                } else if (mt == IConst.CHAT_TYPE_SERVICE_MESSAGE) {        //服务号消息
                    dealServiceMsg(msgsObj, Long.parseLong(id), content);
                    continue;
                } else if (mt == IConst.CHAT_TYPE_SOUYUE_MSG) {   //搜悦调用消息
                    JSONObject msgObj = null;
                    msgObj = msgsObj.getJSONObject(0);
                    content = getJsonString(msgObj, "c");
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_SOUYUE_MSG, content, false);
                    continue;
                } else {
                    mIsShowNfBar = false;
                }

                /**
                 * private Long myid; private Long chat_id; private Integer
                 * chat_type; private String content; private Integer
                 * content_type; private Long date; private String uuid; private
                 * Integer status; private Integer bubble_num;
                 */
                MessageRecent chat = new MessageRecent();
                chat.setMyid(this.getOwner().getUid());
                chat.setChat_type(mt);
                MessageHistory lastNewMsg = null;
                long chat_id = 0;
                for (int j = 0, m = msgsObj.length(); j < m; j++) {
                    JSONObject msgObj = msgsObj.getJSONObject(j);
                    if (obj.getInt("mt") == 0) {// 判断群聊还是私聊
                        if (chat_id == 0) {
                            chat_id = msgObj.getLong("sid");
                            if (chat_id == getOwner().getUid()) { //判断是否是webim 上自己给别人发的消息的同步
                                chat_id = msgObj.getLong("rid");
                            }
                        }
                    } else if (obj.getInt("mt") == 1) {
                        chat_id = obj.getInt("id");
                    }

                    /**
                     * private Long myid; private Long chat_id; private Integer
                     * chat_type; private String content; private Integer
                     * content_type; private Long date; private Long
                     * session_order; private String uuid; private Integer
                     * status;
                     */
                    MessageHistory msg = new MessageHistory();
                    if (obj.getInt("mt") == 1) {// 获取用户nickname、头像
                        GroupMembers groupMembers = db_findMemberListByGroupidandUid(this.getManager().getContext(), this.getOwner().getUid(),
                                obj.getLong("id"),
                                msgObj.getLong("sid"));
                        String comment_name = null;
                        Contact contact_group = ContactDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), msgObj.getLong("sid"));
                        if (contact_group != null) {
                            comment_name = contact_group.getComment_name();
                        }
                        if (groupMembers == null) {
                            List<GroupMembers> groupMemberses = GroupMembersDaoHelper.getInstance(this.getManager().getContext()).findMemebers(this.getOwner().getUid(), obj.getLong("id"));
                            List<Long> vecIds = new ArrayList<Long>();
                            for (GroupMembers members : groupMemberses) {
                                vecIds.add(members.getMember_id());
                            }
                            findGroupInfo(12, obj.getLong("id"), vecIds);
                        } else {
                            JSONObject object = new JSONObject();
                            object.put(
                                    "nickname",
                                    TextUtils.isEmpty(comment_name) ? (TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers
                                            .getNick_name() : groupMembers.getMember_name()) : comment_name);
                            object.put("userImage", groupMembers.getMember_avatar());
                            msg.setBy2(object.toString());
                        }
                    } else if (obj.getInt("mt") == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                        msg.setServiceJumpData(msgObj.getString("nc") != null ? msgObj.getString("nc") : "");
                    }
                    msg.setMyid(this.getOwner().getUid());
                    if (msgObj.getLong("sid") == this.getOwner().getUid()) {
                        msg.setChat_id(msgObj.getLong("rid"));
                    } else {
                        msg.setChat_id(chat_id);
                    }
                    msg.setSender(msgObj.getLong("sid"));
                    msg.setChat_type(chat.getChat_type());
                    msg.setContent(getJsonString(msgObj, "c"));
                    if (msgObj.getInt("ct") == 1) {
                        msg.setContent_type(11);
                    } else if (msgObj.getInt("ct") == MessageHistory.CONTENT_TYPE_AT_FRIEND) {
                        msg.setContent_type(MessageHistory.CONTENT_TYPE_TEXT);
                        String msgc = getJsonString(msgObj, "c");
                        AtFriend atFriend = new Gson().fromJson(msgc, AtFriend.class);
                        String c = atFriend.getC();
                        List<UserBean> userBean = atFriend.getUsers();
                        //循环替换c里面的nickname
                        for (int k = 0; k < userBean.size(); k++) {
                            long memberid = userBean.get(k).getUid();
                            String nickname = userBean.get(k).getNick();
                            Contact newcontact = ContactDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), memberid);
                            String mem = new Gson().toJson(GroupMembersDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), chat_id, memberid));
                            GroupMembers groupMembers = new Gson().fromJson(mem, GroupMembers.class);
                            if (newcontact != null && !TextUtils.isEmpty(newcontact.getComment_name())) {
                                c = c.replace(nickname, newcontact.getComment_name());
                            } else if (groupMembers != null && !TextUtils.isEmpty(groupMembers.getMember_name())) {
                                c = c.replace(nickname, groupMembers.getMember_name());
                            }
                            if (userBean.get(k).getUid() == this.getOwner().getUid())
                                chat.setBy1("1");
                        }

                        msg.setContent(c);
                    } else {
                        msg.setContent_type(msgObj.getInt("ct"));
                    }
                    msg.setDate(msgObj.getLong("t"));
                    // msg.setDate(System.currentTimeMillis());
                    msg.setSession_order(msgObj.getLong("ss"));
                    msg.setUuid(getJsonString(msgObj, "muid"));
                    msg.setStatus(IMessageConst.STATUS_HAS_SENT);
//					if (msg.getSession_order() > maxSession_order) {
                    lastNewMsg = msg;
//					}
                    /**
                     * save db
                     */
                    MessageHistoryDaoHelper.getInstance(
                            this.getManager().getContext()).save(msg);
                    if (isOnline) {
                        result = new MessageHistory();
                        result.setMyid(msg.getMyid());
                        result.setChat_id(msg.getChat_id());
                        result.setSender(msg.getSender());
                        result.setChat_type(msg.getChat_type());
                        result.setContent(msg.getContent());
                        result.setContent_type(msg.getContent_type());
                        result.setDate(msg.getDate());
                        result.setSession_order(msg.getSession_order());
                        result.setUuid(msg.getUuid());
                        result.setStatus(msg.getStatus());
                        result.setId(msg.getId());
                        result.setServiceJumpData(msg.getServiceJumpData());
                    }
                    if (msgObj.getLong("sid") != this.getOwner().getUid()) {
                        chat.setBubble_num(obj.getInt("num"));
                    } else {
                        chat.setBubble_num(0);
                    }
                }
                chat.setChat_id(chat_id);
                if (lastNewMsg != null) {
                    chat.setContent_type(lastNewMsg.getContent_type());
                    chat.setContent(lastNewMsg.getContent());
                    chat.setSender(lastNewMsg.getSender());
                    chat.setDate(lastNewMsg.getDate());
                    chat.setUuid(lastNewMsg.getUuid());
                    chat.setStatus(lastNewMsg.getStatus());
                }
                contact = ContactDaoHelper.getInstance(
                        this.getManager().getContext()).find(
                        this.getOwner().getUid(), chat.getChat_id());

                //最近表入库
                MessageRecent reC = MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).find(this.getOwner().getUid(),
                        chat.getChat_id());

                if (reC == null) {
                    reC = new MessageRecent();
                }

                chat.setMyid(this.getOwner().getUid());
                chat.setDrafttext(reC.getDrafttext());
                chat.setDraftforat(reC.getDraftforat());
                if (contact != null || obj.getInt("mt") == 1) {
                    // 当本地没有联系人时就不更新最近聊天
                    MessageRecentDaoHelper.getInstance(
                            this.getManager().getContext()).save(chat);
                }
            }
            int count = MessageRecentDaoHelper.getInstance(
                    this.getManager().getContext()).countBubble(
                    this.getOwner().getUid());
            ConfigDaoHelper.getInstance(this.getManager().getContext())
                    .updateMessageBubble(this.getOwner().getUid(), count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private NewFriend existFriend(long chat_id) {
        NewFriend nf = NewFriendDaoHelper.getInstance(
                this.getManager().getContext()).find(this.getOwner().getUid(),
                chat_id);
        return nf;
    }

    public static class Owner implements DontObfuscateInterface {
        private long uid;
        private String nick;
        private String pass;
        private String avatar;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

    }

    private JSONObject getJsonObject(JSONObject json, String key) {
        try {
            return json.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }
    private JSONArray getJsonArray(JSONObject json, String key) {
        try {
            return json.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public String getJsonString(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    private int getJsonInt(JSONObject json, String key) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            return -1;
        }
    }

    private long getJsonLong(JSONObject json, String key) {
        try {
            return json.getLong(key);
        } catch (JSONException e) {
            return -1;
        }
    }

    public TuitaSDKManager getManager() {
        return manager;
    }

    public void setManager(TuitaSDKManager manager) {
        this.manager = manager;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public int getTuitaIMState() {
        return tuitaIMState;
    }

    public void setTuitaIMState(int tuitaIMState) {
        this.tuitaIMState = tuitaIMState;
    }

    public int getImConnState() {
        return imConnState;
    }

    public void setImConnState(int imConnState) {
        this.imConnState = imConnState;
    }

    public SmallMap<Long, ImCommand> getRunningCmd() {
        return runningCmd;
    }


    /**
     * im请求超时操作
     *
     * @param manager
     * @param tidTime
     */
    public void requestTimeOut(final TuitaSDKManager manager, final long tidTime) {
        manager.getScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                ImCommand cmd = runningCmd.get(tidTime);
                if (cmd != null) {
                    runningCmd.remove(tidTime);
                    if (cmd.getPacket() != null && cmd.getPacket().getType() != TuitaPacket.TUITA_MSG_TYPE_CONNECT) {
                        BroadcastUtil.sendBroadcastToUI(TuitaIMManager.this.getManager().getContext(), BroadcastUtil.ACTION_ERROR_TIP, TimeOutBean.setTimeOutBean(), false);
                    }
                    //重连
                    manager.reConnectIM();
                }
            }
        }, NEW_TUITA_IM_COMMAND_DELAY_SECOND, TimeUnit.SECONDS);
    }

    /**
     * 查找某一个分类的详细信息
     *
     * @param context
     * @param myId
     * @param cateId
     * @return
     */
    public static Cate findCate(Context context, long myId, long cateId) {
        return CateDaoHelper.getInstance(context).find(myId, cateId);
    }

    /**
     * 按照5.0新协议处理服务号消息
     *
     * @param msgsObj
     * @param serviceId
     */
    private void dealServiceMsg(JSONArray msgsObj, long serviceId, String content) throws Exception {
        JSONObject msgObj = null;
        JSONObject cTmp = null;
        ServiceMessage seMsg = null;
        ServiceMessageRecent seMsgRe = null;
        MessageHistory history = null;
        String muid = null;
        Cate cate = null;
        long t = 0;
        int ct = 0;
        for (int j = 0, m = msgsObj.length(); j < m; j++) {
            msgObj = msgsObj.getJSONObject(j);
            content = getJsonString(msgObj, "c");
            muid = getJsonString(msgObj, "muid");
            t = msgObj.getLong("t");
            cTmp = new JSONObject(content);
            ct = msgObj.getInt("ct");

            seMsgRe = ServiceMessageRecentDaoHelper
                    .getInstance(this.getManager().getContext())
                    .find(this.getOwner().getUid(), serviceId);

            //搜小悦 排重  目前只有  2，3 没有 这3种情况
            int isIntroduceInfo = 3;
            if (cTmp.has("isIntroduceInfo")) {
                isIntroduceInfo = cTmp.getInt("isIntroduceInfo");
            } else {
                isIntroduceInfo = 3;
            }
            if (seMsgRe == null || isIntroduceInfo != 2 || TextUtils.isEmpty(seMsgRe.getDigst())) {
                if (seMsgRe == null) {
                    seMsgRe = new ServiceMessageRecent();
                    seMsgRe.setBubble_num(1 + "");
                    getServiceMsgDetail(serviceId);
                } else {
                    int parseInt = seMsgRe.getBubble_num() == null ? 0 : Integer.parseInt(seMsgRe
                            .getBubble_num());
                    seMsgRe.setBubble_num((parseInt + 1) + "");

                    //查询cate表
                    cate = CateDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), seMsgRe.getCate_id());
                }

                if (history == null) {
                    history = new MessageHistory();
                }
                //入history库
                history.setMyid(this.getOwner().getUid());
                history.setUuid(muid);
                history.setContent(content);
                history.setPushFrom(IConst.PUSH_TYPE_TUITA);
                history.setChat_id(serviceId);
                history.setChat_type(IConst.CHAT_TYPE_SERVICE_MESSAGE);
                history.setDate(t);
                history.setBy4(msgObj.getLong("mid") + "");
                history.setStatus(IMessageConst.STATUS_HAS_SENT);
                if (cTmp.has("nc"))
                    history.setServiceJumpData(cTmp.getString("nc") != null ? cTmp.getString("nc") : "");

                seMsgRe.setDate(t);
                seMsgRe.setMyid(this.getOwner().getUid());
                seMsgRe.setBy1(muid);
                seMsgRe.setDate(t);
                seMsgRe.setDetail_type(ct);

                //入服务号数据库
                seMsg = new ServiceMessage();
                seMsg.setMyid(this.getOwner().getUid());
                seMsg.setService_id(serviceId);
                seMsg.setDate(t);
                seMsg.setBy1(muid);
                seMsgRe.setBy2(seMsgRe.getBy2());
                seMsg.setService_name(seMsgRe.getService_name());
                seMsg.setService_avatar(seMsgRe.getService_avatar());
                seMsg.setCate_id(seMsgRe.getCate_id());
                seMsg.setDate(t);
                seMsg.setDetail_type(ct);

                //最近表入库
                MessageRecent reC = MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).find(this.getOwner().getUid(),
                        seMsg.getCate_id());

                if (reC == null) {
                    reC = new MessageRecent();
                }
                reC.setBubble_num(1);
                reC.setMyid(this.getOwner().getUid());
                reC.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                reC.setChat_id(seMsgRe.getCate_id());
                reC.setChat_type(IConst.CHAT_TYPE_SERVICE_MESSAGE);
                reC.setDate(System.currentTimeMillis());
                reC.setUuid(genUuid(this.getOwner().getUid(), t));
                if (cTmp.has("nc")) {
                    seMsg.setBy2(cTmp.getString("nc"));
                }
                if (cTmp.has("title") && cate != null) {
                    cate.setDigst(cTmp.getString("title") != null ? cTmp.getString("title") : "");
                    cate.setBubble_num(1);
                    CateDaoHelper.getInstance(this.getManager().getContext()).save(cate);
                }
                //现在的类型比较混乱，以后需要跟服务器统一  4.0.2
                if (ct == 1) {
                    history.setContent_type(IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST);
                    history.setContent(cTmp.getString("title"));
                    reC.setContent(cTmp.getString("title"));
                    seMsg.setAvatar(cTmp.getString("avatar"));
                    seMsg.setDigst(cTmp.getString("digst"));
                    if (cTmp.has("pics")) {
                        seMsg.setImage_url(cTmp.getString("pics"));
                    }
                    seMsg.setIntent_data(cTmp.getString("c"));
                    if (cTmp.has("title")) {
                        seMsg.setTitle(cTmp.getString("title"));
                        seMsgRe.setDigst(cTmp.getString("title"));
                    }
//                    if (cTmp.has("showNotify")){
//                        seMsg.setIsShowNotify(cTmp.getBoolean("showNotify"));
//                    }
//                    if (cTmp.has("digst")) {
//                        seMsgRe.setDigst(cTmp.getString("digst"));
//                    }
                    if (cTmp.has("subDigst")) {
                        seMsg.setSubDigst(cTmp.getString("subDigst"));
                    }
                    ServiceMessageDaoHelper.getInstance(
                            this.getManager().getContext()).save(seMsg);
                } else if (ct == 2) {
                    history.setContent_type(IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND);
                    seMsg.setImage_url(cTmp.getString("pic"));
                    seMsg.setIntent_data(cTmp.getString("c"));
                    seMsg.setExra(cTmp.getJSONArray("list").toString());
                    if (cTmp.has("digst")) {
                        seMsg.setDigst(cTmp.getString("digst") != null ? cTmp.getString("digst") : "");
                    }
                    if (cTmp.has("title")) {
                        seMsgRe.setDigst(cTmp.getString("title") != null ? cTmp.getString("title") : "");
                        reC.setContent(cTmp.getString("title") != null ? cTmp.getString("title") : "");
                        history.setContent(cTmp.getString("title") != null ? cTmp.getString("title") : "");
                        seMsg.setTitle(cTmp.getString("title") != null ? cTmp.getString("title") : "");
                    }
                    if (cTmp.has("subDigst")) {
                        seMsg.setSubDigst(cTmp.getString("subDigst") != null ? cTmp.getString("subDigst") : "");
                    }
//                    if (cTmp.has("showNotify")){
//                        seMsg.setIsShowNotify(cTmp.getBoolean("showNotify"));
//                    }
                    ServiceMessageDaoHelper.getInstance(
                            this.getManager().getContext()).save(seMsg);
                } else if (ct == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_TEXT) {
                    history.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                    reC.setContent(cTmp.getString("c"));
                    history.setContent(cTmp.getString("c"));
                    seMsgRe.setDigst(cTmp.getString("c"));
                    ServiceMessageDaoHelper.getInstance(
                            this.getManager().getContext()).save(seMsg);
                } else {
                    history.setContent_type(ct);
                    history.setContent(cTmp.getString("c"));
                    ServiceMessageDaoHelper.getInstance(
                            this.getManager().getContext()).save(seMsg);
                }
                ServiceMessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).save(seMsgRe);
                history.setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MSG, new Gson().toJson(history),
                        false);
                MessageHistoryDaoHelper.getInstance(this.getManager().getContext()).save(history);
                MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).save(reC);

                if (seMsgRe.getBy3() != null && seMsgRe.getBy3().equals("1")) {
                    mIsShowNfBar = true;
                } else {
                    if (cTmp.has("showNotify") && !cTmp.getBoolean("showNotify")) {
                        mIsShowNfBar = true;
                    } else {
                        mIsShowNfBar = false;
                    }
                }
//                mIsShowNfBar = seMsgRe.getBy3() != null && seMsgRe.getBy3().equals("1") ? true : false;
                if (!mIsShowNfBar)
                    BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(), BroadcastUtil.ACTION_MSG_ADD, new Gson().toJson(history), true);
            }

        }

    }

    /**
     * 处理极光推送的服务号消息
     */
    public void dealJPushMsg(JSONObject _dataJson, int _pushType) throws Exception {
        //解析data
        int mt = _dataJson.getInt("mt");
        long time = _dataJson.getLong("t");
        long mid = _dataJson.getLong("mid");
        int ct = _dataJson.getInt("ct");
        JSONObject content = _dataJson.getJSONObject("c");
        String muid = _dataJson.getString("muid");
        long serviceId = _dataJson.getLong("id");

        //解析c
        String nc = content.getString("nc");
        String title = content.getString("title");
        String digst = content.getString("digst");
        String subDigst = content.getString("subDigst");
        boolean showNotify = content.getBoolean("showNotify");


        String[] jumpData = HelperUtils.getJumpData(nc, title, digst);  //重新拼数据格式
        ServiceMessageRecent seMsgRe = null;
        MessageHistory msgHistory = null;
        ServiceMessage seMsg = null;
        MessageRecent msgRecent = null;
        Cate cate = null;

        MessageMidDaoHelper msgmidHelper = MessageMidDaoHelper.getInstance(this.getManager().getContext());

        if (!msgmidHelper.isContraintAndinsert(mid + this.getOwner().getUid() + "", time)) {    //排重 此时是不重复
            if (mt == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                seMsgRe = ServiceMessageRecentDaoHelper
                        .getInstance(this.getManager().getContext())
                        .find(this.getOwner().getUid(), serviceId);


                if (seMsgRe == null) {
                    seMsgRe = new ServiceMessageRecent();
                    seMsgRe.setBubble_num(1 + "");
                    getServiceMsgDetail(serviceId);
                } else {
                    int parseInt = seMsgRe.getBubble_num() == null ? 0 : Integer.parseInt(seMsgRe
                            .getBubble_num());
                    seMsgRe.setBubble_num((parseInt + 1) + "");

                    //查询cate表
                    cate = CateDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(), seMsgRe.getCate_id());
                }


                msgHistory = new MessageHistory();
                //入history库
                msgHistory.setMyid(this.getOwner().getUid());
                msgHistory.setUuid(muid);
                msgHistory.setContent(content.toString());
                msgHistory.setChat_id(serviceId);
                msgHistory.setBy4(mid + "");
                msgHistory.setChat_type(IConst.CHAT_TYPE_SERVICE_MESSAGE);
                msgHistory.setDate(time);
                msgHistory.setStatus(IMessageConst.STATUS_HAS_SENT);
                msgHistory.setServiceJumpData(jumpData[1] != null ? jumpData[1] : "");
                msgHistory.setPushFrom(_pushType + "");

                seMsgRe.setDate(time);
                seMsgRe.setMyid(this.getOwner().getUid());
                seMsgRe.setBy1(muid);
                seMsgRe.setDetail_type(ct);

                //入服务号数据库
                seMsg = new ServiceMessage();
                seMsg.setMyid(this.getOwner().getUid());
                seMsg.setService_id(serviceId);
                seMsg.setBy1(muid);
                seMsgRe.setBy2(seMsgRe.getBy2());
                seMsg.setService_name(seMsgRe.getService_name());
                seMsg.setService_avatar(seMsgRe.getService_avatar());
                seMsg.setCate_id(seMsgRe.getCate_id());
                seMsg.setDate(time);
                seMsg.setDetail_type(ct);

                //最近表入库
                msgRecent = MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).find(this.getOwner().getUid(),
                        seMsg.getCate_id());

                if (msgRecent == null) {
                    msgRecent = new MessageRecent();
                }
                msgRecent.setBubble_num(1);
                msgRecent.setMyid(this.getOwner().getUid());
                msgRecent.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                msgRecent.setChat_id(seMsgRe.getCate_id());
                msgRecent.setChat_type(IConst.CHAT_TYPE_SERVICE_MESSAGE);
                msgRecent.setDate(System.currentTimeMillis());
                msgRecent.setUuid(genUuid(this.getOwner().getUid(), time));

                seMsg.setBy2(jumpData[1]);

                if (content.has("title") && cate != null) {
                    cate.setDigst(content.getString("title") != null ? content.getString("title") : "");
                    cate.setBubble_num(1);
                    CateDaoHelper.getInstance(this.getManager().getContext()).save(cate);
                }

                if (ct == 1) {
                    msgHistory.setContent_type(IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST);
                    msgHistory.setContent(title);
                    msgRecent.setContent(title);
                    seMsg.setAvatar(content.getString("avatar"));
                    seMsg.setDigst(digst);
                    seMsg.setImage_url(content.getString("pics"));
                    //TODO 是否要区分极光
                    seMsg.setIntent_data(jumpData[0]);
                    seMsg.setTitle(title);
                    seMsgRe.setDigst(title);
                    seMsg.setSubDigst(subDigst);
                    ServiceMessageDaoHelper.getInstance(
                            this.getManager().getContext()).save(seMsg);
                } else if (ct == 2) {
                    msgHistory.setContent_type(IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND);
                    seMsg.setImage_url(content.getString("pic"));
                    seMsg.setIntent_data(jumpData[0]);
                    seMsg.setExra(content.getJSONArray("list").toString());
                    seMsg.setDigst(digst);
                    seMsgRe.setDigst(title);
                    msgRecent.setContent(title);
                    msgHistory.setContent(title);
                    seMsg.setTitle(title);
                    seMsg.setSubDigst(subDigst);
                    ServiceMessageDaoHelper.getInstance(
                            this.getManager().getContext()).save(seMsg);
                }

                ServiceMessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).save(seMsgRe);
                msgHistory.setAction(BroadcastUtil.ACTION_MSG_ADD_ONLINE);
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MSG, new Gson().toJson(msgHistory),
                        false);
                MessageHistoryDaoHelper.getInstance(this.getManager().getContext()).save(msgHistory);
                MessageRecentDaoHelper.getInstance(
                        this.getManager().getContext()).save(msgRecent);

                if (seMsgRe.getBy3() != null && seMsgRe.getBy3().equals("1")) {
                    mIsShowNfBar = true;
                } else {
                    if (!showNotify) {
                        mIsShowNfBar = true;
                    } else {
                        mIsShowNfBar = false;
                    }
                }
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MSG_ADD, null, false);
            }
        }else {     //重复的消息，IM先推送过来
           msgHistory = MessageHistoryDaoHelper.getInstance(this.getManager().getContext()).findByMid(this.getOwner().getUid(),mid+"");
            msgHistory.setServiceJumpData(jumpData[1] != null ? jumpData[1] : "");
            msgHistory.setPushFrom(_pushType + "");
        }
        if (IConst.PUSH_TYPE_JPUSH.equals(_pushType + "")) {
            if (!mIsShowNfBar)      //是否显示通知栏，此时是显示
                BroadcastUtil.sendBroadcastToUI(this.getManager().getContext(),
                        BroadcastUtil.ACTION_MSG, new Gson().toJson(msgHistory), true);
        } else if (IConst.PUSH_TYPE_MI.equals(_pushType + "") || IConst.PUSH_TYPE_HUAWEI.equals(_pushType + "")) {
            this.getManager().getContext().sendBroadcast(BroadcastUtil.sendNotifyBroadCast(
                                                                        this.getManager().getContext(),
                                                                        jumpData[1],
                                                                        _pushType + "",
                                                                        mid + ""));
        }


    }

}
