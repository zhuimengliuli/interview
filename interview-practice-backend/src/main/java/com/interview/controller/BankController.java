package com.interview.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.interview.annotation.AuthCheck;
import com.interview.annotation.HotKeyCache;
import com.interview.common.BaseResponse;
import com.interview.common.DeleteRequest;
import com.interview.common.ErrorCode;
import com.interview.common.ResultUtils;
import com.interview.constant.UserConstant;
import com.interview.exception.BusinessException;
import com.interview.exception.ThrowUtils;
import com.interview.model.dto.bank.BankAddRequest;
import com.interview.model.dto.bank.BankEditRequest;
import com.interview.model.dto.bank.BankQueryRequest;
import com.interview.model.dto.bank.BankUpdateRequest;
import com.interview.model.dto.question.QuestionQueryRequest;
import com.interview.model.entity.Bank;
import com.interview.model.entity.Question;
import com.interview.model.entity.User;
import com.interview.model.vo.BankVO;
import com.interview.model.vo.QuestionVO;
import com.interview.service.BankService;
import com.interview.service.QuestionService;
import com.interview.service.UserService;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.interview.constant.HotKeyConstant.BANK_HOT_KEY_PREFIX;

/**
 * 题库接口
 *
 *
 */
@RestController
@RequestMapping("/bank")
@Slf4j
public class BankController {

    @Resource
    private BankService bankService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;



    // region 增删改查

    /**
     * 创建题库
     *
     * @param bankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addBank(@RequestBody BankAddRequest bankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(bankAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Bank bank = new Bank();
        BeanUtils.copyProperties(bankAddRequest, bank);
        // 数据校验
        bankService.validBank(bank, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        bank.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = bankService.save(bank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newBankId = bank.getId();
        return ResultUtils.success(newBankId);
    }

    /**
     * 删除题库
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Bank oldBank = bankService.getById(id);
        ThrowUtils.throwIf(oldBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = bankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库（仅管理员可用）
     *
     * @param bankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateBank(@RequestBody BankUpdateRequest bankUpdateRequest) {
        if (bankUpdateRequest == null || bankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Bank bank = new Bank();
        BeanUtils.copyProperties(bankUpdateRequest, bank);
        // 数据校验
        bankService.validBank(bank, false);
        // 判断是否存在
        long id = bankUpdateRequest.getId();
        Bank oldBank = bankService.getById(id);
        ThrowUtils.throwIf(oldBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = bankService.updateById(bank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库（封装类）
     *
     * @param bankQueryRequest
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<BankVO> getBankVOById(BankQueryRequest bankQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(bankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = bankQueryRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        String key = BANK_HOT_KEY_PREFIX + id;

        // 查询数据库
        Bank bank = bankService.getById(id);
        ThrowUtils.throwIf(bank == null, ErrorCode.NOT_FOUND_ERROR);
        BankVO bankVO = bankService.getBankVO(bank, request);
        // 判断是否需要查询题目列表
        boolean needQueryQuestionList = bankQueryRequest.isNeedQueryQuestionList();
        if (needQueryQuestionList) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setBankId(id);
            questionQueryRequest.setPageSize(bankQueryRequest.getPageSize());
            questionQueryRequest.setCurrent(bankQueryRequest.getCurrent());
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            bankVO.setPage(questionService.getQuestionVOPage(questionPage, request));
        }
        // 缓存热 key
        JdHotKeyStore.smartSet(key, bankVO);

        // 获取封装类
        return ResultUtils.success(bankVO);
    }

    /**
     * 分页获取题库列表（仅管理员可用）
     *
     * @param bankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Bank>> listBankByPage(@RequestBody BankQueryRequest bankQueryRequest) {
        long current = bankQueryRequest.getCurrent();
        long size = bankQueryRequest.getPageSize();
        // 查询数据库
        Page<Bank> bankPage = bankService.page(new Page<>(current, size),
                bankService.getQueryWrapper(bankQueryRequest));
        return ResultUtils.success(bankPage);
    }

    /**
     * 分页获取题库列表（封装类）
     *
     * @param bankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @SentinelResource(value = "listBankVOByPage",
            blockHandler = "handleBlockException",
            fallback = "handleFallBack")
    public BaseResponse<Page<BankVO>> listBankVOByPage(@RequestBody BankQueryRequest bankQueryRequest,
                                                               HttpServletRequest request) {
        long current = bankQueryRequest.getCurrent();
        long size = bankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Bank> bankPage = bankService.page(new Page<>(current, size),
                bankService.getQueryWrapper(bankQueryRequest));
        // 获取封装类
        return ResultUtils.success(bankService.getBankVOPage(bankPage, request));
    }

    /**
     * listBankVOByPage 流控操作
     * 限流：返回系统压力过大，请稍后尝试
     * 熔断：执行降级操作
     *
     * @param bankQueryRequest
     * @param request
     * @param ex
     * @return
     */
    public BaseResponse<Page<BankVO>> handleBlockException(@RequestBody BankQueryRequest bankQueryRequest,
                                                           HttpServletRequest request, BlockException ex) {
        // 降级操作
        if (ex instanceof DegradeException) {
            return handleFallBack(bankQueryRequest, request, ex);
        }
        // 限流操作
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统压力过大，请稍后尝试");
    }

    /**
     * 降级操作
     * @param bankQueryRequest
     * @param request
     * @param ex
     * @return
     */
    public BaseResponse<Page<BankVO>> handleFallBack(@RequestBody BankQueryRequest bankQueryRequest,
                                                           HttpServletRequest request, Throwable ex) {
        return ResultUtils.success(null);
    }

    /**
     * 分页获取当前登录用户创建的题库列表
     *
     * @param bankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<BankVO>> listMyBankVOByPage(@RequestBody BankQueryRequest bankQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(bankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        bankQueryRequest.setUserId(loginUser.getId());
        long current = bankQueryRequest.getCurrent();
        long size = bankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Bank> bankPage = bankService.page(new Page<>(current, size),
                bankService.getQueryWrapper(bankQueryRequest));
        // 获取封装类
        return ResultUtils.success(bankService.getBankVOPage(bankPage, request));
    }

    // endregion
}
