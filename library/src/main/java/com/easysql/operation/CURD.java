package com.easysql.operation;


import android.content.ContentValues;
import android.text.TextUtils;

import com.easysql.SqliteDBConfig;
import com.easysql.table.TableMapping;
import com.easysql.utils.BaseDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 增删改的具体操作类
 * Created by Pan_ on 2018/2/7.
 */

public class CURD<T> extends BaseOperate<T> {

    public CURD(Class<T> entityClass, SqliteDBConfig sqliteDBConfig, BaseDbHelper baseDbHelper) {
        super(entityClass, sqliteDBConfig, baseDbHelper);
    }

    /**
     * 批量插入
     * @param list
     * @return
     */
    public long insert(List<T> list) {
        List<ContentValues> contentValues = new ArrayList<>();
        for (T item : list) {
            ContentValues value = getTableMapping().beanToContentValues(item);
            filterIdColumn(getTableMapping(), value);
            contentValues.add(value);
        }
        return getBaseDbHelper().insert(getTableName(), contentValues);
    }

    /**
     * 插入一条数据
     * @param value
     * @return
     */
    public long insert(T value) {
        ContentValues contentValues = getTableMapping().beanToContentValues(value);
        if (contentValues == null) {
            return 0;
        }
        filterIdColumn(getTableMapping(), contentValues);
        return getBaseDbHelper().insert(getTableName(), contentValues);
    }

    /**
     * 更新一条数据
     * @param entity
     * @return
     */
    public long update(T entity) {
        ContentValues contentValues = getTableMapping().beanToContentValues(entity);
        if (contentValues == null) {
            return 0;
        }
        return getBaseDbHelper().update(getTableName(), contentValues, getTableMapping().getIDColumn().getColumnName() + "=?", new String[]{contentValues.getAsString(getTableMapping().getIDColumn().getColumnName())});
    }

    /**
     * 批量更新数据
     * @param entitys
     * @return
     */
    public long update(List<T> entitys) {
        List<ContentValues> valuesList = new ArrayList<>();
        for (T entity : entitys) {
            ContentValues contentValues = getTableMapping().beanToContentValues(entity);
            valuesList.add(contentValues);
        }
        if (valuesList == null || valuesList.isEmpty()) {
            return 0;
        }
        return getBaseDbHelper().update(getTableMapping(), valuesList);
    }

    /**
     * 根据条件修改对应的列的值
     * @param modifyKeyValue 需要修改的列的名字和value
     * @param whereBuilder where条件表达式
     * @return
     */
    public int update(ContentValues modifyKeyValue, WhereBuilder whereBuilder) {
        if (modifyKeyValue == null || modifyKeyValue.size() <= 0) {
            return 0;
        }
        String sql = SqlBuilder.buildUpdateSqlInfo(getTableMapping(), whereBuilder, modifyKeyValue);
        filterIdColumn(getTableMapping(), modifyKeyValue);
        return getBaseDbHelper().executeUpdateDeleteSql(sql, modifyKeyValue);
    }

    /**
     *根据id删除记录
     * @param id 条件的id
     * @return
     */
    public int deleteById(String id) {
        if (TextUtils.isEmpty(id)) {
            return -1;
        }
        String sql = SqlBuilder.buildDeleteSqlById(getTableMapping(), id);
        return getBaseDbHelper().executeUpdateDeleteSql(sql);
    }

    /**
     * 删除全部的记录
     * @return
     */
    public int deleteAll() {
        String sql = SqlBuilder.buildDeleteSql(getTableMapping());
        return getBaseDbHelper().executeUpdateDeleteSql(sql);
    }

    /**
     * 删除一条记录
     * @param entity
     * @return
     */
    public int delete(T entity) {
        ContentValues contentValues = getTableMapping().beanToContentValues(entity);
        if (contentValues == null || contentValues.size() <= 0) {
            return -1;
        }
        Object id = contentValues.get(getTableMapping().getIDColumn().getColumnName());
        String sql = SqlBuilder.buildDeleteSqlById(getTableMapping(), id);
        return getBaseDbHelper().executeUpdateDeleteSql(sql);
    }

    /**
     * 根据条件删除记录
     * @param whereBuilder
     * @return
     */
    public int deleteBy(WhereBuilder whereBuilder) {
        if (whereBuilder == null) {
            return -1;
        }
        String sql = SqlBuilder.buildDeleteSqlInfo(getTableMapping(), whereBuilder);
        return getBaseDbHelper().executeUpdateDeleteSql(sql);
    }

    /**
     * 过滤并去掉主键，以免业务上面修改了主键，然后更新了
     *
     * @param tableMapping
     * @param contentValues
     */
    private void filterIdColumn(TableMapping tableMapping, ContentValues contentValues) {
        if (tableMapping == null || contentValues == null || contentValues.size() <= 0 || !tableMapping.getIDColumn().isAutoincrement()) {
            return;
        }
        String idColmnName = tableMapping.getIDColumn().getColumnName();
        contentValues.remove(idColmnName);
    }


}
