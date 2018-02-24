package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class ByteArrayColumnConverter implements ColumnConverter<byte[]> {
    @Override
    public byte[] getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getBlob(index);
    }

    @Override
    public Object fieldValue2DbValue(byte[] fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.BLOB;
    }
}
