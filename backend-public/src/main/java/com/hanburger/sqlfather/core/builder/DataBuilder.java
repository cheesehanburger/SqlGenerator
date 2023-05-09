package com.hanburger.sqlfather.core.builder;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hanburger.sqlfather.core.generator.DataGenerator;
import com.hanburger.sqlfather.core.generator.DataGeneratorFactory;
import com.hanburger.sqlfather.core.model.enums.MockTypeEnum;
import com.hanburger.sqlfather.core.schema.TableSchema;
import com.hanburger.sqlfather.core.schema.TableSchema.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 数据生成器
 *
 * @author hanburger
 */
public class DataBuilder {

    /**
     * 生成数据
     *
     * @param tableSchema
     * @param rowNum
     * @return
     */
    public static List<Map<String, Object>> generateData(TableSchema tableSchema, int rowNum) {
        List<Field> fieldList = tableSchema.getFieldList();
        // 初始化结果数据
        List<Map<String, Object>> resultList = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            resultList.add(new HashMap<>());
        }
        // 依次生成每一列
        for (Field field : fieldList) {
            MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                    .orElse(MockTypeEnum.NONE);
            // 根据模拟类型，创建对应的数据构造器
            DataGenerator dataGenerator = DataGeneratorFactory.getGenerator(mockTypeEnum);
            // 使用数据构造器生成对应模拟数据
            List<String> mockDataList = dataGenerator.doGenerate(field, rowNum);
            String fieldName = field.getFieldName();
            // 填充结果列表
            if (CollectionUtils.isNotEmpty(mockDataList)) {
                for (int i = 0; i < rowNum; i++) {
                    resultList.get(i).put(fieldName, mockDataList.get(i));
                }
            }
        }
        return resultList;
    }
}
