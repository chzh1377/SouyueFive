package com.zhongsou.souyue.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.SuberedItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqiang on 15/8/5.
 *   多线程 访问db
 */
public class SuberTableDBHelper extends SouYueDBHelper {


    private static final String whereClause_delete = " srpId = ? and userId = ? ";
    private static final String sql_select = "select * from " + TABLE_HAS_SUBERED + " where userId = ? order by position asc";
    private static final String sql_select_one = "select * from " + TABLE_HAS_SUBERED + " where userId = ? and srpId = ? ";
    private static final String sql_select_position_max = "select min(position) from " + TABLE_HAS_SUBERED + " where userId = ?"; //最大值
    private static final String sql_select_position_min = "select max(position) from " + TABLE_HAS_SUBERED + " where userId = ?"; //最小值
    private static final String whereClause_delete_all = " userId = ? ";
    private static final String sql_select_not_group = "select * from " + TABLE_HAS_SUBERED + " where userId = ? and ("+SUBER_CATERGORY+" ='srp' or "+SUBER_CATERGORY+" ='interest') order by position asc";
    private static SuberTableDBHelper instance;

    private SuberTableDBHelper(){

    }

    public static synchronized SuberTableDBHelper getInstance(){
        if(instance==null){
            instance = new SuberTableDBHelper();
        }
        return  instance;
    }

    public  void insertForList(List<SuberedItemInfo> infos, String userId, boolean isPre) throws Exception{
        synchronized(this) {
            Log.e(this.getClass().getName(),"suber insert:size="+infos.size());
            openWritable();
            int pos= makeSuberedItem(isPre, userId);//不能放到try里面，不然会出错
            try {
                beginTransaction();
                SuberedItemInfo info;
                ContentValues contentValues;
                for (int i = 0; i < infos.size(); i++) {
                    info = infos.get(i);
                    if (isPre){
                        info.setPosition(pos-i);
                    }else {
                        info.setPosition(pos+i);
                    }
                    contentValues = getValues(info, userId);
                    db.insert(TABLE_HAS_SUBERED, null, contentValues);
                }
                commit();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("批量插入异常");
            }finally {
                endTransition();
            }
        }

    }


    public  void initForList(List<SuberedItemInfo> infos, String userId) throws Exception{
        synchronized(this) {
            Log.e(this.getClass().getName(),"suber init:size="+infos.size());
            openWritable();

            try {
                beginTransaction();
                SuberedItemInfo info;
                ContentValues contentValues;
                for (int i = 0; i < infos.size(); i++) {
                    info = infos.get(i);
                    info.setPosition(i);
                    contentValues = getValues(info, userId);
                    db.insert(TABLE_HAS_SUBERED, null, contentValues);
                }
                commit();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("批量插入异常");
            }finally {
                endTransition();
            }
        }

    }

    public synchronized void insertOne(SuberedItemInfo  src, String userId, boolean isPre) throws Exception{
        try {
            Log.e(this.getClass().getName(),"suber insert:one");
            openWritable();
            int pos = makeSuberedItem(isPre,userId);
            Slog.e("db","position----------"+pos);
            src.setPosition(pos);
//            if (isPre){
//                src.setPosition(pos - 1);
//            }else {
//                src.setPosition(pos + 1);
//            }
            ContentValues contentValues = getValues(src, userId);
            db.insert(TABLE_HAS_SUBERED, null, contentValues);
//            commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("单条数据插入异常");
        }

    }

    private int  makeSuberedItem(boolean isPre, String userId) {
        int position = getPosition(isPre, userId);
        if (isPre) {
            position -=1;
        } else {
            position +=1;
        }
        Log.d("callback","subscrible position="+position);
        return position;
    }

    private int getPosition(boolean isPre, String userId) {
        Cursor cursor;
        int position;
        if (!isPre)
            cursor = db.rawQuery(sql_select_position_min, new String[]{userId});
        else {
            cursor = db.rawQuery(sql_select_position_max, new String[]{userId});
        }

        if (cursor != null && cursor.moveToNext()) {
            position = cursor.getInt(0);
            cursor.close();
            return position;
        }

        return 0;
    }

    public synchronized void delete(SuberedItemInfo info, String userId) throws Exception{
        try {
            Log.e(this.getClass().getName(),"suber delete:one");
            openWritable();
            db.delete(TABLE_HAS_SUBERED, whereClause_delete, new String[]{String.valueOf(info.getSrpId()), userId});
//            commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("删除单条数据异常");
        }
    }

    public synchronized void deleteList(List<SuberedItemInfo> infos, String userId) throws Exception{
        SuberedItemInfo info ;
        openWritable();
        try {
            Log.e(this.getClass().getName(),"suber delete:size="+infos.size());
            beginTransaction();
            for (SuberedItemInfo info1 : infos) {
                info = info1;
                db.delete(TABLE_HAS_SUBERED, whereClause_delete, new String[]{String.valueOf(info.getSrpId()), userId});
            }
            commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("删除单条数据异常");
        } finally {
            endTransition();
        }
    }

    public synchronized void deleteAll(String userId) throws Exception{
        try {
            long start = System.currentTimeMillis();
            openWritable();
            long take = System.currentTimeMillis()-start;
            Slog.d("callback","db open --------------->"+take);
            Log.e(this.getClass().getName(),"suber delete:all");
            db.delete(TABLE_HAS_SUBERED, whereClause_delete_all, new String[]{userId});
//            commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("批量删除异常");
        }
    }

    public synchronized List<SuberedItemInfo> queryAll(String userId) throws Exception{
        Cursor cursor = null;
        Slog.d("callback","queryall  userId-------------------->"+userId);
        List<SuberedItemInfo> infos = new ArrayList<SuberedItemInfo>();
        try {
            openReadable();
            cursor = db.rawQuery(sql_select, new String[]{userId});
            SuberedItemInfo info;
            while (cursor != null && cursor.moveToNext()) {
                info = new SuberedItemInfo();
                info.setCategory(cursor.getString(cursor.getColumnIndex(SUBER_CATERGORY)));
                info.setImage(cursor.getString(cursor.getColumnIndex(SUBER_IMAGE)));
                info.setStatus(cursor.getString(cursor.getColumnIndex(SUBER_STATUS)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(SUBER_URL)));
                info.setId(cursor.getLong(cursor.getColumnIndex(SUBER_ID)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(SUBER_TITLE)));
                info.setKeyword(cursor.getString(cursor.getColumnIndex(SUBER_KEYWORD)));
                info.setSrpId(cursor.getString(cursor.getColumnIndex(SUBER_SRPID)));
                info.setPosition(cursor.getInt(cursor.getColumnIndex(SUBER_POSITION)));
                info.setType(cursor.getString(cursor.getColumnIndex(SUBER_TYPE)));
                info.setInvokeType(cursor.getInt(cursor.getColumnIndex(SUBER_CHANNEL)));
                infos.add(info);
            }
            Log.e(this.getClass().getName(),"suber query:size="+infos.size());

        } catch (Exception e) {
            e.printStackTrace();
            infos = null;
            throw new Exception("批量查询异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return infos;
    }
    public synchronized List<SuberedItemInfo> queryDiction(String userId) throws Exception{
        Cursor cursor = null;
        Slog.d("callback","queryall  userId-------------------->"+userId);
        List<SuberedItemInfo> infos = new ArrayList<SuberedItemInfo>();
        try {
            openReadable();
            cursor = db.rawQuery(sql_select_not_group, new String[]{userId});
            SuberedItemInfo info;
            while (cursor != null && cursor.moveToNext()) {
                info = new SuberedItemInfo();
                info.setCategory(cursor.getString(cursor.getColumnIndex(SUBER_CATERGORY)));
                info.setImage(cursor.getString(cursor.getColumnIndex(SUBER_IMAGE)));
                info.setStatus(cursor.getString(cursor.getColumnIndex(SUBER_STATUS)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(SUBER_URL)));
                info.setId(cursor.getLong(cursor.getColumnIndex(SUBER_ID)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(SUBER_TITLE)));
                info.setKeyword(cursor.getString(cursor.getColumnIndex(SUBER_KEYWORD)));
                info.setSrpId(cursor.getString(cursor.getColumnIndex(SUBER_SRPID)));
                info.setPosition(cursor.getInt(cursor.getColumnIndex(SUBER_POSITION)));
                info.setType(cursor.getString(cursor.getColumnIndex(SUBER_TYPE)));
                info.setInvokeType(cursor.getInt(cursor.getColumnIndex(SUBER_CHANNEL)));
                infos.add(info);
            }
            Log.e(this.getClass().getName(),"suber query:size="+infos.size());

        } catch (Exception e) {
            e.printStackTrace();
            infos = null;
            throw new Exception("批量查询异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return infos;
    }
    public synchronized  SuberedItemInfo queryOne(String userId,String srpId) throws Exception{
        Cursor cursor = null;
        Slog.d("callback","queryone  userId-------------------->"+userId);
        SuberedItemInfo info = null;
        try {
            openReadable();
            cursor = db.rawQuery(sql_select_one, new String[]{userId,srpId});
            if (cursor != null && cursor.moveToNext()) {
                info = new SuberedItemInfo();
                info.setCategory(cursor.getString(cursor.getColumnIndex(SUBER_CATERGORY)));
                info.setImage(cursor.getString(cursor.getColumnIndex(SUBER_IMAGE)));
                info.setStatus(cursor.getString(cursor.getColumnIndex(SUBER_STATUS)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(SUBER_URL)));
                info.setId(cursor.getLong(cursor.getColumnIndex(SUBER_ID)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(SUBER_TITLE)));
                info.setKeyword(cursor.getString(cursor.getColumnIndex(SUBER_KEYWORD)));
                info.setSrpId(cursor.getString(cursor.getColumnIndex(SUBER_SRPID)));
                info.setPosition(cursor.getInt(cursor.getColumnIndex(SUBER_POSITION)));
                info.setType(cursor.getString(cursor.getColumnIndex(SUBER_TYPE)));
                info.setInvokeType(cursor.getInt(cursor.getColumnIndex(SUBER_CHANNEL)));
            }
            Log.e(this.getClass().getName(),"suber query:one");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("单个数据查询异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return info;
    }


    private ContentValues getValues(SuberedItemInfo info, String userId) {
        ContentValues values = new ContentValues();
        values.put(SUBER_CATERGORY, info.getCategory());
        values.put(SUBER_ID, info.getId());
        values.put(SUBER_IMAGE, info.getImage());
        values.put(SUBER_KEYWORD, info.getKeyword());
        values.put(SUBER_POSITION, info.getPosition());
        values.put(SUBER_STATUS, info.getStatus());
        values.put(SUBER_SRPID, info.getSrpId());
        values.put(SUBER_TITLE, info.getTitle());
        values.put(SUBER_TYPE, info.getType());
        values.put(SUBER_URL, info.getUrl());
        values.put(SUBER_USERID, userId);
        values.put(SUBER_CHANNEL,info.getInvokeType());
        return values;
    }


    private void beginTransaction() {
        db.beginTransaction();
    }

    private void commit() {
        db.setTransactionSuccessful();
    }

    private void endTransition() {
        db.endTransaction();
    }

    public void close() {
        long start = System.currentTimeMillis();
        super.close();
        long take = System.currentTimeMillis()-start;
        Slog.d("callback","db close --------------->"+take);
    }


}
