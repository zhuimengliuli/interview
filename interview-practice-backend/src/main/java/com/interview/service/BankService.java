package com.interview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.interview.model.dto.bank.BankQueryRequest;
import com.interview.model.entity.Bank;
import com.interview.model.vo.BankVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题库服务
 *
 *
 */
public interface BankService extends IService<Bank> {

    /**
     * 校验数据
     *
     * @param bank
     * @param add 对创建的数据进行校验
     */
    void validBank(Bank bank, boolean add);

    /**
     * 获取查询条件
     *
     * @param bankQueryRequest
     * @return
     */
    QueryWrapper<Bank> getQueryWrapper(BankQueryRequest bankQueryRequest);
    
    /**
     * 获取题库封装
     *
     * @param bank
     * @param request
     * @return
     */
    BankVO getBankVO(Bank bank, HttpServletRequest request);

    /**
     * 分页获取题库封装
     *
     * @param bankPage
     * @param request
     * @return
     */
    Page<BankVO> getBankVOPage(Page<Bank> bankPage, HttpServletRequest request);
}
