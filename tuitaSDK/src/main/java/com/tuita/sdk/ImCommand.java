package com.tuita.sdk;

import java.util.List;

import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.TuitaPacket;
import com.tuita.sdk.im.db.module.Contact;

public class ImCommand {
	public static final int TUITA_IM_COMMAND_STATUS_RUNNING = 0;
	public static final int TUITA_IM_COMMAND_STATUS_SUCCESS = 1;
	public static final int TUITA_IM_COMMAND_STATUS_TIMEOUT = 2;
	public static final int TUITA_IM_COMMAND_STATUS_ERROR = 3;
	private long tid;
	private TuitaPacket packet;
	private int status; // 0 running 1 success 2 timeout 3 error
	
	private int type;
	private int op;
	private Contact contact;
	private List<Contact> contacts;
	private String text;
	private long uid;
	private int giftzsbnum;
	private boolean isGroupSaved;
	private boolean isNewsNotifyShielded;
    private int mRetryCount;        //消息发送的重试次数

    public int getRetryCount() {
        return mRetryCount;
    }

    public void setRetryCount(int retryCount) {
        this.mRetryCount = retryCount;
    }

	public boolean isGroupSaved() {
		return isGroupSaved;
	}

	public void setGroupSaved(boolean isGroupSaved) {
		this.isGroupSaved = isGroupSaved;
	}

	public boolean isNewsNotifyShielded() {
		return isNewsNotifyShielded;
	}

	public void setNewsNotifyShielded(boolean isNewsNotifyShielded) {
		this.isNewsNotifyShielded = isNewsNotifyShielded;
	}

	public ImCommand(TuitaPacket packet) {
		this.packet = packet;
	}

	public static ImCommand newConnectCmd(TuitaIMManager immanger, long lastUpdate,String version) {
        long tid = System.currentTimeMillis();
		return new ImCommand(TuitaPacket.createIMConnectPacket(immanger,lastUpdate,version,tid));
	}

    public static ImCommand newConnectIMCmd(TuitaSDKManager manager,TuitaIMManager immanger, long lastUpdate,String version) {
        long tid = System.currentTimeMillis();
        ImCommand cmd = new ImCommand(TuitaPacket.createConnectPacket(manager,immanger, lastUpdate, version,tid));
        cmd.setTid(tid);
		cmd.setType(TuitaPacket.TUITA_MSG_TYPE_CONNECT);
        return cmd;
    }

	public static ImCommand newLogoutCmd() {
		return new ImCommand(TuitaPacket.createIMLogoutPacket());
	}

	public static ImCommand newAckCmd(long tid) {
		return new ImCommand(TuitaPacket.createIMAckPacket(tid));
	}

	public static ImCommand newSendMessageCmd(int type, long id, int contentType, String content, String uuid, int retry,int retryCount) {
		long tid = System.currentTimeMillis();
		ImCommand cmd = new ImCommand(TuitaPacket.createIMSendMessagePacket(tid, type, id, contentType, content, uuid, retry));
		cmd.setTid(tid);
		cmd.setText(uuid);
        cmd.setRetryCount(retryCount);
		return cmd;
	}

	public static ImCommand newUserOpCmd(TuitaIMManager immanger, int op, Contact contact, String text,int originType) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.createIMUserOPPacket(immanger, tid, op, contact.getChat_id(), text, originType);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setType(packet.getType());
		cmd.setOp(op);
		cmd.setContact(contact);
		cmd.setText(text);
		return cmd;
	}

    /**
     * 私聊消息免打扰
     * @param op
     * @param uid
     * @param is_news_notify
     * @return
     */
    public static ImCommand newIMnotifyCmd(int op, long uid, boolean is_news_notify) {
        long tid = System.currentTimeMillis();
        TuitaPacket packet = TuitaPacket.createIMNotifyPacket(tid, op, uid, is_news_notify);
        ImCommand cmd = new ImCommand(packet);
        cmd.setTid(tid);
        cmd.setType(packet.getType());
        cmd.setOp(op);
        cmd.setUid(uid);
        cmd.setNewsNotifyShielded(is_news_notify);
        return cmd;
    }

	public static ImCommand newGroupOpCmd(TuitaIMManager immanger, int op,List<Long> uids) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.createIMGroupOPPacket(immanger, tid, op, uids);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}

	public static ImCommand addGroupMembersOpCmd(TuitaIMManager immanger, int op,String gid,List<Long> uids) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.addIMGroupMembersAcket(immanger, tid, op, gid, uids);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}

    public static ImCommand addGroupMembers(TuitaIMManager immanger, int op,String gid,String inviterId,int mode,List<Long> uids,String source) {
        long tid = System.currentTimeMillis();
        TuitaPacket packet = TuitaPacket.addGroupMembersAcket(immanger, tid, op, gid, inviterId, mode, uids, source);
        ImCommand cmd = new ImCommand(packet);
        cmd.setTid(tid);
        cmd.setOp(op);
        cmd.setType(packet.getType());
        return cmd;
    }

	public static ImCommand retreatGroupOpCmd(TuitaIMManager immanger, int op,String gid,String nextOwnerId) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.retreatGroupAcket(immanger, tid, op, gid, nextOwnerId);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}
	public static ImCommand saveGroupConfigOpCmd(TuitaIMManager immanger, int op,String gid,boolean isGroupSaved,boolean isNewsNotifyShielded) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.saveGroupConfigAcket(immanger, tid, op, gid, isGroupSaved, isNewsNotifyShielded);
		ImCommand cmd = new ImCommand(packet);
		cmd.setOp(op);
		cmd.setTid(tid);
		cmd.setGroupSaved(isGroupSaved);
		cmd.setNewsNotifyShielded(isNewsNotifyShielded);
		cmd.setUid(Long.parseLong(gid));
		cmd.setType(packet.getType());
		return cmd;
	}
	public static ImCommand updateGroupNickNameOpCmd(TuitaIMManager immanger, int op,String gid,String nick) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.updateGroupNickNameAcket(immanger, tid, op, gid, nick);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}
	public static ImCommand getGroupDetailsOpCmd(TuitaIMManager immanger, int op,String gid) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.getGroupDetailsAcket(immanger, tid, op, gid);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}

	public static ImCommand getMemberDetailOpCmd(TuitaIMManager immanger, int op,long gid,long memberId) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.getMemberDetailAcket(immanger, tid, op, gid, memberId);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}
	public static ImCommand getUserOpCmd(TuitaIMManager immanger, int op,long gid) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.getUserAcket(immanger, tid, op, gid);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setOp(op);
		cmd.setType(packet.getType());
		return cmd;
	}
	public static ImCommand newUpdateCmd(TuitaIMManager immanger, int op, long uid, String text) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.createUpdatePacket(immanger, tid, op, uid, text);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setType(packet.getType());
		cmd.setOp(op);
		cmd.setUid(uid);
		cmd.setText(text);
		return cmd;
	}

	public static ImCommand newInfoCmd(TuitaIMManager immanger, int op, long lastUpdate, String text) {
		long tid = System.currentTimeMillis();
		ImCommand cmd = new ImCommand(TuitaPacket.createInfoPacket(immanger, tid, op, lastUpdate, text));
		cmd.setTid(tid);
		return cmd;
	}

	public static ImCommand newSearchCmd(String keyword) {
		long tid = System.currentTimeMillis();
		ImCommand cmd = new ImCommand(TuitaPacket.createUserSearchMessagePacket(tid, keyword));
		cmd.setTid(tid);
		return cmd;
	}

	public static ImCommand newGiftzsbCmd(long uid, int num) {
		long tid = System.currentTimeMillis();
		String uuid = uid + "_" + tid;
		ImCommand cmd = new ImCommand(TuitaPacket.createGiftzsbPacket(tid, uid, num, uuid));
		cmd.setTid(tid);
		cmd.setUid(uid);
		cmd.setGiftzsbnum(num);
		cmd.setText(uuid);
		return cmd;
	}
	
	public static ImCommand newChargezsbCmd() {
		long tid = System.currentTimeMillis();
		String uuid = "" + tid;
		ImCommand cmd = new ImCommand(TuitaPacket.createChargezsbPacket(tid, uuid));
		cmd.setTid(tid);
		cmd.setText(uuid);
		return cmd;
	}

	public static ImCommand newContactStatusCmd(List<Contact> contacts) {
		long tid = System.currentTimeMillis();
		ImCommand cmd = new ImCommand(TuitaPacket.createContactsStatusMessagePacket(tid, contacts));
		cmd.setTid(tid);
		cmd.setContacts(contacts);
		return cmd;
	}
	
	public static ImCommand newContactUploadCmd(List<Contact> contacts) {
		long tid = System.currentTimeMillis();
		ImCommand cmd = new ImCommand(TuitaPacket.createContactsUploadMessagePacket(tid, contacts));
		cmd.setTid(tid);
		cmd.setContacts(contacts);
		return cmd;
	}
	
	public static ImCommand newGetMessageCmd(TuitaIMManager immanger, int type, String chatid, long maxMid) {
		long tid = System.currentTimeMillis();
		ImCommand cmd = new ImCommand(TuitaPacket.createGetMessagePacket(immanger, tid, type, chatid, maxMid));
		cmd.setTid(tid);
		return cmd;
	}

    public static ImCommand findGroupInfoCmd(TuitaIMManager immanger, int type, int op, long groupid, List<Long> memberIds) {
        long tid = System.currentTimeMillis();
        TuitaPacket packet = TuitaPacket.createfindGroupInfoPacket(immanger, tid, op, groupid,memberIds);
        ImCommand cmd = new ImCommand(packet);
        cmd.setTid(tid);
        cmd.setType(packet.getType());
        cmd.setOp(op);
        return cmd;
    }

    /**
     * 118 服务号相关-服务号免打扰命令
     * @param srvId
     * @param isNewsNotifyShielded
     * @return
     */
    public static ImCommand saveServiceMsgNotify(long srvId,boolean isNewsNotifyShielded) {
        long tid = System.currentTimeMillis();
        TuitaPacket packet = TuitaPacket.saveServiceMsgNotify(tid, srvId, isNewsNotifyShielded);
        ImCommand cmd = new ImCommand(packet);
        cmd.setOp(1);
        cmd.setTid(tid);
        cmd.setNewsNotifyShielded(isNewsNotifyShielded);
        cmd.setUid(srvId);
        cmd.setType(packet.getType());
        return cmd;
    }

    /**
     * 获取服务号详细信息
     * @param srvId
     * @return
     */
    public static ImCommand getServiceMsgDetailCommand(long srvId) {
        long tid = System.currentTimeMillis();
        TuitaPacket packet = TuitaPacket.getServiceMsgDetailPacket(tid, srvId);
        ImCommand cmd = new ImCommand(packet);
        cmd.setOp(2);
        cmd.setTid(tid);
        cmd.setUid(srvId);
        cmd.setType(packet.getType());
        return cmd;
    }
	/**
     *  5.2 更新用户信息
	 *
     * @param
     * @return
     */
	public static ImCommand newUpdateCmd(TuitaIMManager immanger, int op, long uid,boolean isNewsNotifyShielded ,boolean hasSound,boolean isVibrate) {
		long tid = System.currentTimeMillis();
		TuitaPacket packet = TuitaPacket.createUpdatePacket(immanger, tid, op, uid,isNewsNotifyShielded,hasSound,isVibrate);
		ImCommand cmd = new ImCommand(packet);
		cmd.setTid(tid);
		cmd.setType(packet.getType());
		cmd.setOp(op);
		cmd.setUid(uid);
		return cmd;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public TuitaPacket getPacket() {
		return packet;
	}

	public void setPacket(TuitaPacket packet) {
		this.packet = packet;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getGiftzsbnum() {
		return giftzsbnum;
	}

	public void setGiftzsbnum(int giftzsbnum) {
		this.giftzsbnum = giftzsbnum;
	}

	

}