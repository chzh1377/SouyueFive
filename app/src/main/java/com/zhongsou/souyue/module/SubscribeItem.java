package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class SubscribeItem extends AGridDynamic {

	/**
	 * 原rss的id
	 */
	private long id = 0;
	/**
	 * 用户订阅后的id
	 */
	private long subscribeId = 0;
	private long groupId = 0;
	private String keyword = "";
	private String category = "";
	private String url = "";
	private boolean hasSubscribe = false;
	private String image = "";
	private String srpId = "";
	private String sid;
	private int subscribeType;
	private String entId = "";
	private int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getEntId() {
		return entId;
	}

	public void setEntId(String entId) {
		this.entId = entId;
	}

	public int getSubscribeType() {
		return subscribeType;
	}

	public void setSubscribeType(int subscribeType) {
		this.subscribeType = subscribeType;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public SubscribeItem(String keyword, String srpId) {
		this.keyword = keyword;
		this.srpId = srpId;
	}

	public SubscribeItem(long groupId, String sid) {
		this.groupId = groupId;
		this.sid = sid;
	}

	public void hasSubscribe_$eq(boolean sub) {
		hasSubscribe = sub;
	}

	public long id() {
		return id;
	}

	public void id_$eq(long id) {
		this.id = id;
	}

	public long subscribeId() {
		return subscribeId;
	}

	public void subscribeId_$eq(long subscribeId) {
		this.subscribeId = subscribeId;
	}

	public long groupId() {
		return groupId;
	}

	public void groupId_$eq(long groupId) {
		this.groupId = groupId;
	}

	public String keyword() {
		return keyword;
	}

	public void keyword_$eq(String keyword) {
		this.keyword = keyword;
	}

	public String category() {
		return category;
	}

	public void category_$eq(String category) {
		this.category = category;
	}

	public String url() {
		return url;
	}

	public void url_$eq(String url) {
		this.url = url;
	}

	public String image() {
		return image;
	}

	public void image_$eq(String image) {
		this.image = image;
	}

	public String srpId() {
		return srpId;
	}

	public void srpId_$eq(String srpId) {
		this.srpId = srpId;
	}

	public boolean hasSubscribe() {
		return hasSubscribe;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscribeItem other = (SubscribeItem) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
