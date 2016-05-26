package com.zhongsou.souyue.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author chefb@zhongsou.com state 1点击过
 */
public class WendaAnswerTableDBHelper extends SouYueDBHelper {

	public long insert(String questionId, String answerId, int state,int upOrDown) {
		long num = 0;
		ContentValues values = new ContentValues();
		values.put(QUESTION_ID, questionId);
		values.put(ANSWER_ID, answerId);
		values.put(ANSWER_STATE, state);
		values.put(UPORDOWN, upOrDown);
		num = db.insert(TABLE_WENDA_ANSWER, null, values);
		// Log.d("WENDAANSWERTABLE_INSERT","QUESTION_ID:"+questionId+"ANSWER_ID:"+answerId+"ANSWER_STATE:"+state);
		return num;
	}

	public void update(String questionId, String answerId, int state,int upOrDown) {
		ContentValues values = new ContentValues();
		values.put(ANSWER_STATE, state);
		if (upOrDown != -1)
			values.put(UPORDOWN, upOrDown);
		db.update(TABLE_WENDA_ANSWER, values, QUESTION_ID + "=? and "
			+ ANSWER_ID + "=?", new String[] { questionId, answerId });
		// Log.d("WENDAANSWERTABLE_update","QUESTION_ID:"+questionId+"ANSWER_ID:"+answerId+"ANSWER_STATE:"+state);
	}

	/**
	 * return state -1 0 1
	 */
	public int select(String questionId, String answerId) {
        int state = 0;
		Cursor cursor = null;
       try{
           cursor = db.query(TABLE_WENDA_ANSWER,
                   new String[] { ANSWER_STATE }, QUESTION_ID + "=? and "
                   + ANSWER_ID + "=?",
                   new String[] { questionId, answerId }, null, null, null);
           if (cursor.moveToFirst()) {
               state = cursor.getInt(0);
           } else {
               state = -1;
           }
       } catch (Exception e){
           state = -1;
       } finally {
           if (cursor != null && !cursor.isClosed())
               cursor.close();;
       }
        return state;
    }

	public int selectUpOrDown(String questionId, String answerId) {
        Cursor cursor = null;
        int upordown = 0;
        try {
        cursor = db.query(TABLE_WENDA_ANSWER, new String[] { UPORDOWN },
				QUESTION_ID + "=? and " + ANSWER_ID + "=? and "+ANSWER_STATE+"=1", new String[] {
						questionId, answerId }, null, null, null);
		if (cursor.moveToFirst()) {
			upordown = cursor.getInt(0);
		} else {
            upordown = -1;
		}
        } catch (Exception ex){

        } finally {
           if (cursor != null && !cursor.isClosed())
               cursor.close();;

        }
        return upordown;
    }
}
