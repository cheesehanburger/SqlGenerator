package com.hanburger.sqlfather;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.hanburger.sqlfather.model.file.CsvFile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CsvTest {

    @Test
    public void testTrans() {
        String chinese = "nihao";
        // 将中文转换为英文小写
        String lowercaseEnglish = convertToEnglishLowercase(chinese);
        System.out.println("转换后的英文小写：" + lowercaseEnglish);

        // 将中文转换为英文大写
        String uppercaseEnglish = convertToEnglishUppercase(chinese);
        System.out.println("转换后的英文大写：" + uppercaseEnglish);
    }

    private static String convertToEnglishLowercase(String input) {
        return input.toLowerCase();
    }

    private static String convertToEnglishUppercase(String input) {
        return input.toUpperCase();
    }



    @Test
    public void parserCsvFile() {
        CsvReadConfig csvReadConfig = new CsvReadConfig();
        // 设置 文本分隔符，文本包装符，默认双引号'"'
        //csvReadConfig.setTextDelimiter('\t');
        // 字段分割符号，默认为逗号
        csvReadConfig.setFieldSeparator(',');
        // 设置注释符号
        // csvReadConfig.setCommentCharacter('#');
        // CSV文件是否包含表头(因为表头不是数据内容)
        csvReadConfig.setContainsHeader(true);
        // 或者使用如下配置设置表头开始行号，-1L代表无表头
        // csvReadConfig.setHeaderLineNo(1L);
        //设置开始的行（包括），默认0，此处为原始文件行号
        // csvReadConfig.setBeginLineNo(0);
        // 是否跳过空白行，默认为true
        // csvReadConfig.setSkipEmptyRows(true);
        // 设置每行字段个数不同时是否抛出异常，默认false
        // csvReadConfig.setErrorOnDifferentFieldCount(false);
        // 将表头的别称对转换为对应的字段
        Map<String, String> headerAlias = new LinkedHashMap<>();
        /**
         * 管井名称,管井ID,所属区域,维护单位,井类型,采集单位,采集时间,产权单位,管井用途,维护方式,生命周期状态,产权性质,业务级别,地形特征,所属工程,区域类型,
         * 原有名称,井盖材质,井类别,人井结构,井底长,井底宽,井底高,路边距,井底深度,上覆厚,道路名称,是否有电子锁,井盖形状,监理单位,参建单位,组织ID,
         * 固定资产编号,管井规格,是否危险点,MIS编号,是否局前井,所有权人,审验锁定标记,NFC编号,所属分公司,所属管理区域,备注一字段,质量责任人,
         * 具体位置,一线数据维护人,管孔数
         */
        headerAlias.put("管井名称", "param1");
        headerAlias.put("管井ID", "param2");
        headerAlias.put("所属区域", "param3");

        csvReadConfig.setHeaderAlias(headerAlias);
        CsvReader reader = CsvUtil.getReader();
        List<CsvFile> result = reader.read(ResourceUtil.getUtf8Reader("test_csv.csv"), CsvFile.class);
        int i = 0;
        for (CsvFile csvFile : result) {
            System.out.println(csvFile);
            if (i == 10) {
                return;
            }
            i++;
        }
    }
}
