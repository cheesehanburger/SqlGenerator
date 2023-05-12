package com.hanburger.sqlfather.controller;

import com.alibaba.excel.EasyExcel;
import com.hanburger.sqlfather.core.GeneratorFacade;
import com.hanburger.sqlfather.core.model.vo.GenerateVO;
import com.hanburger.sqlfather.core.schema.TableSchema;
import com.hanburger.sqlfather.core.schema.TableSchemaBuilder;
import com.hanburger.sqlfather.common.BaseResponse;
import com.hanburger.sqlfather.common.ErrorCode;
import com.hanburger.sqlfather.common.ResultUtils;
import com.hanburger.sqlfather.exception.BusinessException;
import com.hanburger.sqlfather.model.dto.GenerateByAutoRequest;
import com.hanburger.sqlfather.model.dto.GenerateBySqlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL 相关接口
 *
 * @author hanburger
 */
@RestController
@RequestMapping("/sql")
@Slf4j
public class SqlController {

    //可视化表单接口一键生成接口
    @PostMapping("/generate/schema")
    public BaseResponse<GenerateVO> generateBySchema(@RequestBody TableSchema tableSchema) {
        return ResultUtils.success(GeneratorFacade.generateAll(tableSchema));
    }

    //智能导入接口
    @PostMapping("/get/schema/auto")
    public BaseResponse<TableSchema> getSchemaByAuto(@RequestBody GenerateByAutoRequest autoRequest) {

        if (autoRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(TableSchemaBuilder.buildFromAuto(autoRequest.getContent()));
    }

    /**
     * 根据 SQL 获取 schema
     *
     * @param sqlRequest
     * @return
     */
    //导入建表sql接口
    @PostMapping("/get/schema/sql")
    public BaseResponse<TableSchema> getSchemaBySql(@RequestBody GenerateBySqlRequest sqlRequest) {
        if (sqlRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取 tableSchema
        return ResultUtils.success(TableSchemaBuilder.buildFromSql(sqlRequest.getSql()));
    }

    //导入建表excle接口
    @PostMapping("/get/schema/excel")
    public BaseResponse<TableSchema> getSchemaByExcel(MultipartFile file) {
        return ResultUtils.success(TableSchemaBuilder.buildFromExcel(file));
    }

    /**
     * 下载模拟数据 Excel
     *
     * @param response
     */
    // 下载excle表 接口
    @PostMapping("/download/data/excel")
    public void downloadDataExcel(@RequestBody GenerateVO generateVO, HttpServletResponse response) {
        TableSchema tableSchema = generateVO.getTableSchema();
        String tableName = tableSchema.getTableName();
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里 URLEncoder.encode 可以防止中文乱码
            String fileName = URLEncoder.encode(tableName + "表数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 设置表头 [[id], [username], [create_time], [update_time], [is_deleted]]
            List<List<String>> headList = new ArrayList<>();
            for (TableSchema.Field field : tableSchema.getFieldList()) {
                List<String> head = Collections.singletonList(field.getFieldName());
                headList.add(head);
            }
            // [id, username, create_time, update_time, is_deleted]
            List<String> fieldNameList = tableSchema.getFieldList().stream()
                    .map(TableSchema.Field::getFieldName).collect(Collectors.toList());
            // 设置数据 [[1, 许文轩, 2023-03-16 15:29:34, 2023-03-16 15:29:34, 0], [2, 陈鹏飞, 2023-03-16 15:29:34, 2023-03-16 15:29:34, 0]]
            List<List<Object>> dataList = new ArrayList<>();
            for (Map<String, Object> data : generateVO.getDataList()) {
                List<Object> dataRow = fieldNameList.stream().map(data::get).collect(Collectors.toList());
                dataList.add(dataRow);
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream())
                    .autoCloseStream(Boolean.FALSE)
                    .head(headList)
                    .sheet(tableName + "表")
                    .doWrite(dataList);
        } catch (Exception e) {
            // 重置 response
            response.reset();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }
    }

}
