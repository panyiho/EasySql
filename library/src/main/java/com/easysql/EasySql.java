package com.easysql;

import android.content.Context;
import android.text.TextUtils;

import com.easysql.operation.CURD;
import com.easysql.operation.Query;
import com.easysql.table.TableMapping;
import com.easysql.utils.BaseDbHelper;

import java.util.HashMap;

/**
 * EasySql orm的总管理类，用来获取对应的操作实例，也进行数据库的孵化
 * Created by Pan_ on 2018/2/6.
 */

public class EasySql {
    private Context context;
    private int version;
    private boolean logger;
    private String dataBaseName;
    private HashMap<Class<?>, TableMapping> tableMappingHashMap = new HashMap<>();
    private SqliteDBConfig sqliteDBConfig;
    private BaseDbHelper baseDbHelper;

    private static final HashMap<String, EasySql> sSqliteClients = new HashMap<>();

    private EasySql(Context context, int version, boolean logger, String dataBaseName, HashMap<Class<?>, TableMapping> tableMappingHashMap, SqliteDBConfig sqliteDBConfig) {
        this.context = context;
        this.version = version;
        this.logger = logger;
        this.dataBaseName = dataBaseName;
        this.tableMappingHashMap = tableMappingHashMap;
        this.sqliteDBConfig = sqliteDBConfig;
        this.baseDbHelper = new BaseDbHelper(context, sqliteDBConfig);
    }

    /**
     * 孵化数据化，一个SqliteDBConfig对应一个db数据库文件，也就是对应一个数据库
     * @param config
     */
    public static void hatch(SqliteDBConfig config) {
        synchronized (sSqliteClients) {
            sSqliteClients.put(config.dataBaseName, new EasySql(config.context, config.version, config.logger, config.dataBaseName, config.tableMappingHashMap, config));
        }
    }

    /**
     * 获取默认的数据库操作实例，默认的数据库名字为“ymDb”
     * @return
     */
    public static synchronized EasySql getDefault() {
        return getSqlClient(SqliteDBConfig.DEFAULT_DB_NAME);
    }

    /**
     * 根据数据库名字获取对应的数据库操作实例
     * @param dbName
     * @return
     */
    public static synchronized EasySql getSqlClient(String dbName) {
        if (TextUtils.isEmpty(dbName)) {
            return null;
        }
        EasySql easySql = sSqliteClients.get(dbName);
        if (easySql == null) {
            throw new RuntimeException("the db " + dbName + "have not be hatch");
        }
        return easySql;
    }

    /**
     * 获取查询实例，使用这个方法进行查询
     * @param clazz 操作的表对应的java类
     * @param <T>
     * @return
     */
    public <T> Query<T> query(Class<T> clazz) {
        return new Query<>(clazz, sqliteDBConfig, baseDbHelper);
    }

    /**
     * 获取表操作的实例，使用这个方法进行增删改
     * @param clazz 操作的表对应的java类
     * @param <T>
     * @return
     */
    public <T> CURD<T> curd(Class<T> clazz) {
        return new CURD<>(clazz, sqliteDBConfig, baseDbHelper);
    }

}
