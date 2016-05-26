package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * User: FlameLi
 * Date: 2014/8/1
 * Time: 15:17
 */
public class CircleMemberInfo  implements  DontObfuscateInterface {

    private long user_id;
    private String nickname;
    private String image;
    private String signature;
    private long interest_id;
    private int role;
    private int is_private; //是否开启隐私保护 1-开启，0-不开启
    private int is_bantalk; //是否被禁言
    private boolean is_admin;   //是否是圈主

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public int getIs_bantalk() {
        return is_bantalk;
    }

    public void setIs_bantalk(int is_bantalk) {
        this.is_bantalk = is_bantalk;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }
}
