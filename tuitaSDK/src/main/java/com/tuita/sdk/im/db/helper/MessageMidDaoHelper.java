package com.tuita.sdk.im.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import com.tuita.sdk.im.db.dao.ConfigDao;
import com.tuita.sdk.im.db.dao.MessageMidDao;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.MessageMid;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * 103消息mid表，排重用
 *
 * @author zhangwb
 */
public class MessageMidDaoHelper extends BaseDaoHelper<Config> {
    private static MessageMidDaoHelper instance;

    public static MessageMidDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageMidDaoHelper();
            instance.dao = getDaoSession(context).getMessageMidDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private MessageMidDao dao;

    private MessageMidDaoHelper() {
    }

    /**
     * 排重方法
     */
    @SuppressWarnings("boxing")
    public boolean isContraintAndinsert(String mid, long timestamp) {
        log(dao.getTablename(), "insert(messageMid:" + mid + ")");
        MessageMid messageMid = new MessageMid(mid, timestamp);
        try {
            dao.insert(messageMid);
            Log.i("sqlmid", "--->插入成功");
            return false;
        } catch (SQLiteConstraintException e) {
            Log.i("sqlmid", "--->重复消息");
            return true;
        }
    }

    /**
     * 批量删除小于给定时间的 mid
     * @param timestamp
     */
    public void deleteByTimeStamp(long timestamp) {
        QueryBuilder<MessageMid> qb = dao.queryBuilder();
        DeleteQuery<MessageMid> bd = qb.where(MessageMidDao.Properties.Timestamp.lt(timestamp)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }


}
