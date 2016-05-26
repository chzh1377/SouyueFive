package com.zhongsou.souyue.im.search;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * im消息搜索native接口
 * 
 * @author zhaomeng
 * 
 */
public class IMQuery implements DontObfuscateInterface {

	static {
		System.loadLibrary("imsearch");
	}

	public native static int init();

	/**
	 * 为搜索添加一条消息
	 * 
	 * @param msgText
	 *            消息内容
	 * @param msgTextLen
	 *            消息byte长度
	 * @param sessionType
	 * @param sessionId
	 * @param msgId
	 * @return
	 */
	public native static int addMessage(byte[] msgText, int msgTextLen,
										short sessionType, long sessionId,
										int msgId);

	/**
	 * 搜索某个关键字在整个消息中的会话，返回会话列表
	 * 
	 * @param msgText
	 * @param msgTextlen
	 * @param resultBuffer
	 * @param resulBufferLen
	 * @param usedLen
	 * @return 0 正常 1 正常buffer长度不够 -1 error
	 */
	public native static int queryList(byte[] msgText, int msgTextlen,
										byte[] resultBuffer, int resulBufferLen);

	/**
	 * 搜索某个关键字在某个会话下的消息，返回消息id列表
	 * 
	 * @param msgText
	 * @param msgTextlen
	 * @param sessionType
	 * @param sessionId
	 * @param resultBuffer
	 * @param resulBufferLen
	 * @param usedLen
	 * @return 0 正常 1 正常buffer长度不够 -1 error
	 */
	public native static int queryMessage(byte[] msgText, int msgTextlen,
											short sessionType, long sessionId,
											byte[] resultBuffer,
											int resulBufferLen);

	/**
	 * 删除一条会话
	 * 
	 * @param sessionType
	 * @param sessionId
	 * @return
	 */
	public native static int deleteSession(short sessionType, long sessionId);

	/**
	 * 删除一条消息
	 * 
	 * @param sessionType
	 * @param sessionId
	 * @param msgId
	 * @param msgText
	 * @param msgTextlen
	 * @return
	 */
	public native static int delMessage(short sessionType, long sessionId,
										int msgId, byte[] msgText,
										int msgTextlen);

	/**
	 * 加载保存过的搜索结果
	 * 
	 * @param filePath
	 * @return
	 */
	public native static int loadIndex(String filePath);

	/**
	 * 保存当前搜索结果
	 * 
	 * @param filePath
	 * @return
	 */
	public native static int saveIndex(String filePath);

	/**
	 * 返回搜索层最大的消息id
	 * 
	 * @return
	 */
	public native static long getMaxMessageID();

	/**
	 * 返回当前内存跌最大的消息id
	 * 
	 * @return
	 */
	public native static long getCurMaxMessageID();

	/**
	 * 销毁搜索创建的内存
	 * 
	 * @return
	 */
	public native static long destory();

}
