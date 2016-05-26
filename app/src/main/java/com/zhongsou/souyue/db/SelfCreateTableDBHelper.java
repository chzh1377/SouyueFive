package com.zhongsou.souyue.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.zhongsou.souyue.module.SelfCreateItem;

import java.util.ArrayList;
import java.util.List;


public class SelfCreateTableDBHelper extends SouYueDBHelper {
	private static String whereClause = SELF_CREATE_KEY + "=?";
	public long insert(SelfCreateItem sci) {
		long num = 0;
		ContentValues values = new ContentValues();
		values.put(SELF_CREATE_ID, sci.id());
		values.put(SELF_CREATE_TITLE, sci.title());
		values.put(SELF_CREATE_KEYWORD, sci.keyword());
		values.put(SELF_CREATE_SRPID, sci.srpId());
		values.put(SELF_CREATE_MD5, sci.md5());
		values.put(SELF_CREATE_COLUMN_TYPE, sci.column_type());
		values.put(SELF_CREATE_NAME, sci.column_name());
		values.put(SELF_CREATE_CONTENT, sci.content());
		values.put(SELF_CREATE_CONPIC, sci.conpic());
		values.put(SELF_CREATE_PUBTIME, sci.pubtime());
		values.put(SELF_CREATE_STATUS, 4);
		num = db.insert(TABLE_SELF_CREATE, null, values);
		return num;
	}

	public long delete(SelfCreateItem sci) {
		long num = 0;
		String[] whereArgs = { sci._id() + "" };
		num = db.delete(TABLE_SELF_CREATE, whereClause, whereArgs);
		return num;
	}
	
	public void deleteAll(){
		db.delete(TABLE_SELF_CREATE, null, null);
	}
	public List<SelfCreateItem> select(String str) {
	    return select(str, null,null);
	}
	/**
	 * 
	 * @param str TYPE
	 * @param key KEY_WORD
	 * @return
	 */
	public List<SelfCreateItem> select(String str, String key,String key1) {
		List<SelfCreateItem> res = new ArrayList<SelfCreateItem>();
		String[] args = null;
		String wheres = null;
		if(!"0".equals(str)){
//			args =  new String[]{ConstantsUtils.STATUS_STR_SEND_FAIL};
//			wheres = SELF_CREATE_STATUS + "=?";
//		}else{
//			args = new String[]{ConstantsUtils.STATUS_STR_SEND_FAIL, str + ""};
//			wheres = SELF_CREATE_STATUS + "=?" + " and " + SELF_CREATE_COLUMN_TYPE + "=?";
		    if(key == null) {
		        args = new String[]{ str};
		        wheres = SELF_CREATE_COLUMN_TYPE + "=?";
		    } else {
		        args = new String[]{ str, key,key1};
                wheres = SELF_CREATE_COLUMN_TYPE + "=? and "+(SELF_CREATE_KEYWORD + "=? or " +SELF_CREATE_KEYWORD+"=?");
		    }
		}
		SelfCreateItem sci;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_SELF_CREATE,SELF_COLUMNS, wheres, args, null, null, null);
			while (cursor.moveToNext()) {
				sci = new SelfCreateItem();
				sci._id_$eq(cursor.getString(SELF_CREATE_KEY_INDEX));
				sci.id_$eq(cursor.getString(SELF_CREATE_ID_INDEX));
				sci.title_$eq(cursor.getString(SELF_CREATE_TITLE_INDEX));
				sci.keyword_$eq(cursor.getString(SELF_CREATE_KEYWORD_INDEX));
				sci.srpId_$eq(cursor.getString(SELF_CREATE_SRPID_INDEX));
				sci.md5_$eq(cursor.getString(SELF_CREATE_MD5_INDEX));
				sci.column_name_$eq(cursor.getString(SELF_CREATE_NAME_INDEX));
				sci.column_type_$eq(cursor.getLong(SELF_CREATE_COLUMN_TYPE_INDEX));
				sci.content_$eq(cursor.getString(SELF_CREATE_CONTENT_INDEX));
				sci.pubtime_$eq(cursor.getString(SELF_CREATE_PUBTIME_INDEX));
				sci.status_$eq(cursor.getInt(SELF_CREATE_STATUS_INDEX));
				sci.conpics_$eq(stringToList(cursor.getString(SELF_CREATE_CONPIC_INDEX)));
				res.add(sci);
			}
		} catch (Exception e) {
			return null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return res;
	}
	
	public String selectKeyId(SelfCreateItem sci, long rowid){
		StringBuilder wheres = new StringBuilder();
		wheres.append(SELF_CREATE_TITLE).append("=? and ")
			  .append(SELF_CREATE_KEYWORD).append("=? and ")
			  .append(SELF_CREATE_SRPID).append("=? and ")
			  .append(SELF_CREATE_MD5).append("=? and ")
			  .append(SELF_CREATE_NAME).append("=? and ")
			  .append(SELF_CREATE_CONTENT).append("=? and ")
			  .append(SELF_CREATE_PUBTIME).append("=? and ")
			  .append("rowid").append("=?");
			  
		String[] args = new String[]{
				sci.title(), sci.keyword(), sci.srpId(), sci.md5(), sci.column_name(), sci.content(), sci.pubtime(), "" + rowid
		};
		String _id = "";
//		String rid = null;
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_SELF_CREATE, new String[]{"rowid, _id"}, wheres.toString(), args, null, null, null);
			if (cursor.moveToFirst()){
//				rid = cursor.getString(0);
				_id = cursor.getString(1);
			}
		} catch (Exception e){
			
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return _id;
	}
	
//	public SelfCreateItem selectItem(){
//		SelfCreateItem sci = null;
//		String[] selectionArgs = { ConstantsUtils.STATUS_STR_SEND_ING };
//		Cursor cursor = null;
//		try {
//			cursor = db.query(TABLE_SELF_CREATE,SELF_COLUMNS, whereByStatus, selectionArgs, null, null, "IDS desc");
//			if (cursor.moveToFirst()) {
//				sci = new SelfCreateItem();
//				sci._id_$eq(cursor.getString(SELF_CREATE_KEY_INDEX));
//				sci.id_$eq(cursor.getString(SELF_CREATE_ID_INDEX));
//				sci.title_$eq(cursor.getString(SELF_CREATE_TITLE_INDEX));
//				sci.keyword_$eq(cursor.getString(SELF_CREATE_KEYWORD_INDEX));
//				sci.srpId_$eq(cursor.getString(SELF_CREATE_SRPID_INDEX));
//				sci.md5_$eq(cursor.getString(SELF_CREATE_MD5_INDEX));
//				sci.column_name_$eq(cursor.getString(SELF_CREATE_NAME_INDEX));
//				sci.column_type_$eq(cursor.getLong(SELF_CREATE_COLUMN_TYPE_INDEX));
//				sci.title_$eq(cursor.getString(SELF_CREATE_TITLE_INDEX));
//				sci.content_$eq(cursor.getString(SELF_CREATE_CONTENT_INDEX));
//				sci.pubtime_$eq(cursor.getString(SELF_CREATE_PUBTIME_INDEX));
//				sci.status_$eq(cursor.getInt(SELF_CREATE_STATUS_INDEX));
//				sci.conpic_$eq(cursor.getString(SELF_CREATE_CONPIC_INDEX));
//				sci.conpics_$eq(stringToList(cursor.getString(SELF_CREATE_CONPIC_INDEX)));
//			}
//		} catch (Exception e) {
//			return null;
//		} finally {
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.close();
//			}
//		}
//		return sci;
//	}

	public long update(SelfCreateItem sci) {
		long num = 0;
		ContentValues values = new ContentValues();
		values.put(SELF_CREATE_ID, sci.id());
		values.put(SELF_CREATE_TITLE, sci.title());
		values.put(SELF_CREATE_KEYWORD, sci.keyword());
		values.put(SELF_CREATE_SRPID, sci.srpId());
		values.put(SELF_CREATE_MD5, sci.md5());
		values.put(SELF_CREATE_COLUMN_TYPE, sci.column_type());
		values.put(SELF_CREATE_NAME, sci.column_name());
		values.put(SELF_CREATE_CONTENT, sci.content());
		values.put(SELF_CREATE_CONPIC, sci.conpic());
		values.put(SELF_CREATE_PUBTIME, sci.pubtime());
		values.put(SELF_CREATE_STATUS, sci.status());
		String[] whereArgs = { sci._id() + "" };
		num = db.update(TABLE_SELF_CREATE, values, whereClause, whereArgs);
		return num;
	}
	
	private List<String> stringToList(String str){
		List<String> res = new ArrayList<String>();
		String[] ss;
		if (str.length() > 0){
			ss = str.trim().split(" ");
			for(String s: ss){
				res.add(s);
			}
		}
		return res;
	}
	//判断数据表是否存在
	public boolean isTableExist(){
	    boolean result=false;
	    Cursor cursor = null ;
	    try {
	        cursor = db.rawQuery( "SELECT * FROM " + TABLE_SELF_CREATE + " LIMIT 0"
                , null );
            result = cursor != null && cursor.getColumnIndex(SELF_CREATE_ID) != -1 ;
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