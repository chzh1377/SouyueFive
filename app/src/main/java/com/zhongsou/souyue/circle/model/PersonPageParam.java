package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by tiansj on 14/11/5.
 */
public class PersonPageParam implements Serializable, DontObfuscateInterface{



    public static final int FROM_OTHER = 0;         //其他

    public static final int FROM_INTEREST = 1;      //来自圈子

    public static final int FROM_IM = 2;            //来自IM

    public static final int FROM_SINGLE_CHAT = 3;            //来自IM私聊

    private long comment_id; // 评论id.

    public long getComment_id() {
        return comment_id;
    }

    public void setComment_id(long comment_id) {
        this.comment_id = comment_id;
    }

    private long viewerUid;   // 被查看用户ID
    private String srp_id;      // 兴趣圈ID
    private int from;           // 来自，1:来自圈子，2:IM，0:其他
    private String circleName;  //圈子名，如果来自圈子，需要传圈子名
    private long interest_id;   //圈子id,如果来自圈子，需要传圈子id
    private String showName;    // 显示名称（搜悦昵称）
    private String subName1;    // 副标题名称 （IM备注名）
    private String subName2;    // 副标题名称 （IM群昵称）

    public String getSubName2() {
        return subName2;
    }

    public void setSubName2(String subName2) {
        this.subName2 = subName2;
    }

    public String getSubName1() {
        return subName1;
    }

    public void setSubName1(String subName1) {
        this.subName1 = subName1;
    }

    public long getViewerUid() {
        return viewerUid;
    }

    public void setViewerUid(long viewerUid) {
        this.viewerUid = viewerUid;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public long getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }
}
