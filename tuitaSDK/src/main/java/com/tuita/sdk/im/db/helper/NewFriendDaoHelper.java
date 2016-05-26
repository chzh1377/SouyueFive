package com.tuita.sdk.im.db.helper;
import java.util.List;
import android.content.Context;
import com.tuita.sdk.im.db.dao.NewFriendDao;
import com.tuita.sdk.im.db.dao.NewFriendDao.Properties;
import com.tuita.sdk.im.db.module.NewFriend;
/**
 * 新的朋友
 * @author wanglong@zhongsou.com
 *
 */
public class NewFriendDaoHelper extends BaseDaoHelper<NewFriend> {
	private static NewFriendDaoHelper instance;
	public static NewFriendDaoHelper getInstance(Context context) {
		if (instance == null) {
			instance = new NewFriendDaoHelper();
			instance.dao = getDaoSession(context).getNewFriendDao();
			instance.db = instance.dao.getDatabase();
		}
		return instance;
	}
	private NewFriendDao dao;
	private NewFriendDaoHelper() {
	}
	@SuppressWarnings("boxing")
	public void delete(long id) {
		log(dao.getTablename(), "delete(id:" + id + ")");
		dao.deleteByKey(id);
	}
	public void delete(long myid, long chat_id) {
		log(dao.getTablename(), "deleteAll(myid:" + myid + ",chat_id:" + chat_id + ")");
		db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id);
	}
	public void deleteAll(long myid) {
		log(dao.getTablename(), "deleteAll(myid:" + myid + ")");
		db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid);
	}
	/**
	 * 插入或更新
	 * @param friend
	 */
	public void save(NewFriend friend) {
		log(dao.getTablename(), "save(friend:" + friend + ")");
		if (friend != null) {
			List<NewFriend> list = dao.queryRaw("WHERE myid=? AND chat_id=?", friend.getMyid() + "", friend.getChat_id() + "");
			if (list == null || list.size() == 0) {
				log(dao.getTablename(), "insert(friend:" + friend + ")");
				dao.insert(friend);
			} else {
				friend.setId(list.get(0).getId());
				log(dao.getTablename(), "update(friend:" + friend + ")");
				dao.update(friend);
			}
		}
	}
	@SuppressWarnings("boxing")
	public List<NewFriend> findAll(long myid) {
		List<NewFriend> list = dao.queryBuilder().where(Properties.Myid.eq(myid)).orderDesc(Properties.Id).list();
		log(dao.getTablename(), "findAll(myid:" + myid + ")", list);
		return list;
	}
	public NewFriend find(long myid, long chat_id) {
		List<NewFriend> list = dao.queryRaw("WHERE myid=? AND chat_id=? AND chat_type=?", myid + "", chat_id + "", 0 + "");
		log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id + ")", list);
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
	public void update(long id, int status) {
		log(dao.getTablename(), "update(id:" + id + ",status:" + status + ")");
		db.execSQL("UPDATE " + dao.getTablename() + " SET status=" + status + " WHERE id=" + id);
	}
	/**
	 * 新的未读的朋友的个数，用于气泡显示
	 * @param myid
	 * @return
	 */
	@SuppressWarnings("boxing")
	public long findNotRead(long myid) {
		long count = dao.queryBuilder().where(Properties.Myid.eq(myid)).count();
		log(dao.getTablename(), "findNotRead(myid:" + myid + ")", count);
		return count;
	}
}
