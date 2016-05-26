package com.tuita.sdk.im.db.helper;

import android.content.Context;
import android.database.Cursor;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.dao.ServiceMessageRecentDao;
import com.tuita.sdk.im.db.module.*;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务号最近聊天
 */
public class ServiceMessageRecentDaoHelper extends BaseDaoHelper<Group> {
    private static ServiceMessageRecentDaoHelper instance;

    public static ServiceMessageRecentDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceMessageRecentDaoHelper();
            instance.dao = getDaoSession(context).getServiceMessageRecentDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private ServiceMessageRecentDao dao;

    private ServiceMessageRecentDaoHelper() {
    }

    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

    /**
     * 根据myid和cateid删除serRecent表里数据
     *
     * @param myId
     * @param cateId
     */
    public void deleteByCateId(long myId, long cateId) {
        QueryBuilder<ServiceMessageRecent> qb = dao.queryBuilder();
        DeleteQuery<ServiceMessageRecent> bd = qb.where(ServiceMessageRecentDao.Properties.Myid.eq(myId), ServiceMessageRecentDao.Properties.Cate_id.eq(cateId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据myid和srvId删除serRecent表里数据
     *
     * @param myId
     */
    public void deleteByServId(long myId, long srvId, long cateId) {
        QueryBuilder<ServiceMessageRecent> qb = dao.queryBuilder();
        DeleteQuery<ServiceMessageRecent> bd = qb.where(ServiceMessageRecentDao.Properties.Myid.eq(myId), ServiceMessageRecentDao.Properties.Cate_id.eq(cateId), ServiceMessageRecentDao.Properties.Service_id.eq(srvId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    @SuppressWarnings("boxing")
    protected Map<Long, ServiceMessageRecent> find(long myid, List<Long> cate_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",cate_id:" + cate_id + ")");
        QueryBuilder<ServiceMessageRecent> qb = dao.queryBuilder();
        qb.where(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Myid.eq(myid));
        if (cate_id != null) {
            qb.where(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Cate_id.in(cate_id));
        }
        Map<Long, ServiceMessageRecent> map = new HashMap<Long, ServiceMessageRecent>();
        List<ServiceMessageRecent> list = qb.orderAsc(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Date).list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ServiceMessageRecent c = list.get(i);
                map.put(c.getCate_id(), c);
            }
        }
        return map;
    }

    public ServiceMessageRecent findWithCate(long myid, long service_id, long cate_id) {
        log(dao.getTablename(), "find(service_id:" + service_id + ")");
//		List<ServiceMessageRecent> list = dao.queryBuilder().where(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Service_id.eq(service_id)).list();
        List<ServiceMessageRecent> list = dao.queryRaw("WHERE cate_id =? AND service_id=? AND myid=?", cate_id + "", service_id + "", myid + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public ServiceMessageRecent find(long myid, long service_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",service_id:" + service_id + ")");
        List<ServiceMessageRecent> list = dao.queryRaw("WHERE myid =? AND service_id=?", myid + "", service_id + "");
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    //	@SuppressWarnings("boxing")
    public List<ServiceMessageRecent> findAll(long myid, long cateId) {
        log(dao.getTablename(), "findAll(cateId:" + cateId + ")");
        return dao.queryBuilder().where(ServiceMessageRecentDao.Properties.Myid.eq(myid), com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Cate_id.eq(cateId)).orderDesc(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Date).list();
    }

    /**
     * 查询不是当前serviceI的服务号
     *
     * @param myid
     * @param cateId
     * @return
     */
    public List<ServiceMessageRecent> findAllNoEqCurrentSer(long myid, long cateId, long serviceId) {
        log(dao.getTablename(), "findAll(cateId:" + cateId + ")");
        return dao.queryBuilder().where(ServiceMessageRecentDao.Properties.Myid.eq(myid), com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Cate_id.eq(cateId), ServiceMessageRecentDao.Properties.Service_id.notEq(serviceId)).orderDesc(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Date).list();
    }

    /**
     * 获取cate里面最上面一条服务号
     *
     * @param myid
     * @param cateId
     * @return
     */
    private ServiceMessageRecent findByCateIdFirst(long myid, long cateId, long serviceId) {
        List<ServiceMessageRecent> cateList = findAllNoEqCurrentSer(myid, cateId, serviceId);
        return cateList != null && cateList.size() > 0 ? cateList.get(0) : null;
    }


    /**
     * 查询所有数据
     *
     * @return
     */
    public List<ServiceMessageRecent> loadAll() {
        return dao.queryBuilder().list();
    }

    //
    @SuppressWarnings("boxing")
    public List<ServiceMessageRecent> findByMyid(long myid) {
        log(dao.getTablename(), "findByMyid(myid:" + myid + ")");
        return dao.queryBuilder().where(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Myid.eq(myid)).orderDesc(com.tuita.sdk.im.db.dao.ServiceMessageRecentDao.Properties.Date).list();
    }

    /**
     * 插入或更新
     *
     * @param serviceMessageRes
     */
    public void save(List<ServiceMessageRecent> serviceMessageRes) {
        log(dao.getTablename(), "save(serviceMessageRes:" + serviceMessageRes + ")");
        if (serviceMessageRes != null)
            for (int i = 0; i < serviceMessageRes.size(); i++) {
                save(serviceMessageRes.get(i));
            }
    }

    /**
     * 插入或更新
     *
     * @param serviceMessageRes
     */
    @SuppressWarnings("boxing")
    public void save(ServiceMessageRecent serviceMessageRes) {
        log(dao.getTablename(), "save(serviceMessageRes:" + serviceMessageRes + ")");
        if (serviceMessageRes != null) {
            ServiceMessageRecent result = find(serviceMessageRes.getMyid(), serviceMessageRes.getService_id());
            if (result == null) {
                log(dao.getTablename(), "insert(serviceMessageRes:" + serviceMessageRes + ")");
                dao.insert(serviceMessageRes);
            } else {
                if (result.getBy1() != null && result.getBy1().equals(serviceMessageRes.getBy1())) {
                    serviceMessageRes.setBubble_num(Integer.parseInt(serviceMessageRes.getBubble_num()) - 1 + "");
                }
                serviceMessageRes.setId(result.getId());
                log(dao.getTablename(), "update(serviceMessageRes:" + serviceMessageRes + ")");
                dao.update(serviceMessageRes);
            }
        }
    }


    public void cleanBubble(long myid, long serviceId) {
        log(dao.getTablename(), "cleanBubble(myid:" + myid + ",serviceId:" + serviceId + ")");
        db.execSQL("UPDATE " + dao.getTablename() + " SET bubble_num=0 WHERE myid=" + myid + " AND service_id=" + serviceId);
    }

    public void addBubble(long myid, long serviceId, int bubbleCount) {
        log(dao.getTablename(), "addBubble(myid:" + myid + ",serviceId:" + serviceId + ",bubbleCount:" + bubbleCount + ")");
        db.execSQL("UPDATE " + dao.getTablename() + " SET bubble_num=" + bubbleCount + " WHERE myid=" + myid + " AND serviceId=" + serviceId);
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

    private void update(ServiceMessageRecent serviceMessageRecent) {
        dao.update(serviceMessageRecent);
    }

    /**
     * 根据userid 更新消息提醒
     */
    public void updateNewsNotify(long myid, long chat_id, int is_news_notify) {
        ServiceMessageRecent result = find(myid, chat_id);
        log(dao.getTablename(), "updateAvatar(myid:" + myid + ",chat_id:" + chat_id + ")", result);
        if (result != null) {
            result.setBy3(is_news_notify + "");
            dao.update(result);
        }
    }

    /**
     * 更新服务号消息   用于100  109  201
     *
     * @param data
     * @param manager
     * @throws Exception
     */
    public void updateServiceMsg(JSONObject data, TuitaIMManager manager) throws Exception {
        //cate消息
        if (data.has("srvCategories")) {
            JSONArray srvCategories = data.getJSONArray("srvCategories");
            for (int i = 0, m = srvCategories.length(); i < m; i++) {
                JSONObject srvcateJsonObj = srvCategories.getJSONObject(i);
                if (srvcateJsonObj.getInt("s") == IConst.CONTACT_DEL) {     //删除

                    MessageRecentDaoHelper.getInstance(manager.getManager().getContext()).delete(manager.getOwner().getUid(), srvcateJsonObj.getLong("cateId"), IConst.CHAT_TYPE_SERVICE_MESSAGE);
//                    deleteByCateId(manager.getOwner().getUid(), srvcateJsonObj.getLong("cateId"));
                    CateDaoHelper.getInstance(manager.getManager().getContext()).deleteByCateId(manager.getOwner().getUid(), srvcateJsonObj.getLong("cateId"));
//                    ServiceMessageDaoHelper.getInstance(manager.getManager().getContext()).deleteByCateId(manager.getOwner().getUid(), srvcateJsonObj.getLong("cateId"));

                } else { //更新或者新增
                    Cate cateUpdate = CateDaoHelper.getInstance(manager.getManager().getContext()).find(manager.getOwner().getUid(), srvcateJsonObj.getLong("cateId"));
                    if (cateUpdate != null) {
                        cateUpdate.setCate_id(srvcateJsonObj.has("cateId") ? srvcateJsonObj.getLong("cateId") : cateUpdate.getCate_id());
                        cateUpdate.setCate_name(srvcateJsonObj.has("cateTitle") ? srvcateJsonObj.getString("cateTitle") : cateUpdate.getCate_name());
                        cateUpdate.setCate_avatar(srvcateJsonObj.has("cateAvatar") ? srvcateJsonObj.getString("cateAvatar") : cateUpdate.getCate_avatar());
                    } else {
                        createCate(manager.getManager().getContext(), manager.getOwner().getUid(), srvcateJsonObj.getLong("cateId"), srvcateJsonObj.getString("cateTitle"), srvcateJsonObj.getString("cateAvatar"), Cate.CATE_HAS_CATEID, 0);
                    }
                }
            }
        }

        //srv消息
        if (data.has("srvAccounts")) {
            JSONArray srvAccounts = data.getJSONArray("srvAccounts");
            for (int i = 0, m = srvAccounts.length(); i < m; i++) {
                JSONObject srvJsonObj = srvAccounts.getJSONObject(i);
                if (srvJsonObj.getInt("s") == IConst.CONTACT_DEL) {     //删除
                    MessageRecentDaoHelper.getInstance(manager.getManager().getContext()).delete(manager.getOwner().getUid(), srvJsonObj.getLong("srvId"), IConst.CHAT_TYPE_SERVICE_MESSAGE);
                    deleteByServId(manager.getOwner().getUid(), srvJsonObj.getLong("srvId"), srvJsonObj.getLong("cateId") == 0 ? srvJsonObj.getLong("srvId") : srvJsonObj.getLong("cateId"));
                    ServiceMessageDaoHelper.getInstance(manager.getManager().getContext()).deleteByCateId(manager.getOwner().getUid(), srvJsonObj.getLong("cateId") == 0 ? srvJsonObj.getLong("srvId") : srvJsonObj.getLong("cateId"), srvJsonObj.getLong("srvId"));
                    MessageHistoryDaoHelper.getInstance(manager.getManager().getContext()).deleteAll(manager.getOwner().getUid(),srvJsonObj.getLong("srvId"),IConst.CHAT_TYPE_SERVICE_MESSAGE);
                    if (srvJsonObj.has("cateId")) {
                        if (srvJsonObj.getLong("cateId") != 0) {
                            ServiceMessageRecent serviceMessageRecentNew = findByCateIdFirst(manager.getOwner().getUid(), srvJsonObj.getLong("cateId"), srvJsonObj.getLong("srvId"));
                            CateDaoHelper.getInstance(manager.getManager().getContext()).updateDigstOrBubble(manager.getOwner().getUid(), srvJsonObj.getLong("cateId"), serviceMessageRecentNew.getDigst(), serviceMessageRecentNew.getBubble_num() != null ? Integer.parseInt(serviceMessageRecentNew.getBubble_num()) : 0);
                        }
                    }

                } else {       //更新或者新增
                    ServiceMessageRecent serMsgReUpdate = find(manager.getOwner().getUid(), srvJsonObj.getLong("srvId"));

                    if (serMsgReUpdate != null) {
                        if (srvJsonObj.has("cateId")) {
                            if (srvJsonObj.getLong("cateId") != 0) {        //不等于0说明此次不是一级
                                if (serMsgReUpdate.getCate_id() == serMsgReUpdate.getService_id()) {     //判断此服务号之前是否是一级  yes
                                    //TO DO 列表对象  挪进去
                                    MessageRecentDaoHelper.getInstance(manager.getManager().getContext()).delete(manager.getOwner().getUid(), serMsgReUpdate.getService_id(), IConst.CHAT_TYPE_SERVICE_MESSAGE);
                                    ServiceMessageRecent serviceMessageRecentNew = find(manager.getOwner().getUid(), serMsgReUpdate.getService_id());
                                    CateDaoHelper.getInstance(manager.getManager().getContext()).updateDigstOrBubble(manager.getOwner().getUid(), srvJsonObj.getLong("cateId"), serviceMessageRecentNew.getDigst(), serviceMessageRecentNew.getBubble_num() != null ? Integer.parseInt(serviceMessageRecentNew.getBubble_num()) : 0);

                                    MessageRecent reC = MessageRecentDaoHelper.getInstance(
                                            manager.getManager().getContext()).find(manager.getOwner().getUid(),
                                            srvJsonObj.getLong("cateId"));
                                    if (reC == null) {
                                        reC = new MessageRecent();
                                        reC.setBubble_num(1);
                                        reC.setMyid(manager.getOwner().getUid());
                                        reC.setChat_id(srvJsonObj.getLong("cateId"));
                                        reC.setChat_type(IConst.CHAT_TYPE_SERVICE_MESSAGE);
                                        reC.setDate(System.currentTimeMillis());
                                        reC.setUuid("" + manager.getOwner().getUid() + "_" + System.currentTimeMillis());
                                        MessageRecentDaoHelper.getInstance(
                                                manager.getManager().getContext()).save(reC);
                                    }

                                }

                                serMsgReUpdate.setCate_id(srvJsonObj.getLong("cateId"));
                            } else {        //相等说明此次是一级
                                if (serMsgReUpdate.getCate_id() != serMsgReUpdate.getService_id()) {      //之前不是一级
                                    //TO DO 挪出来  new recent对象
                                    MessageRecent messageRecent = new MessageRecent();
                                    messageRecent.setMyid(manager.getOwner().getUid());
                                    messageRecent.setChat_id(srvJsonObj.has("srvId") ? srvJsonObj.getLong("srvId") : serMsgReUpdate.getService_id());
                                    messageRecent.setChat_type(IConst.CHAT_TYPE_SERVICE_MESSAGE);
                                    messageRecent.setUuid("" + manager.getOwner().getUid() + "_" + System.currentTimeMillis());
                                    messageRecent.setBy1("0");
                                    messageRecent.setBy3("0");
                                    messageRecent.setBubble_num(serMsgReUpdate.getBubble_num() != null ? Integer.parseInt(serMsgReUpdate.getBubble_num()) : 0);
                                    MessageRecentDaoHelper.getInstance(manager.getManager().getContext())
                                            .save(messageRecent);
                                    ServiceMessageRecent serviceMessageRecentNew = findByCateIdFirst(manager.getOwner().getUid(), serMsgReUpdate.getCate_id(), serMsgReUpdate.getService_id());
                                    if (serviceMessageRecentNew != null)
                                        CateDaoHelper.getInstance(manager.getManager().getContext()).updateDigstOrBubble(manager.getOwner().getUid(), serMsgReUpdate.getCate_id(), serviceMessageRecentNew.getDigst(), serviceMessageRecentNew.getBubble_num() != null ? Integer.parseInt(serviceMessageRecentNew.getBubble_num()) : 0);

                                }

                                serMsgReUpdate.setCate_id(srvJsonObj.has("srvId") ? srvJsonObj.getLong("srvId") : serMsgReUpdate.getService_id());
                            }
                        } else {
                            serMsgReUpdate.setCate_id(serMsgReUpdate.getCate_id());
                        }
                        serMsgReUpdate.setService_id(srvJsonObj.has("srvId") ? srvJsonObj.getLong("srvId") : serMsgReUpdate.getService_id());
                        serMsgReUpdate.setService_name(srvJsonObj.has("srvTitle") ? srvJsonObj.getString("srvTitle") : serMsgReUpdate.getService_name());
                        serMsgReUpdate.setService_avatar(srvJsonObj.has("srvAvatar") ? srvJsonObj.getString("srvAvatar") : serMsgReUpdate.getService_avatar());
                        if (srvJsonObj.has("isInteractive")) {
                            serMsgReUpdate.setBy2(srvJsonObj.getBoolean("isInteractive") ? "1" : "0");
                        }
                        if (srvJsonObj.has("isNewsNotifyShielded")) {
                            serMsgReUpdate.setBy3(srvJsonObj.getBoolean("isNewsNotifyShielded") ? "1" : "0");
                        }

                        if (srvJsonObj.has("showHistory")) {
                            serMsgReUpdate.setIsShowHistory(srvJsonObj.getBoolean("showHistory") ? 1 : 0);
                        }
                        update(serMsgReUpdate);
                    } else {
                        createServiceRe(manager.getManager().getContext(), manager.getOwner().getUid(), srvJsonObj);
                    }
                }
            }
        }


    }

    /**
     * 创建一个服务号
     *
     * @param srvJsonObj
     * @throws JSONException
     */
    public void createServiceRe(Context context, long myId, JSONObject srvJsonObj) throws JSONException {
        ServiceMessageRecent serMsgRe = new ServiceMessageRecent();
        serMsgRe.setMyid(myId);
        serMsgRe.setService_id(srvJsonObj.getLong("srvId"));
        serMsgRe.setService_name(srvJsonObj.getString("srvTitle"));
        serMsgRe.setService_avatar(srvJsonObj.getString("srvAvatar"));
        serMsgRe.setBy2(srvJsonObj.getBoolean("isInteractive") ? "1" : "0");
        serMsgRe.setDate(System.currentTimeMillis());
        serMsgRe.setBy3(srvJsonObj.getBoolean("isNewsNotifyShielded") ? "1" : "0");
        serMsgRe.setIsShowHistory(srvJsonObj.getBoolean("showHistory") ? 1 : 0);
        if (srvJsonObj.getLong("cateId") != 0) {      //有分类的服务号
            serMsgRe.setCate_id(srvJsonObj.getLong("cateId"));
        } else {     //无分类的服务号
            serMsgRe.setCate_id(srvJsonObj.getLong("srvId"));

            createCate(context, myId, srvJsonObj.getLong("srvId"), srvJsonObj.getString("srvTitle"), srvJsonObj.getString("srvAvatar"), Cate.CATE_NO_CATEID, 0);
        }

        save(serMsgRe);     //这里可能会出问题，气泡排重等问题,而且多查一遍速度需要改
    }


    /**
     * 如果是新增状态 造出一个cate
     *
     * @param context
     * @param myid
     * @param cateId
     * @param cateName
     * @param cateAvatar
     */
    public void createCate(Context context, long myid, long cateId, String cateName, String cateAvatar, int is_has_cateid, int bubble_num) {
        Cate cateCreate = new Cate();
        cateCreate.setMy_id(myid);
        cateCreate.setCate_id(cateId);
        cateCreate.setCate_name(cateName);
        cateCreate.setCate_avatar(cateAvatar);
        cateCreate.setDate(System.currentTimeMillis());
        cateCreate.setIs_has_cateid(is_has_cateid);
        cateCreate.setBubble_num(bubble_num);

        CateDaoHelper.getInstance(context).save(cateCreate);
    }

}
