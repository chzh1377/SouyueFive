package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

public class SignatureBean  implements  DontObfuscateInterface {
	// id: 主键，删除时用此ID，
	// mood_id:心情图片ID，
	// signature:心情签名，
	// create_time:创建时间
	private String id;
	private int mood_id;
	private String signature;
	private String create_time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMood_id() {
		return mood_id;
	}

	public void setMood_id(int mood_id) {
		this.mood_id = mood_id;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

}
