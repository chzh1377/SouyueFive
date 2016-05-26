package com.zhongsou.souyue.circle.model;


import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;

/**
 * 个人中心信息
 * Created by bob zhou on 14-11-6.
 */

public class PersonalCenterInfo implements Serializable, DontObfuscateInterface {


    private int is_private;     //是否开启隐私保护  0 不开启， 1 开启

    private Viewer person;      //被查看人的信息

    private int srp_num;        //srp数量

    private List<SRP> srp;      //srp列表

    private int interest_num;   //圈子数量

    private List<Interest> interest;    //圈子列表

    public static final int IS_PRIVATE_YES = 1;

    public static final int IS_PRIVATE_NO = 0;

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public int getSrp_num() {
        return srp_num;
    }

    public void setSrp_num(int srp_num) {
        this.srp_num = srp_num;
    }

    public int getInterest_num() {
        return interest_num;
    }

    public void setInterest_num(int interest_num) {
        this.interest_num = interest_num;
    }

    public Viewer getPerson() {
        return person;
    }

    public void setPerson(Viewer person) {
        this.person = person;
    }

    public List<SRP> getSrp() {
        return srp;
    }

    public void setSrp(List<SRP> srp) {
        this.srp = srp;
    }

    public List<Interest> getInterest() {
        return interest;
    }

    public void setInterest(List<Interest> interest) {
        this.interest = interest;
    }

    //被查看人的个人信息
    public class Viewer implements Serializable, DontObfuscateInterface {

        private String bigImage;
        private String bg_img;          //背景图
        private String head_img;        //头像
        private String nickname;        //昵称
        private String souyue_id;       //搜悦id
        private int mood_id;            //表情id,暂时ID为1~8
        private String signature;       //签名
        private int sex;                //1:男 2:女

        public String getBigImage() {
            return bigImage;
        }

        public void setBigImage(String bigImage) {
            this.bigImage = bigImage;
        }

        public String getBg_img() {
            return bg_img;
        }

        public void setBg_img(String bg_img) {
            this.bg_img = bg_img;
        }

        public String getHead_img() {
            return head_img;
        }

        public void setHead_img(String head_img) {
            this.head_img = head_img;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSouyue_id() {
            return souyue_id;
        }

        public void setSouyue_id(String souyue_id) {
            this.souyue_id = souyue_id;
        }

        public int getMood_id() {
            return mood_id;
        }

        public void setMood_id(int mood_id) {
            this.mood_id = mood_id;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }
    }


    //srp信息
    public class SRP implements Serializable, DontObfuscateInterface {
        private String subscribeId;

        private String keyword;

        private String srpId;

        private String image;

        public String getSubscribeId() {
            return subscribeId;
        }

        public void setSubscribeId(String subscribeId) {
            this.subscribeId = subscribeId;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getSrpId() {
            return srpId;
        }

        public void setSrpId(String srpId) {
            this.srpId = srpId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }


    //圈子信息
    public class Interest implements Serializable, DontObfuscateInterface {

        private String id;

        private String name;

        private String image;

        private long interestId;

        private String keyword;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public long getInterestId() {
            return interestId;
        }

        public void setInterestId(long interestId) {
            this.interestId = interestId;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }


}
