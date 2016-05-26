package com.tuita.sdk.im.db.helper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.tuita.sdk.im.db.dao.*;
import com.tuita.sdk.im.db.dao.MessageRecentDao.Properties;
import com.tuita.sdk.im.db.module.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 最近聊天记录，类似微件的第一个页面。
 *
 * @author wanglong@zhongsou.com
 */
public class MessageRecentDaoHelper extends BaseDaoHelper<MessageRecent> {
    private static MessageRecentDaoHelper instance;
    private final int _id = 0;
    private final int M_MYID = 1;
    private final int M_CHAT_ID = 2;
    private final int M_CHAT_TYPE = 3;
    private final int M_CONTENT = 4;
    private final int M_CONTENT_TYPE = 5;
    private final int M_DATE = 6;
    private final int M_UUID = 7;
    private final int M_STATUS = 8;
    private final int M_BUBBLENUM = 9;
    private final int M_SENDER = 10;
    private final int M_BY1 = 11;
    private final int M_BY2 = 12;
    private final int M_DRAFTTEXT = 13;
    private final int M_DRAFTFORAT = 14;
    private final int M_BY3 = 15;
    private final int M_BY4 = 16;
    private final int M_BY5 = 17;
    private final int C_NICK_NAME = 18;
    private final int C_COMMMENT_NAME = 19;
    private final int C_AVATAR = 20;
    private final int C_IS_NEWS_NOTIFY = 21;
    private final int G_GROUP_NICK_NAME = 22;
    private final int G_AVATAR = 23;
    private final int G_IS_NEWS_NOTIFY = 24;
    private final int CA_NAME = 25;
    private final int CA_AVATAR = 26;
    private final int CA_IS_HAS_CATE_ID = 27;
    private final int CA_DIGST = 28;
    private final int CA_BUBBLE_NUM = 29;
    private final int S_NAME = 30;
    private final int S_AVATAR = 31;
    private final int S_BY3 = 32;
    private final int S_DATE = 33;
    private final int ME_CONTENT_TYPE = 34;
    private final int ME_CONTENT = 35;
    private final int ME_DATE = 36;
    private final int ME_SENDER = 37;
    private final int ME_UUID = 38;
    private final int ME_STATUS = 39;

    public static MessageRecentDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageRecentDaoHelper();
            instance.dao = getDaoSession(context).getMessageRecentDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private MessageRecentDao dao;

    private MessageRecentDaoHelper() {
    }

    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

//    public void deleteAll(long myid, long chat_id) {
//        log(dao.getTablename(), "deleteAll(myid:" + myid + ",chat_id:" + chat_id + ")");
//        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=0");
//    }

//    public void delete(long myid, long chat_id) {
//        log(dao.getTablename(), "deleteAll(myid:" + myid + ",chat_id:" + chat_id + ")");
//        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=1");
//    }

    public void delete(long myid, long chat_id, int chat_type) {
        log(dao.getTablename(), "deleteAll(myid:" + myid + ",chat_id:" + chat_id + ")");
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id=" + chat_id + " AND chat_type=" + chat_type);
    }

    public MessageRecent find(long myid, long chat_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id + ")");
        List<MessageRecent> list = dao.queryRaw("WHERE myid =? AND chat_id=?", myid + "", chat_id + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public MessageRecent find(long myid, long chat_id,int chatType) {
        log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id + ")");
        List<MessageRecent> list = dao.queryRaw("WHERE myid =? AND chat_id=? AND chat_type=?", myid + "", chat_id + "",chatType+"");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public MessageRecent find(String uuid, long myid) {
        log(dao.getTablename(), "find(uuid:" + uuid + ")");
        List<MessageRecent> list = dao.queryRaw("WHERE uuid=? AND myid=?", uuid, myid + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 插入或更新
     *
     * @param msg
     */
    @SuppressWarnings("boxing")
    public void save(MessageRecent msg) {
        log(dao.getTablename(), "save(msg:" + msg + ")");
        if (msg != null) {
            List<MessageRecent> list = new ArrayList<MessageRecent>();
            switch (msg.getChat_type()) {
                case IConst.CHAT_TYPE_CMD:
                    list = dao.queryRaw("WHERE myid=? AND chat_id=? AND chat_type=?",
                            msg.getMyid() + "", msg.getChat_id() + "", 0 + "");
                    msg.setChat_type(0);
                    break;
                default:
                    if (msg.getChat_type() == IConst.CHAT_TYPE_PRIVATE) {
                        list = dao.queryRaw("WHERE myid=? AND chat_id=? AND chat_type=?",
                                msg.getMyid() + "", msg.getChat_id() + "", 0 + "");
                    } else {
                        list = dao.queryRaw("WHERE myid=? AND chat_id=? AND chat_type=?",
                                msg.getMyid() + "", msg.getChat_id() + "", msg.getChat_type() + "");
                    }
                    break;
            }
            if (list == null || list.size() == 0) {
                log(dao.getTablename(), "insert(msg:" + msg + ")");
                msg.setBy3(msg.getChat_type() == IConst.CHAT_TYPE_SERVICE_MESSAGE && msg.getChat_id() == IConst.SOUXIAOYUE_ID ? System.currentTimeMillis() + "" : "0");
                dao.insert(msg);
            } else {
                MessageRecent r = list.get(0);
//				if (msg.getUuid() != null && ! msg.getUuid().equals(r.getUuid())) {
                msg.setId(r.getId());
                log(dao.getTablename(), "update(msg:" + msg + ")");
                //保证有人@你不被冲掉
                if (msg.getBy1() != null && msg.getBy1().equals("0")) {
                    msg.setBy1("0");
                } else if (r.getBy1() != null && r.getBy1().equals("1"))
                    msg.setBy1("1");
                if (msg.getUuid() != null && !msg.getUuid().equals(r.getUuid())) {
                    msg.setBubble_num(msg.getBubble_num() + r.getBubble_num());
                }
                //保证定置顶信息不被冲掉
                if (r.getBy3() != null) {
                    msg.setBy3(r.getBy3());
                } else {
                    msg.setBy3("0");    //保证列表查询没有问题
                }
                dao.update(msg);
            }
        }
    }

    /**
     * 找到全部的最近聊天历史
     *
     * @param context
     * @param myid
     * @return
     */
    @SuppressWarnings("boxing")
    public List<MessageRecent> findWithUser(Context context, long myid) {
        log(dao.getTablename(), "findWithUser(myid:" + myid + ")");
        long timeStart = System.currentTimeMillis();
//        SELECT DISTINCT M.*,ME.* FROM MESSAGE_RECENT M LEFT JOIN MESSAGE_HISTORY ME ON M.CHAT_ID=ME.CHAT_ID AND M.MYID=ME.MYID WHERE M.MYID='40409' GROUP BY M._id ORDER BY ME._id DESC,M.DATE DESC,M.UUID DESC
        List<MessageRecent> recents = new ArrayList<MessageRecent>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DISTINCT " +
                    "M.*," +
                    "C.NICK_NAME,C.COMMENT_NAME,C.AVATAR,C.IS_NEWS_NOTIFY," +
                    "G.GROUP__NICK__NAME,G.GROUP__AVATAR,G.IS__NEWS__NOTIFY," +
                    "CA.CATE_NAME,CA.CATE_AVATAR,CA.IS_HAS_CATEID,CA.DIGST,CA.BUBBLE_NUM," +
                    "S.SERVICE_NAME,S.SERVICE_AVATAR,S.BY3,S.DATE," +
                    "ME.CONTENT_TYPE,ME.CONTENT,ME.DATE,ME.SENDER,ME.UUID,ME.STATUS " +
                    "FROM MESSAGE_RECENT M " +
                    "LEFT JOIN " + ContactDao.TABLENAME + " C ON M.CHAT_ID=C.CHAT_ID AND M.MYID=C.MYID " +
                    "LEFT JOIN " + GroupDao.TABLENAME + " G ON M.CHAT_ID=G.GROUP__ID AND M.MYID=G.SELF__ID " +
                    "LEFT JOIN " + CateDao.TABLENAME + " CA ON M.CHAT_ID=CA.CATE_ID AND M.MYID=CA.MY_ID " +
                    "LEFT JOIN " + ServiceMessageRecentDao.TABLENAME + " S ON M.CHAT_ID=S.SERVICE_ID AND M.MYID=S.MYID " +
                    "LEFT JOIN (SELECT * FROM " + MessageHistoryDao.TABLENAME + " WHERE MYID=? GROUP BY CHAT_ID ORDER BY _id DESC) ME ON M.CHAT_ID=ME.CHAT_ID " +
                    "WHERE M.MYID=? GROUP BY M._id " +
                    "ORDER BY M.BY3 DESC,M.DATE DESC"
                    , new String[]{myid + "", myid + ""});

            if (cursor != null) {
                MessageRecent messageRecent;
                while (cursor.moveToNext()) {
                    messageRecent = new MessageRecent();
                    messageRecent.setMyid(myid);
                    messageRecent.setId(cursor.getLong(_id));
                    messageRecent.setChat_id(cursor.getLong(M_CHAT_ID));
                    messageRecent.setChat_type(cursor.getInt(M_CHAT_TYPE));
                    messageRecent.setDrafttext(cursor.getString(M_DRAFTTEXT));
                    messageRecent.setDraftforat(cursor.getString(M_DRAFTFORAT));
                    messageRecent.setBy1(cursor.getString(M_BY1));
                    messageRecent.setBy2(cursor.getString(M_BY2));
                    messageRecent.setBy3(cursor.getString(M_BY3));
                    messageRecent.setBy4(cursor.getString(M_BY4));
                    messageRecent.setBy5(cursor.getString(M_BY5));
                    messageRecent.setBubble_num(cursor.getInt(M_BUBBLENUM));

                    //message
                    messageRecent.setUuid(cursor.getString(ME_UUID));
                    messageRecent.setContent(cursor.getString(ME_CONTENT));
                    messageRecent.setContent_type(cursor.getString(ME_UUID) != null ? cursor.getInt(ME_CONTENT_TYPE) : IMessageConst.CONTENT_TYPE_TEXT);
                    messageRecent.setDate(cursor.getLong(M_DATE));
                    messageRecent.setSender(cursor.getLong(ME_SENDER));
                    messageRecent.setStatus(cursor.getString(ME_UUID) != null ? cursor.getInt(ME_STATUS) : IMessageConst.STATUS_HAS_SENT);

                    if (messageRecent.getChat_type() == IConst.CHAT_TYPE_PRIVATE) {      //私聊
                        messageRecent.setJumpType(MessageRecent.ITEM_JUMP_IMCHAT);
                        messageRecent.setChatName(cursor.getString(C_COMMMENT_NAME) == null || cursor.getString(C_COMMMENT_NAME).equals("") ? cursor.getString(C_NICK_NAME) : cursor.getString(C_COMMMENT_NAME));
                        messageRecent.setChatAvatar(cursor.getString(C_AVATAR));
                        messageRecent.setNotify(cursor.getInt(C_IS_NEWS_NOTIFY) == 1 ? false : true);

                    } else if (messageRecent.getChat_type() == IConst.CHAT_TYPE_GROUP) {    //群聊
                        messageRecent.setJumpType(MessageRecent.ITEM_JUMP_IMCHAT);
                        messageRecent.setChatName(cursor.getString(G_GROUP_NICK_NAME));
                        messageRecent.setChatAvatar(cursor.getString(G_AVATAR));
                        messageRecent.setNotify(cursor.getInt(G_IS_NEWS_NOTIFY) == 1 ? false : true);

                    } else if (messageRecent.getChat_type() == IConst.CHAT_TYPE_SERVICE_MESSAGE && cursor.getInt(CA_IS_HAS_CATE_ID) == Cate.CATE_HAS_CATEID) {    //服务号有cateid
                        if (cursor.getString(CA_NAME) == null){
                            messageRecent = null;
                        }else {
                            messageRecent.setJumpType(MessageRecent.ITEM_JUMP_SERVICE_LIST);
                            messageRecent.setChatName(cursor.getString(CA_NAME));
                            messageRecent.setChatAvatar(cursor.getString(CA_AVATAR));
                            messageRecent.setContent(cursor.getString(CA_DIGST) != null && !cursor.getString(CA_DIGST).equals("") ? cursor.getString(CA_DIGST) : cursor.getString(ME_CONTENT));
                            messageRecent.setBubble_num(cursor.getInt(CA_BUBBLE_NUM));
                            messageRecent.setNotify(true);
                        }
                    } else if (messageRecent.getChat_type() == IConst.CHAT_TYPE_SERVICE_MESSAGE && cursor.getInt(CA_IS_HAS_CATE_ID) == Cate.CATE_NO_CATEID) {    //服务号没有cateId
                        if (cursor.getString(S_NAME) == null){
                            messageRecent = null;
                        }else {
                            messageRecent.setJumpType(MessageRecent.ITEM_JUMP_IMCHAT);
                            messageRecent.setChatName(cursor.getString(S_NAME));
                            messageRecent.setChatAvatar(cursor.getString(S_AVATAR));
                            messageRecent.setDate(cursor.getLong(S_DATE));
                            messageRecent.setNotify(cursor.getString(S_BY3) == null || cursor.getString(S_BY3).equals("0") ? true : false);
                        }

                    }
                    if (messageRecent != null){
                        recents.add(messageRecent);
                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }

        Log.i("findRecent", "----->" + (System.currentTimeMillis() - timeStart));
        return recents;
    }

    /**
     * 找到全部的最近聊天历史，并将每条记录挂上联系人信息,剔除服务号
     *
     * @param context
     * @param myid
     * @return
     */
    @SuppressWarnings("boxing")
    public List<MessageRecent> findRecentWithUser(Context context, long myid) {
        log(dao.getTablename(), "findWithUser(myid:" + myid + ")");
        List<MessageRecent> recents = dao.queryBuilder().where(Properties.Myid.eq(myid)).orderDesc(Properties.Date).list();
        List<Long> chatIds = new ArrayList<Long>();
        for (int i = 0; i < recents.size(); i++) {
            chatIds.add(recents.get(i).getChat_id());
        }

        Map<Long, Contact> contacts = ContactDaoHelper.getInstance(context).find(myid, chatIds);
        Map<Long, Group> groups = GroupDaoHelper.getInstance(context).find(myid, chatIds);
        List<MessageRecent> removed = new ArrayList<MessageRecent>();
        for (int i = 0; i < recents.size(); i++) {
            MessageRecent r = recents.get(i);
            MessageHistory h = MessageHistoryDaoHelper.getInstance(context).findLast(myid, r.getChat_id());
            if (h != null) {
                r.setContent_type(h.getContent_type());
                r.setContent(h.getContent());
                r.setDate(h.getDate());
                r.setSender(h.getSender());
                r.setUuid(h.getUuid());
                r.setStatus(h.getStatus());
            } else {
                r.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                if (r.getChat_type() != IConst.CHAT_TYPE_SERVICE_MESSAGE)
                    r.setContent("");
                r.setStatus(IMessageConst.STATUS_HAS_SENT);
            }
            Contact c = contacts.get(r.getChat_id());
            Group group = groups.get(r.getChat_id());
            if (c != null) {
                r.setJumpType(MessageRecent.ITEM_JUMP_IMCHAT);
                r.setChatName(c.getComment_name() == null || c.getComment_name().equals("") ? c.getNick_name() : c.getComment_name());
                r.setChatAvatar(c.getAvatar());
                r.setNotify(c.getIs_news_notify() == 1 ? false : true);
            } else if (group != null) {
                r.setJumpType(MessageRecent.ITEM_JUMP_IMCHAT);
                r.setChatName(group.getGroup_nick_name());
                r.setChatAvatar(group.getGroup_avatar());
                r.setNotify(group.getIs_news_notify() == 1 ? false : true);
            } else {
                removed.add(r);
            }

        }
        recents.removeAll(removed);
        return recents;
    }

    /**
     * 找到全部的最近聊天历史，并将每条记录挂上联系人信息,剔除服务号
     *
     * @param context
     * @param myid
     * @return
     */
    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String CATE_ID = "SERVICE_ID";
    public static final int Chat_Type = 3;
    public static final int PARENT_ID = 0;

    public static final int SERVICE_ID_INDEX = 0;
    public static final int CATE_ID_INDEX = 1;

    @SuppressWarnings("boxing")
    public JSONArray findRecentWithUserList(Context context, long myid) {
        log(dao.getTablename(), "findWithUser(myid:" + myid + ")");
        List<MessageRecent> recents = findWithUser(context,myid);
        JSONArray jsonArray = new JSONArray();
        Cursor cursor= db.rawQuery("SELECT SERVICE_ID,CATE_ID FROM SERVICE_MESSAGE_RECENT WHERE CATE_ID IN(SELECT DISTINCT CHAT_ID FROM MESSAGE_RECENT WHERE MYID=" + myid + ") AND SERVICE_ID != CATE_ID AND MYID="+myid, null);
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("chat_type",Chat_Type);
                    jsonObject.put("id",cursor.getLong(SERVICE_ID_INDEX));
                    jsonObject.put("parentid",cursor.getLong(CATE_ID_INDEX));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }
        cursor.close();
        for(MessageRecent recent:recents){
            int chatType = recent.getChat_type();
            if(chatType==0){
                chatType = 1;
            }else if(chatType==1){
                chatType = 2;
            } else if(chatType==4){//只有4没有3
                if(recent.getJumpType()==recent.ITEM_JUMP_IMCHAT){
                    chatType = 3;
                }
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("chat_type",chatType);
                jsonObject.put("id",recent.getChat_id());
                jsonObject.put("parentid",PARENT_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }


    /**
     * 更新
     *
     * @param uuid
     * @param myid
     */
    @SuppressWarnings("boxing")
    public void update(String uuid, long myid, int status) {
        MessageRecent msg = find(uuid, myid);
        log(dao.getTablename(), "update(uuid:" + uuid + ",status:" + status + ")", msg);
        if (msg != null) {
            msg.setStatus(status);
            dao.update(msg);
        }
    }

    public void updateBy1(long chatid, long myid, String num) {
        MessageRecent msg = find(chatid, myid);
        if (msg != null) {
            msg.setBy1(num);
            dao.update(msg);
        }

    }

    /**
     * 置顶相关
     */
    public void updateBy3(long myId, long chatId, String by3) {
        MessageRecent msg = find(myId, chatId);
        if (msg != null) {
            msg.setBy3(by3);
            dao.update(msg);
        }

    }

    /**
     * 新的置顶相关   以后要用这个上面的要废除
     * @param chatId
     * @param myId
     * @param by3
     */
    public void updateBy3(long myId, long chatId, String by3,int chatType) {
        MessageRecent msg = find(myId, chatId,chatType);
        if (msg != null) {
            msg.setBy3(by3);
            dao.update(msg);
        }

    }

    public void updateTime(long chatid, long myid, long date) {
        MessageRecent msg = find(chatid, myid);
        if (msg != null) {
            msg.setDate(date);
            dao.update(msg);
        }
    }

    public void cleanBubble(long myid, long chat_id) {
        log(dao.getTablename(), "cleanBubble(myid:" + myid + ",chat_id:" + chat_id + ")");
        db.execSQL("UPDATE " + dao.getTablename() + " SET bubble_num=0 WHERE myid=" + myid + " AND chat_id=" + chat_id);
    }

    public void addBubble(long myid, long chat_id, int bubbleCount) {
        log(dao.getTablename(), "addBubble(myid:" + myid + ",chat_id:" + chat_id + ",bubbleCount:" + bubbleCount + ")");
        db.execSQL("UPDATE " + dao.getTablename() + " SET bubble_num=" + bubbleCount + " WHERE myid=" + myid + " AND chat_id=" + chat_id);
    }

    public int countBubble(long myid) {
        int count = 0;
        Cursor c = db.rawQuery("SELECT SUM(bubble_num) FROM " + dao.getTablename() + " WHERE myid=" + myid + " AND chat_id!=" + myid + " AND bubble_num>0", null);
        if (c != null) {
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            c.close();
        }
        log(dao.getTablename(), "countBubble(myid:" + myid + ")", count);
        return count;
    }

//    SELECT M.* FROM MESSAGE_RECENT M LEFT JOIN CONTACT C ON M.CHAT_ID=C.CHAT_ID WHERE M.MYID='40409'
}
