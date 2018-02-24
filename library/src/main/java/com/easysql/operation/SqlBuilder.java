
package com.easysql.operation;

import android.content.ContentValues;

import com.easysql.table.ColumnMapping;
import com.easysql.table.TableMapping;

import java.util.List;
import java.util.Set;

/**
 * 用来创建 "update", "delete"  sql语句的工具类.
 */
public final class SqlBuilder {

    private SqlBuilder() {
    }

    //*********************************************** delete sql ***********************************************

    public static String buildDeleteSql(TableMapping tableMapping) {
        if (tableMapping == null) return null;

        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(tableMapping).append("\"");
        return builder.toString();
    }

    public static String buildDeleteSqlById(TableMapping tableMapping, Object idValue) {
        if (tableMapping == null) return null;
        if (idValue == null) {
            throw new RuntimeException("this  id value is null");
        }
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(tableMapping.tableName).append("\"");
        builder.append(" WHERE ").append(WhereBuilder.b(tableMapping.getIDColumn().getColumnName(), "=", idValue));

        return builder.toString();
    }

    public static String buildDeleteSqlInfo(TableMapping tableMapping, WhereBuilder whereBuilder) {
        if (tableMapping == null) return null;
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(tableMapping.getTableName()).append("\"");

        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ").append(whereBuilder.toString());
        }

        return builder.toString();
    }

    //*********************************************** update sql ***********************************************

    public static String buildUpdateSqlInfo(TableMapping tableMapping, ContentValues modifyKeyValue, Object idValue) {
        if (tableMapping == null) return null;
        if (idValue == null) {
            throw new RuntimeException("this id value is null");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" SET ");
        List<ColumnMapping> columnMappingList = tableMapping.getColumns();
        Set<String> keySets = modifyKeyValue.keySet();
        for (String key : keySets) {
            builder.append("\"").append(key).append("\"").append("=?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" WHERE ").append(tableMapping.getIDColumn().getColumnName() + "=?");

        return builder.toString();
    }

    public static String buildUpdateSqlInfo(TableMapping tableMapping, WhereBuilder whereBuilder, ContentValues contentValues) {
        if (tableMapping == null || contentValues == null) return null;
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append("\"").append(tableMapping.getTableName()).append("\"");
        builder.append(" SET ");
        for (String key : contentValues.keySet()) {
            builder.append("\"").append(key).append("\"").append("=?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ").append(whereBuilder.toString());
        }

        return builder.toString();
    }


}
