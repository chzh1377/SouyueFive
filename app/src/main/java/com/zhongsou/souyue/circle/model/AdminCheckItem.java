package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class AdminCheckItem  implements Serializable, DontObfuscateInterface{
	private String applicant_pic;   //头像
	private String applicant_nickname;  //用户名
	private int audit_status;   //审核状态
	private String apply_content;  //留言
	private long interest_id;  //圈子id
	private long applicant_id;  //申请人user_id 
	private long apply_id;
	private String inviter_nickname;		//邀请人
	private String sy_nickname;
	
	public void setSy_nickname(String sy_nickname) {
		this.sy_nickname = sy_nickname;
	}
	
	public String getSy_nickname() {
		return sy_nickname;
	}
	
	public void setInviter_nickname(String inviter_nickname) {
		this.inviter_nickname = inviter_nickname;
	}
	
	public String getInviter_nickname() {
		return inviter_nickname;
	}
	
	public long getApply_id() {
		return apply_id;
	}
	
	public void setApply_id(long apply_id) {
		this.apply_id = apply_id;
	}
	 
	public void setAudit_status(int audit_status) {
		this.audit_status = audit_status;
	}
	
	public int getAudit_status() {
		return audit_status;
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
	
	public void setApply_content(String apply_content) {
		this.apply_content = apply_content;
	}
	
	public String getApply_content() {
		return apply_content;
	}
	
	public void setInterest_id(long interest_id) {
		this.interest_id = interest_id;
	}
	
	public long getInterest_id() {
		return interest_id;
	}
	
	public void setApplicant_id(long applicant_id) {
		this.applicant_id = applicant_id;
	}
	
	public long getApplicant_id() {
		return applicant_id;
	}

}
