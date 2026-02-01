package com.tianji.learning.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Period;

@Data
@AllArgsConstructor
@ApiModel("购买后课程详情页信息")
public class LessonSimpleVO {
    @ApiModelProperty("课程进度")
    private Integer learnedSections;

    @ApiModelProperty("课程有效期")
    private Period validationPeriod = Period.ofYears(10000);
}
