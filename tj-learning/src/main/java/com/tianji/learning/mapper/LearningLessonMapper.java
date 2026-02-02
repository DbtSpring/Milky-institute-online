package com.tianji.learning.mapper;

import com.tianji.learning.pojo.entity.LearningLesson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 学生课程表 Mapper 接口
 * </p>
 *
 * @author 天哥
 */
public interface LearningLessonMapper extends BaseMapper<LearningLesson> {

    @Select("select sum(week_freq) from learning_lesson where user_id = #{userId} and lesson_status=1")
    Integer queryTotalPlan(Long userId);
}
