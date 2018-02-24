package com.easysql.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.easysql.SqliteDBConfig;
import com.easysql.converter.ColumnConverter;
import com.easysql.converter.ColumnConverterFactory;
import com.easysql.table.AnnotationTableMappping;
import com.easysql.table.TableMapping;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * 底层数据库操作的helper类，继承了SQLiteOpenHelper
 * Created by Pan_ on 2018/2/6.
 */

public class BaseDbHelper extends SQLiteOpenHelper {
    private SqliteDBConfig sqliteDBConfig;
    private static final String TAG = "EasySql";
    private SQLiteDatabase sqLiteDatabase;


    public BaseDbHelper(Context context, SqliteDBConfig config) {
        super(context, config.dataBaseName, null, config.version);
        this.sqliteDBConfig = config;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Iterator<TableMapping> mappingIterator = sqliteDBConfig.tableMappingHashMap.values().iterator();
        while (mappingIterator.hasNext()) {
            TableMapping mapping = mappingIterator.next();
            if (mapping != null) {
                db.execSQL(mapping.buildTableString());
                Log.d(TAG, "create table " + "[ \n" + mapping.buildTableString() + "\n ]" + " successful! ");
                if (mapping instanceof AnnotationTableMappping) {
                    String onCreateSql = ((AnnotationTableMappping) mapping).getOnCreateSql();
                    if (!TextUtils.isEmpty(onCreateSql)) {
                        db.execSQL(onCreateSql);
                    }
                }
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Iterator<TableMapping> mappingIterator = sqliteDBConfig.tableMappingHashMap.values().iterator();
        while (mappingIterator.hasNext()) {
            TableMapping mapping = mappingIterator.next();
            if (mapping != null) {
                mapping.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
            }
        }
        if (sqliteDBConfig.onDbVersionChangeListener != null) {
            sqliteDBConfig.onDbVersionChangeListener.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Iterator<TableMapping> mappingIterator = sqliteDBConfig.tableMappingHashMap.values().iterator();
        while (mappingIterator.hasNext()) {
            TableMapping mapping = mappingIterator.next();
            if (mapping != null) {
                mapping.onDowngrade(db, oldVersion, newVersion);
            }
        }
        if (sqliteDBConfig.onDbVersionChangeListener != null) {
            sqliteDBConfig.onDbVersionChangeListener.onDowngrade(db, oldVersion, newVersion);
        }
    }

    public SQLiteDatabase getSqLiteDatabase() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = getWritableDatabase();
        }
        return sqLiteDatabase;
    }

    public long insert(String tableName, ContentValues values) {
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = getSqLiteDatabase();
        long result = -1;
        beginTransaction();
        try {
            result = db.insert(tableName, null, values);
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute insert  error:" + e.getMessage());
            }
        } finally {
            endTransaction();
        }
        long endTime = System.currentTimeMillis();
        if (sqliteDBConfig.logger) {
            Log.d(TAG, "execute insert  tableName:" + tableName + " value is :" + values + " \n 耗时:" + String.valueOf(endTime - startTime) + " 操作返回值：" + result);
        }
        return result;
    }


    public long insert(String tableName, List<ContentValues> values) {
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = getSqLiteDatabase();
        long result = 0;
        beginTransaction();
        try {
            for (ContentValues entity : values) {
                result = db.insert(tableName, null, entity);
            }
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute insert  error:" + e.getMessage());
            }
        } finally {
            endTransaction();
        }
        long endTime = System.currentTimeMillis();
        if (sqliteDBConfig.logger) {
            Log.d(TAG, "execute insert  tableName:" + tableName + "  耗时:" + String.valueOf(endTime - startTime) + " 操作返回值：" + result);
        }
        return result;
    }


    public long update(String tableName, ContentValues entity, String whereClause, String[] whereArgs) {
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = getSqLiteDatabase();
        long result = -1;
        beginTransaction();
        try {
            result = db.update(tableName, entity, whereClause, whereArgs);
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute update  error:" + e.getMessage());
            }
        } finally {
            endTransaction();
        }
        long endTime = System.currentTimeMillis();
        if (sqliteDBConfig.logger) {
            Log.d(TAG, "execute update  tableName:" + tableName + " value is :" + entity + "  耗时:" + String.valueOf(endTime - startTime) + " 操作返回值：" + result);
        }
        return result;
    }

    public long update(TableMapping tableMapping, List<ContentValues> modifyKeyVlaue) {
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = getSqLiteDatabase();
        beginTransaction();
        long result = -1;
        try {
            for (ContentValues contentValues : modifyKeyVlaue) {
                String idColmnName = tableMapping.getIDColumn().getColumnName();
                Object id = contentValues.get(idColmnName);
                result += db.update(tableMapping.getTableName(), contentValues, idColmnName + "=?", new String[]{String.valueOf(id)});
            }
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute update  error:" + e.getMessage());
            }
        } finally {
            endTransaction();
        }
        long endTime = System.currentTimeMillis();
        if (sqliteDBConfig.logger) {
            Log.d(TAG, "execute update  tableName:" + tableMapping.getTableName() + " value is :" + modifyKeyVlaue + "  耗时:" + String.valueOf(endTime - startTime) + " 操作返回值：" + result);
        }
        return result;
    }

    public int executeUpdateDeleteSql(String sql, ContentValues contentValues) {
        long startTime = System.currentTimeMillis();
        int result = -1;
        if (TextUtils.isEmpty(sql) || contentValues == null || contentValues.size() <= 0) {
            return result;
        }
        if (TextUtils.isEmpty(sql)) {
            return result;
        }
        beginTransaction();
        try {
            result = executeStatement(sql, contentValues);
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute executeUpdateDeleteSql  error:" + e.getMessage());
            }
        } finally {
            endTransaction();
        }
        long endTime = System.currentTimeMillis();
        if (sqliteDBConfig.logger) {
            Log.d(TAG, "executeSql  sql:" + sql + " value is :" + contentValues + "  耗时:" + String.valueOf(endTime - startTime) + " 操作返回值：" + result);
        }
        return result;
    }

    public int executeUpdateDeleteSql(String sql) {
        long startTime = System.currentTimeMillis();
        int result = -1;
        if (TextUtils.isEmpty(sql)) {
            return result;
        }
        beginTransaction();
        try {
            result = executeStatement(sql);
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute executeUpdateDeleteSql  error:" + e.getMessage());
            }
        } finally {
            endTransaction();
        }
        long endTime = System.currentTimeMillis();
        if (sqliteDBConfig.logger) {
            Log.d(TAG, "executeSql  sql:" + sql + "  耗时:" + String.valueOf(endTime - startTime) + " 操作返回值：" + result);
        }
        return result;
    }

    public Cursor rawQuery(String sql) {
        SQLiteDatabase db = getSqLiteDatabase();
        try {
            return db.rawQuery(sql, null);
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute rawQuery  error:" + e.getMessage());
            }
            return null;
        } finally {
        }
    }

    private int executeStatement(String sql, ContentValues contentValues) {
        int result = -1;
        SQLiteStatement statement = null;
        beginTransaction();
        try {
            statement = buildStatement(sql, contentValues);
            result = statement.executeUpdateDelete();
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute executeStatement  error:" + e.getMessage());
            }
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            endTransaction();
        }
        return result;
    }


    private int executeStatement(String sql) {
        int result = -1;
        SQLiteStatement statement = null;
        beginTransaction();
        try {
            statement = getSqLiteDatabase().compileStatement(sql);
            result = statement.executeUpdateDelete();
            setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            if (sqliteDBConfig.logger) {
                Log.e(TAG, "execute executeStatement  error:" + e.getMessage());
            }
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            endTransaction();
        }
        return result;
    }

    private SQLiteStatement buildStatement(String sql, ContentValues contentValues) {
        SQLiteStatement result = getSqLiteDatabase().compileStatement(sql);
        if (contentValues != null) {
            Set<String> keySets = contentValues.keySet();
            int index = 1;
            for (String key : keySets) {
                Object value = contentValues.get(key);
                ColumnConverter converter = ColumnConverterFactory.getColumnConverter(value.getClass());
                switch (converter.getColumnDbType()) {
                    case INTEGER:
                        result.bindLong(index, ((Number) value).longValue());
                        break;
                    case REAL:
                        result.bindDouble(index, ((Number) value).doubleValue());
                        break;
                    case TEXT:
                        result.bindString(index, value.toString());
                        break;
                    case BLOB:
                        result.bindBlob(index, (byte[]) value);
                        break;
                    case NULL:
                        result.bindNull(index);
                        break;
                    default:
                        result.bindString(index, value.toString());
                        break;
                } // end switch
                index++;
            }

        }
        return result;
    }


    private void beginTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && getSqLiteDatabase().isWriteAheadLoggingEnabled()) {
            getSqLiteDatabase().beginTransactionNonExclusive();
        } else {
            getSqLiteDatabase().beginTransaction();
        }
    }

    private void setTransactionSuccessful() {
        getSqLiteDatabase().setTransactionSuccessful();
    }

    private void endTransaction() {
        getSqLiteDatabase().endTransaction();
    }

}