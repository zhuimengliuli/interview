package com.interview.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interview.model.entity.Post;
import com.interview.model.entity.Question;

import java.util.Date;
import java.util.List;

/**
* @author 未央
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-11-04 22:19:41
* @Entity generator.domain.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Question> listQuestionWithDelete(Date minUpdateTime);

}




