package com.tuita.sdk.im.db.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zoulu
 * on 2014/11/18
 * Description:@好友使用的bean
 */
public class AtFriend implements Serializable,DontObfuscateInterface {
    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> users) {
        this.users = users;
    }

    private String c ;
    private List<UserBean> users;
}
