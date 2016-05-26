package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;

/** 
 * @author : zoulu
 * 2014年7月14日
 * 上午11:26:27 
 * 类说明 : 圈名片信息
 */
public class CircleCardInfo extends ResponseObject{

	private static final long serialVersionUID = 1L;
	
	private long interest_id;
	private String interest_logo;
	private String interest_name;
	private int interest_type;
	private String create_time;
	private int usr_count;
	private int mblog_count;
	private String master_nickname;
	private String interest_desc;
	private String new_srpid;
	private String srp_word;
	
	public String getNew_srpid() {
		return new_srpid;
	}
	
	public void setSrp_word(String srp_word) {
		this.srp_word = srp_word;
	}
	
	public String getSrp_word() {
		return srp_word;
	}
	
	public void setNew_srpid(String new_srpid) {
		this.new_srpid = new_srpid;
	}
	
	public long getInterest_id() {
		return interest_id;
	}
	public void setInterest_id(long interest_id) {
		this.interest_id = interest_id;
	}
	public String getInterest_logo() {
		return interest_logo;
	}
	public void setInterest_logo(String interest_logo) {
		this.interest_logo = interest_logo;
	}
	public String getInterest_name() {
		return interest_name;
	}
	public void setInterest_name(String interest_name) {
		this.interest_name = interest_name;
	}
	public int getInterest_type() {
		return interest_type;
	}
	public void setInterest_type(int interest_type) {
		this.interest_type = interest_type;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getUsr_count() {
		return usr_count;
	}
	public void setUsr_count(int usr_count) {
		this.usr_count = usr_count;
	}
	public int getMblog_count() {
		return mblog_count;
	}
	public void setMblog_count(int mblog_count) {
		this.mblog_count = mblog_count;
	}
	public String getMaster_nickname() {
		return master_nickname;
	}
	public void setMaster_nickname(String master_nickname) {
		this.master_nickname = master_nickname;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getInterest_desc() {
		return interest_desc;
	}
	public void setInterest_desc(String interest_desc) {
		this.interest_desc = interest_desc;
	}

}
