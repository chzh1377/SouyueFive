package com.tuita.sdk.im.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.tuita.sdk.im.db.module.MessageRecent;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * DAO for table MESSAGE_RECENT.
 */
public class MessageRecentDao extends AbstractDao<MessageRecent, Long> {

    public static final String TABLENAME = "MESSAGE_RECENT";

    /**
     * Properties of entity MessageRecent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Myid = new Property(1, long.class, "myid", false, "MYID");
        public final static Property Chat_id = new Property(2, Long.class, "chat_id", false, "CHAT_ID");
        public final static Property Chat_type = new Property(3, Integer.class, "chat_type", false, "CHAT_TYPE");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
        public final static Property Content_type = new Property(5, Integer.class, "content_type", false, "CONTENT_TYPE");
        public final static Property Date = new Property(6, Long.class, "date", false, "DATE");
        public final static Property Uuid = new Property(7, String.class, "uuid", false, "UUID");
        public final static Property Status = new Property(8, Integer.class, "status", false, "STATUS");
        public final static Property Bubble_num = new Property(9, Integer.class, "bubble_num", false, "BUBBLE_NUM");
        public final static Property Sender = new Property(10, Long.class, "sender", false, "SENDER");
        public final static Property By1 = new Property(11, String.class, "by1", false, "BY1");
        public final static Property By2 = new Property(12, String.class, "by2", false, "BY2");
        public final static Property Drafttext = new Property(13, String.class, "drafttext", false, "DRAFTTEXT");
        public final static Property Draftforat = new Property(14, String.class, "draftforat", false, "DRAFTFORAT");
        public final static Property By3 = new Property(15, String.class, "by3", false, "BY3");
        public final static Property By4 = new Property(16, String.class, "by4", false, "BY4");
        public final static Property By5 = new Property(17, String.class, "by5", false, "BY5");
    };


    public MessageRecentDao(DaoConfig config) {
        super(config);
    }

    public MessageRecentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MESSAGE_RECENT' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MYID' INTEGER NOT NULL ," + // 1: myid
                "'CHAT_ID' INTEGER," + // 2: chat_id
                "'CHAT_TYPE' INTEGER," + // 3: chat_type
                "'CONTENT' TEXT," + // 4: content
                "'CONTENT_TYPE' INTEGER," + // 5: content_type
                "'DATE' INTEGER," + // 6: date
                "'UUID' TEXT," + // 7: uuid
                "'STATUS' INTEGER," + // 8: status
                "'BUBBLE_NUM' INTEGER," + // 9: bubble_num
                "'SENDER' INTEGER," + // 10: sender
                "'BY1' TEXT," + // 11: by1
                "'BY2' TEXT," + // 12: by2
                "'DRAFTTEXT' TEXT," + // 13: drafttext
                "'DRAFTFORAT' TEXT," + // 14: draftforat
                "'BY3' TEXT," + // 15: by3
                "'BY4' TEXT," + // 16: by4
                "'BY5' TEXT);"); // 17: by5
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MESSAGE_RECENT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MessageRecent entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMyid());

        Long chat_id = entity.getChat_id();
        if (chat_id != null) {
            stmt.bindLong(3, chat_id);
        }

        Integer chat_type = entity.getChat_type();
        if (chat_type != null) {
            stmt.bindLong(4, chat_type);
        }

        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }

        Integer content_type = entity.getContent_type();
        if (content_type != null) {
            stmt.bindLong(6, content_type);
        }

        Long date = entity.getDate();
        if (date != null) {
            stmt.bindLong(7, date);
        }

        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(8, uuid);
        }

        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(9, status);
        }

        Integer bubble_num = entity.getBubble_num();
        if (bubble_num != null) {
            stmt.bindLong(10, bubble_num);
        }

        Long sender = entity.getSender();
        if (sender != null) {
            stmt.bindLong(11, sender);
        }

        String by1 = entity.getBy1();
        if (by1 != null) {
            stmt.bindString(12, by1);
        }

        String by2 = entity.getBy2();
        if (by2 != null) {
            stmt.bindString(13, by2);
        }

        String drafttext = entity.getDrafttext();
        if (drafttext != null) {
            stmt.bindString(14, drafttext);
        }

        String draftforat = entity.getDraftforat();
        if (draftforat != null) {
            stmt.bindString(15, draftforat);
        }

        String by3 = entity.getBy3();
        if (by3 != null) {
            stmt.bindString(16, by3);
        }

        String by4 = entity.getBy4();
        if (by4 != null) {
            stmt.bindString(17, by4);
        }

        String by5 = entity.getBy5();
        if (by5 != null) {
            stmt.bindString(18, by5);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public MessageRecent readEntity(Cursor cursor, int offset) {
        MessageRecent entity = new MessageRecent( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getLong(offset + 1), // myid
                cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // chat_id
                cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // chat_type
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // content
                cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // content_type
                cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // date
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // uuid
                cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // status
                cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // bubble_num
                cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // sender
                cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // by1
                cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // by2
                cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // drafttext
                cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // draftforat
                cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // by3
                cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // by4
                cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17) // by5
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MessageRecent entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMyid(cursor.getLong(offset + 1));
        entity.setChat_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setChat_type(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setContent_type(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setDate(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setUuid(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setStatus(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setBubble_num(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setSender(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setBy1(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBy2(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setDrafttext(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setDraftforat(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setBy3(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setBy4(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setBy5(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MessageRecent entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(MessageRecent entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
