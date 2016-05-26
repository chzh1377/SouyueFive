package com.zhongsou.souyue.im.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.ExpressionPackage;
import com.zhongsou.souyue.im.module.ExpressionTab;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.im.util.ZipUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 表情管理Dao
 * 
 * @author wangqiang
 */
public class PackageDao {

	private Context context;

	private DBOpenHelper openHelper;

	public PackageDao(Context context) {
		openHelper = new DBOpenHelper(context);
		this.context = context;
	}

	/**
	 * 
	 * 返回database实例
	 * 
	 * @return
	 */
	public SQLiteDatabase getConnection() {
		SQLiteDatabase database = null;
		try {
			database = openHelper.getReadableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return database;
	}

	/**
	 * 
	 * 保存包下载纪录
	 * 
	 * @param packagePath
	 * @param iconPath
	 */
	public void save(PackageBean bean) {
		boolean exist = hasPackageRecord(bean.getPackageId());
		if (exist) {
			deleteRecord(bean);
		}
		SQLiteDatabase db = getConnection();
		try {
			db.beginTransaction();
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
			values.put(DBOpenHelper.COLUM_ISEXIST, "1");
			values.put(DBOpenHelper.COLUM_USERID, SYUserManager.getInstance()
					.getUserId());

			db.insertOrThrow(DBOpenHelper.TAB_PACKAGE, null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	/**
	 * 
	 * packageid是否下载了
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean hasPackage(String packageId) {
		SQLiteDatabase db = getConnection();
		db.beginTransaction();
		Cursor cursor = null;
		try {
			String sql = "select * from " + DBOpenHelper.TAB_PACKAGE
					+ " where " + DBOpenHelper.COLUM_ID + "  = ? and "
					+ DBOpenHelper.COLUM_USERID + " = ? and "
					+ DBOpenHelper.COLUM_ISEXIST + " =1";
			cursor = db.rawQuery(sql, new String[] { packageId,
					SYUserManager.getInstance().getUserId() });
			return cursor.moveToNext();
		} finally {
			db.endTransaction();
			if (cursor != null)
				cursor.close();
			db.close();

		}

	}

	/**
	 * 
	 * db是否有下载纪录
	 * 
	 * @param packageId
	 * @return
	 */
	public boolean hasPackageRecord(String packageId) {
		SQLiteDatabase db = getConnection();
		db.beginTransaction();
		try {
			String sql = "select * from " + DBOpenHelper.TAB_PACKAGE
					+ " where " + DBOpenHelper.COLUM_ID + "  = ? and "
					+ DBOpenHelper.COLUM_USERID + " = ?";
			Cursor cursor = db.rawQuery(sql, new String[] { packageId,
					SYUserManager.getInstance().getUserId() });
			return cursor.moveToNext();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * 删除一条下载纪录
	 * 
	 * @param packageBean
	 */
	public void deleteRecord(PackageBean packageBean) {
		SQLiteDatabase db = getConnection();
		String packageName = packageBean.getPackageName();

		try {
			db.beginTransaction();
			String sql = "delete from " + DBOpenHelper.TAB_PACKAGE + " where "
					+ DBOpenHelper.COLUM_NAME + "  = ? and "
					+ DBOpenHelper.COLUM_USERID + " = ?";
			db.execSQL(sql, new Object[] { packageName,
					SYUserManager.getInstance().getUserId() });
			Slog.d("callback", "成功删除纪录");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();

	}

	/**
	 * 
	 * 卸载表情包
	 * 
	 * @param packageBean
	 */

	public void delete(PackageBean packageBean) {
		SQLiteDatabase db = getConnection();
		String packageName = packageBean.getPackageName();
		try {

			db.beginTransaction();
			String sql = "update " + DBOpenHelper.TAB_PACKAGE + " set "
					+ DBOpenHelper.COLUM_DOWN + " = 1 , "
					+ DBOpenHelper.COLUM_ISEXIST + "= 0" + " where "
					+ DBOpenHelper.COLUM_NAME + "  = ? and "
					+ DBOpenHelper.COLUM_USERID + " = ?";
			db.execSQL(sql, new Object[] { packageName,
					SYUserManager.getInstance().getUserId() });
			Slog.d("callback", "成功删除纪录");
			BroadcastUtil.sendDeleteBroadCast(context, packageBean);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	/**
	 * 
	 * 返回所有下载包 设置界面(包括卸载)
	 * 
	 * @return
	 */

	public List<PackageBean> queryDone() {
		SQLiteDatabase db = getConnection();
		List<PackageBean> packageNames = new ArrayList<PackageBean>();
		try {
			db.beginTransaction();
			String sql = "select * from " + DBOpenHelper.TAB_PACKAGE
					+ " where " + DBOpenHelper.COLUM_USERID + " = ? ";
			Slog.d("callback", "sql--------------->" + sql);
			Cursor rawQuery = db.rawQuery(sql, new String[] { SYUserManager
					.getInstance().getUserId() });
			while (rawQuery.moveToNext()) {
				PackageBean bean = new PackageBean();
				String packageId = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_ID));
				bean.setPackageId(packageId);
				String fileName = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_FILENAME));
				bean.setFileName(fileName);
				String iconurl = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_ICON));
				bean.setIconUrl(iconurl);
				String packagename = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_NAME));
				bean.setPackageName(packagename);
				Long packagesize = rawQuery.getLong(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_SIZE));
				bean.setPackageSize(packagesize);
				int isNew = rawQuery.getInt(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_NEW));
				bean.setIsNew(isNew);
				int isdownloaded = rawQuery.getInt(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_DOWN));
				bean.setIsDownloaded(isdownloaded);
				long sortno = rawQuery.getLong(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_SORT));
				bean.setSortNo(sortno);
				Slog.d("callback", "已下载包名－－－－－－－－－" + fileName);

				packageNames.add(bean);
			}
		} finally {
			db.endTransaction();
		}
		db.close();
		return packageNames;
	}

	/**
	 * 
	 * 返回所有下载包（没有卸载）
	 * 
	 * @return
	 */

	public List<PackageBean> queryExist() {
		SQLiteDatabase db = getConnection();
		List<PackageBean> packageNames = new ArrayList<PackageBean>();
		try {
			db.beginTransaction();
			String sql = "select * from " + DBOpenHelper.TAB_PACKAGE
					+ " where " + DBOpenHelper.COLUM_USERID + " = ?  and "
					+ DBOpenHelper.COLUM_ISEXIST + " = 1";
			Slog.d("callback", "sql--------------->" + sql);
			Cursor rawQuery = db.rawQuery(sql, new String[] { SYUserManager
					.getInstance().getUserId() });
			while (rawQuery.moveToNext()) {
				PackageBean bean = new PackageBean();
				String packageId = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_ID));
				bean.setPackageId(packageId);
				String fileName = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_FILENAME));
				bean.setFileName(fileName);
				String iconurl = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_ICON));
				bean.setIconUrl(iconurl);
				String packagename = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_NAME));
				bean.setPackageName(packagename);
				Long packagesize = rawQuery.getLong(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_SIZE));
				bean.setPackageSize(packagesize);
				int isNew = rawQuery.getInt(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_NEW));
				bean.setIsNew(isNew);
				int isdownloaded = rawQuery.getInt(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_DOWN));
				bean.setIsDownloaded(isdownloaded);
				long sortno = rawQuery.getLong(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_SORT));
				bean.setSortNo(sortno);
				Slog.d("callback", "已下载包名－－－－－－－－－" + fileName);
				packageNames.add(bean);

			}

		} finally {

			db.endTransaction();

		}

		db.close();

		return packageNames;

	}

	/**
	 * 
	 * 通过packageid查找packagebean
	 * 
	 * @param packageId
	 * @return
	 */

	public PackageBean queryByPackageId(String packageId) {
		SQLiteDatabase db = getConnection();
		PackageBean bean = null;

		try {
			db.beginTransaction();
			String sql = "select * from " + DBOpenHelper.TAB_PACKAGE
					+ " where " + DBOpenHelper.COLUM_USERID + " = ? and "
					+ DBOpenHelper.COLUM_ID + " = ?";
			Slog.d("callback", "sql--------------->" + sql);
			Cursor rawQuery = db.rawQuery(sql, new String[] {
					SYUserManager.getInstance().getUserId(), packageId });
			if (rawQuery.moveToNext()) {
				bean = new PackageBean();
				bean.setPackageId(packageId);
				String fileName = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_FILENAME));
				bean.setFileName(fileName);
				String iconurl = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_ICON));
				bean.setIconUrl(iconurl);
				String packagename = rawQuery.getString(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_NAME));
				bean.setPackageName(packagename);
				Long packagesize = rawQuery.getLong(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_SIZE));
				bean.setPackageSize(packagesize);
				int isNew = rawQuery.getInt(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_NEW));
				bean.setIsNew(isNew);
				int isdownloaded = rawQuery.getInt(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_DOWN));
				bean.setIsDownloaded(isdownloaded);
				long sortno = rawQuery.getLong(rawQuery
						.getColumnIndex(DBOpenHelper.COLUM_SORT));
				bean.setSortNo(sortno);
				Slog.d("callback", "已下载包名－－－－－－－－－" + fileName);
			}

		} finally {
			if (db != null)
				db.endTransaction();
		}
		db.close();
		return bean;

	}

	/**
	 * 返回所有下载包的tab和item信息
	 * 
	 * @param context
	 * @return
	 */

	public List<ExpressionPackage> getAllExpPackage(Context context) {

		List<PackageBean> packageNames = queryExist();
		List<ExpressionPackage> packages = new ArrayList<ExpressionPackage>();
		String path = context.getFilesDir()+Constants.PACKAGE_DOWNURL + File.separator
				+ SYUserManager.getInstance().getUserId();

		for (int i = 0; packageNames != null && i < packageNames.size(); i++) {
			ExpressionPackage packag = new ExpressionPackage();
			PackageBean bean = packageNames.get(i);
			String fileName = bean.getFileName();
			int index = fileName.indexOf(".");
			fileName = fileName.substring(0, index);

			List<ExpressionBean> expressions = ZipUtil.readExpressionEntity(
					context, path, fileName);
			ExpressionTab tab = new ExpressionTab();
			tab.setFileName(fileName);

			packag.setExpressionBeans(expressions);
			packag.setTab(tab);
			packages.add(packag);
		}
		return packages;

	}

	/**
	 * 
	 * 返回单个 tab和item信息
	 * 
	 * 
	 * 
	 * @param context
	 * 
	 * @param packageId
	 * 
	 * @return
	 */

	public ExpressionPackage getExpPackageById(Context context, String packageId) {
		PackageBean bean = queryByPackageId(packageId);
		ExpressionPackage mPackage = null;
		String path = context.getFilesDir()+Constants.PACKAGE_DOWNURL + File.separator
				+ SYUserManager.getInstance().getUserId();
		if (bean != null) {
			mPackage = new ExpressionPackage();
			String fileName = bean.getFileName();
			int index = fileName.indexOf(".");
			fileName = fileName.substring(0, index);
			List<ExpressionBean> expressions = ZipUtil.readExpressionEntity(
					context, path, fileName);
			ExpressionTab tab = new ExpressionTab();
			tab.setFileName(fileName);
			mPackage.setExpressionBeans(expressions);
			mPackage.setTab(tab);
		}

		return mPackage;

	}

}
