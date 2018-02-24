package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class StringColumnConverter implements ColumnConverter<String> {
    @Override
    public String getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    @Override
    public Object fieldValue2DbValue(String fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.TEXT;
    }
}
