package com.hanburger.sqlfather.core;


import com.hanburger.sqlfather.core.schema.TableSchemaBuilder;
import org.junit.jupiter.api.Test;

/**
 * 表概要生成器测试
 *
 * @author hanburger
 */
class TableSchemaBuilderTest {

    @Test
    void getFieldTypeByValue() {
        System.out.println(TableSchemaBuilder.getFieldTypeByValue("123.4"));
    }
}