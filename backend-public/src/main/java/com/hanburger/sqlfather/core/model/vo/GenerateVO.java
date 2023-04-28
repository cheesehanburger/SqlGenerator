package com.hanburger.sqlfather.core.model.vo;

import com.hanburger.sqlfather.core.schema.TableSchema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * 生成的返回值
 *
 * @author hanburger
 */
@Data
public class GenerateVO implements Serializable {

    //tableSchema数据
    private TableSchema tableSchema;

    //建表sql
    private String createSql;

    //模拟数据
    private List<Map<String, Object>> dataList;

    //插入语句
    private String insertSql;

    //json数据
    private String dataJson;

    //java类数据
    private String javaEntityCode;

    //java对象数据
    private String javaObjectCode;

    //ts数据
    private String typescriptTypeCode;

    private static final long serialVersionUID = 7122637163626243606L;
}
