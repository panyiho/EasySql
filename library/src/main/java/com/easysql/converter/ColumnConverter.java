package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;

/**
 * 表列类型转换基类
 * @param <T>
 */
public interface ColumnConverter<T> {

    T getFieldValue(final Cursor cursor, int index);

    Object fieldValue2DbValue(T fieldValue);

    ColumnType getColumnDbType();
}
