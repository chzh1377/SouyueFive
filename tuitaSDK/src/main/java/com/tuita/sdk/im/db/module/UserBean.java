package com.tuita.sdk.im.db.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by zoulu
 * on 2014/11/18
 * Description:userid nickname
 */
public class UserBean implements Serializable,DontObfuscateInterface {
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

    private String nick;
    private long uid;
}
