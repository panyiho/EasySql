package com.easysql.operation;

import android.database.Cursor;

import com.easysql.SqliteDBConfig;
import com.easysql.table.AnnotationTableMappping;
import com.easysql.table.TableMapping;
import com.easysql.utils.BaseDbHelper;


/**
 * 数据库操作的动作基类，包含了一些公用的方法和属性
 * Created by Pan_ on 2018/2/7.
 */

public abstract class BaseOperate<T> {
    public  static final String TAG = "EasySql";
    Class<T> entityClass;
    public SqliteDBConfig sqliteDBConfig;
    private BaseDbHelper baseDbHelper;
    public String tableName;
    public TableMapping tableMapping;
    private volatile boolean checkedDatabase;

    public BaseOperate(Class<T> entityClass,SqliteDBConfig sqliteDBConfig, BaseDbHelper baseDbHelper) {
        this.sqliteDBConfig = sqliteDBConfig;
        this.baseDbHelper = baseDbHelper;
        this.entityClass = entityClass;

        this.tableMapping = sqliteDBConfig.tableMappingHashMap.get(entityClass);
        if(tableMapping == null){
            try{
                tableMapping = new AnnotationTableMappping<>(entityClass);
                sqliteDBConfig.tableMappingHashMap.put(entityClass,tableMapping);
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
        if (tableMapping == null) {
            throw new RuntimeException("tableMapping is not found!Please config tableMapping with" + entityClass);
        }
        this.tableName = tableMapping.tableName;

    }
    protected SqliteDBConfig getSqliteDBConfig() {
        return sqliteDBConfig;
    }

    protected BaseDbHelper getBaseDbHelper() {
        return baseDbHelper;
    }

    protected String getTableName() {
        return tableName;
    }

    protected TableMapping getTableMapping() {
        return tableMapping;
    }

    protected boolean isCheckedDatabase() {
        return checkedDatabase;
    }

    /*package*/ void setCheckedDatabase(boolean checkedDatabase) {
        this.checkedDatabase = checkedDatabase;
    }


    protected boolean tableIsExist() {
        if (this.isCheckedDatabase()) {
            return true;
        }
        Cursor cursor = baseDbHelper.getWritableDatabase().rawQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        this.setCheckedDatabase(true);
                        return true;
                    }
                }
            } catch (Throwable e) {
                return false;
            } finally {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

}
