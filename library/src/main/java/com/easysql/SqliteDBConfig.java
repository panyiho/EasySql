package com.easysql;

import android.app.Activity;
import android.content.Context;

import com.easysql.table.TableMapping;

import java.util.HashMap;


/**
 * Created by EasySql的数据库配置管理类 on 2018/2/6.
 */

public class SqliteDBConfig {
    public static final String DEFAULT_DB_NAME = "easySqlDb";
    public Context context;
    public int version;
    public boolean logger;
    public String dataBaseName;
    public OnDbVersionChangeListener onDbVersionChangeListener;
    public HashMap<Class<?>, TableMapping> tableMappingHashMap = new HashMap<>();


    public SqliteDBConfig(Context context, int version, boolean logger, String dataBaseName, OnDbVersionChangeListener onDbVersionChangeListener, HashMap<Class<?>, TableMapping> tableMappingHashMap) {
        this.context = context;
        this.version = version;
        this.logger = logger;
        this.dataBaseName = dataBaseName;
        this.onDbVersionChangeListener = onDbVersionChangeListener;
        this.tableMappingHashMap = tableMappingHashMap;
    }

    public static class Builder {
        private Context context;
        private int version = 1;
        private boolean logger = true;
        private String dataBaseName = DEFAULT_DB_NAME;
        private OnDbVersionChangeListener onDbVersionChangeListener;
        private HashMap<Class<?>, TableMapping> tableMappingHashMap = new HashMap<>();

        /**
         * 设置数据库的版本号，默认是1
         * @param version 版本号
         * @return
         */
        public Builder setVersion(int version){
            this.version = version;
            return this;
        }

        /**
         * 设置关于这个数据库的操作是否打印log
         * @param logger true为打印，false为不打印
         * @return
         */
        public Builder setLogger(boolean logger){
            this.logger = logger;
            return this;
        }

        /**
         * 设置数据库的名字
         * @param dataBaseName
         * @return
         */
        public Builder setDataBaseName(String dataBaseName){
            this.dataBaseName = dataBaseName;
            return this;
        }

        /**
         * 设置这个数据库的升级和降级的监听，如果你是用注解方式来使用表的话，可以通过这个方法设置数据库的升级和降级的回调，来达到操作升级和降级的目的，如果你是通过自定义
         * TableMapping的方式来创建表的话，则不需要设置这个监听，直接复写TableMapping的两个升级和降级方法就好了
         * @param onDbVersionChangeListener
         */
        public Builder setOnDbVersionChangeListener(OnDbVersionChangeListener onDbVersionChangeListener) {
            this.onDbVersionChangeListener = onDbVersionChangeListener;
            return this;
        }

        /**
         * 设置类和表的映射
         * @param clazz
         * @param tableMapping
         * @return
         */
        public Builder addTableMapping(Class<?> clazz, TableMapping tableMapping){
            tableMappingHashMap.put(clazz,tableMapping);
            return this;
        }


        public SqliteDBConfig build(Context context){
            if(context == null){
                throw new RuntimeException("context is null!");
            }
            if(context instanceof Activity){
                this.context = context.getApplicationContext();
            }

            return new SqliteDBConfig(context,version,logger,dataBaseName,onDbVersionChangeListener,tableMappingHashMap);

        }

    }
}
