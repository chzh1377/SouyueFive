package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by bob zhou on 15-1-14.
 * <p/>
 * 帖子详情页扩展信息model
 */
public class PostExtraInfo implements Serializable, DontObfuscateInterface {

    private String brief;

    private int role;

    private String nickname;

    private String image;

    private int is_bantalk;

    private String interest_name;

    private int is_prime;

    private int top_status;

    private int has_favorite;

    private int has_dogood;

    private int good_num;

    private int follow_num;

    private int interest_type;

    private String srp_id;

    private String srp_word;


    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIs_bantalk() {
        return is_bantalk;
    }

    public void setIs_bantalk(int is_bantalk) {
        this.is_bantalk = is_bantalk;
    }

    public String getInterest_name() {
        return interest_name;
    }

    public void setInterest_name(String interest_name) {
        this.interest_name = interest_name;
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

    public int getHas_favorite() {
        return has_favorite;
    }

    public void setHas_favorite(int has_favorite) {
        this.has_favorite = has_favorite;
    }

    public int getHas_dogood() {
        return has_dogood;
    }

    public void setHas_dogood(int has_dogood) {
        this.has_dogood = has_dogood;
    }

    public int getGood_num() {
        return good_num;
    }

    public void setGood_num(int good_num) {
        this.good_num = good_num;
    }

    public int getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(int follow_num) {
        this.follow_num = follow_num;
    }

    public int getInterest_type() {
        return interest_type;
    }

    public void setInterest_type(int interest_type) {
        this.interest_type = interest_type;
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
}
