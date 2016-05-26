package com.zhongsou.souyue.module;

import java.io.Serializable;

/**
 * Created by bob zhou on 14-11-24.
 */
public class ShortCutInfo implements Serializable{

    public static final String FROM_SHORTCUT = "shortcut";


    public static final String GO_TO_SRP = "srpmain";

    public static final String GO_TO_INTEREST = "interestmain";

    public static final String GO_TO_M_SEARCH = "msearchonly";//搜索-無頭無尾
    
    private String keyword;

    private String srpId;

    private String interest_name;

    private String interest_logo;

    private String goTo;
    
    private String url;

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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

    public String getGoTo() {
        return goTo;
    }

    public void setGoTo(String goTo) {
        this.goTo = goTo;
    }
}
