package com.tianji.learning.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.pojo.entity.LearningLesson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.pojo.vo.LearningLessonVO;
import com.tianji.learning.pojo.vo.LearningPlanPageVO;
import com.tianji.learning.pojo.vo.LessonSimpleVO;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 学生课程表 服务类
 * </p>
 *
 * @author 天哥
 */
public interface ILearningLessonService extends IService<LearningLesson> {
    void addUserLessons(Long userId, List<Long> courseIds);

    PageDTO<LearningLessonVO> queryMyLessons(PageQuery query);

    LearningLessonVO queryMyCurrentLesson();

    void deleteLesson(Long userId, Long courseId);

    Long isLessonValid(Long courseId);

    LessonSimpleVO isLessonPossessed(Long courseId);

    Integer countLessonByCourse(Long courseId);

    LearningLesson queryByUserAndCourseId(Long userId, Long courseId);

    void createLearningPlan(@NotNull @Min(1) Long courseId, @NotNull @Range(min = 1, max = 50) Integer freq);

    LearningPlanPageVO queryMyPlans(PageQuery query);

    void handleExpiredLesson();
}