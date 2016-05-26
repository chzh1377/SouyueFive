package com.zhongsou.souyue.module;
/** 
 * @author : zoulu
 * 2014年7月1日
 * 上午11:59:11 
 * 类说明 :注册友宝赠送返回参数
 */
public class Promotions extends ResponseObject{
	private static final long serialVersionUID = 1L;
	private String endedDate;
	private String startDate;
	private int pID;
	private String name;
	private String productImage;
	private String shortName;
	private String productName;
	private int productID;
	
	public String getEndedDate() {
		return endedDate;
	}
	public void setEndedDate(String endedDate) {
		this.endedDate = endedDate;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public int getpID() {
		return pID;
	}
	public void setpID(int pID) {
		this.pID = pID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getProductID() {
		return productID;
	}
	public void setProductID(int productID) {
		this.productID = productID;
	}
}
