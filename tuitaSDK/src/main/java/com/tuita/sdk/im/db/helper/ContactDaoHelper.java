package com.tuita.sdk.im.db.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.dao.ContactDao;
import com.tuita.sdk.im.db.dao.ContactDao.Properties;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.IConst;
import de.greenrobot.dao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 联系人
 *
 * @author wanglong@zhongsou.com
 */
public class ContactDaoHelper extends BaseDaoHelper<Contact> {
    private static ContactDaoHelper instance;

    public static ContactDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ContactDaoHelper();
            instance.dao = getDaoSession(context).getContactDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private ContactDao dao;

    private ContactDaoHelper() {
    }

    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

    public void deleteByUid(long chatId, long myid) {
        log(dao.getTablename(), "deleteAll(uid:" + chatId + ",myid:" + myid
                + ")");
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE chat_id=?" + " AND myid=?", new Object[]{chatId, myid});
    }

    @SuppressWarnings("boxing")
    protected Map<Long, Contact> find(long myid, List<Long> chat_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id + ")");
        QueryBuilder<Contact> qb = dao.queryBuilder();
        qb.where(com.tuita.sdk.im.db.dao.MessageHistoryDao.Properties.Myid.eq(myid));
        if (chat_id != null) {
            qb.where(com.tuita.sdk.im.db.dao.MessageHistoryDao.Properties.Chat_id.in(chat_id));
        }
        Map<Long, Contact> map = new HashMap<Long, Contact>();
        List<Contact> list = qb.list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Contact c = list.get(i);
                map.put(c.getChat_id(), c);
            }
        }
        return map;
    }

    public Contact find(long myid, long chat_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id + ")");
        List<Contact> list = dao.queryRaw("WHERE myid=? AND chat_id=? AND chat_type=?", myid + "", chat_id + "", 0 + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @SuppressWarnings("boxing")
    public List<Contact> findAll(long myid) {
        log(dao.getTablename(), "findAll(myid:" + myid + ")");
        return dao.queryBuilder().where(Properties.Myid.eq(myid)).orderAsc(Properties.Local_order).list();
    }

    /**
     * 插入或更新
     *
     * @param contacts
     */
    public void save(List<Contact> contacts) {
        log(dao.getTablename(), "save(contacts:" + contacts + ")");
        if (contacts != null)
            for (int i = 0; i < contacts.size(); i++) {
                save(contacts.get(i));
            }
    }

    /**
     * 插入或更新
     *
     * @param contact
     */
    @SuppressWarnings("boxing")
    public void save(Contact contact) {
        log(dao.getTablename(), "save(contact:" + contact + ")");
        if (contact != null) {
            Contact result = find(contact.getMyid(), contact.getChat_id());
            if (result == null) {
                log(dao.getTablename(), "insert(contact:" + contact + ")");
                contact.setLocal_order(genLocal_order(contact));
                dao.insert(contact);
            } else {
                contact.setId(result.getId());
                log(dao.getTablename(), "update(contact:" + contact + ")");
                contact.setLocal_order(genLocal_order(contact));
                dao.update(contact);
            }
        }
    }

    private String genLocal_order(Contact contact) {
        if (contact.getComment_name() == null || contact.getComment_name().length() == 0) {
            return PingYinUtil.conver2SqlRow(contact.getNick_name());
        } else {
            return PingYinUtil.conver2SqlRow(contact.getComment_name() + " " + contact.getNick_name());
        }
    }

    /**
     * 更新头像
     *
     * @param myid
     * @param chat_id
     * @param avatar
     */
    public void updateAvatar(long myid, long chat_id, String avatar) {
        Contact result = find(myid, chat_id);
        log(dao.getTablename(), "updateAvatar(myid:" + myid + ",chat_id:" + chat_id + ",avatar:" + avatar + ")", result);
        if (result != null) {
            result.setAvatar(avatar);
            dao.update(result);
        }
    }

    /**
     * 更新备注名
     *
     * @param myid
     * @param chat_id
     * @param comment_name
     */
    public void updateCommentName(long myid, long chat_id, String comment_name) {
        log(dao.getTablename(), "updateCommentName(myid:" + myid + ",chat_id:" + chat_id + ",comment_name:" + comment_name + ")");
        Contact result = find(myid, chat_id);
        if (result != null) {
            result.setComment_name(comment_name);
            dao.update(result);
        }
    }

    /**
     * 查询备注名
     */
    public String findCommentName(long myid, long chat_id) {
        log(dao.getTablename(), "findCommentName(myid:" + myid + ",chat_id:" + chat_id + ")");
        Contact result = find(myid, chat_id);
        if (result != null) {
            return result.getComment_name();
        }
        return "";
    }

    /**
     * 按照myid和keywrod模糊查询，keyword可能为备注名，local order或昵称
     *
     * @return
     */
    @SuppressWarnings("boxing")
    public List<Contact> findLike(long myid, String localOrder) {
        log(dao.getTablename(), "findLike(myid:" + myid + ",localOrder:" + localOrder + ")");
        if (localOrder.equals("%")) {
            return null;
        } else {
            return dao.queryBuilder().where(Properties.Myid.eq(myid), HelperUtils.like(Properties.Local_order, localOrder.toUpperCase())).orderAsc(Properties.Local_order).list();
        }
    }

    /**
     * 根据userid 更新昵称和头像
     */
    public void updateNickandAvatar(long myid, long chat_id, String nickname, String avatar) {
        Contact result = find(myid, chat_id);
        log(dao.getTablename(), "updateAvatar(myid:" + myid + ",chat_id:" + chat_id + ",avatar:" + avatar + ")", result);
        if (result != null) {
            if (!TextUtils.isEmpty(nickname)) {
                result.setNick_name(nickname);
            }
            if (!TextUtils.isEmpty(avatar)) {
                result.setAvatar(avatar);
            }
            dao.update(result);
        }
    }

    /**
     * 根据userid 更新消息提醒
     */
    public void updateNewsNotify(long myid, long chat_id, int is_news_notify) {
        Contact result = find(myid, chat_id);
        log(dao.getTablename(), "updateAvatar(myid:" + myid + ",chat_id:" + chat_id + ")", result);
        if (result != null) {
            result.setIs_news_notify(is_news_notify);
            dao.update(result);
        }
    }


    public void saveList(List<Contact> contacts){
        dao.insertOrReplaceInTx(contacts);
    }
    /**
     * 更新联系人的信息 用于100   109
     *
     * @param data
     * @param manager
     * @throws Exception
     */
    public void updateContact(JSONObject data, TuitaIMManager manager) throws Exception {
        JSONArray usersObj = data.getJSONArray("users");
        Contact contact = null;
        JSONObject userObj = null;
        List<Contact> contacts = new ArrayList<Contact>();
        long chat_id = 0;
        boolean hasDel = false;
        for (int i = 0, m = usersObj.length(); i < m; i++) {
            userObj = usersObj.getJSONObject(i);
            /**
             * private Long myid; private Long chat_id; private Integer
             * chat_type; private String comment_name; private String
             * nick_name; private String avatar;
             */
            chat_id = userObj.getLong("uid");
            if (userObj.getInt("s") == IConst.CONTACT_DEL) {
                // NewFriend nf =
                // NewFriendDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(),
                // chat_id);
                // // if (nf != null) {
                // //
                // NewFriendDaoHelper.getInstance(this.getManager().getContext()).delete(nf.getId());
                // // }
                // contact =
                // ContactDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(),
                // chat_id);
                // if (contact != null) {
                // ContactDaoHelper.getInstance(this.getManager().getContext()).delete(contact.getId());
                // }
                // MessageRecent recent =
                // MessageRecentDaoHelper.getInstance(this.getManager().getContext()).find(this.getOwner().getUid(),
                // chat_id);
                // if (recent != null) {
                // MessageRecentDaoHelper.getInstance(this.getManager().getContext()).delete(recent.getId());
                // MessageHistoryDaoHelper.getInstance(this.getManager().getContext()).deleteAll(this.getOwner().getUid(),
                // chat_id);
                // hasDel = true;
                // }
                continue;
//                    ContactDaoHelper.getInstance(this.getManager().getContext()).deleteByUid(chat_id,this.getOwner().getUid());
//                    MessageRecentDaoHelper.getInstance(this.getManager().getContext()).delete(this.getOwner().getUid(), chat_id);
            } else {
                contact = find(manager.getOwner().getUid(),chat_id);
                if (contact != null){
                    deleteByUid(chat_id,manager.getOwner().getUid());
                    contact = null;
                }
                contact = new Contact();
                contact.setMyid(manager.getOwner().getUid());
                contact.setChat_id(chat_id);
                contact.setChat_type(IConst.CHAT_TYPE_PRIVATE);
                contact.setNick_name(manager.getJsonString(userObj, "nick"));
                contact.setAvatar(manager.getJsonString(userObj, "avatar"));
                contact.setComment_name(manager.getJsonString(userObj, "alias"));
                if (userObj.has("isNewsNotifyShielded")){
                    contact.setIs_news_notify(userObj.getBoolean("isNewsNotifyShielded") ? 1 : 0);
                }
                if (!"".equals(manager.getJsonString(userObj, "pn"))) {
                    contact.setPhone(manager.getJsonString(userObj, "pn"));
                    if (!"".equals(manager.getJsonString(userObj, "am"))
                            && manager.getJsonString(userObj, "am").equals(
                            TuitaIMManager.TUITA_IM_SYSMSG_TYPE_IM)) {
                        contact.setBy2(IConst.CONTACT_PHONE_RECOMMEND);
                    } else {
                        contact.setBy2(IConst.CONTACT_PHONE_MATCHING);
                    }
                }
                contact.setLocal_order(genLocal_order(contact));
                contacts.add(contact);
//                ContactDaoHelper
//                        .getInstance(manager.getManager().getContext()).save(
//                        contact);

            }
        }
        saveList(contacts);
        contacts.clear();

    }

}
