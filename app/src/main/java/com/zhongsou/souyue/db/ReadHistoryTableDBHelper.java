package com.zhongsou.souyue.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * 阅读历史
 * @author wanglong@zhongsou.com
 *
 */
public class ReadHistoryTableDBHelper extends SouYueDBHelper {
	/**
	 * 插入一条新纪录
	 * @param url
	 * @return
	 */
	public long insert(String url) {
		if (StringUtils.isEmpty(url) || exists(url))
			return -1;
		ContentValues values = new ContentValues();
		values.put("MD5", Utils.getMD5Hex(url));
		return db.insert(TABLE_READ_HISTORY, null, values);
	}
	private boolean exists(String url) {
		boolean found = false;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_READ_HISTORY, new String[] { "MD5" }, "MD5 = ?", new String[] { Utils.getMD5Hex(url) }, null, null, null);
			if (cursor.moveToFirst()) {
				found = true;
			}
		} catch (Exception e) {
			return found;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return found;
	}
	/**
	 * 清库
	 * @return
	 */
	public long deleteAll() {
	    if(isTableExist()){
	        return db.delete(TABLE_READ_HISTORY, null, null);
	    }
	    return 0;
		
	}
	private static String join(int length) {
		StringBuffer sb = new StringBuffer(1024);
		for (int i = 0; i < length; i++) {
			sb.append("?,");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	/**
	 * 查询数据库中全部阅读的历史记录，返回命中的数据
	 * @param url
	 * @return
	 */
	public Set<String> select(List<String> url) {
		Set<String> result = new HashSet<String>();
		if (url == null || url.size() == 0)
			return result;
		String[] selectionArgs = new String[url.size()];
		for (int i = 0; i < selectionArgs.length; i++) {
			selectionArgs[i] = Utils.getMD5Hex(url.get(i));
		}
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_READ_HISTORY, new String[] { "MD5" }, "MD5 IN(" + join(url.size()) + ")", selectionArgs, null, null, null);
			while (cursor.moveToNext()) {
				result.add(cursor.getString(0));
			}
		} catch (Exception e) {
			return result;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}
	
    public boolean isTableExist(){
        boolean result=false;
        Cursor cursor = null ;
        try {
            cursor = db.rawQuery( "SELECT * FROM " + TABLE_READ_HISTORY + " LIMIT 0"
                , null );
            result = cursor != null && cursor.getColumnIndex("MD5") != -1 ;
        } catch (Exception e) {
            result=false;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }
        return result;
    }
}