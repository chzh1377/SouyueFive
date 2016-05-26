package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class CircleMemberItem implements Serializable, DontObfuscateInterface {
	private String nickname;
	private String image;
	private long member_id;
	private int is_bantalk; // 是否禁言
	private int role;
	private long user_id;
	private long interest_id;

    private String indexKey;    //索引字母
    
    public long getInterest_id() {
		return interest_id;
	}
    
    public void setInterest_id(long interest_id) {
		this.interest_id = interest_id;
	}
    
    public long getUser_id() {
		return user_id;
	}
    
    public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getIs_bantalk() {
		return is_bantalk;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getRole() {
		return role;
	}

	public void setIs_bantalk(int is_bantalk) {
		this.is_bantalk = is_bantalk;
	}

	public long getMember_id() {
		return member_id;
	}

	public void setMember_id(long member_id) {
		this.member_id = member_id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

    public String getIndexKey() {
        return indexKey;
    }

    public void setIndexKey(String indexKey) {
        this.indexKey = indexKey;
    }
}
