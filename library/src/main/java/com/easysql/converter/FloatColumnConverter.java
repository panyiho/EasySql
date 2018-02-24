package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class FloatColumnConverter implements ColumnConverter<Float> {
    @Override
    public Float getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getFloat(index);
    }

    @Override
    public Object fieldValue2DbValue(Float fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.REAL;
    }
}
