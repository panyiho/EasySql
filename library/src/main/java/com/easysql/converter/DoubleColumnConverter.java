package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;

public class DoubleColumnConverter implements ColumnConverter<Double> {
    @Override
    public Double getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    @Override
    public Object fieldValue2DbValue(Double fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.REAL;
    }
}
