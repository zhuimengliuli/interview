package com.interview.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.interview.model.entity.Bank;
import com.interview.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题库视图
 *
 *
 */
@Data
public class BankVO implements Serializable {

    /**
     * id
     */
    private Long id;

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

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 题目列表（分页）
     */
    private Page<QuestionVO> page;

    /**
     * 封装类转对象
     *
     * @param bankVO
     * @return
     */
    public static Bank voToObj(BankVO bankVO) {
        if (bankVO == null) {
            return null;
        }
        Bank bank = new Bank();
        BeanUtils.copyProperties(bankVO, bank);
        return bank;
    }

    /**
     * 对象转封装类
     *
     * @param bank
     * @return
     */
    public static BankVO objToVo(Bank bank) {
        if (bank == null) {
            return null;
        }
        BankVO bankVO = new BankVO();
        BeanUtils.copyProperties(bank, bankVO);
        return bankVO;
    }
}
