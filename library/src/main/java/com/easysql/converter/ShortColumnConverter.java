package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class ShortColumnConverter implements ColumnConverter<Short> {
    @Override
    public Short getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getShort(index);
    }

    @Override
    public Object fieldValue2DbValue(Short fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.INTEGER;
    }
}
