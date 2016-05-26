package com.zhongsou.souyue.module;

import com.zhongsou.souyue.utils.SYUserManager;

/** 
 * @author : zoulu
 * 2014年2月26日
 * 下午2:07:00 
 * 类说明 :用户行为统计事件类型ID
 */
public class UserAction {
	/**
	 * 绑定第三方平台账号
	 */
	public static String BINDSNS = "61";
	/**
	 * 回访(再次到Splash页面)
	 */
	public static String OPENAGAIN = "62";
	/**
	 * 分享到第三方平台
	 */
	public static String SHARETOSNS = "66";
	/**
	 * 分享到微信好友
	 */
	public static String FRIENDS = "97";
	/**
	 * 分享到微信朋友圈
	 */
	public static String FRIENDSGROUP = "98";
	
	/**
	 * 系统标识(由积分系统分配的系统标识，
　　	 * hem系统：hems3  
　　   * 搜悦：sy
	 */
	public static String SYSTEMSIGN = "sy";
	/**
	 * 系统apikey(由积分系统分配的apikey，
　　   *hems系统：8f60c8102d29fcd525162d02eed4566b 
　　   *搜悦：f1f90d6f5f409f992ad7ade0c0ee3b06）
	 */
	public static String APIKEY = "f1f90d6f5f409f992ad7ade0c0ee3b06";
	/**
	 * 用户类型(用户类型说明：
　　    * 29 普通用户  （搜悦使用）
　　    *31 搜索达人
　　    * 32 社区运营管理员
　　    * 4  社区系统管理员	)
	 */
	public static String USERTYPE = "29";
	/**
	 * 加积分次数：默认值为1
	 */
	public static String COUNT = "1";
	/**
	 * 添加积分词条：默认为空
	 */
	public static String KEYWORD = "";
	/**
	 * 　　加积分的倍数：默认为1
	 */
	public static String PERCENTAGE = "1";
	/**
	 * 用户积分接口必须带参数  默认为1
	 */
	public static String ENCRYPT = "1";
	/**
	 * 判断用户是否登录
	 * @return
	 */
	public static boolean isLogin(){
        return SYUserManager.USER_ADMIN.equals(SYUserManager.getInstance().getUserType());
	}
	/**
	 * 获取用户登录帐号
	 * @return
	 */
	public static String getUsername(){
		User user = SYUserManager.getInstance().getUser();
		return user.userName();
	}
	
}
