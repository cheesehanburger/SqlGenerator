package com.hanburger.sqlfather.core.builder.sql;

/**
 * SQL 方言
 *
 * @author hanburger
 */
public interface SQLDialect {

    /**
     * 封装字段名
     * @param name
     * @return
     */
    String wrapFieldName(String name);

    /**
     * 解析字段名
     * @param fieldName
     * @return
     */
    String parseFieldName(String fieldName);

    /**
     * 封装表名
     * @param name
     * @return
     */
    String wrapTableName(String name);

    /**
     * 解析表名
     * @param tableName
     * @return
     */
    String parseTableName(String tableName);
}
