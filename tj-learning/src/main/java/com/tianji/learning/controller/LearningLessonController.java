package com.tianji.learning.controller;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.pojo.vo.LearningLessonVO;
import com.tianji.learning.pojo.vo.LessonSimpleVO;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.*;
import com.tianji.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 学生课程表 控制器
 * </p>
 *
 * @author 天哥
 */
@Api(tags = "我的课程表相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LearningLessonController {

    private final ILearningLessonService learningLessonService;


    @ApiOperation("查询我的课表，排序字段 latest_learn_time:学习时间排序，create_time:购买时间排序")
    @GetMapping("/page")
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        return learningLessonService.queryMyLessons(query);
    }

    @GetMapping("/now")
    @ApiOperation("查询我正在学习的课程")
    public LearningLessonVO queryMyCurrentLesson() {
        return learningLessonService.queryMyCurrentLesson();
    }

    @DeleteMapping("/{courseId}")
    @ApiOperation("用户主动删除（已过期的）课程")
    public void deleteByCourseId(@PathVariable Long courseId){
        Long userId = UserContext.getUser();
        learningLessonService.deleteLesson(userId, courseId);
    }



    @ApiOperation("校验当前用户是否可以学习当前课程(提供给tj-media的接口）")
    @GetMapping("/{courseId}/valid")
    public Long isLessonValid(@PathVariable("courseId") Long courseId){
        return learningLessonService.isLessonValid(courseId);
    }

    /**
     * 根据课程id，查询当前用户的课表中是否有该课程，如果有该课程则需要返回课程的学习进度、课程有效期等信息。
     */
    @ApiOperation("根据courseId查询用户课表有无课程，如果有返回信息")
    @GetMapping("/{courseId}")
    public LessonSimpleVO isLessonPossessed(@PathVariable("courseId") Long courseId){
        return learningLessonService.isLessonPossessed(courseId);
    }


    @ApiOperation("统计课程总学习人数(内部接口）")
    @GetMapping("/{courseId}/count")
    public Integer countLearningLessonByCourse(@PathVariable("courseId") Long courseId){
        return learningLessonService.countLessonByCourse(courseId);
    };


}
