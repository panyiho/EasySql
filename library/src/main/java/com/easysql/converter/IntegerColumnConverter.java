package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;

public class IntegerColumnConverter implements ColumnConverter<Integer> {
    @Override
    public Integer getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index);
    }

    @Override
    public Object fieldValue2DbValue(Integer fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.INTEGER;
    }
}
