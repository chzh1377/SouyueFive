package com.zhongsou.souyue.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @author huanglb@zhongsou.com
 */

public class UserTableDBHelper extends SouYueDBHelper {
    private static String whereClause = USER_TYPE + "=?";

    private static String whereClauseById = USER_ID + "=?";

    public long insert(User user) {
        long num = 0;
        ContentValues values = new ContentValues();
        values.put(USER_ID, user.userId());
        values.put(USER_NAME, user.name());
        values.put(USER_IMAGE, user.image());
        values.put(USER_TOKEN, user.token());
        values.put(USER_EMAIL, user.email());
        values.put(USER_TYPE, user.userType());
        values.put(USER_LOGIN_NAME, user.userName());
        values.put(USER_BGURL, user.bgUrl());
        values.put(USER_SIGNATURE, user.signature());
        values.put(USER_SEX, user.getSex());
        
        values.put(USER_LEVEL, user.getUser_level());
        values.put(USER_LEVELTITLE, user.getUser_level_title());
        values.put(USER_LEVEL_TIME, user.getUser_level_time());

        //add by yinguanping 新加密协议 
        values.put(USER_OPENID, user.getOpenid());
        values.put(USER_OPID, user.getOpid());
        values.put(USER_AUTH_TOKEN, user.getAuth_token());
        values.put(USER_PRIVATE_KEY, user.getPrivate_key());
        values.put(USER_APPID, user.getAppId());
        Cursor cursor=null;
        try {
            num = db.insert(TABLE_USER, null, values);
        } catch (Exception e) {
            //如果插入数据发生异常 1 判断当前user中的信息是游客的还是登陆用户的，如果是游客的话drop数据表之后重新建表，将游客的信息插入
            //如果当前表中是登陆用户的数据 ，先查出游客的数据，drop数据表，创建数据表，插入游客信息  插入当前登陆用户的数据     
            if(user.userType().equals(SYUserManager.USER_GUEST)){//如果是游客
                db.execSQL("DROP TABLE IF EXISTS " + "USER");
                db.execSQL(SouYueDBHelper.SQL_CREATE_USER);
                num = db.insert(TABLE_USER, null, values);
            }else{
                num=fixUser(cursor,num,values);
            }
        }finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return num;
    }
    /**
     * 修复user数据
     */
    private long fixUser(Cursor cursor,long num,ContentValues values){
        try {
            cursor = db.query(TABLE_USER, new String[] {USER_ID, USER_TOKEN, USER_TYPE}, whereClause, new String[] {SYUserManager.USER_GUEST}, null, null, null);
            User guestuser = null;
            if (cursor != null && cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    guestuser = new User();
                    guestuser.userId_$eq(cursor.getLong(USER_ID_INDEX));
                    guestuser.userType_$eq(cursor.getString(USER_TYPE_INDEX));
                    guestuser.token_$eq(cursor.getString(USER_TOKEN_INDEX));
                }
            }
            db.execSQL("DROP TABLE IF EXISTS " + "USER");
            db.execSQL(SouYueDBHelper.SQL_CREATE_USER);
            if (guestuser != null) {
                ContentValues guestvalues = new ContentValues();
                guestvalues.put(USER_ID, guestuser.userId());
                guestvalues.put(USER_TOKEN, guestuser.token());
                guestvalues.put(USER_TYPE, guestuser.userType());
                db.insert(TABLE_USER, null, guestvalues);// 插入游客的数据
            }
            num = db.insert(TABLE_USER, null, values);// 插入登陆用户的数据
            return num;
        } catch (Exception e) {
        }
        return 0;
    }
    public long delete(User user) {
        long num = 0;
        String[] whereArgs = {user.userType()};
        num = db.delete(TABLE_USER, whereClause, whereArgs);
        return num;
    }

    /**
     * 查询token
     *
     * @return
     */
    public User select(String userType) {
        User user = null;
        Cursor cursor = null;
        String[] selectionArgs = {userType};
        try {
            cursor = db.query(TABLE_USER, USER_COLUMNS, whereClause, selectionArgs, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            if (cursor.moveToFirst()) {
                user = new User();
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
                if (sex != null && !"".equals(sex)) {
                    user.setSex(Integer.parseInt(sex));
                } else {
                    user.setSex(0);
                }
                user.user_level_$eq(cursor.getString(USER_LEVEL_INDEX));
                user.user_level_title_$eq(cursor.getString(USER_LEVELTITLE_INDEX));
                user.user_level_time_$eq(cursor.getString(USER_LEVEL_TIME_INDEX));

                // add by yinguanping 新加密协议 
                user.setOpenid(cursor.getString(USER_OPENID_INDEX));
                user.setOpid(cursor.getString(USER_OPID_INDEX));
                user.setAuth_token(cursor.getString(USER_AUTH_TOKEN_INDEX));
                user.setPrivate_key(cursor.getString(USER_PRIVATE_KEY_INDEX));
                user.setAppId(cursor.getString(USER_APPID_INDEX));
            }
        } catch (Exception e) {
            if(cursor==null||USER_COLUMNS.length>cursor.getColumnCount()){
                db.execSQL("DROP TABLE IF EXISTS " + "USER");
                db.execSQL(SouYueDBHelper.SQL_CREATE_USER);
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

    public User selectByUserId(long userId) {
        User user = null;
        Cursor cursor = null;
        String[] selectionArgs = {userId + ""};
        try {

            cursor = db.query(TABLE_USER, USER_COLUMNS, whereClauseById, selectionArgs, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            if (cursor.moveToFirst()) {
                user = new User();
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
                if (sex != null && !"".equals(sex)) {
                    user.setSex(Integer.parseInt(sex));
                } else {
                    user.setSex(0);
                }
                user.user_level_$eq(cursor.getString(USER_LEVEL_INDEX));
                user.user_level_title_$eq(cursor.getString(USER_LEVELTITLE_INDEX));
                user.user_level_time_$eq(cursor.getString(USER_LEVEL_TIME_INDEX));

                //add by yinguanping 新加密协议 
                user.setOpenid(cursor.getString(USER_OPENID_INDEX));
                user.setOpid(cursor.getString(USER_OPID_INDEX));
                user.setAuth_token(cursor.getString(USER_AUTH_TOKEN_INDEX));
                user.setPrivate_key(cursor.getString(USER_PRIVATE_KEY_INDEX));
                user.setAppId(cursor.getString(USER_APPID_INDEX));
            }

        } catch (Exception e) {
            if(cursor==null||USER_COLUMNS.length>cursor.getColumnCount()){
                db.execSQL("DROP TABLE IF EXISTS " + "USER");
                db.execSQL(SouYueDBHelper.SQL_CREATE_USER);
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

    /**
     * 更新userinfo
     *
     * @param user
     * @return
     */
    public long updata(User user) {
        long num = 0;
        ContentValues values = new ContentValues();
        values.put(USER_ID, user.userId());
        values.put(USER_NAME, user.name());
        values.put(USER_IMAGE, user.image());
        values.put(USER_TOKEN, user.token());
        values.put(USER_EMAIL, user.email());
        values.put(USER_TYPE, user.userType());
        values.put(USER_LOGIN_NAME, user.userName());
        values.put(USER_BGURL, user.bgUrl());
        values.put(USER_SIGNATURE, user.signature());
        values.put(USER_SEX, user.getSex());
        values.put(USER_LEVEL, user.getUser_level());
        values.put(USER_LEVELTITLE, user.getUser_level_title());
        values.put(USER_LEVEL_TIME, user.getUser_level_time());

        //add by yinguanping 新加密协议 
        values.put(USER_OPENID, user.getOpenid());
        values.put(USER_OPID, user.getOpid());
        values.put(USER_AUTH_TOKEN, user.getAuth_token());
        values.put(USER_PRIVATE_KEY, user.getPrivate_key());
        values.put(USER_APPID, user.getAppId());

        String[] whereArgs = {user.userType()};
        Cursor cursor=null;
        try {
            num = db.update(TABLE_USER, values, whereClause, whereArgs);
        } catch (Exception e) {
            //如果更新出现问题先1 查询出游客的数据，2 drop数据表，3 创建数据表 4 插入游客数据，插入登陆用户数据   
            num=fixUser(cursor,num,values);
        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }
        return num;
    }

//  /**
//   * 更新userinfo_image
//   * @return
//   */
//  public long updataImage(String image) {
//      long num = 0;
//      ContentValues values = new ContentValues();
//      values.put(USER_IMAGE, image);
//      String[] whereArgs = {"1"};
//      num = db.update(TABLE_USER, values, whereClause, whereArgs);
//      return num;
//  }
//  
//  /**
//   * 更新userinfo_name
//   * @return
//   */
//  public long updateNick(String nick) {
//      long num = 0;
//      ContentValues values = new ContentValues();
//      values.put(USER_NAME, nick);
//      String[] whereArgs = {"1"};
//      num = db.update(TABLE_USER, values, whereClause, whereArgs);
//      return num;
//  }
//  
//  public long updateBgUrl(String bgUrl){
//      long num = 0;
//      ContentValues values = new ContentValues();
//      values.put(USER_BGURL, bgUrl);
//      String[] whereArgs = {"1"};
//      num = db.update(TABLE_USER, values, whereClause, whereArgs);
//      return num;
//  }
}