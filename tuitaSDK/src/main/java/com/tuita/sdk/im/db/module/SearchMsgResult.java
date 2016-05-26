package com.tuita.sdk.im.db.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import java.io.Serializable;
import java.util.List;

public class SearchMsgResult implements Serializable,DontObfuscateInterface{

    private long chat_id;                       //聊天id 如果是群代表群ID 联系人即联系人ID
    private int chat_type;
    private int layoutType;                     //0:header  1:item  2:bottom
    private String groupName;                   //分组类型名称
    private int groupType;                     //0:联系人  1:群聊  2:聊天记录
    private String mark;                        //组标示（用于进入更多详情）
    private boolean hasMore;                    //是否大於一條記錄 （只有一條點擊后直接進入聊天頁定位，大於一條進入詳細列表頁）
    private String userImage;
    private String title;
    private String content;
    private long time;
    private int msgId;                          //具體消息的ID 為了定位消息用
    private int historyType;                    //歷史記錄類型（是群聊歷史記錄還是私聊歷史記錄）0:联系人  1:群聊

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHistoryType() {
        return historyType;
    }

    public void setHistoryType(int historyType) {
        this.historyType = historyType;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }


    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

//    public SearchMsgResultItem getItem() {
//        return item;
//    }
//
//    public void setItem(SearchMsgResultItem item) {
//        this.item = item;
//    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }


    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public int getChat_type() {
        return chat_type;
    }

    public void setChat_type(int chat_type) {
        this.chat_type = chat_type;
    }

}
