package com.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interview.common.ErrorCode;
import com.interview.constant.CommonConstant;
import com.interview.exception.ThrowUtils;
import com.interview.mapper.BankMapper;
import com.interview.model.dto.bank.BankQueryRequest;
import com.interview.model.entity.Bank;
import com.interview.model.entity.User;
import com.interview.model.vo.BankVO;
import com.interview.model.vo.UserVO;
import com.interview.service.BankService;
import com.interview.service.UserService;
import com.interview.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库服务实现
 *
 *
 */
@Service
@Slf4j
public class BankServiceImpl extends ServiceImpl<BankMapper, Bank> implements BankService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param bank
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBank(Bank bank, boolean add) {
        ThrowUtils.throwIf(bank == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = bank.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param bankQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Bank> getQueryWrapper(BankQueryRequest bankQueryRequest) {
        QueryWrapper<Bank> queryWrapper = new QueryWrapper<>();
        if (bankQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = bankQueryRequest.getId();
        Long notId = bankQueryRequest.getNotId();
        String title = bankQueryRequest.getTitle();
        String searchText = bankQueryRequest.getSearchText();
        String sortField = bankQueryRequest.getSortField();
        String sortOrder = bankQueryRequest.getSortOrder();
        Long userId = bankQueryRequest.getUserId();
        String description = bankQueryRequest.getDescription();
        String picture = bankQueryRequest.getPicture();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // JSON 数组查询
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(picture), "picture", picture);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库封装
     *
     * @param bank
     * @param request
     * @return
     */
    @Override
    public BankVO getBankVO(Bank bank, HttpServletRequest request) {
        // 对象转封装类
        BankVO bankVO = BankVO.objToVo(bank);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = bank.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        bankVO.setUser(userVO);
        // endregion

        return bankVO;
    }

    /**
     * 分页获取题库封装
     *
     * @param bankPage
     * @param request
     * @return
     */
    @Override
    public Page<BankVO> getBankVOPage(Page<Bank> bankPage, HttpServletRequest request) {
        List<Bank> bankList = bankPage.getRecords();
        Page<BankVO> bankVOPage = new Page<>(bankPage.getCurrent(), bankPage.getSize(), bankPage.getTotal());
        if (CollUtil.isEmpty(bankList)) {
            return bankVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BankVO> bankVOList = bankList.stream().map(bank -> {
            return BankVO.objToVo(bank);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = bankList.stream().map(Bank::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        bankVOList.forEach(bankVO -> {
            Long userId = bankVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            bankVO.setUser(userService.getUserVO(user));
        });
        // endregion

        bankVOPage.setRecords(bankVOList);
        return bankVOPage;
    }

}
