package com.tuita.sdk.im.db.helper;
import android.content.Context;
import com.tuita.sdk.im.db.dao.CateDao;
import com.tuita.sdk.im.db.module.Cate;
import com.tuita.sdk.im.db.module.Group;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服务号分类
 *
 */
public class CateDaoHelper extends BaseDaoHelper<Group> {
    private static CateDaoHelper instance;
    public static CateDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CateDaoHelper();
            instance.dao = getDaoSession(context).getCateDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }
    private CateDao dao;
    private CateDaoHelper() {
    }
    @SuppressWarnings("boxing")
    public void delete(long id) {
        log(dao.getTablename(), "delete(id:" + id + ")");
        dao.deleteByKey(id);
    }

    /**
     * 根据myid和cateid删除cate表里数据
     *
     * @param myId
     * @param cateId
     */
    public void deleteByCateId(long myId, long cateId) {
        QueryBuilder<Cate> qb = dao.queryBuilder();
        DeleteQuery<Cate> bd = qb.where(CateDao.Properties.My_id.eq(myId), CateDao.Properties.Cate_id.eq(cateId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    @SuppressWarnings("boxing")
    protected Map<Long, Cate> find(long myid, List<Long> cate_id) {
        log(dao.getTablename(), "find(myid:" + myid + ",cate_id:" + cate_id + ")");
        QueryBuilder<Cate> qb = dao.queryBuilder();
        qb.where(CateDao.Properties.My_id.eq(myid));
        if (cate_id != null) {
            qb.where(CateDao.Properties.Cate_id.in(cate_id));
        }
        Map<Long, Cate> map = new HashMap<Long, Cate>();
        List<Cate> list = qb.orderAsc(CateDao.Properties.Date).list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Cate c = list.get(i);
                map.put(c.getCate_id(), c);
            }
        }
        return map;
    }

    /**
     * 插入数据，每次插入都要查询下该条数据是否存在，存在直接update
     * @param newCate
     */
    public void save(Cate newCate) {
        Cate oldCate = find(newCate.getMy_id(),newCate.getCate_id());
        if (oldCate == null){
            dao.insert(newCate);
        }else {
            newCate.setId(oldCate.getId());
            newCate.setBubble_num(oldCate.getBubble_num() + newCate.getBubble_num());
            newCate.setDigst(newCate.getDigst() == null || newCate.getDigst().equals("") ? "" : newCate.getDigst());
            dao.update(newCate);
        }
    }

    public void update(Cate newCate){
        dao.update(newCate);
    }

    /**
     * update 原有cate的digst
     * @param myId
     * @param cateId
     * @param digst
     */
    public void updateDigstOrBubble(long myId, long cateId, String digst,int bubbleNum) {
        Cate oldCate = find(myId,cateId);
        if (oldCate !=null) {
            oldCate.setDigst(digst);
            oldCate.setBubble_num(bubbleNum);
            dao.update(oldCate);
        }
    }

    /**
     * 查找该条cate信息
     * @param myid
     * @param cateId
     * @return
     */
    public Cate find(long myid, long cateId) {
        List<Cate> list = dao.queryBuilder().where(CateDao.Properties.My_id.eq(myid),CateDao.Properties.Cate_id.eq(cateId)).list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 清除 气泡
     * @param myId
     * @param cateId
     */
    public void clearCateBubble(long myId,long cateId){
        db.execSQL("UPDATE " + dao.getTablename() + " SET BUBBLE_NUM=0 WHERE MY_ID=" + myId + " AND CATE_ID=" + cateId);
    }
}
