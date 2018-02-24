package com.easysql.converter;

import android.database.Cursor;

import com.easysql.table.ColumnType;


public class CharColumnConverter implements ColumnConverter<Character> {
    @Override
    public Character getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (char) cursor.getInt(index);
    }

    @Override
    public Object fieldValue2DbValue(Character fieldValue) {
        if (fieldValue == null) return null;
        return (int) fieldValue;
    }

    @Override
    public ColumnType getColumnDbType() {
        return ColumnType.INTEGER;
    }
}
