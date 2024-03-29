package com.hanburger.sqlfather.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanburger.sqlfather.model.entity.TableInfo;

/**
 * @author hanburgerli
 * @description 针对表【table_info】的数据库操作Service
 */
public interface TableInfoService extends IService<TableInfo> {

    /**
     * 校验并处理
     *
     * @param tableInfo
     * @param add 是否为创建校验
     */
    void validAndHandleTableInfo(TableInfo tableInfo, boolean add);
}
