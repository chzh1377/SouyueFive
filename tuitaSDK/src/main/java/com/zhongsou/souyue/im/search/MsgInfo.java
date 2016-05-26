package com.zhongsou.souyue.im.search;

import com.google.gson.annotations.SerializedName;
import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class MsgInfo implements Serializable,DontObfuscateInterface {
	
	@SerializedName("msg_id")
	public int msgId;
	@SerializedName("type")
	public int sessionType;
	@SerializedName("session_id")
	public long sessionId;
	@SerializedName("msg")
	public String msg;
}
