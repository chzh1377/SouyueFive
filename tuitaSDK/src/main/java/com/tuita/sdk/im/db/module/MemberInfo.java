package com.tuita.sdk.im.db.module;

import java.io.Serializable;
import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by zoulu
 * on 14-9-4
 * Description:
 */
public class MemberInfo implements Serializable,DontObfuscateInterface{
    private long uid;
    private String nick;
    private String avatar;
    private boolean isFriend;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }
}
