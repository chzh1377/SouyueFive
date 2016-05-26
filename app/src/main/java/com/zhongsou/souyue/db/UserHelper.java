package com.zhongsou.souyue.db;

import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @author huanglb@zhongsou.com
 * 
 */
public class UserHelper {
//	public static final String GUEST_TOKEY_TYPE = "0";// 游客类型
//	public static final String ADMIN_TOKEN_TYPE = "1";// 正式用户
	private static UserHelper instance = null;
	private UserHelper() {
	}

	public synchronized static UserHelper getInstance() {
		if (instance == null) {
			instance = new UserHelper();
		}
		return instance;
	}

	/**
	 * 添加UserInfo
	 * 
	 * @param user
	 * @return
	 */
	public long addUserInfo(User user) {
		long l = 0;
		User oldUser = getUserInfoById(user.userId());
		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
		userTableDBHelper.openWritable();
		if (oldUser != null && oldUser.token() != null && !"".equals(oldUser.token()))
			updataUser(user);
		else {
			l = userTableDBHelper.insert(user);
		}
		userTableDBHelper.close();
		return l;
	}

	/**
	 * 获得UserInfo
	 * 
	 * @return
	 */
	public User getUserInfo() {
		return getUserInfo(SYUserManager.USER_ADMIN);
	}

	/**
	 * 获得游客信息
	 *
	 * @return
	 */
	public User getGuestUserInfo(){
		return getUserInfo(SYUserManager.USER_GUEST);
	}
	/**
	 * 获得UserInfo 优先获取注册用户信息，没有登陆获得游客信息
	 *
	 * @return
	 */
	private User getUserInfo(String type){
		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
		userTableDBHelper.openReadable();
		User user = null;
		user = userTableDBHelper.select(type);
		if (user == null || "".equals(user.token()))
			user = userTableDBHelper.select(SYUserManager.USER_GUEST);
		userTableDBHelper.close();
		return user;
	}
	 /**
     * 获得UserInfo
     * 
     * @return
     */
    public User getAdminUserInfo() {
        UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
        userTableDBHelper.openReadable();
        User user = null;
        user = userTableDBHelper.select(SYUserManager.USER_ADMIN);
        userTableDBHelper.close();
        return user;
    }

	/**
	 * 获取指定userinfo
	 */
	public User getUserInfoByType(String type) {
		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
		userTableDBHelper.openReadable();
		User user = null;
		user = userTableDBHelper.select(type);
		userTableDBHelper.close();
		return user;
	}
	
	/**
     * 获取指定userinfo userid
     */
    public User getUserInfoById(long userId) {
        UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
        userTableDBHelper.openReadable();
        User user = null;
        user = userTableDBHelper.selectByUserId(userId);
        userTableDBHelper.close();
        return user;
    }

	/**
	 * 删除UserInfo
	 */
	public long deleteUser(User user) {
		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
		userTableDBHelper.openWritable();
		long l = userTableDBHelper.delete(user);
		userTableDBHelper.close();
		return l;
	}

	/**
	 * 更新UserInfo
	 */
	public void updataUser(User user) {
		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
		userTableDBHelper.openReadable();
		userTableDBHelper.updata(user);
		userTableDBHelper.close();
	}
	
//	/**
//	 * 更新UserInfo
//	 */
//	public void updataUserImage(String image) {
//		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
//		userTableDBHelper.openWritable();
//		userTableDBHelper.updataImage(image);
//		userTableDBHelper.close();
//	}
//	
//	/**
//	 * 更新UserInfo_Name
//	 */
//	public void updateUserNick(String nick) {
//		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
//		userTableDBHelper.openWritable();
//		userTableDBHelper.updateNick(nick);
//		userTableDBHelper.close();
//	}
//	
//	public void updateUserBgUrl(String bgUrl) {
//		UserTableDBHelper userTableDBHelper = new UserTableDBHelper();
//		userTableDBHelper.openWritable();
//		userTableDBHelper.updateBgUrl(bgUrl);
//		userTableDBHelper.close();
//	}
	
}
