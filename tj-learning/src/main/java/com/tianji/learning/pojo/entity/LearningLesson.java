package com.tianji.learning.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.PlanStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生课程表
 * </p>
 *
 * @author 天哥
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true) //覆盖@Data的get方法，get时返回this对象，方便链式调用(其实不如用Builder)
@TableName("learning_lesson")
@ApiModel(value="LearningLesson对象", description="学生课程表")
public class LearningLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "学员id")
    private Long userId;

    @ApiModelProperty(value = "课程id")
    private Long courseId;

    @ApiModelProperty(value = "课程状态，0-未学习，1-学习中，2-已学完，3-已失效")
    private LessonStatus lessonStatus;

    @ApiModelProperty(value = "每周学习频率，例如每周学习6小节，则频率为6")
    private Integer weekFreq;

    @ApiModelProperty(value = "学习计划状态，0-没有计划，1-计划进行中")
    private PlanStatus planStatus;

    @ApiModelProperty(value = "已学习小节数量")
    private Integer learnedSections;

    @ApiModelProperty(value = "最近一次学习的小节id")
    private Long latestSectionId;

    @ApiModelProperty(value = "最近一次学习的时间")
    private LocalDateTime latestLearnTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
