package com.easysql;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Pan_ on 2018/2/23.
 */

public interface OnDbVersionChangeListener {

    /**
     * 数据库升级
     * @param sqLiteDatabase 数据库db
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);

    /**
     * 数据库降级
     * @param db 数据库db
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
