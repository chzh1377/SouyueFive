package com.tuita.sdk;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by zoulu
 * on 2015/1/4
 * Description:搜悦user表helper，读取当前
 */
public class UserDBHelper extends SqliteUtil{
    public static final String[] USER_COLUMNS = { USER_ID, USER_NAME, USER_IMAGE, USER_TOKEN, USER_EMAIL, USER_TYPE, USER_LOGIN_NAME, USER_BGURL, USER_SIGNATURE,USER_SEX};
    private static String whereClause = USER_TYPE + "=?";

    public UserDBHelper(Context context) {
        super(context);
    }


    public SYUserBean select(String userType) {
        SYUserBean user = null;
        Cursor cursor = null;
        String[] selectionArgs = { userType };
        try {

            cursor = db.query(TABLE_USER,USER_COLUMNS, whereClause, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                user = new SYUserBean();
                user.userId_$eq(cursor.getLong(USER_ID_INDEX));
                user.name_$eq(cursor.getString(USER_NAME_INDEX));
                user.image_$eq(cursor.getString(USER_IMAGE_INDEX));
                user.token_$eq(cursor.getString(USER_TOKEN_INDEX));
                user.email_$eq(cursor.getString(USER_EMAIL_INDEX));
                user.userType_$eq(cursor.getString(USER_TYPE_INDEX));
                user.userName_$eq(cursor.getString(USER_LOGIN_NAME_INDEX));
                user.bgUrl_$eq(cursor.getString(USER_BGURL_INDEX));
                user.signature_$eq(cursor.getString(USER_SIGNATURE_INDEX));
                String sex = cursor.getString(USER_SEX_INDEX);
                if(sex != null && !"".equals(sex)) {
                    user.setSex(Integer.parseInt(sex));
                }else {
                    user.setSex(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if(cursor==null||USER_COLUMNS.length>cursor.getColumnCount()){
                db.execSQL("DROP TABLE IF EXISTS " + "USER");
                db.execSQL(SqliteUtil.SQL_CREATE_USER);
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

}
