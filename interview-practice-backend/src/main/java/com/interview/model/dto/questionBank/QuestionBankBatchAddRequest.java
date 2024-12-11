package com.interview.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量创建题目题库关联请求
 *
 *
 */
@Data
public class QuestionBankBatchAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private List<Long> questionIdList;

    /**
     * 题库 id
     */
    private Long bankId;

    private static final long serialVersionUID = 1L;
}