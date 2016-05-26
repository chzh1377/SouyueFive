package com.tuita.sdk.im.db.helper;
import android.content.Context;
import com.tuita.sdk.im.db.dao.ServiceMessageDao;
import com.tuita.sdk.im.db.dao.ServiceMessageRecentDao;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.ServiceMessage;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 服务号
 *
 */
public class ServiceMessageDaoHelper extends BaseDaoHelper<Group> {
    private static ServiceMessageDaoHelper instance;
    public static ServiceMessageDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceMessageDaoHelper();
            instance.dao = getDaoSession(context).getServiceMessageDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }
    private ServiceMessageDao dao;
    private ServiceMessageDaoHelper() {
    }
    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

    /**
     * 删除serviceMsg表中信息
     * @param myId
     * @param cateId
     */
    public void deleteByCateId(long myId,long cateId,long srvId){
        QueryBuilder<ServiceMessage> qb = dao.queryBuilder();
        DeleteQuery<ServiceMessage> bd = qb.where(ServiceMessageDao.Properties.Myid.eq(myId), ServiceMessageDao.Properties.Cate_id.eq(cateId),ServiceMessageDao.Properties.Service_id.eq(srvId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    @SuppressWarnings("boxing")
    protected Map<Long, ServiceMessage> find(long myid, List<Long> cate_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",cate_id:" + cate_id + ")");
        QueryBuilder<ServiceMessage> qb = dao.queryBuilder();
//		qb.where(com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.SELF_ID.eq(myid));
        if (cate_id != null) {
            qb.where(com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.Cate_id.in(cate_id));
        }
        Map<Long, ServiceMessage> map = new HashMap<Long, ServiceMessage>();
        List<ServiceMessage> list = qb.list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ServiceMessage c = list.get(i);
                map.put(c.getCate_id(), c);
            }
        }
        return map;
    }
    public ServiceMessage find(long myid,long service_id) {
        log(dao.getTablename(), "find(service_id:" + service_id + ")");
        List<ServiceMessage> list = dao.queryBuilder().where(com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.Service_id.eq(service_id), ServiceMessageDao.Properties.Myid.eq(myid)).list();
        return list != null && list.size() >0 ? list.get(0) : null;
    }
    @SuppressWarnings("boxing")
    public List<ServiceMessage> findAll(long myid,long cateId) {
        log(dao.getTablename(), "findAll(cateId:" + cateId + ")");
        return dao.queryBuilder().where(com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.Cate_id.eq(cateId), ServiceMessageDao.Properties.Myid.eq(myid)).list();
    }

    @SuppressWarnings("boxing")
    public List<ServiceMessage> findAll(long myid,long cateId,long service_id) {
        log(dao.getTablename(), "findAll(cateId:" + cateId + ")");
        return dao.queryBuilder().where(com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.Cate_id.eq(cateId),com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.Service_id.eq(service_id),ServiceMessageDao.Properties.Myid.eq(myid)).list();
    }

    public ServiceMessage findByMuid(String muid) {
        log(dao.getTablename(), "find(muid:" + muid + ")");
        List<ServiceMessage> list = dao.queryBuilder().where(com.tuita.sdk.im.db.dao.ServiceMessageDao.Properties.By1.eq(muid)).list();
        return list != null && list.size() >0 ? list.get(0) : null;
    }
    /**
     * 插入或更新
     * @param serviceMessages
     */
    public void save(List<ServiceMessage> serviceMessages) {
        log(dao.getTablename(), "save(serviceMessages:" + serviceMessages + ")");
        if (serviceMessages != null)
            for (int i = 0; i < serviceMessages.size(); i++) {
                save(serviceMessages.get(i));
            }
    }
    /**
     * 插入或更新
     * @param serviceMessage
     */
    @SuppressWarnings("boxing")
    public void save(ServiceMessage serviceMessage) {
        log(dao.getTablename(), "save(serviceMessage:" + serviceMessage + ")");
        if (serviceMessage != null) {
            ServiceMessage result = findByMuid(serviceMessage.getBy1());
            if (result == null) {
                log(dao.getTablename(), "insert(serviceMessage:" + serviceMessage + ")");
                dao.insert(serviceMessage);
            } else {
                serviceMessage.setId(result.getId());
                log(dao.getTablename(), "update(serviceMessage:" + serviceMessage + ")");
                dao.update(serviceMessage);
            }
        }
    }
}
