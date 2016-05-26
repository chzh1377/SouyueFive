package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;

/** 
 * @author : zoulu
 * 2014年7月18日
 * 上午11:57:38 
 * 类说明 :私密圈申请记录
 */
public class CircleCheckRecord extends ResponseObject{

	private static final long serialVersionUID = 1L;
	
	private long user_id;
	private int apply_id;
	private String apply_content;
	private String applicant_nickname;
	private int audit_status;
	private String create_time;
	private int interest_id;
	private String interest_name;
	private String interest_logo;
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public int getApply_id() {
		return apply_id;
	}
	public void setApply_id(int apply_id) {
		this.apply_id = apply_id;
	}
	public String getApply_content() {
		return apply_content;
	}
	public void setApply_content(String apply_content) {
		this.apply_content = apply_content;
	}
	public String getApplicant_nickname() {
		return applicant_nickname;
	}
	public void setApplicant_nickname(String applicant_nickname) {
		this.applicant_nickname = applicant_nickname;
	}
	public int getAudit_status() {
		return audit_status;
	}
	public void setAudit_status(int audit_status) {
		this.audit_status = audit_status;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getInterest_id() {
		return interest_id;
	}
	public void setInterest_id(int interest_id) {
		this.interest_id = interest_id;
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
}
