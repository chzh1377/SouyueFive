package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * 
 * @ClassName: InterestMemberInfo 
 * @Description: 分享到兴趣圈
 * @author gengsong@zhongsou.com
 * @date 2014年5月12日 下午5:11:12 
 * @version 3.8
 */
public class InterestMemberInfo  implements  DontObfuscateInterface {

	public long  interest_id;
	public String interest_name;
	public String interest_logo;
	public String srp_id;
	public int usr_count;
	public int sys_freetrial;
	public int group_sort;
	public int hpage_show;
	public int status;
	public int type;
	public String create_time;
	
    private String indexKey;    //索引字母
	
	public long getInterest_id() {
		return interest_id;
	}
	public void setInterest_id(long interest_id) {
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
	public String getSrp_id() {
		return srp_id;
	}
	public void setSrp_id(String srp_id) {
		this.srp_id = srp_id;
	}
	public int getUsr_count() {
		return usr_count;
	}
	public void setUsr_count(int usr_count) {
		this.usr_count = usr_count;
	}
	public int getSys_freetrial() {
		return sys_freetrial;
	}
	public void setSys_freetrial(int sys_freetrial) {
		this.sys_freetrial = sys_freetrial;
	}
	public int getGroup_sort() {
		return group_sort;
	}
	public void setGroup_sort(int group_sort) {
		this.group_sort = group_sort;
	}
	public int getHpage_show() {
		return hpage_show;
	}
	public void setHpage_show(int hpage_show) {
		this.hpage_show = hpage_show;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getIndexKey() {
		return indexKey;
	}
	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}
	
	
	
}
