package com.tuita.sdk.im.db.helper;

import android.content.Context;
import com.tuita.sdk.im.db.dao.MessageFileDao;
import com.tuita.sdk.im.db.dao.MessageHistoryDao;
import com.tuita.sdk.im.db.module.MessageFile;

import java.util.List;

/**
 * Created by zhou on 2015/11/11.
 */
public class MessageFileDaoHelper extends BaseDaoHelper<MessageFile>{
    private static MessageFileDaoHelper instance;

    public static MessageFileDaoHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageFileDaoHelper();
            instance.dao = getDaoSession(context).getMessageFileDao();
            instance.db = instance.dao.getDatabase();
        }
        return instance;
    }

    private MessageFileDao dao;

    /**
     * 构造方法，这里故意弄成私有，外部不可实例化该类
     */
    private MessageFileDaoHelper(){}

    /**
     * 删除单个
     * @param id
     */
    public void delete(long id){
        dao.deleteByKey(id);
    }

    /**
     * 清空这张表
     */
    public void deleteAll(){
        dao.deleteAll();
    }

    /**
     * 插入一条数据
     * @param messageFile
     * @return 插入数据id
     */
    public long insert(MessageFile messageFile){
        return dao.insert(messageFile);
    }

    /**
     * 根据ID查询单个
     * @param id 要查询的数据id
     */
    public MessageFile select(long id){
        return dao.load(id);
    }

    /**
     * 查询所有 返回一个列表
     */
    public List<MessageFile> selectAll(){
        return dao.loadAll();
    }

    /**
     * 查询所有已下载完成的文件
     */
    public List<MessageFile> selectAllDownLoadFile(){

        return dao.queryBuilder().where(MessageFileDao.Properties.State.eq(MessageFile.DOWNLOAD_STATE_COMPLETE)).orderDesc(MessageFileDao.Properties.UpdateTime).list();
    }

    /**
     * 更新数据库
     * @param messageFile
     * @return
     */
    public void update(MessageFile messageFile){
        dao.update(messageFile);
    }


    /**
     * 更新数据库
     * @param
     * @return
     */
    public void updateProgress(Long id,Long curSize){
        dao.getDatabase().execSQL("UPDATE " + dao.getTablename() + " SET CURSIZE = '" + curSize + "' WHERE _id = '" + id + "';");
    }


    /**
     * 更新数据库
     * @param
     * @return
     */
    public void updateState(Long id,Integer state){
        dao.getDatabase().execSQL("UPDATE "+dao.getTablename()+" SET STATE = '"+state+"' WHERE _id = '"+id+"';");
    }


}
