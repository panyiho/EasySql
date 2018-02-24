package com.easysql.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * 数据库表映射类，用来定义和描述一个表的结构，如需要自定义表结构和映射关系，可以继承实现这个类
 * Created by Pan_ on 2018/2/6.
 */

public abstract class TableMapping<T> {
    protected List<ColumnMapping> columnMappingList;    //所有的表字段
    public String tableName;           //表名

    /**
     * 数据库升级，可根据需要复写修改
     * @param sqLiteDatabase 数据库db
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i("EasySql","TableMapping onUpgrade"+oldVersion+"  "+newVersion);
    }

    /**
     * 数据库降级，可根据需要复写修改
     * @param db 数据库db
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("EasySql","TableMapping onDowngrade"+oldVersion+"  "+newVersion);
    }

    /**
     * 获取表名，表名字直接定义在这里
     *
     * @return
     */
    public abstract String getTableName();

    /**
     * 生成列，列的定义在这里
     *
     * @return
     */
    public abstract List<ColumnMapping> generateCloumn();

    /**
     * 这里设置目标类和contentvalue的转换和映射
     *
     * @param bean
     * @return ContentValues
     */
    public abstract ContentValues beanToContentValues(T bean);

    /**
     * 这里设置目标类和Cursor的转换和映射T
     *
     * @param cursor
     * @return T
     */
    public abstract T cursorToBean(Cursor cursor);

    public TableMapping() {
        init();
    }

    public void init() {
        tableName = getTableName();
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalStateException("table name is not be null!!");
        }
        columnMappingList = generateCloumn();
        if (columnMappingList == null || columnMappingList.isEmpty() || getIDColumn() == null) {
            throw new IllegalStateException("table column is not be null or need Primary!!");
        }
    }

    /**
     * 建表语句
     */
    public final String buildTableString() {
        ColumnMapping idColumnMapping = getIDColumn();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append("\"").append(getTableName()).append("\"");
        builder.append(" ( ");

        if (idColumnMapping.isPrimary()) {
            builder.append("\"").append(idColumnMapping.getColumnName()).append("\"").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        } else {
            builder.append("\"").append(idColumnMapping.getColumnName()).append("\"").append(idColumnMapping.getColumnType()).append(" PRIMARY KEY, ");
        }

        for (ColumnMapping columnMapping : columnMappingList) {
            if (columnMapping.isPrimary()) continue;
            builder.append("\"").append(columnMapping.getColumnName()).append("\"");
            builder.append(' ').append(columnMapping.getColumnType());
            builder.append(' ').append(columnMapping.getProperty());
            builder.append(',');
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append(" )");
        return builder.toString();
    }

    /**
     * 获取数据库表中列的名字
     *
     * @param columnIndex 列在表中的序号
     * @return 返回列的名字
     */
    public String getColumnName(int columnIndex) {
        return columnMappingList.get(columnIndex).getColumnName();
    }

    /**
     * 获取主键
     *
     * @return
     */
    public ColumnMapping getIDColumn() {
        for (ColumnMapping columnMapping : columnMappingList) {
            if (columnMapping.isPrimary()) {
                return columnMapping;
            }
        }
        return null;
    }

    /**
     * 获取数据库表中列的个数
     */
    public int getColumnCount() {
        return columnMappingList.size();
    }

    public int getColumnIndex(String columnName) {
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (columnMappingList.get(i).getColumnName().equals(columnName)) return i;
        }
        return -1;
    }

    public List<ColumnMapping> getColumns() {
        return columnMappingList;
    }
}
