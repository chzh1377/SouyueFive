package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;


public class SecurityInfo implements Serializable,DontObfuscateInterface {
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private String mobile;
    private String password;
    private String verifyNum;
    
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getVerifyNum() {
        return verifyNum;
    }
    public void setVerifyNum(String verifyNum) {
        this.verifyNum = verifyNum;
    }
}
