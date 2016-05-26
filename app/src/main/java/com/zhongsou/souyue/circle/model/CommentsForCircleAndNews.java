package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;


public class CommentsForCircleAndNews implements Serializable, DontObfuscateInterface,Cloneable {

    private long blog_id;
    private long mblogid;  //此评论对应的主贴id
    private long comment_id;
    private long user_id;
    private int role; //当前评论作者的角色
    private String content;
    private List<String> images;
    private String voice;
    private int voicelength;
    private String create_time;
    private String update_time;
    private String nickname;
    private String image_url; // 头像
    private String good_num; // 点赞数
    private int type;   //代表评论的来源类型，新闻、帖子、rss
    private String srp_id;
    private String srp_word;
    private boolean has_praised;
    private List<Reply> replyList;
    private int is_anonymity; // 是否是匿名的帖子

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    private int is_private; // 是否处于隐私保护状态

    public int getIs_anonymity() {
        return is_anonymity;
    }

    public void setIs_anonymity(int is_anonymity) {
        this.is_anonymity = is_anonymity;
    }

    //增加字段is_anonymity(是否匿名，1是0否，默认为否)。
    //下面这些参数只有旧客户端用到

//    private String sign_id;
//    private long interest_id;
//    private int is_host;
//    private String  new_srpid;
//    private int  status;
//    private int  text_type;
    private int  is_current_comment;
    private int ishot;  //0是否非为热门评论，1代表是热门评论

    //5.0.5
    private String blog_title;
    private String blog_author;
    private String blog_createTime;

    public int getIshot() {
        return ishot;
    }

    public void setIshot(int ishot) {
        this.ishot = ishot;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getIs_current_comment() {
        return is_current_comment;
    }

    public void setIs_current_comment(int is_current_comment) {
        this.is_current_comment = is_current_comment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }

    public String getSrp_word() {
        return srp_word;
    }

    public void setSrp_word(String srp_word) {
        this.srp_word = srp_word;
    }

    public long getMblogid() {
        return mblogid;
    }

    public void setMblogid(long mblogid) {
        this.mblogid = mblogid;
    }

    public void setVoice_length(int voice_length) {
        this.voicelength = voice_length;
    }

    public int getVoice_length() {
        return voicelength;
    }

    public void setComment_id(long comment_id) {
        this.comment_id = comment_id;
    }

    public long getComment_id() {
        return comment_id;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
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

    public String getBlog_author() {
        return blog_author;
    }

    public void setBlog_author(String blog_author) {
        this.blog_author = blog_author;
    }

    public String getBlog_createTime() {
        return blog_createTime;
    }

    public void setBlog_createTime(String blog_createTime) {
        this.blog_createTime = blog_createTime;
    }
    public String getBlog_title() {
        return blog_title;
    }

    public void setBlog_title(String blog_title) {
        this.blog_title = blog_title;
    }

    public CommentsForCircleAndNews clone(){
        try{
        return (CommentsForCircleAndNews)super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }
}
