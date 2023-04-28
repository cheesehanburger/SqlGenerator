package com.hanburger.sqlfather.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.hanburger.sqlfather.core.GeneratorFacade;
import com.hanburger.sqlfather.core.schema.TableSchema;
import com.hanburger.sqlfather.service.FieldInfoService;
import com.hanburger.sqlfather.common.ErrorCode;
import com.hanburger.sqlfather.exception.BusinessException;
import com.hanburger.sqlfather.mapper.FieldInfoMapper;
import com.hanburger.sqlfather.model.entity.FieldInfo;
import com.hanburger.sqlfather.model.enums.ReviewStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author hanburgerli
 * @description 针对表【field_info】的数据库操作Service实现
 */
@Service
public class FieldServiceImpl extends ServiceImpl<FieldInfoMapper, FieldInfo> implements FieldInfoService {

    private final static Gson GSON = new Gson();

    @Override
    public void validAndHandleFieldInfo(FieldInfo fieldInfo, boolean add) {
        if (fieldInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = fieldInfo.getContent();
        String name = fieldInfo.getName();
        Integer reviewStatus = fieldInfo.getReviewStatus();
        // 创建时，所有参数必须非空
        if (add && StringUtils.isAnyBlank(name, content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(name) && name.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(content)) {
            if (content.length() > 20000) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
            }
            // 校验字段内容
            try {
                TableSchema.Field field = GSON.fromJson(content, TableSchema.Field.class);
                GeneratorFacade.validField(field);
                // 填充 fieldName
                fieldInfo.setFieldName(field.getFieldName());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容格式错误");
            }
        }
        if (reviewStatus != null && !ReviewStatusEnum.getValues().contains(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }
}




