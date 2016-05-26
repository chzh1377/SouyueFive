package com.zhongsou.souyue.module;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 模板请求  底部评论数接口
 * @date 2016/04/05
 */
public class TemplateCommentsInfo {
    //数据格式
//    upCount: 49,
//    downCount: 14,
//    commentsCount: 0,
//    hasFavorited: false,
//    hasDown: false,
//    hasUp: false,
//    zsbCount: 0

    private int upCount;
    private int downCount;
    private int commentsCount;
    private boolean hasFavorited;
    private boolean hasDown;
    private boolean hasUp;
    private int zsbCount;

    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isHasFavorited() {
        return hasFavorited;
    }

    public void setHasFavorited(boolean hasFavorited) {
        this.hasFavorited = hasFavorited;
    }

    public boolean isHasDown() {
        return hasDown;
    }

    public void setHasDown(boolean hasDown) {
        this.hasDown = hasDown;
    }

    public boolean isHasUp() {
        return hasUp;
    }

    public void setHasUp(boolean hasUp) {
        this.hasUp = hasUp;
    }

    public int getZsbCount() {
        return zsbCount;
    }

    public void setZsbCount(int zsbCount) {
        this.zsbCount = zsbCount;
    }
}
