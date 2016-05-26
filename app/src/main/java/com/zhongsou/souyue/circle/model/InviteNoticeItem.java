package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class InviteNoticeItem  implements Serializable, DontObfuscateInterface{
	private String applicant_pic;   //头像
	private String applicant_nickname;  //用户名
	private String beizhu = "";  //备注
	private long user_id ;
	
	
	public long getUser_id() {
		return user_id;
	}
	
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	
	public void setApplicant_pic(String applicant_pic) {
		this.applicant_pic = applicant_pic;
	}
	
	public String getApplicant_pic() {
		return applicant_pic;
	}
	
	public void setApplicant_nickname(String applicant_nickname) {
		this.applicant_nickname = applicant_nickname;
	}
	
	public String getApplicant_nickname() {
		return applicant_nickname;
	}
	
	public String getBeizhu() {
		return beizhu;
	}
	
	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

}
