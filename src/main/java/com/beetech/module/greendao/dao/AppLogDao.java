package com.beetech.module.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.beetech.module.bean.AppLog;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APP_LOG".
*/
public class AppLogDao extends AbstractDao<AppLog, Long> {

    public static final String TABLENAME = "APP_LOG";

    /**
     * Properties of entity AppLog.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property InputTime = new Property(1, java.util.Date.class, "inputTime", false, "INPUT_TIME");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property SendFlag = new Property(3, int.class, "sendFlag", false, "SEND_FLAG");
    }


    public AppLogDao(DaoConfig config) {
        super(config);
    }
    
    public AppLogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP_LOG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"INPUT_TIME\" INTEGER," + // 1: inputTime
                "\"CONTENT\" TEXT," + // 2: content
                "\"SEND_FLAG\" INTEGER NOT NULL );"); // 3: sendFlag
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_APP_LOG_SEND_FLAG ON \"APP_LOG\"" +
                " (\"SEND_FLAG\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP_LOG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AppLog entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        java.util.Date inputTime = entity.getInputTime();
        if (inputTime != null) {
            stmt.bindLong(2, inputTime.getTime());
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
        stmt.bindLong(4, entity.getSendFlag());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AppLog entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        java.util.Date inputTime = entity.getInputTime();
        if (inputTime != null) {
            stmt.bindLong(2, inputTime.getTime());
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
        stmt.bindLong(4, entity.getSendFlag());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AppLog readEntity(Cursor cursor, int offset) {
        AppLog entity = new AppLog( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // inputTime
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.getInt(offset + 3) // sendFlag
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AppLog entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setInputTime(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSendFlag(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AppLog entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AppLog entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AppLog entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
