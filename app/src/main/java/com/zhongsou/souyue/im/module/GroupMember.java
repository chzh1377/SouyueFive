package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by zoulu
 * on 14-9-3
 */
public class GroupMember implements Serializable,DontObfuscateInterface{
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    private String userImage;
}
