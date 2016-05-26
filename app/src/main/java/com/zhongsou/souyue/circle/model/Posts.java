package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc: 帖子详情
 * User: tiansj
 * DateTime: 14-4-18 下午3:13
 */
public class Posts implements Serializable, DontObfuscateInterface {

    private long blog_id;
    private long user_id;
    private long mblog_id;
    private String title;
    private String content;
    private String nickname;
    private String image_url; // 头像
    private List<String> images;
    private String voice;
    private String create_time;
    private String update_time;
    private String floor_num;	//	楼层
    private int is_prime; //加精
    private int top_status;//置顶
    private List<Reply> replyList;
    private ArrayList<CircleMemberItem> at_users;
    private int text_type;           // 0-普通文本 1-富文本
    private String url;
    private String good_num; // 点赞数
    private boolean has_praised;
    private int is_mblog;
    private String sign_id;
    private int posting_state; //是不是匿名发帖  0：正常发帖，1：匿名发帖



    //分享新增字段
	private String keyword;
	private String srpId;
    private String new_srpid;


    public int getPosting_state() {
        return posting_state;
    }

    public void setPosting_state(int posting_state) {
        this.posting_state = posting_state;
    }

    public String getSign_id() {
        return sign_id;
    }

    public void setSign_id(String sign_id) {
        this.sign_id = sign_id;
    }

    public void setIs_mblog(int is_mblog) {
		this.is_mblog = is_mblog;
	}
    
    public int getIs_mblog() {
		return is_mblog;
	}
    
    public boolean isHas_praised() {
		return has_praised;
	}

	public void setHas_praised(boolean has_praised) {
		this.has_praised = has_praised;
	}

	public String getGood_num() {
		return good_num;
	}

	public void setGood_num(String good_num) {
		this.good_num = good_num;
	}

	public ArrayList<CircleMemberItem> getSelMembers() {
		return at_users;
	}
    
    public void setSelMembers(ArrayList<CircleMemberItem> selMembers) {
		this.at_users = selMembers;
	}

    public long getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(long blog_id) {
        this.blog_id = blog_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getMblog_id() {
        return mblog_id;
    }

    public void setMblog_id(long mblog_id) {
        this.mblog_id = mblog_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getFloor_num() {
        return floor_num;
    }

    public void setFloor_num(String floor_num) {
        this.floor_num = floor_num;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public int getText_type() {
        return text_type;
    }

    public void setText_type(int text_type) {
        this.text_type = text_type;
    }

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

    public String getNew_srpid() {
        return new_srpid;
    }

    public void setNew_srpid(String new_srpid) {
        this.new_srpid = new_srpid;
    }
}
