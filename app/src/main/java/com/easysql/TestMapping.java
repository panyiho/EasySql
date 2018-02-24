package com.easysql;

import android.content.ContentValues;
import android.database.Cursor;

import com.easysql.table.ColumnMapping;
import com.easysql.table.ColumnType;
import com.easysql.table.TableMapping;
import com.easysql.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pan_ on 2018/2/6.
 */

public class TestMapping extends TableMapping<Persion> {
    private static final String ID = "id";
    private static final String isMan = "isMan";
    private static final String age = "age";
    private static final String longValue = "longValue";
    private static final String shortValue = "shortValue";
    private static final String doubleValue = "doubleValue";
    private static final String stringValue = "stringValue";
    private static final String byteValue = "byteValue";
    private static final String floatValue = "floatValue";
    private static final String parter = "parter";


    @Override
    public String getTableName() {
        return "persion";
    }

    @Override
    public List<ColumnMapping> generateCloumn() {
        List<ColumnMapping> columnMappings = new ArrayList<>();
        columnMappings.add(new ColumnMapping(ID, ColumnType.TEXT, true, false));
        columnMappings.add(new ColumnMapping(isMan, ColumnType.INTEGER, false, false));
        columnMappings.add(new ColumnMapping(age, ColumnType.INTEGER, false, false));
        columnMappings.add(new ColumnMapping(longValue, ColumnType.INTEGER, false, false));
        columnMappings.add(new ColumnMapping(shortValue, ColumnType.INTEGER, false, false));
        columnMappings.add(new ColumnMapping(doubleValue, ColumnType.REAL, false, false));
        columnMappings.add(new ColumnMapping(stringValue, ColumnType.TEXT, false, false));
        columnMappings.add(new ColumnMapping(byteValue, ColumnType.INTEGER, false, false));
        columnMappings.add(new ColumnMapping(floatValue, ColumnType.REAL, false, false));
        columnMappings.add(new ColumnMapping(parter, ColumnType.BLOB, false, false));
        return columnMappings;
    }

    @Override
    public ContentValues beanToContentValues(Persion bean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, bean.id);
        contentValues.put(isMan, bean.isMan);
        contentValues.put(age, bean.age);
        contentValues.put(longValue, bean.longValue);
        contentValues.put(shortValue, bean.shortValue);
        contentValues.put(doubleValue, bean.doubleValue);
        contentValues.put(stringValue, bean.stringValue);
        contentValues.put(byteValue, bean.byteValue);
        contentValues.put(floatValue, bean.floatValue);
        contentValues.put(parter, IOUtil.toByteArray(bean.baby));
        return contentValues;
    }

    @Override
    public Persion cursorToBean(Cursor cursor) {
        Persion persion = new Persion();
        persion.id = cursor.getInt(cursor.getColumnIndex(ID));
        persion.isMan = cursor.getInt(cursor.getColumnIndex(isMan)) == 1 ? true : false;
        persion.age = cursor.getInt(cursor.getColumnIndex(age));
        persion.longValue = cursor.getLong(cursor.getColumnIndex(longValue));
        persion.shortValue = cursor.getShort(cursor.getColumnIndex(shortValue));
        persion.doubleValue = cursor.getDouble(cursor.getColumnIndex(doubleValue));
        persion.byteValue = (byte) cursor.getInt(cursor.getColumnIndex(byteValue));
        persion.floatValue = cursor.getFloat(cursor.getColumnIndex(floatValue));
        persion.baby = (Baby) IOUtil.toObject(cursor.getBlob(cursor.getColumnIndex(parter)));
        persion.stringValue = cursor.getString(cursor.getColumnIndex(stringValue));

        return persion;
    }
}
