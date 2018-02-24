package com.easysql.operation;

import android.database.Cursor;
import android.util.Log;

import com.easysql.SqliteDBConfig;
import com.easysql.utils.BaseDbHelper;
import com.easysql.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来进行查询的具体的操作类
 * Created by Pan_ on 2018/2/6.
 */

public class Query<T> extends BaseOperate {
    private List<OrderBy> orderByList;
    private int limit = 0;
    private int offset = 0;

    private WhereBuilder whereBuilder;

    public Query(Class<T> entityClass, SqliteDBConfig sqliteDBConfig, BaseDbHelper baseDbHelper) {
        super(entityClass, sqliteDBConfig, baseDbHelper);
    }

    /**
     * 设置排序，默认是升序
     * @param columnName 排序的列名
     * @return
     */
    public Query<T> orderBy(String columnName) {
        if (orderByList == null) {
            orderByList = new ArrayList<OrderBy>(5);
        }
        orderByList.add(new OrderBy(columnName));
        return this;
    }

    /**
     * 设置排序，可设置是否是降序
     * @param columnName 排序的列名
     *  @param desc true是降序，false是升序
     * @return
     */
    public Query<T> orderBy(String columnName, boolean desc) {
        if (orderByList == null) {
            orderByList = new ArrayList<OrderBy>(5);
        }
        orderByList.add(new OrderBy(columnName, desc));
        return this;
    }

    /**
     * 设置返回个数的限制
     * @param limit
     * @return
     */
    public Query<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 设置limit的偏移值，表示跳过offset个，
     * @param offset 偏移值
     * @return
     */
    public Query<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * where 表达式，可以用来组合丰富的查询条件，如：
     * age = 1的表达式为where("age","=",1)
     * WHERE age BETWEEN  1 and 10 的表达式为where("age","BETWEEN",new int[]{1,10})
     * WHERE age IN(1,10) 的表达式为where("age","IN",new int[]{1,10})
     * @param columnName 需要操作的列名
     * @param op 操作符
     * @param value 值
     * @return
     */
    public Query<T> where(String columnName, String op, Object value) {
        this.whereBuilder = WhereBuilder.b(columnName, op, value);
        return this;
    }

    /**
     * and 操作表达式
     * @param columnName 需要操作的列名
     * @param op 操作符
     * @param value 值
     * @return
     */
    public Query<T> and(String columnName, String op, Object value) {
        this.whereBuilder.and(columnName, op, value);
        return this;
    }

    /**
     * or 操作表达式
     * @param columnName 需要操作的列名
     * @param op 操作符
     * @param value 值
     * @return
     */
    public Query<T> or(String columnName, String op, Object value) {
        this.whereBuilder.or(columnName, op, value);
        return this;
    }


    /**
     * 自定义表达式
     * @param expr 自定义的where表达式
     * @return
     */
    public Query<T> expr(String expr) {
        if (this.whereBuilder == null) {
            this.whereBuilder = WhereBuilder.b();
        }
        this.whereBuilder.expr(expr);
        return this;
    }

    /**
     * 返回查询结果的第一条
     * @return T
     */
    public T first() {
        if (!tableIsExist()) return null;

        this.limit(1);
        long startTime = System.currentTimeMillis();
        String queryString = this.toString();
        Cursor cursor = getBaseDbHelper().getSqLiteDatabase().rawQuery(queryString, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return (T) getTableMapping().cursorToBean(cursor);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeAll(cursor);
                long endTime = System.currentTimeMillis();
                if (sqliteDBConfig.logger) {
                    Log.d(TAG, "query first,  sql:" + queryString + "   耗时:" + String.valueOf(endTime - startTime));
                }
            }
        }
        return null;
    }

    /**
     * 返回全部的查询结果
     * @return List<T>
     */
    public List<T> all() {
        if (!tableIsExist()) return null;

        long startTime = System.currentTimeMillis();
        List<T> result = null;
        String queryString = this.toString();
        Cursor cursor = getBaseDbHelper().getSqLiteDatabase().rawQuery(queryString, null);
        if (cursor != null) {
            try {
                result = new ArrayList<T>();
                while (cursor.moveToNext()) {
                    T entity = (T) getTableMapping().cursorToBean(cursor);
                    result.add(entity);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeAll(cursor);
            }

            long endTime = System.currentTimeMillis();
            if (sqliteDBConfig.logger) {
                Log.d(TAG, "query all, sql:" + queryString + "  耗时:" + String.valueOf(endTime - startTime) + " 查询到：" + result.size() +"条记录");
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        result.append("*");
        result.append(" FROM ").append("\"") .append(getTableName()) .append("\"")
        ;
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(whereBuilder.toString());
        }
        if (orderByList != null && orderByList.size() > 0) {
            result.append(" ORDER BY ");
            for (OrderBy orderBy : orderByList) {
                result.append(orderBy.toString()).append(',');
            }
            result.deleteCharAt(result.length() - 1);
        }
        if (limit > 0) {
            result.append(" LIMIT ").append(limit);
            result.append(" OFFSET ").append(offset);
        }
        return result.toString();
    }


    public static class OrderBy {
        private String columnName;
        private boolean desc;

        public OrderBy(String columnName) {
            this.columnName = columnName;
        }

        public OrderBy(String columnName, boolean desc) {
            this.columnName = columnName;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return "\"" + columnName + "\"" + (desc ? " DESC" : " ASC");
        }
    }

}
