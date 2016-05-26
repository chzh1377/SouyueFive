package com.zhongsou.souyue.im.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 浏览纪录DAO
 * 
 * 
 * 
 * @author wangqiang
 * 
 * 
 */

public class HistoryDao {
	private DBOpenHelper openHelper;
	private Context mContext;
	public HistoryDao(Context context) {
		openHelper = new DBOpenHelper(context);
	}

	/**
	 * 批量 添加第一页纪录
	 */
	public synchronized void save(List<PackageBean> beans) {
		deleteAll();
		List<ContentValues> list = new ArrayList<ContentValues>();
		for (int i = 0; i < beans.size(); i++) {
			PackageBean bean = beans.get(i);
			String packageId = bean.getPackageId();
			String packageName = bean.getPackageName();
			String iconUrl = bean.getIconUrl();
			long packageSize = bean.getPackageSize();
			String price = bean.getPrice();
			long sortNo = bean.getSortNo();
			int down = bean.getIsDownloaded();
			int isnew = bean.getIsNew();
			String filename = bean.getFileName();

			ContentValues values = new ContentValues();
			values.put(DBOpenHelper.COLUM_ID, packageId);
			values.put(DBOpenHelper.COLUM_NAME, packageName);
			values.put(DBOpenHelper.COLUM_ICON, iconUrl);
			values.put(DBOpenHelper.COLUM_SIZE, packageSize);
			values.put(DBOpenHelper.COLUM_PRICE, price);
			values.put(DBOpenHelper.COLUM_SORT, sortNo);
			values.put(DBOpenHelper.COLUM_DOWN, down);
			values.put(DBOpenHelper.COLUM_NEW, isnew);
			values.put(DBOpenHelper.COLUM_FILENAME, filename);
			values.put(DBOpenHelper.COLUM_USERID, SYUserManager.getInstance()
			.getUserId());
			list.add(values);
		}

		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction(); // 手动设置开始事务
		for (ContentValues v : list) {
			db.insert(DBOpenHelper.TAB_PACKAGE_HISTORY, null, v);
		}
		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction(); // 处理完成
		db.close();
	}

	/**
	 * 清空纪录
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		String sql = "delete from " + DBOpenHelper.TAB_PACKAGE_HISTORY
		+ " where " + DBOpenHelper.COLUM_USERID + " = ?";
		db.execSQL(sql,
		new Object[] { SYUserManager.getInstance().getUserId() });
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	/**
	 * 
	 * 查询一页浏览纪录
	 * 
	 * @return
	 */
	public List<PackageBean> queryAll() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		db.beginTransaction();
		List<PackageBean> lists = new ArrayList<PackageBean>();
		String sql = "select * from  " + DBOpenHelper.TAB_PACKAGE_HISTORY
		+ " where " + DBOpenHelper.COLUM_USERID + " = ?";
		Cursor rawQuery = db.rawQuery(sql, null);
		while (rawQuery.moveToNext()) {
			PackageBean bean = new PackageBean();
			// bean.setDesc(rawQuery.getString(rawQuery.getColumnIndex("desc")));
			bean.setFileName(rawQuery.getString(rawQuery
			.getColumnIndex("filename")));
			bean.setIconUrl(rawQuery.getString(rawQuery
			.getColumnIndex("iconUrl")));
			bean.setIsDownloaded(rawQuery.getInt(rawQuery
			.getColumnIndex("isdownloaded")));
			bean.setIsNew(rawQuery.getInt(rawQuery.getColumnIndex("isnew")));
			bean.setPackageId(rawQuery.getString(rawQuery
			.getColumnIndex("packageId")));
			bean.setPackageName(rawQuery.getString(rawQuery
			.getColumnIndex("packagename")));
			bean.setPackageSize(Long.parseLong(rawQuery.getString(rawQuery
			.getColumnIndex("packagesize"))));
			bean.setPrice(rawQuery.getString(rawQuery.getColumnIndex("price")));
			bean.setSortNo(Long.parseLong(rawQuery.getString(rawQuery
			.getColumnIndex("sortno"))));
			lists.add(bean);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return lists;
	}

}
