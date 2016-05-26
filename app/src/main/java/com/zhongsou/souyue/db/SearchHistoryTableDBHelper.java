package com.zhongsou.souyue.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.zhongsou.souyue.module.ToolTip;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryTableDBHelper extends SouYueDBHelper{


	public long insert(ToolTip t){
		long num = 0;
		ContentValues values = new ContentValues();
		values.put(HISTORY_CATEGORY, t.category());
		values.put(HISTORY_RSSIMAGE, t.rssImage());
		values.put(HISTORY_URL, t.url());
		values.put(HISTORY_KEYWORD, t.keyword());
		values.put(HISTORY_SRPID, t.srpId());
		values.put(HISTORY_SRPCATE, t.srpCate());
		values.put(HISTORY_M, t.m());
		values.put(HISTORY_G, t.g());
		values.put(HISTORY_LASTUPDATE, System.currentTimeMillis());
//        values.put(HISTORY_VERSION, t.version());
		num = db.insert(TABLE_SEARCH_HISTORY, null, values);
		return num;
	}
	

 
	private static String whereClausekeyword = //
	HISTORY_CATEGORY + "=? and "//
			+ HISTORY_RSSIMAGE + "=? and "//
			+ HISTORY_URL + "=? and "//
			+ HISTORY_KEYWORD + "=? and "//
			+ HISTORY_SRPID + "=? and "//
			+ HISTORY_SRPCATE + "=? and "//
			+ HISTORY_M + "=? and "//
			+ HISTORY_G + "=?";
	
	/**
	 * 更新搜索时间
	 */
	public long update(ToolTip t) {
		long num = 0;
		ContentValues values = new ContentValues();
		values.put(HISTORY_LASTUPDATE, System.currentTimeMillis());
//        values.put(HISTORY_VERSION, t.version());
		String[] whereArgs = {t.category(), t.rssImage(),t.url(),t.keyword(),t.srpId(),t.srpCate(),t.m(),t.g()};
		num = db.update(TABLE_SEARCH_HISTORY, values, whereClausekeyword,whereArgs);
		return num;
	}
	public long delete(ToolTip t){
		long num = 0;
		String[] whereArgs = {t.category(), t.rssImage(),t.url(),t.keyword(),t.srpId(),t.srpCate(),t.m(),t.g()};
		num = db.delete(TABLE_SEARCH_HISTORY, whereClausekeyword, whereArgs);
		return num;
	}
	public boolean clear(){
		db.delete(TABLE_SEARCH_HISTORY, null, null);
		return true;
	}

	public List<ToolTip> select(){
		List<ToolTip> list = null;
        Cursor cursor = null;
        try{
		cursor = this.db.query(TABLE_SEARCH_HISTORY, HISTORY_COLUMNS,
				null, null, null, null, HISTORY_LASTUPDATE+" desc");
		if(cursor.getCount()>0){
			list = new ArrayList<ToolTip>();
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
				ToolTip toolTip = new ToolTip();
				toolTip.category_$eq(cursor.getString(0));
				toolTip.rssImage_$eq(cursor.getString(1));
				toolTip.url_$eq(cursor.getString(2));
				toolTip.keyword_$eq(cursor.getString(3));
				toolTip.srpId_$eq(cursor.getString(4));
				toolTip.srpCate_$eq(cursor.getString(5));
				toolTip.m_$eq(cursor.getString(6));
				toolTip.g_$eq(cursor.getString(7));
//                toolTip.version_$eq(cursor.getString(9));
				list.add(toolTip);
			}
		}
        } catch (Exception e)  {
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
		return list;
	}
	
}