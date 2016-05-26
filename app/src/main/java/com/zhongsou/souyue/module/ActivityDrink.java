package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年7月1日
 * 下午12:59:06 
 * 类说明 :注册友宝赠送返回参数 包含Promotions
 */
public class ActivityDrink extends ResponseObject{

	private static final long serialVersionUID = 1L;
	
	private float rate = 0.0f;
	private int singleLimit = 0;
	private boolean checkExpend = false;
	private int todayExpend = 0;
	private List<Promotions> promotions = new ArrayList<Promotions>();
	private int totalLimit = -1;
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public int getSingleLimit() {
		return singleLimit;
	}
	public void setSingleLimit(int singleLimit) {
		this.singleLimit = singleLimit;
	}
	public boolean isCheckExpend() {
		return checkExpend;
	}
	public void setCheckExpend(boolean checkExpend) {
		this.checkExpend = checkExpend;
	}
	public int getTodayExpend() {
		return todayExpend;
	}
	public void setTodayExpend(int todayExpend) {
		this.todayExpend = todayExpend;
	}
	public int getTotalLimit() {
		return totalLimit;
	}
	public void setTotalLimit(int totalLimit) {
		this.totalLimit = totalLimit;
	}
	public List<Promotions> getPromotions() {
		return promotions;
	}
	public void setPromotions(List<Promotions> promotions) {
		this.promotions = promotions;
	}

}
