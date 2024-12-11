package com.interview.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除题目题库关联请求
 *
 *
 */
@Data
public class QuestionBankRemoveRequest implements Serializable {
    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题库 id
     */
    private Long bankId;
    private static final long serialVersionUID = 1L;
}