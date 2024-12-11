package com.interview.model.dto.bank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题库请求
 *
 *
 */
@Data
public class BankAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;


    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    private static final long serialVersionUID = 1L;
}