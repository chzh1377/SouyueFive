package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Ad extends ResponseObject {

	private String image = "";
	private String url = "";
	private String category = "";
	private String jumpTo = "";
	private String keyword = "";
	private String srpId = "";
	private String md5 = "";
	
	public String md5(){
		return md5;
	}

	public String keyword() {
		return keyword;
	}

	public String srpId() {
		return srpId;
	}

	public String image() {
		return image;
	}

	public void image_$eq(String image) {
		this.image = image;
	}

	public String url() {
		return url;
	}

	public void url_$eq(String url) {
		this.url = url;
	}

	public String category() {
		return category;
	}

	public void category_$eq(String category) {
		this.category = category;
	}

	public String jumpTo() {
		return jumpTo;
	}

	public void jumpTo_$eq(String jumpTo) {
		this.jumpTo = jumpTo;
	}

	public boolean isJumtToSlot() {
		return "slot".equals(this.jumpTo);
	}

	public boolean isJumtToSrp() {
		return "srp".equals(this.jumpTo);
	}

}
