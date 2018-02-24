
package com.easysql.utils;


import android.content.ContentValues;
import android.database.Cursor;

import com.easysql.converter.ColumnConverter;
import com.easysql.converter.ColumnConverterFactory;
import com.easysql.table.ColumnMapping;
import com.easysql.table.ColumnType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

/**
 * 列操作的工具类，用来进行bean和cursor的转换，bean和contentvalues的数据转移和填充等
 */
public final class ColumnUtils {

    private ColumnUtils() {
    }

    private static final HashSet<Class<?>> BOOLEAN_TYPES = new HashSet<Class<?>>(2);
    private static final HashSet<Class<?>> INTEGER_TYPES = new HashSet<Class<?>>(2);
    private static final HashSet<Class<?>> AUTO_INCREMENT_TYPES = new HashSet<Class<?>>(4);

    static {
        BOOLEAN_TYPES.add(boolean.class);
        BOOLEAN_TYPES.add(Boolean.class);

        INTEGER_TYPES.add(int.class);
        INTEGER_TYPES.add(Integer.class);

        AUTO_INCREMENT_TYPES.addAll(INTEGER_TYPES);
        AUTO_INCREMENT_TYPES.add(long.class);
        AUTO_INCREMENT_TYPES.add(Long.class);
    }

    public static boolean isAutoIdType(Class<?> fieldType) {
        return AUTO_INCREMENT_TYPES.contains(fieldType);
    }

    public static boolean isInteger(Class<?> fieldType) {
        return INTEGER_TYPES.contains(fieldType);
    }

    public static boolean isBoolean(Class<?> fieldType) {
        return BOOLEAN_TYPES.contains(fieldType);
    }

    /**
     * 从cursor里面提取数据，填充到具体的object里面
     *
     * @param object
     * @param columnMappingList
     * @param cursor
     */
    public static void convertCursorToBean(Object object, List<ColumnMapping> columnMappingList, Cursor cursor) {
        if (object == null || columnMappingList == null || columnMappingList.isEmpty()) {
            return;
        }
        for (ColumnMapping columnMapping : columnMappingList) {
            int index = cursor.getColumnIndex(columnMapping.getColumnName());
            columnMapping.setValueFromCursor(object, cursor, index);
        }
    }

    /**
     * 把列的名字和值都填充到contentvalue里面
     *
     * @param contentValues 需要填充的contentvalue
     * @param columnName    列名字
     * @param value         列的值
     */
    public static void fillValueToContentValue(ContentValues contentValues, String columnName, Object value) {
        Object dbValue = convert2DbValueIfNeeded(value);
        if (dbValue == null) {
            contentValues.put(columnName, "");
        } else {
            ColumnConverter converter = ColumnConverterFactory.getColumnConverter(dbValue.getClass());
            ColumnType type = converter.getColumnDbType();
            switch (type) {
                case INTEGER:
                    contentValues.put(columnName, ((Number) dbValue).longValue());
                    break;
                case REAL:
                    contentValues.put(columnName, ((Number) dbValue).doubleValue());
                    break;
                case TEXT:
                    contentValues.put(columnName, dbValue.toString());
                    break;
                case BLOB:
                    contentValues.put(columnName, (byte[]) dbValue);
                    break;
                default:
                    contentValues.put(columnName, dbValue.toString());
                    break;
            } // end switch
        }
    }

    @SuppressWarnings("unchecked")
    private static Object convert2DbValueIfNeeded(final Object value) {
        Object result = value;
        if (value != null) {
            Class<?> valueType = value.getClass();
            ColumnConverter converter = ColumnConverterFactory.getColumnConverter(valueType);
            result = converter.fieldValue2DbValue(value);
        }
        return result;
    }


    public static Method findGetMethod(Class<?> entityType, Field field) {
        if (Object.class.equals(entityType)) return null;

        String fieldName = field.getName();
        Method getMethod = null;
        if (isBoolean(field.getType())) {
            getMethod = findBooleanGetMethod(entityType, fieldName);
        }
        if (getMethod == null) {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                getMethod = entityType.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
            }
        }

        if (getMethod == null) {
            return findGetMethod(entityType.getSuperclass(), field);
        }
        return getMethod;
    }


    public static Method findSetMethod(Class<?> entityType, Field field) {
        if (Object.class.equals(entityType)) return null;

        String fieldName = field.getName();
        Class<?> fieldType = field.getType();
        Method setMethod = null;
        if (isBoolean(fieldType)) {
            setMethod = findBooleanSetMethod(entityType, fieldName, fieldType);
        }
        if (setMethod == null) {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                setMethod = entityType.getDeclaredMethod(methodName, fieldType);
            } catch (NoSuchMethodException e) {
            }
        }

        if (setMethod == null) {
            return findSetMethod(entityType.getSuperclass(), field);
        }
        return setMethod;
    }

    public static Method findBooleanGetMethod(Class<?> entityType, final String fieldName) {
        String methodName = null;
        if (fieldName.startsWith("is")) {
            methodName = fieldName;
        } else {
            methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        try {
            return entityType.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    public static Method findBooleanSetMethod(Class<?> entityType, final String fieldName, Class<?> fieldType) {
        String methodName = null;
        if (fieldName.startsWith("is")) {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
        } else {
            methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        try {
            return entityType.getDeclaredMethod(methodName, fieldType);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

}
