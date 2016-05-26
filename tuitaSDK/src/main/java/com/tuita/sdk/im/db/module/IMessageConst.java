package com.tuita.sdk.im.db.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public interface IMessageConst  extends   Serializable,DontObfuscateInterface{
    
	/**
	 * 内容类型：纯文本
	 */
	public static final int  CONTENT_TYPE_TEXT = 0;

	/**
	 * 内容类型：语音  (废弃)
	 */
	public static final int CONTENT_TYPE_VOICE = 1;
	
	/**
	 * 内容类型：图片   （废弃）
	 */
	public static final int CONTENT_TYPE_IMAGE = 2;
	
	/**
	 * 内容类型：名片
	 */
	public static final int CONTENT_TYPE_VCARD = 3;
	
	/**
	 * 内容类型：求中搜币
	 */
	public static final int CONTENT_TYPE_TIGER = 4;
	
	/**
     * 内容类型：分享老虎机
     */
	public static final int CONTENT_TYPE_SHARE_TIGER = 5;
	
	/**
	 * 内容类型：系统提示 发消息时发现不是好友了
	 */
	public static final int CONTENT_TYPE_SYS_NOTFRIEND = 6;
	
	/**
     * 圈吧分享帖 7
     */
	public static final int CONTENT_TYPE_INTEREST_SHARE = 7;
    
	/**
     *添加好友入公开圈吧 8
     */
	public static final int CONTENT_TYPE_INTEREST_ADD_FRIEND = 8;
	
	
    
	/**
     *搜悦新闻分享 9
     */
	public static final int CONTENT_TYPE_SOUYUE_NEWS_SHARE = 9;
	
	/**
	 * 密信（悄悄话）10
	 */
	public static final int CONTENT_TYPE_SECRET_MSG = 10;
	
	/**
	 * 赠中搜币（废弃）
	 */
	public static final int CONTENT_TYPE_SENDCOIN = 11;
	
	/**
	 * 内容类型：语音    3.9以后语音
	 */
	public static final int CONTENT_TYPE_NEW_VOICE = 12;
	
	
	/**
     *圈名片分享 13
     */
	public static final int CONTENT_TYPE_INTEREST_CIRCLE_CARD = 13;
	
	/**
	 *添加好友入私密圈吧 14
	 */
	public static final int CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE = 14;
	
	/**
	 * 内容类型：图片 4.0新加
	 */
	public static final int CONTENT_TYPE_NEW_IMAGE = 15;

    /**
     * 内容类型： 4.0.2新加服务号ct=1的类型
     */
    public static final int CONTENT_TYPE_SERVICE_MESSAGE_FIRST = 16;

    /**
     * 内容类型： 4.0.2新加服务号ct=2的类型
     */
    public static final int CONTENT_TYPE_SERVICE_MESSAGE_SECOND = 17;

    /**
     *
     * 内容类型：4.0.2新加服务号文本类型，为以后适配，客户端需要转成0
     */
    public static final int CONTENT_TYPE_SERVICE_MESSAGE_TEXT = 18;

    /**
     * 4.1群名片分享
     */
    public static final int CONTENT_TYPE_GROUP_CARD = 19;

    /**
     * SRP词分享类型
     */
    public static final int CONTENT_TYPE_SRP_SHARE = 20;
    /**
     * at好友类型
     */
    public static final int CONTENT_TYPE_AT_FRIEND = 21;
    /**
     * GIF表情
     */
    public static final int CONTENT_TYPE_GIF = 22;
    /**
     * WEB跳转类型（贺卡等）4.2.Max添加
     */
    public static final int CONTENT_TYPE_WEB = 23;
    /**
     * 新增赠币类型   4.2.2新加
     */
    public static final int CONTENT_TYPE_GIFT_COIN = 24;

	/**
	 * 新增发送文件类型 4.0.8新加
	 */
	public static final int CONTENT_TYPE_FILE = 25;

	/**
	 * 新增红包类型 5.1新加
	 */
	public static final int CONTENT_TYPE_RED_PAKETS = 26;

	/**
	 * 适配新协议的系统消息类型 5.1新加用于红包系统消息
	 */
	public static final int CONTENT_TYPE_NEW_SYSTEM_MSG = 27;
	/**
	 * 系统消息
	 */
	public static final int CONTENT_TYPE_SYSMSG = 1001;

    /**
     * 搜悦调用消息类型
     */
    public static final int CONTENT_TYPE_SOUYUE_MSG = 2000;
	/**
	 * 状态，已发送
	 */
	public static final int STATUS_HAS_SENT = 1;
	/**
	 * 状态，未发送
	 */
	public static final int STATUS_SENTING = 0;
	/**
	 * 状态，发送失败
	 */
	public static final int STATUS_SENT_FAIL = 3;

    /**
     * 状态，语音已读
     */
    public static final int STATE_HAS_READ = 2;

	/**
	 * 状态，红包已拆
	 */
	public static final int STATE_RED_PACKET_READ = 4;

    /**
     * 状态：保存到通讯录
     */
    public static final int STATE_IS_SAVED = 1;

    /**
     * 状态：不保存到通讯录
     */
    public static final int STATE_NO_SAVED = 0;

    /**
     * 服务号消息类型CT = 1
     */
    public static final int MSG_SERVICE_MESSAGE_FIRST = 1;

    /**
     * 服务号消息类型CT = 2
     */
    public static final int MSG_SERVICE_MESSAGE_SECOND = 2;

    /**
     * 服务号消息类型CT = 3
     */
    public static final int MSG_SERVICE_MESSAGE_TEXT = 3;

}
