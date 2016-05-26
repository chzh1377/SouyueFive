package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class QQUserInfo extends ResponseObject {

	private String figureurl_qq_1;
	private String id;
	private String nickname;
	private String third_type;

	public String getFigureurl_qq_1() {
		return figureurl_qq_1;
	}

	public void setFigureurl_qq_1(String figureurl_qq_1) {
		this.figureurl_qq_1 = figureurl_qq_1;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getThird_type() {
		return third_type;
	}

	public void setThird_type(String third_type) {
		this.third_type = third_type;
	}

}
