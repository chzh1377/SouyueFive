package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.ResponseObject;

public class CircleBlogReply extends ResponseObject implements  DontObfuscateInterface {
	public static int COMMENTTYPE_MINE = 1; // 我评论的
	public static int COMMENTTYPE_ME_TO_OTHER = 2; // 我回复他人的
	public static int COMMENTTYPE_OTHER_TO_ME = 3; // 他人回复我的

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

	private int commentType = COMMENTTYPE_MINE; // 回复类型，int值，1为我评论的，2我回复他人的，3为他人回复我的
	private SubBlog subBlog;
	private MainBlog mainBlog;
	public SubBlog getSubBlog() {
		return subBlog;
	}
	public void setSubBlog(SubBlog subBlog) {
		this.subBlog = subBlog;
	}
	public MainBlog getMainBlog() {
		return mainBlog;
	}
	public void setMainBlog(MainBlog mainBlog) {
		this.mainBlog = mainBlog;
	}
}
