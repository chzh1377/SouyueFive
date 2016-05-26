package com.tuita.sdk;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * @author wanglong@zhongsou.com
 *
 */
public interface Constants extends DontObfuscateInterface{
	public static final String TYPE = "TYPE"; //取推送数据类型的key
	public static final int TYPE_GET_CLIENTID = 1;//推送数据类型，clientid
	public static final int TYPE_LOGIN = 40;//切换用户时，广播的额值
	public static final int TYPE_GET_DATA = 2;//推送数据类型，数据
	public static final String DATA = "DATA";//推送数据类型，取数据的key
	public static final String TITLE = "TITLE";

	//下面通知栏跳转专用
	public static final int TYPE_CHAT = 1;//跳转到聊天界面
	public static final int TYPE_LIST = 2;//跳转到消息列表
	public static final int TYPE_SERVICE_LIST = 3;//无NC的服务号跳转到列表页
	public static final int TYPE_DEFAULT_LIST = 4;//有NC但是 非1或2 走默认跳转到列表页
	public static final int TYPE_NEW_FRIEND = 5;//跳转到新朋友
	public static final String TARGET_TYPE = "TARGET_TYPE";//Bundle中携带过来的数据 TARGET_TYPE
	public static final String TARGET_ID = "TARGET_ID";//Bundle中携带过来的数据 TARGET_ID
	public static final String NOTIFY_ID = "NOTIFY_ID";//Bundle中携带过来的数据 NOTIFY_ID
	//--------------------

    public static final String PUSH_FROM = "PUSH_FROM"; //推送消息类型
    public static final String MID = "MID"; //消息id
}
