package com.tuita.sdk.im.db.helper;

import android.content.Context;
import android.database.Cursor;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.dao.GroupMembersDao;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IConst;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 群成员
 */
public class GroupMembersDaoHelper extends BaseDaoHelper<Group> {
    private static GroupMembersDaoHelper instance;

    public static GroupMembersDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GroupMembersDaoHelper();
            instance.dao = getDaoSession(context).getGroupMembersDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    public GroupMembersDao dao;

    public GroupMembersDaoHelper() {
    }

    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

    public void deleteAll(long gid, long self_id) {
        log(dao.getTablename(), "deleteAll(gid:" + gid + ",self_id:" + self_id
                + ")");
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE GROUP__ID="
                + gid + " AND SELF__ID=" + self_id);
    }

    public void deleteBygr(long myid, long uid, long gid) {
        log(dao.getTablename(), "deleteAll(uid:" + uid + ")");
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE MEMBER__ID="
                + uid + " AND GROUP__ID=" + gid + " AND SELF__ID=" + myid);
    }

    // @SuppressWarnings("boxing")
    // protected Map<Long, Group> find(long myid, List<Long> chat_id) {
    // log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id +
    // ")");
    // QueryBuilder<Group> qb = dao.queryBuilder();
    // qb.where(com.tuita.sdk.im.db.dao.MessageHistoryDao.Properties.Myid.eq(myid));
    // if (chat_id != null) {
    // qb.where(com.tuita.sdk.im.db.dao.MessageHistoryDao.Properties.Chat_id.in(chat_id));
    // }
    // Map<Long, Group> map = new HashMap<Long, Group>();
    // List<Group> list = qb.list();
    // if (list != null) {
    // for (int i = 0; i < list.size(); i++) {
    // Group c = list.get(i);
    // map.put(c.getChat_id(), c);
    // }
    // }
    // return map;
    // }
//	public  GroupMembers find(long myid, long members_id) {
//		log(dao.getTablename(), "find(group_id:" + members_id + ")");
//		// List<GroupMembers> list = dao.queryRaw("WHERE MEMBER_ID=?",
//		// members_id + "");
//		List<GroupMembers> list = dao
//				.queryBuilder()
//				.where(com.tuita.sdk.im.db.dao.GroupMembersDao.Properties.MEMBER_ID
//						.eq(members_id), GroupMembersDao.Properties.SELF_ID.eq(myid)).list();
//		return list != null && list.size() > 0 ? list.get(0) : null;
//	}

    /**
     * 根据userid和members_id 查询我的群昵称
     *
     * @param members_id
     * @param group_id
     * @return
     */
    public GroupMembers find(long myid, long group_id, long members_id) {
        log(dao.getTablename(), "find(group_id:" + members_id + ")");
        List<GroupMembers> list = dao
                .queryBuilder()
                .where(GroupMembersDao.Properties.MEMBER_ID.eq(members_id),
                        GroupMembersDao.Properties.GROUP_ID.eq(group_id), GroupMembersDao.Properties.SELF_ID.eq(myid))
                .list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据members_id 查询所有群昵称
     *
     * @param myid
     * @param members_id
     * @return
     */
    public List<GroupMembers> findBymemberId(long myid, long members_id) {
        log(dao.getTablename(), "find(members_id:" + members_id + ")");
        List<GroupMembers> list = dao
                .queryBuilder()
                .where(GroupMembersDao.Properties.MEMBER_ID.eq(members_id), GroupMembersDao.Properties.SELF_ID.eq(myid))
                .list();
        return list != null && list.size() > 0 ? list : null;
    }

    /**
     * 根据group_id查询member
     *
     * @param group_id
     * @return
     */
    public List<GroupMembers> findMemebers(long myid, long group_id) {
        System.out.println("---->DAO1"+System.currentTimeMillis());
        List<GroupMembers> list = dao.queryBuilder().where(GroupMembersDao.Properties.GROUP_ID.eq(group_id), GroupMembersDao.Properties.SELF_ID.eq(myid)).list();
        System.out.println("---->DAO2"+System.currentTimeMillis());
        return list != null && list.size() > 0 ? list : null;
    }

    // @SuppressWarnings("boxing")
    // public List<GroupMembers> findAll(long myid) {
    // log(dao.getTablename(), "findAll(myid:" + myid + ")");
    // return
    // dao.queryBuilder().where(Properties.Myid.eq(myid)).orderAsc(Properties.Local_order).list();
    // }

    /**
     * 插入或更新
     *
     * @param groupMembers
     */
    public void save(List<GroupMembers> groupMembers) {
        log(dao.getTablename(), "save(groupMembers:" + groupMembers + ")");
        if (groupMembers != null)
            for (int i = 0; i < groupMembers.size(); i++) {
                save(groupMembers.get(i));
            }
    }

    /**
     * 插入或更新
     *
     * @param groupMembers
     */
    @SuppressWarnings("boxing")
    public void save(GroupMembers groupMembers) {
        log(dao.getTablename(), "save(groupMembers:" + groupMembers + ")");
        if (groupMembers != null) {
            GroupMembers result = find(groupMembers.getSelf_id(), groupMembers.getGroup_id(),
                    groupMembers.getMember_id());
            if (result == null) {
                log(dao.getTablename(), "insert(groupMembers:" + groupMembers
                        + ")");
                dao.insert(groupMembers);
            } else {
                groupMembers.setId(result.getId());
                log(dao.getTablename(), "update(groupMembers:" + groupMembers
                        + ")");
                dao.update(groupMembers);
            }
        }
    }

    public void saveList(List<GroupMembers> memberses){
        dao.insertOrReplaceInTx(memberses);
    }

    //
    // private String genLocal_order(Group group) {
    // if (group.get == null || group.getComment_name().length() == 0) {
    // return PingYinUtil.conver2SqlRow(group.getNick_name());
    // } else {
    // return PingYinUtil.conver2SqlRow(group.getComment_name() + " " +
    // contact.getNick_name());
    // }
    // }
    //

    /**
     * 更新头像或名称
     *
     * @param members_id
     * @param myid
     * @param avatar
     */
    public void updateAvatar(long myid, long members_id, String avatar, String name) {
        List<GroupMembers> result = findBymemberId(myid, members_id);
        log(dao.getTablename(), "updateAvatar(members_id:" + members_id
                + ",members_id:" + members_id + ",avatar:" + avatar + ")", result);
        if (result != null && result.size() >= 0) {
            for (int i = 0; i < result.size(); i++) {
                if (!"".equals(avatar)) {
                    result.get(i).setMember_avatar(avatar);
                }
                if (!"".equals(name)) {
                    result.get(i).setNick_name(name);
                }

                dao.update(result.get(i));
            }
        }
    }

    /**
     * 更新群主
     *
     * @param members_id
     * @param group_id
     */
    public void updateOwner(long myid, long members_id, long group_id) {
        GroupMembers result = find(myid, group_id, members_id);
        log(dao.getTablename(), "updateAvatar(members_id:" + members_id
                + ",group_id:" + group_id + ")", result);
        if (result != null) {
            result.setIs_owner(1);
            dao.update(result);
        }
    }

    /**
     * 根据groupid查找群人数
     *
     * @param group_id
     * @return
     */
    public long findMemberCountByGroupid(long group_id, long myid) {
        long count = dao.queryBuilder().where(GroupMembersDao.Properties.SELF_ID.eq(myid), GroupMembersDao.Properties.GROUP_ID.eq(group_id)).count();
        return count;

    }

    /**
     * 更新成员昵称
     *
     * @param members_id
     * @param group_id
     * @param comment_name
     */
    public void updateCommentName(long myid, long members_id, long group_id,
                                  String comment_name) {
        log(dao.getTablename(), "updateCommentName(members_id:" + members_id
                + ",group_id:" + group_id + ",comment_name:" + comment_name
                + ")");
        GroupMembers result = find(myid, group_id, members_id);
        if (result != null) {
            result.setMember_name(comment_name);
            dao.update(result);
        }
    }

    public String[] getFinalName(long myid, long gid, long memberid) {
        Cursor c = db.rawQuery("SELECT G.NICK__NAME,G.MEMBER__NAME,G.MEMBER__AVATAR,C.COMMENT_NAME FROM GROUP_MEMBERS G LEFT JOIN CONTACT C ON G.MEMBER__ID=C.CHAT_ID AND G.SELF__ID=C.MYID WHERE G.GROUP__ID=? AND G.MEMBER__ID=? AND G.SELF__ID=?", new String[]{gid + "", memberid + "", myid + ""});
        String name = "";
        String avatar = "";
        if (c != null) {
            if (c.moveToFirst()) {
                c.getColumnCount();
                avatar = c.getString(2);
                if (c.getString(3) == null || "".equals(c.getString(3))) {
                    if (c.getString(1) == null || "".equals(c.getString(1))) {
                        name = c.getString(0);
                    } else {
                        name = c.getString(1);
                    }
                } else {
                    name = c.getString(3);
                }
            }

        }
        return new String[]{name, avatar};
    }

    /**
     * 更新群成员的信息  用于100   109
     *
     * @param data
     * @param manager
     * @throws Exception
     */
    public void updateGroupMembers(JSONObject data, TuitaIMManager manager) throws Exception {
        JSONObject memberObj;
        if (data.has("members")) {
            JSONArray membersObj = data.getJSONArray("members");
            GroupMembers memmbers = null;
            List<GroupMembers> membersList = new ArrayList<GroupMembers>();
//            memberObj = null;
            for (int i = 0, m = membersObj.length(); i < m; i++) {
                memberObj = membersObj.getJSONObject(i);
                if (memberObj.getInt("s") == IConst.CONTACT_DEL) {
//                        GroupMembersDaoHelper.getInstance(this.getManager().getContext()).deleteBygr(this.getOwner().getUid(),memberObj.getLong("uid"),memberObj.getLong("gid"));
                    continue;
                } else {
                   memmbers = find(manager.getOwner().getUid(),memberObj.getLong("gid"),memberObj.getLong("uid"));
                    if (memmbers != null){
                        deleteBygr(manager.getOwner().getUid(),memberObj.getLong("uid"),memberObj.getLong("gid"));
                        memmbers = null;
                    }

                    memmbers = new GroupMembers();
                    if (manager.getGroupOwnerId().containsKey(memberObj.getLong("gid"))) {
                        if (memberObj.getLong("uid") == manager.getGroupOwnerId().get(memberObj.getLong("gid"))) {
                            memmbers.setIs_owner(1);
                        } else {
                            memmbers.setIs_owner(0);
                        }
                    }
                    memmbers.setGroup_id(memberObj.getLong("gid"));
                    memmbers.setMember_id(memberObj.getLong("uid"));
                    memmbers.setNick_name(memberObj.getString("nick"));
                    if (memberObj.has("memberNick")) {
                        memmbers.setMember_name(memberObj
                                .getString("memberNick"));
                    }
                    if (memberObj.has("avatar")) {
                        memmbers.setMember_avatar(memberObj
                                .getString("avatar"));
                    }
                    memmbers.setSelf_id(manager.getOwner().getUid());
//                    GroupMembersDaoHelper.getInstance(
//                            manager.getManager().getContext()).save(memmbers);
//                        groupOwnerId.remove(memberObj.getLong("gid"));
                    membersList.add(memmbers);
                }
            }

            saveList(membersList);
            membersList.clear();
            manager.getGroupOwnerId().clear();
        }
    }

}
