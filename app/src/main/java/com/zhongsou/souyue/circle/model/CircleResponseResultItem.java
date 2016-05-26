package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;
import com.zhongsou.souyue.net.HttpJsonResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输对象
 * 
 * @author chz
 * 
 */
public class CircleResponseResultItem extends ResponseObject {

	private long blog_id; // 帖子ID
	private long interest_id; // 圈子ID
	private String title; // 标题
	private String brief; // 概要
	
	private int is_prime; // 是否精华 0||1
	private int top_status; // 是否置顶 0||1
	private int top_day; // 置顶天数

	private long user_id; // 发帖人id
	private String nickname; // 发帖人昵称
	private String user_image; // 作者图像
	private String create_time; // 时间
	private String follow_num; // 跟帖数
	private String reply_num; // 回复数
	private String good_num; // 点赞数
	private boolean has_praised;	//true|false 是否点过赞
	private long sort_num;	//排序号

	private String srp_word; // 帖子主题
	private String srp_id; // srp_id
    private String new_srpid;
	private boolean isCollect;//是否收藏
	private List<String> images = new ArrayList<String>();// 图片地址
	private int postLayoutType = CircleResponseResult.POSTS_TYPE_NOPIC; // 默认布局类型

	private int type;

    private List<String> tag_id;    // 圈吧列表接口返回的标签ID，是个数组
    private String broadcast_tag_id; // 发帖后广播的标签ID，是单个
    private String broadcast_tag_name;// 发帖后广播标签名称
    private String sign_id = "";

   
	public CircleResponseResultItem() {

    }
	public CircleResponseResultItem(HttpJsonResponse response) {

	}

    public String getSign_id() {
        return sign_id;
    }

    public void setSign_id(String sign_id) {
        this.sign_id = sign_id;
    }

    public List<String> getTag_id() {
        return tag_id;
    }

    public void setTag_id(List<String> tag_id) {
        this.tag_id = tag_id;
    }

    public String getBroadcast_tag_id() {
        return broadcast_tag_id;
    }

    public void setBroadcast_tag_id(String broadcast_tag_id) {
        this.broadcast_tag_id = broadcast_tag_id;
    }

    public String getBroadcast_tag_name() {
        return broadcast_tag_name;
    }

    public void setBroadcast_tag_name(String broadcast_tag_name) {
        this.broadcast_tag_name = broadcast_tag_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	public long getSort_num() {
		return sort_num;
	}

	public void setSort_num(long sort_num) {
		this.sort_num = sort_num;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public int getIs_prime() {
		return is_prime;
	}

	public void setIs_prime(int is_prime) {
		this.is_prime = is_prime;
	}

	public int getTop_status() {
		return top_status;
	}

	public void setTop_status(int top_status) {
		this.top_status = top_status;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getFollow_num() {
		return follow_num;
	}

	public void setFollow_num(String follow_num) {
		this.follow_num = follow_num;
	}

	public String getReply_num() {
		return reply_num;
	}

	public void setReply_num(String reply_num) {
		this.reply_num = reply_num;
	}

	public String getSrp_word() {
		return srp_word;
	}

	public void setSrp_word(String srp_word) {
		this.srp_word = srp_word;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPostLayoutType() {
		return postLayoutType;
	}

	public void setPostLayoutType(int postLayoutType) {
		this.postLayoutType = postLayoutType;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getTitle() {
		return title;
	}

	public long getBlog_id() {
		return blog_id;
	}

	public void setBlog_id(long blog_id) {
		this.blog_id = blog_id;
	}

	public long getInterest_id() {
		return interest_id;
	}

	public void setInterest_id(long interest_id) {
		this.interest_id = interest_id;
	}

	public int getTop_day() {
		return top_day;
	}

	public void setTop_day(int top_day) {
		this.top_day = top_day;
	}

	public String getUser_image() {
		return user_image;
	}

	public void setUser_image(String user_image) {
		this.user_image = user_image;
	}

	public String getSrp_id() {
		return srp_id;
	}

	public void setSrp_id(String srp_id) {
		this.srp_id = srp_id;
	}

	public String getGood_num() {
		return good_num;
	}

	public void setGood_num(String good_num) {
		this.good_num = good_num;
	}
	public boolean isCollect() {
		return isCollect;
	}
	public void setCollect(boolean isCollect) {
		this.isCollect = isCollect;
	}
	
	 public boolean isHas_praised() {
			return has_praised;
		}
		public void setHas_praised(boolean has_praised) {
			this.has_praised = has_praised;
		}

    public String getNew_srpid() {
        return new_srpid;
    }

    public void setNew_srpid(String new_srpid) {
        this.new_srpid = new_srpid;
    }
}
