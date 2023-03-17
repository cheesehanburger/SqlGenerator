package com.yupi.sqlfather;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
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
 * @author https://github.com/liyupi
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
        System.out.println(list);
    }
}