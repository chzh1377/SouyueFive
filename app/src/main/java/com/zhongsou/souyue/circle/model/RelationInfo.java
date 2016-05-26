package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;

/** 
 * @author : zoulu
 * 2014年7月17日
 * 下午2:10:14 
 * 类说明 :用户与某个私密圈关系
 */
public class RelationInfo extends ResponseObject{

	private static final long serialVersionUID = 1L;
	
	private long apply_id;
	private String applicant_nickname;//申请昵称
	private String apply_content;//申请留言
	private String refuse_content;//拒绝留言
	private int refuse_type;//拒绝类型
	private long create_time;
	
	public long getApply_id() {
		return apply_id;
	}
	public void setApply_id(long apply_id) {
		this.apply_id = apply_id;
	}
	public String getApplicant_nickname() {
		return applicant_nickname;
	}
	public void setApplicant_nickname(String applicant_nickname) {
		this.applicant_nickname = applicant_nickname;
	}
	public String getApply_content() {
		return apply_content;
	}
	public void setApply_content(String apply_content) {
		this.apply_content = apply_content;
	}
	public String getRefuse_content() {
		return refuse_content;
	}
	public void setRefuse_content(String refuse_content) {
		this.refuse_content = refuse_content;
	}
	public int getRefuse_type() {
		return refuse_type;
	}
	public void setRefuse_type(int refuse_type) {
		this.refuse_type = refuse_type;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	
}
