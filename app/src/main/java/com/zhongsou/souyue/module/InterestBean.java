package com.zhongsou.souyue.module;
/** 
 * @author : zoulu
 * 2014年5月10日
 * 下午4:47:15 
 * 类说明 :兴趣圈bean
 */
public class InterestBean extends AGridDynamic {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String url;
	private String image;
	private int type;
    private String srpId;
    private String srp_word;

    public String getSrp_word() {
        return srp_word;
    }

    public void setSrp_word(String srp_word) {
        this.srp_word = srp_word;
    }


    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
