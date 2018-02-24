package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class ByteColumnConverter implements ColumnConverter<Byte> {
    @Override
    public Byte getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (byte) cursor.getInt(index);
    }

    @Override
    public Object fieldValue2DbValue(Byte fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.INTEGER;
    }
}
