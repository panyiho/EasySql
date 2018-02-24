package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class BooleanColumnConverter implements ColumnConverter<Boolean> {
    @Override
    public Boolean getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index) == 1;
    }

    @Override
    public Object fieldValue2DbValue(Boolean fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue ? 1 : 0;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.INTEGER;
    }
}
