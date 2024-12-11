package com.interview.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.interview.annotation.AuthCheck;
import com.interview.common.BaseResponse;
import com.interview.common.DeleteRequest;
import com.interview.common.ErrorCode;
import com.interview.common.ResultUtils;
import com.interview.constant.UserConstant;
import com.interview.exception.BusinessException;
import com.interview.exception.ThrowUtils;
import com.interview.model.dto.questionBank.*;
import com.interview.model.entity.QuestionBank;
import com.interview.model.entity.User;
import com.interview.model.vo.QuestionBankVO;
import com.interview.service.QuestionBankService;
import com.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.Cacheable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目题库关联接口
 *
 *
 */
@RestController
@RequestMapping("/questionBank")
@Slf4j
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建题目题库关联
     *
     * @param questionBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankAddRequest, questionBank);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, true);
        User loginUser = userService.getLoginUser(request);
        questionBank.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankId = questionBank.getId();
        return ResultUtils.success(newQuestionBankId);
    }

    /**
     * 删除题目题库关联
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目题库关联（仅管理员可用）
     *
     * @param questionBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankUpdateRequest, questionBank);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, false);
        // 判断是否存在
        long id = questionBankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目题库关联（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionBank questionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionBankService.getQuestionBankVO(questionBank, request));
    }

    /**
     * 分页获取题目题库关联列表（仅管理员可用）
     *
     * @param questionBankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        return ResultUtils.success(questionBankPage);
    }

    /**
     * 分页获取题目题库关联列表（封装类）
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目题库关联列表
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionBankQueryRequest.setUserId(loginUser.getId());
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    @PostMapping("/remove")
    public BaseResponse<Boolean> removeQuestionBank(@RequestBody QuestionBankRemoveRequest questionBankRemoveRequest) {
        // 参数校验
        ThrowUtils.throwIf(questionBankRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionId = questionBankRemoveRequest.getQuestionId();
        Long bankId = questionBankRemoveRequest.getBankId();
        ThrowUtils.throwIf(questionId == null || bankId == null, ErrorCode.PARAMS_ERROR);
        // 移除题目题库关联
        LambdaQueryWrapper<QuestionBank> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBank.class)
                .eq(QuestionBank::getQuestionId, questionId)
                .eq(QuestionBank::getBankId, bankId);
        boolean result = questionBankService.remove(lambdaQueryWrapper);
        return ResultUtils.success(result);
    }

    // endregion

    /**
     * 批量创建题目题库关联
     *
     * @param questionBankBatchAddRequest
     * @param request
     * @return
     */
    @PostMapping("/batch/add")
    public BaseResponse<Boolean> batchAddQuestionsToBank(@RequestBody QuestionBankBatchAddRequest questionBankBatchAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankBatchAddRequest == null, ErrorCode.PARAMS_ERROR);
        List<Long> questionIdList = questionBankBatchAddRequest.getQuestionIdList();
        Long bankId = questionBankBatchAddRequest.getBankId();
        User loginUser = userService.getLoginUser(request);
        questionBankService.batchAddQuestionToBank(questionIdList, bankId, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 批量删除题目题库关联
     *
     * @param questionBankBatchRemoveRequest
     * @param request
     * @return
     */
    @PostMapping("/batch/delete")
    public BaseResponse<Boolean> batchRemoveQuestionsFromBank(@RequestBody QuestionBankBatchRemoveRequest questionBankBatchRemoveRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankBatchRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        List<Long> questionIdList = questionBankBatchRemoveRequest.getQuestionIdList();
        Long bankId = questionBankBatchRemoveRequest.getBankId();
        User loginUser = userService.getLoginUser(request);
        questionBankService.batchRemoveQuestionFromBank(questionIdList, bankId, loginUser);
        return ResultUtils.success(true);
    }
}
