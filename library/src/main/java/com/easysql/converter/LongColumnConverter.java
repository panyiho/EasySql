package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;

public class LongColumnConverter implements ColumnConverter<Long> {
    @Override
    public Long getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getLong(index);
    }

    @Override
    public Object fieldValue2DbValue(Long fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.INTEGER;
    }
}
