package com.tuita.sdk.im.db.helper;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import com.tuita.sdk.im.db.dao.MessageHistoryDao;
import com.tuita.sdk.im.db.dao.MessageHistoryDao.Properties;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.im.search.IMQuery;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.search.Session;
import com.zhongsou.souyue.log.Logger;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 聊天历史
 *
 * @author wanglong@zhongsou.com
 */
public class MessageHistoryDaoHelper extends BaseDaoHelper<MessageHistory> {
    private static MessageHistoryDaoHelper instance;

    public static MessageHistoryDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageHistoryDaoHelper();
            instance.dao = getDaoSession(context).getMessageHistoryDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private MessageHistoryDao dao;

    private MessageHistoryDaoHelper() {
    }

    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "deleteAll(id:" + id + ")");
        dao.deleteByKey(id);
    }

    public void deleteAll(long myid, long chat_id, int chat_type) {
        log(dao.getTablename(), "deleteAll(myid:" + myid + ",chat_id:" + chat_id + ")");
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=" + chat_type);
    }

    public void deleteSelected(long myid, long chat_id, String uuid) {
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=0" + " AND uuid='" + uuid + "'");
    }

    public void deleteGroupSelected(long myid, long chat_id, String uuid) {
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=1" + " AND uuid='" + uuid + "'");
    }


    /**
     * 新增删除所有chat_type类型方法 zcz
     *
     * @param myid
     * @param chat_id
     * @param uuid
     * @param chat_type
     */
    public void deleteSelectedItem(long myid, long chat_id, String uuid, int chat_type) {
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=" + chat_type + " AND uuid='" + uuid + "'");
    }

    /**
     * 向上取第一页的数据
     *
     * @param myid
     * @param chat_id
     * @return
     */
    public List<MessageHistory> findWhileFirstIn(long myid, long chat_id) {
        log(dao.getTablename(), "findToUp(myid:" + myid + ",chat_id:" + chat_id + ")");
        return findUp(myid, chat_id, -1);
    }

    /**
     * 向上取翻页的数据，session_order为上一页的
     *
     * @param myid
     * @param chat_id
     * @param session_order
     * @return
     */
    @SuppressWarnings("boxing")
    public List<MessageHistory> findUp(long myid, long chat_id, long session_order) {
        log(dao.getTablename(), "update(myid:" + myid + ",chat_id:" + chat_id + ",session_order:" + session_order + ")");
        QueryBuilder<MessageHistory> qb;
        if (session_order == 0) {
            return Collections.EMPTY_LIST;
        } else if (session_order == -1) {
            qb = dao.queryBuilder().limit(20).
                    where(Properties.Myid.eq(myid), Properties.Chat_id.eq(chat_id)).orderDesc(Properties.Id);//"session_order","id"
        } else {
            qb = dao.queryBuilder().limit(20).
                    where(Properties.Myid.eq(myid), Properties.Chat_id.eq(chat_id), Properties.Id.lt(session_order)).orderDesc(Properties.Id);//"session_order","id"
        }
//		if (session_order > 0) {
//			qb.where(Properties.Session_order.lt(session_order));
//		}
        List<MessageHistory> list = qb.list();
        Collections.reverse(list);
        return list;
    }


    /**
     * 取得此聊天框所有的数据
     *
     * @param myid
     * @param chat_id
     * @return
     */
    @SuppressWarnings("boxing")
    public List<MessageHistory> findAll(long myid, long chat_id) {
//        log(dao.getTablename(), "update(myid:" + myid + ",chat_id:" + chat_id + ",session_order:" + session_order + ")");
        QueryBuilder<MessageHistory> qb;

        qb = dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Chat_id.eq(chat_id)).orderDesc(Properties.Id);
        List<MessageHistory> list = qb.list();
        Collections.reverse(list);
        return list;
    }

    /**
     * 查找消息根据 bubblenumber
     *
     * @param myid
     * @param chat_id
     * @param limitCount
     * @return
     */
    @SuppressWarnings("boxing")
    public List<MessageHistory> findByLimitCount(long myid, long chat_id, int limitCount) {
        log(dao.getTablename(), "findByLimitCount(myid:" + myid + ",chat_id:" + chat_id + ",limitCount:" + limitCount + ")");
        QueryBuilder<MessageHistory> qb;
        qb = dao.queryBuilder().limit(limitCount).
                where(Properties.Myid.eq(myid), Properties.Chat_id.eq(chat_id)).orderDesc(Properties.Id);//"session_order","id"
        List<MessageHistory> list = qb.list();
        Collections.reverse(list);
        return list;
    }

    /**
     * @param myid
     * @param chat_id
     * @return
     */
    @SuppressWarnings("boxing")
    public long getMaxSessionOrder(long myid, long chat_id) {
        log(dao.getTablename(), "getMaxSessionOrder(myid:" + myid + ",chat_id:" + chat_id + ")");
        Cursor c = db.rawQuery("SELECT MAX(session_order) FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id, null);
        if (c != null) {
            if (c.moveToFirst()) {
                return c.getLong(0);
            }
            c.close();
        }
        return 0;
    }

    /**
     * @param myid
     * @param chat_id
     * @param session_order
     * @return
     */
    @SuppressWarnings("boxing")
    public List<MessageHistory> findWhileNewMessageCome(long myid, long chat_id, long session_order) {
        log(dao.getTablename(), "findToDown(myid:" + myid + ",chat_id:" + chat_id + ",session_order:" + session_order + ")");
        if (session_order < 0) {
            return Collections.EMPTY_LIST;
        }
//		return dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Chat_id.eq(chat_id), Properties.Session_order.gt(session_order)).orderAsc(Properties.Session_order, Properties.Id).list();
        return dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Chat_id.eq(chat_id), Properties.Session_order.gt(session_order)).orderAsc(Properties.Id).list();
    }

    public List<MessageHistory> findSearchList(long myid, ArrayList<Session> sessions) {
        log(dao.getTablename(), "findSearchList(myid:" + myid + ",sessions:" + sessions + ")");
        List<MessageHistory> msgList = new ArrayList<MessageHistory>();
        for (Session session : sessions) {
            List<MessageHistory> msgs = dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Id.eq(session.msgId)).orderAsc(Properties.Id).list();
            if (msgs != null && msgs.size() > 0)
                msgList.add(msgs.get(0));
        }
        return msgList;
    }

    public List<MessageHistory> findSearchListByMsgIds(long myid, ArrayList<Integer> msgIds) {
        List<MessageHistory> msgList = new ArrayList<MessageHistory>();
        for (Integer msgId : msgIds) {
            List<MessageHistory> msgs = dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Id.eq(msgId)).orderAsc(Properties.Id).list();
            if (msgs != null && msgs.size() > 0)
                msgList.add(msgs.get(0));
        }
        return msgList;
    }

    //查询命中消息后的所有消息
    public List<MessageHistory> findSearchTargetMsg(long myid, int msgId, long chatId, int chatType) throws UnsupportedEncodingException {
        log(dao.getTablename(), "findById(myid:" + myid + ")");
        long longMsgId = msgId;
        List<MessageHistory> list = dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Id.ge(longMsgId), Properties.Chat_id.eq(chatId), Properties.Chat_type.eq(chatType)).orderAsc(Properties.Id).list();
        return list;
    }

    public MessageHistory findSingleSearchList(long myid, Session session) {
        MessageHistory mHistory = new MessageHistory();
        List<MessageHistory> msgs = dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Id.eq(session.msgId)).orderAsc(Properties.Id).list();
        if (msgs != null && msgs.size() > 0)
            mHistory = msgs.get(0);
        return mHistory;
    }

    public MessageHistory findLast(long myid, long chatid) {
        List<MessageHistory> list = dao.queryRaw("WHERE myid=? AND chat_id=? order by _id desc limit 1", myid + "", chatid + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public MessageHistory find(String uuid, long myid) {
        log(dao.getTablename(), "find(uuid:" + uuid + ")");
        List<MessageHistory> list = dao.queryRaw("WHERE uuid=? AND myid=?", uuid, myid + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 查询历史消息表最大ID
     *
     * @param myid
     * @return
     */
    public long findMaxId(long myid) {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT MAX(_id) FROM " + dao.getTablename() + " WHERE myid=" + myid, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    return c.getLong(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return 0;
    }

    public void updateSearchMsg(long myid, long id) throws UnsupportedEncodingException {
        log(dao.getTablename(), "updateSearchMsg(myid:" + myid + ")");
        List<MessageHistory> list = dao.queryBuilder().where(Properties.Myid.eq(myid), Properties.Id.gt(id), Properties.Content_type.eq(IMessageConst.CONTENT_TYPE_TEXT), Properties.Chat_type.notEq(IConst.CHAT_TYPE_SERVICE_MESSAGE)).orderAsc(Properties.Id).list();

        //打印搜索结果日志
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Logger.i("IM消息搜索————MessageHistoryDaoHelper————updateSearchMsg", "time:" + date.format(new Date()), "list.size() ：" + list.size());

        for (MessageHistory msg : list) {
            SearchUtils.addMessage(msg.getContent(), (short) msg.getChat_type(), msg.getChat_id(), new Long(msg.getId()).intValue());
        }

    }

    public void save(MessageHistory msg) {
        log(dao.getTablename(), "save(msg:" + msg + ")");
        if (msg != null) {
            List<MessageHistory> list = dao.queryRaw("WHERE uuid=? and myid=? and chat_id=?", msg.getUuid(), msg.getMyid() + "", msg.getChat_id() + "");
            if (list == null || list.size() == 0) {
                log(dao.getTablename(), "insert(msg:" + msg + ")");
                dao.insert(msg);
            }
        }
    }

    @SuppressWarnings("boxing")
    public MessageHistory update(String uuid, long myId, int status, long session_order) {
        MessageHistory msg = find(uuid, myId);
        log(dao.getTablename(), "update(uuid:" + uuid + ",status:" + status + ",session_order:" + session_order + ")", msg);
        if (msg != null) {
            msg.setStatus(status);
            if (session_order > 0) {
                msg.setSession_order(session_order);
            }
            msg.setDate(System.currentTimeMillis());
            dao.update(msg);
        }
        return msg;
    }

    public void updateMsgText(String uuid, String text) {
        dao.getDatabase().execSQL("UPDATE " + dao.getTablename() + " SET CONTENT = '" + text + "' WHERE UUID = '" + uuid + "';");
    }

    @SuppressWarnings("boxing")
    public void update(String uuid, long myId, String content, long session_order, int status) {
        MessageHistory msg = find(uuid, myId);
//		log(dao.getTablename(), "update(uuid:" + uuid + ",status:" + status + ",session_order:" + session_order + ")", msg);
        if (msg != null) {
            msg.setContent(content);
            msg.setStatus(status);
            if (session_order > 0) {
                msg.setSession_order(session_order);
            }
            msg.setDate(System.currentTimeMillis());
            dao.update(msg);
        }
    }

    public void updateCurrentTime(String uuid, int content_type, long myId, long chat_id, long currentTime) {
        List<MessageHistory> list = dao.queryRaw("WHERE uuid=? and content_type=? and myid=? and chat_id=?", uuid, content_type + "", myId + "", chat_id + "");
        MessageHistory msg = list != null && list.size() > 0 ? list.get(0) : null;
        if (msg != null) {
            msg.setBy1(currentTime + "");
            dao.update(msg);
        }
    }

    public void updateStatus(String uuid, int content_type, long myId, long chat_id, int isRead) {
        List<MessageHistory> list = dao.queryRaw("WHERE uuid=? and content_type=? and myid=? and chat_id=?", uuid, content_type + "", myId + "", chat_id + "");
        MessageHistory msg = list != null && list.size() > 0 ? list.get(0) : null;
        if (msg != null) {
            msg.setStatus(isRead);
            dao.update(msg);
        }

        log(dao.getTablename(), "updateStatus(uuid:" + uuid + ",content_type:" + content_type + ",myId:" + myId + ")", msg);
    }

    /**
     * 根据指定chatIdl来更新msgId
     *
     * @param id
     * @param msgFileId
     */
    public void updateMsgFileId(long id, long msgFileId) {
        dao.getDatabase().execSQL("UPDATE " + dao.getTablename() + " SET FILE_MSG_ID = '" + msgFileId + "' WHERE _id = '" + id + "';");
    }


    /**
     * 根据指定id来查找msgId
     */
    public long findMsgFileId(long id) {

        List<MessageHistory> msgList = dao.queryBuilder().where(Properties.Id.eq(id)).list();
        MessageHistory history = msgList.get(0);

        return history.getFileMsgId();
    }

    /**
     * 根据指定msgId来返回chatId
     */
    public long findMsgChatId(long msgId) {
        List<MessageHistory> msgList = dao.queryBuilder().where(Properties.fileMsgId.eq(msgId)).list();
        if (msgList.size() > 0) {
            MessageHistory history = msgList.get(0);
            return history.getId();
        } else {
            return -1;
        }

    }

    /**
     * 根据mid查询 相应地消息
     * @param myId
     * @param mid
     * @return
     */
    public MessageHistory findByMid(long myId,String mid){
        List<MessageHistory> msgList = dao.queryBuilder().where(Properties.Myid.eq(myId),Properties.By4.eq(mid)).list();
        return msgList != null && msgList.size() > 0 ? msgList.get(0) : null;
    }


}
