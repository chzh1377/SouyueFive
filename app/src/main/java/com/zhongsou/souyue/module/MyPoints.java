package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author "jianxing.fan@iftek.cn" 我的积分
 */
@SuppressWarnings("serial")
public class MyPoints extends ResponseObject {
    private String username = "";
    private String userlevel = "";
    private String userexperience = "";
    private List<JiFen> score = new ArrayList<JiFen>();
    private String userleveltitle = "";
    private String state = "";
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserlevel() {
        return userlevel;
    }
    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }
    public String getUserexperience() {
        return userexperience;
    }
    public void setUserexperience(String userexperience) {
        this.userexperience = userexperience;
    }
    public List<JiFen> getScore() {
        return score;
    }
    public void setScore(List<JiFen> score) {
        this.score = score;
    }
	public String getUserleveltitle() {
		return userleveltitle;
	}
	public void setUserleveltitle(String userleveltitle) {
		this.userleveltitle = userleveltitle;
	}

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
