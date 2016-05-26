package com.tuita.sdk;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.log.Logger;
import org.json.JSONObject;

import com.tuita.sdk.TuitaSDKManager.ReadListener;
/**
 * 
 * @author fangxm
 *
 */
class DefaultReadListener implements ReadListener {
	private static final String LOGTAG = DefaultReadListener.class.getSimpleName();
	private TuitaSDKManager manager;
	
	private TuitaIMManager imManager;
	private String notice_recieved = "notice_recieved";	  //Umeng

	protected DefaultReadListener(TuitaSDKManager manager) {
		this.manager = manager;
		this.imManager=TuitaIMManager.getInstance(manager);
	}
	/*
	 * 
	 * 返回 {"t":"消息类型，连接消息","c":"clientid","sn":"本次连接token，验证用"}
	 * 
	 * 
	 * 发送 {
	 * "t":"消息类型，接收消息",
	 * "mid":"",
	 * "gm":"0/1",
	 * "sn":"根据appid/appkey/token/msg，生成的加密串签名",
	 * "msg":{
	 * 		"type":"消息数据类型",
	 * 		"transtype":"消息数据类型",
	 * 		"title":"消息数据",
	 * 		"text":"消息数据"}}

	 * @see com.tuita.sdk.tuitasdkmanager.readlistener#read(com.tuita.sdk.tuitaconnection, com.tuita.sdk.tuitapacket)
	 */
	@Override
	public boolean read(TuitaConnection conn, TuitaPacket packet) {
		try {
			String str = packet.toString();
			if (str.equals(TuitaPacket.TUITA_PACK_PING)) {
				manager.getPingNoAckCount().set(0);
				return true;
			}

			JSONObject json = new JSONObject(str);
			int type = Integer.parseInt(getJson(json, "t"));
			packet.setType(type);
            packet.setTid(getJson(json,"tid").equals("") ? 0 : Long.parseLong(getJson(json, "tid")));
			if (type == TuitaPacket.TUITA_MSG_TYPE_CONNECT) {
				String cid = getJson(json, "c");
				if (isNotEmpty(cid)) {
					Log.i(LOGTAG, "read,clientId = " + cid);
					if (! manager.getClientID().equals(cid)) {
						manager.setClientID(cid);
					}
					manager.setConnState(TuitaSDKManager.CONN_STATE_NOTCONNECT);
					imManager.setTuitaIMState(TuitaIMManager.TUITA_IM_STATE_DISCONNECT);
					if (manager.getTuitaState() != TuitaSDKManager.TUITA_STATE_CONNECT) {
						manager.setTuitaState(TuitaSDKManager.TUITA_STATE_CONNECT);
					}
					manager.getPingNoAckCount().set(0);

					MobclickAgent.onEvent(manager.context, notice_recieved);
					ContextUtil.notify(manager.context, Constants.TYPE_GET_CLIENTID, cid);
				}
				String sn = getJson(json, "sn");
				if (isNotEmpty(sn)) {
					manager.setToken(sn);
				}

                TuitaIMManager.getInstance(manager).dealRead(type, json);

                android.util.Log.i("Tutiaread","----read tuita-----");
//                imManager.im_innerconnect();
			} else if (type == TuitaPacket.TUITA_MSG_TYPE_RECEIVE_NOTGM || type == TuitaPacket.TUITA_MSG_TYPE_RECEIVE_ISGM) {
				String sn = getJson(json, "sn");
				String msg = getJson(json, "msg");
				String mid = getJson(json, "mid");
				String gm = getJson(json, "gm");
//              long currentTime = json.has("tm") ? json.getLong("tm") : System.currentTimeMillis();

				if (isNotEmpty(sn, msg, mid, gm)) {
					manager.ack(type, mid, Integer.parseInt(gm));
//					if (Long.parseLong(mid) <= Long.parseLong(manager.getLastMSGTime())) {
//						Log.i(TuitaSDKManager.TAG, "duplicate or old msg:" + mid);
//						return true;
//					}
					manager.setLastMSGTime(mid);
					manager.setLastMSGType(gm);
					JSONObject msgObj = new JSONObject(msg);
					String title = getJson(msgObj, "title");
					int channel = getJsonInt(msgObj, "channel", -1);
					String description = getJson(msgObj, "description");
//					String body = getJson(msgObj, "body");
                    Object body = msgObj.get("body");
                    if (body instanceof JSONObject) {
                        JSONObject bodyObj = new JSONObject(body.toString());
                        if (bodyObj.has("t") && getJson(bodyObj,"t").equals("kicked_out")) {
                            String token = getJson(bodyObj, "token");
                            String msg_kicked = getJson(bodyObj, "msg");
                            ContextUtil.notify2Souyue(manager.context, token, msg_kicked);
                        }else{
                            if (isNotEmpty(title, description, body) && !RecordMsgUtil.hasMsg(manager.context, mid)) {
                                ContextUtil.notify(manager.context, Constants.TYPE_GET_DATA, channel, title, description, body.toString());
                                RecordMsgUtil.editMsg(manager.context, mid);
                            }
                            RecordMsgUtil.deleteMsg(manager.context, str);
                        }
                    }else {
                        if (isNotEmpty(title, description, body) && !RecordMsgUtil.hasMsg(manager.context, mid)) {
                            ContextUtil.notify(manager.context, Constants.TYPE_GET_DATA, channel, title, description, body.toString());
                            RecordMsgUtil.editMsg(manager.context, mid);
                        }
                        RecordMsgUtil.deleteMsg(manager.context, str);
                    }
				}
			} else if (type >= TuitaPacket.TUITA_MSG_TYPE_IM_CONNECT) {
                TuitaIMManager.getInstance(manager).dealRead(type, json);
			}
		} catch (Exception e) {
			Logger.e("tuita","DefaultReadListener.read", "read()  Exception t = " + packet.getType(), e);
			e.printStackTrace();
		}
		return true;
	}
	private static String getJson(JSONObject json, String key) {
		try {
//			Object obj = json.get(key);
            return json.has(key) ? json.get(key).toString() : "";
//			return obj == null ? "" : obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	private static int getJsonInt(JSONObject json, String key, int defaultValue) {
		try {
			return Integer.parseInt(getJson(json, key));
		} catch (Exception e) {
//			e.printStackTrace();
			return defaultValue;
		}
	}
	private static boolean isNotEmpty(Object... obj) {
		for (Object o : obj) {
			if (o == null || obj.toString().trim().length() == 0) {
				return false;
			}
		}
		return true;
	}
	//	private boolean messageAuth(String msg, String sign) {
	//		StringBuilder build = new StringBuilder();
	//		build.append(ContextUtil.getAppId(manager.getContext())).append(",").append(ContextUtil.getAppKey(manager.getContext())).append(",").append(manager.getToken()).append(",").append(msg);
	//		String authStr = MD5Util.md5(build);
	//		if (authStr.equals(sign)) {
	//			return true;
	//		}
	//		return false;
	//	}
}
