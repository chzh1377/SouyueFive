package com.zhongsou.souyue.im.aidl;

interface ImAidlService {
    void im_connect(in String version);//上线 通
 //   boolean appOnline();//程序前台运行
//    void setAppOnline(in boolean status);//程序运行状态。前台，后台
    void im_logout();//IM下线
    boolean im_search(in String keyword);//查找好友 通
    boolean newGroupOp(in int op, in List uids);//创建群
    boolean addOrDeleteGroupMembersOp(in int op, in String gid, in List uids);//加好友入群,删除群好友
    boolean retreatGroupMembersOp(in int op, in String gid, in String nextOwnerId);//退出群
    boolean saveGroupConfigOp(in int op, in String gid, in boolean isGroupSaved, in boolean isNewsNotifyShielded);//保存配置信息
    boolean updateGroupNickNameOp(in int op, in String gid, in String nick);//修改群昵称或者群成员昵称
    boolean getGroupDetailsOp(in int op, in String gid);//获取群详细信息
    boolean getUserOp(in int op, in long uid);//获取用户并查看是否是好友
    boolean im_userOp(in int o, in long uid, in String nick, in String avatar, in String text,int originType);//加好友 通
    boolean im_info(in int op, in String text);//获得分组，好友列表 调试中
    boolean im_contacts_status(in String contactJson);//手机联系人状态匹配
    boolean im_contacts_upload(in String contactJson);//手机联系人上传
    //String im_sendMessage(int type, in long uidorgid, in int contentType, in String content, in String retry);//发送消息
    String im_saveMessage(int type, in long uidorgid, in int contentType, in String content, in String retry);//发送消息(针对语音，图片等，只存不发)
    boolean im_update(in int op, in long uid, in String text);//更新各种信息 op 1更新昵称 2更新群里的昵称 3更新好友名称
	boolean im_giftzsb(in long uid, in int num);//赠送中搜币
	boolean addGroupMemberOp(in int op, in String gid,in String inviterId,in int mode,in List uids,in String source);//加人进群   5.2 添加进群时候的来源
	boolean findGroupInfo(in int op, in long group_id, in List memberIds);//查询群信息
	boolean getMemberDetail(in int op,in long group_id,in long memberId); //获取群成员详细信息 op 9
	boolean updateNewsNotify(in int op, in long uid, in boolean is_news_notify);//私聊消息提醒
	boolean saveServiceMsgNotify(in long srvId,in boolean isNewsNotifyShielded);//服务号消息免打扰
	void initImService();//初始化tuita用户，以后可能去掉
	void cancelNotify(int id);//取消通知栏的AIDL方法
	int getNotifyNum(int id);//获得某个ID的通知栏计数的AIDL方法

	boolean setUserMessageNotify(in int op,in long uid,boolean isNewsNotifyShielded ,boolean hasSound,boolean isVibrate); // 5.2 添加的传递给 服务器关于，消息免打扰的问题

//	boolean im_chargezsb();//系统赠送中搜币

    //by  zhangwb
    //String db_getNewFriend();//本地新朋友
    //String db_getServiceMsg(in long cateId);//本地同一个cateId服务号
    //String db_getServiceMsgRe(in long cateId);//本地同一个cateId服务号最近表
    //String db_getServiceMsgByServiceid(in long cateId,in long serviceMsgId,in int queryType,in long session_order);//本地同一个serviceMsgId服务号
    //void db_delNewFriend(in long chat_id);//删除新朋友
    //void db_clearNewFriend();//删除全部新朋友
    //String db_getServiceMessageByMyid(in long myid);//查找服务号列表
    //String db_getContact();
    //String db_getMessageRecent();//最近聊天
    //String db_getServiceMessageMessage(in String uuid);//by uuid获得服务号信息
	//void db_clearSouyueMessageRecentBubble(in long serviceId,in long cateId);//清除服务号最近气泡
    //void db_delMessageRecent(in long chat_id);//根据id最近聊天删除

    //by   zoulu
    //String db_getMessage(in long chat_id, in long session_order, in int queryType);//获取消息历史
    //void db_clearMessageHistory(in long chat_id); //单个会话删除全部记录
    //void db_deleteSelectedMessageHistory(in long chat_id,in String uuid);
    //void db_clearFriendBubble();//清除好友气泡
	//void db_clearMessageRecentBubble(in long chat_id);//清除最近联系人气泡
	//void db_updateSouyueMessageRecentBubble(in long chat_id,in long service_id);//更改最近联系人气泡数
	//void db_updateMessageHistoryTime(in String uuid,in int content_type, in long chat_id, in long currentTime);//修改密信时间
	//String db_getConfig();//获取泡得个数
	//String db_getContactByid(in long chat_id);//获取联系人
	//void db_updateMessageHistoryForVoiceRead(in String uuid,in int content_type, in long chat_id, in int isRead);//更新语音读与未读
	//String db_findMemberListByGroupidandUid(long groupid,long member_id);//根据groupid和memberid查询群昵称

	//by  gengsong
	//String db_findMemberListByGroupid(long group_id);//根据groupid查member
	//String db_findGroupListByGroupidAndIsSaved(long group_id,int is_saved);//根据groupid和is_saved查显示在群聊中的群
	//db_updateGroup(long group_id);//根据groupid更新Group
	//void db_delGroupMessageRecent(in long group_id);//根据group_id删除群聊天记录
	//String db_findGroupMemberByMemberid(long member_id);//根据memberid查询群成员信息
	//void db_deleteGroupSelectedMessageHistory(in long chat_id,in String uuid);//删除群聊历史
	//void db_deleteMemberById(long member_id,in long gid);//根据member_id 删除对应成员
	//void db_updateCommentName(in long chat_id,String commentName);//更新备注名
	//String db_findGourp(in long group_id);//根据群id查群信息
	//void db_updateRecent(String msg);//update最近联系人信息
    //void db_updateRecentBy1(long chatid,long myid,String num);//update最近联系人的by1状态

}
