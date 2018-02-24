package com.easysql.table;

import android.database.Cursor;

import com.easysql.annotation.Column;
import com.easysql.converter.ColumnConverter;
import com.easysql.converter.ColumnConverterFactory;
import com.easysql.utils.ColumnUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 表的列的映射描述类
 */
public class ColumnMapping {
    private String columnName;               //列的名字
    private ColumnType columnType;               //列的类型
    private String property;                //property属性，例如UNIQUE
    private boolean isPrimary;               //是否是主键
    private boolean isNotNull;               //是否不能为空
    private boolean isAutoincrement;         //AUTOINCREMENT 是否自增

    private Method getMethod;
    private Method setMethod;
    private Field columnField;
    private ColumnConverter columnConverter;


    /**
     * 这个是利用注解方式来操作数据库的构造方式
     *
     * @param entityType 表的class
     * @param field      对应列的变量
     * @param column     列的注解描述
     */
    public ColumnMapping(Class<?> entityType, Field field, Column column) {
        field.setAccessible(true);

        this.columnField = field;
        this.columnName = column.name();
        this.property = column.property();
        this.isPrimary = column.isId();

        Class<?> fieldType = field.getType();
        this.isAutoincrement = this.isPrimary && column.autoGen() && ColumnUtils.isAutoIdType(fieldType);
        this.columnConverter = ColumnConverterFactory.getColumnConverter(fieldType);
        this.columnType = columnConverter.getColumnDbType();

        this.getMethod = ColumnUtils.findGetMethod(entityType, field);
        if (this.getMethod != null && !this.getMethod.isAccessible()) {
            this.getMethod.setAccessible(true);
        }
        this.setMethod = ColumnUtils.findSetMethod(entityType, field);
        if (this.setMethod != null && !this.setMethod.isAccessible()) {
            this.setMethod.setAccessible(true);
        }
    }


    /**
     * @param columnName 列名
     * @param columnType 列的数据类型
     */
    public ColumnMapping(String columnName, ColumnType columnType) {
        this(columnName, columnType, false, false, false, null);
    }

    /**
     * @param columnName 列名
     * @param columnType 列的数据类型
     * @param isPrimary  是否为主键
     * @param isNotNull  是否不能为空
     */
    public ColumnMapping(String columnName, ColumnType columnType, boolean isPrimary, boolean isNotNull) {
        this(columnName, columnType, isPrimary, isNotNull, false, null);
    }

    /**
     * @param columnName      列名
     * @param columnType      列的数据类型
     * @param isPrimary       是否为主键
     * @param isNotNull       是否不能为空
     * @param isAutoincrement 是否自增
     */
    public ColumnMapping(String columnName, ColumnType columnType, boolean isPrimary, boolean isNotNull, boolean isAutoincrement, String property) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.isPrimary = isPrimary;
        this.isNotNull = isNotNull;
        this.isAutoincrement = isAutoincrement;
        this.property = property;
    }


    public void setValueFromCursor(Object entity, Cursor cursor, int index) {
        Object value = columnConverter.getFieldValue(cursor, index);
        if (value == null) return;

        if (setMethod != null) {
            try {
                setMethod.invoke(entity, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.columnField.set(entity, value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public Object getFieldValue(Object entity) {
        Object fieldValue = null;
        if (entity != null) {
            if (getMethod != null) {
                try {
                    fieldValue = getMethod.invoke(entity);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    fieldValue = this.columnField.get(entity);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return fieldValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    public boolean isAutoincrement() {
        return isAutoincrement;
    }

    public void setAutoincrement(boolean autoincrement) {
        isAutoincrement = autoincrement;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
