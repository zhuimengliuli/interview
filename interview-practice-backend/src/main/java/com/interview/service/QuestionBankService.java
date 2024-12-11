package com.interview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.interview.model.dto.questionBank.QuestionBankQueryRequest;
import com.interview.model.entity.QuestionBank;
import com.interview.model.entity.User;
import com.interview.model.vo.QuestionBankVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目题库关联服务
 *
 *
 */
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 校验数据
     *
     * @param questionBank
     * @param add 对创建的数据进行校验
     */
    void validQuestionBank(QuestionBank questionBank, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQueryRequest
     * @return
     */
    QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest);
    
    /**
     * 获取题目题库关联封装
     *
     * @param questionBank
     * @param request
     * @return
     */
    QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request);

    /**
     * 分页获取题目题库关联封装
     *
     * @param questionBankPage
     * @param request
     * @return
     */
    Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request);

    /**
     * 批量添加题目题库关联（仅供内部使用）
     *
     * @param questionBankList
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAddQuestionBankToInner(List<QuestionBank> questionBankList);

    /**
     * 批量添加题目题库关联
     *
     * @param questionIdList
     * @param bankId
     * @param loginUser
     * @return
     */
    void batchAddQuestionToBank(List<Long> questionIdList, long bankId, User loginUser);

    /**
     * 批量删除题目题库关联
     *
     * @param questionIdList
     * @param bankId
     * @param loginUser
     * @return
     */
    void batchRemoveQuestionFromBank(List<Long> questionIdList, long bankId, User loginUser);

}