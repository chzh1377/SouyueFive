package com.tuita.sdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zoulu on 2015/1/4 Description:读取搜悦数据库帮助类，针对非GreenDao生成的数据库
 */
public class SqliteUtil extends SQLiteOpenHelper {
    private static final String NAME = "souyue.db";
    public static final int VERSION = 15;
    protected SQLiteDatabase db;
    // 表名
    public static final String TABLE_SEARCH_HISTORY = "SEARCH_HISTORY";
    public static final String TABLE_USER = "USER";
    public static final String TABLE_READ_HISTORY = "READ_HISTORY";// 用户阅读历史
    public static final String TABLE_WENDA_ANSWER = "WENDA_ANSWER";
    public static final String TABLE_SELF_CREATE = "SELF_CREATE";

    // 用户原创表字段
    public static final String SELF_CREATE_KEY = "_id";
    public static final String SELF_CREATE_ID = "IDS";// 数据库id
    public static final String SELF_CREATE_KEYWORD = "KEYWORD";// "关键词"
    public static final String SELF_CREATE_SRPID = "SRPID";// "关键词对应srpid",
    public static final String SELF_CREATE_MD5 = "MD5";// 微件对应md5"
    public static final String SELF_CREATE_COLUMN_TYPE = "COLUMN_TYPE";// 微件类型
    public static final String SELF_CREATE_NAME = "NAME";// "栏目名",
    public static final String SELF_CREATE_TITLE = "TITLE";// 原创标题",
    public static final String SELF_CREATE_CONTENT = "CONTENT";// "原创内容",
    public static final String SELF_CREATE_CONPIC = "CONPIC";// 内容图片(若有多个图片空格隔开共同存储)
    public static final String SELF_CREATE_PUBTIME = "PUBTIME";// 发布时间
    public static final String SELF_CREATE_STATUS = "STATUS";// 审核状态
    public static final int SELF_CREATE_KEY_INDEX = 0;// 主键
    public static final int SELF_CREATE_ID_INDEX = 1;// 数据库id
    public static final int SELF_CREATE_KEYWORD_INDEX = 2;// "关键词"
    public static final int SELF_CREATE_SRPID_INDEX = 3;// "关键词对应srpid",
    public static final int SELF_CREATE_MD5_INDEX = 4;// 微件对应md5"
    public static final int SELF_CREATE_NAME_INDEX = 5;// "栏目名",
    public static final int SELF_CREATE_COLUMN_TYPE_INDEX = 6;// 微件类型
    public static final int SELF_CREATE_TITLE_INDEX = 7;// 原创标题",
    public static final int SELF_CREATE_CONTENT_INDEX = 8;// "原创内容",
    public static final int SELF_CREATE_CONPIC_INDEX = 9;// 内容图片(若有多个图片空格隔开共同存储)
    public static final int SELF_CREATE_PUBTIME_INDEX = 10;// 发布时间
    public static final int SELF_CREATE_STATUS_INDEX = 11;// 审核状态
    public static final int SELF_CREATE_SEND_STATUS_INDEX = 12;// 发送状态
    public static final String[] SELF_COLUMNS = {SELF_CREATE_KEY, SELF_CREATE_ID, SELF_CREATE_KEYWORD, SELF_CREATE_SRPID, SELF_CREATE_MD5, SELF_CREATE_NAME, SELF_CREATE_COLUMN_TYPE,
            SELF_CREATE_TITLE, SELF_CREATE_CONTENT, SELF_CREATE_CONPIC, SELF_CREATE_PUBTIME, SELF_CREATE_STATUS};

    // 历史记录表字段
    public static final String HISTORY_CATEGORY = "CATEGORY";
    public static final String HISTORY_RSSIMAGE = "RSSIMAGE";
    public static final String HISTORY_URL = "URL";
    public static final String HISTORY_KEYWORD = "KEYWORD";
    public static final String HISTORY_SRPID = "SRPID";
    public static final String HISTORY_SRPCATE = "SRPCATE";
    public static final String HISTORY_M = "M";
    public static final String HISTORY_G = "G";
    public static final String HISTORY_LASTUPDATE = "LASTUPDATE";
    public static final String HISTORY_VERSION = "SEARCH_VERSION";
    public static final String[] HISTORY_COLUMNS = {HISTORY_CATEGORY, HISTORY_RSSIMAGE, HISTORY_URL, HISTORY_KEYWORD, HISTORY_SRPID, HISTORY_SRPCATE, HISTORY_M, HISTORY_G, HISTORY_LASTUPDATE,
            HISTORY_VERSION};
    // 问答
    public static final String QUESTION_ID = "QUESTION_ID";
    public static final String ANSWER_ID = "ANSWER_ID";
    public static final String ANSWER_STATE = "ANSWER_STATE";
    public static final String UPORDOWN = "UPORDOWN";

    // UESR表
    public static final int USER_ID_INDEX = 0;
    public static final int USER_NAME_INDEX = 1;
    public static final int USER_IMAGE_INDEX = 2;
    public static final int USER_TOKEN_INDEX = 3;
    public static final int USER_EMAIL_INDEX = 4;
    public static final int USER_TYPE_INDEX = 5;
    public static final int USER_LOGIN_NAME_INDEX = 6;
    public static final int USER_BGURL_INDEX = 7;
    public static final int USER_SIGNATURE_INDEX = 8;
    public static final int USER_SEX_INDEX = 9;

    public static final int USER_LEVEL_INDEX = 10;
    public static final int USER_LEVELTITLE_INDEX = 11;
    public static final int USER_LEVEL_TIME_INDEX = 12;

    // add by yinguanping 新加密协议
    public static final int USER_OPENID_INDEX = 13;
    public static final int USER_OPID_INDEX = 14;
    public static final int USER_AUTH_TOKEN_INDEX = 15;
    public static final int USER_PRIVATE_KEY_INDEX = 16;
    public static final int USER_APPID_INDEX = 17;

    public static final String USER_ID = "USER_ID";// 1
    public static final String USER_NAME = "USER_NAME";// 2
    public static final String USER_IMAGE = "USER_IMAGE";// 3
    public static final String USER_TOKEN = "USER_TOKEN";// 4
    public static final String USER_EMAIL = "USER_EMAIL";// 5
    public static final String USER_TYPE = "USER_TYPE";// 6
    public static final String USER_LOGIN_NAME = "LOGIN_NAME";// 7
    public static final String USER_BGURL = "USER_BGURL";// 8
    public static final String USER_SIGNATURE = "USER_SIGNATURE";
    public static final String USER_SEX = "USER_SEX";

    public static final String USER_LEVEL = "USER_LEVEL";
    public static final String USER_LEVELTITLE = "USER_LEVELTITLE";
    public static final String USER_LEVEL_TIME = "USER_LEVEL_TIME";

    // add by yinguanping 新加密协议
    public static final String USER_OPENID = "OPENID";// "用户唯一标识" 
    public static final String USER_OPID = "OPID";// "当前登录标识id", 
    public static final String USER_AUTH_TOKEN = "AUTH_TOKEN";// "当前登录凭证", 
    public static final String USER_PRIVATE_KEY = "PRIVATE_KEY";// "私钥为加密时使用"
    public static final String USER_APPID = "APPID";

    public static final String[] USER_COLUMNS = {USER_ID, USER_NAME, USER_IMAGE, USER_TOKEN, USER_EMAIL, USER_TYPE, USER_LOGIN_NAME, USER_BGURL, USER_SIGNATURE, USER_SEX, USER_LEVEL, USER_LEVELTITLE,
            USER_LEVEL_TIME, USER_OPENID, USER_OPID, USER_AUTH_TOKEN, USER_PRIVATE_KEY, USER_APPID};

    private static final String SQL_CREATE_SELF_CREATE = "CREATE TABLE " + TABLE_SELF_CREATE + " (" + //
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," + // 主键自动增长
            SELF_CREATE_ID + " TEXT," + // 数据库id
            SELF_CREATE_KEYWORD + " TEXT," + // "关键词"
            SELF_CREATE_SRPID + " TEXT," + // "关键词对应srpid",
            SELF_CREATE_MD5 + " TEXT," + // 微件对应md5"
            SELF_CREATE_COLUMN_TYPE + " TEXT," + // 微件类型
            SELF_CREATE_NAME + " TEXT," + // "栏目名",
            SELF_CREATE_TITLE + " TEXT," + // 原创标题",
            SELF_CREATE_CONTENT + " TEXT," + // 原创内容",
            SELF_CREATE_CONPIC + " TEXT," + // 内容图片(若有多个图片空格隔开共同存储)
            SELF_CREATE_PUBTIME + " TEXT," + // 发布时间
            SELF_CREATE_STATUS + " TEXT(10)" + // 审核状态
            ")";// 发送状态

    private static final String SQL_CREATE_SEARCH_HISTORY = "CREATE TABLE " + TABLE_SEARCH_HISTORY + " (" + //
            HISTORY_CATEGORY + " TEXT(50) NOT NULL," + //
            HISTORY_RSSIMAGE + " TEXT(500)," + //
            HISTORY_URL + " TEXT(500)," + //
            HISTORY_KEYWORD + " TEXT(50) NOT NULL ," + //
            HISTORY_SRPID + " TEXT(50)," + //
            HISTORY_SRPCATE + " TEXT(50)," + //
            HISTORY_M + " TEXT(50)," + //
            HISTORY_G + " TEXT(50)," + //
            HISTORY_LASTUPDATE + " TEXT(13) NOT NULL," + //
            HISTORY_VERSION + " TEXT(20)," + //
            "TMP" + " TEXT(50)" + //
            ")";

    private static final String SQL_CREATE_WENDA_ANSWER = "CREATE TABLE " + TABLE_WENDA_ANSWER + " (" + //
            QUESTION_ID + " TEXT(50)   ," + //
            ANSWER_ID + " TEXT(50)     ," + //
            ANSWER_STATE + " TEXT(1)    ," + //
            UPORDOWN + " TEXT(1)    ," + //
            "TMPLINE" + " TEXT(50) " + //
            ")";

    /* 用户表 */
    public static final String SQL_CREATE_USER = "CREATE TABLE " + TABLE_USER + " (" + //
            USER_ID + " TEXT(50)," + //
            USER_NAME + " TEXT(100)," + //
            USER_IMAGE + " TEXT(200)," + //
            USER_TOKEN + " TEXT(100)," + //
            USER_EMAIL + " TEXT(100)," + //
            USER_TYPE + " TEXT(50)," + //
            USER_LOGIN_NAME + " TEXT(100)," + //
            USER_BGURL + " TEXT," + //
            USER_SIGNATURE + " TEXT," + //
            USER_SEX + " TEXT," + //
            USER_LEVEL + " TEXT," + //
            USER_LEVELTITLE + " TEXT," + //
            USER_LEVEL_TIME + " TEXT," + USER_OPENID + " TEXT," + USER_OPID + " TEXT," + USER_AUTH_TOKEN + " TEXT," + USER_PRIVATE_KEY + " TEXT," + USER_APPID + " TEXT" + ")";
    /* 阅读历史表 */
    private static final String SQL_CREATE_READ_HISTORY = "CREATE TABLE " + TABLE_READ_HISTORY + " (" + //
            "MD5" + " TEXT(32)," + "PRIMARY KEY (MD5)" + ")";

    public SqliteUtil(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SEARCH_HISTORY);
        db.execSQL(SQL_CREATE_WENDA_ANSWER);
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_READ_HISTORY);
        db.execSQL(SQL_CREATE_SELF_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            db.execSQL("DROP TABLE IF EXISTS " + "USER");
        }
        db.execSQL("DROP TABLE IF EXISTS " + "SEARCH_HISTORY");
        db.execSQL("DROP TABLE IF EXISTS " + "WENDA_ANSWER");
        db.execSQL("DROP TABLE IF EXISTS " + "READ_HISTORY");
        db.execSQL("DROP TABLE IF EXISTS " + "SELF_CREATE");
        this.onUpgradeSql(db, oldVersion);
    }
    private void onUpgradeSql(SQLiteDatabase db, int oldVersion) {
        try {
            if (oldVersion < 8) {
                db.execSQL(SQL_CREATE_USER);
            } else {//》＝8
                if (oldVersion < 9) {
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_LOGIN_NAME + " TEXT(100)");
                }
                if (oldVersion < 10) {
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_BGURL + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_SIGNATURE + " TEXT");
                }
                if (oldVersion < 11) {
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_SEX + " TEXT");

                }
                if (oldVersion < 12) {
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_LEVEL + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_LEVELTITLE + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_LEVEL_TIME + " TEXT");
                }
                if (oldVersion < 13) {
                    //add by yinguanping 新加密协议 
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_OPENID + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_OPID + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_AUTH_TOKEN + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_PRIVATE_KEY + " TEXT");
                    db.execSQL("ALTER TABLE USER ADD COLUMN " + USER_APPID + " TEXT");
                }
            }
            db.execSQL(SQL_CREATE_SEARCH_HISTORY);
            db.execSQL(SQL_CREATE_WENDA_ANSWER);
            db.execSQL(SQL_CREATE_READ_HISTORY);
            db.execSQL(SQL_CREATE_SELF_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 用户表 */
    // private static final String SQL_CREATE_USER = "CREATE TABLE " + TABLE_USER + " (" + //
    // USER_ID + " TEXT(50)," + //
    // USER_NAME + " TEXT(100)," + //
    // USER_IMAGE + " TEXT(200)," + //
    // USER_TOKEN + " TEXT(100)," + //
    // USER_EMAIL + " TEXT(100)," + //
    // USER_TYPE + " TEXT(50)," + //
    // USER_LOGIN_NAME + " TEXT(100)," + //
    // USER_BGURL + " TEXT," + //
    // USER_SIGNATURE + " TEXT," + //
    // USER_SEX + " TEXT" + //
    // ")";

    public void close() {
        this.db.close();
        this.db = null;
    }

    public void openReadable() {
        this.db = getReadableDatabase();
    }
}
