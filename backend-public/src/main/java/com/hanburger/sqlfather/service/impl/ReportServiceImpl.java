package com.hanburger.sqlfather.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanburger.sqlfather.exception.BusinessException;
import com.hanburger.sqlfather.mapper.ReportMapper;
import com.hanburger.sqlfather.service.ReportService;
import com.hanburger.sqlfather.common.ErrorCode;
import com.hanburger.sqlfather.model.entity.Report;
import com.hanburger.sqlfather.model.enums.ReportStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author hanburgerli
 * @description 针对表【report(举报)】的数据库操作Service实现
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Override
    public void validReport(Report report, boolean add) {
        if (report == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = report.getContent();
        Long reportedId = report.getReportedId();
        Integer status = report.getStatus();
        if (StringUtils.isNotBlank(content) && content.length() > 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        // 创建时必须指定
        if (add) {
            if (reportedId == null || reportedId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        } else {
            if (status != null && !ReportStatusEnum.getValues().contains(status)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }
}




