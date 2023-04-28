package com.hanburger.sqlfather.core;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hanburger.sqlfather.core.model.vo.GenerateVO;
import com.hanburger.sqlfather.core.schema.SchemaException;
import com.hanburger.sqlfather.core.schema.TableSchema;
import com.hanburger.sqlfather.core.builder.FrontendCodeBuilder;
import com.hanburger.sqlfather.core.builder.DataBuilder;
import com.hanburger.sqlfather.core.builder.JavaCodeBuilder;
import com.hanburger.sqlfather.core.builder.JsonBuilder;
import com.hanburger.sqlfather.core.builder.SqlBuilder;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 集中数据生成器
 * 门面模式，统一生成
 *
 * @author hanburger
 */
@Component
@Slf4j
public class GeneratorFacade {

    /**
     * 生成所有内容
     *
     * @param tableSchema
     * @return
     */
    public static GenerateVO generateAll(TableSchema tableSchema) {
        // 校验
        validSchema(tableSchema);
        SqlBuilder sqlBuilder = new SqlBuilder();
        // 构造建表 SQL
        String createSql = sqlBuilder.buildCreateTableSql(tableSchema);
        int mockNum = tableSchema.getMockNum();
        // 生成模拟数据 [{username='han',address='jiangsu'},{username='zhao',address='nanjing'}]
        List<Map<String, Object>> dataList = DataBuilder.generateData(tableSchema, mockNum);
        // 生成插入 SQL
        String insertSql = sqlBuilder.buildInsertSql(tableSchema, dataList);
        // 生成数据 json
        String dataJson = JsonBuilder.buildJson(dataList);
        // 生成 java 实体代码
        String javaEntityCode = JavaCodeBuilder.buildJavaEntityCode(tableSchema);
        // 生成 java 对象代码
        String javaObjectCode = JavaCodeBuilder.buildJavaObjectCode(tableSchema, dataList);
        // 生成 typescript 类型代码
        String typescriptTypeCode = FrontendCodeBuilder.buildTypeScriptTypeCode(tableSchema);
        // 封装返回
        GenerateVO generateVO = new GenerateVO();
        generateVO.setTableSchema(tableSchema);
        generateVO.setCreateSql(createSql);
        generateVO.setDataList(dataList);
        generateVO.setInsertSql(insertSql);
        generateVO.setDataJson(dataJson);
        generateVO.setJavaEntityCode(javaEntityCode);
        generateVO.setJavaObjectCode(javaObjectCode);
        generateVO.setTypescriptTypeCode(typescriptTypeCode);
        return generateVO;
    }

    /**
     * 验证 schema
     *
     * @param tableSchema 表概要
     */
    public static void validSchema(TableSchema tableSchema) {
        if (tableSchema == null) {
            throw new SchemaException("数据为空");
        }
        String tableName = tableSchema.getTableName();
        if (StringUtils.isBlank(tableName)) {
            throw new SchemaException("表名不能为空");
        }
        Integer mockNum = tableSchema.getMockNum();
        // 默认生成 20 条
        if (tableSchema.getMockNum() == null) {
            tableSchema.setMockNum(20);
            mockNum = 20;
        }
        if (mockNum > 100 || mockNum < 10) {
            throw new SchemaException("生成条数设置错误");
        }
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new SchemaException("字段列表不能为空");
        }
        for (TableSchema.Field field : fieldList) {
            validField(field);
        }
    }

    /**
     * 校验字段
     *
     * @param field
     */
    public static void validField(TableSchema.Field field) {
        String fieldName = field.getFieldName();
        String fieldType = field.getFieldType();
        if (StringUtils.isBlank(fieldName)) {
            throw new SchemaException("字段名不能为空");
        }
        if (StringUtils.isBlank(fieldType)) {
            throw new SchemaException("字段类型不能为空");
        }
    }

}
