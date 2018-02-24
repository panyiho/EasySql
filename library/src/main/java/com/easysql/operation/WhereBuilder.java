package com.easysql.operation;

import android.text.TextUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 创建where语句的builder工具类，用来组合sql语句的where语句
 * Created by Pan_ on 2018/2/7.
 */

public class WhereBuilder {

    private final List<String> whereItems;

    private WhereBuilder() {
        this.whereItems = new ArrayList<String>();
    }

    /**
     * builder 一个新实例
     */
    public static WhereBuilder b() {
        return new WhereBuilder();
    }

    /**
     * builder 一个新实例
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public static WhereBuilder b(String columnName, String op, Object value) {
        WhereBuilder result = new WhereBuilder();
        result.appendCondition(null, columnName, op, value);
        return result;
    }

    /**
     *  AND 的情况
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public WhereBuilder and(String columnName, String op, Object value) {
        appendCondition(whereItems.size() == 0 ? null : "AND", columnName, op, value);
        return this;
    }

    /**
     *  OR 的情况
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public WhereBuilder or(String columnName, String op, Object value) {
        appendCondition(whereItems.size() == 0 ? null : "OR", columnName, op, value);
        return this;
    }

    public WhereBuilder expr(String expr) {
        whereItems.add(" " + expr);
        return this;
    }

    public WhereBuilder expr(String columnName, String op, Object value) {
        appendCondition(null, columnName, op, value);
        return this;
    }

    public int getWhereItemSize() {
        return whereItems.size();
    }

    @Override
    public String toString() {
        if (whereItems.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String item : whereItems) {
            sb.append(item);
        }
        return sb.toString();
    }

    private void appendCondition(String conj, String columnName, String op, Object value) {
        StringBuilder sqlSb = new StringBuilder();

        if (whereItems.size() > 0) {
            sqlSb.append(" ");
        }

        // append conj
        if (!TextUtils.isEmpty(conj)) {
            sqlSb.append(conj + " ");
        }

        // append columnName
        sqlSb.append(columnName);

        // convert op
        if ("!=".equals(op)) {
            op = "<>";
        } else if ("==".equals(op)) {
            op = "=";
        }

        // append op & value
        if (value == null) {
            if ("=".equals(op)) {
                sqlSb.append(" IS NULL");
            } else if ("<>".equals(op)) {
                sqlSb.append(" IS NOT NULL");
            } else {
                sqlSb.append(" " + op + " NULL");
            }
        } else {
            sqlSb.append(" " + op + " ");

            if ("IN".equalsIgnoreCase(op)) {
                Iterable<?> items = null;
                if (value instanceof Iterable) {
                    items = (Iterable<?>) value;
                } else if (value.getClass().isArray()) {
                    ArrayList<Object> arrayList = new ArrayList<Object>();
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; i++) {
                        arrayList.add(Array.get(value, i));
                    }
                    items = arrayList;
                }
                if (items != null) {
                    StringBuffer stringBuffer = new StringBuffer("(");
                    for (Object item : items) {
                        if (String.class.equals(item.getClass())) {
                            String valueStr = (String) item;
                            if (valueStr.indexOf('\'') != -1) { // convert single quotations
                                valueStr = valueStr.replace("'", "''");
                            }
                            stringBuffer.append("'" + valueStr + "'");
                        } else {
                            stringBuffer.append(item);
                        }
                        stringBuffer.append(",");
                    }
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    stringBuffer.append(")");
                    sqlSb.append(stringBuffer.toString());
                } else {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            } else if ("BETWEEN".equalsIgnoreCase(op)) {
                Iterable<?> items = null;
                if (value instanceof Iterable) {
                    items = (Iterable<?>) value;
                } else if (value.getClass().isArray()) {
                    ArrayList<Object> arrayList = new ArrayList<Object>();
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; i++) {
                        arrayList.add(Array.get(value, i));
                    }
                    items = arrayList;
                }
                if (items != null) {
                    Iterator<?> iterator = items.iterator();
                    if (!iterator.hasNext()) throw new IllegalArgumentException("value must have tow items.");
                    Object start = iterator.next();
                    if (!iterator.hasNext()) throw new IllegalArgumentException("value must have tow items.");
                    Object end = iterator.next();


                    if (String.class.equals(start.getClass())) {
                        String startStr = (String) start;
                        if (startStr.indexOf('\'') != -1) { // convert single quotations
                            startStr = startStr.replace("'", "''");
                        }
                        String endStr = end.toString();
                        if (endStr.indexOf('\'') != -1) { // convert single quotations
                            endStr = endStr.replace("'", "''");
                        }
                        sqlSb.append("'" + startStr + "'");
                        sqlSb.append(" AND ");
                        sqlSb.append("'" + endStr + "'");
                    } else {
                        sqlSb.append(start);
                        sqlSb.append(" AND ");
                        sqlSb.append(end);
                    }
                } else {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            } else {
                if (String.class.equals(value.getClass())) {
                    String valueStr = (String) value;
                    if (valueStr.indexOf('\'') != -1) { // convert single quotations
                        valueStr = valueStr.replace("'", "''");
                    }
                    sqlSb.append(valueStr);
                } else {
                    sqlSb.append(value);
                }
            }
        }
        whereItems.add(sqlSb.toString());
    }
}
