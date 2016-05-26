package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class GroupKeywordItem extends ResponseObject {
	private String url = "";
	private String title = "";
    private String keyword = "";
    private String srpId = "";
    private boolean ischeck = false;
    
    public boolean ischeck() {
        return ischeck;
    }

    public void ischeck_$eq(boolean ischeck) {
        this.ischeck = ischeck;
    }
    
    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }
    
    public String title() {
    	return title;
    }
    
    public void title_$eq(String title) {
    	this.title = title;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((srpId == null) ? 0 : srpId.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		GroupKeywordItem other = (GroupKeywordItem) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (srpId == null) {
			if (other.srpId != null)
				return false;
		} else if (!srpId.equals(other.srpId))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
