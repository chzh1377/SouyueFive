package com.tuita.sdk.im.db.helper;
import android.content.Context;
import com.tuita.sdk.im.db.dao.ConfigDao;
import com.tuita.sdk.im.db.module.Config;

import java.util.List;
/**
 * 联系人时间戳
 * @author wanglong@zhongsou.com
 *
 */
public class ConfigDaoHelper extends BaseDaoHelper<Config> {
	private static ConfigDaoHelper instance;
	public static ConfigDaoHelper getInstance(Context context) {
		if (instance == null) {
			instance = new ConfigDaoHelper();
			instance.dao = getDaoSession(context).getConfigDao();
			instance.db = instance.dao.getDatabase();
		}
		return instance;
	}
	private ConfigDao dao;
	private ConfigDaoHelper() {
	}
	public Config find(long myid) {
		List<Config> list = dao.queryRaw("WHERE myid=?", myid + "");
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
	/**
	 * 插入新config
	 */
	@SuppressWarnings("boxing")
	public void insert(Config config) {
		log(dao.getTablename(), "insert(config:" + config + ")");
		dao.insert(config);
	}


	/**
	 * 更新config
	 */
	@SuppressWarnings("boxing")
	public void update(Config config) {
		log(dao.getTablename(), "insert(config:" + config + ")");
		dao.update(config);
	}
	/**
	 * 插入或更新
	 * @param myid
	 * @param contact_last_update
	 */
	@SuppressWarnings("boxing")
	public void updateContactLastUpdate(long myid, long contact_last_update) {
		log(dao.getTablename(), "updateContactLastUpdate(myid:" + myid + ",contact_last_update:" + contact_last_update + ")");
		log(dao.getTablename(), "save(myid:" + myid + ",contact_last_update:" + contact_last_update + ")");
		Config config = find(myid);
		if (config != null) {
			config.setContact_last_update(contact_last_update);
			dao.update(config);
		} else {
			config = new Config();
			config.setMyid(myid);
			config.setContact_last_update(contact_last_update);
			dao.insert(config);
		}
	}
	public void updateMessageBubble(long myid, int bubbleCount) {
//		log(dao.getTablename(), "updateMessageBubble(myid:" + myid + ",bubbleCount:" + bubbleCount + ")");
		Config config = find(myid);
		if (config != null) {
			db.execSQL("UPDATE " + dao.getTablename() + " SET total_message_bubble=" + bubbleCount + " WHERE myid=" + myid);
		} else {
			config = new Config();
			config.setMyid(myid);
			config.setTotal_message_bubble(bubbleCount);
			dao.insert(config);
		}
	}
	public void cleanFriendBubble(long myid) {
		log(dao.getTablename(), "cleanFriendBubble(myid:" + myid + ")");
		db.execSQL("UPDATE " + dao.getTablename() + " SET friend_bubble=0 WHERE myid=" + myid);
	}
	public void addFriendBubble(long myid, int bubbleCount) {
		log(dao.getTablename(), "addFriendBubble(myid:" + myid + ",bubbleCount:" + bubbleCount + ")");
		Config config = find(myid);
		if (config != null) {
			db.execSQL("UPDATE " + dao.getTablename() + " SET friend_bubble=friend_bubble+" + bubbleCount + " WHERE myid=" + myid);
		} else {
			config = new Config();
			config.setMyid(myid);
			config.setFriend_bubble(bubbleCount);
			dao.insert(config);
		}
	}
}
