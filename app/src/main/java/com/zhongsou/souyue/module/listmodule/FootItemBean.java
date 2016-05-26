package com.zhongsou.souyue.module.listmodule;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.DiskLikeBean;

import java.util.List;
import java.util.Map;

/**
 * 底部展示样式数据
 * Created by lvqiang on 15/12/23.
 */
public class FootItemBean implements DontObfuscateInterface {
    /** 底部展示类型1 默认类型 */
    public static final int FOOT_VIEW_TYPE_DEFAULT = 1;
    /** 底部展示类型2  */
    public static final int FOOT_VIEW_TYPE_2 = 2;
    /** 底部展示类型3  */
    public static final int FOOT_VIEW_TYPE_3 = 3;
    /** 底部展示类型4  */
    public static final int FOOT_VIEW_TYPE_4 = 4;
    /** 底部展示类型5 */
    public static final int FOOT_VIEW_TYPE_5 = 5;

    private String author;//作者
    private int footType;
    private String source;
    private String channelName;
    private Map<String,String> tag;
    private int channelInvokeType;
    private long ctime;
    private int showMenu;
    private int showShare;
    private int showFavorator;
    private int isFavorator;
    private int isUp;
    private int isDown;
    private int upCount;
    private int downCount;
    private int commentCount;
    private int deleteId;
    private int isSubscribe; // 是否订阅， 0:未订阅 1：订阅
    private String shareUrl;//分享短链

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * tag : 重复内容
     * log : 1
     */

    private List<DiskLikeBean> disLike;

    public void setFootType(int footType) {
        this.footType = footType;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setTag(Map<String,String> tag) {
        this.tag = tag;
    }

    public void setChannelInvokeType(int channelInvokeType) {
        this.channelInvokeType = channelInvokeType;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public void setShowMenu(int showMenu) {
        this.showMenu = showMenu;
    }

    public void setShowShare(int showShare) {
        this.showShare = showShare;
    }

    public void setShowFavorator(int showFavorator) {
        this.showFavorator = showFavorator;
    }

    public void setIsFavorator(int isFavorator) {
        this.isFavorator = isFavorator;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setDisLike(List<DiskLikeBean> disLike) {
        this.disLike = disLike;
    }

    public int getFootType() {
        return footType;
    }

    public String getSource() {
        return source;
    }

    public String getChannelName() {
        return channelName;
    }

    public Map<String,String> getTag() {
        return tag;
    }

    public int getChannelInvokeType() {
        return channelInvokeType;
    }

    public long getCtime() {
        return ctime;
    }

    public int getShowMenu() {
        return showMenu;
    }

    public int getShowShare() {
        return showShare;
    }

    public int getShowFavorator() {
        return showFavorator;
    }

    public int getIsFavorator() {
        return isFavorator;
    }

    public int getUpCount() {
        return upCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public List<DiskLikeBean> getDisLike() {
        return disLike;
    }

    public int getIsUp() {
        return isUp;
    }

    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }

    public int getIsDown() {
        return isDown;
    }

    public void setIsDown(int isDown) {
        this.isDown = isDown;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(int deleteId) {
        this.deleteId = deleteId;
    }

    public int getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(int isSubscribe) {
        this.isSubscribe = isSubscribe;
    }
}
