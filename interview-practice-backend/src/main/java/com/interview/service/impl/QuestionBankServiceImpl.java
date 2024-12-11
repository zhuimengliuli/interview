package com.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interview.common.ErrorCode;
import com.interview.constant.CommonConstant;
import com.interview.exception.BusinessException;
import com.interview.exception.ThrowUtils;
import com.interview.mapper.QuestionBankMapper;
import com.interview.model.dto.questionBank.QuestionBankQueryRequest;
import com.interview.model.entity.Bank;
import com.interview.model.entity.Question;
import com.interview.model.entity.QuestionBank;
import com.interview.model.entity.User;
import com.interview.model.vo.QuestionBankVO;
import com.interview.model.vo.UserVO;
import com.interview.service.BankService;
import com.interview.service.QuestionBankService;
import com.interview.service.QuestionService;
import com.interview.service.UserService;
import com.interview.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 题目题库关联服务实现
 */
@Service
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Resource
    private BankService bankService;

    /**
     * 校验数据
     *
     * @param questionBank
     * @param add          对创建的数据进行校验
     */
    @Override
    public void validQuestionBank(QuestionBank questionBank, boolean add) {
        ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long questionId = questionBank.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        }
        Long bankId = questionBank.getBankId();
        if (bankId != null) {
            Bank bank = bankService.getById(bankId);
            ThrowUtils.throwIf(bank == null, ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验规则
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        if (questionBankQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQueryRequest.getId();
        Long notId = questionBankQueryRequest.getNotId();
        String sortField = questionBankQueryRequest.getSortField();
        String sortOrder = questionBankQueryRequest.getSortOrder();
        Long userId = questionBankQueryRequest.getUserId();
        Long questionId = questionBankQueryRequest.getQuestionId();
        Long bankId = questionBankQueryRequest.getBankId();
        // todo 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(bankId), "bankId", bankId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目题库关联封装
     *
     * @param questionBank
     * @param request
     * @return
     */
    @Override
    public QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankVO questionBankVO = QuestionBankVO.objToVo(questionBank);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBank.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankVO.setUser(userVO);
        // endregion

        return questionBankVO;
    }

    /**
     * 分页获取题目题库关联封装
     *
     * @param questionBankPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request) {
        List<QuestionBank> questionBankList = questionBankPage.getRecords();
        Page<QuestionBankVO> questionBankVOPage = new Page<>(questionBankPage.getCurrent(), questionBankPage.getSize(), questionBankPage.getTotal());
        if (CollUtil.isEmpty(questionBankList)) {
            return questionBankVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankVO> questionBankVOList = questionBankList.stream().map(questionBank -> {
            return QuestionBankVO.objToVo(questionBank);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankList.stream().map(QuestionBank::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankVOList.forEach(questionBankVO -> {
            Long userId = questionBankVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankVOPage.setRecords(questionBankVOList);
        return questionBankVOPage;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionBankToInner(List<QuestionBank> questionBankList) {
        try {
            boolean result = this.saveBatch(questionBankList);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或者违反其它完整性约束 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在题库");
        } catch (DataAccessException e) {
            log.error("数据库连接、事务等异常, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            log.error("向题库添加题目发生未知错误, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        }
    }

    @Override
    public void batchAddQuestionToBank(List<Long> questionIdList, long bankId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(CollectionUtils.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        ThrowUtils.throwIf(bankId <= 0, ErrorCode.PARAMS_ERROR, "题库ID非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.OPERATION_ERROR);
        // 获取合法题目ID列表
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList);
        List<Long> validQuestionIdList = questionService.listObjs(questionLambdaQueryWrapper, obj -> (Long) obj);
        ThrowUtils.throwIf(CollectionUtils.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法题目ID列表为空");
        // 找到题库中已经存在的题目列表
        LambdaQueryWrapper<QuestionBank> existlambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBank.class)
                .eq(QuestionBank::getBankId, bankId)
                .in(QuestionBank::getQuestionId, validQuestionIdList);
        Set<Long> existQuestionIdSet = this.list(existlambdaQueryWrapper).stream().map(QuestionBank::getQuestionId).collect(Collectors.toSet());
        validQuestionIdList = validQuestionIdList.stream().filter(questionId -> !existQuestionIdSet.contains(questionId)).collect(Collectors.toList());

        ThrowUtils.throwIf(CollectionUtils.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "所有题目都已存在题库中");
        // 数据库存在题库ID
        Bank bank = bankService.getById(bankId);
        ThrowUtils.throwIf(bank == null, ErrorCode.PARAMS_ERROR, "题库不存在对应ID的题库");
        // 使用线程池异步处理
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(20, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // 批量添加题目
        final int batchSize = 1000;
        int validQuestionIdListSize = validQuestionIdList.size();
        for (int i = 0; i < validQuestionIdList.size(); i += batchSize) {
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, validQuestionIdListSize));
            List<QuestionBank> subQuestionBankList = subList.stream().map(questionId -> {
                QuestionBank questionBank = new QuestionBank();
                questionBank.setUserId(loginUser.getId());
                questionBank.setQuestionId(questionId);
                questionBank.setBankId(bankId);
                return questionBank;
            }).collect(Collectors.toList());
            QuestionBankService questionBankService = (QuestionBankServiceImpl) AopContext.currentProxy();

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                questionBankService.batchAddQuestionBankToInner(subQuestionBankList);
            });
            futureList.add(future);
        }
        // 等待所有异步任务处理完成
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        // 关闭线程池
        customExecutor.shutdown();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionFromBank(List<Long> questionIdList, long bankId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(CollectionUtils.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        ThrowUtils.throwIf(bankId <= 0, ErrorCode.PARAMS_ERROR, "题库ID非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.OPERATION_ERROR);
        // 批量删除
        for (Long questionId : questionIdList) {
            LambdaQueryWrapper<QuestionBank> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBank.class)
                    .eq(QuestionBank::getQuestionId, questionId)
                    .eq(QuestionBank::getBankId, bankId);
            boolean result = this.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "从题库删除题目失败");
        }
    }

}
