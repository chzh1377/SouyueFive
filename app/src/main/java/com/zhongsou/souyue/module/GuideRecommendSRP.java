package com.zhongsou.souyue.module;

public class GuideRecommendSRP extends ResponseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4046657966816132281L;
	private String keyword = "";
    private String srpId = "";
    private String pic = "";
    private String title = "";
    private String date = "";
    private int status = 0;//订阅状态，订阅：1，未订阅：0
	private int index = 0;//标示数据位置
    private String category = "";
	private String image = "";
    
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSrpId() {
		return srpId;
	}
	public void setSrpId(String srpId) {
		this.srpId = srpId;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}    

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
