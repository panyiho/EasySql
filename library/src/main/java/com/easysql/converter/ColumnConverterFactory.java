package com.easysql.converter;


import com.easysql.table.ColumnType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 表类型转换工厂类，可以通过这里来注册需要特殊处理的类型，也可以添加自定义的转换类型
 */
public final class ColumnConverterFactory {

    private ColumnConverterFactory() {
    }


    private static final ConcurrentHashMap<String, ColumnConverter> sColumnConvertMap;

    static {
        sColumnConvertMap = new ConcurrentHashMap<String, ColumnConverter>();

        BooleanColumnConverter booleanColumnConverter = new BooleanColumnConverter();
        sColumnConvertMap.put(boolean.class.getName(), booleanColumnConverter);
        sColumnConvertMap.put(Boolean.class.getName(), booleanColumnConverter);

        ByteArrayColumnConverter byteArrayColumnConverter = new ByteArrayColumnConverter();
        sColumnConvertMap.put(byte[].class.getName(), byteArrayColumnConverter);

        ByteColumnConverter byteColumnConverter = new ByteColumnConverter();
        sColumnConvertMap.put(byte.class.getName(), byteColumnConverter);
        sColumnConvertMap.put(Byte.class.getName(), byteColumnConverter);

        CharColumnConverter charColumnConverter = new CharColumnConverter();
        sColumnConvertMap.put(char.class.getName(), charColumnConverter);
        sColumnConvertMap.put(Character.class.getName(), charColumnConverter);

        DoubleColumnConverter doubleColumnConverter = new DoubleColumnConverter();
        sColumnConvertMap.put(double.class.getName(), doubleColumnConverter);
        sColumnConvertMap.put(Double.class.getName(), doubleColumnConverter);

        FloatColumnConverter floatColumnConverter = new FloatColumnConverter();
        sColumnConvertMap.put(float.class.getName(), floatColumnConverter);
        sColumnConvertMap.put(Float.class.getName(), floatColumnConverter);

        IntegerColumnConverter integerColumnConverter = new IntegerColumnConverter();
        sColumnConvertMap.put(int.class.getName(), integerColumnConverter);
        sColumnConvertMap.put(Integer.class.getName(), integerColumnConverter);

        LongColumnConverter longColumnConverter = new LongColumnConverter();
        sColumnConvertMap.put(long.class.getName(), longColumnConverter);
        sColumnConvertMap.put(Long.class.getName(), longColumnConverter);

        ShortColumnConverter shortColumnConverter = new ShortColumnConverter();
        sColumnConvertMap.put(short.class.getName(), shortColumnConverter);
        sColumnConvertMap.put(Short.class.getName(), shortColumnConverter);

        StringColumnConverter stringColumnConverter = new StringColumnConverter();
        sColumnConvertMap.put(String.class.getName(), stringColumnConverter);
    }


    /**
     * 根据类文件，来获取对应的类型转换器
     * @param columnType
     * @return
     */
    public static ColumnConverter getColumnConverter(Class columnType) {
        ColumnConverter result = null;
        if (sColumnConvertMap.containsKey(columnType.getName())) {
            result = sColumnConvertMap.get(columnType.getName());
        } else if (ColumnConverter.class.isAssignableFrom(columnType)) {
            try {
                ColumnConverter columnConverter = (ColumnConverter) columnType.newInstance();
                if (columnConverter != null) {
                    sColumnConvertMap.put(columnType.getName(), columnConverter);
                }
                result = columnConverter;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (result == null) {
            throw new RuntimeException("Database Column Not Support: " + columnType.getName() +
                    ", please impl ColumnConverter or use ColumnConverterFactory#registerColumnConverter(...)");
        }

        return result;
    }

    /**
     * 根据类文件，来获取对应的数据库存储类型
     * @param columnType
     * @return
     */
    public static ColumnType getDbColumnType(Class columnType) {
        ColumnConverter converter = getColumnConverter(columnType);
        return converter.getColumnDbType();
    }

    /**
     * 注册自定义的类型转换器
     * @param columnType 类型的class
     * @param columnConverter 类型对应的转换器
     */
    public static void registerColumnConverter(Class columnType, ColumnConverter columnConverter) {
        sColumnConvertMap.put(columnType.getName(), columnConverter);
    }

    public static boolean isSupportColumnConverter(Class columnType) {
        if (sColumnConvertMap.containsKey(columnType.getName())) {
            return true;
        } else if (ColumnConverter.class.isAssignableFrom(columnType)) {
            try {
                ColumnConverter columnConverter = (ColumnConverter) columnType.newInstance();
                if (columnConverter != null) {
                    sColumnConvertMap.put(columnType.getName(), columnConverter);
                }
                return columnConverter == null;
            } catch (Throwable e) {
            }
        }
        return false;
    }

}
