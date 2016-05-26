package com.zhongsou.souyue.download;
 
 import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
     /**
      * 建立一个数据库帮助类
      */
 public class DownloadDBHelper extends SQLiteOpenHelper {
     //sydownload.db-->数据库名
     public DownloadDBHelper(Context context) {
         super(context, "sydownload.db", null, 1);
     }
     
     /**
      * 在sydownload.db数据库下创建一个download_info表存储下载信息
      */
     @Override
     public void onCreate(SQLiteDatabase db) {
    	 //分行写有 sql=" "+" "+" "; 不分行sql=" ";
//    	 String sql ="create table download_info"
//    	 		+ "(_id integer PRIMARY KEY AUTOINCREMENT, "
//    	 		+ "thread_id integer, "
//    	 		+ "start_pos integer, "
//    	 		+ "end_pos integer, "
//    	 		+ "compelete_size integer,"
//    	 		+ "url char)";
    	 String sql ="create table download_info"
     	 		+ "(_id integer PRIMARY KEY AUTOINCREMENT, "
    			 
//     	 		+ "only_id integer, "	//服务器传来的唯一id，作为一个视频（可能含有多段视频url）的唯一标示
     	 		+ "only_id char, "	//服务器传来的唯一id，作为一个视频（可能含有多段视频url）的唯一标示
     	 		+ "name char,"
     	 		+ "length integer,"
     	 		+ "cur_length integer,"
     	 		+ "type integer,"
     	 		+ "urls text,"
     	 		+ "cur_url char,"
     	 		+ "image char,"
     	 		+ "status integer,"		//下载状态
     	 		
     	 		+ "thread_id integer, "	//下载器id
     	 		+ "start_pos integer, "
     	 		+ "end_pos integer, "
     	 		+ "compelete_size integer,"
     	 		+ "url char)";		//类型： 1视频  2小说
    	 db.execSQL(sql);
     }
     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
     }
 
 }