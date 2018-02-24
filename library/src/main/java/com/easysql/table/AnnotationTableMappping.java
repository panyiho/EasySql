package com.easysql.table;

import android.content.ContentValues;
import android.database.Cursor;

import com.easysql.annotation.Table;
import com.easysql.utils.ColumnUtils;
import com.easysql.utils.TableUtils;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 注解模式的默认表映射结构类，每一个用注解注释的表都对应着一个AnnotationTableMappping，无需用户手动添加
 * Created by Pan_ on 2018/2/11.
 */

public class AnnotationTableMappping<T> extends TableMapping<T> {
    Class<T> tableClazz;
    private Constructor<T> constructor;
    private String onCreateSql;

    @Override
    public void init() {
    }

    public AnnotationTableMappping(Class<T> tableClazz) throws Throwable {
        this.tableClazz = tableClazz;
        this.constructor = tableClazz.getConstructor();
        this.constructor.setAccessible(true);
        Table table = tableClazz.getAnnotation(Table.class);
        this.tableName = table.name();
        this.onCreateSql = table.onCreated();

        generateCloumn();
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public List<ColumnMapping> generateCloumn() {
        if (columnMappingList == null || columnMappingList.isEmpty()) {
            columnMappingList = TableUtils.findColumnList(tableClazz);
        }
        return columnMappingList;
    }

    @Override
    public ContentValues beanToContentValues(T bean) {
        ContentValues contentValues = new ContentValues();
        for (ColumnMapping columnMapping : columnMappingList) {
            ColumnUtils.fillValueToContentValue(contentValues, columnMapping.getColumnName(), columnMapping.getFieldValue(bean));
        }
        return contentValues;
    }

    @Override
    public T cursorToBean(Cursor cursor) {
        Object bean = null;
        try {
            bean = constructor.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
        ColumnUtils.convertCursorToBean(bean, columnMappingList, cursor);
        return (T) bean;
    }

    /**
     * 获取Table注解里面的onCreate的语句
     * @return
     */
    public String getOnCreateSql(){
        return onCreateSql;
    }
}
