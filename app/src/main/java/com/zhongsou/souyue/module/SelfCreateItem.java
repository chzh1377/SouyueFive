package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
/**
 * 原创数据封装
 * @author Administrator
 *
 */
public class SelfCreateItem extends ResponseObject {
	/* 我的原创，分享数，评论数，顶数 */
	private String upCount = "0";// 顶数
	private String shareCount = "0";// 分享数
	private String commentCount = "0";// 评论数
	/* 原创大赛start */
	private int wrank = 0;// 周排行
	private int mrank = 0;// 月排行
	private int score = 0; // 积分
	/* 原创大赛end */
	private String url = "";
	private String _id = ""; // 本地数据库id
	private String token = "";
	private String id = ""; // 服务器数据库id
	private String keyword = ""; // "关键词" zhongguo,beijing,renmien,
	private String srpId = ""; // "关键词对应srpid", sfsfsff,sfsdfsd,fsf,fs
	private String kid = ""; // ?
	private String md5 = ""; // 微件对应md5"
	private String column_name = ""; // "栏目名",
	private long column_type = 0; // 微件类型
	private String title = ""; // 原创标题",
	private String content = ""; // "原创内容",
	private String conpic = ""; // 内容图片(若有多个图片空格隔开共同存储)
	private String pubtime = ""; // 发布时间
	private int status = 0; // 审核状态
	private int isHtml;
	private List<String> conpics = new ArrayList<String>(); // 图片容器

	public SelfCreateItem() {
	}

	public SelfCreateItem(String _id, String ids, String keyword, String srpId,
			String md5, Long column_type, String column_name, String title,
			String content, String conpic, String pubtime, Integer status) {
		this._id = _id;
		this.id = ids;
		this.keyword = keyword;
		this.srpId = srpId;
		this.md5 = md5;
		this.column_type = column_type;
		this.column_name = column_name;
		this.title = title;
		this.content = content;
		this.conpic = conpic;
		this.pubtime = pubtime;
		this.status = status;
	}

	public int wrank() {
		return wrank;
	}

	public void wrank_$eq(int wrank) {
		this.wrank = wrank;
	}

	public int mrank() {
		return mrank;
	}

	public void mrank_$eq(int mrank) {
		this.mrank = mrank;
	}

	public int score() {
		return score;
	}

	public void score_$eq(int score) {
		this.score = score;
	}

	public String url() {
		return url;
	}

	public void url_$eq(String url) {
		this.url = url;
	}

	public String _id() {
		return _id;
	}

	public void _id_$eq(String _id) {
		this._id = _id;
	}

	public String token() {
		return token;
	}

	public void token_$eq(String token) {
		this.token = token;
	}

	public String id() {
		return id;
	}

	public void id_$eq(String id) {
		this.id = id;
	}

	public String keyword() {
		return keyword;
	}

	public void keyword_$eq(String keyword) {
		this.keyword = keyword;
	}

	public String srpId() {
		return srpId;
	}

	public void srpId_$eq(String srpId) {
		this.srpId = srpId;
	}

	public String kid() {
		return kid;
	}

	public void kid_$eq(String kid) {
		this.kid = kid;
	}

	public String md5() {
		return md5;
	}

	public void md5_$eq(String md5) {
		this.md5 = md5;
	}

	public String column_name() {
		return column_name;
	}

	public void column_name_$eq(String column_name) {
		this.column_name = column_name;
	}

	public long column_type() {
		return column_type;
	}

	public void column_type_$eq(long column_type) {
		this.column_type = column_type;
	}

	public String title() {
		return title;
	}

	public void title_$eq(String title) {
		this.title = title;
	}

	public String content() {
		return content;
	}

	public void content_$eq(String content) {
		this.content = content;
	}

	public String conpic() {
		return conpic;
	}

	public void conpic_$eq(String conpic) {
		this.conpic = conpic;
	}

	public String pubtime() {
		return pubtime;
	}

	public void pubtime_$eq(String pubtime) {
		this.pubtime = pubtime;
	}

	public int status() {
		return status;
	}

	public void status_$eq(int status) {
		this.status = status;
	}

	public List<String> conpics() {
		return conpics;
	}

	public void conpics_$eq(List<String> conpics) {
		this.conpics = conpics;
	}

	public boolean isHtml() {
		return this.isHtml == 1;
	}

	public void commentCount_$eq(String commentCount) {
		this.commentCount = commentCount;
	}

	public String commentCount() {
		return commentCount;
	}

	public void upCount_$eq(String upCount) {
		this.upCount = upCount;
	}

	public String upCount() {
		return upCount;
	}

	public void shareCount_$eq(String shareCount) {
		this.shareCount = shareCount;
	}

	public String shareCount() {
		return shareCount;
	}

}
