package com.zhongsou.souyue.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 一个业务类
 */
public class DownloadDao {  
	private static DownloadDao dao=null;
	private Context context; 
	private  DownloadDao(Context context) { 
		this.context=context;
	}
	public static  DownloadDao getInstance(Context context){
		if(dao==null){
			dao=new DownloadDao(context); 
		}
		return dao;
	}
	public  SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try { 
			sqliteDatabase= new DownloadDBHelper(context).getReadableDatabase();
		} catch (Exception e) {  
		}
		return sqliteDatabase;
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public synchronized boolean isHasInfors(String onlyId) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from download_info where only_id =?";
			cursor = database.rawQuery(sql, new String[] { onlyId });
			
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return count > 0;
	}
/*
	*//**
	 * 保存 下载的具体信息
	 *//*
	public synchronized void saveInfos(List<DownloadInfo> infos) {
		SQLiteDatabase database = getConnection();
		try {
			for (DownloadInfo info : infos) {
				String sql = "insert into download_info("
						+ "only_id , "	//服务器传来的唯一id，作为一个视频（可能含有多段视频url）的唯一标示
		     	 		+ "name ,"
		     	 		+ "length ,"
		     	 		+ "cur_length ,"
		     	 		+ "type ,"
		     	 		+ "urls ,"
		     	 		+ "cur_url ,"
		     	 		+ "image ,"
		     	 		+ "status ,"		//下载状态
		     	 		
						+ "thread_id,"
						+ "start_pos, "
						+ "end_pos,"
						+ "end_pos,"
						+ "url) values (?,?,?,?,?,?,?,?,?, ?,?,?,?,?)";
				Object[] bindArgs = { 
						info.getOnlyId(),info.getName(),info.getLength(),
						info.getCurLength(),info.getType(),info.getUrls(),
						info.getCurUrl(),info.getImgUrl(),info.getState(),
						
						info.getThreadId(), info.getStartPos(),info.getEndPos(), 
						info.getCompeleteSize(),info.getUrl() };
				database.execSQL(sql, bindArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
*/
	/**
	 * 根据id获得一个具体的下载信息
	 */
	public synchronized DownloadInfo getInfo(String onlyId) {
		SQLiteDatabase database = getConnection();
		DownloadInfo info = null;
		Cursor cursor = null;
		try {
			String sql = "select "
					+ "only_id , "	
	     	 		+ "name ,"
	     	 		+ "length ,"
	     	 		
	     	 		+ "cur_length ,"
	     	 		+ "type ,"
	     	 		+ "urls ,"
	     	 		
	     	 		+ "cur_url ,"
	     	 		+ "image ,"
	     	 		+ "status ,"		
	     	 		
					+ "thread_id, start_pos, end_pos ,compelete_size ,url from download_info where only_id = ? ";
			cursor = database.rawQuery(sql, new String[] {onlyId});
			while (cursor.moveToNext()) {
				info = new DownloadInfo(
						cursor.getString(0),cursor.getString(1), cursor.getInt(2),
						cursor.getInt(3),cursor.getInt(4), cursor.getString(5),
						cursor.getString(6),cursor.getString(7), cursor.getInt(8),
						cursor.getInt(9),cursor.getInt(10), cursor.getInt(11), 
						cursor.getInt(12),cursor.getString(13));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return info;
	}
	
	/**
	 * 获取已缓存的数据
	 */
	
	public synchronized List<DownloadInfo> getHasDownloadInfos(int type) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select "
					+ "only_id , "	
	     	 		+ "name ,"
	     	 		+ "length ,"
	     	 		
	     	 		+ "cur_length ,"
	     	 		+ "type ,"
	     	 		+ "urls ,"
	     	 		
	     	 		+ "cur_url ,"
	     	 		+ "image ,"
	     	 		+ "status ,"		
	     	 		
					+ "thread_id, start_pos, end_pos,compelete_size,url from download_info where type = ? and status=" + DownloadInfo.STATE_COMPLETE;
			cursor = database.rawQuery(sql, new String[] {String.valueOf(type)});
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(
						cursor.getString(0),cursor.getString(1), cursor.getInt(2),
						cursor.getInt(3),cursor.getInt(4), cursor.getString(5),
						cursor.getString(6),cursor.getString(7), cursor.getInt(8),
						cursor.getInt(9),cursor.getInt(10), cursor.getInt(11), 
						cursor.getInt(12),cursor.getString(13));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}
	
	/**
	 * 获取正在缓存中的数据
	 */

	public synchronized List<DownloadInfo> getDownloadingInfos(int type) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select "
					+ "only_id , "	
	     	 		+ "name ,"
	     	 		+ "length ,"
	     	 		
	     	 		+ "cur_length ,"
	     	 		+ "type ,"
	     	 		+ "urls ,"
	     	 		
	     	 		+ "cur_url ,"
	     	 		+ "image ,"
	     	 		+ "status ,"		
	     	 		
					+ "thread_id, start_pos, end_pos,compelete_size,url from download_info where status in (1,2,3,4) and type=?";
			cursor = database.rawQuery(sql, new String[] {String.valueOf(type)});
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(
						cursor.getString(0),cursor.getString(1), cursor.getInt(2),
						cursor.getInt(3),cursor.getInt(4), cursor.getString(5),
						cursor.getString(6),cursor.getString(7), cursor.getInt(8),
						cursor.getInt(9),cursor.getInt(10), cursor.getInt(11), 
						cursor.getInt(12),cursor.getString(13));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}
	
	/**
	 * 插入数据库
	 */
	
	public synchronized void insertInfos(DownloadInfo info) {
		SQLiteDatabase database = getConnection();
		try {
				String sql = "insert into download_info("
						+ "only_id , "	//服务器传来的唯一id，作为一个视频（可能含有多段视频url）的唯一标示
		     	 		+ "name ,"
		     	 		+ "length ,"
		     	 		+ "cur_length ,"
		     	 		+ "type ,"
		     	 		+ "urls ,"
		     	 		+ "cur_url ,"
		     	 		+ "image ,"
		     	 		+ "status ,"		//下载状态
		     	 		
						+ "thread_id,"
						+ "start_pos, "
						+ "end_pos,"
						+"compelete_size,"
						+ "url) values (?,?,?,?,?,?,?,?,?, ?,?,?,?,?)";
				Object[] bindArgs = { 
						info.getOnlyId(),info.getName(),info.getLength(),
						info.getCurLength(),info.getType(),info.getUrls(),
						info.getCurUrl(),info.getImgUrl(),info.getState(),
						
						info.getThreadId(), info.getStartPos(),info.getEndPos(), 
						info.getCompeleteSize(),info.getUrl() };
				database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	/**
	 * 更新已完成的进度，当前下载的url
	 */
	
	public synchronized void updataInfo(String onlyId,long compeleteSize,String curUrl,long curLength ) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set compelete_size=?,cur_url=?,cur_length=? where only_id=?";
			Object[] bindArgs = {compeleteSize ,curUrl,curLength,onlyId};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	/**
	 * 更新下载状态
	 */
	
	public synchronized void updataState(String onlyId,int state ) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set status=? where only_id=?";
			Object[] bindArgs = {state,onlyId};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	
	
	/**
	 * 更新数据库中的下载信息
	 */
	public synchronized void updataInfos(String onlyId,int compeleteSize ) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set compelete_size=? where thread_id=? and only_id=?";
			Object[] bindArgs = { onlyId , compeleteSize };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public synchronized void delete(String onlyId) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("download_info", "only_id=?", new String[] { onlyId });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
}