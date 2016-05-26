package com.zhongsou.souyue.module;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 帖子头接口对象
 * @date 2016/04/05
 */
@SuppressWarnings("serial")
public class CWidgetHead extends ResponseObject implements IWidgetData {
    //    “userRole”：//1:圈主|主题管理员 2:成员（已订阅）,3:非成员
//            “isTop”:// 是否置顶 0:非 1：置顶
//            “isGood”//是否加精  0：非 1：加精
//            “isAllowEdit” //是否可编辑 0:非 1：允许
//            “authorUserId” //帖子的用户ID（作者）

    //数据结构
//    WidgetHead: {
//                userRole: 0,
//                isTop: 0,
//                isGood: 1,
//                isAllowEdit: 0,
//                authorUserId: 214,
//                isBantalk: 0,
//                nickName: "301昵称uu",
//                portraitImage: "http://souyue-image.b0.upaiyun.com/user/0004/25528849.jpg",
//                isPrivate: 0
//    },
    private int userRole;
    private int isTop;
    private int isGood;
    private int isAllowEdit;
    private String authorUserId;
    private long subscribeId;
    private String nickName;
    private int isBantalk;
    private String interestType;
    private String portraitImage;   //圈成员里面的头像
    private int isPrivate;

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPortraitImage() {
        return portraitImage;
    }

    public void setPortraitImage(String portraitImage) {
        this.portraitImage = portraitImage;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setIsBantalk(int isBantalk) {
        this.isBantalk = isBantalk;
    }

    public int getIsBantalk() {
        return isBantalk;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public long getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(long subscribeId) {
        this.subscribeId = subscribeId;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getIsGood() {
        return isGood;
    }

    public void setIsGood(int isGood) {
        this.isGood = isGood;
    }

    public int getIsAllowEdit() {
        return isAllowEdit;
    }

    public void setIsAllowEdit(int isAllowEdit) {
        this.isAllowEdit = isAllowEdit;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }
}
