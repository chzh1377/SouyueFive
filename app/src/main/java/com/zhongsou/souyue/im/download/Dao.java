package com.zhongsou.souyue.im.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * 
 * 保存下载器进度
 * 
 * @author wangqiang
 * 
 * 
 */

public class Dao {

	private DBOpenHelper dbOpenHelper;
	public static String lock = "dblock";
	public static String fileLock = "filelock";
	private static final String DATABASE_NAME = "expression.db";

	public Dao(Context context) {
		dbOpenHelper = new DBOpenHelper(context);
	}

	// 保存下载纪录
	public void saveInfo(LoadInfo info, Context context) {
		synchronized (lock) {
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
			database.beginTransaction();
			try {
				String sql = "insert into downloadlog(completesize,totalsize,downurl,packageid) values(?,?,?,?)";
				Object[] bindArgs = new Object[] { info.getComplete(),
						info.getFileSize(), info.getUrlstring(),
						info.getPackageId() };
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();
				database.close();
			}
			
		}
	}

	/**
	 * 
	 * 下载更新纪录
	 * 
	 * @param packageId
	 * @param completeSize
	 *            完成度
	 * @param context
	 */
	public void updateInfo(String packageId, int completeSize) {
		synchronized (lock) {
			SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
			database.beginTransaction();
			try {
				String sql = "update downloadlog set completesize=? where packageid=?";
				Object[] bingArgs = new Object[] { completeSize, packageId };
				database.execSQL(sql, bingArgs);
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();
			}
			database.close();
		}
	}

	/**
	 * 
	 * 根据id查询下载进度
	 * 
	 * @param packageId
	 * 
	 * @param context
	 * 
	 * @return
	 */

	public LoadInfo getInfos(String packageId) {
		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		database.beginTransaction();
		LoadInfo info = null;
		Cursor rawQuery = null;
		try {
			String sql = "select * from downloadlog where packageid = " + "'"
					+ packageId + "'";
			// String[] selectionArgs = new String[]{packageId};
			rawQuery = database.rawQuery(sql, null);
			if (rawQuery != null && rawQuery.moveToNext()) {
				int completesize = rawQuery.getInt(rawQuery
						.getColumnIndex("completesize"));
				int totalsize = rawQuery.getInt(rawQuery
						.getColumnIndex("totalsize"));
				info = new LoadInfo(totalsize, completesize, null, packageId);
				Slog.d("callback", info.toString());
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			if (rawQuery != null)
				rawQuery.close();
			database.close();
		}
		return info;
	}

	/**
	 * 
	 * 判断packageid开始下载
	 * 
	 * @param packageId
	 * 
	 * @return
	 */

	public boolean isHasInfo(String packageId) {
		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		database.beginTransaction();
		boolean isExist = false;
		Cursor rawQuery = null;
		try {
			String sql = "select count(*) from downloadlog where packageid = ?";
			String[] selectionArgs = new String[] { packageId };
			rawQuery = database.rawQuery(sql, selectionArgs);
			rawQuery.moveToNext();
			if (rawQuery.getInt(0) != 0) {
				isExist = true;
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			if(rawQuery!=null)
			rawQuery.close();
			database.close();
		}
		return isExist;
	}

	public boolean deleteInfo(String packageId) {

		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		database.beginTransaction();
		boolean result = false;
		try {
			String sql = "delete from downloadlog where packageid = ?";
			String[] selectionArgs = new String[] { packageId };
			int i = database.delete("downloadlog", "packageid = ?",
					new String[] { packageId });
			result = (i != 0);
			if (result) {
				Slog.d("callback", "删除成功下载纪录---------" + packageId);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			database.close();
		}
		return result;
	}

	/**
	 * 
	 * 是否下载了
	 * 
	 * 
	 * 
	 * @param fileName
	 * 
	 * @return
	 */

	public synchronized boolean hasPackage(String packageId) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = null;
		db.beginTransaction();
		boolean isExist = false;
		try {
			String sql = "select * from " + DBOpenHelper.TAB_PACKAGE
					+ " where " + DBOpenHelper.COLUM_ID + "  = ? and "
					+ DBOpenHelper.COLUM_USERID + " = ? and "
					+ DBOpenHelper.COLUM_ISEXIST + " =1";
			cursor = db.rawQuery(sql, new String[] { packageId,
					SYUserManager.getInstance().getUserId() });
			isExist = cursor.moveToNext();
		} finally {
			db.endTransaction();
			if (cursor != null)
				cursor.close();
			db.close();
		}
		return isExist;
	}
}
