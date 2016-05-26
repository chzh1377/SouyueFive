package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.ResponseObject;

public class ImAskInterest extends ResponseObject implements DontObfuscateInterface {
    private static final long serialVersionUID = 1L;
    private String interest_logo;
    private String interest_name;
    private String interest_id;
    public String getInterest_logo() {
        return interest_logo;
    }
    public void setInterest_logo(String interest_logo) {
        this.interest_logo = interest_logo;
    }
    public String getInterest_name() {
        return interest_name;
    }
    public void setInterest_name(String interest_name) {
        this.interest_name = interest_name;
    }
    public String getInterest_id() {
        return interest_id;
    }
    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }
    

}
