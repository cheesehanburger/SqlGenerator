package com.hanburger.sqlfather;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hanburger.sqlfather.core.model.enums.FieldTypeEnum;
import com.hanburger.sqlfather.core.schema.TableSchema;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EasyExcel 测试
 *
 * @author hanburger
 */
@SpringBootTest
public class EasyExcelTest {

    @Test
    public void doImport() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        Map<Integer, String> titleMap = list.get(0);
        List<TableSchema.Field> fieldList = titleMap.values().stream().map(name -> {
            TableSchema.Field field = new TableSchema.Field();
            field.setFieldName(name);
            field.setComment(name);
            field.setFieldType(FieldTypeEnum.TEXT.getValue());
            return field;
        }).collect(Collectors.toList());
        System.out.println(fieldList);
    }
}