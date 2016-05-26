package com.tuita.sdk;

import android.text.TextUtils;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.IConst;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author fangxm@zhongsou.com
 */
/*
 * 
sdk 连上 node
发送 {
	"t":"消息类型，连接消息",
	"v":"版本号",
	"c":"clientid",
	"app":"appid",
	"key":"appkey",
	"last":"上个群发消息的时间戳"
}
返回 {
	"t":"消息类型，连接消息",
	"c":"clientid",
	"sn":"本次连接token，验证用"
}

sdk 心跳包
发送 {}

node 推送消息 sdk
发送 {
	"t":"消息类型，接收消息",
	"mid":"消息id",
	"gm":"0/1，是否群发，1为群发",
	"sn":"根据appid/appkey/token/msg，生成的加密串签名",
	"msg":"{   //msg是json格式字符串。
		"title":"消息数据",
		"body":{}
			}"
	}

 *
 */
public class TuitaPacket implements Serializable{
	protected static final String TUITA_PACK_PING = "{}";
	public static final int TUITA_MSG_TYPE_CONNECT = 1;
	protected static final int TUITA_MSG_TYPE_RECEIVE_NOTGM = 2;
	protected static final int TUITA_MSG_TYPE_RECEIVE_ISGM = 3;
	protected static final int TUITA_MSG_TYPE_PING = 4;
	protected static final int TUITA_MSG_TYPE_SOUYUE_LOGINOUT = 5;

	protected static final int TUITA_MSG_TYPE_IM_CONNECT = 100;
	protected static final int TUITA_MSG_TYPE_IM_MESSAGE_ACK = 101;
	protected static final int TUITA_MSG_TYPE_IM_MESSAGE_OFFLINE = 102;
	protected static final int TUITA_MSG_TYPE_IM_MESSAGE_ONLINE = 103;
	protected static final int TUITA_MSG_TYPE_IM_MESSAGE = 104;
	protected static final int TUITA_MSG_TYPE_IM_MESSAGE_HISTORY = 105;
	protected static final int TUITA_MSG_TYPE_IM_RELATION_USER = 106;
	protected static final int TUITA_MSG_TYPE_IM_RELATION_GROUP = 107;
	protected static final int TUITA_MSG_TYPE_IM_UPDATE = 108;
	protected static final int TUITA_MSG_TYPE_IM_INFO = 109;
	protected static final int TUITA_MSG_TYPE_IM_LOGOUT = 110;
	protected static final int TUITA_MSG_TYPE_IM_CONTACTS_STATUS = 111;
	protected static final int TUITA_MSG_TYPE_IM_USER_SEARCH = 112;
	protected static final int TUITA_MSG_TYPE_IM_ZSB_GIFT = 113;
	protected static final int TUITA_MSG_TYPE_IM_ZSB_CHARGE = 114;
	protected static final int TUITA_MSG_TYPE_IM_CONTACTS_UPLOAD = 115;
	protected static final int TUITA_MSG_TYPE_IM_KICKED_OUT = 116;
	protected static final int TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG = 118;      //服务号相关

	//	private static final int TUITA_MSG_ISGM = 1;
	//	private static final int TUITA_MSG_NOTGM = 0;
	
	private int type;

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    private long tid;    //时间戳
	private byte[] data;
	public TuitaPacket(byte[] data) {
		this.data = data;
	}
	protected TuitaPacket(int type, byte[] data) {
		this.type = type;
		this.data = data;
	}

    protected TuitaPacket(int type, byte[] data,long tid) {
        this.type = type;
        this.data = data;
        this.tid = tid;
    }
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public byte[] toBytes() throws Exception {
		byte[] result = null;
		if (type == TUITA_MSG_TYPE_CONNECT) {
			result = new byte[data.length + 2];
			System.arraycopy(shortToByte(data.length), 0, result, 0, 2);
			System.arraycopy(data, 0, result, 2, data.length);
		} else {
            result = new byte[data.length + 4];
            System.arraycopy(intToByte(data.length), 0, result, 0, 4);
            System.arraycopy(data, 0, result, 4, data.length);
        }
        //如果数据大于1k 使用GZip压缩
        if(data.length > GzipUtil.BUFFER) {
            byte[] g = {'g'};
            byte[] temp = GzipUtil.compress(data);//压缩数组
            temp = GzipUtil.addBytes(g, temp);//给压缩后的数组拼接g
            temp = GzipUtil.addBytes(intToByte(temp.length), temp);
            return temp;
        }
        return result;
	}
	public String toString() {
        byte[] newdata = new byte[data.length - 1];
		try {
            if(data[0] == 103){//如果字节数组中第一个字母为g，就解压缩。g的ASCII=103。
                System.arraycopy(data,1,newdata,0,data.length - 1);
                byte[] decompressByte = GzipUtil.decompress(newdata);
                return new String(decompressByte, "UTF-8");
            }else{
                return new String(data, "UTF-8");
            }
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	protected static TuitaPacket createPingPacket() {
		byte[] data = null;
		try {
			data = TUITA_PACK_PING.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_PING, data);
	}
	private static void putJson(JSONObject json, String key, Object value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	protected static TuitaPacket createConnectPacket(TuitaSDKManager manager,TuitaIMManager immanager, long lastUpdate,String version,long tid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", "" + TUITA_MSG_TYPE_CONNECT);
		putJson(json, "v", TuitaSDKManager.TUITA_SDK_VERSION);
        putJson(json, "tid", tid);
		putJson(json, "c", manager.getDeviceId());
		putJson(json, "app", ContextUtil.getAppId(manager.getContext()));
		putJson(json, "key", ContextUtil.getAppKey(manager.getContext()));
		putJson(json, "last", manager.getLastMSGTime());
		putJson(json, "gm", manager.getLastMSGType());
		putJson(json, "device", 1);
		putJson(json,"imei",DeviceUtil.getIMEI(manager.getContext()));
		putJson(json,"mac",DeviceUtil.getMacAddr(manager.getContext()));
		putJson(json,"iccid",DeviceUtil.getSIMNum(manager.getContext()));
		putJson(json,"uuid",DeviceUtil.getUUID(manager.getContext()));
		putJson(json, "deviceToken", DeviceUtil.getPushRegID(immanager.getManager().getContext()));
        //4.2.2协议整合     100
        JSONObject dataJson = new JSONObject();
        putJson(dataJson, "uid", immanager.getOwner().getUid());
        putJson(dataJson, "nick", immanager.getOwner().getNick());
        putJson(dataJson, "pass", immanager.getOwner().getPass());
        putJson(dataJson, "avatar", immanager.getOwner().getAvatar());
        putJson(dataJson, "device", 1);
        putJson(dataJson, "lastContact", lastUpdate);
		putJson(dataJson, "deviceName", DeviceUtil.getDeviceName());
		putJson(dataJson, "pushChannel", DeviceUtil.getPushChannel());
		putJson(dataJson, "reset", false);
		putJson(dataJson, "deviceToken", DeviceUtil.getPushRegID(immanager.getManager().getContext()));
        putJson(dataJson, "sv", version);
        putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_CONNECT, data);
	}
	protected static TuitaPacket createAckPacket(int type, String mid, int gm) {
		JSONObject json = new JSONObject();
		putJson(json, "t", "" + type);
		putJson(json, "mid", mid);
		putJson(json, "gm", "" + gm);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(type, data);
	}
	
	public static TuitaPacket createIMAckPacket(long tid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_MESSAGE_ACK);
		putJson(json, "tid", tid);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_MESSAGE_ACK, data);
	}

	public static TuitaPacket createIMUserOPPacket(TuitaIMManager immanager, long tid, int op, long uid, String text,int originType) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_USER);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "uid", uid);
		putJson(dataJson, "text", text);
        putJson(dataJson, "originType", originType);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_USER, data);
	}

    /**
     * 组私聊消息提醒包
     * @param tid
     * @param op
     * @param uid
     * @param is_news_notify
     * @return
     */
    public static TuitaPacket createIMNotifyPacket(long tid, int op, long uid, boolean is_news_notify) {
        JSONObject json = new JSONObject();
        putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_USER);
        putJson(json, "sn", "");
        putJson(json, "tid", tid);
        JSONObject dataJson = new JSONObject();
        putJson(dataJson, "op", op);
        putJson(dataJson, "uid", uid);
        putJson(dataJson, "isNewsNotifyShielded", is_news_notify);
        putJson(json, "data", dataJson);
        String jsonStr = json.toString();
        byte[] data = null;
        try {
            data = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_USER, data);
    }
	public static TuitaPacket createUpdatePacket(TuitaIMManager immanager, long tid, int op, long uid, String text) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_UPDATE);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "uid", uid);
		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_UPDATE, data);
	}
	/**
	 * 更新基本信息
	 * 消息免打扰的三个数据
	 * by qubian
	 */
	public static TuitaPacket createUpdatePacket(TuitaIMManager immanager, long tid, int op, long uid,  boolean isNewsNotifyShielded ,boolean hasSound,boolean isVibrate) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_UPDATE);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "uid", uid);
		putJson(dataJson, "isNewsNotifyShielded", isNewsNotifyShielded);
		putJson(dataJson, "hasSound", hasSound);
		putJson(dataJson, "isVibrate", isVibrate);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_UPDATE, data);
	}
	public static TuitaPacket createInfoPacket(TuitaIMManager immanager, long tid, int op, long lastUpdate, String text) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_INFO);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "lastUpdate", lastUpdate);
		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_INFO, data);
	}
	public static TuitaPacket createGetMessagePacket(TuitaIMManager immanager, long tid, int type, String chatid, long maxMid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_MESSAGE_HISTORY);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "mt", type);
		putJson(dataJson, "id", chatid);
		putJson(dataJson, "maxMid", maxMid);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_MESSAGE_HISTORY, data);
	}

    public static TuitaPacket createfindGroupInfoPacket(TuitaIMManager immanager, long tid, int op, long groupid, List<Long> memberIds) {
        JSONObject json = new JSONObject();
        putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
        putJson(json, "tid", tid);
        JSONObject dataJson = new JSONObject();
        putJson(dataJson, "op", op);
        putJson(dataJson, "gid", groupid);
        JSONArray membersArr = new JSONArray();
        for (Long s : memberIds) {
            membersArr.put(s);
        }
        putJson(dataJson, "memberIds", membersArr);
        putJson(json, "data", dataJson);
        String jsonStr = json.toString();
        byte[] data = null;
        try {
            data = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
    }

	public static TuitaPacket createIMGroupOPPacket(TuitaIMManager immanager, long tid, int op,List<Long> uid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
//		putJson(dataJson, "gid", gid);
		JSONArray uidsArr = new JSONArray();
		for (Long s : uid) {
			uidsArr.put(s);
		}
		putJson(dataJson, "uids", uidsArr);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}
	
	public static TuitaPacket retreatGroupAcket(TuitaIMManager immanager, long tid, int op,String gid,String nextOwnerId) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", Long.parseLong(gid));
		if(!TextUtils.isEmpty(nextOwnerId)) {
		  putJson(dataJson, "nextOwnerId", Long.parseLong(nextOwnerId));
		}
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}
	
	public static TuitaPacket addIMGroupMembersAcket(TuitaIMManager immanager, long tid, int op,String gid,List<Long> uids) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", Long.parseLong(gid));
		JSONArray uidsArr = new JSONArray();
		for (Long s : uids) {
			uidsArr.put(s);
		}
		putJson(dataJson, "uids", uidsArr);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}

	public static TuitaPacket addGroupMembersAcket(TuitaIMManager immanager, long tid, int op,String gid,String invite,int mode, List<Long> uids,String source) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", Long.parseLong(gid));
		putJson(dataJson, "inviterId", Long.parseLong(invite));
		putJson(dataJson, "mode", mode);
		if(!TextUtils.isEmpty(source))
		{
			putJson(dataJson, "source", source);
		}
		JSONArray uidsArr = new JSONArray();
		for (Long s : uids) {
			uidsArr.put(s);
		}
		putJson(dataJson, "uids", uidsArr);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}

	public static TuitaPacket saveGroupConfigAcket(TuitaIMManager immanager, long tid, int op,String gid,boolean isGroupSaved,boolean isNewsNotifyShielded) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", Long.parseLong(gid));
		putJson(dataJson, "isGroupSaved", isGroupSaved);
		putJson(dataJson, "isNewsNotifyShielded", isNewsNotifyShielded);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}
	
	public static TuitaPacket updateGroupNickNameAcket(TuitaIMManager immanager, long tid, int op,String gid,String nick) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", Long.parseLong(gid));
		putJson(dataJson, "nick", nick);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}
	
	public static TuitaPacket getGroupDetailsAcket(TuitaIMManager immanager, long tid, int op,String gid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", Long.parseLong(gid));
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}

	public static TuitaPacket getMemberDetailAcket(TuitaIMManager immanager, long tid, int op,long gid,long memberId) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "gid", gid);
		putJson(dataJson, "uid", memberId);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}
	public static TuitaPacket getUserAcket(TuitaIMManager immanager, long tid, int op,long uid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_RELATION_GROUP);
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "op", op);
		putJson(dataJson, "uid", uid);
//		putJson(dataJson, "text", text);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_RELATION_GROUP, data);
	}
	public static TuitaPacket createIMSendMessagePacket(long tid, int type, long id, int contentType, String content, String uuid, int retry) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_MESSAGE);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "mt", type);
		
		if (type == IConst.CHAT_TYPE_PRIVATE || type == IConst.CHAT_TYPE_GROUP || type == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
			putJson(dataJson, "rid", id);
		} else if(type == IConst.CHAT_TYPE_CMD) {
			putJson(dataJson, "rid", id);
		} else {
			putJson(dataJson, "gid", id);
		}
		if(contentType ==11) {
			putJson(dataJson, "ct", 1);
		} else {
			putJson(dataJson, "ct", contentType);
		}
		
		putJson(dataJson, "c", content);
		putJson(dataJson, "muid", uuid);
		if (retry == 1) {
			putJson(dataJson, "rs", 1);
		}
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_MESSAGE, data,tid);
	}
	public static TuitaPacket createContactsStatusMessagePacket(long tid, List<Contact> contacts) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_CONTACTS_STATUS);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();

		JSONArray contactArr = new JSONArray();
		for (Contact s : contacts) {
            JSONObject contactJson = new JSONObject();
            putJson(contactJson, "pn", s.getPhone());
            putJson(contactJson,"name",s.getNick_name());
			contactArr.put(contactJson);
		}
		putJson(dataJson, "contacts2", contactArr);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_CONTACTS_STATUS, data);
	}
	public static TuitaPacket createContactsUploadMessagePacket(long tid, List<Contact> contacts) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_CONTACTS_UPLOAD);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();

		JSONArray contactArr = new JSONArray();
		for (Contact s : contacts) {
            JSONObject contactJson = new JSONObject();
            putJson(contactJson, "pn", s.getPhone());
            putJson(contactJson,"name",s.getNick_name());
            contactArr.put(contactJson);
		}
		putJson(dataJson, "contacts2", contactArr);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_CONTACTS_UPLOAD, data);
	}
	public static TuitaPacket createUserSearchMessagePacket(long tid, String keyword) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_USER_SEARCH);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "key", keyword);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_USER_SEARCH, data);
	}
	public static TuitaPacket createGiftzsbPacket(long tid, long uid, int num, String uuid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_ZSB_GIFT);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "rid", uid);
		putJson(dataJson, "n", num);
		putJson(dataJson, "uuid", uuid);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_ZSB_GIFT, data);
	}
	
	
	public static TuitaPacket createChargezsbPacket(long tid, String uuid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_ZSB_CHARGE);
		putJson(json, "sn", "");
		putJson(json, "tid", tid);
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "uuid", uuid);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_ZSB_CHARGE, data);
	}
	public static TuitaPacket createIMConnectPacket(TuitaIMManager immanager, long lastUpdate,String version,long tid) {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_CONNECT);
		putJson(json, "tid", tid);
		putJson(json, "v", TuitaIMManager.TUITA_IM_SDK_VERSION);
		//putJson(json, "c", immanager.getManager().getDeviceId());
		JSONObject dataJson = new JSONObject();
		putJson(dataJson, "uid", immanager.getOwner().getUid());
		putJson(dataJson, "nick", immanager.getOwner().getNick());
		putJson(dataJson, "pass", immanager.getOwner().getPass());
		putJson(dataJson, "avatar", immanager.getOwner().getAvatar());
		putJson(dataJson, "device", 1);
        putJson(dataJson, "deviceName", DeviceUtil.getDeviceName());
        putJson(dataJson, "pushChannel", DeviceUtil.getPushChannel());
        putJson(dataJson, "reset", false);
        putJson(dataJson, "deviceToken", DeviceUtil.getPushRegID(immanager.getManager().getContext()));
		putJson(dataJson, "lastContact", lastUpdate);
		putJson(dataJson, "sv", version);
		putJson(json, "data", dataJson);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_CONNECT, data);
	}

    /**
     * 118  服务号相关-消息免打扰功能
     * @param tid
     * @param srvId
     * @param isNewsNotifyShielded
     * @return
     */
    public static TuitaPacket saveServiceMsgNotify(long tid,long srvId,boolean isNewsNotifyShielded) {
        JSONObject json = new JSONObject();
        putJson(json, "t", TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG);
        putJson(json, "tid", tid);
        JSONObject dataJson = new JSONObject();
        putJson(dataJson, "op", 1);
        putJson(dataJson, "srvId", srvId);
        putJson(dataJson, "isNewsNotifyShielded", isNewsNotifyShielded);
        putJson(json, "data", dataJson);
        String jsonStr = json.toString();
        byte[] data = null;
        try {
            data = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new TuitaPacket(TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG, data);
    }

    /**
     * 获取服务号详情 包
     * @param tid
     * @param srvId
     * @return
     */
    public static TuitaPacket getServiceMsgDetailPacket(long tid,long srvId) {
        JSONObject json = new JSONObject();
        putJson(json, "t", TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG);
        putJson(json, "tid", tid);
        JSONObject dataJson = new JSONObject();
        putJson(dataJson, "op", 2);
        putJson(dataJson, "srvId", srvId);
        putJson(json, "data", dataJson);
        String jsonStr = json.toString();
        byte[] data = null;
        try {
            data = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new TuitaPacket(TUITA_MSG_TYPE_IM_ABOUT_SERVICE_MSG, data);
    }


	public static TuitaPacket createIMLogoutPacket() {
		JSONObject json = new JSONObject();
		putJson(json, "t", TUITA_MSG_TYPE_IM_LOGOUT);
		String jsonStr = json.toString();
		byte[] data = null;
		try {
			data = jsonStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new TuitaPacket(TUITA_MSG_TYPE_IM_LOGOUT, data);
	}
	private static byte[] shortToByte(int num) {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			b[i] = (byte) (num >>> (8 - i * 8));
		}
		return b;
	}
	private static byte[] intToByte(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}
	public static int byteToShort(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 2; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}
	public static int byteToInt(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}
}
