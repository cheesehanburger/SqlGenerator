package com.yupi.sqlfather.model.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 创建请求
 *
 * @author hanburger
 */
@Data
public class FieldInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}