package com.zhongsou.souyue.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.db.homepage.DaoMaster;
import com.zhongsou.souyue.db.homepage.DaoSession;
import com.zhongsou.souyue.db.homepage.HomeList;
import com.zhongsou.souyue.db.homepage.HomeListDao;
import com.zhongsou.souyue.db.homepage.UserHomeList;
import com.zhongsou.souyue.db.homepage.UserHomeListDao;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.ListDeserializer;
import com.zhongsou.souyue.net.volley.CMainHttp;
import de.greenrobot.dao.query.QueryBuilder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by lvqiang on 15/5/26.
 */
public class HomePageDBHelper extends DaoMaster.DevOpenHelper {
    public static final String NAME = "homepage.db";

    public static SoftReference<HashMap<String, UserHomeList>> mHomeListReadCache;
    private static HomePageDBHelper mInstance;

    private Gson mGson;

    public static HomePageDBHelper getInstance() {
        if (mInstance == null) {
            mInstance = new HomePageDBHelper(MainApplication.getInstance());
        }
        return mInstance;
    }

    private HomePageDBHelper(Context context) {
        super(context, NAME, null);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BaseListData.class, new ListDeserializer());
        mGson = builder.create();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        switch (newVersion){
            case 3:
            case 4:
                CMainHttp.getInstance().clearCache();
                break;
        }
    }

    private void addListData(List<HomeList> _datas) {
        SQLiteDatabase mDataBase = getWritableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        HomeListDao listDao = session.getHomeListDao();
        listDao.insertOrReplaceInTx(_datas);
    }

    private void addUserData(List<UserHomeList> _datas) {
        SQLiteDatabase mDataBase = getWritableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        UserHomeListDao listDao = session.getUserHomeListDao();
        listDao.insertOrReplaceInTx(_datas);
    }

    public List<BaseListData> getData(String userid, String type, String id, String time, int maxnum) throws Exception {
        //select * from list where id_type_time in (select id_type_time from user where userid = id) and type =type and id =id and time<time orderby
        SQLiteDatabase mDataBase = getReadableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        HomeListDao listDao = session.getHomeListDao();
        id = id == null ? "" : id;
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        //type,id,time,userid,maxnum

//        select * from (select * from USER_HOME_LIST U where U.userid=1000000062) T left join HOME_LIST H on T.id_type_time=H.id_type_time;
        String sql = "SELECT * FROM (SELECT * FROM " + UserHomeListDao.TABLENAME + " WHERE " + UserHomeListDao.Properties.Userid.columnName
                + "=?) T LEFT JOIN " + HomeListDao.TABLENAME + " H ON T." + HomeListDao.Properties.Id_type_time.columnName + "=H."
                + HomeListDao.Properties.Id_type_time.columnName + " WHERE H." + HomeListDao.Properties.Id.columnName + "=? and H."
                + HomeListDao.Properties.Type.columnName + "=? AND H." + HomeListDao.Properties.Time.columnName + "<? ORDER BY H."
                + HomeListDao.Properties.Time.columnName + " DESC LIMIT ?";
//        Log.v(this.getClass().getName(),"sql word："+sql);
        Cursor c = mDataBase.rawQuery(sql, new String[]{userid, id, type, time, maxnum + ""});
        List<BaseListData> items = new ArrayList<BaseListData>();
        if (c == null || c.getCount() == 0) {
            return items;
        }
        try {
            while (c.moveToNext()) {
                String data = c.getString(c.getColumnIndex(HomeListDao.Properties.Data.columnName));
                BaseListData item = mGson.fromJson(data, new TypeToken<BaseListData>() {
                }.getType());
                item.setJsonResource(data);
                items.add(item);
                String read = c.getString(c.getColumnIndex(UserHomeListDao.Properties.Read.columnName));
                if (read.equals("1")) {
                    item.setHasRead(true);
                } else {
                    item.setHasRead(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }


        return items;
    }

    public void addData(String userid, List<HomeList> _list) {
        List<UserHomeList> userList = new ArrayList<UserHomeList>(_list.size());
        for (HomeList home : _list) {
            UserHomeList user = new UserHomeList(userid + home.getId_type_time(), userid, home.getId_type_time(), 0 + "");
            userList.add(user);
        }

        addUserData(userList);
        addListData(_list);
    }


//    public void deleteData(Context context,String userid ,String type,SearchResultItem item){
//        BroadCastUtils.sendToDeleteSearchResultItemData(context,item);
//        String mSrpId= item.srpId() == null ? item.id() + "" : item.srpId();
//
//        if (type.equals(HomeBallBean.SPECIAL)||HomeBallBean.){
//            mSrpId="0";
//        }
//        deleteData(userid,mSrpId,type,item.getDateId());
//    }

    public void deleteData(String userid, String time) {
        SQLiteDatabase mDataBase = getWritableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        HomeListDao listDao = session.getHomeListDao();
        UserHomeListDao userDao = session.getUserHomeListDao();
        userDao.deleteByKey(userid + "0_" + HomeBallBean.HEADLINE + "_" + time);
        listDao.deleteByKey("0_" + HomeBallBean.HEADLINE + "_" + time);
        userDao.deleteByKey(userid + "0_" + HomeBallBean.RECOMMEND + "_" + time);
        listDao.deleteByKey("0_" + HomeBallBean.RECOMMEND + "_" + time);
    }

    public void deleteAll(){
        SQLiteDatabase mDataBase = getWritableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        HomeListDao listDao = session.getHomeListDao();
        UserHomeListDao userDao = session.getUserHomeListDao();
        listDao.deleteAll();
        userDao.deleteAll();
    }

    public void setHasRead(String userid, String id, String type, String time) {
        if (userid == null) {
            Log.e(this.getClass().getName(), "userid is null!");
            return;
        }
        SQLiteDatabase mDataBase = getWritableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        UserHomeListDao userDao = session.getUserHomeListDao();
        UserHomeList item1 = new UserHomeList();
        String id_type = id + "_" + type + "_" + time;
        item1.setId_type_time(id_type);
        item1.setUserid(userid);
        item1.setUserid_identify(userid + id_type);
        item1.setRead("1");

        String id_type1 = "0_" + HomeBallBean.HEADLINE + "_" + time;
        UserHomeList item2 = new UserHomeList();
        item2.setId_type_time(id_type1);
        item2.setUserid(userid);
        item2.setUserid_identify(userid + id_type1);
        item2.setRead("1");

        String id_type2 = "0_" + HomeBallBean.RECOMMEND + "_" + time;
        UserHomeList item3 = new UserHomeList();
        item3.setId_type_time(id_type2);
        item3.setUserid(userid);
        item3.setUserid_identify(userid + id_type2);
        item3.setRead("1");

        String id_type3 = "0_" + HomeBallBean.YAOWEN + "_" + time;
        UserHomeList item4 = new UserHomeList();
        item4.setId_type_time(id_type3);
        item4.setUserid(userid);
        item4.setUserid_identify(userid + id_type3);
        item4.setRead("1");


        List<UserHomeList> lists = new ArrayList<UserHomeList>();
        lists.add(item1);
        lists.add(item2);
        lists.add(item3);
        lists.add(item4);

        HashMap<String, UserHomeList> ls = getUserListDaoReadCache(userid);
        for (UserHomeList us : lists) {
            ls.put(us.getId_type_time(), us);
        }
        mHomeListReadCache = new SoftReference<HashMap<String, UserHomeList>>(ls);


        userDao.insertOrReplaceInTx(lists);
    }


    public HashMap<String, UserHomeList> getUserListDaoReadCache(String userid) {
        if (mHomeListReadCache == null || mHomeListReadCache.get() == null) {
            mHomeListReadCache = new SoftReference<HashMap<String, UserHomeList>>(getUserListReadCacahe(userid));
        }
        return mHomeListReadCache.get();
    }

    private HashMap<String, UserHomeList> getUserListReadCacahe(String userid) {
        SQLiteDatabase mDataBase = getWritableDatabase();
        DaoMaster master = new DaoMaster(mDataBase);
        DaoSession session = master.newSession();
        UserHomeListDao userDao = session.getUserHomeListDao();
        QueryBuilder<UserHomeList> usdao = userDao.queryBuilder();
        usdao.where(UserHomeListDao.Properties.Userid.eq(userid), UserHomeListDao.Properties.Read.eq(1));
        List<UserHomeList> homslists = usdao.list();

        HashMap<String, UserHomeList> has = new HashMap<String, UserHomeList>();
        for (UserHomeList usls : homslists) {
            has.put(usls.getId_type_time(), usls);
        }
        return has;
    }
}
