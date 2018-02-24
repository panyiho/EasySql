
package com.easysql.utils;

import com.easysql.annotation.Column;
import com.easysql.converter.ColumnConverterFactory;
import com.easysql.table.ColumnMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 注解模式下，用来解释类里面的注解，从而得到表结构的工具类
 */
public final class TableUtils {

    private TableUtils() {
    }

    public static synchronized ArrayList<ColumnMapping> findColumnList(Class<?> entityType) {
        ArrayList<ColumnMapping> columnList = new ArrayList<>();
        addColumns2List(entityType, columnList);
        return columnList;
    }

    private static void addColumns2List(Class<?> entityType, ArrayList<ColumnMapping> columnList) {
        if (Object.class.equals(entityType)) return;

        try {
            Field[] fields = entityType.getDeclaredFields();
            for (Field field : fields) {
                int modify = field.getModifiers();
                if (Modifier.isStatic(modify) || Modifier.isTransient(modify)) {
                    continue;
                }
                Column columnMappingAnn = field.getAnnotation(Column.class);
                if (columnMappingAnn != null) {
                    if (ColumnConverterFactory.isSupportColumnConverter(field.getType())) {
                        ColumnMapping column = new ColumnMapping(entityType, field, columnMappingAnn);
                        columnList.add(column);
                    }
                }
            }

            addColumns2List(entityType.getSuperclass(), columnList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
