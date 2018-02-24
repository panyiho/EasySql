package com.easysql.table;

/**
 * 数据库列的类型
 * Created by Pan_ on 2018/2/8.
 */

public enum ColumnType {

    INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT"), BLOB("BLOB"), NULL("NULL");

    private String value;

    ColumnType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

