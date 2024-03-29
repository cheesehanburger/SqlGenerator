package com.hanburger.sqlfather.core.schema;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.hanburger.sqlfather.core.builder.sql.MySQLDialect;
import com.hanburger.sqlfather.common.ErrorCode;
import com.hanburger.sqlfather.core.schema.TableSchema.Field;
import com.hanburger.sqlfather.core.model.enums.FieldTypeEnum;
import com.hanburger.sqlfather.core.model.enums.MockTypeEnum;
import com.hanburger.sqlfather.exception.BusinessException;
import com.hanburger.sqlfather.model.entity.FieldInfo;
import com.hanburger.sqlfather.service.FieldInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 表概要生成器
 *
 * @author hanburger
 */
@Component
@Slf4j
public class TableSchemaBuilder {

    private final static Gson GSON = new Gson();

    private static FieldInfoService fieldInfoService;

    private static final MySQLDialect sqlDialect = new MySQLDialect();

    @Resource
    public void setFieldInfoService(FieldInfoService fieldInfoService) {
        TableSchemaBuilder.fieldInfoService = fieldInfoService;
    }

    /**
     * 日期格式
     */
    private static final String[] DATE_PATTERNS = {"yyyy-MM-dd", "yyyy年MM月dd日", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};

    /**
     * 智能构建
     *
     * @param content
     * @return
     */
    // 将字段名转为TableSchema
    public static TableSchema buildFromAuto(String content) {
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 切分单词
        String[] words = content.split("[,，]");
        if (ArrayUtils.isEmpty(words) || words.length > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据单词去词库里匹配列信息(name或者fieldName相同)，未匹配到的使用默认值
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("name", Arrays.asList(words)).or().in("fieldName", Arrays.asList(words));
        List<FieldInfo> fieldInfoList = fieldInfoService.list(queryWrapper);

        // 名称 => 字段信息（以name为键，字段信息为值）
        Map<String, List<FieldInfo>> nameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getName));
        // 字段名称 => 字段信息（以Field为键，字段信息为值）
        Map<String, List<FieldInfo>> fieldNameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getFieldName));

        TableSchema tableSchema = new TableSchema();
        tableSchema.setTableName("my_table");
        tableSchema.setTableComment("自动生成的表");
        List<Field> fieldList = new ArrayList<>();
        //将所有智能输入的字段进行比对
        for (String word : words) {
            Field field;
            //将所有能在库中匹配得到的Name或者fieldName的字段信息，计入字段信息表infoList
            List<FieldInfo> infoList = Optional.ofNullable(nameFieldInfoMap.get(word)).orElse(fieldNameFieldInfoMap.get(word));
            if (CollectionUtils.isNotEmpty(infoList)) {
                // 匹配到的将字段信息表已有第一个字段的作为值
                field = GSON.fromJson(infoList.get(0).getContent(), Field.class);
            } else {
                // 未匹配到的使用默认值
                field = getDefaultField(word);
            }
            fieldList.add(field);
        }
        tableSchema.setFieldList(fieldList);
        return tableSchema;
    }

    /**
     * 根据建表 SQL 构建
     *
     * @param sql 建表 SQL
     * @return 生成的 TableSchema
     */
    //将sql语句解析为TableSchema
    public static TableSchema buildFromSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        try {
            //解析 SQL
            MySqlCreateTableParser parser = new MySqlCreateTableParser(sql);
            SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
            TableSchema tableSchema = new TableSchema();
            tableSchema.setDbName(sqlCreateTableStatement.getSchema());
            tableSchema.setTableName(sqlDialect.parseTableName(sqlCreateTableStatement.getTableName()));    // `user`=>user
            String tableComment = null;
            if (sqlCreateTableStatement.getComment() != null) {
                tableComment = sqlCreateTableStatement.getComment().toString();
                if (tableComment.length() > 2) {
                    tableComment = tableComment.substring(1, tableComment.length() - 1);
                }
            }
            tableSchema.setTableComment(tableComment);
            List<Field> fieldList = new ArrayList<>();
            // 解析列
            for (SQLTableElement sqlTableElement : sqlCreateTableStatement.getTableElementList()) {

                // 主键字段,如果从SQL解析字段为pk，则在schema中对应的field的primarykey设置为ture（未起作用）
                if (sqlTableElement instanceof SQLPrimaryKey) {
                    SQLPrimaryKey sqlPrimaryKey = (SQLPrimaryKey) sqlTableElement;
                    String primaryFieldName = sqlDialect.parseFieldName(sqlPrimaryKey.getColumns().get(0).toString()); // 从sql中获取主键名称，'id' => id

                    fieldList.forEach(field -> {
                        if (field.getFieldName().equals(primaryFieldName)) {
                            field.setPrimaryKey(true);
                        }
                    });
                } else if (sqlTableElement instanceof SQLColumnDefinition) {
                    // 非主键字段进行字段信息设置
                    SQLColumnDefinition columnDefinition = (SQLColumnDefinition) sqlTableElement;
                    Field field = new Field();
                    //设置字段名
                    field.setFieldName(sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                    //设置字段类型
                    field.setFieldType(columnDefinition.getDataType().toString());
                    //设置字段默认值
                    String defaultValue = null;
                    if (columnDefinition.getDefaultExpr() != null) {
                        defaultValue = columnDefinition.getDefaultExpr().toString();
                    }
                    field.setDefaultValue(defaultValue);
                    //设置字段是否为空
                    field.setNotNull(columnDefinition.containsNotNullConstaint());
                    //设置字段注释
                    String comment = null;
                    if (columnDefinition.getComment() != null) {
                        comment = columnDefinition.getComment().toString();
                        if (comment.length() > 2) {
                            comment = comment.substring(1, comment.length() - 1);  //注释信息去除头尾的""
                        }
                    }
                    field.setComment(comment);
                    //设置字段是否为主键
                    field.setPrimaryKey(columnDefinition.isPrimaryKey());
                    //设置字段是否自增
                    field.setAutoIncrement(columnDefinition.isAutoIncrement());
                    //设置字段的更新事件
                    String onUpdate = null;
                    if (columnDefinition.getOnUpdate() != null) {
                        onUpdate = columnDefinition.getOnUpdate().toString();
                    }
                    field.setOnUpdate(onUpdate);
                    //设置字段模拟数据类型，默认为不模拟
                    field.setMockType(MockTypeEnum.NONE.getValue());
                    fieldList.add(field);
                }
            }
            tableSchema.setFieldList(fieldList);
            return tableSchema;
        } catch (Exception e) {
            log.error("SQL 解析错误", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请确认 SQL 语句正确");
        }
    }

    /**
     * 根据 Excel 文件构建
     *
     * @param file Excel 文件
     * @return 生成的 TableSchema
     */
    public static TableSchema buildFromExcel(MultipartFile file) {
        try {
            //通过EasyExcel将excle数据转为[{0=title1,1=title},{0=value,1=value}]类型的list
            List<Map<Integer, String>> dataList = EasyExcel.read(file.getInputStream()).sheet().headRowNumber(0).doReadSync();
            if (CollectionUtils.isEmpty(dataList)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格无数据");
            }
            // 第一行元素为表头
            Map<Integer, String> titleMap = dataList.get(0);
            //将表头解析为字段，为[field1,field2，field3]格式
            List<Field> fieldList = titleMap.values().stream().map(name -> {
                Field field = new Field();
                field.setFieldName(name);
                field.setComment(name);
                field.setFieldType(FieldTypeEnum.TEXT.getValue());
                return field;
            }).collect(Collectors.toList());
            // 第二行元素为值
            if (dataList.size() > 1) {
                Map<Integer, String> dataMap = dataList.get(1);
                //根据第二行元素，解析每一列数据的的格式
                for (int i = 0; i < fieldList.size(); i++) {
                    String value = dataMap.get(i);
                    String fieldType = getFieldTypeByValue(value);
                    //将解析出的数据格式设置到对应的字段中
                    fieldList.get(i).setFieldType(fieldType);
                }
            }
            TableSchema tableSchema = new TableSchema();
            tableSchema.setFieldList(fieldList);
            return tableSchema;
        } catch (Exception e) {
            log.error("buildFromExcel error", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格解析错误");
        }
    }

    /**
     * 根据 value 获取字段类型枚举
     *
     * @param value
     * @return
     */
    public static String getFieldTypeByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return FieldTypeEnum.TEXT.getValue();
        }
        // 布尔
        if ("false".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
            return FieldTypeEnum.TINYINT.getValue();
        }
        // 整数
        if (StringUtils.isNumeric(value)) {
            long number = Long.parseLong(value);
            if (number > Integer.MAX_VALUE) {
                return FieldTypeEnum.BIGINT.getValue();
            }
            return FieldTypeEnum.INT.getValue();
        }
        // 小数
        if (isDouble(value)) {
            return FieldTypeEnum.DOUBLE.getValue();
        }
        // 日期
        if (isDate(value)) {
            return FieldTypeEnum.DATETIME.getValue();
        }
        return FieldTypeEnum.TEXT.getValue();
    }

    /**
     * 判断字符串是不是 double 型
     *
     * @param str
     * @return
     */
    private static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断是否为日期
     *
     * @param str
     * @return
     */
    private static boolean isDate(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            DateUtils.parseDate(str, DATE_PATTERNS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取默认字段
     *
     * @param word
     * @return
     */
    private static Field getDefaultField(String word) {
        final Field field = new Field();
        field.setFieldName(word);
        field.setFieldType("text");
        field.setDefaultValue("");
        field.setNotNull(false);
        field.setComment(word);
        field.setPrimaryKey(false);
        field.setAutoIncrement(false);
        field.setMockType("");
        field.setMockParams("");
        field.setOnUpdate("");
        return field;
    }

}
