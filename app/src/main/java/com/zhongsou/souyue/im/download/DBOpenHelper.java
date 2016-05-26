package com.zhongsou.souyue.im.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * 表情模块db
 * 
 * @author wangqiang
 * 
 * 
 */

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final String DBNAME = "expression.db";
	private static final int VERSION = 2;
	
	public static final String TAB_PACKAGE = "packagelog"; // 下载表情包表
	// 表情字段
	public static final String COLUM_ID = "packageId";
	public static final String COLUM_ICON = "iconurl";
	public static final String COLUM_NAME = "packagename";
	public static final String COLUM_PRICE = "price";
	public static final String COLUM_SIZE = "packagesize";
	public static final String COLUM_NEW = "isnew";
	public static final String COLUM_DOWN = "isdownloaded";
	public static final String COLUM_SORT = "sortno";
	public static final String COLUM_FILENAME = "filename";
	public static final String COLUM_USERID = "userid"; // 用户id
	public static final String COLUM_ISEXIST = "exist"; // 是否存在
	// 表情sql
	public static final String SQL_CREATE_PACKAGELOG = "CREATE TABLE "
	+ TAB_PACKAGE + "(" + " id INTEGER primary key autoincrement, "
	+ COLUM_ID + " varchar(100), " + COLUM_ICON + " varchar(100) ,"
	+ COLUM_NAME + " varchar(100) , " + COLUM_PRICE + " varchar(100), "
	+ COLUM_SIZE + " varchar(10), " + COLUM_NEW + " TEXT(1), "
	+ COLUM_DOWN + " TEXT(1), " + COLUM_SORT + " varchar(10), "
	+ COLUM_FILENAME + " varchar(100), " + COLUM_USERID
	+ " varchar(100) , " + COLUM_ISEXIST + " varchar(10))";

	// 下载纪录
	public static final String TAB_DOWNLOG = "downloadlog"; // 下载纪录
	public static final String COLUM_COMPLETE = "completesize";// 完成度
	public static final String COLUM_TOTAL = "totalsize"; // 文件大小
	public static final String COLUM_URL = "downurl"; // 下载文件
	public static final String COLUM_PACKAGEID = "packageid";
	public static final String SQL_CRETE_DOWNLOG = "CREATE TABLE "
	+ TAB_DOWNLOG + "(" + " id INTEGER primary key autoincrement, "
	+ COLUM_COMPLETE + " int , " + COLUM_TOTAL + " int , " + COLUM_URL
	+ " varchar(100), " + COLUM_PACKAGEID + " varchar(100) )";
	// 创建浏览纪录表
	public static final String TAB_PACKAGE_HISTORY = "packagehistory"; // 浏览历史（一页）
	// 浏览记录sql
	public static final String SQL_CREATE_HISTORY = "CREATE TABLE "
	+ TAB_PACKAGE_HISTORY + "("
	+ " id INTEGER primary key autoincrement, " + COLUM_ID
	+ " varchar(100), " + COLUM_ICON + " varchar(100) ," + COLUM_NAME
	+ " varchar(100) , " + COLUM_PRICE + " varchar(100), " + COLUM_SIZE
	+ " varchar(10), " + COLUM_NEW + " TEXT(1), " + COLUM_DOWN
	+ " TEXT(1), " + COLUM_SORT + " varchar(10), " + COLUM_FILENAME
	+ " varchar(100), " + COLUM_USERID + " varchar(100) , "
	+ COLUM_ISEXIST + " varchar(10))";
	
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_PACKAGELOG);
		db.execSQL(SQL_CRETE_DOWNLOG);
		db.execSQL(SQL_CREATE_HISTORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS packagelog");
		db.execSQL("DROP TABLE IF EXISTS downloadlog");
		db.execSQL("DROP TABLE IF EXISTS packagehistory");
		onCreate(db);
	}

}
