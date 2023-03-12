package com.yupi.sqlfather;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Test
    public void TestStream() {
        @Data
        class User {
            String name;
            String address;
            Integer age;
        }

        Map<Integer,User> map = new HashMap<>();
        User user1 = new User();
        user1.setName("han1");
        user1.setAddress("jiansu1");
        user1.setAge(101);

        User user2 = new User();
        user2.setName("han2");
        user2.setAddress("jiansu2");
        user2.setAge(102);

        User user3 = new User();
        user3.setName("han3");
        user3.setAddress("jiansu3");
        user3.setAge(103);

        map.put(1,user1);
        map.put(2,user2);
        map.put(3,user3);

        List<String> list = map.values().stream().map(user -> user.getName()).collect(Collectors.toList());
        System.out.println(list);
    }
}