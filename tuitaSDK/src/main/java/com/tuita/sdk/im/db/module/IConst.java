package com.tuita.sdk.im.db.module;

public interface IConst extends IMessageConst {

	public static final int CHAT_TYPE_PRIVATE = 0;//私聊mt
	public static final int CHAT_TYPE_GROUP = 1;//群聊mt
	public static final int CHAT_TYPE_SYSTEM = 2;//形同消息mt
	public static final int CHAT_TYPE_CMD = 3;//老虎机赠币mt
    public static final int CHAT_TYPE_SERVICE_MESSAGE = 4;//服务号mt
    public static final int CHAT_TYPE_SOUYUE_MSG = 5;   //搜悦调用mt
	
	public static final int QUERY_MSG_FIRST = 0;
	public static final int QUERY_MSG_UP = 1;
	public static final int QUERY_MSG_DOWN = 2;
    public static final int QUERY_MSG_ALL = 3;//查询数据库中所有聊天
	
	public static final int CONTACT_NEW = 1;
	public static final int CONTACT_UPDATE = 2;
	public static final int CONTACT_DEL = 3;
	
	public static final String FIRST_MESSAGE = "我们已经是好友啦，开始聊天吧!";
	public static final long FIRST_SESSION_ORDER = 0;
	public static final int FIRST_BUBBLE = 1;
	
	public static final String GIFT_ZSB_SUCCESS = "1";
	public static final String GIFT_ZSB_FAIL = "2";
	public static final String GIFT_ZSB_TIMEOUT = "3";
	public static final String GIFT_ZSB_ERROR_NOTFRIEND = "4";
	
	public static final String CHARGE_ZSB_SUCCESS = "1";
	public static final String CHARGE_ZSB_FAIL = "2";
	
	//新朋友数据类型
	public static final String NEWFRIEND_PHONE_RECOMMEND = "1";//服务器给的类型为9的
	public static final String NEWFRIEND_PHONE_MATCHING = "2"; //服务器给的类型为2的am为1的
	
	//通讯录录数据类型
	public static final String CONTACT_PHONE_RECOMMEND = "0";
    public static final String CONTACT_PHONE_MATCHING = "1";
    
    //通讯录表中是否修改过备注名
    public static final String IM_UPDATEOP_EDITALIAS = "1"; //修改过为1


	//服务号相关常量
	public static final String SOUXIAOYUE_URL = "http://souyue-image.b0.upaiyun.com/sysimg/xiaoyuenew.png";
	public static final long SOUXIAOYUE_ID = 25;
	public static final String SOUYXIAOYUE_NAME = "搜小悦-官方客服";

    //新增通用协议 pos
    public static final int SYSTEM_POS_SERVICE_MSG = 3;   //服务号pos

    //推送消息类型
    public static final String PUSH_TYPE_JPUSH = "1";    //极光推送消息类型
    public static final String PUSH_TYPE_TUITA = "tuita";    //tuitaIM消息推送类型
    public static final String PUSH_TYPE_DEFAULT = "tuita";    //消息推送类型
    public static final String PUSH_TYPE_MI = "2";    //小米推送类型
    public static final String PUSH_TYPE_HUAWEI = "3";    //华为推送类型
}
