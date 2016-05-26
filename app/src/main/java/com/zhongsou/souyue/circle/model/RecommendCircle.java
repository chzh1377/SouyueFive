package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by wlong on 14-4-21.
 *
 * 首页推荐兴趣圈
 *
 */
public class RecommendCircle  implements  DontObfuscateInterface {

    private String interest_id;
    private String interest_name;
    private String interest_logo;

    public String getInterest_id() {
        return interest_id;
    }

    public String getInterest_logo() {
        return interest_logo;
    }

    public String getInterest_name() {
        return interest_name;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public void setInterest_logo(String interest_logo) {
        this.interest_logo = interest_logo;
    }

    public void setInterest_name(String interest_name) {
        this.interest_name = interest_name;
    }
}
