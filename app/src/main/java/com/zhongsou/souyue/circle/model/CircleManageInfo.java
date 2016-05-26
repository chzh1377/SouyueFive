package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;

/**
 * 圈吧管理信息
 * @author chz
 *
 */
public class CircleManageInfo extends ResponseObject {
	private int is_private;				//0 不开启隐私保护|1 开启
	private String image;				//图像
	private String nickname;			//昵称
	private String signature;			//自我介绍
	private long member_count;			//圈成员数量
	private long need_audit_count;		//待审稿数量
	private boolean is_admin;			//是否圈主 true 是圈主|false 不是圈主
	private long member_id;				//成员id
	private String bg_image;				//背景图像
	private String interest_logo;     //圈子logo
	private String interest_name;      //圈子名称
	private int type;
	
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public String getInterest_name() {
		return interest_name;
	}
	
	public void setInterest_name(String interest_name) {
		this.interest_name = interest_name;
	}
	
	public String getInterest_logo() {
		return interest_logo;
	}
	
	public void setInterest_logo(String interest_logo) {
		this.interest_logo = interest_logo;
	}
	
	public long getMember_id() {
		return member_id;
	}
	public void setMember_id(long member_id) {
		this.member_id = member_id;
	}
	public String getBg_image() {
		return bg_image;
	}
	public void setBg_image(String bg_image) {
		this.bg_image = bg_image;
	}
	public int getIs_private() {
		return is_private;
	}
	public void setIs_private(int is_private) {
		this.is_private = is_private;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public long getMember_count() {
		return member_count;
	}
	public void setMember_count(long member_count) {
		this.member_count = member_count;
	}
	public long getNeed_audit_count() {
		return need_audit_count;
	}
	public void setNeed_audit_count(long need_audit_count) {
		this.need_audit_count = need_audit_count;
	}
	public boolean isIs_admin() {
		return is_admin;
	}
	public void setIs_admin(boolean is_admin) {
		this.is_admin = is_admin;
	}
}
