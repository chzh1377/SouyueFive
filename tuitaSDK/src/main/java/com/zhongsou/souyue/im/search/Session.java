package com.zhongsou.souyue.im.search;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * 搜索会话结果
 * 
 * @author zhaomeng
 * 
 */
public class Session implements Serializable,DontObfuscateInterface {
	/**
	 * 会话类型
	 */
	public int sessionType;
	/**
	 * 会话id
	 */
	public long sessionId;
	/**
	 * 搜索结果最新消息id
	 */
	public int msgId;
	/**
	 * 关键字出现次数
	 */
	public int count;
}
