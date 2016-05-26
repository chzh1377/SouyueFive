package com.zhongsou.souyue.im.search;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 搜索列表结果
 * 
 * @author zhaomeng
 * 
 */
public class ListResult extends Result implements Serializable,DontObfuscateInterface {
	private ArrayList<Session> sessionList;

    /**
     * 会话列表信息
     */
    public ArrayList<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(ArrayList<Session> sessionList) {
        this.sessionList = sessionList;
    }
}
