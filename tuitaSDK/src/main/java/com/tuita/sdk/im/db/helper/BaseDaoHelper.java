package com.tuita.sdk.im.db.helper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.tuita.sdk.im.db.dao.DaoMaster;
import com.tuita.sdk.im.db.dao.DaoMaster.OpenHelper;
import com.tuita.sdk.im.db.dao.DaoSession;
import de.greenrobot.dao.query.QueryBuilder;
public abstract class BaseDaoHelper<T> {
	protected static final String LOG_TAG = "DAO";
	static {
		QueryBuilder.LOG_SQL = true;
	}
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	public static final String DBNAME = "im.db";
	private static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context, DBNAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}
	protected static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			daoSession = getDaoMaster(context).newSession();
		}
		return daoSession;
	}
	/*
	private static ImNewfriendsHelp instance;
	private ImNewfriendsDao dao;
	public static ImNewfriendsHelp getInstance() {
		if (instance == null) {
			instance = new ImNewfriendsHelp();
			instance.dao = MainApplication.getDaoSession().getImNewfriendsDao();
			instance.db = instance.dao.getDatabase();
		}
		return instance;
	}
	private ImNewfriendsHelp() {
	}
	 */
	protected SQLiteDatabase db;
	protected static void log(String table, String methodAndArgs) {
		log(table, methodAndArgs, "");
	}
	protected static void log(String table, String methodAndArgs, Object result) {
		Log.d(LOG_TAG, table.toLowerCase() + "." + methodAndArgs + "--->" + result);
	}
}
