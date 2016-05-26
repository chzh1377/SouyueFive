package com.tuita.sdk.im.db.helper;

import android.content.Context;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.dao.GroupDao;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupExtendInfo;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.ImToCricle;

import de.greenrobot.dao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群
 */
public class GroupDaoHelper extends BaseDaoHelper<Group> {
    private static GroupDaoHelper instance;

    public static GroupDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GroupDaoHelper();
            instance.dao = getDaoSession(context).getGroupDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private GroupDao dao;

    private GroupDaoHelper() {
    }

    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

    public void deleteAll(long gid, long selfid) {
        log(dao.getTablename(), "deleteAll(gid:" + gid + ",selfid:" + selfid + ")");
//		db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE GROUP__ID=" + gid + " AND SELF__ID=" + selfid);
        db.execSQL("DELETE FROM " + dao.getTablename() + " WHERE GROUP__ID=?" + " AND SELF__ID=?", new Object[]{gid, selfid});
    }

    @SuppressWarnings("boxing")
    protected Map<Long, Group> find(long myid, List<Long> chat_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",chat_id:" + chat_id + ")");
        QueryBuilder<Group> qb = dao.queryBuilder();
        qb.where(com.tuita.sdk.im.db.dao.GroupDao.Properties.SELF_ID.eq(myid));
        if (chat_id != null) {
            qb.where(com.tuita.sdk.im.db.dao.GroupDao.Properties.GROUP_ID.in(chat_id));
        }
        Map<Long, Group> map = new HashMap<Long, Group>();
        List<Group> list = qb.list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Group c = list.get(i);
                map.put(c.getGroup_id(), c);
            }
        }
        return map;
    }

    public Group find(long myid, long group_id) {
        log(dao.getTablename(), "find(group_id:" + group_id + ")");
//		Group list = (Group) dao.queryRaw("WHERE GROUP_ID=?", group_id+"");
//		return list != null  ? list : null;
        List<Group> list = dao.queryBuilder().where(GroupDao.Properties.SELF_ID.eq(myid), com.tuita.sdk.im.db.dao.GroupDao.Properties.GROUP_ID.eq(group_id)).list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 按照myid和keywrod模糊查询，keyword可能为备注名，local order或昵称
     *
     * @return
     */
    @SuppressWarnings("boxing")
    public List<Group> findLike(long myid, String localOrder) {
        log(dao.getTablename(), "findLike(myid:" + myid + ",localOrder:" + localOrder + ")");
        if (localOrder.equals("%")) {
            return null;
        } else {
            return dao.queryBuilder().where(GroupDao.Properties.SELF_ID.eq(myid), HelperUtils.like(GroupDao.Properties.BY1, localOrder.toUpperCase())).orderAsc(GroupDao.Properties.BY1).list();
        }
    }

    @SuppressWarnings("boxing")
//	public List<Group> findAll(long myid) {
//		log(dao.getTablename(), "findAll(myid:" + myid + ")");
//		return dao.queryBuilder().where(Properties.Myid.eq(myid)).orderAsc(Properties.Local_order).list();
//	}
    /**
     * 插入或更新
     * @param groups
     */
    public void save(List<Group> groups) {
        log(dao.getTablename(), "save(groups:" + groups + ")");
        if (groups != null)
            for (int i = 0; i < groups.size(); i++) {
                save(groups.get(i));
            }
    }

    /**
     * 插入或更新
     *
     * @param group
     */
    @SuppressWarnings("boxing")
    public void save(Group group) {
        log(dao.getTablename(), "save(group:" + group + ")");
        if (group != null) {
            Group result = find(group.getSelf_id(), group.getGroup_id());
            if (result == null) {
                log(dao.getTablename(), "insert(group:" + group + ")");
                group.setBy1(genLocal_order(group));
                dao.insert(group);
            } else {
                group.setId(result.getId());
                log(dao.getTablename(), "update(group:" + group + ")");
                group.setBy1(genLocal_order(group));
                dao.update(group);
            }
        }
    }


    public void saveList(List<Group> groups){
        dao.insertOrReplaceInTx(groups);
    }

//	
//	private String genLocal_order(Group group) {
//		if (group.get == null || group.getComment_name().length() == 0) {
//			return PingYinUtil.conver2SqlRow(group.getNick_name());
//		} else {
//			return PingYinUtil.conver2SqlRow(group.getComment_name() + " " + contact.getNick_name());
//		}
//	}
//	
//	/**
//	 * 更新头像
//	 * @param myid
//	 * @param chat_id
//	 * @param avatar
//	 */
//	public void updateAvatar(long myid, long chat_id, String avatar) {
//		Group result = find(myid, chat_id);
//		log(dao.getTablename(), "updateAvatar(myid:" + myid + ",chat_id:" + chat_id + ",avatar:" + avatar + ")", result);
//		if (result != null) {
//			result.setAvatar(avatar);
//			dao.update(result);
//		}
//	}

    /**
     * 更新群昵称
     *
     * @param comment_name
     */
    public void updateCommentName(long myid, long group_id, String comment_name) {
        log(dao.getTablename(), "updateCommentName(group_id:" + group_id + ",comment_name:" + comment_name + ")");
        Group result = find(myid, group_id);
        if (result != null) {
            result.setGroup_nick_name(comment_name);
            result.setBy1(genLocal_order(result));
            dao.update(result);
        }
    }

    /**
     * 得到群头像
     *
     * @param avatar
     */
    public void updateGroupAvatar(long myid, long group_id, String avatar, String GroupName) {
        log(dao.getTablename(), "updateCommentName(group_id:" + group_id + ",avatar:" + avatar + ")");
        Group result = find(myid, group_id);
        if (result != null) {
            result.setGroup_avatar(avatar);
            result.setGroup_nick_name(GroupName);
            dao.update(result);
        }
    }

    /**
     * 获取保存或未保存的群
     *
     * @param group_id
     * @param issaved  1  保存的群  0 未保存的群
     * @return
     */
    public List<Group> db_findGroupListByGroupidAndIsSaved(long group_id, int issaved) {
        List<Group> list = dao.queryBuilder().where(GroupDao.Properties.SELF_ID.eq(group_id), GroupDao.Properties.IS_GROUP_SAVED.eq(issaved)).list();
        return list != null && list.size() > 0 ? list : null;
    }

    /**
     * 根据userid获取所有的群
     *
     * @param userid
     * @return
     */
    public List<Group> db_findGroupListByUserid(long userid) {
        List<Group> list = dao.queryBuilder().where(GroupDao.Properties.SELF_ID.eq(userid)).list();
        return list != null && list.size() > 0 ? list : null;
    }

    private String genLocal_order(Group group) {
        if (group.getGroup_nick_name() != null || group.getGroup_nick_name().length() > 0) {
            return PingYinUtil.conver2SqlRow(group.getGroup_nick_name());
        } else {

            return "";
        }
    }

    /**
     * update 群信息 用于100  109
     *
     * @param data
     * @param manager
     * @throws Exception
     */
    public void updateGroup(JSONObject data, TuitaIMManager manager) throws Exception {
        if (data.has("groups")) {
            Group group = null;
            List<Group> groups = new ArrayList<Group>();
            JSONArray groupsObj = data.getJSONArray("groups");
            for (int i = 0, m = groupsObj.length(); i < m; i++) {
                JSONObject groupObj = groupsObj.getJSONObject(i);
                if (groupObj.getInt("s") == IConst.CONTACT_DEL) {
                    GroupDaoHelper.getInstance(manager.getManager().getContext()).deleteAll(groupObj.getLong("gid"), manager.getOwner().getUid());
                    MessageRecentDaoHelper.getInstance(manager.getManager().getContext()).delete(manager.getOwner().getUid(), groupObj.getLong("gid"),IConst.CHAT_TYPE_GROUP);
//                        continue;
                } else {
                    group = find(manager.getOwner().getUid(),groupObj.getLong("gid"));
                    if (group != null){
                        deleteAll(groupObj.getLong("gid"),manager.getOwner().getUid());
                        group = null;
                    }
                    group = new Group();
                    group.setGroup_id(groupObj.getLong("gid"));
                    group.setGroup_nick_name(groupObj.getString("nick"));
                    if (groupObj.has("avatar")) {
                        group.setGroup_avatar(groupObj.getString("avatar"));
                    }
                    group.setOwner_id(groupObj.getLong("ownerId"));
                    manager.getGroupOwnerId().put(groupObj.getLong("gid"), groupObj.getLong("ownerId"));
                    group.setSelf_id(manager.getOwner().getUid());
                    group.setMax_numbers(groupObj.getInt("maxMembers"));
                    if (groupObj.getBoolean("isNickSet")) {
                        group.setIs_nick_set(1);
                    } else {
                        group.setIs_nick_set(0);
                    }

                    if (groupObj.getBoolean("isGroupSaved")) {
                        group.setIs_group_saved(1);
                    } else {
                        group.setIs_group_saved(0);
                    }

                    if (groupObj.getBoolean("isNewsNotifyShielded")) {
                        group.setIs_news_notify(1);
                    } else {
                        group.setIs_news_notify(0);
                    }
                    if(groupObj.has("extendInfo"))
                    {
                        JSONObject extendObj = getJsonObject(groupObj, "extendInfo");
                        group.setExtendInfo(dealWithExtendInfo(extendObj));
                    }
                    group.setSelf_id(manager.getOwner().getUid());
                    group.setBy1(genLocal_order(group));
                    groups.add(group);
                }
            }
           saveList(groups);
            groups.clear();
        }
    }


    /**
     * 处理组中的 添加的额外字段
     * @param extendObj
     * @return
     * @throws JSONException
     */
    private GroupExtendInfo dealWithExtendInfo(JSONObject extendObj)
    {
        GroupExtendInfo extendInfo = new GroupExtendInfo();
        try {
            if(extendObj!=null)
            {
                JSONArray circleJsonList =getJsonArray(extendObj,"circle_boundCircleList");
                if(circleJsonList!=null)
                {
                    JSONObject circleJsonObj;
                    ImToCricle imToCricle;
                    List<ImToCricle>  imToCricleList = new ArrayList<ImToCricle>();
                    for (int i=0;i<circleJsonList.length();i++)
                    {
                        circleJsonObj = circleJsonList.getJSONObject(i);
                        imToCricle = new ImToCricle();
                        imToCricle.setKeyword(circleJsonObj.getString("keyword"));
                        imToCricle.setSrpId(circleJsonObj.getString("srpId"));
                        imToCricle.setInterestId(circleJsonObj.getLong("interestId"));
                        imToCricle.setInterestLogo(circleJsonObj.getString("interestLogo"));
                        imToCricle.setInterestName(circleJsonObj.getString("interestName"));
                        imToCricle.setType(circleJsonObj.getInt("type"));
                        imToCricleList.add(imToCricle);
                    }
                    if(imToCricleList.size()>0)
                    {
                        extendInfo.setCircle_boundCircleList(imToCricleList);
                    }
                }
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return  extendInfo;
    }
    private JSONArray getJsonArray(JSONObject json, String key) {
        try {
            return json.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }
    private JSONObject getJsonObject(JSONObject json, String key) {
        try {
            return json.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }
}
